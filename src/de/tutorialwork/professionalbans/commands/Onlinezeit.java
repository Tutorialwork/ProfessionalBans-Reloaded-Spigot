package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;

import de.tutorialwork.professionalbans.utils.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Onlinezeit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length == 0){
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                    ArrayList<String> players = TimeManager.getTopOnlineTime();
                    p.sendMessage("§8[]============§8[§e§l Top "+players.size()+" "+Main.messages.getString("ontime")+" §8]============[]");
                    for(int i = 0; i < players.size(); i++){
                        int rank = i + 1;
                        String name = Main.ban.getNameByUUID(players.get(i));
                        p.sendMessage("§8#§7"+rank+" » §e"+name+" §7- "+TimeManager.formatOnlineTime(TimeManager.getOnlineTime(players.get(i)) / 60 / 1000));
                    }
                });
            } else {
                if(args.length == 1){
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                        String UUID = Main.ban.getUUIDByName(args[0]);
                        if(UUID != null && Main.ban.playerExists(UUID)){
                            Player target = Bukkit.getPlayer(args[0]);
                            p.sendMessage("§8[]=================§8[§e§l "+Main.ban.getNameByUUID(UUID)+" §8]=================[]");

                            if(target != null){
                                p.sendMessage(Main.messages.getString("ontime_on_msg")+TimeManager.formatOnlineTime(TimeManager.getOnlineTime(UUID) / 60 / 1000));
                            } else {
                                p.sendMessage(Main.messages.getString("ontime_off_msg").replace("%date%", Main.ban.formatTimestamp(Long.valueOf(Main.ban.getLastLogin(UUID))))+TimeManager.formatOnlineTime(TimeManager.getOnlineTime(UUID) / 60 / 1000));
                            }

                            String spaces = "";
                            for(int i = 0; i < Main.ban.getNameByUUID(UUID).length() + 4; i++){
                                spaces = spaces + "=";
                            }
                            p.sendMessage("§8[]=================================="+spaces+"[]");
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                        }
                    });
                } else {
                    p.sendMessage(Main.data.Prefix+"/onlinezeit <§e"+Main.messages.getString("player")+"§7>");
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}

