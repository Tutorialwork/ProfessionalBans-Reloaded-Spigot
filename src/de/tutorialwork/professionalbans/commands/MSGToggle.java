package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MSGToggle implements CommandExecutor {

    public static ArrayList<Player> toggle = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(toggle.contains(p)){
                toggle.remove(p);
                p.sendMessage(Main.data.Prefix+Main.messages.getString("msg_toggle_on"));
            } else {
                toggle.add(p);
                p.sendMessage(Main.data.Prefix+Main.messages.getString("msg_toggle_off"));
            }
        } else {
            System.out.println(Main.messages.getString("only_player_cmd"));
        }
        return false;
    }
}
