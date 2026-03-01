package ru.yourdomain.wmh.listener;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.yourdomain.wmh.manager.HorseManager;
import ru.yourdomain.wmh.manager.SkillManager;
import ru.yourdomain.wmh.model.HorseSkill;

import java.util.concurrent.ThreadLocalRandom;

public class DamageListener implements Listener {
    private final HorseManager horseManager;
    private final SkillManager skillManager;

    public DamageListener(HorseManager horseManager, SkillManager skillManager) {
        this.horseManager = horseManager;
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onHorseDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Horse horse)
            || !(horse.getPassengers().stream().findFirst().orElse(null) instanceof Player rider)) {
            return;
        }

        var profile = horseManager.getOrCreate(horse, rider.getUniqueId());
        if (skillManager.hasCombatAvoidChance(profile) && ThreadLocalRandom.current().nextDouble() < 0.10) {
            event.setCancelled(true);
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && profile.hasSkill(HorseSkill.MOUNTAIN)) {
            event.setDamage(event.getDamage() * 0.65);
        }
    }
}
