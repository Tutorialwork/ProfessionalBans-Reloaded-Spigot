package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.LogManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Reports implements CommandExecutor {

    public static ArrayList<Player> not_logged = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("professionalbans.reports") || p.hasPermission("professionalbans.*")){
                if(args.length == 0){
                    if(Main.ban.countOpenReports() != 0){
                        p.sendMessage("§8[]===================================[]");
                        p.sendMessage(Main.messages.getString("open_reports")+" §7(§8"+Main.ban.countOpenReports()+"§7)");
                        int offline = 0;
                        for(int i = 0; i < Main.ban.getIDsFromOpenReports().size(); ++i) {
                            int id = (int) Main.ban.getIDsFromOpenReports().get(i);
                            Player target = Bukkit.getPlayer(Main.ban.getNameByReportID(id));
                            if(target != null){
                                TextComponent tc = new TextComponent();
                                tc.setText("§e§l"+target.getName()+" "+Main.messages.getString("reported_from")+" §c§l "+Main.ban.getReasonByReportID(id)+" §8| §7Online @ §e§l"+target.getWorld().getName());
                                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports jump "+target.getName()+" "+id));
                                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klicken um §e§l"+target.getName()+" §7nachzuspringen").create()));
                                p.spigot().sendMessage(tc);
                            } else {
                                offline++;
                            }
                        }
                        if(offline != 0){
                            p.sendMessage("§4§o"+offline+" "+Main.messages.getString("open_reports_count"));
                        }
                        p.sendMessage("§8[]===================================[]");
                    } else {
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("no_reports_open"));
                    }
                } else if(args[0].equalsIgnoreCase("jump")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){
                        p.teleport(target.getLocation());
                        int id = Integer.parseInt(args[2]);
                        Main.ban.setReportDone(id);
                        Main.ban.setReportTeamUUID(id, p.getUniqueId().toString());
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("report_accepted").replace("%player%", Main.ban.getNameByReportID(id)).replace("%reason%", Main.ban.getReasonByReportID(id)));
                        LogManager.createEntry(p.getUniqueId().toString(), null, "REPORT_ACCEPT", String.valueOf(id));
                    } else {
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                    }
                } else if(args[0].equalsIgnoreCase("toggle")){
                    if(not_logged.contains(p)){
                        not_logged.remove(p);
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("report_toggle_on"));
                    } else {
                        not_logged.add(p);
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("report_toggle_off"));
                    }
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
