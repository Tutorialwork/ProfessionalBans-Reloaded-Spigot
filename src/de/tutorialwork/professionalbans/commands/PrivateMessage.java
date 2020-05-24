package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.MessagesManager;
import de.tutorialwork.professionalbans.utils.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class PrivateMessage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(!Main.ban.isMuted(p.getUniqueId().toString())){
                if(args.length > 1){
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target != null){
                        String message = "";
                        for(int i = 1; i < args.length; i++){
                            message = message + " " + args[i];
                        }
                        if(!Main.ban.isMuted(target.getUniqueId().toString())){
                            if(!MSGToggle.toggle.contains(target)){
                                MessagesManager.sendMessage(p, target, message);
                            } else {
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("msg_toggled"));
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("target_muted"));
                        }
                    } else {
                        if(MessagesManager.hasApp(UUIDFetcher.getUUID(args[0]))){
                            File file = new File(Main.main.getDataFolder(), "config.yml");
                            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                            String message = "";
                            for(int i = 1; i < args.length; i++){
                                message = message + " " + args[i];
                            }
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("CHATFORMAT.MSG").replace("%from%", Main.messages.getString("you")).replace("%message%", message)));
                            MessagesManager.insertMessage(p.getUniqueId().toString(), UUIDFetcher.getUUID(args[0]), message);
                            if(MessagesManager.getFirebaseToken(UUIDFetcher.getUUID(args[0])) != null){
                                MessagesManager.sendPushNotify(MessagesManager.getFirebaseToken(UUIDFetcher.getUUID(args[0])), Main.messages.getString("messages_from")+" "+p.getName(), message);
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                        }
                    }
                } else {
                    p.sendMessage(Main.data.Prefix+"/msg <"+Main.messages.getString("player")+"> <"+Main.messages.getString("message")+">");
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
