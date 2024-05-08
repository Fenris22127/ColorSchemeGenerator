package de.colorscheme.output;

import com.itextpdf.text.BaseColor;

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
    private static final int ROUND_VALUE = 100_000;

    /**
     *
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
     *
     * @param fgColor
     * @param bgColor
     * @param coefficientLevel
     * @return
     */
    public static boolean isContrastValid(final BaseColor fgColor, final BaseColor bgColor, Float coefficientLevel) {
        return getConstrastRatio(fgColor, bgColor) > coefficientLevel;
    }

    /**
     * This method computes the contrast ratio between 2 colors. It needs to
     * determine which one is lighter first.
     *
     * @param fgColor
     * @param bgColor
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

    public static double getConstrastRatio5DigitRound(final BaseColor fgColor, final BaseColor bgColor) {
        double fgLuminosity = getLuminosity(fgColor);
        double bgLuminosity = getLuminosity(bgColor);
        if (fgLuminosity > bgLuminosity) {
            return (double) Math.round(computeContrast(fgLuminosity, bgLuminosity) * ROUND_VALUE) / ROUND_VALUE;
        } else {
            return (double) Math.round(computeContrast(bgLuminosity, fgLuminosity) * ROUND_VALUE) / ROUND_VALUE;
        }
    }

    /**
     *
     * @param lighter
     * @param darker
     * @return
     */
    private static double computeContrast(double lighter, double darker) {
        return ((lighter + CONTRAST_FACTOR) / (darker + CONTRAST_FACTOR));
    }

    /**
     *
     * @param color
     * @return
     */
    public static double getLuminosity(BaseColor color) {
        return getComposantValue(color.getRed()) * RED_FACTOR
                        + getComposantValue(color.getGreen()) * GREEN_FACTOR
                        + getComposantValue(color.getBlue()) * BLUE_FACTOR;
    }

    /**
     *
     * @param composant
     * @return
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
