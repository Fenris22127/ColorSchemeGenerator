package de.colorscheme.output;

import com.itextpdf.text.BaseColor;

/**
 * The class contains methods to check the contrast between 2 colors. <br>
 * {@see <a href="https://github.com/Tanaguru/Contrast-Finder/blob/master/contrast-finder-utils/src/main/java/org/opens/utils/contrastchecker/ContrastChecker.java">Contrast Finder by Tanaguru on GitHub</a>}
 *
 * @author Slightly altered by Elisa Johanna Woelk | elisa-johanna. woelk@outlook. de | @fenris_22127
 * @version 1.1
 * @since 18.0.1
 */
public final class ContrastChecker {

    private static final double RED_FACTOR = 0.2126;
    private static final double GREEN_FACTOR = 0.7152;
    private static final double BLUE_FACTOR = 0.0722;
    private static final double CONTRAST_FACTOR = 0.05;
    private static final double RGB_MAX_VALUE = 255;
    private static final double RSGB_FACTOR = 0.03928;
    private static final double LUMINANCE_INF = 12.92;
    private static final double LUMINANCE_SUP_CONST = 0.055;
    private static final double LUMINANCE_SUP_CONST2 = 1.055;
    private static final double LUMINANCE_EXP = 2.4;

    /**
     * Private constructor to prevent instantiation
     */
    private ContrastChecker() {
    }

    public static double distanceColor(final BaseColor fgColor, final BaseColor bgColor) {
        int redFg = fgColor.getRed();
        int redBg = bgColor.getRed();
        int greenBg = bgColor.getGreen();
        int greenFg = fgColor.getGreen();
        int blueFg = fgColor.getBlue();
        int blueBg = bgColor.getBlue();
        return (Math.sqrt(Math.pow(redFg - redBg, 2) + Math.pow(greenFg - greenBg, 2) + Math.pow(blueFg - blueBg, 2)));
    }

    /**
     * This method checks if the contrast between 2 colors is valid. It uses the default coefficient level of 4.5.
     * @param fgColor The foreground color
     * @param bgColor The background color
     * @param coefficientLevel The coefficient level
     * @return {@code true} if the contrast is valid, {@code false} otherwise
     */
    public static boolean isContrastValid(final BaseColor fgColor, final BaseColor bgColor, Float coefficientLevel) {
        return getConstrastRatio(fgColor, bgColor) > coefficientLevel;
    }

    /**
     * This method computes the contrast ratio between 2 colors. It needs to
     * determine which one is lighter first.
     *
     * @param fgColor The foreground color
     * @param bgColor The background color
     * @return the contrast ratio between the 2 colors
     */
    public static double getConstrastRatio(final BaseColor fgColor, final BaseColor bgColor) {
        double fgLuminosity = getLuminosity(fgColor);
        double bgLuminosity = getLuminosity(bgColor);
        if (fgLuminosity > bgLuminosity) {
            return computeContrast(fgLuminosity, bgLuminosity);
        } else {
            return computeContrast(bgLuminosity, fgLuminosity);
        }
    }

    /**
     * This method computes the contrast ratio between 2 colors. It needs to determine which one is lighter first.
     * @param lighter The lighter color
     * @param darker The darker color
     * @return the contrast ratio between the 2 colors
     */
    private static double computeContrast(double lighter, double darker) {
        return ((lighter + CONTRAST_FACTOR) / (darker + CONTRAST_FACTOR));
    }

    /**
     * This method computes the luminosity of a color.
     * @param color The color
     * @return The luminosity of the color
     */
    public static double getLuminosity(BaseColor color) {
        return getComposantValue(color.getRed()) * RED_FACTOR
                        + getComposantValue(color.getGreen()) * GREEN_FACTOR
                        + getComposantValue(color.getBlue()) * BLUE_FACTOR;
    }

    /**
     * This method computes the composant value of a color.
     * @param composant The composant
     * @return the composant value of the color
     */
    private static double getComposantValue(double composant) {
        double rsgb = composant / RGB_MAX_VALUE;
        if (rsgb <= RSGB_FACTOR) {
            return rsgb / LUMINANCE_INF;
        } else {
            return Math.pow(((rsgb + LUMINANCE_SUP_CONST) / LUMINANCE_SUP_CONST2), LUMINANCE_EXP);
        }
    }
}
