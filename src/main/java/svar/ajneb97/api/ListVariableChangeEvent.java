package svar.ajneb97.api;

import org.bukkit.entity.Player;
import svar.ajneb97.model.structure.Variable;

import java.util.List;

@SuppressWarnings("unused")
public class ListVariableChangeEvent extends VariableChangeEvent {

    private final List<String> newValue;
    private final List<String> oldValue;
    private final int indexModified;

    public ListVariableChangeEvent(Player player, Variable variable, List<String> newValue, List<String> oldValue, int indexModified) {
        super(player, variable);
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.indexModified = indexModified;
    }

    @Override
    public List<String> getNewValue() {
        return newValue;
    }

    @Override
    public List<String> getOldValue() {
        return oldValue;
    }

    public int getIndexModified() {
        return indexModified;
    }
}
