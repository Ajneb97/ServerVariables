package svar.ajneb97.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import svar.ajneb97.model.structure.Variable;

@SuppressWarnings("unused")
public abstract class VariableChangeEvent extends Event {

    private final Player player; //Will be null if not a PLAYER variable
    private final Variable variable;
    private static final HandlerList handlers = new HandlerList();

    //Event called when a variable changes its value
    public VariableChangeEvent(Player player, Variable variable) {
        this.player = player;
        this.variable = variable;
    }

    public Player getPlayer() {
        return player;
    }

    public Variable getVariable() {
        return variable;
    }

    public abstract Object getNewValue();

    public abstract Object getOldValue();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
