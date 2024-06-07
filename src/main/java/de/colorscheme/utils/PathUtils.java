package de.colorscheme.utils;

import java.io.File;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * A utility class for finding the path depending on the environment the application is running in.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.0
 * @since 18.0.1
 */
public class PathUtils {
    /**
     * The base path for the fonts.
     */
    private static String fontBase;
    /**
     * The path for the color wheel with the main colors drawn on it.
     */
    private static String colorWheelPath;
    /**
     * The path template for the harmony icon.
     */
    private static String harmonyBubble;
    /**
     * The base path for the resources.
     */
    private static String resourceBase;

    static {
        if (runningFromJAR()) {
            setJarPaths();
        } else {
            setStandardPaths();
        }
    }

    /**
     * The constructor of the utility class.
     */
    PathUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if the application is running from a JAR file.
     * @return A {@link Boolean}: True if the application is running from a JAR file, false otherwise
     */
    public static boolean runningFromJAR() {
        try {
            String jarFilePath = new File(FontUtils.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath()).
                    toString();
            jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");

            try (ZipFile zipFile = new ZipFile(jarFilePath)) {
                ZipEntry zipEntry = zipFile.getEntry("META-INF/MANIFEST.MF");

                return zipEntry != null;
            }
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Sets the paths for the JAR environment.
     */
    private static void setJarPaths() {
        fontBase = "/de/colorscheme/main/fonts/";
        colorWheelPath = "img/SchemeWheel.png";
        harmonyBubble = "%sde/colorscheme/app/icons/%s_bubble.png";
        resourceBase = "";
    }

    /**
     * Sets the standard paths.
     */
    private static void setStandardPaths() {
        fontBase = "../main/fonts/";
        colorWheelPath = "src/main/resources/img/SchemeWheel.png";
        harmonyBubble = "%s/app/icons/%s_bubble.png";
        resourceBase = "src/main/resources/de/colorscheme/";
    }

    /**
     * Returns the base path for the fonts.
     * @return A {@link String}: The base path for the fonts
     */
    public static String getFontBase() {
        return fontBase;
    }

    /**
     * Returns the path for the color wheel with the main colors drawn on it.
     * @return A {@link String}: The path for the color wheel with the main colors drawn on it
     */
    public static String getColorWheelPath() {
        return colorWheelPath;
    }

    /**
     * Returns the base path for the resources.
     * @return A {@link String}: The base path for the resources
     */
    public static String getResourceBase() {
        return resourceBase;
    }

    /**
     * Returns the path template for the harmony icon.
     * @return A {@link String}: The path template for the harmony icon
     */
    public static String getHarmonyBubble() {
        return harmonyBubble;
    }
}
