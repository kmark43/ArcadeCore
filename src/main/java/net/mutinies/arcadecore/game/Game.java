package net.mutinies.arcadecore.game;

import net.mutinies.arcadecore.game.damage.DamageManager;
import net.mutinies.arcadecore.game.damage.DefaultDamageManager;
import net.mutinies.arcadecore.game.kit.DefaultKitManager;
import net.mutinies.arcadecore.game.kit.KitManager;
import net.mutinies.arcadecore.game.map.MapManager;
import net.mutinies.arcadecore.game.module.ModuleManager;
import net.mutinies.arcadecore.game.projectile.ProjectileManager;
import net.mutinies.arcadecore.game.scoreboard.ScoreboardManager;
import net.mutinies.arcadecore.game.spec.SpectateManager;
import net.mutinies.arcadecore.game.state.GameStateManager;
import net.mutinies.arcadecore.game.team.TeamManager;
import net.mutinies.arcadecore.modules.gamescore.EndHandler;

import java.util.Objects;

public class Game {
    private String name;
    private String displayName;
    private String acronym;
    private int minPlayers;
    private int maxPlayers;
    
    private KitManager kitManager;
    private TeamManager teamManager;
    private MapManager mapManager;
    private DamageManager damageManager;
    private SpectateManager spectateManager;
    private ModuleManager moduleManager;
    private GameStateManager gameStateManager;
    private ProjectileManager projectileManager;
    private ScoreboardManager scoreboardManager;
    private EndHandler endHandler;
    
    public Game(String name, String displayName, String acronym, int minPlayers, int maxPlayers) {
        this.name = Objects.requireNonNull(name);
        this.displayName = Objects.requireNonNull(displayName);
        this.acronym = Objects.requireNonNull(acronym);
        
        if (minPlayers <= 0 || minPlayers > maxPlayers) {
            throw new IllegalArgumentException("Min players and max players don't allow for anyone to join");
        }
        
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        
        kitManager = new DefaultKitManager(this);
        teamManager = new TeamManager(this);
        mapManager = new MapManager(this);
        damageManager = new DefaultDamageManager(this);
        moduleManager = new ModuleManager(this);
        spectateManager = new SpectateManager(this);
        projectileManager = new ProjectileManager(this);
        scoreboardManager = new ScoreboardManager(this);
        
        gameStateManager = new GameStateManager(this);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getAcronym() {
        return acronym;
    }
    
    public int getMinPlayers() {
        return minPlayers;
    }
    
    public int getMaxPlayers() {
        return maxPlayers;
    }
    
    public KitManager getKitManager() {
        return kitManager;
    }
    
    public TeamManager getTeamManager() {
        return teamManager;
    }
    
    public MapManager getMapManager() {
        return mapManager;
    }
    
    public DamageManager getDamageManager() {
        return damageManager;
    }
    
    public SpectateManager getSpectateManager() {
        return spectateManager;
    }
    
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    public ProjectileManager getProjectileManager() {
        return projectileManager;
    }
    
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
    
    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }
    
    public EndHandler getEndHandler() {
        return endHandler;
    }
    
    public void setEndHandler(EndHandler endHandler) {
        this.endHandler = endHandler;
    }
}
