package com.javacup.model;

import com.javacup.model.command.Command;
import com.javacup.model.engine.GameSituations;
import com.javacup.model.util.Position;

import java.util.List;

/**
 * Main interface for AI tactics.
 * <p>
 * This interface must be implemented by participants to create their own AI tactics
 * for controlling a football team during a match. The tactic is responsible for:
 * <ul>
 *   <li>Defining team configuration (name, players, uniforms)</li>
 *   <li>Making decisions each iteration based on game state</li>
 *   <li>Positioning players during kickoffs</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public interface Tactic {

    /**
     * Returns the team configuration details.
     * <p>
     * This method is called once at match initialization to retrieve
     * team information such as name, coach, player details, and uniform colors.
     * </p>
     *
     * @return TacticDetail object containing team configuration
     */
    TacticDetail getDetail();

    /**
     * Executes the AI logic and returns commands for all players.
     * <p>
     * This is the most important method - it contains the tactical AI logic.
     * It is called every iteration (60 times per second) during the match.
     * The method receives current game state information and must return
     * a list of commands specifying what each player should do.
     * </p>
     *
     * @param gameSituations current game state information
     * @return list of commands for the 11 players to execute in the next iteration
     */
    List<Command> execute(GameSituations gameSituations);

    /**
     * Returns player positions when this team kicks off.
     * <p>
     * Called when the team needs to position players for kickoff (after conceding
     * a goal or at match start). All players must be positioned in their own half
     * of the field.
     * </p>
     *
     * @param gameSituations current game state information
     * @return array of 11 positions, one for each player
     */
    Position[] getStartPositions(GameSituations gameSituations);

    /**
     * Returns player positions when the opponent kicks off.
     * <p>
     * Called when the opponent is taking kickoff (after scoring a goal).
     * Players can be positioned anywhere on the field.
     * </p>
     *
     * @param gameSituations current game state information
     * @return array of 11 positions, one for each player
     */
    Position[] getNoStartPositions(GameSituations gameSituations);
}
