package com.shnok.javaserver.util;


import java.util.HashMap;
import java.util.Map;

public class ServerNameDAO {
    private static final Map<Integer, String> SERVERS = new HashMap<>();

    static {
        SERVERS.put(Integer.valueOf(1), "Bartz");
        SERVERS.put(Integer.valueOf(2), "Sieghardt");
        SERVERS.put(Integer.valueOf(3), "Kain");
        SERVERS.put(Integer.valueOf(4), "Lionna");
        SERVERS.put(Integer.valueOf(5), "Erica");
        SERVERS.put(Integer.valueOf(6), "Gustin");
        SERVERS.put(Integer.valueOf(7), "Devianne");
        SERVERS.put(Integer.valueOf(8), "Hindemith");
        SERVERS.put(Integer.valueOf(9), "Teon (EURO)");
        SERVERS.put(Integer.valueOf(10), "Franz (EURO)");
        SERVERS.put(Integer.valueOf(11), "Luna (EURO)");
        SERVERS.put(Integer.valueOf(12), "Sayha");
        SERVERS.put(Integer.valueOf(13), "Aria");
        SERVERS.put(Integer.valueOf(14), "Phoenix");
        SERVERS.put(Integer.valueOf(15), "Chronos");
        SERVERS.put(Integer.valueOf(16), "Naia (EURO)");
        SERVERS.put(Integer.valueOf(17), "Elhwynna");
        SERVERS.put(Integer.valueOf(18), "Ellikia");
        SERVERS.put(Integer.valueOf(19), "Shikken");
        SERVERS.put(Integer.valueOf(20), "Scryde");
        SERVERS.put(Integer.valueOf(21), "Frikios");
        SERVERS.put(Integer.valueOf(22), "Ophylia");
        SERVERS.put(Integer.valueOf(23), "Shakdun");
        SERVERS.put(Integer.valueOf(24), "Tarziph");
        SERVERS.put(Integer.valueOf(25), "Aria");
        SERVERS.put(Integer.valueOf(26), "Esenn");
        SERVERS.put(Integer.valueOf(27), "Elcardia");
        SERVERS.put(Integer.valueOf(28), "Yiana");
        SERVERS.put(Integer.valueOf(29), "Seresin");
        SERVERS.put(Integer.valueOf(30), "Tarkai");
        SERVERS.put(Integer.valueOf(31), "Khadia");
        SERVERS.put(Integer.valueOf(32), "Roien");
        SERVERS.put(Integer.valueOf(33), "Kallint (Non-PvP)");
        SERVERS.put(Integer.valueOf(34), "Baium");
        SERVERS.put(Integer.valueOf(35), "Kamael");
        SERVERS.put(Integer.valueOf(36), "Beleth");
        SERVERS.put(Integer.valueOf(37), "Anakim");
        SERVERS.put(Integer.valueOf(38), "Lilith");
        SERVERS.put(Integer.valueOf(39), "Thifiel");
        SERVERS.put(Integer.valueOf(40), "Lithra");
        SERVERS.put(Integer.valueOf(41), "Lockirin");
        SERVERS.put(Integer.valueOf(42), "Kakai");
        SERVERS.put(Integer.valueOf(43), "Cadmus");
        SERVERS.put(Integer.valueOf(44), "Athebaldt");
        SERVERS.put(Integer.valueOf(45), "Blackbird");
        SERVERS.put(Integer.valueOf(46), "Ramsheart");
        SERVERS.put(Integer.valueOf(47), "Esthus");
        SERVERS.put(Integer.valueOf(48), "Vasper");
        SERVERS.put(Integer.valueOf(49), "Lancer");
        SERVERS.put(Integer.valueOf(50), "Ashton");
        SERVERS.put(Integer.valueOf(51), "Waytrel");
        SERVERS.put(Integer.valueOf(52), "Waltner");
        SERVERS.put(Integer.valueOf(53), "Tahnford");
        SERVERS.put(Integer.valueOf(54), "Hunter");
        SERVERS.put(Integer.valueOf(55), "Dewell");
        SERVERS.put(Integer.valueOf(56), "Rodemaye");
        SERVERS.put(Integer.valueOf(57), "Ken Rauhel");
        SERVERS.put(Integer.valueOf(58), "Ken Abigail");
        SERVERS.put(Integer.valueOf(59), "Ken Orwen");
        SERVERS.put(Integer.valueOf(60), "Van Holter");
        SERVERS.put(Integer.valueOf(61), "Desperion");
        SERVERS.put(Integer.valueOf(62), "Einhovant");
        SERVERS.put(Integer.valueOf(63), "Shunaiman");
        SERVERS.put(Integer.valueOf(64), "Faris");
        SERVERS.put(Integer.valueOf(65), "Tor");
        SERVERS.put(Integer.valueOf(66), "Carneiar");
        SERVERS.put(Integer.valueOf(67), "Dwyllios");
        SERVERS.put(Integer.valueOf(68), "Baium");
        SERVERS.put(Integer.valueOf(69), "Hallate");
        SERVERS.put(Integer.valueOf(70), "Zaken");
        SERVERS.put(Integer.valueOf(71), "Core");
    }

    public static String getServer(int id) {
        return SERVERS.getOrDefault(Integer.valueOf(id), "Undefined");
    }

    public static Map<Integer, String> getServers() {
        return SERVERS;
    }
}