package ch.usi.inf.genesis.data.bugtracker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BugHistory {
    private final HashMap<String, List<BugHistoryTransition>> entries;

    public BugHistory(){
        this.entries = new HashMap<String, List<BugHistoryTransition>>();
    }

    public void addTransition(final String key, final BugHistoryTransition value){
        if(!entries.containsKey(key)){
            final List<BugHistoryTransition> transitions = new ArrayList<BugHistoryTransition>();
            transitions.add(value);
            entries.put(key,transitions);
            return;
        }

        final List<BugHistoryTransition> transitions = entries.get(key);
        transitions.add(value);
        entries.put(key,transitions);
    }

    public void addTransitions(final String key, final List<BugHistoryTransition> values){
        for(final BugHistoryTransition e : values)
            addTransition(key, e);
    }

    public List<BugHistoryTransition> getAllTransitions(final String key){
        return this.entries.get(key);
    }

    public BugHistoryTransition getTransition(final String key){
        if(!entries.containsKey(key))
            return null;

        return this.entries.get(key).get(0);
    }

    @Override
    public String toString(){
        return entries.toString();
    }

}
