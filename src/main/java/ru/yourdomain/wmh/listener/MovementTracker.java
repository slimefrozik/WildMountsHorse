package ru.yourdomain.wmh.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.yourdomain.wmh.WMHPlugin;
import ru.yourdomain.wmh.manager.HorseManager;
import ru.yourdomain.wmh.manager.ProgressionManager;
import ru.yourdomain.wmh.manager.SkillManager;
import ru.yourdomain.wmh.model.HorseProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementTracker extends BukkitRunnable {
    private final WMHPlugin plugin;
    private final HorseManager horseManager;
    private final ProgressionManager progressionManager;
    private final SkillManager skillManager;
    private final Map<UUID, Location> lastLocations = new HashMap<>();

    public MovementTracker(WMHPlugin plugin, HorseManager horseManager, ProgressionManager progressionManager, SkillManager skillManager) {
        this.plugin = plugin;
        this.horseManager = horseManager;
        this.progressionManager = progressionManager;
        this.skillManager = skillManager;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!(player.getVehicle() instanceof Horse horse) || !horse.isAdult()) {
                continue;
            }
            ItemStack saddle = horse.getInventory().getSaddle();
            if (saddle == null || saddle.getType() != Material.SADDLE) {
                continue;
            }

            HorseProfile profile = horseManager.getOrCreate(horse, player.getUniqueId());
            Location current = horse.getLocation();
            Location previous = lastLocations.put(horse.getUniqueId(), current.clone());
            if (previous != null && previous.getWorld() == current.getWorld()) {
                double dx = current.getX() - previous.getX();
                double dz = current.getZ() - previous.getZ();
                profile.addDistance(Math.sqrt(dx * dx + dz * dz));
            }

            if (isShallowWater(horse.getLocation())) {
                profile.incrementWaterSteps();
            }

            profile.setLastKnownLocation(current);
            progressionManager.applyProgression(horse, profile);
            skillManager.updateSkills(horse, profile);
        }
    }

    private boolean isShallowWater(Location location) {
        if (!location.getBlock().isLiquid()) {
            return false;
        }
        Location below = location.clone().subtract(0, 1, 0);
        return below.getBlock().isLiquid() || below.getBlock().getType() == Material.WATER;
    }
}
