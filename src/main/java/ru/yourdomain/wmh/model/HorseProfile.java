package ru.yourdomain.wmh.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class HorseProfile {
    private final UUID ownerUUID;
    private final UUID horseUUID;
    private double distanceTravelled;
    private long jumpCount;
    private long waterSteps;
    private Set<HorseSkill> skillsUnlocked;
    private String lastWorld;
    private double lastX;
    private double lastY;
    private double lastZ;

    public HorseProfile(UUID ownerUUID, UUID horseUUID) {
        this.ownerUUID = ownerUUID;
        this.horseUUID = horseUUID;
        this.skillsUnlocked = EnumSet.noneOf(HorseSkill.class);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public UUID getHorseUUID() {
        return horseUUID;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void addDistance(double distance) {
        this.distanceTravelled += Math.max(0, distance);
    }

    public long getJumpCount() {
        return jumpCount;
    }

    public void incrementJumpCount() {
        this.jumpCount++;
    }

    public long getWaterSteps() {
        return waterSteps;
    }

    public void incrementWaterSteps() {
        this.waterSteps++;
    }

    public Set<HorseSkill> getSkillsUnlocked() {
        return EnumSet.copyOf(skillsUnlocked);
    }

    public boolean hasSkill(HorseSkill skill) {
        return skillsUnlocked.contains(skill);
    }

    public boolean unlockSkill(HorseSkill skill) {
        return skillsUnlocked.add(skill);
    }

    public Location getLastKnownLocation() {
        if (lastWorld == null) {
            return null;
        }
        World world = Bukkit.getWorld(lastWorld);
        return world == null ? null : new Location(world, lastX, lastY, lastZ);
    }

    public void setLastKnownLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        this.lastWorld = location.getWorld().getName();
        this.lastX = location.getX();
        this.lastY = location.getY();
        this.lastZ = location.getZ();
    }


    public void setLastKnownRaw(String world, double x, double y, double z) {
        this.lastWorld = world;
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
    }
    public String getLastWorld() {
        return lastWorld;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public double getLastZ() {
        return lastZ;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public void setJumpCount(long jumpCount) {
        this.jumpCount = jumpCount;
    }

    public void setWaterSteps(long waterSteps) {
        this.waterSteps = waterSteps;
    }

    public void setSkillsUnlocked(Set<HorseSkill> skillsUnlocked) {
        this.skillsUnlocked = EnumSet.copyOf(skillsUnlocked);
    }
}
