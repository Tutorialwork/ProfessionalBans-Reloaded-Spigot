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

public class PrivateMessageReply implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(!Main.ban.isMuted(p.getUniqueId().toString())){
                if(MessagesManager.getLastChatPlayer(p) != null){
                    if(args.length > 0){
                        String message = "";
                        for(int i = 0; i < args.length; i++){
                            message = message + " " + args[i];
                        }
                        if(!Main.ban.isMuted(MessagesManager.getLastChatPlayer(p).getUniqueId().toString())){
                            if(!MSGToggle.toggle.contains(MessagesManager.getLastChatPlayer(p))){
                                MessagesManager.sendMessage(p, MessagesManager.getLastChatPlayer(p), message);
                            } else {
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("msg_toggled"));
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("target_muted"));
                        }
                    } else {
                        p.sendMessage(Main.data.Prefix+"/r <"+Main.messages.getString("message")+"> - §8§oAntwortet §e§l"+MessagesManager.getLastChatPlayer(p).getName());
                    }
                } else {
                    p.sendMessage(Main.data.Prefix+Main.messages.getString("no_reply"));
                }
            } else {
                File config = new File(Main.main.getDataFolder(), "config.yml");
                YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);

                String MSG = configcfg.getString("LAYOUT.TEMPMUTE");
                MSG = MSG.replace("%grund%", Main.ban.getReasonString(p.getUniqueId().toString()));
                MSG = MSG.replace("%dauer%", Main.ban.getEnd(p.getUniqueId().toString()));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}
