package svar.ajneb97;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import svar.ajneb97.api.ServerVariablesAPI;
import svar.ajneb97.api.ServerVariablesExpansion;
import svar.ajneb97.config.ConfigsManager;
import svar.ajneb97.database.MySQLConnection;
import svar.ajneb97.listeners.PlayerListener;
import svar.ajneb97.managers.*;
import svar.ajneb97.managers.dependencies.Metrics;
import svar.ajneb97.model.internal.UpdateCheckerResult;
import svar.ajneb97.tasks.DataSaveTask;

import java.io.File;


public class ServerVariables extends JavaPlugin {

    public String prefix = "&8[&a&lServerVariables&8]";
    private PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();

    private VariablesManager variablesManager;
    private ServerVariablesManager serverVariablesManager;
    private PlayerVariablesManager playerVariablesManager;
    private MessagesManager messagesManager;
    private ConfigsManager configsManager;
    private UpdateCheckerManager updateCheckerManager;

    private DataSaveTask dataSaveTask;

    private MySQLConnection mySQLConnection;

    public void onEnable(){
        this.variablesManager = new VariablesManager(this);
        this.serverVariablesManager = new ServerVariablesManager(this);
        this.playerVariablesManager = new PlayerVariablesManager(this);
        registerCommands();
        registerEvents();

        this.configsManager = new ConfigsManager(this);
        this.configsManager.configure();

        ServerVariablesAPI api = new ServerVariablesAPI(this);
        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new ServerVariablesExpansion(this).register();
        }
        Metrics metrics = new Metrics(this,19731);

        if(configsManager.getMainConfigManager().isMySQL()){
            mySQLConnection = new MySQLConnection(this);
            mySQLConnection.setupMySql();
        }

        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &eThanks for using my plugin!   &f~Ajneb97"));

        updateCheckerManager = new UpdateCheckerManager(version);
        updateMessage(updateCheckerManager.check());
    }

    public void onDisable(){
        this.configsManager.saveServerData();
        this.configsManager.savePlayerData();
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &eHas been disabled! &fVersion: "+version));
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

    public void setDataSaveTask(DataSaveTask dataSaveTask) {
        this.dataSaveTask = dataSaveTask;
    }
    public UpdateCheckerManager getUpdateCheckerManager() {
        return updateCheckerManager;
    }

    public void registerCommands(){
        this.getCommand("servervariables").setExecutor(new MainCommand(this));
    }

    public MySQLConnection getMySQLConnection() {
        return mySQLConnection;
    }

    public void updateMessage(UpdateCheckerResult result){
        if(!result.isError()){
            String latestVersion = result.getLatestVersion();
            if(latestVersion != null){
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cThere is a new version available. &e(&7"+latestVersion+"&e)"));
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &fhttps://www.spigotmc.org/resources/107803/"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &cError while checking update."));
        }

    }
}
