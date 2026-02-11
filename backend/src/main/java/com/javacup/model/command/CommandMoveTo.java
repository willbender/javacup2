package com.javacup.model.command;

import com.javacup.model.util.Position;
import lombok.Getter;

/**
 * Command that directs a player to move toward a target position.
 * <p>
 * This command specifies:
 * <ul>
 *   <li>Which player should move (by index 0-10)</li>
 *   <li>Where the player should move to (target position)</li>
 *   <li>Whether the player should sprint (faster but uses more energy)</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Sprint Mode:</strong> When enabled, movement speed is multiplied by 1.2×
 * but requires minimum energy of 0.8 and costs 0.02 energy per iteration.
 * This is a strategic trade-off between speed now versus stamina later.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
public final class CommandMoveTo extends Command {
    
    /**
     * Index of the player that will execute this command (0-10).
     */
    private final int playerIndex;
    
    /**
     * Target position where the player should move.
     */
    private final Position moveTo;
    
    /**
     * Flag indicating if the player should sprint.
     * Sprint provides 1.2× speed boost but costs energy.
     */
    private final boolean sprint;

    /**
     * Creates a move command without sprint.
     *
     * @param playerIndex index of the player (0-10)
     * @param moveTo target position to move toward
     */
    public CommandMoveTo(int playerIndex, Position moveTo) {
        this.playerIndex = playerIndex;
        this.moveTo = moveTo;
        this.sprint = false;
    }
    
    /**
     * Creates a move command with optional sprint.
     *
     * @param playerIndex index of the player (0-10)
     * @param moveTo target position to move toward
     * @param sprint true to enable sprint mode, false for normal movement
     */
    public CommandMoveTo(int playerIndex, Position moveTo, boolean sprint) {
        this.playerIndex = playerIndex;
        this.moveTo = moveTo;
        this.sprint = sprint;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MOVE_TO;
    }
    
    @Override
    public String toString() {
        return String.format("MoveTo(player=%d, pos=%s, sprint=%b)", 
            playerIndex, moveTo, sprint);
    }
}
