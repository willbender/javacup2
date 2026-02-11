package com.javacup.model;

/**
 * Enumeration of uniform styles for player jerseys.
 * <p>
 * This enum defines the visual appearance patterns available for team uniforms.
 * Each style determines how the shirt line color is applied to the jersey.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
public enum UniformStyle {

    /**
     * Jersey with a wide horizontal stripe across the chest.
     */
    HORIZONTAL_STRIPE(1, "Horizontal Stripe"),

    /**
     * Jersey with a wide vertical stripe down the center.
     */
    VERTICAL_STRIPE(2, "Vertical Stripe"),

    /**
     * Jersey with a diagonal stripe from shoulder to hip.
     */
    DIAGONAL_STRIPE(3, "Diagonal Stripe"),

    /**
     * Jersey with multiple thin horizontal lines.
     */
    HORIZONTAL_LINES(4, "Horizontal Lines"),

    /**
     * Jersey with multiple thin vertical lines.
     */
    VERTICAL_LINES(5, "Vertical Lines"),

    /**
     * Plain solid color jersey without patterns.
     */
    PLAIN(6, "Plain");

    private final int number;
    private final String displayName;

    /**
     * Creates a uniform style with the given number and display name.
     *
     * @param number internal number for resource loading
     * @param displayName human-readable name
     */
    UniformStyle(int number, String displayName) {
        this.number = number;
        this.displayName = displayName;
    }

    /**
     * Gets the internal number associated with this style.
     * <p>
     * Used for loading visual resources.
     * </p>
     *
     * @return style number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the display name of this uniform style.
     *
     * @return human-readable style name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
