package de.colorscheme.output;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
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
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/*import static de.colorscheme.app.AppController.getResBundle;*/
import static de.colorscheme.app.NewController.getResBundle;
import static de.colorscheme.clustering.KMeans.getCentroids;
import static de.colorscheme.output.ColorWheel.*;
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
     * The {@link Font} used for the {@link Document}s regular content
     */
    private static Font regular = FontFactory.getFont("../fonts/Mulish-Regular.ttf", 8, Font.NORMAL);
    private static final Path schemeWheelPath = Path.of("src/main/resources/img/SchemeWheel.png");
    private static final Path RESOURCE_BASE = Path.of("src/main/resources/de/colorscheme/");


    //private static final Path schemeWheelPath = Path.of("src/main/resources/img/SchemeWheel.png");

    /**
     * The {@link String} containing the path where the file will be downloaded to
     */
    private static String downloadPath;
    private static Font MULISH_REGULAR;
    private static Font MULISH_EXTRABOLD;
    private static Font QUATTROCENTO_SANS_REGULAR;
    private static Font QUATTROCENTO_SANS_BOLD;
    private static PdfWriter writer;

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
        MULISH_REGULAR = FontFactory.getFont("src/main/resources/de/colorscheme/main/fonts/Mulish-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.NORMAL, BaseColor.BLACK);
        MULISH_EXTRABOLD = FontFactory.getFont("src/main/resources/de/colorscheme/main/fonts/Mulish-ExtraBold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 32, Font.NORMAL, BaseColor.BLACK);
        QUATTROCENTO_SANS_REGULAR = FontFactory.getFont("src/main/resources/de/colorscheme/main/fonts/QuattrocentoSans-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.NORMAL, BaseColor.BLACK);
        QUATTROCENTO_SANS_BOLD = FontFactory.getFont("src/main/resources/de/colorscheme/main/fonts/QuattrocentoSans-Bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.NORMAL, BaseColor.BLACK);

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
            writer = PdfWriter.getInstance(doc, new FileOutputStream(path.toString()));
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
            if (getCentroids() > 5) {
                regular = FontFactory.getFont("src/main/resources/de/colorscheme/main/fonts/Mulish-Regular.ttf", 9, Font.NORMAL);
            } else {
                regular = FontFactory.getFont("src/main/resources/de/colorscheme/main/fonts/Mulish-Regular.ttf", 11, Font.NORMAL);
            }

            float margin = 61F;
            doc.setMargins(margin, margin, 42, margin);
            doc.newPage();
            //LinkedList<BaseColor> colors = getColors(c); //TODO: Check if this is correct

            /*Paragraph title = new Paragraph("Color Scheme", header);
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
            doc.add(space);*/
            //Add header
            Chunk titleChunk = new Chunk("COLOR SCHEME", MULISH_EXTRABOLD);
            titleChunk.setCharacterSpacing(1.2F);
            Paragraph title = new Paragraph(titleChunk);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            LineSeparator line = new LineSeparator(3F, 60, BaseColor.BLACK, Element.ALIGN_CENTER, -10);
            doc.add(line);

            Chunk docImgChunk = new Chunk(String.valueOf(imagePath.getFileName()), QUATTROCENTO_SANS_REGULAR);
            docImgChunk.setCharacterSpacing(0.7F);

            //Add image
            Paragraph docImg = new Paragraph(31, docImgChunk);
            docImg.setAlignment(Element.ALIGN_CENTER);
            doc.add(docImg);

            Paragraph space = new Paragraph(new Paragraph(" "));
            space.setMultipliedLeading(2.8F);
            doc.add(space);

            //Add colours
            float[] hsbColors = new float[3];
            Image img = Image.getInstance(imagePath.toString());
            if (img.getWidth() < img.getHeight()) {
                addForVerticalImage(c, doc, img, margin, hsbColors); //Works
            } else {
                addForHorizontalImage(c, doc, img, hsbColors); //Works
            }

            space = new Paragraph(new Paragraph(" "));
            space.setMultipliedLeading(1.5F);
            doc.add(space);
            doc.add(new Paragraph("COLOR SCHEME AVERAGES", QUATTROCENTO_SANS_BOLD));
            addAverageTable(doc, hsbColors);

            space = new Paragraph(new Paragraph(" "));
            space.setMultipliedLeading(2F);
            doc.add(space);
            checkNewPage(doc);

            /*float[] hsbColors = new float[3];
            addMainColors(doc, colors, hsbColors);

            Paragraph empty = new Paragraph();
            addEmptyLine(empty, 20);
            doc.add(empty);*/

            Rectangle pageSize = new Rectangle(doc.getPageSize());
            float width = pageSize.getWidth();
            float height = pageSize.getHeight();
            float width40 = width / 10 * 4;
            float height40 = height / 10 * 4;

            if (ColorWheel.createColorWheel(getColors(c))) {
                Image colorWheel = Image.getInstance(schemeWheelPath.toString());
                colorWheel.setAlignment(Element.ALIGN_CENTER);
                colorWheel.scaleToFit(width40, height40);
                Paragraph imgParagraph = new Paragraph();
                imgParagraph.add(colorWheel);
                imgParagraph.setAlignment(Element.ALIGN_CENTER);
                doc.add(imgParagraph);
            }

            /*Paragraph clrWheelSpacing = new Paragraph();
            addEmptyLine(clrWheelSpacing, 2);
            doc.add(clrWheelSpacing);

            addAverage(doc, hsbColors);

            Paragraph spacing = new Paragraph();
            addEmptyLine(spacing, 2);
            doc.add(spacing);*/



            /*PdfPTable meta = new PdfPTable(2);
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
            doc.add(meta);*/

            doc.add(new Paragraph("IMAGE META DATA", QUATTROCENTO_SANS_BOLD));
            addMetaTable(doc, imagePath);

            space = new Paragraph(new Paragraph(" "));
            space.setMultipliedLeading(2F);
            doc.add(space);
            checkNewPage(doc);

            doc.add(new Paragraph("COLOR HARMONICS", QUATTROCENTO_SANS_BOLD));
            space = new Paragraph(new Paragraph(" "));
            space.setMultipliedLeading(1F);
            doc.add(space);

            List<ColorHarmony> harmonies = new LinkedList<>();
            harmonies.addAll(Arrays.asList(
                    ColorHarmony.COMPLEMENTARY,
                    ColorHarmony.SPLIT_COMPLEMENTARY,
                    ColorHarmony.MONOCHROMATIC,
                    ColorHarmony.ANALOGOUS,
                    ColorHarmony.TRIADIC,
                    ColorHarmony.TETRADIC
            ));
            addHarmony(c, doc, harmonies); //TODO: Add color harmonics list

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
     * <b>Image format: Portrait</b><br>
     * Adds the image and the main colors of the image with their HEX, HSB and RGB values to the {@link Document}.
     *
     * @param doc       A {@link Document}: The document to be written in
     * @param img       An {@link Image}: The image to be added to the document
     * @param margin    A {@link Float}: The margin of the document
     * @param hsbColors A {@link Float} array: The average HSB values of the main colors
     * @throws DocumentException If the {@link PdfPTable table} could not be added to the document
     */
    private static void addForVerticalImage(ColorData c, Document doc, Image img, float margin, float[] hsbColors) throws DocumentException {
        Rectangle pageSize = new Rectangle(doc.getPageSize());
        img.scaleToFit(330, 350);

        Rectangle rec = new Rectangle(350, 350);
        rec.setBackgroundColor(BaseColor.RED);

        doc.add(img);
        float topPosition = 670.5F;
        float spacer = 10.0F;
        float height = (img.getScaledHeight() - (getCentroids() - 1) * spacer) / getCentroids();

        PdfContentByte canvas = writer.getDirectContent();

        ColumnText ct = new ColumnText(canvas);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor color = getColors(c).get(i);

            regular.setColor(checkContrast(color));

            Color awtColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
            float[] hsb = Color.RGBtoHSB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), null);
            hsbColors[0] += hsb[0];
            hsbColors[1] += hsb[1];
            hsbColors[2] += hsb[2];
            Rectangle rect = new Rectangle(
                    img.getScaledWidth() + margin + spacer, // top left X start
                    topPosition - i * height - i * spacer, // top left Y start
                    pageSize.getWidth() - margin, // bottom right X end (width)
                    topPosition - (i + 1) * height - i * spacer); // bottom right Y end (height)
            rect.setBorder(0);
            rect.setBackgroundColor(color);
            canvas.rectangle(rect);

            String hex = getHex(color);
            ct.setSimpleColumn(rect);
            Paragraph colorValues = new Paragraph();
            Paragraph hexVal = new Paragraph(" HEX: " + hex, regular);
            Paragraph hslVal = new Paragraph(
                    String.format(" HSB: %d°, %d%%, %d%%",
                            Math.round(hsb[0] * 360),
                            Math.round(hsb[1] * 100),
                            Math.round(hsb[2] * 100)), regular);
            Paragraph rgbVal = new Paragraph(
                    String.format(" RGB: %d, %d, %d",
                            awtColor.getRed(),
                            awtColor.getGreen(),
                            awtColor.getBlue()), regular);
            colorValues.add(hexVal);
            colorValues.add(hslVal);
            colorValues.add(rgbVal);
            ct.addElement(colorValues);
            ct.go();
        }
    }

    /**
     * <b>Image format: Landscape or square</b><br>
     * Adds the image and the main colors of the image with their HEX, HSB and RGB values to the {@link Document}.
     *
     * @param doc       A {@link Document}: The document to be written in
     * @param img       An {@link Image}: The image to be added to the document
     * @param hsbColors A {@link Float} array: The average HSB values of the main colors
     * @throws DocumentException If the {@link PdfPTable table} could not be added to the document
     */
    private static void addForHorizontalImage(ColorData c, Document doc, Image img, float[] hsbColors) throws DocumentException {
        Rectangle pageSize = new Rectangle(doc.getPageSize());
        float pageWidth = pageSize.getWidth();
        float width80 = pageWidth / 10 * 8F;
        img.scaleToFit(width80, 350);
        img.setAlignment(Element.ALIGN_CENTER);
        doc.add(img);
        float spacer = 10.0F;
        float topPosition = 670.5F - img.getScaledHeight() - spacer;
        float width = (img.getScaledWidth() - (getCentroids() - 1) * spacer) / getCentroids();
        float start = (pageWidth - img.getScaledWidth()) / 2;

        PdfContentByte canvas = writer.getDirectContent();
        ColumnText ct = new ColumnText(canvas);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor color = getColors(c).get(i);
            Color awtColor = new Color(color.getRed(), color.getGreen(), color.getBlue());

            float[] hsb = Color.RGBtoHSB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), null);
            hsbColors[0] += hsb[0];
            hsbColors[1] += hsb[1];
            hsbColors[2] += hsb[2];

            Rectangle rect = new Rectangle(
                    start + i * width + i * spacer, // top left X start
                    topPosition, // top left Y start
                    start + (i + 1) * width + i * spacer, // bottom right X end (width)
                    topPosition - spacer - 140); // bottom right Y end (height)
            rect.setBorder(0);
            rect.setBackgroundColor(color);
            canvas.rectangle(rect);

            String hex = getHex(color);
            ct.setSimpleColumn(rect);
            Paragraph colorValues = new Paragraph();
            Paragraph hexHeader = new Paragraph(" HEX:", getMulish(10, "Bold", checkContrastAWT(color)));
            Paragraph hexVal = new Paragraph(" " + hex, getMulish(9, "Regular", checkContrastAWT(color)));
            hexVal.setSpacingAfter(4);
            Paragraph hslHeader = new Paragraph(" HSB:", getMulish(10, "Bold", checkContrastAWT(color)));
            Paragraph hslVal = new Paragraph(
                    String.format(" %d°, %d%%, %d%%",
                            Math.round(hsb[0] * 360),
                            Math.round(hsb[1] * 100),
                            Math.round(hsb[2] * 100)), getMulish(9, "Regular", checkContrastAWT(color)));
            hslVal.setSpacingAfter(4);
            Paragraph rgbHeader = new Paragraph(" RGB:", getMulish(10, "Bold", checkContrastAWT(color)));
            Paragraph rgbVal = new Paragraph(
                    String.format(" %d, %d, %d",
                            awtColor.getRed(),
                            awtColor.getGreen(),
                            awtColor.getBlue()), getMulish(9, "Regular", checkContrastAWT(color)));
            rgbVal.setSpacingAfter(4);
            colorValues.add(hexHeader);
            colorValues.add(hexVal);
            colorValues.add(hslHeader);
            colorValues.add(hslVal);
            colorValues.add(rgbHeader);
            colorValues.add(rgbVal);
            ct.addElement(colorValues);
            ct.go();
        }
        Paragraph space = new Paragraph(new Paragraph(" "));
        space.setLeading(topPosition - 140);
        doc.add(space);
    }

    //TODO: New
    private static void addAverageTable(Document doc, float[] hsbColors) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(14);
        table.setSpacingAfter(20);
        table.setWidths(new int[]{1, 2});
        Paragraph header = new Paragraph("Average", getMulish(9, "Bold", hexToColor("#404040")));
        Paragraph value = new Paragraph("Value", getMulish(9, "Bold", hexToColor("#404040")));
        PdfPCell avgHeader = getHeaderCell(header);
        PdfPCell valueHeader = getHeaderCell(value);

        table.addCell(avgHeader);
        table.addCell(valueHeader);
        String[] averages = getAverage(hsbColors);

        Font mulishSemibold = getMulish(12, "Semibold", Color.BLACK);
        Paragraph color = new Paragraph("Colour", mulishSemibold);
        Paragraph colorAvg = new Paragraph(averages[0], getMulish(12));
        table.addCell(getACell(color));
        table.addCell(getACell(colorAvg));
        Paragraph saturation = new Paragraph("Saturation", mulishSemibold);
        Paragraph saturationAvg = new Paragraph(averages[1], getMulish(12));
        table.addCell(getBCell(saturation));
        table.addCell(getBCell(saturationAvg));
        Paragraph brightness = new Paragraph("Brightness", mulishSemibold);
        Paragraph brightnessAvg = new Paragraph(averages[2], getMulish(12));
        table.addCell(getACell(brightness, true));
        table.addCell(getACell(brightnessAvg, true));

        doc.add(table);
    }

    //TODO: New
    private static void addMetaTable(Document doc, Path imagePath) throws DocumentException {
        Set<MetaData> metaData = readMetaData(imagePath);
        List<MetaData> metaList = new LinkedList<>(metaData);
        Collections.reverse(metaList);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(14);
        table.setSpacingAfter(20);
        table.setWidths(new int[]{1, 2});
        Paragraph header = new Paragraph("Meta Data", getMulish(9, "bold", hexToColor("#404040")));
        Paragraph value = new Paragraph("Value", getMulish(9, "bold", hexToColor("#404040")));
        PdfPCell metaHeader = getHeaderCell(header);
        PdfPCell valueHeader = getHeaderCell(value);

        table.addCell(metaHeader);
        table.addCell(valueHeader);

        boolean isBRow = false;
        for (int i = 0; i < metaList.size(); i++) {
            Paragraph metaName = new Paragraph(metaList.get(i).getDescriptor(), getMulish(12, "semibold", Color.BLACK));
            Paragraph metaValue = new Paragraph(metaList.get(i).getData(), getMulish(12));
            if (!isBRow) {
                if (i == metaList.size() - 1) {
                    table.addCell(getACell(metaName, true));
                    table.addCell(getACell(metaValue, true));
                } else {
                    table.addCell(getACell(metaName));
                    table.addCell(getACell(metaValue));
                    isBRow = true;
                }
            } else {
                if (i == metaList.size() - 1) {
                    table.addCell(getBCell(metaName, true));
                    table.addCell(getBCell(metaValue, true));
                } else {
                    table.addCell(getBCell(metaName));
                    table.addCell(getBCell(metaValue));
                    isBRow = false;
                }
            }
        }

        doc.add(table);
    }

    //TODO: New
    private static void addHarmony(ColorData c, Document doc, List<ColorHarmony> harmonies) throws DocumentException {
        for (ColorHarmony harmony : harmonies) {
            switch (harmony) {
                case COMPLEMENTARY:
                    checkNewPage(doc, 133.0);
                    addComplementary(c, doc);
                    break;
                case SPLIT_COMPLEMENTARY:
                    checkNewPage(doc, 168.0);
                    addSplitComplementary(c, doc);
                    break;
                case MONOCHROMATIC:
                    checkNewPage(doc, 234.032);
                    addMonochromatic(c, doc);
                    break;
                case ANALOGOUS:
                    checkNewPage(doc, 167.808);
                    addAnalogous(c, doc);
                    break;
                case TRIADIC:
                    checkNewPage(doc, 164.032);
                    addTriadic(c, doc);
                    break;
                case TETRADIC:
                    checkNewPage(doc, 199.032);
                    addTetradic(c, doc);
                    break;
            }
        }
    }

    //TODO: Add JavaDoc
    private static void addComplementary(ColorData c, Document doc) throws DocumentException {
        addHarmonyHeader(doc, "Complementary");

        PdfPTable table = new PdfPTable(getCentroids());
        table.setWidthPercentage(100);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor bc = getColors(c).get(i);
            float[] hsb = Color.RGBtoHSB(bc.getRed(), bc.getGreen(), bc.getBlue(), null);
            List<javafx.scene.paint.Color> col = getComplementaryColourList(hsb);

            PdfPCell cell = getHarmonyCells(bc, col);
            table.addCell(cell);
            doc.add(table);
        }
    }

    //TODO: Add JavaDoc
    private static void addSplitComplementary(ColorData c, Document doc) throws DocumentException {
        addHarmonyHeader(doc, "Split Complementary");

        PdfPTable table = new PdfPTable(getCentroids());
        table.setWidthPercentage(100);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor bc = getColors(c).get(i);
            float[] hsb = Color.RGBtoHSB(bc.getRed(), bc.getGreen(), bc.getBlue(), null);
            List<javafx.scene.paint.Color> col = getSplitComplementaryColour(hsb);

            PdfPCell cell = getHarmonyCells(bc, col);
            table.addCell(cell);
            doc.add(table);
        }
    }

    //TODO: Add JavaDoc
    private static void addMonochromatic(ColorData c, Document doc) throws DocumentException {
        addHarmonyHeader(doc, "Monochromatic");

        PdfPTable table = new PdfPTable(getCentroids());
        table.setWidthPercentage(100);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor bc = getColors(c).get(i);
            float[] hsb = Color.RGBtoHSB(bc.getRed(), bc.getGreen(), bc.getBlue(), null);
            List<javafx.scene.paint.Color> col = getMonochromaticColour(hsb);

            PdfPCell cell = getHarmonyCells(bc, col);
            table.addCell(cell);
            doc.add(table);
        }
    }

    //TODO: Add JavaDoc
    private static void addAnalogous(ColorData c, Document doc) throws DocumentException {
        addHarmonyHeader(doc, "Analogous");

        PdfPTable table = new PdfPTable(getCentroids());
        table.setWidthPercentage(100);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor bc = getColors(c).get(i);
            float[] hsb = Color.RGBtoHSB(bc.getRed(), bc.getGreen(), bc.getBlue(), null);
            List<javafx.scene.paint.Color> col = getAnalogousColour(hsb);

            PdfPCell cell = getHarmonyCells(bc, col);
            table.addCell(cell);
            doc.add(table);
        }
    }

    //TODO: Add JavaDoc
    private static void addTriadic(ColorData c, Document doc) throws DocumentException {
        addHarmonyHeader(doc, "Triadic");

        PdfPTable table = new PdfPTable(getCentroids());
        table.setWidthPercentage(100);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor bc = getColors(c).get(i);
            float[] hsb = Color.RGBtoHSB(bc.getRed(), bc.getGreen(), bc.getBlue(), null);
            List<javafx.scene.paint.Color> col = getTriadicColour(hsb);

            PdfPCell cell = getHarmonyCells(bc, col);
            table.addCell(cell);
            doc.add(table);
        }
    }

    //TODO: Add JavaDoc
    private static void addTetradic(ColorData c, Document doc) throws DocumentException {
        addHarmonyHeader(doc, "Tetradic");

        PdfPTable table = new PdfPTable(getCentroids());
        table.setWidthPercentage(100);
        for (int i = 0; i < getCentroids(); i++) {
            BaseColor bc = getColors(c).get(i);
            float[] hsb = Color.RGBtoHSB(bc.getRed(), bc.getGreen(), bc.getBlue(), null);
            List<javafx.scene.paint.Color> col = getTetradicColour(hsb);

            PdfPCell cell = getHarmonyCells(bc, col);
            table.addCell(cell);
            doc.add(table);
        }
    }

    //TODO: New
    private static void addHarmonyHeader(Document doc, String header) throws DocumentException {
        Paragraph space1 = new Paragraph(new Paragraph(" "));
        space1.setMultipliedLeading(1F);
        doc.add(space1);
        Image im;
        String file = header.toLowerCase().replace(" ", "");
        try {
            Path complementaryBubble = Paths.get(
                    String.format("src/main/resources/de/colorscheme/app/icons/%s_bubble.png", file));
            im = Image.getInstance(complementaryBubble.toString());
            im.scaleAbsolute(22, 22);
        } catch (IOException | BadElementException e) {
            throw new RuntimeException(e);
        }
        PdfPTable tab = new PdfPTable(2);
        tab.setWidthPercentage(100);
        tab.setWidths(new float[]{5, 75});
        PdfPCell imgCell = new PdfPCell(im);
        imgCell.setBorder(0);
        PdfPCell pCell = new PdfPCell(new Phrase(header, QUATTROCENTO_SANS_BOLD));
        pCell.setBorder(0);
        pCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pCell.setPadding(0);
        pCell.setPaddingBottom(6F);
        tab.addCell(imgCell);
        tab.addCell(pCell);
        doc.add(tab);

        Paragraph space2 = new Paragraph(new Paragraph(" "));
        space2.setMultipliedLeading(0.5F);
        doc.add(space2);
    }

    //TODO: New
    private static PdfPCell getHeaderCell(Paragraph label) {
        PdfPCell cell = new PdfPCell(label);
        cell.setPaddingTop(8);
        cell.setPaddingLeft(2);
        cell.setPaddingBottom(10);
        cell.setBackgroundColor(new BaseColor(hexToColor("#F2F2F2").getRGB()));
        cell.setBorder(0);
        cell.setBorderWidthBottom(1);
        cell.setBorderColorBottom(new BaseColor(hexToColor("#F3F3F4").getRGB()));
        return cell;
    }

    //TODO: New
    private static PdfPCell getHarmonyCells(BaseColor bc, List<javafx.scene.paint.Color> c) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.setPadding(5);
        PdfPTable innerTable = new PdfPTable(1);
        innerTable.setWidthPercentage(100);

        innerTable.addCell(getHarmonicMainCell(bc));
        c.forEach(color -> innerTable.addCell(getHarmonicCell(new BaseColor((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255)))));

        cell.addElement(innerTable);
        return cell;
    }

    //TODO: New
    private static PdfPCell getHarmonicMainCell(BaseColor color) {
        PdfPCell innerCell = new PdfPCell();
        innerCell.setBorder(0);
        innerCell.setPadding(15);
        innerCell.setBackgroundColor(color);
        innerCell.setFixedHeight(50);

        return innerCell;
    }

    //TODO: New
    private static PdfPCell getHarmonicCell(BaseColor c) {
        PdfPCell innerCell = new PdfPCell();

        innerCell.setCellEvent((cell1, position, canvases) -> {
            float x1 = position.getLeft();
            float x2 = position.getRight();
            float y1 = position.getTop() - 5;
            float y2 = position.getBottom() + 5;
            PdfContentByte canvas = canvases[PdfPTable.BACKGROUNDCANVAS];
            canvas.rectangle(x1, y1, x2 - x1, y2 - y1);
            canvas.setColorFill(c);
            canvas.fill();
        });
        innerCell.setBorder(0);
        innerCell.setPadding(10);
        innerCell.setPaddingLeft(5);
        innerCell.setFixedHeight(35);
        String hex = getHex(c);
        innerCell.setPhrase(new Phrase(hex, getMulish(8, "regular", checkContrastAWT(c))));

        return innerCell;
    }

    private static PdfPCell getACell(Paragraph label) {
        PdfPCell cell = new PdfPCell(label);
        cell.setPaddingTop(6);
        cell.setPaddingLeft(2);
        cell.setPaddingBottom(7);
        cell.setBorder(0);
        cell.setBorderWidthBottom(1);
        cell.setBorderColorBottom(new BaseColor(hexToColor("#F3F3F4").getRGB()));
        return cell;
    }

    private static PdfPCell getACell(Paragraph label, boolean isLast) {
        PdfPCell cell = new PdfPCell(label);
        cell.setPaddingTop(6);
        cell.setPaddingLeft(2);
        cell.setPaddingBottom(7);
        cell.setBorder(0);
        if (!isLast) {
            cell.setBorderWidthBottom(1);
        }
        cell.setBorderColorBottom(new BaseColor(hexToColor("#F3F3F4").getRGB()));
        return cell;
    }

    private static PdfPCell getBCell(Paragraph label, boolean isLast) {
        PdfPCell cell = new PdfPCell(label);
        cell.setPaddingTop(6);
        cell.setPaddingLeft(2);
        cell.setPaddingBottom(7);
        cell.setBackgroundColor(new BaseColor(hexToColor("#FAFAFA").getRGB()));
        cell.setBorder(0);
        if (!isLast) {
            cell.setBorderWidthBottom(1);
        }
        cell.setBorderColorBottom(new BaseColor(hexToColor("#F3F3F4").getRGB()));
        return cell;
    }

    private static PdfPCell getBCell(Paragraph label) {
        PdfPCell cell = new PdfPCell(label);
        cell.setPaddingTop(6);
        cell.setPaddingLeft(2);
        cell.setPaddingBottom(7);
        cell.setBackgroundColor(new BaseColor(hexToColor("#FAFAFA").getRGB()));
        cell.setBorder(0);
        cell.setBorderWidthBottom(1);
        cell.setBorderColorBottom(new BaseColor(hexToColor("#F3F3F4").getRGB()));
        return cell;
    }

    /*//TODO: New
    *//**
     * @return A {@link LinkedList} of {@link BaseColor}s: A {@link LinkedList} containing the main
     * {@link BaseColor colors} of the selected image
     *//*
    private static LinkedList<BaseColor> getBaseColors() {
        LinkedList<BaseColor> schemeColors = new LinkedList<>();
        for (int i = 0; i < getCentroids(); i++) {
            Point3D p = getColors().get(i);
            int x = (int) p.getX();
            int y = (int) p.getY();
            int z = (int) p.getZ();
            BaseColor color = new BaseColor(x, y, z);
            schemeColors.add(color);
        }
        return schemeColors;
    }*/

    //TODO: Add JavaDoc
    private static Color checkContrastAWT(BaseColor color) {
        BaseColor col = checkContrast(color);
        return new Color(col.getRed(), col.getGreen(), col.getBlue());
    }

    //TODO: Add JavaDoc
    private static BaseColor checkContrast(BaseColor color) {
        //check against white
        double whiteContrast = ContrastChecker.getConstrastRatio(color, BaseColor.WHITE);

        //check against black
        double blackContrast = ContrastChecker.getConstrastRatio(color, BaseColor.BLACK);

        //compare contrast
        if (whiteContrast > blackContrast) {
            return BaseColor.WHITE;
        }
        return BaseColor.BLACK;
    }

    //TODO: Add JavaDoc
    private static String getHex(BaseColor color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    //TODO: Add JavaDoc
    private static Font getMulish(float fontSize) {
        return FontFactory.getFont("mulish regular", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, BaseColor.BLACK);
    }

    //TODO: Add JavaDoc
    private static Font getMulish(float fontSize, String fontWeight, Color color) {
        return FontFactory.getFont("src/main/resources/de/colorscheme/main/fonts/Mulish-" + fontWeight + ".ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, new BaseColor(color.getRGB()));
    }

    //TODO: Add JavaDoc
    private static void checkNewPage(Document doc) {
        if (writer.getVerticalPosition(true) < 250) {
            doc.newPage();
        }
    }

    //TODO: Add JavaDoc
    private static void checkNewPage(Document doc, double h) {
        if (writer.getVerticalPosition(true) - h < 61F) {
            doc.newPage();
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

    //TODO: New
    /**
     * Adds the description of the average HSB values of the main colors to the {@link Document}.
     *
     * @param hsbColors A {@link Float} array: The average HSB values of the main colors
     * @return A {@link String} array: The description of the average HSB values of the main colors
     */
    private static String[] getAverage(float[] hsbColors) {
        hsbColors[0] /= getCentroids();
        hsbColors[1] /= getCentroids();
        hsbColors[2] /= getCentroids();

        String color = getResBundle().getString("avgColorPre") +
                determineHue(hsbColors[0]);

        String saturation = (hsbColors[1] < 0.5 ? getResBundle().getString("avgSaturationUnSat")
                : getResBundle().getString("avgSaturationSat"))
                .concat(String.format(" (%s %.2f %%)",
                        getResBundle().getString("avgSaturation"),
                        hsbColors[1] * 100));

        String brightness = (hsbColors[2] < 0.5 ? getResBundle().getString("avgBrightnessDark")
                : getResBundle().getString("avgBrightnessLight"))
                .concat(String.format(" (%s %.2f %%)",
                        getResBundle().getString("avgBrightness"),
                        hsbColors[2] * 100));
        return new String[]{color, saturation, brightness};
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

    //TODO: Add JavaDoc
    private static float[] hexToRgb(String hex) {
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

    //TODO: Add JavaDoc
    private static Color hexToColor(String hex) {
        float[] rgb = hexToRgb(hex);
        return new Color((int) rgb[0], (int) rgb[1], (int) rgb[2]);
    }
}
