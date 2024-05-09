package de.colorscheme.utils;

import com.itextpdf.text.BaseColor;

import java.awt.*;

/**
 * A utility class for color operations.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.0
 * @since 18.0.1
 */
public class ColorUtils {
    /**
     * Converts a hex value to an array of RGB values.
     * @param hex A {@link String}: The hex value to convert
     * @return A {@link Float} array: The RGB values of the hex value
     */
    public float[] hexToRgb(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() > 6) {
            throw new IllegalArgumentException("Hex value is too long! Expected length: 6, actual length: " + hex.length());
        }
        float red = Integer.valueOf(hex.substring(0, 2), 16);
        float green = Integer.valueOf(hex.substring(2, 4), 16);
        float blue = Integer.valueOf(hex.substring(4, 6), 16);
        return new float[]{red, green, blue};
    }

    /**
     * Converts a hex value to a {@link Color}.
     * @param hex A {@link String}: The hex value to convert
     * @return A {@link Color}: The color represented by the hex value
     */
    public Color hexToColor(String hex) {
        float[] rgb = hexToRgb(hex);
        return new Color((int) rgb[0], (int) rgb[1], (int) rgb[2]);
    }

    /**
     * Returns the hexadecimal representation of the passed {@link BaseColor}.
     * @param color A {@link BaseColor}: The color to get the hex value of
     * @return A {@link String}: The hexadecimal representation of the passed {@link BaseColor}
     */
    public String getHex(BaseColor color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}
