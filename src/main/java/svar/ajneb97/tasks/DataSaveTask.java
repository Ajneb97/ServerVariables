package svar.ajneb97.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import svar.ajneb97.ServerVariables;

public class DataSaveTask {

	private ServerVariables plugin;
	private boolean end;
	public DataSaveTask(ServerVariables plugin) {
		this.plugin = plugin;
		this.end = false;
	}
	
	public void end() {
		end = true;
	}
	
	public void start(int minutes) {
		long ticks = minutes*60*20;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(end) {
					this.cancel();
				}else {
					execute();
				}
			}
			
		}.runTaskTimerAsynchronously(plugin, 0L, ticks);
	}
	
	public void execute() {
		plugin.getConfigsManager().saveData();
	}
}
