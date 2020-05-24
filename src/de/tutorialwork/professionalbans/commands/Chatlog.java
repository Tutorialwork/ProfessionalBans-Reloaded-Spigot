package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.listener.Chat;
import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.LogManager;
import de.tutorialwork.professionalbans.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chatlog implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length == 0){
                p.sendMessage(Main.data.Prefix+"/chatlog <"+Main.messages.getString("player")+">");
            } else {
                String UUID = UUIDFetcher.getUUID(args[0]);
                if(UUID != null){
                    if(Main.ban.playerExists(UUID)){
                        if(!p.getUniqueId().toString().equals(UUID)){
                            if(Chat.hasMessages(UUID)){
                                String ID = Chat.createChatlog(UUID, p.getUniqueId().toString());
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("chatlog_success").replace("%player%", Main.ban.getNameByUUID(UUID)));
                                if(Main.data.WebURL != null){
                                    p.sendMessage(Main.data.Prefix+"Link: §e§l"+Main.data.WebURL+"/chatlogs/"+ID);
                                } else {
                                    p.sendMessage(Main.data.Prefix+Main.messages.getString("link_err"));
                                }
                                LogManager.createEntry(UUID, p.getUniqueId().toString(), "CREATE_CHATLOG", ID);
                            } else {
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("chatlog_no_msg"));
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("chatlog_self_err"));
                        }
                    } else {
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                    }
                } else {
                    p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}
