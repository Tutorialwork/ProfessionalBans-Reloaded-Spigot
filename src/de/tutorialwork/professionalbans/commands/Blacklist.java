package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Blacklist implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("professionalbans.blacklist") || p.hasPermission("professionalbans.*")){
                if(args.length == 0 || args.length == 1){
                    p.sendMessage(Main.data.Prefix+Main.messages.getString("wordblacklist").replace("%count%", Main.data.blacklist.size()+""));
                    p.sendMessage(Main.data.Prefix+"/blacklist <add/del> <"+Main.messages.getString("word")+">");
                } else if(args.length == 2){
                    File blacklist = new File(Main.main.getDataFolder(), "blacklist.yml");
                    YamlConfiguration cfg = YamlConfiguration.loadConfiguration(blacklist);
                    ArrayList<String> tempblacklist = new ArrayList<>();

                    if(args[0].equalsIgnoreCase("add")){
                        String Wort = args[1];
                        for(String congigstr : cfg.getStringList("BLACKLIST")){
                            tempblacklist.add(congigstr);
                        }
                        tempblacklist.add(Wort);
                        Main.data.blacklist.add(Wort);
                        cfg.set("BLACKLIST", tempblacklist);
                        p.sendMessage(Main.data.Prefix+"§e§l"+Wort+" "+Main.messages.getString("wordblacklist_add"));
                        LogManager.createEntry(null, p.getUniqueId().toString(), "ADD_WORD_BLACKLIST", Wort);
                    } else if(args[0].equalsIgnoreCase("del")){
                        String Wort = args[1];
                        if(Main.data.blacklist.contains(Wort)){
                            for(String congigstr : cfg.getStringList("BLACKLIST")){
                                tempblacklist.add(congigstr);
                            }
                            tempblacklist.remove(Wort);
                            Main.data.blacklist.remove(Wort);
                            cfg.set("BLACKLIST", tempblacklist);
                            p.sendMessage(Main.data.Prefix+"§e§l"+Wort+" "+Main.messages.getString("wordblacklist_del"));
                            LogManager.createEntry(null, p.getUniqueId().toString(), "DEL_WORD_BLACKLIST", Wort);
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("word_404"));
                        }
                    } else {
                        p.sendMessage(Main.data.Prefix+Main.messages.getString("wordblacklist").replace("%count%", Main.data.blacklist.size()+""));
                        p.sendMessage(Main.data.Prefix+"/blacklist <add/del> <"+Main.messages.getString("word")+">");
                    }

                    try {
                        cfg.save(blacklist);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tempblacklist.clear();
                } else {
                    p.sendMessage(Main.data.Prefix+Main.messages.getString("wordblacklist").replace("%count%", Main.data.blacklist.size()+""));
                    p.sendMessage(Main.data.Prefix+"/blacklist <add/del> <"+Main.messages.getString("word")+">");
                }
            } else {
                p.sendMessage(Main.data.NoPerms);
            }
        } else {
            //KONSOLE
            if(args.length == 0 || args.length == 1){
                Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("wordblacklist").replace("%count%", Main.data.blacklist.size()+""));
                Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"/blacklist <add/del> <"+Main.messages.getString("word")+">");
            } else if(args.length == 2){
                File blacklist = new File(Main.main.getDataFolder(), "blacklist.yml");
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(blacklist);
                ArrayList<String> tempblacklist = new ArrayList<>();

                if(args[0].equalsIgnoreCase("add")){
                    String Wort = args[1];
                    for(String congigstr : cfg.getStringList("BLACKLIST")){
                        tempblacklist.add(congigstr);
                    }
                    tempblacklist.add(Wort);
                    Main.data.blacklist.add(Wort);
                    cfg.set("BLACKLIST", tempblacklist);
                    Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"§e§l"+Wort+" "+Main.messages.getString("wordblacklist_add"));
                    LogManager.createEntry(null, "KONSOLE", "ADD_WORD_BLACKLIST", Wort);
                } else if(args[0].equalsIgnoreCase("del")){
                    String Wort = args[1];
                    if(Main.data.blacklist.contains(Wort)){
                        for(String congigstr : cfg.getStringList("BLACKLIST")){
                            tempblacklist.add(congigstr);
                        }
                        tempblacklist.remove(Wort);
                        Main.data.blacklist.remove(Wort);
                        cfg.set("BLACKLIST", tempblacklist);
                        Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"§e§l"+Wort+" "+Main.messages.getString("wordblacklist_del"));
                        LogManager.createEntry(null, "KONSOLE", "DEL_WORD_BLACKLIST", Wort);
                    } else {
                        Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("word_404"));
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("wordblacklist").replace("%count%", Main.data.blacklist.size()+""));
                    Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"/blacklist <add/del> <"+Main.messages.getString("word")+">");
                }

                try {
                    cfg.save(blacklist);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tempblacklist.clear();
            } else {
                Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("wordblacklist").replace("%count%", Main.data.blacklist.size()+""));
                Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"/blacklist <add/del> <"+Main.messages.getString("word")+">");
            }
        }
        return false;
    }
}
