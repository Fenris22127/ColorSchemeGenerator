package de.colorscheme.output;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import de.colorscheme.app.App;
import de.colorscheme.clustering.ColorData;
import de.customlogger.logger.ColorLogger;
import javafx.geometry.Point3D;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.logging.Logger;

import static de.colorscheme.clustering.KMeans.getCentroids;
import static java.util.logging.Level.SEVERE;

/**
 * Writes the color scheme of the selected image into a pdf containing the image, image name, colours
 * and their rgb- and hex-code
 *
 * @author &copy; 2022 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.1
 * @since 17.0.1
 */
public class OutputColors {

    /**
     * Creates a {@link ColorLogger Logger} for this class
     */
    private static final Logger LOGGER = ColorLogger.newLogger(OutputColors.class.getName());

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

    /**
     * Private constructor to hide the public one
     */
    private OutputColors() {}

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
     *         {@link #outputWrite(ColorData, String, String)}  outputWrite()} is called.
     *     </li>
     *     <li>
     *         If a file of that name exists already, a new name is determined by
     *         {@link #getFileName(String) getFileName()}, then the method
     *         {@link #outputWrite(ColorData, String, String) outputWrite()} is called.
     *     </li>
     * </ul>
     * @return a {@link Document}: The PDF file containing the image and its color scheme, or null, if the document
     * couldn't be created
     */
    public static Document createOutput(ColorData c, String imagePath) {
        StringBuilder fileDestinationBuilder = new StringBuilder();
        String imgName = fileName(imagePath);

        String filename = "ColorScheme_" + imgName.substring(0, imgName.lastIndexOf(".")) + ".pdf";

        String finalPath = fileDestinationBuilder.append(App.getDownloadPath()).append("\\").append(filename).toString();

        //Try to create a new file "ColorScheme.pdf"
        try {
            Document colorScheme = new Document();
            File f = new File(finalPath);

            //If a file of the same name exists, find number of files with the same name and set filename accordingly
            if (f.exists()) {
                String newNamePath = getFileName(finalPath);
                outputWrite(c, newNamePath, imagePath);
            }
            //If file object was created successfully and file at destination doesn't exist yet, create new file
            if (!f.exists()) {
                PdfWriter.getInstance(colorScheme, new FileOutputStream(fileDestinationBuilder.toString()));

                outputWrite(c, fileDestinationBuilder.toString(), imagePath);
            }
            return colorScheme;
        }
        catch (DocumentException e) {
            App.getOutputField().setForeground(Color.RED);
            App.getOutputField().setText("An error occurred while trying to write into the created file!");
            App.getTask().cancel(true);

            LOGGER.log(SEVERE, "{0}: Could not instantiate PdfWriter!", e.getClass().getSimpleName());
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            App.getOutputField().setForeground(Color.RED);
            App.getOutputField().setText("An error occurred while trying to create the file!" +
                    "Destination may have been moved or deleted!");
            App.getTask().cancel(true);

            LOGGER.log(SEVERE,"{0}: FileOutputStream could not create file!", e.getClass().getSimpleName());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Determines the file name of a file of the original name exists already. <br>
     *
     * Sets the filename as: Filename (x + 1).pdf with x being the number of files with the same name.
     * @param path a {@link String}: The path at which the file will be saved
     * @return a {@link String}: The path with the updated filename at which the file will now be saved
     */
    private static String getFileName(String path) {
        int fileNumber = 1;
        String filePath = path;
        String name = fileName(path).substring(0, fileName(path).lastIndexOf("."));
        String newName = name;
        String oldName;
        while (new File(filePath).exists()) {
            oldName = newName;
            newName = String.format("%s (%d)", name, fileNumber);
            filePath = filePath.replace(oldName, newName);
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
     *         {@link Document#open() Opens} the document and {@link #addContent(ColorData, Document, String) adds} the
     *         content before {@link Document#close() closing} it again.
     *     </li>
     * </ol>
     * @param c A {@link ColorData} object: The instance used for all processes for the currently inspected image
     * @param path A {@link String}: The path to the location, where the color scheme file will be saved
     * @param image A {@link String}: The path to the selected image
     */
    private static void outputWrite(ColorData c, String path, String image) {
        try {
            Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            addContent(c, doc, image);
            doc.close();
        }
        catch (IOException e) {
            App.getOutputField().setForeground(Color.RED);
            App.getOutputField().setText("An error occurred while trying to access the created file! " +
                    "File may have been moved or access may be denied by a Security Manager!");
            App.getTask().cancel(true);

            LOGGER.log(SEVERE,"{0}: FileOutputStream could not access created document!",
                    e.getClass().getSimpleName());
            e.printStackTrace();
        }
        catch (DocumentException e) {
            App.getOutputField().setForeground(Color.RED);
            App.getOutputField().setText("An error occurred while trying to write into the created file!");
            App.getTask().cancel(true);

            LOGGER.log(SEVERE,"{0}: Could not instantiate PdfWriter!",
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
     * @param c A {@link ColorData} object: The instance used for all processes for the currently inspected image
     * @param doc A {@link Document}: The {@link Document} to be written in
     * @param image A {@link String}: The path to the selected image
     */
    private static void addContent(ColorData c, Document doc, String image) {
        try {
            doc.newPage();
            doc.setMargins(0, 0, 0, 0);
            LinkedList<BaseColor> colors = getColors(c);

            Paragraph title = new Paragraph("Color Scheme", header);
            title.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(title, 1);
            doc.add(title);

            Paragraph docImg = new Paragraph(fileName(image), small);
            docImg.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(docImg, 1);
            doc.add(docImg);

            Rectangle pageSize = new Rectangle(doc.getPageSize());
            float width = pageSize.getWidth();
            float height = pageSize.getHeight();
            float width40 = width / 10 * 4;
            float height40 = height / 10 * 4;
            Image img = Image.getInstance(image);
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleToFit(width40, height40);
            doc.add(img);

            Paragraph space = new Paragraph();
            addEmptyLine(space, 1);
            doc.add(space);

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
                String rgb = "rgb: " + red + ", " + green + ", " + blue;
                String hex = String.format("#%02X%02X%02X", red, green, blue);
                Paragraph p = new Paragraph();
                p.setFont(regular);
                p.add(rgb);
                p.add(new Chunk(System.lineSeparator().concat(System.lineSeparator())));
                p.add(hex);
                p.setAlignment(Element.ALIGN_CENTER);
                PdfPCell cell = new PdfPCell(p);
                cell.setFixedHeight(50);
                table.addCell(cell);
            }
            doc.add(table);

            if (ColorWheel.createColorWheel(colors)) {
                Image colorWheel = Image.getInstance("src/main/resources/SchemeWheel.png");
                colorWheel.setAlignment(Element.ALIGN_CENTER);
                colorWheel.scaleToFit(width40, height40);
                doc.add(colorWheel);
            }
        }
        catch (DocumentException e) {
            App.getOutputField().setForeground(Color.RED);
            App.getOutputField().setText("An error occurred while adding elements to the created file!");
            App.getTask().cancel(true);

            Logger.getAnonymousLogger().log(SEVERE,"{}: Could not add element to created document!",
                    e.getClass().getSimpleName());
            e.printStackTrace();
        }
        catch (IOException e) {
        App.getOutputField().setForeground(Color.RED);
        App.getOutputField().setText("An error occurred while getting the chosen image to display it in the created file! " +
                "Couldn't read image!");
        App.getTask().cancel(true);

            Logger.getAnonymousLogger().log(SEVERE,"{}: Could not read image to display in created document!",
                    e.getClass().getSimpleName());
        e.printStackTrace();
        }
    }

    /**
     * Creates and returns a {@link LinkedList} containing the main {@link BaseColor colors} of the selected image
     * determined in {@link ColorData} and {@link de.colorscheme.clustering.KMeans KMeans}
     * @param c A {@link ColorData} object: The instance used for all processes for the currently inspected image
     * @return  A {@link LinkedList} of {@link BaseColor}s: A {@link LinkedList} containing the main
     *          {@link BaseColor colors} of the selected image
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
     * @param paragraph A {@link Paragraph}: The target {@link Paragraph} for the empty lines
     * @param number An {@link Integer}: The amount of empty lines to be added
     */
    //@SuppressWarnings("SameParameterValue")
    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    /**
     * Gets the name of the file at the specified path.
     * @param path A {@link String}: The path of the target file
     * @return A {@link String}: The name of the file at the specified path
     */
    protected static String fileName(String path) {
        return new File(path).getName();
    }
}
