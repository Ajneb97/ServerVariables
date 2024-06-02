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
	
	private ArrayList<PlayerConfig> configPlayers;
	private ServerVariables plugin;
	
	public PlayerConfigsManager(ServerVariables plugin) {
		this.plugin = plugin;
		this.configPlayers = new ArrayList<PlayerConfig>();
	}
	
	public void configure() {
		createPlayersFolder();
		registerPlayersFiles();
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
	
	public void savePlayersFiles() {
		for(int i=0;i<configPlayers.size();i++) {
			configPlayers.get(i).savePlayerConfig();
		}
	}
	
	public void registerPlayersFiles(){
		String path = plugin.getDataFolder() + File.separator + "players";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (int i=0;i<listOfFiles.length;i++) {
			if(listOfFiles[i].isFile()) {
		        String pathName = listOfFiles[i].getName();
		        PlayerConfig config = new PlayerConfig(pathName,plugin);
		        config.registerPlayerConfig();
		        configPlayers.add(config);
		    }
		}
	}
	
	public ArrayList<PlayerConfig> getConfigPlayers(){
		return this.configPlayers;
	}
	
	public boolean fileAlreadyRegistered(String pathName) {
		for(int i=0;i<configPlayers.size();i++) {
			if(configPlayers.get(i).getPath().equals(pathName)) {
				return true;
			}
		}
		return false;
	}
	
	public PlayerConfig getPlayerConfig(String pathName) {
		for(int i=0;i<configPlayers.size();i++) {
			if(configPlayers.get(i).getPath().equals(pathName)) {
				return configPlayers.get(i);
			}
		}
		return null;
	}
	
	public ArrayList<PlayerConfig> getPlayerConfigs() {
		return this.configPlayers;
	}
	
	public boolean registerPlayer(String pathName) {
		if(!fileAlreadyRegistered(pathName)) {
			PlayerConfig config = new PlayerConfig(pathName,plugin);
	        config.registerPlayerConfig();
	        configPlayers.add(config);
	        return true;
		}else {
			return false;
		}
	}
	
	public void removeConfigPlayer(String path) {
		for(int i=0;i<configPlayers.size();i++) {
			if(configPlayers.get(i).getPath().equals(path)) {
				configPlayers.remove(i);
			}
		}
	}
	
	public void loadPlayers() {
		Map<UUID,ServerVariablesPlayer> players = new HashMap<>();
		
		for(PlayerConfig playerConfig : configPlayers) {
			FileConfiguration playerFile = playerConfig.getConfig();
			String name = playerFile.getString("name");
			String uuidString = playerConfig.getPath().replace(".yml", "");
			ArrayList<ServerVariablesVariable> variables = new ArrayList<ServerVariablesVariable>();
			if(playerFile.contains("variables")){
				for(String key : playerFile.getConfigurationSection("variables").getKeys(false)){
					variables.add(new ServerVariablesVariable(key,playerFile.getString("variables."+key)));
				}
			}

			UUID uuid = UUID.fromString(uuidString);
			ServerVariablesPlayer player = new ServerVariablesPlayer(uuid,name,variables);
			players.put(uuid,player);
		}
		plugin.getPlayerVariablesManager().setPlayerVariables(players);
	}

	public void savePlayer(ServerVariablesPlayer player){
		String playerName = player.getName();
		PlayerConfig playerConfig = getPlayerConfig(player.getUuid()+".yml");
		if(playerConfig == null) {
			registerPlayer(player.getUuid()+".yml");
			playerConfig = getPlayerConfig(player.getUuid()+".yml");
		}
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

		for(Map.Entry<UUID, ServerVariablesPlayer> entry : players.entrySet()){
			ServerVariablesPlayer player = entry.getValue();
			if(player.isModified()){
				savePlayer(player);
			}
			player.setModified(false);
		}
	}
}
