package ru.yourdomain.wmh.listener;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import ru.yourdomain.wmh.manager.HorseManager;
import ru.yourdomain.wmh.manager.ProgressionManager;

public class RideListener implements Listener {
    private final HorseManager horseManager;
    private final ProgressionManager progressionManager;

    public RideListener(HorseManager horseManager, ProgressionManager progressionManager) {
        this.horseManager = horseManager;
        this.progressionManager = progressionManager;
    }

    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getMount() instanceof Horse horse)) {
            return;
        }

        var profile = horseManager.getOrCreate(horse, player.getUniqueId());
        progressionManager.applyProgression(horse, profile);
    }
}
