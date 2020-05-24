package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.MessagesManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class Broadcast implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("professionalbans.broadcast") || p.hasPermission("professionalbans.*")){
                if(args.length > 0){
                    String message = "";
                    for(int i = 0; i < args.length; i++){
                        message = message + " " + args[i];
                    }
                    File file = new File(Main.main.getDataFolder(), "config.yml");
                    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§8[]===================================[]");
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("CHATFORMAT.BROADCAST").replace("%message%", message)));
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§8[]===================================[]");
                    Bukkit.broadcastMessage("");

                    MessagesManager.insertMessage(p.getUniqueId().toString(), "BROADCAST", message);
                } else {
                    p.sendMessage(Main.data.Prefix+"/bc <"+Main.messages.getString("message")+">");
                }
            } else {
                p.sendMessage(Main.data.NoPerms);
            }
        } else {
            Bukkit.broadcastMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}
