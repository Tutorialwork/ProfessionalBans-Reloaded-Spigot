package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfessionalBans implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player p = (Player) sender;
        p.sendMessage("");
        p.sendMessage("§8[]===================================[]");
        p.sendMessage("§e§lProfessionalBans §7§oReloaded §8(§6Spigot§8) §8• §7Version §8» §c"+ Main.Version);
        p.sendMessage("§7Developer §8» §e§lTutorialwork");
        p.sendMessage("§5YT §7"+Main.messages.getString("channel")+" §8» §cyoutube.com/Tutorialwork");
        p.sendMessage("§8[]===================================[]");
        p.sendMessage("");
        return false;
    }
}
