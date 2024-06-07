package de.colorscheme.output;

import com.itextpdf.text.BaseColor;
import de.colorscheme.app.AppController;
import de.fenris.logger.ColorLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static de.colorscheme.utils.PathUtils.*;
import static java.util.logging.Level.SEVERE;

/**
 * Creates the color wheel for a visual representation of the determined main colors.
 *
 * @author &copy; 2024 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 2.0
 * @since 18.0.2
 */
public class ColorWheel {
    /**
     * The {@link Integer} determining the size of the color wheel in pixels
     */
    private static final int WHEEL_SIZE = 1024;

    /**
     * The {@link Integer} determining the size of the image containing the color wheel in pixels
     */
    private static final int IMG_SIZE = 1124;

    /**
     * Creates a {@link ColorLogger Logger} for this class
     */
    private static final Logger LOGGER = ColorLogger.newLogger(ColorWheel.class.getName());
    /*private static final String WHEEL_PATH_STANDARD = "src/main/resources/img/SchemeWheel.png";
    private static final String WHEEL_PATH_JAR = "/img/SchemeWheel.png";
    private static final String WHEEL_PATH;
    static {
        if(runningFromJAR()) {
            WHEEL_PATH = WHEEL_PATH_JAR;
        } else {
            WHEEL_PATH = WHEEL_PATH_STANDARD;
        }
    }*/

    /**
     * Private constructor to hide the public one
     */
    private ColorWheel() { /* Private constructor to hide the implicitly public one */ }

    /**
     * Creates the color wheel by creating the clean color wheel via {@link #makeColorWheelImage()}, adding the main
     * colors of the image passed via the passed {@link List} using {@link #addCircle(List) addCircle()} and saving the
     * resulting image as a PNG in the resources.
     *
     * @param colours A {@link List} of {@link BaseColor}s: The list containing the main colors determined by the
     *                algorithm
     * @return A {@link Boolean}: Returns true, if the file has been created successfully. Returns false, if the file
     * cannot be found at the location it should be saved in.
     */
    public static File createColorWheel(List<BaseColor> colours) {
        BufferedImage colWheel = makeColorWheelImage();
        drawColorWheel(colWheel);

        colWheel = addCircle(colours);
        File f = new File(getColorWheelPath());
        try {
            if (runningFromJAR()) {
                f = File.createTempFile("SchemeWheel", ".png");
                f.deleteOnExit();
            }
            ImageIO.write(Objects.requireNonNull(colWheel), "png", f);
        } catch (IOException e) {
            AppController.addToOutputField("Image could not be written!" + System.lineSeparator(), true);
            LOGGER.log(SEVERE, String.format("%s: Image could not be written!", e.getClass().getSimpleName()));
        }
        return f;
    }


    /**
     * Flips the passed image vertically, horizontally or on both axes'.
     *
     * @param image      A {@link BufferedImage}: The image to be flipped
     * @param horizontal A {@link Boolean}: Whether the image should be flipped horizontally
     * @param vertical   A {@link Boolean}: Whether the image should be flipped vertically
     * @return A {@link BufferedImage}: The flipped image
     */
    public static BufferedImage flipImage(final BufferedImage image,
                                          final boolean horizontal,
                                          final boolean vertical
    ) {
        int xStart = 0;
        int yStart = 0;
        int width = image.getWidth();
        int height = image.getHeight();

        final BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = out.createGraphics();

        if (horizontal) {
            xStart = width;
            width *= -1;
        }

        if (vertical) {
            yStart = height;
            height *= -1;
        }

        g2d.drawImage(image, xStart, yStart, width, height, null);
        g2d.dispose();

        return out;
    }

    /**
     * Uses a {@link Ellipse2D} as a mask for the color-wheel gradient to create the clean color wheel.
     *
     * @param img A {@link BufferedImage}: The gradient to be masked to create the clean color wheel
     */
    private static void drawColorWheel(BufferedImage img) {
        int size = IMG_SIZE;
        Graphics2D g2 = (Graphics2D) img.getGraphics();

        // Soft Clipping
        GraphicsConfiguration gc = g2.getDeviceConfiguration();
        BufferedImage wheel = gc.createCompatibleImage(size, size, Transparency.TRANSLUCENT);
        Graphics2D g2d = wheel.createGraphics();

        g2d.setComposite(AlphaComposite.Src);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fill(new Ellipse2D.Float(
                (float) (IMG_SIZE - WHEEL_SIZE) / 2,
                (float) (IMG_SIZE - WHEEL_SIZE) / 2,
                WHEEL_SIZE, WHEEL_SIZE));

        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.drawImage(img, (IMG_SIZE - WHEEL_SIZE) / 2, (IMG_SIZE - WHEEL_SIZE) / 2, null);
        g2d.dispose();

        g2.drawImage(wheel, -size / 2, -size / 2, null);
        g2.dispose();
    }

