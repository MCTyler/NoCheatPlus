package fr.neatmonster.nocheatplus.checks.blockbreak;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import fr.neatmonster.nocheatplus.actions.ParameterName;
import fr.neatmonster.nocheatplus.checks.Check;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.ViolationData;
import fr.neatmonster.nocheatplus.compat.AlmostBoolean;
import fr.neatmonster.nocheatplus.permissions.Permissions;
import fr.neatmonster.nocheatplus.utilities.BlockProperties;
import fr.neatmonster.nocheatplus.utilities.PotionUtil;
import fr.neatmonster.nocheatplus.utilities.TickTask;

/**
 * A check used to verify if the player isn't breaking blocks faster than possible.
 */
public class FastBreak extends Check {

    /**
     * Instantiates a new fast break check.
     */
    public FastBreak() {
        super(CheckType.BLOCKBREAK_FASTBREAK);
    }

    /**
     * Checks a player.
     * 
     * @param player
     *            the player
     * @param block
     *            the block
     * @param isInstaBreak 
     * @param data 
     * @param cc 
     * @param elaspedTime
     * @return true, if successful
     */
    public boolean check(final Player player, final Block block, final AlmostBoolean isInstaBreak, final BlockBreakConfig cc, final BlockBreakData data) {
    	final long now = System.currentTimeMillis();
        boolean cancel = false;
        
        // First, check the game mode of the player and choose the right limit.
        final long breakingTime;
        final int id = block.getTypeId();
        if (player.getGameMode() == GameMode.CREATIVE)
        	// Modifier defaults to 0, the Frequency check is responsible for those.
            breakingTime = Math.max(0, Math.round((double) cc.fastBreakModCreative / 100D * (double) 100));
        else
        	breakingTime = Math.max(0, Math.round((double) cc.fastBreakModSurvival / 100D * (double) BlockProperties.getBreakingDuration(id, player)));
    	// fastBreakfirstDamage is the first interact on block (!).
        final long elapsedTime;
        // TODO: Should it be breakingTime instead of 0 for inconsistencies?
        if (cc.fastBreakStrict){
        	// Counting interact...break.
        	elapsedTime = (data.fastBreakBreakTime > data.fastBreakfirstDamage) ? 0 : now - data.fastBreakfirstDamage;
        }
        else{
        	// Counting break...break.
        	elapsedTime = (data.fastBreakBreakTime > now) ? 0 : now - data.fastBreakBreakTime;
        }
          
        // Check if the time used time is lower than expected.
        if (isInstaBreak.decideOptimistically()){
        	// Ignore those for now.
        	// TODO: Find out why this was commented out long ago a) did not fix mcMMO b) exploits.
        	// TODO: Maybe adjust time to min(time, SOMETHING) for MAYBE/YES.
        }
        else if (elapsedTime < 0){
        	// Ignore it. TODO: ?
        }
        else if (elapsedTime + cc.fastBreakDelay < breakingTime){
    		// lag or cheat or Minecraft.
        	
    		// Count in server side lag, if desired.
            final float lag = cc.lag ? TickTask.getLag(breakingTime, true) : 1f;
        	
    		final long missingTime = breakingTime - (long) (lag * elapsedTime);
    		
    		if (missingTime > 0){
        		// Add as penalty
        		data.fastBreakPenalties.add(now, (float) missingTime);
        		

        		// Only raise a violation, if the total penalty score exceeds the contention duration (for lag, delay).
        		if (data.fastBreakPenalties.score(cc.fastBreakBucketFactor) > cc.fastBreakGrace){
        			// TODO: maybe add one absolute penalty time for big amounts to stop breaking until then
        		    final double vlAdded = (double) missingTime / 1000.0;
        			data.fastBreakVL += vlAdded;
        			final ViolationData vd = new ViolationData(this, player, data.fastBreakVL, vlAdded, cc.fastBreakActions);
        			if (vd.needsParameters()) vd.setParameter(ParameterName.BLOCK_ID, "" + id);
        			cancel = executeActions(vd);
        		}
        		// else: still within contention limits.
    		}
    	}
    	else if (breakingTime > cc.fastBreakDelay){
    		// Fast breaking does not decrease violation level.
    		data.fastBreakVL *= 0.9D;
    	}
    	
        if ((cc.fastBreakDebug || cc.debug) && player.hasPermission(Permissions.ADMINISTRATION_DEBUG)){
        	// General stats:
        	if (data.stats != null){
                data.stats.addStats(data.stats.getId(Integer.toString(block.getTypeId())+"u", true), elapsedTime);
                data.stats.addStats(data.stats.getId(Integer.toString(block.getTypeId())+ "r", true), breakingTime);
                player.sendMessage(data.stats.getStatsStr(true));
            }
        	// Send info about current break:
        	final int blockId = block.getTypeId();
        	final ItemStack stack = player.getItemInHand();
        	final boolean isValidTool = BlockProperties.isValidTool(blockId, stack);
        	final double haste = PotionUtil.getPotionEffectAmplifier(player, PotionEffectType.FAST_DIGGING);
        	String msg = (isInstaBreak.decideOptimistically() ? ("[Insta=" + isInstaBreak + "]") : "[Normal]") + "[" + blockId + "] "+ elapsedTime + "u / " + breakingTime +"r (" + (isValidTool?"tool":"no-tool") + ")" + (haste == Double.NEGATIVE_INFINITY ? "" : " haste=" + ((int) haste + 1));
        	player.sendMessage(msg);
//        	net.minecraft.server.Item mcItem = net.minecraft.server.Item.byId[stack.getTypeId()];
//        	if (mcItem != null){
//        		double x = mcItem.getDestroySpeed(((CraftItemStack) stack).getHandle(), net.minecraft.server.Block.byId[blockId]);
//        		player.sendMessage("mc speed: " + x);
//        	}
        }
    	 
    	 // (The break time is set in the listener).

        return cancel;
    }
}
