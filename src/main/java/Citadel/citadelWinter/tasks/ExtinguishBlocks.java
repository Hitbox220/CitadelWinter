package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class ExtinguishBlocks extends BukkitRunnable {
    private static final Server server = CitadelWinter.getInstance().getServer();
    public Set<Block> extinguishBlocks = new HashSet<>();

    @Override
    public void run() {
        for (Block block : extinguishBlocks){
            if (Math.random() >= 0.01) continue;
                // Currently unimplemented
        }
    }
}
