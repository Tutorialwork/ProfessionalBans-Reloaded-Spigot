package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.LogManager;
import de.tutorialwork.professionalbans.utils.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.regex.Pattern;

public class IPBan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("professionalbans.ipban") || p.hasPermission("professionalbans.*")){
                if(args.length == 0 || args.length == 1){
                    for(int zaehler = 1; zaehler < Main.ban.countReasons()+1; zaehler++) {
                        if(Main.ban.isBanReason(zaehler)){
                            p.sendMessage("§7"+zaehler+" §8| §e"+Main.ban.getReasonByID(zaehler));
                        }
                    }
                    p.sendMessage(Main.data.Prefix+"/ipban <IP/"+Main.messages.getString("player")+"> <"+Main.messages.getString("reason")+"-ID>");
                } else {
                    String IP = args[0];
                    int ID = Integer.valueOf(args[1]);
                    if(validate(IP)){
                        if(Main.ban.getReasonByID(ID) != null){
                            if(Main.ip.IPExists(IP)){
                                Main.ip.ban(IP, ID, p.getUniqueId().toString());
                                Main.ip.addBan(IP);
                                Main.ban.sendNotify("IPBAN", IP, p.getName(), Main.ban.getReasonByID(ID));
                            } else {
                                Main.ip.insertIP(IP, null);
                                Main.ip.ban(IP, ID, p.getUniqueId().toString());
                                Main.ip.addBan(IP);
                                Main.ban.sendNotify("IPBAN", IP, p.getName(), Main.ban.getReasonByID(ID));
                            }
                            disconnectIPBannedPlayers(IP);
                            LogManager.createEntry(null, p.getUniqueId().toString(), "IPBAN_IP", IP);
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("reason_404"));
                        }
                    } else {
                        String UUID = UUIDFetcher.getUUID(args[0]);
                        if(Main.ip.getIPFromPlayer(UUID) != null){
                            String DBIP = Main.ip.getIPFromPlayer(UUID);
                            Main.ip.ban(DBIP, ID, p.getUniqueId().toString());
                            Main.ip.addBan(DBIP);
                            Main.ban.sendNotify("IPBAN", DBIP, p.getName(), Main.ban.getReasonByID(ID));
                            disconnectIPBannedPlayers(DBIP);
                            LogManager.createEntry(UUID, p.getUniqueId().toString(), "IPBAN_PLAYER", String.valueOf(ID));
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("player_ip_404"));
                        }
                    }
                }
            } else {
                p.sendMessage(Main.data.NoPerms);
            }
        }
        return false;
    }

    public static void disconnectIPBannedPlayers(String IP){
        for(Player all : Bukkit.getOnlinePlayers()){
            if(all.getAddress().getHostString().equals(IP)){
                File config = new File(Main.main.getDataFolder(), "config.yml");
                YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);

                if(Main.ip.getRAWEnd(IP) == -1L){
                    all.kickPlayer(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.IPBAN").replace("%grund%", Main.ip.getReasonString(IP))));
                } else {
                    if(System.currentTimeMillis() < Main.ip.getRAWEnd(IP)){
                        String MSG = configcfg.getString("LAYOUT.TEMPIPBAN");
                        MSG = MSG.replace("%grund%", Main.ip.getReasonString(IP));
                        MSG = MSG.replace("%dauer%", Main.ip.getEnd(IP));
                        all.kickPlayer(ChatColor.translateAlternateColorCodes('&', MSG));
                    } else {
                        Main.ip.unban(IP);
                    }
                }
            }
        }
    }

    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
}
