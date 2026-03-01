package ru.yourdomain.wmh;

import org.bukkit.plugin.java.JavaPlugin;
import ru.yourdomain.wmh.command.WhereMyHorseCommand;
import ru.yourdomain.wmh.listener.DamageListener;
import ru.yourdomain.wmh.listener.JumpListener;
import ru.yourdomain.wmh.listener.MovementTracker;
import ru.yourdomain.wmh.listener.RideListener;
import ru.yourdomain.wmh.listener.SummonListener;
import ru.yourdomain.wmh.manager.HorseManager;
import ru.yourdomain.wmh.manager.ProgressionManager;
import ru.yourdomain.wmh.manager.SkillManager;
import ru.yourdomain.wmh.storage.HorseStorage;

import java.io.IOException;

public class WMHPlugin extends JavaPlugin {
    private HorseStorage horseStorage;
    private HorseManager horseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        horseStorage = new HorseStorage(getDataFolder());
        horseManager = new HorseManager(this, horseStorage.loadAll());

        ProgressionManager progressionManager = new ProgressionManager(this);
        SkillManager skillManager = new SkillManager(this);

        getServer().getPluginManager().registerEvents(new RideListener(horseManager, progressionManager), this);
        getServer().getPluginManager().registerEvents(new JumpListener(horseManager, progressionManager), this);
        getServer().getPluginManager().registerEvents(new SummonListener(this, horseManager), this);
        getServer().getPluginManager().registerEvents(new DamageListener(horseManager, skillManager), this);

        new MovementTracker(this, horseManager, progressionManager, skillManager).runTaskTimer(this, 20L, 20L);
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                horseStorage.saveAll(horseManager.getProfiles());
            } catch (IOException e) {
                getLogger().warning("Failed to async save horse profiles: " + e.getMessage());
            }
        }, 20L * 60, 20L * 60);

        if (getCommand("wmh") != null) {
            getCommand("wmh").setExecutor(new WhereMyHorseCommand(horseManager));
        }

        getLogger().info("WildMountsHorse enabled.");
    }

    @Override
    public void onDisable() {
        if (horseStorage == null || horseManager == null) {
            return;
        }

        try {
            horseStorage.saveAll(horseManager.getProfiles());
        } catch (IOException e) {
            getLogger().warning("Failed to save horse profiles: " + e.getMessage());
        }
    }
}
