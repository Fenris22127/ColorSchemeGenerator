package de.colorscheme.output;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

class OutputColorsTest {

    @Test
    void testReadMetaData() {
        Path imgPath = Path.of("C:\\Users\\Elisa\\Documents\\Iceland (by Jonny Auh).jpg");
        Set<MetaData> metaData = OutputColors.readMetaData(imgPath);
        List<MetaData> metaList = new LinkedList<>(metaData);
        Collections.reverse(metaList);
        metaList.forEach(System.out::println);
    }

    @Test
    void testReturn() {
        testInterrupted();
        System.out.println("Back");
    }

    @Test
    void testInterrupted() {
        try {
            BufferedImage image = ImageIO.read(Paths.get("C:\\Users\\Elisa\\Documents\\Icelan (by Jonny Auh).jpg").toFile());
        } catch (IOException e) {
            return;
        }
        System.out.println("Image read");
        System.out.println("Stuff");
    }

    @Test
    void testHsb() {
        float[] hsb = new float[3];
        System.out.println(Arrays.toString(hsb));
        List<Color> colList = List.of(
                new Color(255, 0, 0),
                new Color(255, 128, 0),
                new Color(255, 255, 0),
                new Color(128, 255, 0),
                new Color(0, 255, 0),
                new Color(0, 255, 128),
                new Color(0, 255, 255),
                new Color(0, 128, 255),
                new Color(0, 0, 255),
                new Color(128, 0, 255),
                new Color(255, 0, 255),
                new Color(255, 0, 128)
        );

        for (Color c : colList) {

            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
            int hue = (int) (hsb[0] * 360);
            int h = (hue - 29) / 60;
            if (hue < 30) {
                System.out.println("red");
            }
            else {
                switch (h) {
                    case 0 -> System.out.println("yellow");
                    case 1 -> System.out.println("green");
                    case 2 -> System.out.println("cyan");
                    case 3 -> System.out.println("blue");
                    case 4 -> System.out.println("purple");
                    case 5 -> System.out.println("red");
                }
            }
        }
    }
}