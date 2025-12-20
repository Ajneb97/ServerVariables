package svar.ajneb97.tasks;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;
import svar.ajneb97.ServerVariables;

public class DataSaveTask {

    private final ServerVariables plugin;
    private final boolean isFolia;
    private Runnable cancel;

    public DataSaveTask(ServerVariables plugin) {
        this.plugin = plugin;
        this.isFolia = plugin.isFolia;
    }

    public void start(int minutes) {
        long period = minutes * 60L * 20L;

        Runnable runnable = this::execute;

        if (isFolia) {
            ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                    plugin,
                    t -> runnable.run(),
                    period,
                    period
            );
            cancel = task::cancel;
        } else {
            BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                    plugin,
                    runnable,
                    period,
                    period
            );
            cancel = task::cancel;
        }
    }

    public void stop() {
        if (cancel != null) {
            cancel.run();
        }
    }

    public void execute() {
        plugin.getConfigsManager().saveServerData();
        plugin.getConfigsManager().savePlayerData();
    }
}
