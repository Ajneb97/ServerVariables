package svar.ajneb97;




import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.model.VariableResult;
import svar.ajneb97.model.structure.Variable;

import java.util.ArrayList;
import java.util.List;


public class MainCommand implements CommandExecutor, TabCompleter {

	private ServerVariables plugin;
	public MainCommand(ServerVariables plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("servervariables.admin")){
			return false;
		}

		FileConfiguration config = plugin.getConfig();
		MessagesManager msgManager = plugin.getMessagesManager();
		if(args.length >= 1){
			if(args[0].equalsIgnoreCase("set")){
				set(sender,args,config,msgManager);
			}else if(args[0].equalsIgnoreCase("reload")){
				reload(sender,args,config,msgManager);
			}else if(args[0].equalsIgnoreCase("get")){
				get(sender,args,config,msgManager);
			}else if(args[0].equalsIgnoreCase("add")){
				add(sender,args,config,msgManager);
			}else if(args[0].equalsIgnoreCase("reduce")){
				reduce(sender,args,config,msgManager);
			}else if(args[0].equalsIgnoreCase("reset")){
				reset(sender,args,config,msgManager);
			}else{
				help(sender,args,config,msgManager);
			}
		}else{
			help(sender,args,config,msgManager);
		}

		return true;

	}

	public void help(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
		sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &8[&aServerVariables&8] &7] ]"));
		sender.sendMessage(MessagesManager.getColoredMessage(" "));
		sender.sendMessage(MessagesManager.getColoredMessage("&6/svar help &8Shows this message."));
		sender.sendMessage(MessagesManager.getColoredMessage("&6/svar set <variable> <value> (optional)<player> &8Sets the value of a variable."));
		sender.sendMessage(MessagesManager.getColoredMessage("&6/svar get <variable> (optional)<player> &8Gets the value from a variable."));
		sender.sendMessage(MessagesManager.getColoredMessage("&6/svar add <variable> <value> (optional)<player> &8Adds a value to a variable (INTEGER or DOUBLE)."));
		sender.sendMessage(MessagesManager.getColoredMessage("&6/svar reduce <variable> <value> (optional)<player> &8Reduces the value of a variable (INTEGER or DOUBLE)."));
		sender.sendMessage(MessagesManager.getColoredMessage("&6/svar reset <variable> <value> (optional)<player> &8Resets the value of a variable."));
		sender.sendMessage(MessagesManager.getColoredMessage("&6/svar reload &8Reloads the config."));
		sender.sendMessage(MessagesManager.getColoredMessage(" "));
		sender.sendMessage(MessagesManager.getColoredMessage("&7[ [ &8[&aServerVariables&8] &7] ]"));

	}

	public void set(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
		//servervariables set <variable> <value> (Set global variable)
		//servervariables set <variable> <value> <player> (Set player variable)
		if(args.length <= 2){
			msgManager.sendMessage(sender,config.getString("messages.commandSetError"),true);
			return;
		}

		String variableName = args[1];
		String newValue = args[2];
		String playerName = null;

		VariableResult result = null;
		if(args.length >= 4){
			playerName = args[3];
			result = plugin.getPlayerVariablesManager().setVariable(playerName,variableName,newValue);
		}else{
			result = plugin.getServerVariablesManager().setVariable(variableName,newValue);
		}

		sendMessageSet(sender,result,msgManager,config,variableName,playerName);
	}

	public void get(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager) {
		//servervariables get <variable> (Get global variable)
		//servervariables get <variable> <player> (Get player variable)
		if (args.length <= 1) {
			msgManager.sendMessage(sender, config.getString("messages.commandGetError"), true);
			return;
		}

		String variableName = args[1];
		String playerName = null;
		VariableResult result = null;

		if(args.length >= 3){
			playerName = args[2];
			result = plugin.getPlayerVariablesManager().getVariableValue(playerName,variableName, false);
		}else{
			result = plugin.getServerVariablesManager().getVariableValue(variableName,false);
		}

		if(result.isError()){
			msgManager.sendMessage(sender,result.getErrorMessage(),true);
		}else{
			if(playerName != null){
				msgManager.sendMessage(sender,config.getString("messages.commandGetCorrectPlayer").replace("%variable%",variableName)
						.replace("%value%",result.getResultValue()).replace("%player%",playerName),true);
			}else{
				msgManager.sendMessage(sender,config.getString("messages.commandGetCorrect").replace("%variable%",variableName)
						.replace("%value%",result.getResultValue()),true);
			}
		}
	}

	public void add(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
		//servervariables add <variable> <value> (Add value to server variable if INTEGER or DOUBLE)
		//servervariables add <variable> <value> <player> (Add value to player variable if INTEGER or DOUBLE)
		if(args.length <= 2){
			msgManager.sendMessage(sender,config.getString("messages.commandAddError"),true);
			return;
		}

		String variableName = args[1];
		String value = args[2];
		String playerName = null;

		VariableResult result = null;
		if(args.length >= 4){
			playerName = args[3];
			result = plugin.getPlayerVariablesManager().modifyVariable(playerName,variableName,value,true);
		}else{
			result = plugin.getServerVariablesManager().modifyVariable(variableName,value,true);
		}

		sendMessageSet(sender,result,msgManager,config,variableName,playerName);
	}

	public void reduce(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
		//servervariables reduce <variable> <value> (Reduce value of server variable if INTEGER or DOUBLE)
		//servervariables reduce <variable> <value> <player> (Reduce value of player variable if INTEGER or DOUBLE)
		if(args.length <= 2){
			msgManager.sendMessage(sender,config.getString("messages.commandReduceError"),true);
			return;
		}

		String variableName = args[1];
		String value = args[2];
		String playerName = null;

		VariableResult result = null;
		if(args.length >= 4){
			playerName = args[3];
			result = plugin.getPlayerVariablesManager().modifyVariable(playerName,variableName,value,false);
		}else{
			result = plugin.getServerVariablesManager().modifyVariable(variableName,value,false);
		}

		sendMessageSet(sender,result,msgManager,config,variableName,playerName);
	}

	public void reset(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
		//servervariables reset <variable> (Resets a global variable to the default value)
		//servervariables reset <variable> <player> (Resets a player variable to the default value)
		if(args.length <= 1){
			msgManager.sendMessage(sender,config.getString("messages.commandResetError"),true);
			return;
		}

		String variableName = args[1];
		String playerName = null;

		VariableResult result = null;
		if(args.length >= 3){
			playerName = args[2];
			result = plugin.getPlayerVariablesManager().resetVariable(playerName,variableName);
		}else{
			result = plugin.getServerVariablesManager().resetVariable(variableName);
		}

		if(result.isError()){
			msgManager.sendMessage(sender,result.getErrorMessage(),true);
		}else{
			if(playerName != null){
				msgManager.sendMessage(sender,config.getString("messages.commandResetCorrectPlayer").replace("%variable%",variableName)
						.replace("%player%",playerName),true);
			}else{
				msgManager.sendMessage(sender,config.getString("messages.commandResetCorrect").replace("%variable%",variableName),true);
			}
		}
	}

	private void sendMessageSet(CommandSender sender,VariableResult result,MessagesManager msgManager,FileConfiguration config,
							   String variableName,String playerName){
		if(result.isError()){
			msgManager.sendMessage(sender,result.getErrorMessage(),true);
		}else{
			if(playerName != null){
				msgManager.sendMessage(sender,config.getString("messages.commandSetCorrectPlayer").replace("%variable%",variableName)
						.replace("%value%",result.getResultValue()).replace("%player%",playerName),true);
			}else{
				msgManager.sendMessage(sender,config.getString("messages.commandSetCorrect").replace("%variable%",variableName)
						.replace("%value%",result.getResultValue()),true);
			}
		}
	}

	public void reload(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
		// /servervariables reload
		plugin.getConfigsManager().reloadConfigs();
		msgManager.sendMessage(sender,config.getString("messages.pluginReloaded"),true);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("servervariables.admin")){
			return null;
		}

		if(args.length == 1){
			//Show all commands
			List<String> completions = new ArrayList<String>();
			List<String> commands = new ArrayList<String>();
			commands.add("reload");commands.add("set");commands.add("get");commands.add("add");commands.add("reduce");
			commands.add("reset");commands.add("help");
			for(String c : commands) {
				if(args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
					completions.add(c);
				}
			}
			return completions;
		}else{
			List<String> completions = new ArrayList<String>();
			ArrayList<Variable> variables = plugin.getVariablesManager().getVariables();
			if((args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("add")
					|| args[0].equalsIgnoreCase("reduce") || args[0].equalsIgnoreCase("reset"))
					&& args.length == 2) {
				String argVariable = args[1];
				for(Variable variable : variables) {
					if(argVariable.isEmpty() || variable.getName().toLowerCase().startsWith(argVariable.toLowerCase())) {
						completions.add(variable.getName());
					}
				}
				return completions;
			}else if(args[0].equalsIgnoreCase("set") && args.length == 3){
				Variable variable = plugin.getVariablesManager().getVariable(args[1]);
				String argVariable = args[2];

				if(variable != null){
					List<String> possibleValues = variable.getPossibleValues();
					for(String possibleValue : possibleValues){
						if(argVariable.isEmpty() || possibleValue.toLowerCase().startsWith(argVariable.toLowerCase())) {
							completions.add(possibleValue);
						}
					}
				}
				return completions;
			}
		}
		return null;
	}
}
