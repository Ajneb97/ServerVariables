package svar.ajneb97.config.model;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import svar.ajneb97.ServerVariables;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CommonConfig {

    private final String fileName;
    private FileConfiguration fileConfiguration = null;
    private File file = null;
    private String route;
    private final ServerVariables plugin;
    private final String folderName;
    private final boolean newFile;
    private boolean isFirstTime;

    public CommonConfig(String fileName, ServerVariables plugin, String folderName, boolean newFile) {
        this.fileName = fileName;
        this.plugin = plugin;
        this.newFile = newFile;
        this.folderName = folderName;
        this.isFirstTime = false;
    }

    public String getPath() {
        return this.fileName;
    }

    public void registerConfig() {
        if (folderName != null) {
            file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
        } else {
            file = new File(plugin.getDataFolder(), fileName);
        }

        route = file.getPath();

        if (!file.exists()) {
            isFirstTime = true;
            if (newFile) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Error creating file " + fileName, e);
                }
            } else {
                if (folderName != null) {
                    plugin.saveResource(folderName + File.separator + fileName, false);
                } else {
                    plugin.saveResource(fileName, false);
                }

            }
        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, "Error loading file " + fileName, e);
        }
    }

    public void saveConfig() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error saving file " + fileName, e);
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            reloadConfig();
        }
        return fileConfiguration;
    }

    public boolean reloadConfig() {
        if (fileConfiguration == null) {
            if (folderName != null) {
                file = new File(plugin.getDataFolder() + File.separator + folderName, fileName);
            } else {
                file = new File(plugin.getDataFolder(), fileName);
            }

        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
        return true;
    }

    public String getRoute() {
        return route;
    }
}
