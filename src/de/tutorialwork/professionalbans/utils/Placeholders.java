package de.tutorialwork.professionalbans.utils;

import de.tutorialwork.professionalbans.commands.Language;
import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.TimeManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Placeholders extends PlaceholderExpansion {
    private Plugin plugin;

    public Placeholders(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "professionalbans";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier){

        if(p == null){
            return "";
        }

        switch (identifier){
            case "onlinetime":
                return TimeManager.formatOnlineTime(TimeManager.getOnlineTime(p.getUniqueId().toString()) / 60 / 1000);
            case "firstjoin":
                SimpleDateFormat sdf = new SimpleDateFormat(Language.getLanguage().equals("de") ? "dd.MM.yyyy HH:mm" : "MM/dd/yyyy HH:mm");
                Timestamp firstjoin = new Timestamp(Long.valueOf(Main.ban.getFirstLogin(p.getUniqueId().toString())));
                return sdf.format(firstjoin);
            case "reports":
                return Main.ban.countOpenReports().toString();
        }

        return null;
    }
}