    /**
     * Creates the color-wheel gradient for the color wheel
     *
     * @return A {@link BufferedImage}: The gradient for the clean color wheel
     */
    private static BufferedImage makeColorWheelImage() {
        BufferedImage wheelGradient = new BufferedImage(WHEEL_SIZE, WHEEL_SIZE, BufferedImage.TYPE_INT_ARGB);
        int[] row = new int[WHEEL_SIZE];
        float radius = WHEEL_SIZE / 2F;

        for (int currentY = 0; currentY < WHEEL_SIZE; currentY++) {
            float distanceCenterY = currentY - radius;
            for (int currentX = 0; currentX < WHEEL_SIZE; currentX++) {
                float distanceCenterX = currentX - radius;
                double theta = Math.atan2(distanceCenterY, distanceCenterX) - 3D * Math.PI / 2D;
                if (theta < 0) {
                    theta += 2D * Math.PI;
                }
                double r = Math.sqrt(distanceCenterX * distanceCenterX + distanceCenterY * distanceCenterY);
                float hue = (float) (theta / (2D * Math.PI));
                float sat = Math.min((float) (r / radius), 1F);
                float bri = 1F;
                row[currentX] = Color.HSBtoRGB(hue, sat, bri);
            }
            wheelGradient.getRaster().setDataElements(0, currentY, WHEEL_SIZE, 1, row);
        }

        wheelGradient = flipImage(wheelGradient, true, true);

        return wheelGradient;
    }

    /**
     * Adds the main colors determined by the algorithm in circles onto the color wheel.
     *
     * @param colors A {@link List} of {@link BaseColor}s: The list containing the main colors determined by the
     *               algorithm
     * @return @return A {@link BufferedImage}: The color wheel with the added circles for the main colors
     */
    private static BufferedImage addCircle(List<BaseColor> colors) {
        try {
            BufferedImage wheel;
            BufferedImage circle;
            if (runningFromJAR()) {
                wheel = ImageIO.read(
                        Objects.requireNonNull(
                                ClassLoader.getSystemClassLoader().getResource("img/wheel.png")));
                circle = ImageIO.read(
                        Objects.requireNonNull(
                                ClassLoader.getSystemClassLoader().getResource("img/circle.png")));
            } else {
                wheel = ImageIO.read(
                        Objects.requireNonNull(
                                ClassLoader.getSystemClassLoader().getResource("img/wheel.png")));
                circle = ImageIO.read(
                        Objects.requireNonNull(
                                ClassLoader.getSystemClassLoader().getResource("img/circle.png")));
            }
            int wheelRadius = wheel.getHeight() / 2;
            int circleRadius = 50;
            circle = resize(circle, circleRadius * 2, circleRadius * 2);
            Graphics2D g = wheel.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));

