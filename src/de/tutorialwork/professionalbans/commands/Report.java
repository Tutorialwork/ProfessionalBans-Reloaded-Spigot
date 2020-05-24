package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.LogManager;
import de.tutorialwork.professionalbans.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

public class Report implements CommandExecutor {

    public static ArrayList<Player> players = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0 || args.length == 1){
                String reasons = "";
                int komma = Main.data.reportreasons.size();
                for(String reason : Main.data.reportreasons){
                    komma--;
                    if(komma != 0){
                        reasons = reasons + reason + ", ";
                    } else {
                        reasons = reasons + reason;
                    }
                }
                p.sendMessage(Main.data.Prefix+Main.messages.getString("reason")+": §e§l"+reasons);
                p.sendMessage(Main.data.Prefix+"/report <"+Main.messages.getString("player")+"> <"+Main.messages.getString("reason")+">");
            } else {
                if(args[0].toUpperCase().equals(p.getName().toUpperCase())){
                    p.sendMessage(Main.data.Prefix+Main.messages.getString("no_self_report"));
                    return false;
                }
                if(Main.data.reportreasons.contains(args[1].toUpperCase())){
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target != null){
                        if(!players.contains(p)){
                            players.add(p);
                            Main.ban.createReport(target.getUniqueId().toString(), p.getUniqueId().toString(), args[1].toUpperCase(), null);
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("report_success").replace("%player%", target.getName()).replace("%reason%", args[1].toUpperCase()));
                            Main.ban.sendNotify("REPORT", target.getName(), p.getName(), args[1].toUpperCase());
                            LogManager.createEntry(target.getUniqueId().toString(), p.getUniqueId().toString(), "REPORT", args[1].toUpperCase());
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("wait"));
                        }
                    } else {
                        File file = new File(Main.main.getDataFolder(), "config.yml");
                        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                        if(cfg.getBoolean("REPORTS.OFFLINEREPORTS")){
                            String UUID = UUIDFetcher.getUUID(args[0]);
                            if(UUID != null){
                                if(Main.ban.playerExists(UUID)){
                                    if(!players.contains(p)){
                                        players.add(p);
                                        Main.ban.createReport(UUID, p.getUniqueId().toString(), args[1].toUpperCase(), null);
                                        p.sendMessage(Main.data.Prefix+Main.messages.getString("report_success").replace("%player%", args[0]).replace("%reason%", args[1].toUpperCase()));
                                        LogManager.createEntry(UUID, p.getUniqueId().toString(), "REPORT_OFFLINE", args[1].toUpperCase());
                                    } else {
                                        p.sendMessage(Main.data.Prefix+Main.messages.getString("wait"));
                                    }
                                } else {
                                    p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                                }
                            } else {
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                        }
                    }
                } else {
                    p.sendMessage(Main.data.Prefix+Main.messages.getString("report_reason_404"));
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}
