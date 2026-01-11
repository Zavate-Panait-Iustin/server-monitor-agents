package util;

public class MessageUtils {

    // Creează mesaj pentru Logger (Server|CPU|RAM|Ping)
    public static String buildMetricsMessage(String serverName, double cpu, double ram, int ping) {
        return String.format("%s|%.1f|%.1f|%d", serverName, cpu, ram, ping);
    }

    // Parcurge mesajul primit și returnează array de string-uri
    public static String[] parseMetricsMessage(String message) {
        // Așteptăm formatul exact "Server|CPU|RAM|Ping"
        return message.split("\\|");
    }
}
