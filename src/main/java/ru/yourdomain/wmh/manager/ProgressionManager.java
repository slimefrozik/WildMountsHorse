package ru.yourdomain.wmh.manager;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.EquipmentSlotGroup;
import ru.yourdomain.wmh.WMHPlugin;
import ru.yourdomain.wmh.model.HorseProfile;

public class ProgressionManager {
    private final WMHPlugin plugin;
    private final NamespacedKey speedModifierKey;
    private final NamespacedKey jumpModifierKey;

    public ProgressionManager(WMHPlugin plugin) {
        this.plugin = plugin;
        this.speedModifierKey = new NamespacedKey(plugin, "speed_bonus");
        this.jumpModifierKey = new NamespacedKey(plugin, "jump_bonus");
    }

    public void applyProgression(Horse horse, HorseProfile profile) {
        applyModifier(horse.getAttribute(Attribute.MOVEMENT_SPEED), speedModifierKey, speedBonus(profile));
        applyModifier(horse.getAttribute(Attribute.JUMP_STRENGTH), jumpModifierKey, jumpBonus(profile));
    }

    private void applyModifier(AttributeInstance attribute, NamespacedKey key, double bonus) {
        if (attribute == null) {
            return;
        }

        attribute.getModifiers().stream()
            .filter(modifier -> modifier.getKey().equals(key))
            .forEach(attribute::removeModifier);
        if (bonus <= 0) {
            return;
        }

        AttributeModifier modifier = new AttributeModifier(key, bonus, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.ANY);
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
