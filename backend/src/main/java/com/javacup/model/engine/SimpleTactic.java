package com.javacup.model.engine;

import com.javacup.model.PlayerDetail;
import com.javacup.model.Tactic;
import com.javacup.model.TacticDetail;
import com.javacup.model.UniformStyle;
import com.javacup.model.command.Command;
import com.javacup.model.command.CommandHitBall;
import com.javacup.model.command.CommandMoveTo;
import com.javacup.model.util.Constants;
import com.javacup.model.util.Position;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple sample tactic for testing purposes.
 * <p>
 * This tactic implements basic football behavior:
 * <ul>
 *   <li>When ball is close, try to kick it toward opponent's goal</li>
 *   <li>When ball is far, move toward it</li>
 *   <li>Players try to maintain formation</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public class SimpleTactic implements Tactic {

    private final String teamName;
    private final Color primaryColor;
    private final Color secondaryColor;

    /**
     * Creates a simple tactic with given name and colors.
     *
     * @param teamName name of the team
     * @param primaryColor primary uniform color
     * @param secondaryColor secondary uniform color
     */
    public SimpleTactic(String teamName, Color primaryColor, Color secondaryColor) {
        this.teamName = teamName;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    @Override
    public TacticDetail getDetail() {
        return new TacticDetail() {
            @Override
            public String getTacticName() {
                return teamName;
            }

            @Override
            public String getCountry() {
                return "Test Country";
            }

            @Override
            public String getCoach() {
                return "Test Coach";
            }

            @Override
            public Color getShirtColor() {
                return primaryColor;
            }

            @Override
            public Color getShortsColor() {
                return primaryColor.darker();
            }

            @Override
            public Color getSocksColor() {
                return primaryColor.darker();
            }

            @Override
            public Color getShirtLineColor() {
                return secondaryColor;
            }

            @Override
            public Color getGoalKeeper() {
                return Color.YELLOW;
            }

            @Override
            public UniformStyle getStyle() {
                return UniformStyle.PLAIN;
            }

            @Override
            public PlayerDetail[] getPlayers() {
                PlayerDetail[] players = new PlayerDetail[11];
                
                // Goalkeeper
                players[0] = createPlayer("GK", 1, true, 0.5, 0.5, 0.5);
                
                // Defenders
                players[1] = createPlayer("LB", 2, false, 0.6, 0.4, 0.4);
                players[2] = createPlayer("CB1", 3, false, 0.5, 0.5, 0.4);
                players[3] = createPlayer("CB2", 4, false, 0.5, 0.5, 0.4);
                players[4] = createPlayer("RB", 5, false, 0.6, 0.4, 0.4);
                
                // Midfielders
                players[5] = createPlayer("LM", 6, false, 0.7, 0.4, 0.5);
                players[6] = createPlayer("CM1", 7, false, 0.6, 0.5, 0.5);
                players[7] = createPlayer("CM2", 8, false, 0.6, 0.5, 0.5);
                players[8] = createPlayer("RM", 9, false, 0.7, 0.4, 0.5);
                
                // Forwards
                players[9] = createPlayer("LF", 10, false, 0.7, 0.6, 0.6);
                players[10] = createPlayer("RF", 11, false, 0.7, 0.6, 0.6);
                
                return players;
            }

            @Override
            public Color getShirtColor2() {
                return secondaryColor;
            }

            @Override
            public Color getShortsColor2() {
                return secondaryColor.darker();
            }

            @Override
            public Color getSocksColor2() {
                return secondaryColor.darker();
            }

            @Override
            public Color getShirtLineColor2() {
                return primaryColor;
            }

            @Override
            public Color getGoalKeeper2() {
                return Color.GREEN;
            }

            @Override
            public UniformStyle getStyle2() {
                return UniformStyle.VERTICAL_STRIPE;
            }
        };
    }

    @Override
    public List<Command> execute(GameSituations game) {
        List<Command> commands = new ArrayList<>();
        
        Position ball = game.ballPosition();
        Position[] myPlayers = game.myPlayers();
        int[] canKick = game.canKick();
        
        // Try to kick if able
        for (int kickerIndex : canKick) {
            Position playerPos = myPlayers[kickerIndex];
            
            // Calculate angle to opponent's goal
            Position opponentGoal = new Position(0, Constants.FIELD_LENGTH / 2);
            double angleToGoal = Math.toDegrees(playerPos.angle(opponentGoal));
            
            // Kick toward goal with medium power
            commands.add(new CommandHitBall(kickerIndex, angleToGoal, 0.7, 30));
            break; // Only one kick per iteration
        }
        
        // Move players toward strategic positions
        for (int i = 0; i < 11; i++) {
            Position target = calculateTargetPosition(i, ball, myPlayers);
            
            // Decide whether to sprint based on distance to ball
            double distanceToBall = myPlayers[i].distance(ball);
            boolean sprint = distanceToBall < 20 && game.getMyPlayerEnergy(i) > 0.6;
            
            commands.add(new CommandMoveTo(i, target, sprint));
        }
        
        return commands;
    }

    @Override
    public Position[] getStartPositions(GameSituations game) {
        // 4-4-2 formation starting positions (home team kicks off - forwards at center)
        return new Position[]{
            new Position(0, -45),      // GK
            new Position(-20, -30),    // LB
            new Position(-10, -35),    // CB1
            new Position(10, -35),     // CB2
            new Position(20, -30),     // RB
            new Position(-25, -15),    // LM
            new Position(-10, -10),    // CM1
            new Position(10, -10),     // CM2
            new Position(25, -15),     // RM
            new Position(-0.5, -0.5),  // LF (very close to ball for immediate kick)
            new Position(0.5, -0.5)    // RF (very close to ball for immediate kick)
        };
    }

    @Override
    public Position[] getNoStartPositions(GameSituations game) {
        // 4-4-2 formation receiving positions (outside center circle)
        return new Position[]{
            new Position(0, -45),      // GK
            new Position(-20, -30),    // LB
            new Position(-10, -35),    // CB1
            new Position(10, -35),     // CB2
            new Position(20, -30),     // RB
            new Position(-25, -15),    // LM
            new Position(-10, -15),    // CM1
            new Position(10, -15),     // CM2
            new Position(25, -15),     // RM
            new Position(-10, -5),     // LF
            new Position(10, -5)       // RF
        };
    }

    /**
     * Calculates target position for a player based on game situation.
     */
    private Position calculateTargetPosition(int playerIndex, Position ball, Position[] myPlayers) {
        Position currentPos = myPlayers[playerIndex];
        
        // Goalkeeper stays near goal
        if (playerIndex == 0) {
            double targetY = Math.max(-Constants.FIELD_LENGTH / 2 + 5, 
                                     Math.min(-Constants.FIELD_LENGTH / 2 + 15, ball.getY() - 35));
            double targetX = Math.max(-10, Math.min(10, ball.getX()));
            return new Position(targetX, targetY);
        }
        
        // Defenders stay in defensive third
        if (playerIndex >= 1 && playerIndex <= 4) {
            double baseY = -Constants.FIELD_LENGTH / 3;
            double targetY = Math.max(-Constants.FIELD_LENGTH / 2 + 10,
                                     Math.min(baseY, ball.getY() - 10));
            
            double spacing = Constants.FIELD_WIDTH / 5;
            double targetX = (playerIndex - 2.5) * spacing;
            targetX = Math.max(-Constants.FIELD_WIDTH / 2 + 5,
                              Math.min(Constants.FIELD_WIDTH / 2 - 5, targetX));
            
            return new Position(targetX, targetY);
        }
        
        // Midfielders follow the ball more closely
        if (playerIndex >= 5 && playerIndex <= 8) {
            double targetY = Math.max(-Constants.FIELD_LENGTH / 3,
                                     Math.min(0, ball.getY() - 5));
            
            double spacing = Constants.FIELD_WIDTH / 5;
            double targetX = (playerIndex - 6.5) * spacing;
            targetX = Math.max(-Constants.FIELD_WIDTH / 2 + 5,
                              Math.min(Constants.FIELD_WIDTH / 2 - 5, targetX));
            
            return new Position(targetX, targetY);
        }
        
        // Forwards move toward ball aggressively
        double targetX = ball.getX() + (playerIndex == 9 ? -5 : 5);
        double targetY = Math.max(-5, Math.min(Constants.FIELD_LENGTH / 3, ball.getY() + 5));
        
        return new Position(targetX, targetY);
    }

    /**
     * Helper to create a player with given attributes.
     */
    private PlayerDetail createPlayer(String name, int number, boolean isGoalkeeper,
                                     double speed, double power, double precision) {
        return new PlayerDetail() {
            @Override
            public String getPlayerName() { return name; }
            
            @Override
            public Color getSkinColor() { return new Color(255, 220, 177); }
            
            @Override
            public Color getHairColor() { return Color.BLACK; }
            
            @Override
            public int getNumber() { return number; }
            
            @Override
            public boolean isGoalKeeper() { return isGoalkeeper; }
            
            @Override
            public double getSpeed() { return speed; }
            
            @Override
            public double getPower() { return power; }
            
            @Override
            public double getPrecision() { return precision; }
        };
    }
}
