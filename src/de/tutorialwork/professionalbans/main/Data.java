package de.tutorialwork.professionalbans.main;

import de.tutorialwork.professionalbans.utils.MessagesManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Data {

    public static String Prefix = "§6§lP§e§lBANS §8• §7";
    public static String NoPerms = Prefix + Main.messages.getString("no_perms");

    public static ArrayList<String> reportreasons = new ArrayList<>();
    public static ArrayList<String> blacklist = new ArrayList<>();
    public static ArrayList<String> adblacklist = new ArrayList<>();
    public static ArrayList<String> adwhitelist = new ArrayList<>();
    public static ArrayList<String> ipwhitelist = new ArrayList<>();

    public static boolean increaseBans = true;
    public static Integer increaseValue = 50;

    public static String APIKey = null;
    public static String WebURL = null;
    public static Integer ReportCooldown = 1;

    public void webChecker(){
        File config = new File(Main.main.getDataFolder(), "config.yml");
        YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);
        for(Player all : Bukkit.getOnlinePlayers()){
            Bukkit.getScheduler().runTaskAsynchronously(Main.main, () -> {
                if(Main.ban.isBanned(all.getUniqueId().toString())){
                    if(Main.ban.getRAWEnd(all.getUniqueId().toString()) == -1L){
                        all.kickPlayer(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.BAN").replace("%grund%", Main.ban.getReasonString(all.getUniqueId().toString()))));
                    } else {
                        String MSG = configcfg.getString("LAYOUT.TEMPBAN");
                        MSG = MSG.replace("%grund%", Main.ban.getReasonString(all.getUniqueId().toString()));
                        MSG = MSG.replace("%dauer%", Main.ban.getEnd(all.getUniqueId().toString()));
                        MSG = MSG.replace("%ea-status%", Main.ban.getEAStatus(all.getUniqueId().toString()));
                        all.kickPlayer(ChatColor.translateAlternateColorCodes('&', MSG));
                    }
                }
                MessagesManager.sendOpenMessages();
                MessagesManager.sendOpenBroadcasts();
            });
        }
    }

    public void checkUpdateConsole(){
        if(!Main.callURL("https://api.spigotmc.org/legacy/update.php?resource=63657").equals(Main.Version)){
            Main.data.sendConsoleUpdateMessage();
        }
    }

    public void seedDatabase(){
        try{
            PreparedStatement ps_acc = Main.mysql.getCon()
                    .prepareStatement(
                            "CREATE TABLE IF NOT EXISTS user(id INT AUTO_INCREMENT NOT NULL, uuid VARCHAR(255) NOT NULL, username VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, auth TINYINT(1) NOT NULL, authcode VARCHAR(255) DEFAULT NULL, roles JSON NOT NULL, PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB"
                    );
            ps_acc.executeUpdate();
            ps_acc.close();

            PreparedStatement ps_reasons = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS reasons(ID int(11) UNIQUE AUTO_INCREMENT, REASON varchar(255), TIME int(255), TYPE int(11), ADDED_AT datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(), BANS int(11), PERMS varchar(255));"
                    );
            ps_reasons.executeUpdate();
            ps_reasons.close();

            PreparedStatement ps_bans = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS bans(UUID varchar(64) UNIQUE, NAME varchar(64), BANNED int(11), MUTED int(11), REASON varchar(64), END long, TEAMUUID varchar(64), BANS int(11), MUTES int(11), FIRSTLOGIN varchar(255), LASTLOGIN varchar(255), ONLINE_STATUS int(11) NULL DEFAULT '0', ONLINE_TIME BIGINT(19) NULL DEFAULT '0');"
                    );
            ps_bans.executeUpdate();
            ps_bans.close();
            PreparedStatement ps_ip = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS ips(IP varchar(64) UNIQUE, USED_BY varchar(64), USED_AT varchar(64), BANNED int(11), REASON varchar(64), END long, TEAMUUID varchar(64), BANS int(11));"
                    );
            ps_ip.executeUpdate();
            ps_ip.close();
            PreparedStatement ps_report = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS reports(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(64), REPORTER varchar(64), TEAM varchar(64), REASON varchar(64), LOG varchar(64), STATUS int(11), CREATED_AT long);"
                    );
            ps_report.executeUpdate();
            ps_report.close();
            PreparedStatement ps_chat = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS chat(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(64), SERVER varchar(64), MESSAGE varchar(2500), SENDDATE varchar(255));"
                    );
            ps_chat.executeUpdate();
            ps_chat.close();
            PreparedStatement ps_chatlog = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS chatlog(ID int(11) AUTO_INCREMENT UNIQUE, LOGID varchar(255), UUID varchar(64), CREATOR_UUID varchar(64), SERVER varchar(64), MESSAGE varchar(2500), SENDDATE varchar(255), CREATED_AT varchar(255));"
                    );
            ps_chatlog.executeUpdate();
            ps_chatlog.close();
            PreparedStatement ps_log = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS log(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(255), BYUUID varchar(255), ACTION varchar(255), NOTE varchar(255), DATE varchar(255));"
                    );
            ps_log.executeUpdate();
            ps_log.close();
            PreparedStatement ps_unban = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS unbans(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(255), FAIR int(11), MESSAGE varchar(10000), DATE varchar(255), STATUS int(11));"
                    );
            ps_unban.executeUpdate();
            ps_unban.close();
            PreparedStatement ps_privatemsg = Main.mysql.getCon()
                    .prepareStatement(
                                    "CREATE TABLE IF NOT EXISTS privatemessages(ID int(11) AUTO_INCREMENT UNIQUE, SENDER varchar(255), RECEIVER varchar(255), MESSAGE varchar(2500), STATUS int(11), DATE varchar(255));"
                    );
            ps_privatemsg.executeUpdate();
            ps_privatemsg.close();
            PreparedStatement ps_tokens = Main.mysql.getCon()
                    .prepareStatement(
                            "CREATE TABLE IF NOT EXISTS tokens(id INT AUTO_INCREMENT NOT NULL, uuid VARCHAR(255) NOT NULL, token VARCHAR(255) NOT NULL, token_description VARCHAR(255) DEFAULT NULL, firebase_token VARCHAR(255) DEFAULT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB"
                    );
            ps_tokens.executeUpdate();
            ps_tokens.close();
            PreparedStatement ps_setting = Main.mysql.getCon()
                    .prepareStatement(
                            "CREATE TABLE IF NOT EXISTS setting(id INT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, value VARCHAR(1000) DEFAULT NULL, UNIQUE INDEX UNIQ_9F74B8985E237E06 (name), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB"
                    );
            ps_setting.executeUpdate();
            ps_setting.close();
            PreparedStatement ps_invite = Main.mysql.getCon()
                    .prepareStatement(
                            "CREATE TABLE IF NOT EXISTS invite(id INT AUTO_INCREMENT NOT NULL, code VARCHAR(255) NOT NULL, creator INT NOT NULL, creationdate DATE NOT NULL, PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB"
                    );
            ps_invite.executeUpdate();
            ps_invite.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean checkMySQLVersion() {
        try {
            PreparedStatement stmt = Main.mysql.getCon().prepareStatement("SELECT VERSION();");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String RAWVersion = rs.getString("VERSION()");
                if(RAWVersion.contains("MariaDB")){
                    RAWVersion = RAWVersion.split("-")[0];
                    RAWVersion = RAWVersion.replace(".", "");
                    RAWVersion = RAWVersion.substring(0, 3);
                    if(Integer.parseInt(RAWVersion) >= 103){
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public void sendConsoleStartupMessage(){
        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
        Bukkit.getConsoleSender().sendMessage("§e§lProfessionalBans §7§oReloaded §8| §7Version: §c"+Main.Version);
        Bukkit.getConsoleSender().sendMessage("§7Developer: §e§lTutorialwork");
        Bukkit.getConsoleSender().sendMessage("§5YT §7"+Main.messages.getString("channel")+": §cyoutube.com/Tutorialwork");
        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
    }

    public void sendConsoleUpdateMessage(){
        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
        Bukkit.getConsoleSender().sendMessage("§e§lProfessionalBans §7Reloaded §8| §7Version §c"+Main.Version);
        Bukkit.getConsoleSender().sendMessage(Main.messages.getString("update"));
        Bukkit.getConsoleSender().sendMessage("§7Update: §4§lhttps://spigotmc.org/resources/63657");
        Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
    }

    /*
    Seed config default arrays
     */
    public void seedArrays(){
        ipwhitelist.add("8.8.8.8");

        reportreasons.add("Hacking");
        reportreasons.add("Behavior");
        reportreasons.add("Teaming");
        reportreasons.add("TPA-Trap");
        reportreasons.add("Ads");

        adwhitelist.add("DeinServer.net");
        adwhitelist.add("forum.DeinServer.net");
        adwhitelist.add("ts.DeinServer.net");

        adblacklist.add(".com");
        adblacklist.add(".org");
        adblacklist.add(".net");
        adblacklist.add(".us");
        adblacklist.add(".co");
        adblacklist.add(".de");
        adblacklist.add(".biz");
        adblacklist.add(".info");
        adblacklist.add(".name");
        adblacklist.add(".yt");
        adblacklist.add(".tv");
        adblacklist.add(".xyz");
        adblacklist.add(".fr");
        adblacklist.add(".ch");
        adblacklist.add(".au");
        adblacklist.add(".at");
        adblacklist.add(".in");
        adblacklist.add(".jp");
        adblacklist.add(".nl");
        adblacklist.add(".uk");
        adblacklist.add(".no");
        adblacklist.add(".ru");
        adblacklist.add(".br");
        adblacklist.add(".tk");
        adblacklist.add(".ml");
        adblacklist.add(".ga");
        adblacklist.add(".cf");
        adblacklist.add(".gq");
        adblacklist.add(".ip");
        adblacklist.add(".dee");
        adblacklist.add(".d e");
        adblacklist.add("[punkt]");
        adblacklist.add("(punkt)");
        adblacklist.add("join now");
        adblacklist.add("join");
        adblacklist.add("mein server");
        adblacklist.add("mein netzwerk");
        adblacklist.add("www");
        adblacklist.add("[.]");
        adblacklist.add("(,)");
        adblacklist.add("(.)");

        blacklist.add("anal");
        blacklist.add("anus");
        blacklist.add("b1tch");
        blacklist.add("bang");
        blacklist.add("banger");
        blacklist.add("bastard");
        blacklist.add("biatch");
        blacklist.add("bitch");
        blacklist.add("bitches");
        blacklist.add("blow job");
        blacklist.add("blow");
        blacklist.add("blowjob");
        blacklist.add("boob");
        blacklist.add("boobs");
        blacklist.add("bullshit");
        blacklist.add("bull shit");
        blacklist.add("c0ck");
        blacklist.add("cock");
        blacklist.add("d1ck");
        blacklist.add("d1ld0");
        blacklist.add("d1ldo");
        blacklist.add("dick");
        blacklist.add("doggie-style");
        blacklist.add("doggy-style");
        blacklist.add("f.u.c.k");
        blacklist.add("fack");
        blacklist.add("faggit");
        blacklist.add("faggot");
        blacklist.add("fagot");
        blacklist.add("fuck");
        blacklist.add("f-u-c-k");
        blacklist.add("ficken");
        blacklist.add("fick");
        blacklist.add("fuckoff");
        blacklist.add("fucks");
        blacklist.add("fuk");
        blacklist.add("fvck");
        blacklist.add("fxck");
        blacklist.add("gai");
        blacklist.add("gay");
        blacklist.add("schwul");
        blacklist.add("schwuchtel");
        blacklist.add("h0m0");
        blacklist.add("h0mo");
        blacklist.add("hitler");
        blacklist.add("homo");
        blacklist.add("lesbe");
        blacklist.add("nigga");
        blacklist.add("niggah");
        blacklist.add("nigger");
        blacklist.add("nippel");
        blacklist.add("pedo");
        blacklist.add("pedo");
        blacklist.add("penis");
        blacklist.add("porn");
        blacklist.add("porno");
        blacklist.add("pornografie");
        blacklist.add("sex");
        blacklist.add("sh1t");
        blacklist.add("s-h-1-t");
        blacklist.add("shit");
        blacklist.add("s-h-i-t");
        blacklist.add("scheiße");
        blacklist.add("scheisse");
        blacklist.add("xxx");
        blacklist.add("Fotze");
        blacklist.add("Hackfresse");
        blacklist.add("Hurensohn");
        blacklist.add("Huso");
        blacklist.add("Hure");
        blacklist.add("hirnamputiert");
        blacklist.add("Honk");
        blacklist.add("kek");
        blacklist.add("Loser");
        blacklist.add("Mongo");
        blacklist.add("Pimmel");
        blacklist.add("Pimmelfresse");
        blacklist.add("Schlampe");
        blacklist.add("Spastard");
        blacklist.add("abspritzer");
        blacklist.add("afterlecker");
        blacklist.add("arschficker");
        blacklist.add("arschgeburt");
        blacklist.add("arschgeige");
        blacklist.add("arschgesicht");
        blacklist.add("arschlecker");
        blacklist.add("arschloch");
        blacklist.add("arschlöcher");
        blacklist.add("assi");
        blacklist.add("beklopter");
        blacklist.add("bummsen");
        blacklist.add("bumsen");
        blacklist.add("drecksack");
        blacklist.add("drecksau");
        blacklist.add("drecksfotze");
        blacklist.add("drecksnigger");
        blacklist.add("drecksnutte");
        blacklist.add("dreckspack");
        blacklist.add("dreckvotze");
        blacklist.add("fagette");
        blacklist.add("fagitt");
        blacklist.add("ficker");
        blacklist.add("fickfehler");
        blacklist.add("fickfresse");
        blacklist.add("fickgesicht");
        blacklist.add("ficknudel");
        blacklist.add("ficksau");
        blacklist.add("hackfresse");
        blacklist.add("lusche");
        blacklist.add("heil");
        blacklist.add("missgeburt");
        blacklist.add("mißgeburt");
        blacklist.add("miststück");
        blacklist.add("nazi");
        blacklist.add("nazis");
        blacklist.add("penner");
        blacklist.add("scheisser");
        blacklist.add("sieg heil");
        blacklist.add("vollidiot");
        blacklist.add("volldepp");
        blacklist.add("wanker");
        blacklist.add("wichser");
        blacklist.add("wichsvorlage");
        blacklist.add("wixa");
        blacklist.add("wixen");
        blacklist.add("wixer");
        blacklist.add("wixxer");
        blacklist.add("wixxxer");
        blacklist.add("wixxxxer");
    }
    
}
