package de.colorscheme.output;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import de.colorscheme.app.AppController;
import de.colorscheme.app.NewController;
import de.colorscheme.clustering.ColorData;
import de.fenris.logger.ColorLogger;
import javafx.geometry.Point3D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/*import static de.colorscheme.app.AppController.getResBundle;*/
import static de.colorscheme.app.NewController.getResBundle;
import static de.colorscheme.clustering.KMeans.getCentroids;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

/**
 * Writes the color scheme of the selected image into a pdf containing the image, image name, colours
 * and their rgb- and hex-code
 *
 * @author &copy; 2023 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 2.2
 * @since 17.0.1
 */
public class OutputColors {

    /**
     * Creates a {@link ColorLogger Logger} for this class
     */
    private static final Logger LOGGER = ColorLogger.newLogger(OutputColors.class.getName());

    /**
     * The {@link Font} used for the {@link Document} bold text
     */
    private static final Font bold = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);

    /**
     * The {@link Font} used for the {@link Document} title
     */
    private static final Font header = FontFactory.getFont(FontFactory.HELVETICA, 30, Font.BOLD);

    /**
     * The {@link Font} used for the {@link Document}s regular content
     */
    private static final Font regular = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL);

    /**
     * The {@link Font} used for the {@link Document}s small content
     */
    private static final Font small = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);

    private static final Path schemeWheelPath = Path.of("src/main/resources/img/SchemeWheel.png");

    /**
     * The {@link String} containing the path where the file will be downloaded to
     */
    private static String downloadPath;

    /**
     * Private constructor to hide the public one
     */
    private OutputColors() {
    }

    public static void setDownloadPath(String path) {
        downloadPath = path;
    }

    /**
     * Create a PDF with the resulting color scheme using the methods of the class {@link OutputColors OutputColors}.
     *
     * <ul>
     *     <li>
     *         Determines the selected images name using {@link #fileName(String) fileName()}, determines the name for
     *         the PDF file and sets the download path before trying to create a new file with that name and path.
     *     </li>
     *     <li>
     *         If a file of the name doesn't exist yet, a new {@link PdfWriter PdfWriter} is
     *         created which generates a new {@link FileOutputStream} at the download path and the method
     *         {@link #outputWrite(ColorData, Path, Path)}  outputWrite()} is called.
     *     </li>
     *     <li>
     *         If a file of that name exists already, a new name is determined by
     *         {@link #getFileName(Path) getFileName()}, then the method
     *         {@link #outputWrite(ColorData, Path, Path) outputWrite()} is called.
     *     </li>
     * </ul>
     *
     * couldn't be created
     */
    public static void createOutput(ColorData c, Path imagePath) {
        StringBuilder fileDestinationBuilder = new StringBuilder();
        String imgName = fileName(imagePath.toString());

        String filename = "ColorScheme_" + imgName.substring(0, imgName.lastIndexOf(".")) + ".pdf";

        Path finalPath = Paths.get(fileDestinationBuilder.append(downloadPath).append("\\").append(filename).toString());

        //If a file of the same name exists, find number of files with the same name and set filename accordingly
        if (Files.exists(finalPath)) {
            Path newNamePath = getFileName(finalPath);
            outputWrite(c, newNamePath, imagePath);
        }
        //If file object was created successfully and file at destination doesn't exist yet, create new file
        if (!Files.exists(finalPath)) {
            outputWrite(c, Paths.get(fileDestinationBuilder.toString()), imagePath);
        }
    }

    /**
     * Determines the file name of a file of the original name exists already. <br>
     * <p>
     * Sets the filename as: Filename (x + 1).pdf with x being the number of files with the same name.
     *
     * @param path a {@link String}: The path at which the file will be saved
     * @return a {@link String}: The path with the updated filename at which the file will now be saved
     */
    private static Path getFileName(Path path) {
        int fileNumber = 1;
        Path filePath = path;
        String name = String.valueOf(path.getFileName()).substring(0, String.valueOf(path.getFileName()).lastIndexOf("."));
        String newName = name;
        String oldName;
        while (Files.exists(filePath)) {
            oldName = newName;
            newName = String.format("%s (%d)", name, fileNumber);
            filePath = Paths.get(filePath.toString().replace(oldName, newName));
            fileNumber++;
        }
        return filePath;
    }

    /**
     * Creates the {@link Document} and fills it with content. <br>
     * <ol>
     *     <li>
     *         Creates a new {@link Document} in {@link PageSize#A4 A4} and no margins.
     *     </li>
     *     <li>
     *         Creates a new {@link PdfWriter} to {@link PdfWriter#getInstance(Document, OutputStream) write} into the
     *         {@link Document} at the specified path by using a {@link FileOutputStream}
     *     </li>
     *     <li>
     *         {@link Document#open() Opens} the document and {@link #addContent(ColorData, Document, Path) adds} the
     *         content before {@link Document#close() closing} it again.
     *     </li>
     * </ol>
     *
     * @param c         A {@link ColorData} object: The instance used for all processes for the currently inspected image
     * @param path      A {@link String}: The path to the location, where the color scheme file will be saved
     * @param imagePath A {@link String}: The path to the selected image
     */
    private static void outputWrite(ColorData c, Path path, Path imagePath) {
        try {
            Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter.getInstance(doc, new FileOutputStream(path.toString()));
            doc.open();
            addContent(c, doc, imagePath);
            doc.close();
            Files.deleteIfExists(schemeWheelPath);
        } catch (IOException e) {
            AppController.addToOutputField(getResBundle().getString("docAccessFileError"), true);
            AppController.setCancelled(true);

            LOGGER.log(SEVERE, "{0}: FileOutputStream could not access created document!",
                    e.getClass().getSimpleName());
            e.printStackTrace();
        } catch (DocumentException e) {
            AppController.addToOutputField(getResBundle().getString("docWriteInFileError"), true);
            AppController.setCancelled(true);

            LOGGER.log(SEVERE, "{0}: Could not instantiate PdfWriter!",
                    e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    /**
     * Adds the resulting color scheme and the selected image to the {@link Document}. <br>
     * <ol>
     *     <li>
     *         Adds a {@link Document#newPage() new page} without margins to the {@link Document} and gets the main
     *         colors of the selected image in a {@link LinkedList} of {@link BaseColor}s.
     *     </li>
     *     <li>
     *         Creates a {@link Paragraph new paragraph} for the title, sets the content to "Color Scheme",
     *         the {@link Font} to {@link #header} and sets the {@link Paragraph#setAlignment(int) alignment} to
     *         {@link Element#ALIGN_CENTER center}. <br>
     *         Then, an {@link #addEmptyLine(Paragraph, int) empty line} is added for spacing before adding the
     *         {@link Paragraph} to the {@link Document}.
     *     </li>
     *     <li>
     *         Then, a {@link Paragraph new paragraph} containing the image name, determined by
     *         {@link #fileName(String) fileName()}, in the {@link #small} {@link Font} is created,
     *         the text is {@link Paragraph#setAlignment(int) aligned} in the {@link Element#ALIGN_CENTER middle}
     *         and an {@link #addEmptyLine(Paragraph, int) empty line} is added for spacing before adding the
     *         {@link Paragraph} to the {@link Document}.
     *     </li>
     *     <li>
     *         Afterwards, the page size is {@link Document#getPageSize() determined} and 40% of the width and height
     *         are calculated. <br>
     *         The selected {@link Image} is then scaled to {@link Image#scaleToFit(float, float) not exceed} 40% of
     *         the {@link Document}s width or height but keep its aspect ratio. <br>
     *         It is then set to {@link Paragraph#setAlignment(int) align} in the {@link Element#ALIGN_CENTER center}
     *         and added to the {@link Document}.
     *     </li>
     *     <li>
     *         Following that, a {@link Paragraph new paragraph} containing an
     *         {@link #addEmptyLine(Paragraph, int) empty line} is added for spacing and added to the {@link Document}.
     *     </li>
     *     <li>
     *         Then, a new {@link PdfPTable table} with the number of columns equaling the amount of main colors to be
     *         extracted from the image is created. <br>
     *         For each color, a new {@link PdfPCell cell} with a {@link PdfPCell#setFixedHeight(float) height} of
     *         <code color="#B5B5B5">200px</code> is created and its background is
     *         {@link PdfPCell#setBackgroundColor(BaseColor) set} to that color. <br>
     *         The {@link PdfPCell cells} are then added to the {@link PdfPTable table}. <br>
     *         The {@link BaseColor#getRed() red}, {@link BaseColor#getGreen() green} and
     *         {@link BaseColor#getBlue() blue} values for each {@link BaseColor color} in the {@link LinkedList} are
     *         then determined, and stored as a {@link String} in rgb and hexadecimal format. <br>
     *         Afterwards, a {@link Paragraph new paragraph} with its {@link Font} set to {@link #regular} is created,
     *         and the {@link String} containing the rgb value of the {@link BaseColor color} is added. <br>
     *         Then, after adding an {@link System#lineSeparator() empty line} for spacing, the {@link String}
     *         containing the hexadecimal value of the {@link BaseColor color} is added. <br>
     *         The {@link Paragraph#setAlignment(int) alignment} of the {@link Paragraph} holding the content is set to
     *         {@link Element#ALIGN_CENTER center} and is then added to a new {@link PdfPCell cell} with a
     *         {@link PdfPCell#setFixedHeight(float) height} of <code color="#B5B5B5">50px</code>. <br>
     *         The {@link PdfPCell cell} is then added to the {@link PdfPTable table}.
     *     </li>
     *     <li>
     *         Finally the {@link PdfPTable} is added to the {@link Document}.
     *     </li>
     * </ol>
     *
     * @param c         A {@link ColorData} object: The instance used for all processes for the currently inspected image
     * @param doc       A {@link Document}: The {@link Document} to be written in
     * @param imagePath A {@link String}: The path to the selected image
     */
    private static void addContent(ColorData c, Document doc, Path imagePath) {
        try {
            doc.newPage();
            doc.setMargins(0, 0, 0, 0);
            LinkedList<BaseColor> colors = getColors(c);

            Paragraph title = new Paragraph("Color Scheme", header);
            title.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(title, 1);
            doc.add(title);

            Paragraph docImg = new Paragraph(fileName(imagePath.toString()), small);
            docImg.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(docImg, 1);
            doc.add(docImg);

            Rectangle pageSize = new Rectangle(doc.getPageSize());
            float width = pageSize.getWidth();
            float height = pageSize.getHeight();
            float width40 = width / 10 * 4;
            float height40 = height / 10 * 4;
            Image img = Image.getInstance(imagePath.toString());
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleToFit(width40, height40);
            doc.add(img);

            Paragraph space = new Paragraph();
            addEmptyLine(space, 2);
            doc.add(space);

            float[] hsbColors = new float[3];
            addMainColors(doc, colors, hsbColors);

            Paragraph empty = new Paragraph();
            addEmptyLine(empty, 20);
            doc.add(empty);

            if (ColorWheel.createColorWheel(colors)) {
                Image colorWheel = Image.getInstance(schemeWheelPath.toString());
                colorWheel.setAlignment(Element.ALIGN_CENTER);
                colorWheel.scaleToFit(width40, height40);
                Paragraph imgParagraph = new Paragraph();
                Chunk imgChunk = new Chunk(colorWheel, 0, 0);
                imgParagraph.add(imgChunk);
                imgParagraph.setAlignment(Element.ALIGN_CENTER);
                doc.add(imgParagraph);
            }

            Paragraph clrWheelSpacing = new Paragraph();
            addEmptyLine(clrWheelSpacing, 2);
            doc.add(clrWheelSpacing);

            addAverage(doc, hsbColors);

            Paragraph spacing = new Paragraph();
            addEmptyLine(spacing, 2);
            doc.add(spacing);

            PdfPTable meta = new PdfPTable(2);
            Set<MetaData> metaData = OutputColors.readMetaData(imagePath);
            List<MetaData> metaList = new LinkedList<>(metaData);
            Collections.reverse(metaList);

            for (int i = 0; i < metaData.size(); i++) {
                String key = metaList.get(i).getDescriptor();
                Paragraph p = new Paragraph();
                p.setFont(bold);
                p.add(key);
                meta.addCell(new PdfPCell(p));
                p = new Paragraph();
                p.setFont(regular);
                p.add(metaList.get(i).getData());
                PdfPCell cell = new PdfPCell(p);
                cell.setPaddingBottom(5);
                meta.addCell(cell);
            }
            doc.add(meta);

        } catch (DocumentException e) {
            AppController.addToOutputField(getResBundle().getString("docAddElementsError"), true);
            AppController.setCancelled(true);

            Logger.getAnonymousLogger().log(SEVERE, "{}: Could not add element to created document!",
                    e.getClass().getSimpleName());
            e.printStackTrace();
        } catch (IOException e) {
            AppController.addToOutputField(getResBundle().getString("docAddImageReadError"), true);
            AppController.setCancelled(true);
            Logger.getAnonymousLogger().log(SEVERE, "{}: Could not read image to display in created document!",
                    e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    /**
     * Adds the main colors of the image with their HEX and RGB values to the {@link Document}.
     *
     * @param doc       A {@link Document}: The document to be written in
     * @param colors    A {@link LinkedList} with {@link BaseColor}s: The main colors of the image
     * @param hsbColors A {@link Float} array: The average HSB values of the main colors
     * @throws DocumentException If the {@link PdfPTable table} could not be added to the document
     */
    private static void addMainColors(Document doc, LinkedList<BaseColor> colors, float[] hsbColors) throws DocumentException {
        PdfPTable table = new PdfPTable(getCentroids());
        for (int i = 0; i < getCentroids(); i++) {
            PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(200);
            cell.setBackgroundColor(colors.get(i));
            table.addCell(cell);
        }
        for (int i = 0; i < getCentroids(); i++) {
            int red = colors.get(i).getRed();
            int green = colors.get(i).getGreen();
            int blue = colors.get(i).getBlue();
            float[] hsb = new float[3];
            Color.RGBtoHSB(red, green, blue, hsb);
            hsbColors[0] += hsb[0];
            hsbColors[1] += hsb[1];
            hsbColors[2] += hsb[2];
            String rgb = "RGB: " + red + ", " + green + ", " + blue;
            String hex = String.format("HEX: #%02X%02X%02X", red, green, blue);
            String hsbString = "HSB: " + Math.round(hsb[0] * 360) + ", " + Math.round(hsb[1] * 100) + "%, " + Math.round(hsb[2] * 100) + "%";
            Paragraph p = new Paragraph();
            p.setFont(regular);
            p.add(rgb);
            p.add(new Chunk(System.lineSeparator().concat(System.lineSeparator())));
            p.add(hex);
            p.add(new Chunk(System.lineSeparator().concat(System.lineSeparator())));
            p.add(hsbString);
            p.setAlignment(Element.ALIGN_CENTER);
            PdfPCell cell = new PdfPCell(p);
            cell.setFixedHeight(70);
            table.addCell(cell);
        }
        doc.add(table);
    }

    /**
     * Adds the description of the average HSB values of the main colors to the {@link Document}.
     *
     * @param doc       A {@link Document}: The document to be written in
     * @param hsbColors A {@link Float} array: The average HSB values of the main colors
     * @throws DocumentException If the {@link Paragraph} could not be added to the document
     */
    private static void addAverage(Document doc, float[] hsbColors) throws DocumentException {
        hsbColors[0] /= getCentroids();
        hsbColors[1] /= getCentroids();
        hsbColors[2] /= getCentroids();

        Paragraph colorDetails = new Paragraph();
        Phrase colorDetailsTitle = new Phrase(NewController.getResBundle().getString("avgTitle"), bold);
        colorDetails.add(colorDetailsTitle);
        colorDetails.setAlignment(Element.ALIGN_CENTER);
        doc.add(colorDetails);

        Paragraph avg = new Paragraph();
        Phrase avgColor = new Phrase("• " +
                NewController.getResBundle().getString("avgColorPre") +
                determineHue(hsbColors[0]), regular);
        avgColor.add(Chunk.NEWLINE);

        avgColor.add(((hsbColors[1] < 0.5 ? "• " +
                NewController.getResBundle().getString("avgSaturationUnSat")
                : "• " + NewController.getResBundle().getString("avgSaturationSat"))
                .concat(String.format(" (%s %.2f %%)",
                        NewController.getResBundle().getString("avgSaturation"),
                        hsbColors[1] * 100))));
        avgColor.add(Chunk.NEWLINE);

        avgColor.add((hsbColors[2] < 0.5 ? "• " +
                NewController.getResBundle().getString("avgBrightnessDark")
                : "• " + NewController.getResBundle().getString("avgBrightnessLight"))
                .concat(String.format(" (%s %.2f %%)",
                        NewController.getResBundle().getString("avgBrightness"),
                        hsbColors[2] * 100)));
        avgColor.add(Chunk.NEWLINE);

        avg.setIndentationLeft(220);
        avg.add(avgColor);
        doc.add(avg);
    }

    /**
     * Determines the color for the passed hue value.
     *
     * @param hue A {@link Float}: The hue value to determine the color of.
     * @return A {@link String}: The color for the passed hue value.
     */
    private static String determineHue(float hue) {
        int newHue = (int) (hue * 360);
        int h = (newHue - 29) / 60;
        String color;
        if (newHue < 30) {
            color = NewController.getResBundle().getString("avgRed");
        } else {
            switch (h) {
                case 0 -> color = NewController.getResBundle().getString("avgYellow");
                case 1 -> color = NewController.getResBundle().getString("avgGreen");
                case 2 -> color = NewController.getResBundle().getString("avgCyan");
                case 3 -> color = NewController.getResBundle().getString("avgBlue");
                case 4 -> color = NewController.getResBundle().getString("avgPurple");
                case 5 -> color = NewController.getResBundle().getString("avgRed");
                default -> color = NewController.getResBundle().getString("avgIndeterminate");
            }
        }
        return color;
    }

    /**
     * Returns the description of the color space indicated by the passed integer.
     *
     * @param index A {@link Integer}: The index of the color space
     * @return A {@link String}: The description of the color space
     */
    private static String getColorSpace(int index) {
        String model;
        switch (index) {
            case 1 -> model = NewController.getResBundle().getString("metaColorModel1");
            case 2 -> model = NewController.getResBundle().getString("metaColorModel2");
            case 3 -> model = NewController.getResBundle().getString("metaColorModel3");
            case 4 -> model = NewController.getResBundle().getString("metaColorModel4");
            case 5 -> model = NewController.getResBundle().getString("metaColorModel5");
            case 6 -> model = NewController.getResBundle().getString("metaColorModel6");
            case 7 -> model = NewController.getResBundle().getString("metaColorModel7");
            case 8 -> model = NewController.getResBundle().getString("metaColorModel8");
            case 9 -> model = NewController.getResBundle().getString("metaColorModel9");
            case 10 -> model = NewController.getResBundle().getString("metaColorModel10");
            case 11 -> model = NewController.getResBundle().getString("metaColorModel11");
            case 12 -> model = NewController.getResBundle().getString("metaColorModel12");
            case 13 -> model = NewController.getResBundle().getString("metaColorModel13");
            default -> model = NewController.getResBundle().getString("metaColorModelDefault");
        }
        return model;
    }

    /**
     * Reads the metadata of the image the passed {@link Path} points to and returns it as a {@link Set} of
     * {@link MetaData}.
     *
     * @param imgPath A {@link Path}: The path to the image to read the metadata from
     * @return A {@link Set} of {@link MetaData}: The metadata of the image
     */
    public static Set<MetaData> readMetaData(Path imgPath) {
        BufferedImage img;
        BasicFileAttributes attr;
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        try {
            img = ImageIO.read(imgPath.toFile());
            attr = Files.readAttributes(imgPath, BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(WARNING, "Could not read image to get meta data!");
            return MetaData.createNoAccessMetaData();
        }

        return MetaData.createMetaData(type ->
                switch (type) {
                    case FILE_NAME -> imgPath.getFileName().toString().split("\\.")[0];
                    case FILE_TYPE -> imgPath.getFileName().toString().split("\\.")[1].toUpperCase();
                    case FILE_SIZE -> formatSize(attr.size());
                    case FILE_CREATION_DATE -> formatTime(attr.creationTime(), dateTimeFormatter);
                    case FILE_LAST_MODIFIED_DATE -> formatTime(attr.lastModifiedTime(), dateTimeFormatter);
                    case FILE_LAST_ACCESSED_DATE -> formatTime(attr.lastAccessTime(), dateTimeFormatter);
                    case FILE_HEIGHT -> img.getHeight() + " px";
                    case FILE_WIDTH -> img.getWidth() + " px";
                    case FILE_IMAGE_TYPE -> getColorSpace(img.getType());
                    case FILE_COLOR_COMPONENTS -> String.valueOf(img.getColorModel().getNumComponents());
                    case FILE_BIT_DEPTH -> String.valueOf(img.getColorModel().getPixelSize());
                    case FILE_TRANSPARENCY -> switch (img.getColorModel().getTransparency()) {
                        case 1 -> NewController.getResBundle().getString("metaTransparency1");
                        case 2 -> NewController.getResBundle().getString("metaTransparency2");
                        case 3 -> NewController.getResBundle().getString("metaTransparency3");
                        default -> NewController.getResBundle().getString("metaTransparencyDefault");
                    };
                    case FILE_ALPHA -> (img.getColorModel().hasAlpha() ?
                            NewController.getResBundle().getString("metaAlphaYes") :
                            NewController.getResBundle().getString("metaAlphaNo"));
                    case FILE_ALPHA_TYPE -> (img.getColorModel().isAlphaPremultiplied() ?
                            NewController.getResBundle().getString("metaAlphaPremultiplied") :
                            NewController.getResBundle().getString("metaAlphaNotPremultiplied"));
                });
    }

    /**
     * Formats the passed {@link FileTime} to a {@link String} using the passed {@link DateTimeFormatter}.
     *
     * @param time              - A {@link FileTime}: The time to format
     * @param dateTimeFormatter - A {@link DateTimeFormatter}: The formatter to use
     * @return A {@link String}: The time, formatted to fit the pattern "dd/MM/yyyy HH:mm:ss"
     */
    private static String formatTime(FileTime time, DateTimeFormatter dateTimeFormatter) {
        LocalDateTime ldt = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ldt.format(dateTimeFormatter);
    }

    /**
     * Formats the passed {@link Long} to a {@link String} containing the size of the file in a better readable format.
     *
     * @param bytes - A {@link Long}: The size of the file in bytes
     * @return A {@link String}: The size of the file in a better readable format
     */
    private static String formatSize(long bytes) {
        long factor = 1_000L;
        int power = 0;
        while (bytes >= factor) {
            power++;
            bytes /= factor;
        }
        return String.format("%.2f %sB", (double) bytes, power == 0 ? "" : "kMGT".charAt(power - 1));
    }

    /**
     * Creates and returns a {@link LinkedList} containing the main {@link BaseColor colors} of the selected image
     * determined in {@link ColorData} and {@link de.colorscheme.clustering.KMeans KMeans}
     *
     * @param c A {@link ColorData} object: The instance used for all processes for the currently inspected image
     * @return A {@link LinkedList} of {@link BaseColor}s: A {@link LinkedList} containing the main
     * {@link BaseColor colors} of the selected image
     */
    private static LinkedList<BaseColor> getColors(ColorData c) {
        LinkedList<BaseColor> schemeColors = new LinkedList<>();
        for (int i = 0; i < c.getCentroids().size(); i++) {
            Point3D p = c.getCentroids().get(i);
            int x = (int) p.getX();
            int y = (int) p.getY();
            int z = (int) p.getZ();
            BaseColor color = new BaseColor(x, y, z);
            schemeColors.add(color);
        }
        return schemeColors;
    }

    /**
     * Adds a specified number of empty lines.
     *
     * @param paragraph A {@link Paragraph}: The target {@link Paragraph} for the empty lines
     * @param number    An {@link Integer}: The amount of empty lines to be added
     */
    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    /**
     * Gets the name of the file at the specified path.
     *
     * @param path A {@link String}: The path of the target file
     * @return A {@link String}: The name of the file at the specified path
     */
    protected static String fileName(String path) {
        return new File(path).getName();
    }
}
