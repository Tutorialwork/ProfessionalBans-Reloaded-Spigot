package de.tutorialwork.professionalbans.main;

import de.tutorialwork.professionalbans.commands.*;
import de.tutorialwork.professionalbans.listener.Chat;
import de.tutorialwork.professionalbans.listener.Login;
import de.tutorialwork.professionalbans.listener.Quit;
import de.tutorialwork.professionalbans.utils.Placeholders;
import de.tutorialwork.professionalbans.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends JavaPlugin {

    public static Locale locale_en = new Locale("en");
    public static Locale locale_de = new Locale("de");
    public static ResourceBundle messages = ResourceBundle.getBundle("messages", locale_en);

    public static Main main;
    public static MySQLConnect mysql;
    public static BanManager ban;
    public static IPManager ip;
    public static Data data;

    //==============================================
    //Plugin Informationen
    public static final String Version = "3.0";
    //==============================================

    @Override
    public void onEnable() {
        init();
        data.sendConsoleStartupMessage();
        data.checkUpdateConsole();
        Metrics metrics = new Metrics(this, 7638);
    }

    private void init(){
        main = this;

        BanManager banManager = new BanManager();
        ban = banManager;

        IPManager ipManager = new IPManager();
        ip = ipManager;

        Data dataManager = new Data();
        data = dataManager;

        Config();
        MySQL();
        Commands();
        Listener();
        Schedulers();
        Placeholders();

        if(Language.getLanguage().equals("de")){
            Language.initLanguage(locale_de);
        }
    }

    private void Schedulers(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                data.webChecker();
            }
        }, 5 * 1000L, 5 * 1000L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                Report.players.clear();
            }
        }, data.ReportCooldown * 60 * 1000L, data.ReportCooldown * 60 * 1000L);
    }

    private void Config() {
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder().getPath(), "mysql.yml");
        File config = new File(getDataFolder().getPath(), "config.yml");
        File blacklistfile = new File(getDataFolder().getPath(), "blacklist.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            YamlConfiguration mysql = YamlConfiguration.loadConfiguration(file);
            mysql.addDefault("HOST", "localhost");
            mysql.addDefault("DATENBANK", "Bans");
            mysql.addDefault("USER", "root");
            mysql.addDefault("PASSWORT", "deinpasswort");
            mysql.addDefault("PORT", 3306);
            mysql.options().copyDefaults(true);
            try {
                mysql.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!config.exists()){
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);
            configcfg.options().header("  _____            __              _                   _ ____                  \n" +
                    " |  __ \\          / _|            (_)                 | |  _ \\                 \n" +
                    " | |__) | __ ___ | |_ ___  ___ ___ _  ___  _ __   __ _| | |_) | __ _ _ __  ___ \n" +
                    " |  ___/ '__/ _ \\|  _/ _ \\/ __/ __| |/ _ \\| '_ \\ / _` | |  _ < / _` | '_ \\/ __|\n" +
                    " | |   | | | (_) | ||  __/\\__ \\__ \\ | (_) | | | | (_| | | |_) | (_| | | | \\__ \\\n" +
                    " |_|   |_|  \\___/|_| \\___||___/___/_|\\___/|_| |_|\\__,_|_|____/ \\__,_|_| |_|___/\n" +
                    "                                                                               \n" +
                    "                                                                               ");
            configcfg.addDefault("WEBINTERFACE.URL", "https://bans.YourServer.com");
            configcfg.addDefault("Prefix", "&6&lP&e&lBANS &8• &7");
            configcfg.addDefault("CHATFORMAT.MSG", "&5&lMSG &8• &7%from% » &e%message%");
            configcfg.addDefault("CHATFORMAT.TEAMCHAT", "&e&lTEAM &8• &7%from% » &e%message%");
            configcfg.addDefault("CHATFORMAT.BROADCAST", "&8• &c&lBROADCAST &8• \n &8» &7%message%");
            configcfg.addDefault("LAYOUT.BAN", "&8[]===================================[] \n\n &4&lYou are BANNED \n\n &eReason: §c§l%grund% \n\n%ea-status% \n\n&8[]===================================[]");
            configcfg.addDefault("LAYOUT.KICK", "&8[]===================================[] \n\n &e&lYou are KICKED \n\n &eReason: §c§l%grund% \n\n&8[]===================================[]");
            configcfg.addDefault("LAYOUT.TEMPBAN", "&8[]===================================[] \n\n &4&lYou are temporarily BANNED \n\n &eGrund: §c§l%grund% \n &eTime remeaning: &c&l%dauer% \n\n%ea-status% \n\n&8[]===================================[]");
            configcfg.addDefault("LAYOUT.MUTE", "&8[]===================================[] \n\n &4&lYou are MUTED \n\n &eReason: §c§l%grund% \n\n&8[]===================================[]");
            configcfg.addDefault("LAYOUT.TEMPMUTE", "&8[]===================================[] \n\n &4&lYou are temporarily MUTED \n\n &eGrund: §c§l%grund% \n &eTime remeaning: &c&l%dauer% \n\n&8[]===================================[]");
            configcfg.addDefault("LAYOUT.IPBAN", "&8[]===================================[] \n\n &4&lYour IP was BANNED \n\n &eReason: §c§l%grund% \n\n&8[]===================================[]");
            configcfg.addDefault("LAYOUT.TEMPIPBAN", "&8[]===================================[] \n\n &4&lYour IP was temporarily BANNED \n\n &eReason: §c§l%grund% \n &eTime remeaning: &c&l%dauer% \n\n&8[]===================================[]");
            configcfg.addDefault("VPN.BLOCKED", true);
            configcfg.addDefault("VPN.KICK", true);
            configcfg.addDefault("VPN.KICKMSG", "&7Using a &4VPN &7is &cDISALLOWED");
            configcfg.addDefault("VPN.BAN", false);
            configcfg.addDefault("VPN.BANID", 0);
            configcfg.addDefault("VPN.WHITELIST", data.ipwhitelist);
            configcfg.addDefault("VPN.APIKEY", "Go to https://proxycheck.io/dashboard and register with your email and enter here your API Key");
            configcfg.addDefault("REPORTS.ENABLED", true);
            configcfg.addDefault("REPORTS.REASONS", data.reportreasons);
            configcfg.addDefault("REPORTS.OFFLINEREPORTS", false);
            configcfg.addDefault("REPORTS.COOLDOWN_MIN", 1);
            configcfg.addDefault("CHATLOG.ENABLED", true);
            configcfg.addDefault("AUTOMUTE.ENABLED", false);
            configcfg.addDefault("AUTOMUTE.AUTOREPORT", true);
            configcfg.addDefault("AUTOMUTE.MUTEID", 0);
            configcfg.addDefault("AUTOMUTE.ADMUTEID", 0);
            configcfg.addDefault("BANTIME-INCREASE.ENABLED", false);
            configcfg.addDefault("BANTIME-INCREASE.PERCENTRATE", 50);
            configcfg.addDefault("COMMANDS.MSG", true);
            configcfg.addDefault("COMMANDS.TEAMCHAT", true);
            configcfg.addDefault("COMMANDS.BROADCAST", true);
            configcfg.addDefault("COMMANDS.SUPPORT", true);
            configcfg.addDefault("ONLINETIME.BYPASSTEAM", false);
            configcfg.options().copyDefaults(true);
            try {
                configcfg.save(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            YamlConfiguration configcfg = YamlConfiguration.loadConfiguration(config);
            if(configcfg.getBoolean("VPN.KICK") && configcfg.getBoolean("VPN.BAN")){
                 Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                 Bukkit.getConsoleSender().sendMessage("§c§lSINNLOSE EINSTELLUNG ENTDECKT");
                 Bukkit.getConsoleSender().sendMessage("§7Wenn ein Spieler mit einer VPN das Netzwerk betritt kann er nicht gekickt UND gebannt werden.");
                 Bukkit.getConsoleSender().sendMessage("§4§lÜberprüfe die VPN Einstellung in der CONFIG.YML");
                 Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                //Setze VPN Einstellung zurück!
                configcfg.set("VPN.BLOCKED", true);
                configcfg.set("VPN.KICK", true);
                configcfg.set("VPN.KICKMSG", "&7Using a &4VPN &7is &cDISALLOWED");
                configcfg.set("VPN.BAN", false);
                configcfg.set("VPN.BANID", 0);
            }
            for(String reasons : configcfg.getStringList("REPORTS.REASONS")){
                data.reportreasons.add(reasons.toUpperCase());
            }
            for(String ips : configcfg.getStringList("VPN.WHITELIST")){
                data.ipwhitelist.add(ips);
            }
            data.Prefix = configcfg.getString("Prefix").replace("&", "§");
            data.increaseBans = configcfg.getBoolean("BANTIME-INCREASE.ENABLED");
            data.increaseValue = configcfg.getInt("BANTIME-INCREASE.PERCENTRATE");
            if(configcfg.getString("VPN.APIKEY").length() == 27){
                data.APIKey = configcfg.getString("VPN.APIKEY");
            }
            if(!configcfg.getString("WEBINTERFACE.URL").equals("https://bans.YourServer.com")){
                Main.data.WebURL = configcfg.getString("WEBINTERFACE.URL");
                if(!data.WebURL.startsWith("https://") && !data.WebURL.startsWith("http://")){
                    data.WebURL = "https://" + data.WebURL;
                }
                if(!data.WebURL.endsWith("/")){
                    data.WebURL = data.WebURL + "/";
                }
            } else {
                //==============================================
                //Warnung über fehlende Einstellung
                 Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                 Bukkit.getConsoleSender().sendMessage("§4§lAchtung!");
                 Bukkit.getConsoleSender().sendMessage("§cEs wurde festgestellt das du noch nicht die Webinterface URL in der §8§oconfig.yml §c§leingestellt hast.");
                 Bukkit.getConsoleSender().sendMessage("§7Folgende Features werden nicht planmäßig funktionieren");
                 Bukkit.getConsoleSender().sendMessage("§c§lChatlog-System");
                 Bukkit.getConsoleSender().sendMessage("§8[]===================================[]");
                //==============================================
            }
            if(configcfg.getInt("REPORTS.COOLDOWN_MIN") != 0){ //Is config file updated?
                data.ReportCooldown = configcfg.getInt("REPORTS.COOLDOWN_MIN");
            }
            try {
                configcfg.save(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!blacklistfile.exists()){
            try {
                blacklistfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            YamlConfiguration blacklistcfg = YamlConfiguration.loadConfiguration(blacklistfile);
            data.seedArrays();
            blacklistcfg.addDefault("ADWHITELIST", data.adwhitelist);
            blacklistcfg.addDefault("ADBLACKLIST", data.adblacklist);
            blacklistcfg.addDefault("BLACKLIST", data.blacklist);
            blacklistcfg.options().copyDefaults(true);
            try {
                blacklistcfg.save(blacklistfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            YamlConfiguration blacklistcfg = YamlConfiguration.loadConfiguration(blacklistfile);
            for(String congigstr : blacklistcfg.getStringList("BLACKLIST")){
                data.blacklist.add(congigstr);
            }
            for(String congigstr : blacklistcfg.getStringList("ADBLACKLIST")){
                data.adblacklist.add(congigstr);
            }
            for(String congigstr : blacklistcfg.getStringList("ADWHITELIST")){
                data.adwhitelist.add(congigstr.toUpperCase());
            }
        }
    }

    private void MySQL() {
        File file = new File(getDataFolder().getPath(), "mysql.yml");
        YamlConfiguration mysqlcfg = YamlConfiguration.loadConfiguration(file);
        MySQLConnect.HOST = mysqlcfg.getString("HOST");
        MySQLConnect.DATABASE = mysqlcfg.getString("DATENBANK");
        MySQLConnect.USER = mysqlcfg.getString("USER");
        MySQLConnect.PASSWORD = mysqlcfg.getString("PASSWORT");
        if(mysqlcfg.getInt("PORT") != 0){
            MySQLConnect.PORT = mysqlcfg.getInt("PORT");
        } else {
            MySQLConnect.PORT = 3306;
        }
        mysql = new MySQLConnect(MySQLConnect.HOST, MySQLConnect.DATABASE, MySQLConnect.USER, MySQLConnect.PASSWORD, MySQLConnect.PORT);
        data.seedDatabase();
    }

    private void Commands() {
        File file = new File(getDataFolder().getPath(), "config.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        getCommand("ban").setExecutor(new Ban());
        getCommand("unban").setExecutor(new Unban());
        getCommand("kick").setExecutor(new Kick());
        getCommand("check").setExecutor(new Check());
        getCommand("professionalbans").setExecutor(new ProfessionalBans());
        getCommand("ipban").setExecutor(new IPBan());
        if(cfg.getBoolean("REPORTS.ENABLED")){
            getCommand("report").setExecutor(new Report());
            getCommand("reports").setExecutor(new Reports());
        }
        if(cfg.getBoolean("CHATLOG.ENABLED")){
            getCommand("chatlog").setExecutor(new Chatlog());
        }
        getCommand("blacklist").setExecutor(new Blacklist());
        getCommand("webverify").setExecutor(new WebVerify());
        if(cfg.getBoolean("COMMANDS.SUPPORT")){
            getCommand("support").setExecutor(new SupportChat());
        }
        if(cfg.getBoolean("COMMANDS.MSG")){
            getCommand("msg").setExecutor(new PrivateMessage());
            getCommand("r").setExecutor(new PrivateMessageReply());
            getCommand("msgtoggle").setExecutor(new MSGToggle());
        }
        if(cfg.getBoolean("COMMANDS.TEAMCHAT")){
            getCommand("tc").setExecutor(new TeamChat());
        }
        if(cfg.getBoolean("COMMANDS.BROADCAST")){
            getCommand("bc").setExecutor(new Broadcast());
        }
        getCommand("history").setExecutor(new PlayerHistory());
        getCommand("onlinezeit").setExecutor(new Onlinezeit());
        getCommand("language").setExecutor(new Language());
    }

    private void Listener() {
        Bukkit.getPluginManager().registerEvents(new Login(), this);
        Bukkit.getPluginManager().registerEvents(new Chat(), this);
        Bukkit.getPluginManager().registerEvents(new Quit(), this);
    }

    private void Placeholders() {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            Bukkit.getConsoleSender().sendMessage(data.Prefix + "§aPlaceholderAPI found!");
            new Placeholders(this).register();
        }
    }

    public static Main getInstance(){
        return main;
    }

    public static String callURL(String myURL) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:"+ myURL, e);
        }

        return sb.toString();
    }
}
