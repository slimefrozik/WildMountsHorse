package ru.yourdomain.wmh.listener;

import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.yourdomain.wmh.WMHPlugin;
import ru.yourdomain.wmh.manager.HorseManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SummonListener implements Listener {
    private final WMHPlugin plugin;
    private final HorseManager horseManager;
    private final Map<UUID, Long> sneakMap = new HashMap<>();

    public SummonListener(WMHPlugin plugin, HorseManager horseManager) {
        this.plugin = plugin;
        this.horseManager = horseManager;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            sneakMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Long time = sneakMap.get(player.getUniqueId());
        if (time == null || System.currentTimeMillis() - time > 300) {
            return;
        }

        event.setCancelled(true);
        Optional<Horse> horse = horseManager.getPlayerHorse(player.getUniqueId()).flatMap(horseManager::resolveHorse);
        if (horse.isEmpty()) {
            player.sendMessage("§cЛошадь не найдена или не загружена.");
            return;
        }

        Horse target = horse.get();
        if (target.isDead() || target.getWorld() != player.getWorld()) {
            player.sendMessage("§cНевозможно призвать лошадь в текущих условиях.");
            return;
        }

        double radius = getSummonRadius(target);
        if (target.getLocation().distance(player.getLocation()) > radius) {
            player.sendMessage("§eЛошадь слишком далеко. Текущий радиус призыва: " + (int) radius + " блоков.");
            return;
        }

        target.teleport(player.getLocation());
        target.setInvulnerable(true);
        int ticks = plugin.getConfig().getInt("summon.invulnerable-seconds", 3) * 20;
        new BukkitRunnable() {
            @Override
            public void run() {
                target.setInvulnerable(false);
            }
        }.runTaskLater(plugin, ticks);
        player.sendMessage("§aЛошадь призвана!");
    }

    private double getSummonRadius(Horse horse) {
        ItemStack armor = horse.getInventory().getArmor();
        if (armor == null || armor.getType() == Material.AIR) {
            return 30;
        }

        return switch (armor.getType()) {
            case IRON_HORSE_ARMOR -> 80;
            case GOLDEN_HORSE_ARMOR -> 120;
            case DIAMOND_HORSE_ARMOR -> 200;
            case NETHERITE_HORSE_ARMOR -> 300;
            default -> 30;
        };
    }
}
