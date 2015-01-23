package fr.neatmonster.nocheatplus.compat.blocks.init.vanilla;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import fr.neatmonster.nocheatplus.compat.blocks.BlockPropertiesSetup;
import fr.neatmonster.nocheatplus.config.WorldConfigProvider;
import fr.neatmonster.nocheatplus.logging.StaticLog;

public class VanillaBlocksFactory {

    public Collection<String> setupVanillaBlocks(final WorldConfigProvider<?> worldConfigProvider) {
        // Standard setups (abort with first failure, low to high MC version).
        final List<BlockPropertiesSetup> setups = new LinkedList<>();
        final List<String> success = new LinkedList<>();
        try{
            setups.add(new BlocksMC1_5());
            setups.add(new BlocksMC1_6_1());
            setups.add(new BlocksMC1_7_2());
            setups.add(new BlocksMC1_8());
        }
        catch(Throwable t){}
        for (final BlockPropertiesSetup setup : setups){
            try{
                // Assume the blocks setup to message success.
                setup.setupBlockProperties(worldConfigProvider);
                success.add(setup.getClass().getSimpleName());
                // TODO: Do logging from here ?
            }
            catch(Throwable t){
                StaticLog.logSevere("[NoCheatPlus] " + setup.getClass().getSimpleName() + ".setupBlockProperties could not execute properly: " + t.getClass().getSimpleName() + " - " + t.getMessage());
                StaticLog.logSevere(t);
                // Abort further processing.
                break;
            }
        }
        return success;
    }

}
