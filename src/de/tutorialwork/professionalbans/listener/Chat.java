package de.tutorialwork.professionalbans.listener;

import de.tutorialwork.professionalbans.commands.SupportChat;
import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.LogManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.security.SecureRandom;
import java.sql.*;

public class Chat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p = (Player) e.getPlayer();
        if(!e.getMessage().startsWith("/")){
            if(SupportChat.activechats.containsKey(p)){
                e.setCancelled(true);
                Player target = SupportChat.activechats.get(p);
                target.sendMessage("§9§lSUPPORT §8• §c"+p.getName()+" §8» "+e.getMessage());
                p.sendMessage("§9§lSUPPORT §8• §aDu §8» "+e.getMessage());
            }
            if(SupportChat.activechats.containsValue(p)){
                e.setCancelled(true);
                for(Player key : SupportChat.activechats.keySet()){
                    //Key has started the support chat
                    key.sendMessage("§9§lSUPPORT §8• §c"+p.getName()+" §8» "+e.getMessage());
                }
                p.sendMessage("§9§lSUPPORT §8• §aDu §8» "+e.getMessage());
            }
            if(Main.ban.isMuted(p.getUniqueId().toString())){
                File config = new File(Main.main.getDataFolder(), "config.yml");
                YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);

                if(Main.ban.getRAWEnd(p.getUniqueId().toString()) == -1L){
                    e.setCancelled(true);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.MUTE").replace("%grund%", Main.ban.getReasonString(p.getUniqueId().toString()))));
                } else {
                    if(System.currentTimeMillis() < Main.ban.getRAWEnd(p.getUniqueId().toString())){
                        e.setCancelled(true);
                        String MSG = configcfg.getString("LAYOUT.TEMPMUTE");
                        MSG = MSG.replace("%grund%", Main.ban.getReasonString(p.getUniqueId().toString()));
                        MSG = MSG.replace("%dauer%", Main.ban.getEnd(p.getUniqueId().toString()));
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG));
                    } else {
                        Main.ban.unmute(p.getUniqueId().toString());
                    }
                }
            } else {
                File config = new File(Main.main.getDataFolder(), "config.yml");

                YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);

                if(!p.hasPermission("professionalbans.blacklist.bypass") || !p.hasPermission("professionalbans.*")){
                    if(configcfg.getBoolean("AUTOMUTE.ENABLED")){
                        insertMessage(p.getUniqueId().toString(), e.getMessage(), p.getLocation().getWorld().getName());
                        for(String blacklist : Main.data.blacklist){
                            if(e.getMessage().toUpperCase().contains(blacklist.toUpperCase())){
                                e.setCancelled(true);
                                Main.ban.mute(p.getUniqueId().toString(), configcfg.getInt("AUTOMUTE.MUTEID"), "KONSOLE");
                                LogManager.createEntry(p.getUniqueId().toString(), "KONSOLE", "AUTOMUTE_BLACKLIST", e.getMessage());
                                Main.ban.setMutes(p.getUniqueId().toString(), Main.ban.getMutes(p.getUniqueId().toString()) + 1);
                                Main.ban.sendNotify("AUTOMUTE", Main.ban.getNameByUUID(p.getUniqueId().toString()), e.getMessage(), Main.ban.getReasonByID(configcfg.getInt("AUTOMUTE.MUTEID")));
                                if(Main.ban.getRAWEnd(p.getUniqueId().toString()) == -1L){
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.MUTE").replace("%grund%", Main.ban.getReasonByID(configcfg.getInt("AUTOMUTE.MUTEID")))));
                                } else {
                                    String MSG = configcfg.getString("LAYOUT.TEMPMUTE");
                                    MSG = MSG.replace("%grund%", Main.ban.getReasonString(p.getUniqueId().toString()));
                                    MSG = MSG.replace("%dauer%", Main.ban.getEnd(p.getUniqueId().toString()));
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG));
                                }
                                return;
                            }
                        }
                        for(String adblacklist : Main.data.adblacklist){
                            if(e.getMessage().toUpperCase().contains(adblacklist.toUpperCase())){
                                if(!Main.data.adwhitelist.contains(e.getMessage().toUpperCase())){
                                    e.setCancelled(true);
                                    Main.ban.mute(p.getUniqueId().toString(), configcfg.getInt("AUTOMUTE.ADMUTEID"), "KONSOLE");
                                    LogManager.createEntry(p.getUniqueId().toString(), "KONSOLE", "AUTOMUTE_ADBLACKLIST", e.getMessage());
                                    Main.ban.setMutes(p.getUniqueId().toString(), Main.ban.getMutes(p.getUniqueId().toString()) + 1);
                                    Main.ban.sendNotify("AUTOMUTE", Main.ban.getNameByUUID(p.getUniqueId().toString()), e.getMessage(), Main.ban.getReasonByID(configcfg.getInt("AUTOMUTE.ADMUTEID")));
                                    if(Main.ban.getRAWEnd(p.getUniqueId().toString()) == -1L){
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.MUTE").replace("%grund%", Main.ban.getReasonByID(configcfg.getInt("AUTOMUTE.MUTEID")))));
                                    } else {
                                        String MSG = configcfg.getString("LAYOUT.TEMPMUTE");
                                        MSG = MSG.replace("%grund%", Main.ban.getReasonString(p.getUniqueId().toString()));
                                        MSG = MSG.replace("%dauer%", Main.ban.getEnd(p.getUniqueId().toString()));
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG));
                                    }
                                    return;
                                }
                            }
                        }
                    } else {
                        insertMessage(p.getUniqueId().toString(), e.getMessage(), p.getLocation().getWorld().getName());
                        if(configcfg.getBoolean("AUTOMUTE.AUTOREPORT")){
                            for(String blacklist : Main.data.blacklist){
                                if(e.getMessage().toUpperCase().contains(blacklist.toUpperCase())){
                                    e.setCancelled(true);
                                    p.sendMessage(Main.data.Prefix+"§cAchte auf deine Wortwahl");
                                    String LogID = Chat.createChatlog(p.getUniqueId().toString(), "KONSOLE");
                                    Main.ban.createReport(p.getUniqueId().toString(),"KONSOLE", "VERHALTEN", LogID);
                                    Main.ban.sendNotify("REPORT", p.getName(), "KONSOLE", "VERHALTEN");
                                    return;
                                }
                            }
                            for(String adblacklist : Main.data.adblacklist){
                                if(e.getMessage().toUpperCase().contains(adblacklist.toUpperCase())){
                                    if(!Main.data.adwhitelist.contains(e.getMessage().toUpperCase())){
                                        e.setCancelled(true);
                                        p.sendMessage(Main.data.Prefix+"§cDu darfst keine Werbung machen");
                                        String LogID = Chat.createChatlog(p.getUniqueId().toString(), "KONSOLE");
                                        Main.ban.createReport(p.getUniqueId().toString(),"KONSOLE", "WERBUNG", LogID);
                                        Main.ban.sendNotify("REPORT", p.getName(), "KONSOLE", "WERBUNG");
                                        return;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                        insertMessage(p.getUniqueId().toString(), e.getMessage(), p.getWorld().getName());
                    });
                }
            }
        }
    }

    public static void insertMessage(String UUID, String Message, String Server){
        try {
            PreparedStatement ps = Main.mysql.getCon().prepareStatement("INSERT INTO chat(UUID, SERVER, MESSAGE, SENDDATE) VALUES (?, ?, ?, ?)");
            ps.setString(1, UUID);
            ps.setString(2, Server);
            ps.setString(3, Message);
            ps.setLong(4, System.currentTimeMillis());
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static String createChatlog(String UUID, String CreatedUUID){
        try {
            PreparedStatement ps = Main.mysql.getCon()
                    .prepareStatement("SELECT * FROM chat WHERE UUID=?");
            ps.setString(1, UUID);
            ResultSet rs = ps.executeQuery();
            String ID = randomString(20);
            Long now = System.currentTimeMillis();
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                try{
                    while (rs.next()){
                        int TEN_MINUTES = 10 * 60 * 1000;
                        long tenAgo = System.currentTimeMillis() - TEN_MINUTES;
                        if (Long.valueOf(rs.getString("SENDDATE")) > tenAgo) {
                            PreparedStatement preparedStatement = Main.mysql.getCon()
                                    .prepareStatement("INSERT INTO chatlog(LOGID, UUID, CREATOR_UUID, SERVER, MESSAGE, SENDDATE, CREATED_AT) " +
                                            "VALUES (?, ?, ?, ?, ?, ?, ?)");
                            preparedStatement.setString(1, ID);
                            preparedStatement.setString(2, UUID);
                            preparedStatement.setString(3, CreatedUUID);
                            preparedStatement.setString(4, rs.getString("SERVER"));
                            preparedStatement.setString(5, rs.getString("MESSAGE"));
                            preparedStatement.setString(6, rs.getString("SENDDATE"));
                            preparedStatement.setLong(7, now);
                            preparedStatement.executeUpdate();
                            preparedStatement.close();
                        }
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
            });
            return ID;
        } catch (SQLException exc){
            exc.printStackTrace();
        }
        return null;
    }

    public static boolean hasMessages(String UUID){
        try {
            PreparedStatement ps = Main.mysql.getCon()
                    .prepareStatement("SELECT * FROM chat WHERE UUID=?");
            ps.setString(1, UUID);
            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()){
                i++;
            }
            if(i != 0){
                return true;
            } else {
                return false;
            }
        } catch (SQLException exc){

        }
        return false;
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String randomString(int len){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

}
