package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WebVerify implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length == 0){
                p.sendMessage(Main.data.Prefix+"/webverify <Token>");
            } else {
                String UUID = p.getUniqueId().toString();
                if(Main.ban.webaccountExists(UUID)){
                    if(Main.ban.hasAuthToken(UUID)){
                        if(args[0].length() == 25){
                            if(Main.ban.getAuthCode(UUID).equals(args[0])){
                                Main.ban.updateAuthStatus(UUID);
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("webverify_success"));
                            } else {
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("token_invalid"));
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("token_invalid"));
                        }
                    } else {
                        p.sendMessage(Main.data.Prefix+"§cEs wurde keine Verifizierungsanfrage von dir gefunden");
                    }
                } else {
                    p.sendMessage(Main.data.Prefix+"§cDu hast keinen Account im Webinterface");
                }
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}
