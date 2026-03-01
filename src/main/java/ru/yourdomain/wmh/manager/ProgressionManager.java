package ru.yourdomain.wmh.manager;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Horse;
import ru.yourdomain.wmh.WMHPlugin;
import ru.yourdomain.wmh.model.HorseProfile;

import java.util.UUID;

public class ProgressionManager {
    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("6e4ce911-2f0d-4a7e-92f8-6c5e8de2cbf0");
    private static final UUID JUMP_MODIFIER_ID = UUID.fromString("34a875d0-d28b-4a42-b604-e3b443db95f9");

    private final WMHPlugin plugin;

    public ProgressionManager(WMHPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyProgression(Horse horse, HorseProfile profile) {
        applyModifier(horse.getAttribute(Attribute.MOVEMENT_SPEED), SPEED_MODIFIER_ID, "wmh_speed_bonus", speedBonus(profile));
        applyModifier(horse.getAttribute(Attribute.JUMP_STRENGTH), JUMP_MODIFIER_ID, "wmh_jump_bonus", jumpBonus(profile));
    }

    private void applyModifier(AttributeInstance attribute, UUID id, String name, double bonus) {
        if (attribute == null) {
            return;
        }
        attribute.getModifiers().stream()
            .filter(m -> m.getUniqueId().equals(id))
            .forEach(attribute::removeModifier);
        if (bonus <= 0) {
            return;
        }

        AttributeModifier modifier = new AttributeModifier(id, name, bonus, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
        attribute.addModifier(modifier);
    }

    public double speedBonus(HorseProfile profile) {
        double distance = profile.getDistanceTravelled();
        double bonus;
        if (distance >= 150_000) {
            bonus = 0.20;
        } else if (distance >= 50_000) {
            bonus = 0.10;
        } else if (distance >= 20_000) {
            bonus = 0.05;
        } else if (distance >= 5_000) {
            bonus = 0.02;
        } else {
            bonus = 0;
        }
        return Math.min(bonus, plugin.getConfig().getDouble("speed.max-bonus", 0.3));
    }

    public double jumpBonus(HorseProfile profile) {
        long jumps = profile.getJumpCount();
        double bonus;
        if (jumps >= 3_000) {
            bonus = 0.15;
        } else if (jumps >= 1_000) {
            bonus = 0.08;
        } else if (jumps >= 200) {
            bonus = 0.03;
        } else {
            bonus = 0;
        }
        return Math.min(bonus, plugin.getConfig().getDouble("jump.max-bonus", 0.25));
    }
}
