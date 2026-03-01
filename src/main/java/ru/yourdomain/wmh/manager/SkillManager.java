package ru.yourdomain.wmh.manager;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import ru.yourdomain.wmh.WMHPlugin;
import ru.yourdomain.wmh.model.HorseProfile;
import ru.yourdomain.wmh.model.HorseSkill;

public class SkillManager {
    private final WMHPlugin plugin;
    private final NamespacedKey amphibianWaterSpeedKey;

    public SkillManager(WMHPlugin plugin) {
        this.plugin = plugin;
        this.amphibianWaterSpeedKey = new NamespacedKey(plugin, "amphibian_water_speed");
    }

    public void updateSkills(Horse horse, HorseProfile profile) {
        int req = plugin.getConfig().getInt("skills.amphibian.water-steps-required", 3_000);
        if (profile.getWaterSteps() >= req && profile.unlockSkill(HorseSkill.AMPHIBIAN)) {
            if (horse.getOwner() instanceof Player player) {
                player.sendMessage("§bВаша лошадь открыла навык: Амфибия!");
            }
        }

        var attr = horse.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attr == null) {
            return;
        }

        attr.getModifiers().stream()
            .filter(modifier -> modifier.getKey().equals(amphibianWaterSpeedKey))
            .forEach(attr::removeModifier);

        if (profile.hasSkill(HorseSkill.AMPHIBIAN) && horse.isInWater()) {
            AttributeModifier modifier = new AttributeModifier(
                amphibianWaterSpeedKey,
                0.15,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                EquipmentSlotGroup.ANY
            );
            attr.addModifier(modifier);
        }
    }

    public boolean hasCombatAvoidChance(HorseProfile profile) {
        return profile.hasSkill(HorseSkill.COMBAT);
    }
}
