package net.mutinies.arcadecore.arcade.classic;

import net.mutinies.arcadecore.util.MessageUtil;
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
                MessageUtil.send(sender, "Unpaused the countdown");
            } else {
                gameManager.pause();
                MessageUtil.send(sender,"Paused the countdown");
            }
        }
        return true;
    }
}
