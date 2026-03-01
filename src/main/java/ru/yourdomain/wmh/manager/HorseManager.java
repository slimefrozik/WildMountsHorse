package ru.yourdomain.wmh.manager;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.yourdomain.wmh.WMHPlugin;
import ru.yourdomain.wmh.model.HorseProfile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HorseManager {
    private final WMHPlugin plugin;
    private final Map<UUID, HorseProfile> profiles;
    private final NamespacedKey ownerKey;

    public HorseManager(WMHPlugin plugin, Map<UUID, HorseProfile> loaded) {
        this.plugin = plugin;
        this.profiles = new ConcurrentHashMap<>(loaded);
        this.ownerKey = new NamespacedKey(plugin, "horse_owner");
    }

    public HorseProfile getOrCreate(Horse horse, UUID ownerUUID) {
        HorseProfile profile = profiles.computeIfAbsent(horse.getUniqueId(), id -> new HorseProfile(ownerUUID, id));
        bindOwner(horse, ownerUUID);
        profile.setLastKnownLocation(horse.getLocation());
        return profile;
    }

    public Optional<HorseProfile> getProfile(UUID horseId) {
        return Optional.ofNullable(profiles.get(horseId));
    }

    public Optional<HorseProfile> getPlayerHorse(UUID playerId) {
        return profiles.values().stream().filter(p -> p.getOwnerUUID().equals(playerId)).findFirst();
    }

    public boolean isOwnedBy(Entity entity, UUID playerId) {
        if (!(entity instanceof AbstractHorse horse)) {
            return false;
        }
        PersistentDataContainer pdc = horse.getPersistentDataContainer();
        String stored = pdc.get(ownerKey, PersistentDataType.STRING);
        return stored != null && stored.equals(playerId.toString());
    }

    public void bindOwner(Horse horse, UUID ownerUUID) {
        horse.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, ownerUUID.toString());
    }

    public Map<UUID, HorseProfile> getProfiles() {
        return profiles;
    }

    public Optional<Horse> resolveHorse(HorseProfile profile) {
        Entity entity = plugin.getServer().getEntity(profile.getHorseUUID());
        return entity instanceof Horse horse ? Optional.of(horse) : Optional.empty();
    }
}
