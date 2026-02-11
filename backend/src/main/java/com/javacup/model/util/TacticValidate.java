package com.javacup.model.util;

import com.javacup.model.TacticDetail;
import com.javacup.model.UniformStyle;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for validating tactic configurations.
 * <p>
 * Provides validation for:
 * <ul>
 *   <li>Team configuration (names, colors, players)</li>
 *   <li>Player attributes (credits, ranges)</li>
 *   <li>Player positions during kickoffs</li>
 *   <li>Uniform color conflicts</li>
 * </ul>
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Slf4j
public final class TacticValidate {

    private static final double COLOR_SIMILARITY_THRESHOLD = 150.0;

    private TacticValidate() {
        throw new UnsupportedOperationException("TacticValidate is a utility class");
    }

    /**
     * Validates a tactic's team configuration.
     * <p>
     * Checks:
     * <ul>
     *   <li>All required fields are non-null and non-empty</li>
     *   <li>Exactly 11 players with exactly 1 goalkeeper</li>
     *   <li>Player numbers are unique and in range [1-99]</li>
     *   <li>Player attributes are in range [0-1]</li>
     *   <li>Total credits don't exceed allowed limit</li>
     * </ul>
     * </p>
     *
     * @param name tactic name for error messages
     * @param detail tactic detail to validate
     * @throws Exception if validation fails with descriptive error message
     */
    public static void validateDetail(String name, TacticDetail detail) throws Exception {
        log.debug("Validating tactic detail for: {}", name);
        
        if (detail == null) {
            throw new Exception(name + ": TacticDetail is null");
        }

        // Validate colors
        if (detail.getSocksColor() == null ||
            detail.getShirtColor() == null ||
            detail.getShirtLineColor() == null ||
            detail.getShortsColor() == null ||
            detail.getGoalKeeper() == null) {
            throw new Exception(name + ": One or more uniform colors are null");
        }

        // Validate uniform style
        if (detail.getStyle() == null) {
            throw new Exception(name + ": Uniform style is null");
        }

        // Validate team information
        if (detail.getCoach() == null || detail.getTacticName() == null || detail.getCountry() == null) {
            throw new Exception(name + ": Coach, team name, or country is null");
        }

        if (detail.getCoach().trim().isEmpty() ||
            detail.getTacticName().trim().isEmpty() ||
            detail.getCountry().trim().isEmpty()) {
            throw new Exception(name + ": Coach, team name, or country is empty");
        }

        // Validate players array
        if (detail.getPlayers() == null) {
            throw new Exception(name + ": Players array is null");
        }

        if (detail.getPlayers().length != 11) {
            throw new Exception(name + ": Must have exactly 11 players, found " + 
                detail.getPlayers().length);
        }

        // Validate individual players
        int goalkeepers = 0;
        double totalCredits = 0;
        Set<Integer> usedNumbers = new HashSet<>();

        for (int i = 0; i < 11; i++) {
            var player = detail.getPlayers()[i];
            
            if (player == null) {
                throw new Exception(name + ": Player[" + i + "] is null");
            }

            if (player.getHairColor() == null || player.getSkinColor() == null) {
                throw new Exception(name + ": Player[" + i + "] has null hair or skin color");
            }

            if (player.getPlayerName() == null || player.getPlayerName().trim().isEmpty()) {
                throw new Exception(name + ": Player[" + i + "] has null or empty name");
            }

            // Validate player number
            if (player.getNumber() <= 0 || player.getNumber() > 99) {
                throw new Exception(name + ": Player[" + i + "] has invalid number " + 
                    player.getNumber() + " (must be 1-99)");
            }

            if (usedNumbers.contains(player.getNumber())) {
                throw new Exception(name + ": Player number " + player.getNumber() + 
                    " is used by multiple players");
            }
            usedNumbers.add(player.getNumber());

            // Validate attribute ranges
            if (player.getPrecision() < 0 || player.getPrecision() > 1) {
                throw new Exception(name + ": Player[" + i + "] has precision " + 
                    player.getPrecision() + " outside range [0,1]");
            }

            if (player.getPower() < 0 || player.getPower() > 1) {
                throw new Exception(name + ": Player[" + i + "] has power " + 
                    player.getPower() + " outside range [0,1]");
            }

            if (player.getSpeed() < 0 || player.getSpeed() > 1) {
                throw new Exception(name + ": Player[" + i + "] has speed " + 
                    player.getSpeed() + " outside range [0,1]");
            }

            // Sum up credits
            totalCredits += player.getPrecision();
            totalCredits += player.getSpeed();
            totalCredits += player.getPower();

            if (player.isGoalKeeper()) {
                goalkeepers++;
            }
        }

        // Round credits to 4 decimal places for comparison
        totalCredits = Math.round(totalCredits * 10000) / 10000.0;

        if (goalkeepers != 1) {
            throw new Exception(name + ": Must have exactly 1 goalkeeper, found " + goalkeepers);
        }

        if (totalCredits > Constants.INITIAL_CREDITS) {
            throw new Exception(name + ": Used " + totalCredits + " credits, but only " + 
                Constants.INITIAL_CREDITS + " are allowed");
        }

        // Check if uniforms are too similar (warning, not error)
        if (areUniformsTooSimilar(detail)) {
            log.warn("{}: Primary and alternate uniforms are very similar", name);
        }
        
        log.debug("Tactic detail validation successful for: {}", name);
    }

