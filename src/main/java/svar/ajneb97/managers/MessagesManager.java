package svar.ajneb97.managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesManager {

    private String prefix;
    public MessagesManager(String prefix) {
        this.prefix = prefix;
    }

    public void sendMessage(CommandSender sender, String message, boolean prefix) {
        if(!message.isEmpty()) {
            if(prefix) {
                sender.sendMessage(getColoredMessage(this.prefix+message));
            }else {
                sender.sendMessage(getColoredMessage(message));
            }
        }
    }

    public static String getColoredMessage(String text) {
        if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")
                || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher match = pattern.matcher(text);

            while(match.find()) {
                String color = text.substring(match.start(),match.end());
                text = text.replace(color, ChatColor.of(color)+"");

                match = pattern.matcher(text);
            }
        }

        text = ChatColor.translateAlternateColorCodes('&', text);
        return text;
    }
}
