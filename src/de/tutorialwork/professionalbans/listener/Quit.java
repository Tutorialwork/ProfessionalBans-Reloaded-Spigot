package de.tutorialwork.professionalbans.listener;

import de.tutorialwork.professionalbans.commands.SupportChat;
import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    @EventHandler
    public static void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(SupportChat.activechats.containsKey(p) || SupportChat.activechats.containsValue(p)){
            for(Player key : SupportChat.activechats.keySet()){
                //Key has started the support chat
                if(key == p){
                    SupportChat.activechats.get(p).sendMessage(Main.data.Prefix+"§e§l"+p.getName()+" "+Main.messages.getString("supportchat_end"));
                    SupportChat.activechats.remove(p);
                } else {
                    key.sendMessage(Main.data.Prefix+"§e§l"+p.getName()+" "+Main.messages.getString("supportchat_end"));
                    SupportChat.activechats.remove(key);
                }
            }
        }
        TimeManager.updateOnlineStatus(p.getUniqueId().toString(), 0);
        try{
            long onlinetime = TimeManager.getOnlineTime(p.getUniqueId().toString());
            long logintime = Login.logintimes.get(p);
            long currentonlinetime = System.currentTimeMillis() - logintime;
            TimeManager.setOnlineTime(p.getUniqueId().toString(), onlinetime + currentonlinetime);
        } catch (NullPointerException e1){
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+"§c§lFEHLER: §cOnlinezeit konnte für "+p.getName()+" nicht aktualisiert werden");
        }
    }

}
