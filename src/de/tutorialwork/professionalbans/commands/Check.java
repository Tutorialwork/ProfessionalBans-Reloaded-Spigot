package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.TimeManager;
import de.tutorialwork.professionalbans.utils.UUIDFetcher;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Check implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("professionalbans.check") || p.hasPermission("professionalbans.*")){
                if(args.length == 0){
                    p.sendMessage(Main.data.Prefix+"/check <"+Main.messages.getString("player")+"/IP>");
                } else if(args[0].equalsIgnoreCase("jump")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null) {
                        p.teleport(target.getLocation());
                    } else {
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                    }
                } else {
                    String UUID = UUIDFetcher.getUUID(args[0]);
                    if(IPBan.validate(args[0])){
                        String IP = args[0];
                        if(Main.ip.IPExists(IP)){
                            p.sendMessage("§8[]===================================[]");
                            if(Main.ip.getPlayerFromIP(IP) != null){
                                TextComponent tc = new TextComponent();
                                tc.setText("§7"+Main.messages.getString("player")+": §e§l"+Main.ban.getNameByUUID(Main.ip.getPlayerFromIP(IP)));
                                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/check " + Main.ban.getNameByUUID(Main.ip.getPlayerFromIP(IP))));
                                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§e"+Main.ban.getNameByUUID(Main.ip.getPlayerFromIP(IP))+" §7überprüfen").create()));
                                p.spigot().sendMessage(tc);
                            } else {
                                p.sendMessage("§7"+Main.messages.getString("player")+": §c§lKeiner");
                            }
                            if(Main.ip.isBanned(IP)){
                                p.sendMessage("§7IP-Ban: §c§l"+Main.messages.getString("yes")+" §8/ "+Main.ip.getReasonString(Main.ip.getIPFromPlayer(UUID)));
                            } else {
                                p.sendMessage("§7IP-Ban: §a§l"+Main.messages.getString("no")+"");
                            }
                            p.sendMessage("§7Bans: §e§l"+Main.ip.getBans(IP));
                            p.sendMessage("§7"+Main.messages.getString("lastuse")+": §e§l"+Main.ban.formatTimestamp(Main.ip.getLastUseLong(IP)));
                            p.sendMessage("§8[]===================================[]");
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("ip_404"));
                        }
                    } else {
                        if (Main.ban.playerExists(UUID)) {
                            p.sendMessage("§8[]===================================[]");
                            if (Main.ip.getIPFromPlayer(UUID) != null) {
                                p.sendMessage("§7"+Main.messages.getString("player")+": §e§l" + Main.ban.getNameByUUID(UUID) + " §8(§e" + Main.ip.getIPFromPlayer(UUID) + "§8)");
                            } else {
                                p.sendMessage("§7"+Main.messages.getString("player")+": §e§l" + Main.ban.getNameByUUID(UUID));
                            }
                            if (Main.ban.isBanned(UUID)) {
                                p.sendMessage("§7"+Main.messages.getString("banned")+": §c§l"+Main.messages.getString("yes")+" §8/ " + Main.ban.getReasonString(UUID));
                            } else {
                                p.sendMessage("§7"+Main.messages.getString("banned")+": §a§l"+Main.messages.getString("no")+"");
                            }
                            if (Main.ban.isMuted(UUID)) {
                                p.sendMessage("§7"+Main.messages.getString("muted")+": §c§l"+Main.messages.getString("yes")+" §8/ " + Main.ban.getReasonString(UUID));
                            } else {
                                p.sendMessage("§7"+Main.messages.getString("muted")+": §a§l"+Main.messages.getString("no")+"");
                            }
                            if (Main.ip.isBanned(Main.ip.getIPFromPlayer(UUID))) {
                                p.sendMessage("§7IP-Ban: §c§l"+Main.messages.getString("yes")+" §8/ " + Main.ip.getReasonString(Main.ip.getIPFromPlayer(UUID)));
                            } else {
                                p.sendMessage("§7IP-Ban: §a§l"+Main.messages.getString("no")+"");
                            }
                            p.sendMessage("§7Bans: §e§l" + Main.ban.getBans(UUID));
                            p.sendMessage("§7Mutes: §e§l" + Main.ban.getMutes(UUID));
                            p.sendMessage("§7"+Main.messages.getString("ontime")+": §e§l" + TimeManager.formatOnlineTime(TimeManager.getOnlineTime(UUID)));
                            if (Main.ban.getLastLogin(UUID) != null) {
                                if (TimeManager.getOnlineStatus(UUID) == 0) {
                                    p.sendMessage("§7"+Main.messages.getString("lastlogin")+": §e§l" + Main.ban.formatTimestamp(Long.valueOf(Main.ban.getLastLogin(UUID))));
                                } else {
                                    Player target = Bukkit.getPlayer(args[0]);
                                    if (target != null) {
                                        TextComponent tc = new TextComponent();
                                        tc.setText("§7"+Main.messages.getString("lastlogin")+": §a§lOnline §7@ §e" + target.getWorld().getName());
                                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/check jump " + target.getName()));
                                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klicken um §e§l" + target.getName() + " §7nachzuspringen").create()));
                                        p.spigot().sendMessage(tc);
                                    } else {
                                        p.sendMessage("§7"+Main.messages.getString("lastlogin")+": §e§l" + Main.ban.formatTimestamp(Long.valueOf(Main.ban.getLastLogin(UUID))));
                                    }
                                }
                            }
                            if (Main.ban.getFirstLogin(UUID) != null) {
                                p.sendMessage("§7"+Main.messages.getString("firstlogin")+": §e§l" + Main.ban.formatTimestamp(Long.valueOf(Main.ban.getFirstLogin(UUID))));
                            }
                            p.sendMessage("§8[]===================================[]");
                        } else {
                            p.sendMessage(Main.data.Prefix + Main.messages.getString("player_404"));
                        }
                    }

                }
            } else {
                p.sendMessage(Main.data.NoPerms);
            }
        } else {
            if(args.length == 0){
                Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"/check <"+Main.messages.getString("player")+"/IP>");
            } else {
                String UUID = UUIDFetcher.getUUID(args[0]);
                if(IPBan.validate(args[0])){
                    String IP = args[0];
                    if(Main.ip.IPExists(IP)){
                        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                        if(Main.ip.getPlayerFromIP(IP) != null){
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("player")+": §e§l"+Main.ban.getNameByUUID(Main.ip.getPlayerFromIP(IP)));
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("player")+": §c§lKeiner");
                        }
                        if(Main.ip.isBanned(IP)){
                            Bukkit.getConsoleSender().sendMessage("§7IP-Ban: §c§l"+Main.messages.getString("yes")+" §8/ "+Main.ip.getReasonString(Main.ip.getIPFromPlayer(UUID)));
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§7IP-Ban: §a§l"+Main.messages.getString("no")+"");
                        }
                        Bukkit.getConsoleSender().sendMessage("§7Bans: §e§l"+Main.ip.getBans(IP));
                        Bukkit.getConsoleSender().sendMessage("§7Zuletzt genutzt: §e§l"+Main.ban.formatTimestamp(Main.ip.getLastUseLong(IP)));
                        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                    } else {
                        Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"§cZu dieser IP-Adresse sind keine Informationen verfügbar");
                    }
                } else {
                    if(Main.ban.playerExists(UUID)){
                        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                        Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("player")+": §e§l"+Main.ban.getNameByUUID(UUID));
                        if(Main.ban.isBanned(UUID)){
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("banned")+": §c§l"+Main.messages.getString("yes")+" §8/ "+Main.ban.getReasonString(UUID));
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("banned")+": §a§l"+Main.messages.getString("no")+"");
                        }
                        if(Main.ban.isMuted(UUID)){
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("muted")+": §c§l"+Main.messages.getString("yes")+" §8/ "+Main.ban.getReasonString(UUID));
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("muted")+": §a§l"+Main.messages.getString("no")+"");
                        }
                        if(Main.ip.isBanned(Main.ip.getIPFromPlayer(UUID))){
                            Bukkit.getConsoleSender().sendMessage("§7IP-Ban: §c§l"+Main.messages.getString("yes")+" §8/ "+Main.ip.getReasonString(Main.ip.getIPFromPlayer(UUID)));
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§7IP-Ban: §a§l"+Main.messages.getString("no")+"");
                        }
                        Bukkit.getConsoleSender().sendMessage("§7Bans: §e§l"+Main.ban.getBans(UUID));
                        Bukkit.getConsoleSender().sendMessage("§7Mutes: §e§l"+Main.ban.getMutes(UUID));
                        if(Main.ban.getLastLogin(UUID) != null){
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("lastlogin")+": §e§l"+Main.ban.formatTimestamp(Long.valueOf(Main.ban.getLastLogin(UUID))));
                        }
                        if(Main.ban.getFirstLogin(UUID) != null){
                            Bukkit.getConsoleSender().sendMessage("§7"+Main.messages.getString("firstlogin")+": §e§l"+Main.ban.formatTimestamp(Long.valueOf(Main.ban.getFirstLogin(UUID))));
                        }
                        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                    } else {
                        Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                    }
                }
            }
        }
        return false;
    }
}
