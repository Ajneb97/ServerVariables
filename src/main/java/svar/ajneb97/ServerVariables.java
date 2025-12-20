package svar.ajneb97;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import svar.ajneb97.api.ServerVariablesAPI;
import svar.ajneb97.api.ServerVariablesExpansion;
import svar.ajneb97.commands.MainCommand;
import svar.ajneb97.config.ConfigsManager;
import svar.ajneb97.database.MySQLConnection;
import svar.ajneb97.listeners.PlayerListener;
import svar.ajneb97.managers.*;
import svar.ajneb97.managers.dependencies.Metrics;
import svar.ajneb97.model.internal.UpdateCheckerResult;
import svar.ajneb97.tasks.DataSaveTask;
import svar.ajneb97.utils.ServerVersion;

@SuppressWarnings("deprecation")
public class ServerVariables extends JavaPlugin {

    public static String prefix;
    public static ServerVersion serverVersion;
    private final PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();

    private VariablesManager variablesManager;
    private ServerVariablesManager serverVariablesManager;
    private PlayerVariablesManager playerVariablesManager;
    private MessagesManager messagesManager;
    private ConfigsManager configsManager;
    private UpdateCheckerManager updateCheckerManager;

    private DataSaveTask dataSaveTask;

    private MySQLConnection mySQLConnection;

    public final boolean isFolia = checkFolia();

    public void onEnable() {
        setVersion();
        setPrefix();

        variablesManager = new VariablesManager(this);
        serverVariablesManager = new ServerVariablesManager();
        playerVariablesManager = new PlayerVariablesManager(this);

        registerCommands();
        registerEvents();

        configsManager = new ConfigsManager(this);
        configsManager.configure();

        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ServerVariablesExpansion(this).register();
        }
        new Metrics(this, 19731);

        if (configsManager.getMainConfigManager().isMySQL()) {
            mySQLConnection = new MySQLConnection(this);
            mySQLConnection.setupMySql();
        }

        dataSaveTask = new DataSaveTask(this);
        dataSaveTask.start(configsManager.getMainConfigManager().getConfig().getInt("config.data_save_time"));

        Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(prefix + " &eHas been enabled! &fVersion: " + version));
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(prefix + " &eThanks for using my plugin!   &f~Ajneb97"));

        updateCheckerManager = new UpdateCheckerManager(version);
        updateMessage(updateCheckerManager.check());
        ServerVariablesAPI.init(this);
    }

    public void onDisable() {
        configsManager.saveServerData();

        configsManager.savePlayerData();

        if (mySQLConnection != null) {
            mySQLConnection.disable();
        }

        Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(prefix + " &eHas been disabled! &fVersion: " + version));
    }

    public void setPrefix() {
        prefix = MessagesManager.getLegacyColoredMessage("&8[&a&lServerVariables&8]");
    }

    public void setVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch (bukkitVersion) {
            case "1.20.5":
            case "1.20.6":
                serverVersion = ServerVersion.v1_20_R4;
                break;
            case "1.21":
            case "1.21.1":
                serverVersion = ServerVersion.v1_21_R1;
                break;
            case "1.21.2":
            case "1.21.3":
                serverVersion = ServerVersion.v1_21_R2;
                break;
            case "1.21.4":
                serverVersion = ServerVersion.v1_21_R3;
                break;
            case "1.21.5":
                serverVersion = ServerVersion.v1_21_R4;
                break;
            case "1.21.6":
            case "1.21.7":
            case "1.21.8":
                serverVersion = ServerVersion.v1_21_R5;
                break;
            case "1.21.9":
            case "1.21.10":
                serverVersion = ServerVersion.v1_21_R6;
                break;
            case "1.21.11":
                serverVersion = ServerVersion.v1_21_R7;
                break;
            default:
                try {
                    serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
                } catch (Exception e) {
                    serverVersion = ServerVersion.v1_21_R7;
                }
        }
    }

    public VariablesManager getVariablesManager() {
        return variablesManager;
    }

    public ServerVariablesManager getServerVariablesManager() {
        return serverVariablesManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public ConfigsManager getConfigsManager() {
        return configsManager;
    }

    public PlayerVariablesManager getPlayerVariablesManager() {
        return playerVariablesManager;
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
    }

    public DataSaveTask getDataSaveTask() {
        return dataSaveTask;
    }

    public UpdateCheckerManager getUpdateCheckerManager() {
        return updateCheckerManager;
    }

    public void registerCommands() {
        PluginCommand command = getCommand("servervariables");
        if (command != null) {
            command.setExecutor(new MainCommand(this));
        }
    }

    public MySQLConnection getMySQLConnection() {
        return mySQLConnection;
    }

    public void updateMessage(UpdateCheckerResult result) {
        if (!result.isError()) {
            String latestVersion = result.getLatestVersion();
            if (latestVersion != null) {
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage("&cThere is a new version available. &e(&7" + latestVersion + "&e)"));
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage("&cYou can download it at: &fhttps://modrinth.com/plugin/servervariables"));
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(prefix + " &cError while checking update."));
        }
    }

    private boolean checkFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
