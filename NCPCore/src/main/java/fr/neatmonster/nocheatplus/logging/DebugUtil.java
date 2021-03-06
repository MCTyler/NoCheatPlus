package fr.neatmonster.nocheatplus.logging;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.compat.BridgeEnchant;
import fr.neatmonster.nocheatplus.compat.MCAccess;
import fr.neatmonster.nocheatplus.utilities.BlockProperties;
import fr.neatmonster.nocheatplus.utilities.PlayerLocation;
import fr.neatmonster.nocheatplus.utilities.StringUtil;
import fr.neatmonster.nocheatplus.utilities.build.BuildParameters;

/**
 * Some auxiliary static-access methods.
 * @author mc_dev
 *
 */
public class DebugUtil {

    // TODO: Add useLoc1 and useLoc2.

    /**
     * Just the coordinates.
     * @param loc
     * @return
     */
    public static String formatLocation(final Location loc) {
        StringBuilder b = new StringBuilder(128);
        addLocation(loc, b);
        return b.toString();
    }

    /**
     * Just the coordinates.
     * @param from
     * @param to
     * @return
     */
    public static String formatMove(Location from, Location to) {
        StringBuilder builder = new StringBuilder(128);
        DebugUtil.addMove(from, to, null, builder);
        return builder.toString();
    }

    public static boolean isSamePos(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2){
        return x1 == x2 && y1 == y2 && z1 == z2;
    }

    public static boolean isSamePos(final Location loc1, final Location loc2){
        return isSamePos(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ());
    }

    public static void addLocation(final double x, final double y, final double z, final StringBuilder builder){
        builder.append(x).append(", ").append(y).append(", ").append(z);
    }

    public static void addLocation(final Location loc, final StringBuilder builder){
        addLocation(loc.getX(), loc.getY(), loc.getZ(), builder);
    }

    public static void addLocation(final PlayerLocation loc, final StringBuilder builder){
        addLocation(loc.getX(), loc.getY(), loc.getZ(), builder);
    }

    public static void addFormattedLocation(final double x, final double y, final double z, final StringBuilder builder){
        builder.append(StringUtil.fdec3.format(x)).append(", ").append(StringUtil.fdec3.format(y)).append(", ").append(StringUtil.fdec3.format(z));
    }

    public static void addFormattedLocation(final Location loc, final StringBuilder builder){
        addFormattedLocation(loc.getX(), loc.getY(), loc.getZ(), builder);
    }

    public static void addFormattedLocation(final PlayerLocation loc, final StringBuilder builder){
        addFormattedLocation(loc.getX(), loc.getY(), loc.getZ(), builder);
    }


    /**
     * With line break between from and to.
     * @param fromX
     * @param fromY
     * @param fromZ
     * @param toX
     * @param toY
     * @param toZ
     * @param builder
     */
    public static void addMove(final double fromX, final double fromY, final double fromZ, final double toX, final double toY, final double toZ, final StringBuilder builder){
        builder.append("from: ");
        addLocation(fromX, fromY, fromZ, builder);
        builder.append("\nto: ");
        addLocation(toX, toY, toZ, builder);
    }

    /**
     * No line breaks, max. 3 digits after comma.
     * @param fromX
     * @param fromY
     * @param fromZ
     * @param toX
     * @param toY
     * @param toZ
     * @param builder
     */
    public static void addFormattedMove(final double fromX, final double fromY, final double fromZ, final double toX, final double toY, final double toZ, final StringBuilder builder){
        addFormattedLocation(fromX, fromY, fromZ, builder);
        builder.append(" -> ");
        addFormattedLocation(toX, toY, toZ, builder);
    }

