package ru.yourdomain.wmh.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.yourdomain.wmh.model.HorseProfile;
import ru.yourdomain.wmh.model.HorseSkill;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class HorseStorage {
    private final File file;

    public HorseStorage(File dataFolder) {
        this.file = new File(dataFolder, "horses.yml");
    }

    public Map<UUID, HorseProfile> loadAll() {
        Map<UUID, HorseProfile> profiles = new HashMap<>();
        if (!file.exists()) {
            return profiles;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = yaml.getConfigurationSection("horses");
        if (root == null) {
            return profiles;
        }

        for (String key : root.getKeys(false)) {
            try {
                UUID horseId = UUID.fromString(key);
                UUID owner = UUID.fromString(yaml.getString("horses." + key + ".owner"));
                HorseProfile profile = new HorseProfile(owner, horseId);
                profile.setDistanceTravelled(yaml.getDouble("horses." + key + ".distance", 0));
                profile.setJumpCount(yaml.getLong("horses." + key + ".jumps", 0));
                profile.setWaterSteps(yaml.getLong("horses." + key + ".water-steps", 0));
                String world = yaml.getString("horses." + key + ".last.world");
                if (world != null) {
                    profile.setLastKnownRaw(
                        world,
                        yaml.getDouble("horses." + key + ".last.x", 0),
                        yaml.getDouble("horses." + key + ".last.y", 0),
                        yaml.getDouble("horses." + key + ".last.z", 0)
                    );
                }
                List<String> rawSkills = yaml.getStringList("horses." + key + ".skills");
                Set<HorseSkill> skills = rawSkills.stream()
                    .map(String::toUpperCase)
                    .map(HorseSkill::valueOf)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(HorseSkill.class)));
                profile.setSkillsUnlocked(skills);
                profiles.put(horseId, profile);
            } catch (Exception ignored) {
            }
        }

        return profiles;
    }

    public void saveAll(Map<UUID, HorseProfile> profiles) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        for (HorseProfile profile : profiles.values()) {
            String base = "horses." + profile.getHorseUUID();
            yaml.set(base + ".owner", profile.getOwnerUUID().toString());
            yaml.set(base + ".distance", profile.getDistanceTravelled());
            yaml.set(base + ".jumps", profile.getJumpCount());
            yaml.set(base + ".water-steps", profile.getWaterSteps());
            yaml.set(base + ".skills", profile.getSkillsUnlocked().stream().map(Enum::name).toList());
            if (profile.getLastWorld() != null) {
                yaml.set(base + ".last.world", profile.getLastWorld());
                yaml.set(base + ".last.x", profile.getLastX());
                yaml.set(base + ".last.y", profile.getLastY());
                yaml.set(base + ".last.z", profile.getLastZ());
            }
        }

        yaml.save(file);
    }
}
