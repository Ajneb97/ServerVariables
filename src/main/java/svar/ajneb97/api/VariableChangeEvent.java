package svar.ajneb97.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import svar.ajneb97.model.structure.Variable;


public class VariableChangeEvent extends Event{

	private Player player; //Will be null if not a PLAYER variable
	private Variable variable;
	private String newValue;
	private static final HandlerList handlers = new HandlerList();

	//Event called when a variable changes its value
	public VariableChangeEvent(Player player, Variable variable, String newValue){
		this.player = player;
		this.variable = variable;
		this.newValue = newValue;
	}	
	
	public Player getPlayer() {
		return player;
	}

	public Variable getVariable() {
		return variable;
	}

	public String getNewValue() {
		return newValue;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
