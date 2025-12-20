package svar.ajneb97.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;
import svar.ajneb97.model.ServerVariablesListVariable;
import svar.ajneb97.model.ServerVariablesPlayer;
import svar.ajneb97.model.ServerVariablesStringVariable;
import svar.ajneb97.model.ServerVariablesVariable;

@SuppressWarnings("DataFlowIssue")
public class PlayersConfigsManager extends DataFolderConfigManager {

    public PlayersConfigsManager(ServerVariables plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void createFiles() {
        //
    }

    @Override
    public void loadConfigs() {
        Map<UUID, ServerVariablesPlayer> players = new HashMap<>();

        ArrayList<CommonConfig> configs = getConfigs();
        for (CommonConfig commonConfig : configs) {
            FileConfiguration playerFile = commonConfig.getConfig();
            String name = playerFile.getString("name");
            String uuidString = commonConfig.getPath().replace(".yml", "");
            Map<String, ServerVariablesVariable> variables = new HashMap<>();
            if (playerFile.contains("variables")) {
                for (String key : playerFile.getConfigurationSection("variables").getKeys(false)) {
                    if (playerFile.isList("variables." + key)) {
                        variables.put(key, new ServerVariablesListVariable(key, playerFile.getStringList("variables." + key)));
                    } else {
                        variables.put(key, new ServerVariablesStringVariable(key, playerFile.getString("variables." + key)));
                    }
                }
            }

            UUID uuid = UUID.fromString(uuidString);
            ServerVariablesPlayer player = new ServerVariablesPlayer(uuid, name, variables);
            players.put(uuid, player);
        }

        plugin.getPlayerVariablesManager().setPlayerVariables(players);
    }

    public void savePlayer(ServerVariablesPlayer player) {
        String playerName = player.getName();
        CommonConfig playerConfig = getConfigFile(player.getUuid() + ".yml");
        FileConfiguration playerFile = playerConfig.getConfig();

        playerFile.set("name", playerName);
        playerFile.set("variables", null);
        Map<String, ServerVariablesVariable> variables = player.getVariables();
        for (Map.Entry<String, ServerVariablesVariable> entry : variables.entrySet()) {
            playerFile.set("variables." + entry.getKey(), entry.getValue().getCurrentValue()); // String or List<String>
        }

        playerConfig.saveConfig();
    }

    @Override
    public void saveConfigs() {
        Map<UUID, ServerVariablesPlayer> players = plugin.getPlayerVariablesManager().getPlayerVariables();

        Map<UUID, ServerVariablesPlayer> playersCopy = new HashMap<>(players);
        for (Map.Entry<UUID, ServerVariablesPlayer> entry : playersCopy.entrySet()) {
            ServerVariablesPlayer player = entry.getValue();
            if (player.isModified()) {
                savePlayer(player);
            }
            player.setModified(false);
        }
    }
}
