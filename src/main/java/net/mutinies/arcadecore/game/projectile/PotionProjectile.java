package net.mutinies.arcadecore.game.projectile;

import org.bukkit.entity.ThrownPotion;

import java.util.ArrayList;
import java.util.List;

public class PotionProjectile extends ListeningProjectile {
    private List<PotionSplashListener> potionSplashHandlers;
    
    public PotionProjectile(ThrownPotion potion) {
        super(potion);
        potionSplashHandlers = new ArrayList<>();
    }
    
    public void addPotionSplashListener(PotionSplashListener potionSplashListener) {
        potionSplashHandlers.add(potionSplashListener);
    }
    
    public List<PotionSplashListener> getPotionSplashHandlers() {
        return new ArrayList<>(potionSplashHandlers);
    }
}
