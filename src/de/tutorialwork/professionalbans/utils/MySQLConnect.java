package de.tutorialwork.professionalbans.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.tutorialwork.professionalbans.main.Main;
import org.bukkit.Bukkit;

public class MySQLConnect {

    public static String HOST;
    public static String DATABASE;
    public static String USER;
    public static String PASSWORD;
    public static Integer PORT;

    private Connection con;

    public MySQLConnect(String host, String database, String user, String password, Integer port) {
        this.HOST = host;
        this.DATABASE = database;
        this.USER = user;
        this.PASSWORD = password;
        this.PORT = port;

        connect();
    }

    public void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true", USER, PASSWORD);
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("mysql_success"));
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(Main.data.Prefix+Main.messages.getString("mysql_fail")+ e.getMessage());
        }
    }

    public void close() {
        try {
            if(con != null) {
                con.close();
            }
        } catch (SQLException e) {
        }
    }

    /*
    public void update(String qry) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate(qry);
            st.close();
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
    }

    public ResultSet query(String qry) {
        ResultSet rs = null;

        try {
            Statement st = con.createStatement();
            rs = st.executeQuery(qry);
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }
    */


    public Connection getCon(){
        return con;
    }
}