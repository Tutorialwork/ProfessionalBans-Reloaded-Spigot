package de.tutorialwork.professionalbans.listener;

import de.tutorialwork.professionalbans.commands.Language;
import de.tutorialwork.professionalbans.commands.SupportChat;
import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Login implements Listener {

    public static HashMap<Player, Long> logintimes = new HashMap<>();

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e){
        String UUID = e.getUniqueId().toString();
        String IP = e.getAddress().getHostAddress();
        Main.ban.createPlayer(UUID, e.getName());
        Main.ip.insertIP(IP, UUID);
        File config = new File(Main.main.getDataFolder(), "config.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
        if(cfg.getBoolean("VPN.BLOCKED")){
            if(!Main.data.ipwhitelist.contains(IP)){
                if(Main.ip.isVPN(IP)){
                    if(cfg.getBoolean("VPN.KICK")){
                        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                        e.setKickMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("VPN.KICKMSG")));
                    }
                    if(cfg.getBoolean("VPN.BAN")){
                        int id = cfg.getInt("VPN.BANID");
                        Main.ban.ban(UUID, id, "KONSOLE", Main.data.increaseValue, Main.data.increaseBans);
                        Main.ban.sendNotify("IPBAN", IP, "KONSOLE", Main.ban.getReasonByID(id));
                        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                        if(Main.ban.getRAWEnd(UUID) == -1L){
                            e.setKickMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("LAYOUT.IPBAN").replace("%grund%", Main.ban.getReasonByID(id))));
                        } else {
                            String MSG = cfg.getString("LAYOUT.TEMPIPBAN");
                            MSG = MSG.replace("%grund%", Main.ban.getReasonString(UUID));
                            MSG = MSG.replace("%dauer%", Main.ban.getEnd(UUID));
                            e.setKickMessage(ChatColor.translateAlternateColorCodes('&', MSG));
                        }
                    }
                }
            }
        }
        if(Main.ip.isBanned(IP)){
            YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);

            if(Main.ip.getRAWEnd(IP) == -1L){
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.IPBAN").replace("%grund%", Main.ip.getReasonString(IP))));
            } else {
                if(System.currentTimeMillis() < Main.ip.getRAWEnd(IP)){
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    String MSG = configcfg.getString("LAYOUT.TEMPIPBAN");
                    MSG = MSG.replace("%grund%", Main.ip.getReasonString(IP));
                    MSG = MSG.replace("%dauer%", Main.ip.getEnd(IP));
                    e.setKickMessage(ChatColor.translateAlternateColorCodes('&', MSG));
                } else {
                    Main.ip.unban(IP);
                }
            }
        }
        if(Main.ban.isBanned(UUID)){
            YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);

            if(Main.ban.getRAWEnd(UUID) == -1L){
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.BAN").replace("%grund%", Main.ban.getReasonString(UUID)).replace("%ea-status%", Main.ban.getEAStatus(UUID))));
            } else {
                if(System.currentTimeMillis() < Main.ban.getRAWEnd(UUID)){
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    String MSG = configcfg.getString("LAYOUT.TEMPBAN");
                    MSG = MSG.replace("%grund%", Main.ban.getReasonString(UUID));
                    MSG = MSG.replace("%dauer%", Main.ban.getEnd(UUID));
                    MSG = MSG.replace("%ea-status%", Main.ban.getEAStatus(UUID));
                    e.setKickMessage(ChatColor.translateAlternateColorCodes('&', MSG));
                } else {
                    Main.ban.unban(UUID);
                }
            }
        }
    }

    @EventHandler
    public void onFinalLogin(PlayerLoginEvent e){
        Player p = e.getPlayer();
        if(p.hasPermission("professionalbans.reports") || p.hasPermission("professionalbans.*")){
            if(Main.ban.countOpenReports() != 0){
                p.sendMessage(Main.data.Prefix+Main.messages.getString("open_report_notify").replace("%count%", Main.ban.countOpenReports()+""));
            }
        }
        if(p.hasPermission("professionalbans.supportchat") || p.hasPermission("professionalbans.*")){
            if(SupportChat.openchats.size() != 0){
                p.sendMessage(Main.data.Prefix+Main.messages.getString("open_support_notify").replace("%count%", SupportChat.openchats.size()+""));
            }
        }
        //Update Check
        if(p.hasPermission("professionalbans.*")){
            if(!Main.callURL("https://api.spigotmc.org/legacy/update.php?resource=63657").equals(Main.Version)){
                p.sendMessage("§8[]===================================[]");
                p.sendMessage("§e§lProfessionalBans §7Reloaded §8| §7Version §c"+Main.Version);
                p.sendMessage(Main.messages.getString("update"));
                p.sendMessage("§7Update: §4§lhttps://spigotmc.org/resources/63657");
                p.sendMessage("§8[]===================================[]");
            }
        }
        //WebURL Conf Check
        if(Main.data.WebURL == null || Main.data.WebURL == "https://bans.YourServer.com"){
            p.sendMessage("§8[]===================================[]");
            p.sendMessage(Main.messages.getString("config_notify"));
            p.sendMessage("§8[]===================================[]");
        }
        //Language Check
        if(!Language.isLanguageSet()){
            p.sendMessage("§8[]===================================[]");

            p.sendMessage("§6§lProfessional§e§lBans §7by §bTutorialwork");
            p.sendMessage("§7Please select a language.");

            TextComponent en = new TextComponent();
            en.setText("§8• §cEnglish");
            en.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/language en"));
            en.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to set language to §cEnglish").create()));

            TextComponent de = new TextComponent();
            de.setText("§8• §aGerman");
            de.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/language de"));
            de.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to set language to §aGerman").create()));

            p.spigot().sendMessage(en);
            p.spigot().sendMessage(de);

            p.sendMessage("§8[]===================================[]");
        }

        TimeManager.updateOnlineStatus(p.getUniqueId().toString(), 1);
        logintimes.put(p, System.currentTimeMillis());
    }
}
