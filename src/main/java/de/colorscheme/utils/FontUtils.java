package de.colorscheme.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

import java.awt.*;
import java.util.Objects;

import static de.colorscheme.utils.PathUtils.getFontBase;

/**
 * A utility class for font operations.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.0
 * @since 18.0.1
 */
public class FontUtils {
    /**
     * Returns the base {@link Font}.
     * @return A {@link Font}: The base font
     */
    public Font getMulish() {
        return FontFactory.getFont(Objects.requireNonNull(getClass().getResource(getFontBase() + "Mulish-Regular.ttf")).toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns the {@link Font} 'Mulish ExtraBold'.
     * @return A {@link Font}: The font 'Mulish ExtraBold'
     */
    public Font getMulishExtraBold() {
        return FontFactory.getFont(Objects.requireNonNull(getClass().getResource(getFontBase() + "Mulish-ExtraBold.ttf")).toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 32, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns the {@link Font} 'Quattrocento Sans Regular'.
     * @return A {@link Font}: The font 'Quattrocento Sans Regular'
     */
    public Font getQuattrocentoSansRegular() {
        return FontFactory.getFont(Objects.requireNonNull(getClass().getResource(getFontBase() + "QuattrocentoSans-Regular.ttf")).toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns the {@link Font} 'Quattrocento Sans Bold'.
     * @return A {@link Font}: The font 'Quattrocento Sans Bold'
     */
    public Font getQuattrocentoSansBold() {
        return FontFactory.getFont(Objects.requireNonNull(getClass().getResource(getFontBase() + "QuattrocentoSans-Bold.ttf")).toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns a {@link Font} with the passed parameters.
     * @param fontSize A {@link Float}: The size of the font
     * @param fontWeight A {@link String}: The weight of the font
     * @param color A {@link Color}: The color of the font
     * @return A {@link Font}: The font with the passed parameters
     */
    public Font getMulish(float fontSize, FontWeight fontWeight, Color color) {
        return FontFactory.getFont(Objects.requireNonNull(getClass().getResource(getFontBase() + "Mulish-" + fontWeight.getWeight() + ".ttf")).toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, new BaseColor(color.getRGB()));
    }

    /**
     * Returns a {@link Font} with the passed parameters.
     * @param fontSize A {@link Float}: The size of the font
     * @return A {@link Font}: The font with the passed parameters
     */
    public Font getMulishRegular(float fontSize) {
        return FontFactory.getFont(Objects.requireNonNull(getClass().getResource(getFontBase() + "Mulish-Regular.ttf")).toExternalForm(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Loads the fonts with the passed names.
     * @param fontName A {@link String} varargs: The names of the fonts to load
     */
    public void loadFont(String... fontName) {
        for (String font : fontName) {
            javafx.scene.text.Font.loadFont(Objects.requireNonNull(getClass().getResource(getFontBase() + font)).toExternalForm(), 10);
        }
    }

    /**
     * The font weights to choose from.
     */
    public enum FontWeight {
        /**
         * The font weight 'Bold'
         */
        BOLD("Bold"),
        /**
         * The font weight 'Medium'
         */
        MEDIUM("Medium"),
        /**
         * The font weight 'Regular'
         */
        REGULAR("Regular"),
        /**
         * The font weight 'SemiBold'
         */
        SEMI_BOLD("SemiBold");

        /**
         * The {@link String} representation of the font weight
         */
        private final String weight;

        /**
         * Creates a new {@link FontWeight font weight} with the given {@link String} representation
         * @param weight A {@link String}: The font weight
         */
        FontWeight(String weight) {
            this.weight = weight;
        }

        /**
         * Returns the {@link String} representation of the font weight
         * @return A {@link String}: The font weight
         */
        public String getWeight() {
            return weight;
        }
    }
}
