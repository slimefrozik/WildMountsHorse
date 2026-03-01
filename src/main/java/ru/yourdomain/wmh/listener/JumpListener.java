package ru.yourdomain.wmh.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.HorseJumpEvent;
import ru.yourdomain.wmh.manager.HorseManager;
import ru.yourdomain.wmh.manager.ProgressionManager;

public class JumpListener implements Listener {
    private final HorseManager horseManager;
    private final ProgressionManager progressionManager;

    public JumpListener(HorseManager horseManager, ProgressionManager progressionManager) {
        this.horseManager = horseManager;
        this.progressionManager = progressionManager;
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent event) {
        if (!(event.getEntity().getPassengers().stream().findFirst().orElse(null) instanceof Player player)) {
            return;
        }

        var profile = horseManager.getOrCreate(event.getEntity(), player.getUniqueId());
        profile.incrementJumpCount();
        progressionManager.applyProgression(event.getEntity(), profile);
    }
}
