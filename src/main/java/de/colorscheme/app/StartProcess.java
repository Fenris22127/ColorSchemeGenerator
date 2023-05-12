package de.colorscheme.app;

import de.colorscheme.clustering.ColorData;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static de.colorscheme.app.App.*;
import static de.colorscheme.clustering.ColorData.resize;
import static de.colorscheme.clustering.KMeans.kMeans;
import static de.colorscheme.output.OutputColors.createOutput;

/**
 * Starts the process of generating a color scheme after an image has been selected in {@link SelectImage}
 * while simultaneously updating the GUI according to the current progress.
 *
 * @author &copy; 2023 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.2
 * @since 17.0.1
 */
public class StartProcess extends SwingWorker<Boolean, Integer> {

    /**
     * {@link App#getRessourceLanguage() Gets} the language and sets the {@link ResourceBundle} used for the displayed
     * text accordingly
     */
    private static final String RESSOURCE = getRessourceLanguage();

    /**
     * The name if the file, selected by the user
     */
    Path filePath;

    /**
    * Class constructor containing a setter for the {@link Path} {@link #filePath}
    * @param filePath A {@link Path} - The name of the file chosen by the user
    */
    protected StartProcess(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Executes all background activities while updating the GUI to display the current progress.
     * <ol>
     *    <li>
     *        Sets the progress of the {@link JProgressBar progress bar} to 10% and adds "File found" to the
     *        {@link JTextArea} {@link App#getOutputField() outputField}.
     *    </li>
     *    <li>
     *        Calls {@link #findImage()} to get the {@link BufferedImage image} either in its original size, if it's
     *        below the threshold or the resized image, if it exceeds a certain dimension. <br>
     *        Then sets the progress of the {@link JProgressBar progress bar} to 24% and adds "Reading colours in
     *        image." to the {@link JTextArea} {@link App#getOutputField() outputField}. <br>
     *        To improve the conciseness, the Thread is put to {@link Thread#sleep(long) sleep} for one
     *        second (1000 ms) before starting to read the pixels of the image.
     *    </li>
     *    <li>
     *        Hands the {@link BufferedImage image} over to the constructor of
     *        {@link de.colorscheme.clustering.ColorData ColorData}, where each pixel
     *        will be inspected and its colour saved. <br>
     *        Then, an instance of ColorData is created, that will be used for all following processes and sets the
     *        progress of the {@link JProgressBar progress bar} to 68% and adds "Determining main colours." to the
     *        {@link JTextArea} {@link App#getOutputField() outputField}.
     *    </li>
     *    <li>
     *        Starts K-Means clustering by calling
     *        {@link de.colorscheme.clustering.KMeans#kMeans(ColorData, int) kMeans()} with the
     *        {@link de.colorscheme.clustering.ColorData ColorData} instance and the chosen
     *        {@link App#getSelectedCentroids() amount of centroids}.
     *    </li>
     *    <li>
     *        Then sets the progress of the {@link JProgressBar progress bar} to 86%, adds "Creating the color
     *        scheme." to the {@link JTextArea} {@link App#getOutputField() outputField}, sets the progress to 99% and then
     *        adds "Finishing up..." to the {@link App#getOutputField() outputField}.
     *    </li>
     *    <li>
     *        Again, to improve the conciseness, the Thread is put to {@link Thread#sleep(long) sleep}
     *        for one second (1000 ms), before setting the progress of the {@link JProgressBar progress bar} to 100%
     *        and adding "Done!" to the {@link App#getOutputField() outputField}.
     *    </li>
     *    <li>
     *        Finally, the {@link App#download download button} is enabled and if {@link App#autoDownload autodownload}
     *        is enabled, {@link de.colorscheme.output.OutputColors#createOutput(ColorData, Path)} is called to save the
     *        color scheme.
     *    </li>
     * </ol>
     * @return A {@link Boolean} - True, if method was executed without errors occurring
     * @throws Exception    An {@link Exception} like a {@link com.itextpdf.text.DocumentException DocumentException}
     *                      or {@link IOException} from {@link de.colorscheme.output.OutputColors OutputColors}
     */
    @Override
    protected Boolean doInBackground() throws Exception {
        newProgress(10);
        getOutputField().append(
                ResourceBundle.getBundle(RESSOURCE).getString("startFoundFile")
                        + System.lineSeparator());

        BufferedImage img = findImage();
        newProgress(24);
        getOutputField().append(
                ResourceBundle.getBundle(RESSOURCE).getString("startReadingColours")
                        + System.lineSeparator());
        TimeUnit.MILLISECONDS.sleep(1000);

        ColorData data = new ColorData(img);
        if (!isCancelled()){
            newProgress(68);
            getOutputField().append(ResourceBundle.getBundle(RESSOURCE).getString("startDeterminingColours")
                    + System.lineSeparator());
        }

        if (!isCancelled()) {
            kMeans(data, getSelectedCentroids());
            newProgress(86);
            getOutputField().append(ResourceBundle.getBundle(RESSOURCE).getString("startCreatingScheme")
                    + System.lineSeparator());
        }

        if (!isCancelled()) {
            newProgress(99);
            getOutputField().append(ResourceBundle.getBundle(RESSOURCE).getString("startFinishing")
                    + System.lineSeparator());
        }

        if (!isCancelled()) {
            TimeUnit.MILLISECONDS.sleep(1000);
            newProgress(100);
            getOutputField().append(ResourceBundle.getBundle(RESSOURCE).getString("startDone"));
            download.setEnabled(true);
            setColorData(data);
        }
        if (autoDownload && !isCancelled()) {
            createOutput(getColorData(), fileName);
        }
        return true;
    }

    /**
     * Gets the image from the path saved in {@link App#fileName fileName} by {@link SelectImage#chooseFile()} and
     * resizes it to 150 pixels on the longest side, using the {@link ColorData#resize(BufferedImage) resize} method,
     * while keeping the aspect ratio.
     * @return A {@link BufferedImage} - The image selected by the user in its original size or scaled down, if it
     *          exceeds a certain Dimension
     * @throws IOException An {@link IOException} - If the selected file cannot be read
     */
    private static BufferedImage findImage() throws IOException {
        File file = fileName.toFile();
        BufferedImage image = ImageIO.read(file);
        double width = image.getWidth();
        double height = image.getHeight();
        if (width > 150 || height > 150) {
            image = resize(image);
        }
        return image;
    }

    /**
     * {@link java.beans.PropertyChangeSupport#firePropertyChange(String, int, int) Updates} the progress of the
     * {@link JProgressBar progress bar} to the value specified as a percentage
     * @param progress  An {@link Integer int} - The value as a percentage to update the
     *                  {@link JProgressBar progress bar} to
     */
    protected void newProgress(int progress) {
        firePropertyChange("progress", 0, progress);
    }

}
