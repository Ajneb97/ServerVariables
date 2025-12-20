package svar.ajneb97.api;

import org.bukkit.entity.Player;
import svar.ajneb97.model.structure.Variable;

public class StringVariableChangeEvent extends VariableChangeEvent {

    private final String newValue;
    private final String oldValue;

    public StringVariableChangeEvent(Player player, Variable variable, String newValue, String oldValue) {
        super(player, variable);
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    @Override
    public String getNewValue() {
        return newValue;
    }

    @Override
    public String getOldValue() {
        return oldValue;
    }
}