    /**
     * 3 decimal digits after comma (StringUtil.fdec3). No leading new line.
     * @param from
     * @param to
     * @param loc Reference location for from, usually Player.getLocation(). May be null.
     * @param builder
     */
    public static void addFormattedMove(final PlayerLocation from, final PlayerLocation to, final Location loc, final StringBuilder builder){
        if (loc != null && !from.isSamePos(loc)){
            builder.append("(");
            addFormattedLocation(loc, builder);
            builder.append(") ");
        }
        addFormattedMove(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), builder);	
    }

    /**
     * Add exact coordinates, multiple lines. No leading new line.
     * @param from
     * @param to
     * @param loc Reference location for from, usually Player.getLocation().
     * @param builder
     */
    public static void addMove(final PlayerLocation from, final PlayerLocation to, final Location loc, final StringBuilder builder){
        if (loc != null && !from.isSamePos(loc)){
            builder.append("Location: ");
            addLocation(loc, builder);
            builder.append("\n");
        }
        addMove(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), builder);	
    }

    /**
     * 3 decimal digits after comma (StringUtil.fdec3). No leading new line.
     * @param from
     * @param to
     * @param loc Reference location for from, usually Player.getLocation().
     * @param builder
     */
    public static void addFormattedMove(final Location from, final Location to, final Location loc, final StringBuilder builder){
        if (loc != null && !isSamePos(from, loc)){
            builder.append("(");
            addFormattedLocation(loc, builder);
            builder.append(") ");
        }
        addFormattedMove(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), builder);		}

    /**
     * Add exact coordinates, multiple lines. No leading new line.
     * @param from
     * @param to
     * @param loc Reference location for from, usually Player.getLocation().
     * @param builder
     */
    public static void addMove(final Location from, final Location to, final Location loc, final StringBuilder builder){
        if (loc != null && !isSamePos(from, loc)){
            builder.append("Location: ");
            addLocation(loc, builder);
            builder.append("\n");
        }
        addMove(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), builder);
    }

    /**
     * Output information specific to player-move events.
     * @param player
     * @param from
     * @param to
     * @param maxYOnGround
     * @param mcAccess
     */
    public static void outputMoveDebug(final Player player, final PlayerLocation from, final PlayerLocation to, final double maxYOnGround, final MCAccess mcAccess) {
        final StringBuilder builder = new StringBuilder(250);
        final Location loc = player.getLocation();
        // TODO: Differentiate debug levels (needs setting up some policy + document in BuildParamteres)?
        if (BuildParameters.debugLevel > 0) {
            builder.append("\n-------------- MOVE --------------\n");
            builder.append(player.getName()).append(" ").append(from.getWorld().getName()).append(":\n");
            addMove(from, to, loc, builder);
        }
        else {
            builder.append(player.getName()).append(" ").append(from.getWorld().getName()).append(" ");
            addFormattedMove(from, to, loc, builder);
        }
        final double jump = mcAccess.getJumpAmplifier(player);
        final double speed = mcAccess.getFasterMovementAmplifier(player);
        final double strider = BridgeEnchant.getDepthStriderLevel(player);
        if (BuildParameters.debugLevel > 0){
            try{
                // TODO: Check backwards compatibility (1.4.2). Remove try-catch
                builder.append("\n(walkspeed=").append(player.getWalkSpeed()).append(" flyspeed=").append(player.getFlySpeed()).append(")");
            } catch (Throwable t){}
            final Vector v = player.getVelocity();
            builder.append("(svel=").append(v.getX()).append(",").append(v.getY()).append(",").append(v.getZ()).append(")");
            if (player.isSprinting()){
                builder.append("(sprinting)");
            }
            if (player.isSneaking()){
                builder.append("(sneaking)");
            }
        }
        if (speed != Double.NEGATIVE_INFINITY){
            builder.append("(e_speed=").append(speed + 1).append(")");
        }
        if (jump != Double.NEGATIVE_INFINITY){
            builder.append("(e_jump=").append(jump + 1).append(")");
        }
        if (strider != 0){
            builder.append("(e_depth_strider=").append(strider).append(")");
        }
        // Print basic info first in order
        NCPAPIProvider.getNoCheatPlusAPI().getLogManager().debug(Streams.TRACE_FILE, builder.toString());
        // Extended info.
        if (BuildParameters.debugLevel > 0){
            builder.setLength(0);
            // Note: the block flags are for normal on-ground checking, not with yOnGrond set to 0.5.
            from.collectBlockFlags(maxYOnGround);
            if (from.getBlockFlags() != 0) builder.append("\nfrom flags: ").append(StringUtil.join(BlockProperties.getFlagNames(from.getBlockFlags()), "+"));
            if (from.getTypeId() != 0) addBlockInfo(builder, from, "\nfrom");
            if (from.getTypeIdBelow() != 0) addBlockBelowInfo(builder, from, "\nfrom");
            if (!from.isOnGround() && from.isOnGround(0.5)) builder.append(" (ground within 0.5)");
            to.collectBlockFlags(maxYOnGround);
            if (to.getBlockFlags() != 0) builder.append("\nto flags: ").append(StringUtil.join(BlockProperties.getFlagNames(to.getBlockFlags()), "+"));
            if (to.getTypeId() != 0) addBlockInfo(builder, to, "\nto");
            if (to.getTypeIdBelow() != 0) addBlockBelowInfo(builder, to, "\nto");
            if (!to.isOnGround() && to.isOnGround(0.5)) builder.append(" (ground within 0.5)");
            NCPAPIProvider.getNoCheatPlusAPI().getLogManager().debug(Streams.TRACE_FILE, builder.toString());
        }

    }

    public static  void addBlockBelowInfo(final StringBuilder builder, final PlayerLocation loc, final String tag) {
        builder.append(tag).append(" below id=").append(loc.getTypeIdBelow()).append(" data=").append(loc.getData(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ())).append(" shape=").append(Arrays.toString(loc.getBlockCache().getBounds(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ())));
    }

    public static  void addBlockInfo(final StringBuilder builder, final PlayerLocation loc, final String tag) {
        builder.append(tag).append(" id=").append(loc.getTypeId()).append(" data=").append(loc.getData()).append(" shape=").append(Arrays.toString(loc.getBlockCache().getBounds(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
    }

    /**
     * Intended for vehicle-move events.
     * 
     * @param player
     * @param vehicle
     * @param from
     * @param to
     * @param fake true if the event was not fired by an external source (just gets noted).
     */
    public static void outputDebugVehicleMove(final Player player, final Entity vehicle, final Location from, final Location to, final boolean fake) {
        final StringBuilder builder = new StringBuilder(250);
        final Location vLoc = vehicle.getLocation();
        final Location loc = player.getLocation();
        // TODO: Differentiate debug levels (needs setting up some policy + document in BuildParamteres)?
        final Entity actualVehicle = player.getVehicle();
        final boolean wrongVehicle = actualVehicle == null || actualVehicle.getEntityId() != vehicle.getEntityId();
        if (BuildParameters.debugLevel > 0) {
            builder.append("\n-------------- VEHICLE MOVE ").append(fake ? "(fake)" : "").append("--------------\n");
            builder.append(player.getName()).append(" ").append(from.getWorld().getName()).append(":\n");
            addMove(from, to, null, builder);
            builder.append("\n Vehicle: ");
            addLocation(vLoc, builder);
            builder.append("\n Player: ");
            addLocation(loc, builder);
        }
        else {
            builder.append(player.getName()).append(" ").append(from.getWorld().getName()).append("veh.").append(fake ? "(fake)" : "").append(" ");
            addFormattedMove(from, to, null, builder);
            builder.append("\n Vehicle: ");
            addFormattedLocation(vLoc, builder);
            builder.append(" Player: ");
            addFormattedLocation(loc, builder);
        }
        builder.append("\n Vehicle type: ").append(vehicle.getType()).append(wrongVehicle ? (actualVehicle == null ? " (exited?)" : " actual: " + actualVehicle.getType()) : "");
        NCPAPIProvider.getNoCheatPlusAPI().getLogManager().debug(Streams.TRACE_FILE, builder.toString());
    }

}
