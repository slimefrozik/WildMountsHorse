package ru.yourdomain.wmh.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.yourdomain.wmh.manager.HorseManager;

public class WhereMyHorseCommand implements CommandExecutor {
    private final HorseManager horseManager;

    public WhereMyHorseCommand(HorseManager horseManager) {
        this.horseManager = horseManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игрок может использовать команду.");
            return true;
        }

        var profileOpt = horseManager.getPlayerHorse(player.getUniqueId());
        if (profileOpt.isEmpty()) {
            player.sendMessage("§cУ вас нет привязанной лошади.");
            return true;
        }

        Location location = horseManager.resolveHorse(profileOpt.get())
            .map(h -> h.getLocation())
            .orElse(profileOpt.get().getLastKnownLocation());

        if (location == null || location.getWorld() == null) {
            player.sendMessage("§cКоординаты лошади неизвестны.");
            return true;
        }

        player.sendMessage("§6Ваша лошадь находится:");
        player.sendMessage("§eМир: §f" + location.getWorld().getName());
        player.sendMessage("§eX: §f" + location.getBlockX());
        player.sendMessage("§eY: §f" + location.getBlockY());
        player.sendMessage("§eZ: §f" + location.getBlockZ());
        return true;
    }
}
