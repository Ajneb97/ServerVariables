package svar.ajneb97.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.MessagesManager;

public class PlayerListener implements Listener {

    private ServerVariables plugin;
    public PlayerListener(ServerVariables plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreJoinMonitor(AsyncPlayerPreLoginEvent event){
        //Create or update player data
        plugin.getPlayerVariablesManager().manageJoin(event);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        //Update notification
        String latestVersion = plugin.getUpdateCheckerManager().getLatestVersion();
        if(player.isOp() && plugin.getConfigsManager().getMainConfigManager().isUpdateNotify() && !plugin.version.equals(latestVersion)){
            player.sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+" &cThere is a new version available. &e(&7"+latestVersion+"&e)"));
            player.sendMessage(MessagesManager.getLegacyColoredMessage("&cYou can download it at: &ahttps://modrinth.com/plugin/servervariables"));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        plugin.getPlayerVariablesManager().manageLeave(event.getPlayer());
    }
}
