package ru.yourdomain.wmh.manager;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import ru.yourdomain.wmh.WMHPlugin;
import ru.yourdomain.wmh.model.HorseProfile;
import ru.yourdomain.wmh.model.HorseSkill;

import java.util.UUID;

public class SkillManager {
    private static final UUID AMPHIBIAN_WATER_SPEED_ID = UUID.fromString("57f2de2c-1497-4a1b-8f76-e63f5f09f246");

    private final WMHPlugin plugin;

    public SkillManager(WMHPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateSkills(Horse horse, HorseProfile profile) {
        int req = plugin.getConfig().getInt("skills.amphibian.water-steps-required", 3_000);
        if (profile.getWaterSteps() >= req && profile.unlockSkill(HorseSkill.AMPHIBIAN)) {
            if (horse.getOwner() instanceof Player player) {
                player.sendMessage("§bВаша лошадь открыла навык: Амфибия!");
            }
        }

        if (profile.hasSkill(HorseSkill.AMPHIBIAN) && horse.isInWater()) {
            var attr = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (attr != null) {
                attr.getModifiers().stream().filter(m -> m.getUniqueId().equals(AMPHIBIAN_WATER_SPEED_ID)).forEach(attr::removeModifier);
                attr.addModifier(new AttributeModifier(AMPHIBIAN_WATER_SPEED_ID, "wmh_amphibian_speed", 0.15,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }
        }
    }

    public boolean hasCombatAvoidChance(HorseProfile profile) {
        return profile.hasSkill(HorseSkill.COMBAT);
    }
}
