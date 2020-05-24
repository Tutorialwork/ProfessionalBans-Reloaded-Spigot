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
import java.util.ArrayList;

public class TeamChat implements CommandExecutor {

    private String chatformat;
    private String message = "";
    private ArrayList<Player> notloggedplayers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(Main.ban.webaccountExists(p.getUniqueId().toString())){
                if(args.length > 0){
                    if(args[0].equalsIgnoreCase("toggle")){
                        if(!notloggedplayers.contains(p)){
                            notloggedplayers.add(p);
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("tc_logged_off"));
                        } else {
                            notloggedplayers.remove(p);
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("tc_logged_on"));
                        }
                        return false;
                    } else {
                        if(notloggedplayers.contains(p)){
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("tc_logged_err"));
                            return false;
                        }
                    }
                    message = "";
                    for(int i = 0; i < args.length; i++){
                        message = message + " " + args[i];
                    }

                    File file = new File(Main.main.getDataFolder(), "config.yml");
                    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                    chatformat = ChatColor.translateAlternateColorCodes('&', cfg.getString("CHATFORMAT.TEAMCHAT"));

                    for(Player all : Bukkit.getOnlinePlayers()){
                        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                            if(Main.ban.webaccountExists(all.getUniqueId().toString()) && !notloggedplayers.contains(all)){
                                if(!all.getUniqueId().toString().equals(p.getUniqueId().toString())){
                                    all.sendMessage(chatformat.replace("%from%", p.getName()).replace("%message%", message));
                                } else {
                                    all.sendMessage(chatformat.replace("%from%", Main.messages.getString("you")).replace("%message%", message));
                                }
                                MessagesManager.insertMessage(p.getUniqueId().toString(), "TEAM", message);
                                if(MessagesManager.getFirebaseToken(all.getUniqueId().toString()) != null){
                                    MessagesManager.sendPushNotify(MessagesManager.getFirebaseToken(all.getUniqueId().toString()), Main.messages.getString("message_from")+" "+p.getName(), message);
                                }
                            }
                        });
                    }
                } else {
                    p.sendMessage(Main.data.Prefix+"/tc <"+Main.messages.getString("message")+">");
                }
            } else {
                p.sendMessage(Main.data.NoPerms);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}
