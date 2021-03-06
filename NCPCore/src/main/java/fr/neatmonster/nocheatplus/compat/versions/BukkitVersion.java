package fr.neatmonster.nocheatplus.compat.versions;

import org.bukkit.Bukkit;
import org.bukkit.Server;

/**
 * Bukkit-specific static access API, adding initialization methods. Note that Bukkit.getVersion() should be used to get the version of Bukkit (...).
 * <hr/>
 * Taken from the TrustCore plugin.
 * @author mc_dev
 *
 */
public class BukkitVersion {

    private static boolean initialized = false;
    private static boolean uniqueIdOnline = false;
    private static boolean uniqueIdOffline = false;

    /**
     * Initialize ServerVersion, BukkitVersion, Bugs.
     * @return 
     */
    public static boolean init() {
        if (initialized) {
            return false;
        }
        // Initialize server version.
        final Server server = Bukkit.getServer();
        ServerVersion.setMinecraftVersion(ServerVersion.parseMinecraftVersion(server.getBukkitVersion(), server.getVersion()));

        // Test availability/reliability of certain features.
        boolean uuidOnline = false;
        boolean uuidOffline = false;

        // TODO: Check for some versions explicitly.
        String mcVersion = ServerVersion.getMinecraftVersion();
        int cmp = -1;
        try {
            cmp = ServerVersion.compareVersions(mcVersion, "1.7.5");
        } catch (IllegalArgumentException e) {
            mcVersion = ServerVersion.UNKNOWN_VERSION; // Fake but somewhat true :p.
        }
        if (ServerVersion.UNKNOWN_VERSION.equals(mcVersion)) {
            // TODO: Might test for features.
        } else if (cmp == 1) {
            uuidOnline = true;
            uuidOffline = true;
        } else if (ServerVersion.compareVersions(mcVersion, "1.7") == 1) {
            // 1.7.x
            uuidOnline = true; // TODO: Test, if REALLY.
            uuidOffline = false;
        }
        uniqueIdOnline = uuidOnline;
        uniqueIdOffline = uuidOffline;
        initialized = true;
        Bugs.init();
        return true;
    }

    /**
     * Test if Player.getUniqueId be used for online players.
     * @return
     */
    public static boolean uniqueIdOnline() {
        return uniqueIdOnline;
    }

    public static void setUniqueIdOnline(boolean uniqueIdOnline) {
        BukkitVersion.uniqueIdOnline = uniqueIdOnline;
    }

    /**
     * Test if OfflinePlayer.getUniqueId can be used for offline payers [provided it exists :p].<br>
     * Not sure this makes sense :p.
     * <hr>
     * IMPORTANT NOTE: DO NOT MIX UP WITH OFFLINE MODE!<br>
     * @return
     */
    public static boolean uniqueIdOffline() {
        return uniqueIdOffline;
    }

    public static void setUniqueIdOffline(boolean uniqueIdOffline) {
        BukkitVersion.uniqueIdOffline = uniqueIdOffline;
    }

    /**
     * Delegating to ServerVersion.getMinecraftVersion.
     * @return
     */
    public static String getMinecraftVersion() {
        return ServerVersion.getMinecraftVersion();
    }
}