            for (BaseColor col : colors) {
                float[] hsbValues = Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
                float radius = (WHEEL_SIZE / 2F) * hsbValues[1];
                double angle = Math.toRadians(360 * hsbValues[0] + 90);
                double xPos = radius * Math.cos(angle) + wheelRadius;
                double yPos = radius * Math.sin(angle) + wheelRadius;
                Color c = new Color(col.getRed(), col.getGreen(), col.getBlue());

                g.setColor(c);
                g.fill(new Ellipse2D.Float(
                        (float) xPos - circleRadius,
                        (float) yPos - circleRadius,
                        (float) circleRadius * 2 - 4,
                        (float) circleRadius * 2 - 4));
                g.drawImage(
                        circle,
                        (int) xPos - circleRadius,
                        (int) yPos - circleRadius,
                        null);
            }
            g.dispose();
            return wheel;
        } catch (IOException e) {
            AppController.addToOutputField("BufferedImage could not be read!" + System.lineSeparator(), true);
            LOGGER.log(SEVERE, "{0}: BufferedImage could not be read!", e.getClass().getSimpleName());
        }
        return null;
    }

    /**
     * Returns the list of complementary colors.
     * @param hsbValues The HSB values of the color
     * @return A {@link List} of {@link javafx.scene.paint.Color}s: The list of complementary colors
     */
    protected static List<javafx.scene.paint.Color> getComplementaryColourList(float[] hsbValues) {
        double colourAngle = 360 * hsbValues[0] + 180;
        colourAngle = getAngle(colourAngle);

        return Arrays.asList(javafx.scene.paint.Color.hsb(colourAngle, hsbValues[1], hsbValues[2]));
    }

    /**
     * Returns the list of split-complementary colors.
     * @param hsbValues The HSB values of the color
     * @return A {@link List} of {@link javafx.scene.paint.Color}s: The list of split-complementary colors
     */
    protected static List<javafx.scene.paint.Color> getSplitComplementaryColour(float[] hsbValues) {
        double colourAngle1 = 360 * hsbValues[0] + 180 + 30;
        double colourAngle2 = 360 * hsbValues[0] + 180 - 30;

        javafx.scene.paint.Color c1 = getColorFromAngle(colourAngle1, hsbValues);
        javafx.scene.paint.Color c2 = getColorFromAngle(colourAngle2, hsbValues);
        return Arrays.asList(
                c1, c2
        );
    }
    private static javafx.scene.paint.Color getColorFromAngle(double angle, float[] hsbValues) {
        angle = getAngle(angle);
        return javafx.scene.paint.Color.hsb(angle, hsbValues[1], hsbValues[2]);
    }

    /**
     * Returns the list of analogous colors.
     * @param hsbValues The HSB values of the color
     * @return A {@link List} of {@link javafx.scene.paint.Color}s: The list of analogous colors
     */
    protected static List<javafx.scene.paint.Color> getAnalogousColour(float[] hsbValues) {
        double colourAngle1 = 360 * hsbValues[0] + 30;
        double colourAngle2 = 360 * hsbValues[0] - 30;

        javafx.scene.paint.Color c1 = getColorFromAngle(colourAngle1, hsbValues);
        javafx.scene.paint.Color c2 = getColorFromAngle(colourAngle2, hsbValues);

        return Arrays.asList(
                c1,
                c2
        );
    }

    /**
     * Returns the list of triadic colors.
     * @param hsbValues The HSB values of the color
     * @return A {@link List} of {@link javafx.scene.paint.Color}s: The list of triadic colors
     */
    protected static List<javafx.scene.paint.Color> getTriadicColour(float[] hsbValues) {
        double colourAngle1 = 360 * hsbValues[0] + 120;
        double colourAngle2 = 360 * hsbValues[0] + 240;

        javafx.scene.paint.Color c1 = getColorFromAngle(colourAngle1, hsbValues);
        javafx.scene.paint.Color c2 = getColorFromAngle(colourAngle2, hsbValues);
        return Arrays.asList(
                c1, c2
        );
    }

    /**
     * Returns the list of tetradic colors.
     * @param hsbValues The HSB values of the color
     * @return A {@link List} of {@link javafx.scene.paint.Color}s: The list of tetradic colors
     */
    protected static List<javafx.scene.paint.Color> getTetradicColour(float[] hsbValues) {
        double colourAngle1 = 360 * hsbValues[0] + 90;
        double colourAngle2 = 360 * hsbValues[0] + 180;
        double colourAngle3 = 360 * hsbValues[0] + 270;

        colourAngle1 = getAngle(colourAngle1);
        colourAngle2 = getAngle(colourAngle2);
        colourAngle3 = getAngle(colourAngle3);

        javafx.scene.paint.Color c1 = javafx.scene.paint.Color.hsb(colourAngle1, hsbValues[1], hsbValues[2]);
        javafx.scene.paint.Color c2 = javafx.scene.paint.Color.hsb(colourAngle2, hsbValues[1], hsbValues[2]);
        javafx.scene.paint.Color c3 = javafx.scene.paint.Color.hsb(colourAngle3, hsbValues[1], hsbValues[2]);
        return Arrays.asList(
                c1,
                c2,
                c3
        );
    }

    /**
     * Returns the list of monochromatic colors.
     * @param hsbValues The HSB values of the color
     * @return A {@link List} of {@link javafx.scene.paint.Color}s: The list of monochromatic colors
     */
    protected static List<javafx.scene.paint.Color> getMonochromaticColour(float[] hsbValues) {
        return Arrays.asList(
                javafx.scene.paint.Color.hsb(hsbValues[0] * 360, 0.22, 1),
                javafx.scene.paint.Color.hsb(hsbValues[0] * 360, 0.46, 1),
                javafx.scene.paint.Color.hsb(hsbValues[0] * 360, 0.75, 0.8),
                javafx.scene.paint.Color.hsb(hsbValues[0] * 360, 0.75, 0.26)
        );
    }

    /**
     * Calculates the hue for the harmonious colors.
     * @param angle The angle of the color
     * @return A {@link Double}: The hue for the harmonious colors
     */
    private static double getAngle(double angle) {
        if (angle < 0) {
            angle = 360 + angle;
        }
        if (angle > 360) {
            angle = angle - 360;
        }
        return angle;
    }

    /**
     * Resizes an image to a new width and height.
     *
     * @param img       A {@link BufferedImage}: The image to be resized
     * @param newWidth  An {@link Integer}: The new width for the image
     * @param newHeight An {@link Integer}: The new height for the image
     * @return A {@link BufferedImage}: The resized image
     */
    private static BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
        Image scaledInstance = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledInstance, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }
}
