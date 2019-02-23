package net.mutinies.arcadecore.scoreboard.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;

public class WrapperPlayServerScoreboardDisplayObjective extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;
    
    /**
     * Enumeration of all the possible scoreboard positions.
     * @author Kristian
     */
    public static class Positions extends IntEnum {
        public static final int LIST = 0;
        public static final int SIDEBAR = 1;
        public static final int BELOW_NAME = 2;
        
        private static final Positions INSTANCE = new Positions();
        
        public static Positions getInstance() {
            return INSTANCE;
        }
    }
    
    public WrapperPlayServerScoreboardDisplayObjective() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerScoreboardDisplayObjective(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve the {@link Positions} of the scoreboard.
     * @return The current Position
     */
    public byte getPosition() {
        return handle.getIntegers().read(0).byteValue();
    }
    
    /**
     * Set the {@link Positions} of the scoreboard.
     * @param value - new value.
     */
    public void setPosition(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve the unique name for the scoreboard to be displayed..
     * @return The current Score Name
     */
    public String getScoreName() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set the unique name for the scoreboard to be displayed..
     * @param value - new value.
     */
    public void setScoreName(String value) {
        handle.getStrings().write(0, value);
    }
}