    /**
     * Validates player positions for kickoff situations.
     * <p>
     * Ensures:
     * <ul>
     *   <li>All 11 positions are provided for both situations</li>
     *   <li>Players are in correct half during kickoff</li>
     *   <li>Receiving players are outside center circle</li>
     * </ul>
     * Returns corrected positions if needed.
     * </p>
     *
     * @param name tactic name for error messages
     * @param startPositions positions when this team kicks off
     * @param noStartPositions positions when opponent kicks off
     * @return array containing [corrected start positions, corrected no-start positions]
     * @throws Exception if validation fails
     */
    public static Position[][] validatePositions(String name, Position[] startPositions, 
                                                  Position[] noStartPositions) throws Exception {
        log.debug("Validating positions for: {}", name);
        
        if (startPositions == null) {
            throw new Exception(name + ": Start positions array is null");
        }
        if (noStartPositions == null) {
            throw new Exception(name + ": No-start positions array is null");
        }
        if (startPositions.length != 11) {
            throw new Exception(name + ": Start positions must have 11 elements, found " + 
                startPositions.length);
        }
        if (noStartPositions.length != 11) {
            throw new Exception(name + ": No-start positions must have 11 elements, found " + 
                noStartPositions.length);
        }

        Position[] correctedStart = new Position[11];
        Position[] correctedNoStart = new Position[11];

        for (int i = 0; i < 11; i++) {
            if (startPositions[i] == null) {
                throw new Exception(name + ": Start position[" + i + "] is null");
            }
            if (noStartPositions[i] == null) {
                throw new Exception(name + ": No-start position[" + i + "] is null");
            }

            // Copy positions
            correctedStart[i] = new Position(startPositions[i]);
            correctedNoStart[i] = new Position(noStartPositions[i]);

            // Clamp start positions to own half (y <= 0)
            if (correctedStart[i].getY() > 0) {
                correctedStart[i] = new Position(correctedStart[i].getX(), 0);
            }

            // Clamp no-start positions to own half (y <= 0)
            if (correctedNoStart[i].getY() > 0) {
                correctedNoStart[i] = new Position(correctedNoStart[i].getX(), 0);
            }

            // Ensure no-start positions are outside center circle
            if (correctedNoStart[i].distance(Constants.FIELD_CENTER) <= 
                Constants.CENTER_CIRCLE_RADIUS) {
                double angle = Constants.FIELD_CENTER.angle(correctedNoStart[i]);
                correctedNoStart[i] = Constants.FIELD_CENTER
                    .moveAngle(angle, Constants.CENTER_CIRCLE_RADIUS + 1);
            }
        }

        log.debug("Position validation successful for: {}", name);
        return new Position[][]{correctedStart, correctedNoStart};
    }

