package svar.ajneb97.config;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.config.model.CommonConfig;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.tasks.DataSaveTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class MainConfigManager {

    private final ServerVariables plugin;
    private final CommonConfig configFile;

    private boolean updateNotify;
    private boolean isMySQL;
    private boolean silentCommandsHideErrors;
    private boolean useMiniMessage;

    public MainConfigManager(ServerVariables plugin) {
        this.plugin = plugin;
        this.configFile = new CommonConfig("config.yml", plugin, null, false);
        configFile.registerConfig();
        checkMessagesUpdate();
    }

    public void configure() {
        FileConfiguration config = configFile.getConfig();

        plugin.setMessagesManager(new MessagesManager(config.getString("messages.prefix")));

        DataSaveTask dataSaveTask = plugin.getDataSaveTask();
        if (dataSaveTask != null) {
            dataSaveTask.stop();
        }
        dataSaveTask = new DataSaveTask(plugin);
        dataSaveTask.start(config.getInt("config.data_save_time"));

        updateNotify = config.getBoolean("update_notify");
        isMySQL = config.getBoolean("config.mysql_database.enabled");
        silentCommandsHideErrors = config.getBoolean("config.silent_commands_hide_errors");
        useMiniMessage = config.getBoolean("config.use_minimessage");
    }

    public boolean reloadConfig() {
        if (!configFile.reloadConfig()) {
            return false;
        }
        configure();
        return true;
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public boolean isUpdateNotify() {
        return updateNotify;
    }

    public FileConfiguration getConfig() {
        return configFile.getConfig();
    }

    public void saveConfig() {
        configFile.saveConfig();
    }

    public void checkMessagesUpdate() {
        Path pathConfig = Paths.get(configFile.getRoute());
        try {
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = getConfig();
            if (!text.contains("use_minimessage:")) {
                config.set("config.use_minimessage", false);
                saveConfig();
            }
            if (!text.contains("commandListError:")) {
                config.set("messages.commandListError", "&cYou need to use: &7/svar list <option>");
                config.set("messages.commandListGetError", "&cYou need to use: &7/svar list get <variable> <index> (optional)<player>");
                config.set("messages.commandListSetError", "&cYou need to use: &7/svar list set <variable> <index> <value> (optional)<player> (optional)silent:true");
                config.set("messages.commandListAddError", "&cYou need to use: &7/svar list add <variable> <value> (optional)<player> (optional)silent:true");
                config.set("messages.commandListDisplayError", "&cYou need to use: &7/svar list display <variable> (optional)<player>");
                config.set("messages.commandListResetError", "&cYou need to use: &7/svar list reset <variable> (optional)<player>");
                config.set("messages.commandListRemoveIndexError", "&cYou need to use: &7/svar list removeindex <variable> <index> (optional)<player>");
                config.set("messages.commandListRemoveValueError", "&cYou need to use: &7/svar list removevalue <variable> <value> (optional)<player>");
                config.set("messages.commandListInvalidIndex", "&cYou need to use a valid index.");
                config.set("messages.variableNotList", "&cThat variable is not a list.");
                config.set("messages.variableIsList", "&cThat variable is a list. Use &7/svar list <option>");
                config.set("messages.variableListIndexError", "&cThat list doesn't have the index &7%index%&c.");
                config.set("messages.variableListValueError", "&cThat list doesn't have the value &7%value%&c.");
                config.set("messages.commandListGetCorrect", "&aVariable &7%variable% &acurrent value at index &7%index% &ais: &7%value%&a.");
                config.set("messages.commandListGetCorrectPlayer", "&aVariable &7%variable% &acurrent value at index &7%index% &afrom player &e%player% &ais: &7%value%&a.");
                config.set("messages.commandListSetCorrect", "&aVariable &7%variable% &avalue at index &7%index% &aset to &7%value%&a.");
                config.set("messages.commandListSetCorrectPlayer", "&aVariable &7%variable% &avalue at index &7%index% &aset to &7%value% &afor player &e%player%&a.");
                config.set("messages.commandListDisplayCorrect", "&aVariable &7%variable% &acurrent values: &7%values%");
                config.set("messages.commandListDisplayCorrectPlayer", "&aVariable &7%variable% &acurrent values from player &e%player%&a: &7%values%");
                config.set("messages.commandListRemoveIndexCorrect", "&aVariable &7%variable% &avalue at index &7%index% &aremoved.");
                config.set("messages.commandListRemoveIndexCorrectPlayer", "&aVariable &7%variable% &avalue at index &7%index% &aremoved for player &e%player%&a.");
                config.set("messages.commandListRemoveValueCorrect", "&aVariable &7%variable% &avalue &7%value% &aremoved.");
                config.set("messages.commandListRemoveValueCorrectPlayer", "&aVariable &7%variable% &avalue &7%value% &aremoved for player &e%player%&a.");
                saveConfig();
            }
            if (!text.contains("update_notify:")) {
                getConfig().set("config.update_notify", true);
                saveConfig();
            }
            if (!text.contains("verifyServerCertificate:")) {
                getConfig().set("config.mysql_database.pool.connectionTimeout", 5000);
                getConfig().set("config.mysql_database.advanced.verifyServerCertificate", false);
                getConfig().set("config.mysql_database.advanced.useSSL", true);
                getConfig().set("config.mysql_database.advanced.allowPublicKeyRetrieval", true);
                saveConfig();
            }
            if (!text.contains("silent_commands_hide_errors:")) {
                getConfig().set("config.silent_commands_hide_errors", false);
                saveConfig();
            }
            if (!text.contains("commandResetCorrectAll:")) {
                getConfig().set("messages.commandResetCorrectAll", "&aVariable &7%variable% &areset for &eall players&a.");
                saveConfig();
            }
            if (!text.contains("variableLimitationMaxCharactersError:")) {
                getConfig().set("messages.variableLimitationMaxCharactersError", "&cVariable supports a maximum of &7%value% &ccharacters.");
                saveConfig();
            }
            if (!text.contains("variableLimitationOutOfRangeMax:")) {
                getConfig().set("messages.variableLimitationOutOfRangeMax", "&cVariable out of range. Max value is &7%value%");
                getConfig().set("messages.variableLimitationOutOfRangeMin", "&cVariable out of range. Min value is &7%value%");
                saveConfig();
            }
            if (!text.contains("mysql_database:")) {
                getConfig().set("config.mysql_database.enabled", false);
                getConfig().set("config.mysql_database.host", "localhost");
                getConfig().set("config.mysql_database.port", 3306);
                getConfig().set("config.mysql_database.username", "root");
                getConfig().set("config.mysql_database.password", "root");
                getConfig().set("config.mysql_database.database", "servervariables");
                saveConfig();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error checking messages update", e);
        }
    }

    public boolean isSilentCommandsHideErrors() {
        return silentCommandsHideErrors;
    }

    public boolean isUseMiniMessage() {
        return useMiniMessage;
    }
}
