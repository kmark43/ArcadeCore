package net.mutinies.arcadecore.arcade.classic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PauseExecutor implements CommandExecutor {
    private ClassicGameManager gameManager;
    
    public PauseExecutor(ClassicGameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!gameManager.isGameRunning()) {
            if (gameManager.isPaused()) {
                gameManager.unpause();
                sender.sendMessage("Unpaused the countdown");
            } else {
                gameManager.pause();
                sender.sendMessage("Paused the countdown");
            }
        }
        return true;
    }
}
