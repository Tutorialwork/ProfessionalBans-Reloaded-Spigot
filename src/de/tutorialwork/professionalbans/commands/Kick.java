package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.LogManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class Kick implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("professionalbans.kick") || p.hasPermission("professionalbans.*")){
                if(args.length == 0 || args.length == 1){
                    p.sendMessage(Main.data.Prefix+"/kick <"+Main.messages.getString("player")+"> <"+Main.messages.getString("reason")+">");
                } else {
                    Player tokick = Bukkit.getPlayer(args[0]);
                    if(tokick != null){
                        File config = new File(Main.main.getDataFolder(), "config.yml");
                        YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);
                        String grund = "";
                        for(int i = 1; i < args.length; i++){
                            grund = grund + " " + args[i];
                        }
                        Main.ban.sendNotify("KICK", tokick.getName(), p.getName(), grund);
                        LogManager.createEntry(tokick.getUniqueId().toString(), p.getUniqueId().toString(), "KICK", grund);
                        tokick.kickPlayer(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.KICK").replace("%grund%", grund)));
                    } else {
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                    }
                }
            } else {
                p.sendMessage(Main.data.NoPerms);
            }
        } else {
            if(args.length == 0 || args.length == 1){
                Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"/kick <"+Main.messages.getString("player")+"> <"+Main.messages.getString("reason")+">");
            } else {
                Player tokick = Bukkit.getPlayer(args[0]);
                if(tokick != null){
                    File config = new File(Main.main.getDataFolder(), "config.yml");
                    YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);
                    String grund = "";
                    for(int i = 1; i < args.length; i++){
                        grund = grund + " " + args[i];
                    }
                    Main.ban.sendNotify("KICK", tokick.getName(), "KONSOLE", grund);
                    LogManager.createEntry(tokick.getUniqueId().toString(), "KONSOLE", "KICK", grund);
                    tokick.kickPlayer(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.KICK").replace("%grund%", grund)));
                } else {
                    Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                }
            }
        }
        return false;
    }
}
