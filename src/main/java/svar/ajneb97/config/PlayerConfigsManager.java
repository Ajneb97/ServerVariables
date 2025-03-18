package svar.ajneb97.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.ServerVariablesPlayer;
import svar.ajneb97.model.ServerVariablesVariable;

public class PlayerConfigsManager {

	private ServerVariables plugin;
	
	public PlayerConfigsManager(ServerVariables plugin) {
		this.plugin = plugin;
	}
	
	public void configure() {
		createPlayersFolder();
		loadPlayers();
	}
	
	public void createPlayersFolder(){
		File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + "players");
            if(!folder.exists()){
                folder.mkdirs();
            }
        } catch(SecurityException e) {
            folder = null;
        }
	}
	
	public PlayerConfig getPlayerConfig(String pathName) {
		PlayerConfig config = new PlayerConfig(pathName,plugin);
		config.registerPlayerConfig();
		return config;
	}
	
	public void loadPlayers() {
		Map<UUID,ServerVariablesPlayer> players = new HashMap<>();

		String path = plugin.getDataFolder() + File.separator + "players";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String pathName = file.getName();
                PlayerConfig playerConfig = new PlayerConfig(pathName, plugin);
                playerConfig.registerPlayerConfig();

                FileConfiguration playerFile = playerConfig.getConfig();
                String name = playerFile.getString("name");
                String uuidString = playerConfig.getPath().replace(".yml", "");
                ArrayList<ServerVariablesVariable> variables = new ArrayList<>();
                if (playerFile.contains("variables")) {
                    for (String key : playerFile.getConfigurationSection("variables").getKeys(false)) {
                        variables.add(new ServerVariablesVariable(key, playerFile.getString("variables." + key)));
                    }
                }

                UUID uuid = UUID.fromString(uuidString);
                ServerVariablesPlayer player = new ServerVariablesPlayer(uuid, name, variables);
                players.put(uuid, player);
            }
        }

		plugin.getPlayerVariablesManager().setPlayerVariables(players);
	}

	public void savePlayer(ServerVariablesPlayer player){
		String playerName = player.getName();
		PlayerConfig playerConfig = getPlayerConfig(player.getUuid()+".yml");
		FileConfiguration playerFile = playerConfig.getConfig();

		playerFile.set("name", playerName);
		playerFile.set("variables", null);
		ArrayList<ServerVariablesVariable> variables = player.getVariables();
		for(ServerVariablesVariable v : variables){
			playerFile.set("variables."+v.getVariableName(), v.getCurrentValue());
		}

		playerConfig.savePlayerConfig();
	}
	
	public void savePlayers() {
		Map<UUID, ServerVariablesPlayer> players = plugin.getPlayerVariablesManager().getPlayerVariables();

		Map<UUID, ServerVariablesPlayer> playersCopy = new HashMap<>(players);
		for(Map.Entry<UUID, ServerVariablesPlayer> entry : playersCopy.entrySet()){
			ServerVariablesPlayer player = entry.getValue();
			if(player.isModified()){
				savePlayer(player);
			}
			player.setModified(false);
		}
	}
}
