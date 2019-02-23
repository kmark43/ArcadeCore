package net.mutinies.arcadecore.scoreboard;

import net.mutinies.arcadecore.util.AnimatedObjectUpdater;

import java.util.ArrayList;
import java.util.List;

public abstract class ScoreboardUpdater extends AnimatedObjectUpdater<ScoreboardDisplay> {
    public ScoreboardUpdater(ScoreboardDisplay targetObject, int delay) {
        super(targetObject, delay);
    }
    
    @Override
    public void onUpdate(ScoreboardDisplay object) {
        List<String> lines = new ArrayList<>();
        updateLines(lines);
        getTarget().update(lines);
    }
    
    public abstract void updateLines(List<String> lines);
}