    /**
     * Determines if visiting team should use alternate uniform.
     * <p>
     * Compares home team's primary uniform with visitor's primary and alternate uniforms.
     * Returns true if alternate uniform has better contrast.
     * </p>
     *
     * @param home home team's tactic detail
     * @param visitor visiting team's tactic detail
     * @return true if visitor should use alternate uniform
     */
    public static boolean shouldUseAlternateUniform(TacticDetail home, TacticDetail visitor) {
        Color homeColor = blendColors(
            home.getShirtColor(),
            home.getShirtLineColor(),
            getStyleBlendFactor(home.getStyle())
        );

        Color visitorPrimaryColor = blendColors(
            visitor.getShirtColor(),
            visitor.getShirtLineColor(),
            getStyleBlendFactor(visitor.getStyle())
        );

        Color visitorAlternateColor = blendColors(
            visitor.getShirtColor2(),
            visitor.getShirtLineColor2(),
            getStyleBlendFactor(visitor.getStyle2())
        );

        double primaryDistance = colorDistance(homeColor, visitorPrimaryColor);
        double alternateDistance = colorDistance(homeColor, visitorAlternateColor);

        boolean useAlternate = primaryDistance < COLOR_SIMILARITY_THRESHOLD && 
                               alternateDistance > primaryDistance;
        
        if (useAlternate) {
            log.info("Visitor should use alternate uniform for better contrast");
        }
        
        return useAlternate;
    }

    /**
     * Checks if a team's two uniforms are too similar.
     *
     * @param detail tactic detail to check
     * @return true if uniforms are too similar
     */
    public static boolean areUniformsTooSimilar(TacticDetail detail) {
        Color primaryColor = blendColors(
            detail.getShirtColor(),
            detail.getShirtLineColor(),
            getStyleBlendFactor(detail.getStyle())
        );

        Color alternateColor = blendColors(
            detail.getShirtColor2(),
            detail.getShirtLineColor2(),
            getStyleBlendFactor(detail.getStyle2())
        );

        double distance = colorDistance(primaryColor, alternateColor);
        return distance < COLOR_SIMILARITY_THRESHOLD;
    }

    /**
     * Calculates color distance in RGB space.
     *
     * @param c1 first color
     * @param c2 second color
     * @return Euclidean distance between colors
     */
    private static double colorDistance(Color c1, Color c2) {
        double dr = c1.getRed() - c2.getRed();
        double dg = c1.getGreen() - c2.getGreen();
        double db = c1.getBlue() - c2.getBlue();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * Blends two colors based on a blend factor.
     *
     * @param c1 primary color
     * @param c2 secondary color
     * @param p1 blend factor for c1 (0.0-1.0)
     * @return blended color
     */
    private static Color blendColors(Color c1, Color c2, double p1) {
        double p2 = 1.0 - p1;
        int r = (int) (c1.getRed() * p1 + c2.getRed() * p2);
        int g = (int) (c1.getGreen() * p1 + c2.getGreen() * p2);
        int b = (int) (c1.getBlue() * p1 + c2.getBlue() * p2);
        return new Color(r, g, b);
    }

    /**
     * Gets blend factor based on uniform style.
     * <p>
     * Determines how much of the primary color vs line color is visible.
     * </p>
     *
     * @param style uniform style
     * @return blend factor (0.5-1.0)
     */
    private static double getStyleBlendFactor(UniformStyle style) {
        return switch (style) {
            case HORIZONTAL_LINES, VERTICAL_LINES -> 0.5;
            case VERTICAL_STRIPE, HORIZONTAL_STRIPE, DIAGONAL_STRIPE -> 0.8;
            case PLAIN -> 1.0;
        };
    }
}
