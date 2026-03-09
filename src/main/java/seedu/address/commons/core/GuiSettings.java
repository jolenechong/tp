package seedu.address.commons.core;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.Serializable;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * A Serializable class that contains the GUI settings.
 * Guarantees: immutable.
 */
public class GuiSettings implements Serializable {

    public static final double MIN_HEIGHT = 650;
    public static final double MIN_WIDTH = 900;

    // default to the screen size of the user's device
    private static final boolean DEFAULT_FULL_SCREEN = true;
    private static final Dimension screenSize;
    static {
        if (GraphicsEnvironment.isHeadless()) {
            screenSize = new Dimension((int) MIN_WIDTH, (int) MIN_HEIGHT);
        } else {
            screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        }
    }
    private static final double DEFAULT_HEIGHT = screenSize.getHeight();
    private static final double DEFAULT_WIDTH = screenSize.getWidth();

    private final double windowWidth;
    private final double windowHeight;
    private final Point windowCoordinates;
    private final boolean isFullScreen;

    /**
     * Constructs a {@code GuiSettings} with the default height, width and position.
     */
    public GuiSettings() {
        windowWidth = DEFAULT_WIDTH;
        windowHeight = DEFAULT_HEIGHT;
        windowCoordinates = null; // null represent no coordinates
        isFullScreen = DEFAULT_FULL_SCREEN;
    }

    /**
     * Constructs a {@code GuiSettings} with the specified height, width and position.
     */
    public GuiSettings(double windowWidth, double windowHeight, int xPosition, int yPosition) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        windowCoordinates = new Point(xPosition, yPosition);

        if (windowWidth >= DEFAULT_WIDTH && windowHeight >= DEFAULT_HEIGHT) {
            isFullScreen = true;
        } else {
            isFullScreen = false;
        }
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public Point getWindowCoordinates() {
        return windowCoordinates != null ? new Point(windowCoordinates) : null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GuiSettings)) {
            return false;
        }

        GuiSettings otherGuiSettings = (GuiSettings) other;
        return windowWidth == otherGuiSettings.windowWidth
                && windowHeight == otherGuiSettings.windowHeight
                && Objects.equals(windowCoordinates, otherGuiSettings.windowCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowWidth, windowHeight, windowCoordinates);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("windowWidth", windowWidth)
                .add("windowHeight", windowHeight)
                .add("windowCoordinates", windowCoordinates)
                .toString();
    }
}
