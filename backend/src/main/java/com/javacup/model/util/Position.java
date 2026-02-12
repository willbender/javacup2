package com.javacup.model.util;

import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Class representing 2D spatial coordinates on the football field.
 * <p>
 * The coordinate system uses:
 * <ul>
 *   <li>Origin (0,0) at field center</li>
 *   <li>X-axis: -52.5 to +52.5 meters (left to right)</li>
 *   <li>Y-axis: -34 to +34 meters (bottom to top)</li>
 * </ul>
 * This class is immutable - all operations return new Position instances.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
public final class Position implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * X coordinate in meters.
     */
    private final double x;
    
    /**
     * Y coordinate in meters.
     */
    private final double y;
    
    private static final Position ZERO = new Position(0, 0);

    /**
     * Creates a new position at the origin (0,0).
     */
    public Position() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Creates a new position by copying from another position.
     *
     * @param position position to copy
     */
    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    /**
     * Creates a new position with given coordinates.
     *
     * @param x horizontal coordinate in meters
     * @param y vertical coordinate in meters
     */
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Moves the position in a specified angle and distance.
     *
     * @param angle angle in radians (0 = right, π/2 = up)
     * @param distance distance to move in meters
     * @return new position after movement
     */
    public Position moveAngle(double angle, double distance) {
        return new Position(
            x + Math.cos(angle) * distance,
            y + Math.sin(angle) * distance
        );
    }

    /**
     * Moves the position in a specified angle and distance, limited by maxDistance.
     *
     * @param angle angle in radians
     * @param distance desired distance in meters
     * @param maxDistance maximum distance allowed in meters
     * @return new position after movement
     */
    public Position moveAngle(double angle, double distance, double maxDistance) {
        double actualDistance = Math.min(distance, maxDistance);
        return new Position(
            x + Math.cos(angle) * actualDistance,
            y + Math.sin(angle) * actualDistance
        );
    }

    /**
     * Moves the position by delta values.
     *
     * @param dx change in x coordinate
     * @param dy change in y coordinate
     * @return new position after movement
     */
    public Position movePosition(double dx, double dy) {
        return new Position(x + dx, y + dy);
    }

    /**
     * Moves the position by delta values, limited by maximum distance.
     *
     * @param dx change in x coordinate
     * @param dy change in y coordinate
     * @param maxDistance maximum distance allowed
     * @return new position after movement
     */
    public Position movePosition(double dx, double dy, double maxDistance) {
        Position dest = new Position(x + dx, y + dy);
        double angle = angle(dest);
        double distance = distance(dest);
        double actualDistance = Math.min(distance, maxDistance);
        return new Position(
            x + Math.cos(angle) * actualDistance,
            y + Math.sin(angle) * actualDistance
        );
    }

    /**
     * Gets the inverted position (reflected through origin).
     *
     * @return new position with coordinates (-x, -y)
     */
    public Position getInvertedPosition() {
        return new Position(-x, -y);
    }

    /**
     * Checks if position is inside the game field boundaries.
     *
     * @param margin additional margin in meters (can be negative)
     * @return true if position is within field boundaries plus margin
     */
    public boolean isInsideGameField(double margin) {
        double mx = Constants.FIELD_WIDTH / 2 + margin;
        double my = Constants.FIELD_LENGTH / 2 + margin;
        return Math.abs(x) <= mx && Math.abs(y) <= my;
    }

    /**
     * Clamps the position to be within the game field boundaries.
     *
     * @return new position clamped to field boundaries
     */
    public Position setInsideGameField() {
        double mx = Constants.FIELD_WIDTH / 2;
        double my = Constants.FIELD_LENGTH / 2;
        double newX = Math.max(-mx, Math.min(mx, x));
        double newY = Math.max(-my, Math.min(my, y));
        return new Position(newX, newY);
    }

    /**
     * Calculates angle from this position to another position.
     *
     * @param p target position
     * @return angle in radians (range: -π to π)
     */
    public double angle(Position p) {
        double dx = p.x - x;
        double dy = p.y - y;
        return Math.atan2(dy, dx);
    }

    /**
     * Calculates angle from origin to this position.
     *
     * @return angle in radians
     */
    public double angle() {
        return ZERO.angle(this);
    }

    /**
     * Calculates Euclidean distance to another position.
     *
     * @param p target position
     * @return distance in meters
     */
    public double distance(Position p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates distance from origin to this position.
     *
     * @return distance in meters
     */
    public double distance() {
        return ZERO.distance(this);
    }

    /**
     * Calculates squared distance (norm) for fast distance comparison.
     * <p>
     * This method is faster than {@link #distance()} because it avoids
     * the square root calculation. Useful when only comparing distances.
     * </p>
     *
     * @return squared distance (x² + y²)
     */
    public double norm() {
        return x * x + y * y;
    }

    /**
     * Calculates squared distance to another position for fast comparison.
     *
     * @param p target position
     * @return squared distance
     */
    public double norm(Position p) {
        double dx = p.x - x;
        double dy = p.y - y;
        return dx * dx + dy * dy;
    }

    /**
     * Finds the index of the nearest position from an array.
     *
     * @param positions array of positions to search
     * @return index of nearest position, or -1 if array is empty
     */
    public int nearestIndex(Position[] positions) {
        double minDistance = Double.MAX_VALUE;
        int nearestIdx = -1;
        
        for (int i = 0; i < positions.length; i++) {
            double dist = norm(positions[i]);
            if (dist < minDistance) {
                minDistance = dist;
                nearestIdx = i;
            }
        }
        return nearestIdx;
    }

    /**
     * Finds the index of the nearest position, excluding specified indices.
     *
     * @param positions array of positions to search
     * @param exclude indices to exclude from search
     * @return index of nearest position (excluding specified ones), or -1 if none found
     */
    public int nearestIndex(Position[] positions, int... exclude) {
        double minDistance = Double.MAX_VALUE;
        int nearestIdx = -1;
        
        for (int i = 0; i < positions.length; i++) {
            boolean isExcluded = false;
            for (int excludeIdx : exclude) {
                if (excludeIdx == i) {
                    isExcluded = true;
                    break;
                }
            }
            
            if (!isExcluded) {
                double dist = norm(positions[i]);
                if (dist < minDistance) {
                    minDistance = dist;
                    nearestIdx = i;
                }
            }
        }
        return nearestIdx;
    }

    /**
     * Returns array of indices sorted by distance (nearest to farthest).
     *
     * @param positions array of positions
     * @return sorted array of indices
     */
    public int[] nearestIndexes(Position[] positions) {
        int[] indices = new int[positions.length];
        double[] distances = new double[positions.length];
        
        for (int i = 0; i < positions.length; i++) {
            indices[i] = i;
            distances[i] = this.norm(positions[i]);
        }
        
        // Bubble sort by distance
        for (int i = 0; i < positions.length; i++) {
            for (int j = i + 1; j < positions.length; j++) {
                if (distances[i] > distances[j]) {
                    // Swap distances
                    double tempDist = distances[j];
                    distances[j] = distances[i];
                    distances[i] = tempDist;
                    // Swap indices
                    int tempIdx = indices[j];
                    indices[j] = indices[i];
                    indices[i] = tempIdx;
                }
            }
        }
        return indices;
    }

    /**
     * Returns array of indices sorted by distance, excluding specified indices.
     *
     * @param positions array of positions
     * @param exclude indices to exclude
     * @return sorted array of indices (excluding specified ones at end)
     */
    public int[] nearestIndexes(Position[] positions, int... exclude) {
        int[] indices = new int[positions.length];
        double[] distances = new double[positions.length];
        
        for (int i = 0; i < positions.length; i++) {
            indices[i] = i;
            boolean isExcluded = false;
            for (int excludeIdx : exclude) {
                if (excludeIdx == i) {
                    distances[i] = Double.MAX_VALUE;
                    isExcluded = true;
                    break;
                }
            }
            if (!isExcluded) {
                distances[i] = this.norm(positions[i]);
            }
        }
        
        // Bubble sort by distance
        for (int i = 0; i < positions.length; i++) {
            for (int j = i + 1; j < positions.length; j++) {
                if (distances[i] > distances[j]) {
                    double tempDist = distances[j];
                    distances[j] = distances[i];
                    distances[i] = tempDist;
                    int tempIdx = indices[j];
                    indices[j] = indices[i];
                    indices[i] = tempIdx;
                }
            }
        }
        return indices;
    }

    /**
     * Calculates the intersection point of two lines.
     * <p>
     * Line 1 passes through points n1 and n2.
     * Line 2 passes through points m1 and m2.
     * </p>
     *
     * @param n1 first point of line 1
     * @param n2 second point of line 1
     * @param m1 first point of line 2
     * @param m2 second point of line 2
     * @return intersection point, or null if lines are parallel
     */
    public static Position intersection(Position n1, Position n2, Position m1, Position m2) {
        double a1 = n1.y - n2.y;
        double b1 = n2.x - n1.x;
        double c1 = n1.x * n2.y - n2.x * n1.y;

        double a2 = m1.y - m2.y;
        double b2 = m2.x - m1.x;
        double c2 = m1.x * m2.y - m2.x * m1.y;

        double d = a1 * b2 - a2 * b1;
        
        if (d != 0) {
            double d1 = -c1 * b2 + c2 * b1;
            double d2 = -a1 * c2 + a2 * c1;
            return new Position(d1 / d, d2 / d);
        } else {
            return null; // Lines are parallel
        }
    }

    /**
     * Calculates the midpoint between two positions.
     *
     * @param p1 first position
     * @param p2 second position
     * @return midpoint position
     */
    public static Position midpoint(Position p1, Position p2) {
        return new Position((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return Double.compare(position.x, x) == 0 &&
               Double.compare(position.y, y) == 0;
    }

    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        return (int) (xBits ^ (xBits >>> 32) ^ yBits ^ (yBits >>> 32));
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
