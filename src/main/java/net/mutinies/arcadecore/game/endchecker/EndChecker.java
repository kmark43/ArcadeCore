package net.mutinies.arcadecore.game.endchecker;

import net.mutinies.arcadecore.game.Game;

public abstract class EndChecker {
    private Game game;
    
    public EndChecker(Game game) {
        this.game = game;
    }
    
    public void checkEnd() {
        if (shouldEnd()) {
            game.getGameStateManager().stop();
        }
    }
    
    public abstract boolean shouldEnd();
}
