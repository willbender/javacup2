package com.javacup.model.command;

/**
 * Abstract base class for player commands.
 * <p>
 * Commands specify actions that players should execute during each iteration.
 * Each command targets a specific player (by index 0-10) and defines what
 * that player should do.
 * </p>
 * <p>
 * Two types of commands are available:
 * <ul>
 *   <li>{@link CommandMoveTo} - directs player movement</li>
 *   <li>{@link CommandHitBall} - directs ball kicking</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public abstract class Command {

    /**
     * Enumeration of available command types.
     */
    public enum CommandType {
        /**
         * Move to a position command type.
         */
        MOVE_TO,
        
        /**
         * Hit/kick the ball command type.
         */
        HIT_BALL
    }

    /**
     * Returns the type of this command.
     *
     * @return command type
     */
    public abstract CommandType getCommandType();

    /**
     * Returns the index of the player that should execute this command.
     *
     * @return player index (0-10)
     */
    public abstract int getPlayerIndex();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Command)) {
            return false;
        }
        Command c = (Command) obj;
        return c.getCommandType() == getCommandType() && c.getPlayerIndex() == getPlayerIndex();
    }

    @Override
    public int hashCode() {
        int result = getCommandType() != null ? getCommandType().hashCode() : 0;
        result = 31 * result + getPlayerIndex();
        return result;
    }

    @Override
    public String toString() {
        if (getCommandType().equals(CommandType.HIT_BALL)) {
            return "HitBall(" + getPlayerIndex() + ")";
        } else if (getCommandType().equals(CommandType.MOVE_TO)) {
            return "MoveTo(" + getPlayerIndex() + ")";
        } else {
            return "Unknown";
        }
    }
}
