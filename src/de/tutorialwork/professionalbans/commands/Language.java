package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Language implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("professionalbans.*")){
                if(args.length == 0){
                    p.sendMessage(Main.data.Prefix+"/language <name>");
                } else {
                    setLanguage(args[0]);
                    switch (args[0]){
                        case "en":
                            p.sendMessage(Main.data.Prefix+"The language was set to §e§lEnglish");
                            Language.initLanguage(Main.locale_en);
                            break;
                        case "de":
                            p.sendMessage(Main.data.Prefix+"Die Sprache wurde zu §e§lDeutsch §7gesetzt");
                            Language.initLanguage(Main.locale_de);
                            break;
                    }
                }
            } else {
                p.sendMessage(Main.data.NoPerms);
            }
        }
        return false;
    }

    public static boolean isLanguageSet(){
        File file = new File(Main.getInstance().getDataFolder().getPath(), "config.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        if(cfg.getString("LANGUAGE") == null){
            return false;
        }
        if(cfg.getString("LANGUAGE").equalsIgnoreCase("de") || cfg.getString("LANGUAGE").equalsIgnoreCase("en")){
            return true;
        } else {
            return false;
        }
    }

    public static void setLanguage(String lang){
        File file = new File(Main.getInstance().getDataFolder().getPath(), "config.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        cfg.set("LANGUAGE", lang);

        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLanguage(){
        File file = new File(Main.getInstance().getDataFolder().getPath(), "config.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        return (cfg.getString("LANGUAGE") != null) ? cfg.getString("LANGUAGE") : "randomValue";
    }

    public static void initLanguage(Locale locale){
        Main.messages = ResourceBundle.getBundle("messages", locale);
    }
}
