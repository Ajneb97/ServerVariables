package svar.ajneb97.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.managers.PlayerVariablesManager;
import svar.ajneb97.model.ServerVariablesPlayer;
import svar.ajneb97.model.ServerVariablesVariable;

import java.util.ArrayList;

public class PlayerListener implements Listener {

    private ServerVariables plugin;
    public PlayerListener(ServerVariables plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        //Create or update player data
        PlayerVariablesManager playerVariablesManager = plugin.getPlayerVariablesManager();
        ServerVariablesPlayer p = playerVariablesManager.getPlayerByUUID(player.getUniqueId().toString());
        if(p != null){
            //Update name
            p.setName(player.getName());
        }else{
            //Create empty data for player
            playerVariablesManager.addPlayer(new ServerVariablesPlayer(player.getUniqueId().toString(),player.getName(),
                                                        new ArrayList<ServerVariablesVariable>()));
        }

        //Update notification
        String latestVersion = plugin.getUpdateCheckerManager().getLatestVersion();
        if(player.isOp() && !plugin.version.equals(latestVersion)){
            player.sendMessage(MessagesManager.getColoredMessage(plugin.prefix+" &cThere is a new version available. &e(&7"+latestVersion+"&e)"));
            player.sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &ahttps://www.spigotmc.org/resources/107803/"));
        }
    }
}
