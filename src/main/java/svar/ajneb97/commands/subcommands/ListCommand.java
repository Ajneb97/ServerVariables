package svar.ajneb97.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.model.ListVariableResult;
import svar.ajneb97.model.StringVariableResult;
import svar.ajneb97.model.internal.ValueFromArgumentResult;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.utils.OtherUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListCommand {

    private ServerVariables plugin;
    public ListCommand(ServerVariables plugin){
        this.plugin = plugin;
    }

    public void command(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager) {
        if(args.length <= 1){
            msgManager.sendMessage(sender,config.getString("messages.commandListError"),true);
            return;
        }

        switch(args[1]){
            case "get":
                get(sender,args,config,msgManager);
                break;
            case "set":
                set(sender,args,config,msgManager);
                break;
            case "add":
                add(sender,args,config,msgManager);
                break;
            case "removevalue":
                removevalue(sender,args,config,msgManager);
                break;
            case "removeindex":
                removeindex(sender,args,config,msgManager);
                break;
            case "reset":
                reset(sender,args,config,msgManager);
                break;
            case "display":
                display(sender,args,config,msgManager);
                break;
            default:
                msgManager.sendMessage(sender,config.getString("messages.commandListError"),true);
        }
    }

    private void get(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
        // Get value from certain index
        //servervariables list get <variable> <index>
        //servervariables list get <variable> <index> <player>

        if(args.length <= 3){
            msgManager.sendMessage(sender,config.getString("messages.commandListGetError"),true);
            return;
        }

        String variableName = args[2];
        int index = getIndex(args[3],sender,msgManager,config);
        if(index == -1){
            return;
        }
        String playerName = null;

        if(args.length >= 5){
            playerName = args[4];
        }
        StringVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(playerName,variableName,index);

        if(result.isError()){
            msgManager.sendMessage(sender,result.getErrorMessage(),true);
        }else{
            if(playerName != null){
                msgManager.sendMessage(sender,config.getString("messages.commandListGetCorrectPlayer").replace("%variable%",variableName)
                        .replace("%value%",result.getResultValue()).replace("%player%",playerName)
                        .replace("%index%",index+""),true);
            }else{
                msgManager.sendMessage(sender,config.getString("messages.commandListGetCorrect").replace("%variable%",variableName)
                        .replace("%value%",result.getResultValue()).replace("%index%",index+""),true);
            }
        }
    }

    private void set(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
        // Set value at certain index
        //servervariables list set <variable> <index> <value>
        //servervariables list set <variable> <index> <value> <player>

        if(args.length <= 4){
            msgManager.sendMessage(sender,config.getString("messages.commandListSetError"),true);
            return;
        }

        String variableName = args[2];
        int index = getIndex(args[3],sender,msgManager,config);
        if(index == -1){
            return;
        }

        ValueFromArgumentResult valueResult = OtherUtils.getValueFromArgument(args,4);
        if(valueResult == null){
            msgManager.sendMessage(sender,config.getString("messages.commandListSetError"),true);
            return;
        }
        String newValue = valueResult.getFinalValue();
        int valueExtraArgs = valueResult.getExtraArgs();

        String playerName = null;
        StringVariableResult result = null;
        if(args.length >= 6+valueExtraArgs && !args[5+valueExtraArgs].equals("silent:true")) {
            playerName = args[5 + valueExtraArgs];
        }
        result = plugin.getVariablesManager().getListVariablesManager().setListVariableValue(playerName,variableName,index,newValue,false);

        boolean silent = args[args.length-1].equals("silent:true");

        sendMessageSet(sender,result,msgManager,config,variableName,playerName,index,silent);
    }

    private void add(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
        // Add value to list
        //servervariables list add <variable> <value>
        //servervariables list add <variable> <value> <player>
        if(args.length <= 3){
            msgManager.sendMessage(sender,config.getString("messages.commandListAddError"),true);
            return;
        }

        String variableName = args[2];
        ValueFromArgumentResult valueResult = OtherUtils.getValueFromArgument(args,3);
        if(valueResult == null){
            msgManager.sendMessage(sender,config.getString("messages.commandListAddError"),true);
            return;
        }
        String newValue = valueResult.getFinalValue();
        int valueExtraArgs = valueResult.getExtraArgs();

        String playerName = null;
        StringVariableResult result;
        if(args.length >= 5+valueExtraArgs && !args[4+valueExtraArgs].equals("silent:true")) {
            playerName = args[4 + valueExtraArgs];
        }
        result = plugin.getVariablesManager().getListVariablesManager().setListVariableValue(playerName,variableName,-1,newValue,true);

        boolean silent = args[args.length-1].equals("silent:true");

        sendMessageSet(sender,result,msgManager,config,variableName,playerName,result.getIndex(),silent);
    }

    private void removevalue(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
        // Remove value from list
        //servervariables list removevalue <variable> <value>
        //servervariables list removevalue <variable> <value> <player>

        if(args.length <= 3){
            msgManager.sendMessage(sender,config.getString("messages.commandListRemoveValueError"),true);
            return;
        }

        String variableName = args[2];
        ValueFromArgumentResult valueResult = OtherUtils.getValueFromArgument(args,3);
        if(valueResult == null){
            msgManager.sendMessage(sender,config.getString("messages.commandListRemoveValueError"),true);
            return;
        }
        String value = valueResult.getFinalValue();
        int valueExtraArgs = valueResult.getExtraArgs();

        String playerName = null;
        StringVariableResult result;
        if(args.length >= 5+valueExtraArgs && !args[4+valueExtraArgs].equals("silent:true")) {
            playerName = args[4 + valueExtraArgs];
        }

        result = plugin.getVariablesManager().getListVariablesManager().removeListVariableValue(playerName,variableName,value);

        boolean silent = args[args.length-1].equals("silent:true");

        if(result.isError()){
            msgManager.sendMessage(sender,result.getErrorMessage(),true);
        }else{
            if(silent){
                return;
            }
            if(playerName != null){
                msgManager.sendMessage(sender,config.getString("messages.commandListRemoveValueCorrectPlayer").replace("%variable%",variableName)
                        .replace("%player%",playerName)
                        .replace("%value%",value),true);
            }else{
                msgManager.sendMessage(sender,config.getString("messages.commandListRemoveValueCorrect").replace("%variable%",variableName)
                        .replace("%value%",value),true);
            }
        }
    }

    private void removeindex(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
        // Remove value from list
        //servervariables list removeindex <variable> <index>
        //servervariables list removeindex <variable> <index> <player>

        if(args.length <= 3){
            msgManager.sendMessage(sender,config.getString("messages.commandListRemoveIndexError"),true);
            return;
        }

        String variableName = args[2];
        int index = getIndex(args[3],sender,msgManager,config);
        if(index == -1){
            return;
        }
        String playerName = null;

        if(args.length >= 5 && !args[4].equals("silent:true")){
            playerName = args[4];
        }
        StringVariableResult result = plugin.getVariablesManager().getListVariablesManager().removeListVariableIndex(playerName,variableName,index);

        boolean silent = args[args.length-1].equals("silent:true");

        if(result.isError()){
            msgManager.sendMessage(sender,result.getErrorMessage(),true);
        }else{
            if(silent){
                return;
            }
            if(playerName != null){
                msgManager.sendMessage(sender,config.getString("messages.commandListRemoveIndexCorrectPlayer").replace("%variable%",variableName)
                        .replace("%player%",playerName)
                        .replace("%index%",index+""),true);
            }else{
                msgManager.sendMessage(sender,config.getString("messages.commandListRemoveIndexCorrect").replace("%variable%",variableName)
                        .replace("%index%",index+""),true);
            }
        }
    }

    private void reset(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
        //servervariables list reset <variable>
        //servervariables list reset <variable> <player>

        if(args.length <= 2){
            msgManager.sendMessage(sender,config.getString("messages.commandListResetError"),true);
            return;
        }

        String variableName = args[2];
        String playerName = null;
        if(args.length >= 4 && !args[3].equals("silent:true")) {
            playerName = args[3];
        }
        StringVariableResult result = plugin.getVariablesManager().resetVariable(playerName,variableName,playerName != null && playerName.equals("*"));

        boolean silent = args[args.length-1].equals("silent:true");

        if(result.isError()){
            msgManager.sendMessage(sender,result.getErrorMessage(),true);
        }else{
            if(silent){
                return;
            }
            if(playerName != null){
                if(playerName.equals("*")){
                    msgManager.sendMessage(sender,config.getString("messages.commandResetCorrectAll").replace("%variable%",variableName),true);
                }else{
                    msgManager.sendMessage(sender,config.getString("messages.commandResetCorrectPlayer").replace("%variable%",variableName)
                            .replace("%player%",playerName),true);
                }
            }else{
                msgManager.sendMessage(sender,config.getString("messages.commandResetCorrect").replace("%variable%",variableName),true);
            }
        }
    }

    private void display(CommandSender sender, String[] args, FileConfiguration config, MessagesManager msgManager){
        // Displays the full list
        //servervariables list display <variable>
        //servervariables list display <variable> <player>

        if(args.length <= 2){
            msgManager.sendMessage(sender,config.getString("messages.commandListDisplayError"),true);
            return;
        }

        String variableName = args[2];
        String playerName = null;

        if(args.length >= 4){
            playerName = args[3];
        }
        ListVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValue(playerName,variableName,false);

        List<String> list = result.getResultValue();

        if(result.isError()){
            msgManager.sendMessage(sender,result.getErrorMessage(),true);
        }else{
            StringBuilder sb = new StringBuilder("&7[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("&e").append(i).append(":&7").append(list.get(i));
            }
            sb.append("]");
            if(playerName != null){
                msgManager.sendMessage(sender,config.getString("messages.commandListDisplayCorrectPlayer").replace("%variable%",variableName)
                        .replace("%values%",sb.toString()).replace("%player%",playerName),true);
            }else{
                msgManager.sendMessage(sender,config.getString("messages.commandListDisplayCorrect").replace("%variable%",variableName)
                        .replace("%values%",sb.toString()),true);
            }
        }
    }

    private int getIndex(String arg,CommandSender sender,MessagesManager msgManager,FileConfiguration config){
        int index;
        try{
            index = Integer.parseInt(arg);
            if(index < 0){
                msgManager.sendMessage(sender,config.getString("messages.commandListInvalidIndex"),true);
                return -1;
            }
        }catch(NumberFormatException e){
            msgManager.sendMessage(sender,config.getString("messages.commandListInvalidIndex"),true);
            return -1;
        }
        return index;
    }

    private void sendMessageSet(CommandSender sender, StringVariableResult result, MessagesManager msgManager, FileConfiguration config,
                                String variableName, String playerName, int index, boolean silent){
        boolean silentCommandsHideErrors = plugin.getConfigsManager().getMainConfigManager().isSilentCommandsHideErrors();
        if(result.isError()){
            if(silent && silentCommandsHideErrors){
                return;
            }
            msgManager.sendMessage(sender,result.getErrorMessage(),true);
        }else{
            if(silent){
                return;
            }
            if(playerName != null){
                msgManager.sendMessage(sender,config.getString("messages.commandListSetCorrectPlayer").replace("%variable%",variableName)
                        .replace("%value%",result.getResultValue()).replace("%player%",playerName)
                        .replace("%index%",index+""),true);
            }else{
                msgManager.sendMessage(sender,config.getString("messages.commandListSetCorrect").replace("%variable%",variableName)
                        .replace("%value%",result.getResultValue()).replace("%index%",index+""),true);
            }
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args, Map<String,Variable> variables) {
        // args length is at least 2
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        commands.add("get");commands.add("set");commands.add("add");commands.add("removevalue");commands.add("removeindex");
        commands.add("display");commands.add("reset");
        if(args.length == 2){
            for(String c : commands) {
                if(args[1].isEmpty() || c.startsWith(args[1].toLowerCase())) {
                    completions.add(c);
                }
            }
            return completions;
        }else{
            if(commands.contains(args[1].toLowerCase())){
                if(args.length == 3){
                    String argVariable = args[2];
                    for(Map.Entry<String, Variable> entry : variables.entrySet()) {
                        Variable variable = entry.getValue();
                        if(!variable.getValueType().equals(ValueType.LIST)){
                            continue;
                        }
                        if(argVariable.isEmpty() || variable.getName().toLowerCase().startsWith(argVariable.toLowerCase())) {
                            completions.add(variable.getName());
                        }
                    }
                    return completions;
                }else if(args.length == 4){
                    if(args[1].equalsIgnoreCase("add")){
                        addVariablePossibleValuesCompletions(completions,args,3);
                        return completions;
                    }else if(args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("get") ||
                            args[1].equalsIgnoreCase("removeindex")){
                        completions.add("<index>");
                        return completions;
                    }else if(args[1].equalsIgnoreCase("removevalue")){
                        completions.add("<value>");
                        return completions;
                    }else if(args[1].equalsIgnoreCase("reset")) {
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            if(args[3].isEmpty() || p.getName().startsWith(args[3].toLowerCase())){
                                completions.add(p.getName());
                            }
                        }
                        addAllWord(completions,args[3]);
                        return completions;
                    }
                }else if(args.length == 5){
                    if(args[1].equalsIgnoreCase("set")){
                        addVariablePossibleValuesCompletions(completions,args,4);
                        return completions;
                    }
                }
            }
        }
        return null;
    }

    private void addVariablePossibleValuesCompletions(List<String> completions,String[] args,int argVariableIndex){
        Variable variable = plugin.getVariablesManager().getVariable(args[2]);
        String argVariable = args[argVariableIndex];
        if(variable != null){
            List<String> possibleRealValues = variable.getPossibleRealValues();
            for(String possibleValue : possibleRealValues){
                if(argVariable.isEmpty() || possibleValue.toLowerCase().startsWith(argVariable.toLowerCase())) {
                    completions.add(possibleValue);
                }
            }
        }
        completions.add("<value>");
    }

    private void addAllWord(List<String> completions,String arg){
        if(arg.isEmpty() || "*".startsWith(arg.toLowerCase())) {
            completions.add("*");
        }
    }
}
