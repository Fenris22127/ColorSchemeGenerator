package de.colorscheme.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import de.colorscheme.main.StartProgram;

import java.awt.*;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A utility class for font operations.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.0
 * @since 18.0.1
 */
public class FontUtils {
    private static final Path FONT_BASE = Path.of("src/main/resources/de/colorscheme/main/fonts/");

    /**
     * Returns the base {@link Font}.
     * @return A {@link Font}: The base font
     */
    public Font getMulish() {
        return FontFactory.getFont( FONT_BASE + "/Mulish_Regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns the {@link Font} 'Mulish ExtraBold'.
     * @return A {@link Font}: The font 'Mulish ExtraBold'
     */
    public Font getMulishExtraBold() {
        return FontFactory.getFont(FONT_BASE + "/Mulish-ExtraBold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 32, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns the {@link Font} 'Quattrocento Sans Regular'.
     * @return A {@link Font}: The font 'Quattrocento Sans Regular'
     */
    public Font getQuattrocentoSansRegular() {
        return FontFactory.getFont(FONT_BASE + "/QuattrocentoSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns the {@link Font} 'Quattrocento Sans Bold'.
     * @return A {@link Font}: The font 'Quattrocento Sans Bold'
     */
    public Font getQuattrocentoSansBold() {
        return FontFactory.getFont(FONT_BASE + "/QuattrocentoSans-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Returns a {@link Font} with the passed parameters.
     * @param fontSize A {@link Float}: The size of the font
     * @param fontWeight A {@link String}: The weight of the font
     * @param color A {@link Color}: The color of the font
     * @return A {@link Font}: The font with the passed parameters
     */
    public Font getMulish(float fontSize, String fontWeight, Color color) {
        return FontFactory.getFont(FONT_BASE + "/Mulish-" + fontWeight + ".ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, new BaseColor(color.getRGB()));
    }

    /**
     * Returns a {@link Font} with the passed parameters.
     * @param fontSize A {@link Float}: The size of the font
     * @return A {@link Font}: The font with the passed parameters
     */
    public Font getMulishRegular(float fontSize) {
        return FontFactory.getFont(FONT_BASE + "/Mulish-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, BaseColor.BLACK);
    }

    /**
     * Loads the fonts with the passed names.
     * @param fontName A {@link String} varargs: The names of the fonts to load
     */
    public void loadFont(String... fontName) {
        for (String font : fontName)
            javafx.scene.text.Font.loadFont(Objects.requireNonNull(StartProgram.class.getResource("fonts/" + font)).toExternalForm(), 10);
    }
}
