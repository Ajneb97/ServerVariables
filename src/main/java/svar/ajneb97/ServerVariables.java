package svar.ajneb97;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import svar.ajneb97.api.ServerVariablesAPI;
import svar.ajneb97.api.ServerVariablesExpansion;
import svar.ajneb97.config.ConfigsManager;
import svar.ajneb97.listeners.PlayerListener;
import svar.ajneb97.managers.PlayerVariablesManager;
import svar.ajneb97.managers.ServerVariablesManager;
import svar.ajneb97.managers.VariablesManager;
import svar.ajneb97.managers.MessagesManager;
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

    private DataSaveTask dataSaveTask;

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

        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &eThanks for using my plugin!   &f~Ajneb97"));
    }

    public void onDisable(){
        this.configsManager.getDataConfigManager().saveData();
        this.configsManager.getPlayerConfigsManager().savePlayers();
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

    public void registerCommands(){
        this.getCommand("servervariables").setExecutor(new MainCommand(this));
    }
}
