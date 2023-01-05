package de.colorscheme.clustering;

import de.colorscheme.app.App;
import javafx.geometry.Point3D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.colorscheme.app.App.outputField;
import static de.colorscheme.app.App.task;
import static java.util.logging.Level.INFO;

/**
 * Reads an image and stores all pixels rgb-values <br>
 * Contains the methods required in {@link KMeans}
 *
 * @author &copy; 2022 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.2
 * @since 17.0.1
 */
public class ColorData {

    /**
     * Creates a {@link Logger} for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ColorData.class.getName());

    /**
     * An Instance of the '{@link Random} class used to generate random values
     */
    private static final Random random = new Random();

    /**
     * Used to store the amount of pixels in the image to test for possible errors while recording pixels to list
     */
    private double pixelCount = 0;

    /**
     * {@link LinkedList} storing {@link Pixels Pixels} objects, created from each pixel's {@link Color#getRGB() color}
     */
    protected final LinkedList<Pixels> pixelColor = new LinkedList<>();

    /**
     * {@link LinkedList} storing {@link Integer Integers} with the indices of Centroids
     */
    private final LinkedList<Integer> indicesOfCentroids = new LinkedList<>();

    /**
     * The {@link LinkedList List} containing the calculated centroids
     */
    private final LinkedList<Point3D> centroids = new LinkedList<>();

    /**
     * A {@link Point3D} object storing the minimums for each coordinate <br>
     * If the minimum is set to <code color="#B5B5B5">(x: -1, y: -1, z: -1)</code>,
     * the minimum has <b color="#B22222">not been set</b> yet, as the value for each color must be
     * between <span color="#6897BB">0</span> and <span color="#6897BB">255</span>.
     */
    private Point3D minimum = new Point3D(-1, -1, -1);

    /**
     * A {@link Point3D} object storing the maximums for each coordinate <br>
     * If the maximum is set to <code color="#B5B5B5">(x: 256, y: 256, z: 256)</code>,
     * the maximum has <b color="#B22222">not been set</b> yet, as the value for each color must be
     * between <span color="#6897BB">0</span> and <span color="#6897BB">255</span>.
     */
    private Point3D maximum = new Point3D(256, 256, 256);

    /**
     * Empty default constructor
     */
    protected ColorData() {
    }

    /**
     * ✓ <i>Successfully reads data from image as Point3D's to pixel into list</i> <br>
     * <b>Alternate constructor invocation:</b><br>
     * Reads content of provided file via {@link ImageIO} into a {@link BufferedImage} and traverses it pixel by pixel,
     * determining the {@link Color} of each pixel. <br>
     * Translates r, g and b values of the current pixel's color to value
     * for x, y and z coordinates of a {@link Point3D} object. <br>
     * Updates the {@link #minimum} and {@link #maximum} values for x, y and z. <br>
     * Adds the {@link Point3D} object to the {@link LinkedList List} {@link #pixelColor} storing all pixels
     * translated to {@link Point3D} objects. <br>
     * Checks, if the {@link #pixelCount total amount of pixels} determined by
     * <span color="#6897BB">width * height</span> match the number of elements stored in {@link #pixelColor}. <br>
     * If the {@link #pixelCount total amount of pixels} and the amount of pixels stored in {@link #pixelColor} differ,
     * a custom exception {@link PixelListSizeException PixelListSizeException} is thrown and the
     * {@link javax.swing.SwingWorker SwingWorker} in {@link de.colorscheme.app.StartProcess StartProcess} is
     * cancelled. The user is informed about
     * the error via the {@link App#outputField textfield}.
     *
     * @param image A {@link BufferedImage}: The image to be read
     **/
    public ColorData(BufferedImage image) {
        try {
            double width = image.getWidth();
            double height = image.getHeight();

            //Read pixel to color, then to Point3D and store in list
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = new Color(image.getRGB(x, y));
                    Point3D p = new Point3D(c.getRed(), c.getGreen(), c.getBlue());

                    pixelColor.add(new Pixels(p));
                    updateMax(p);
                    updateMin(p);
                }
            }
            pixelCount = width * height;
            //compare recorded pixels and total pixels
            if (pixelCount != pixelColor.size()) {
                throw new PixelListSizeException("");
            }
        }
        //recorded pixels and total pixels in image differ
        catch (PixelListSizeException e) {
            outputField.setForeground(Color.RED);
            outputField.setText("An error occurred while getting the pixels in the image! " +
                    "Expected and actual amount of pixels differ!" + System.lineSeparator() + "Expected: " +
                    (int) pixelCount + System.lineSeparator() + "Actual: " + pixelColor.size());
            task.cancel(true);

            LOGGER.log(Level.SEVERE, String.format("%s: Expected and actual amount of pixels differ! Expected: %e%nActual: %d%n",
                    e.getClass().getSimpleName(), pixelCount, pixelColor.size()));
            e.printStackTrace();
        }
    }

    /**
     * Scales the passed image to fit the given boundary without losing its aspect ratio
     *
     * @param img The image to be scaled
     * @return A {@link Dimension} Object, containing the new dimensions of the image
     */
    public static BufferedImage resize(BufferedImage img) {
        Dimension imgSize = new Dimension(img.getWidth(), img.getHeight());

        int originalWidth = imgSize.width;
        int originalHeight = imgSize.height;
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // first check if we need to scale width
        if (originalWidth > originalHeight) {
            //scale width to fit
            newWidth = 150;
            //scale height to maintain aspect ratio
            newHeight = (newWidth * originalHeight) / originalWidth;
        }
        // first check if we need to scale width
        if (originalWidth < originalHeight) {
            //scale width to fit
            newHeight = 150;
            //scale height to maintain aspect ratio
            newWidth = (newHeight * originalWidth) / originalHeight;
        }
        if (originalWidth == originalHeight) {
            newHeight = 150;
            newWidth = 150;
        }
        return convertToBufferedImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH));
    }

    /**
     * Converts an {@link Image} to a {@link BufferedImage}
     *
     * @param img The {@link Image} to be converted
     * @return A {@link BufferedImage} - The {@link Image} converted to a {@link BufferedImage}
     */
    protected static BufferedImage convertToBufferedImage(Image img) {

        if (img instanceof BufferedImage bImg) {
            return bImg;
        }
        // Create a buffered image with transparency
        BufferedImage bi = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return bi;
    }

    /**
     * Calculates the Euclidean Distance between two points in a 3D-space
     *
     * @param p1 The first {@link Point3D}
     * @param p2 The second {@link Point3D}
     * @return A double - The distance between the two points
     */
    protected static Double euclideanDistance(Point3D p1, Point3D p2) {
        double x1 = p1.getX();
        double y1 = p1.getY();
        double z1 = p1.getZ();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double z2 = p2.getZ();
        return Math.sqrt(
                ((x2 - x1) * (x2 - x1)) +
                        ((y2 - y1) * (y2 - y1)) +
                        ((z2 - z1) * (z2 - z1)));
    }

    /**
     * ✓ <i>Successfully updates Minimum depending on whether Minimum has already been set or not</i> <br>
     * Updates the {@link Point3D} object {@link #minimum} with new minimums for x, y and/or z.
     * <ul>
     *     <li>
     *         Gets the {@link Point3D#getX() x}, {@link Point3D#getY() y} and {@link Point3D#getZ() z} values of
     *         the {@link Point3D point} given to the method
     *     </li>
     *     <li>
     *         If no minimum has been set yet, meaning {@link #minimum} is
     *         <code color="#B5B5B5">(x: -1, y: -1, z: -1</code>, the {@link Point3D point} given to the method will
     *         be set as the new {@link #minimum}
     *     </li>
     *     <li>
     *         If {@link #minimum} has been set already, check, if the {@link Point3D point} given to the method
     *         contains smaller coordinates
     *     </li>
     *     <li>
     *         If the {@link Point3D point} given to the method contains smaller coordinates, update the values of
     *         {@link #minimum} to the smaller values of the {@link Point3D point} given to the method
     *     </li>
     * </ul>
     *
     * @param p A {@link Point3D} object: Contains the {@link #minimum} for each axis' coordinates
     **/
    protected void updateMin(Point3D p) {
        //get each coordinate of point
        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();

        //if no minimum has been set yet, set current point to minimum
        if (minimum.equals(new Point3D(-1, -1, -1))) {
            minimum = p;
        }
        //check for smaller coordinates and update minimum accordingly
        else {
            if (x < minimum.getX() ||
                    y < minimum.getY() ||
                    z < minimum.getZ()) {
                double minX = minimum.getX();
                double minY = minimum.getY();
                double minZ = minimum.getZ();

                if (x < minX) {
                    minX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (z < minZ) {
                    minZ = z;
                }
                minimum = new Point3D(minX, minY, minZ);
            }
        }
    }

    /**
     * ✓ <i>Successfully updates Maximum depending on whether Maximum has already been set or not</i> <br>
     * Updates the {@link Point3D} object {@link #maximum} with new maximums for x, y and/or z.
     * <ul>
     *     <li>
     *         Gets the {@link Point3D#getX() x}, {@link Point3D#getY() y} and {@link Point3D#getZ() z} values of
     *         the {@link Point3D point} given to the method
     *     </li>
     *     <li>
     *         If no maximum has been set yet, meaning {@link #maximum} is
     *         <code color="#B5B5B5">(x: 256, y: -256, z: -256</code>, the {@link Point3D point} given to the method
     *         will be set as the new {@link #maximum}
     *     </li>
     *     <li>
     *         If {@link #maximum} has been set already, check, if the {@link Point3D point} given to the method
     *         contains larger coordinates
     *     </li>
     *     <li>
     *         If the {@link Point3D point} given to the method contains larger coordinates, update the values of
     *         {@link #maximum} to the larger values of the {@link Point3D point} given to the method
     *     </li>
     * </ul>
     *
     * @param p A {@link Point3D} object: Contains the {@link #maximum} for each axis' coordinates
     **/
    protected void updateMax(Point3D p) {
        //get each coordinate of point
        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();

        //if no maximum has been set yet, set current point to maximum
        if (maximum.equals(new Point3D(256, 256, 256))) {
            maximum = p;
        }
        //check for larger coordinates and update maximum accordingly
        else {
            if (x > maximum.getX() ||
                    y > maximum.getY() ||
                    z > maximum.getZ()) {
                double maxX = maximum.getX();
                double maxY = maximum.getY();
                double maxZ = maximum.getZ();

                if (x > maxX) {
                    maxX = x;
                }
                if (y > maxY) {
                    maxY = y;
                }
                if (z > maxZ) {
                    maxZ = z;
                }
                maximum = new Point3D(maxX, maxY, maxZ);
            }
        }
    }

    /**
     * Calculates the average values for x, y and z of all {@link Point3D points} in a cluster. <br>
     * Iterates through the elements in the {@link LinkedList List} given to the method. <br>
     * If the current value describing the index of the {@link Point3D point} in that cluster in the
     * {@link #pixelColor List of all pixels} is within the range of indices possible
     * (<code color="#B5B5B5">pixelColor.length()</code>), adds the x, y and z values
     * to the sum of x, y and z values of the other {@link Point3D points} in the cluster. <br>
     * When the iteration is finished, the average value for x, y and z is calculated by dividing the sum by the
     * total amount of pixels in the cluster. <br>
     *
     * @param pointsInCluster List of all {@link Point3D points} in a cluster
     * @return A {@link Point3D} with the average values for x, y and z of the {@link Point3D points} in a cluster
     */
    protected Point3D meanOfAttr(LinkedList<Integer> pointsInCluster) {
        double meanX = 0.0;
        double meanY = 0.0;
        double meanZ = 0.0;

        for (int i : pointsInCluster) {
            if (i < pixelColor.size()) {
                meanX += pixelColor.get(i).getPixel().getX();
                meanY += pixelColor.get(i).getPixel().getY();
                meanZ += pixelColor.get(i).getPixel().getZ();
            }
        }
        meanX = meanX / pointsInCluster.size();
        meanY = meanY / pointsInCluster.size();
        meanZ = meanZ / pointsInCluster.size();

        return new Point3D(meanX, meanY, meanZ);
    }

    /**
     * Recomputes the centroid of a cluster by going through all pixels and adding the pixels with the corresponding
     * cluster number to a {@link LinkedList} <br>
     * Then calculates the centroid by getting the mean of all points in that {@link LinkedList List}
     *
     * @param clusterNr The number of the current cluster
     * @return A {@link Point3D} - The recomputed centroid for that cluster
     */
    protected Point3D calculateCentroid(int clusterNr) {
        Point3D centroid;
        LinkedList<Integer> pointsInCluster = new LinkedList<>();

        for (int i = 0; i < pixelColor.size(); i++) {
            Pixels pixel = pixelColor.get(i);
            if (pixel.clusterNo == clusterNr) {
                pointsInCluster.add(i);
            }
        }
        centroid = meanOfAttr(pointsInCluster);
        return centroid;
    }

    /**
     * Recomputes all centroids by using the {@link #calculateCentroid(int) calculateCentroid()} method <br>
     * Adds the recomputed centroid to the {@link LinkedList} {@link #centroids}
     *
     * @param totalCentroids Total amount of centroids
     */
    protected void recomputeCentroids(int totalCentroids) {
        for (int i = 0; i < totalCentroids; i++) {
            getCentroids().add(calculateCentroid(i));
        }
    }

    /**
     * ✓ <i>Successfully gets a random point from the list of all points saved</i> <br>
     * Gets a random pixel from {@link #pixelColor} by getting a random integer in range of the total number of pixels
     * saved
     *
     * @return A randomly determined {@link Point3D} from the list with all points saved
     */
    @SuppressWarnings("unused")
    protected Point3D randomFromPoint3DList() {
        int index = random.nextInt(pixelColor.size());
        return pixelColor.get(index).getPixel();
    }

    /**
     * Generates a random index between 0 and the total pixels stored in {@link #pixelColor}
     *
     * @return An {@link Integer} - A value pointing to a random element in {@link #pixelColor}
     */
    protected int randomIndexFromPoint3DList() {
        return random.nextInt(pixelColor.size());
    }

    /**
     * Calculates the sum of distances of every pixel from its nearest centroid
     *
     * @param centroid  The {@link Point3D} centroid of the observed cluster
     * @param clusterNo The number of the observed cluster
     * @return A {@link Double} - The squared distance of each pixel in the cluster from its centroid
     */
    private Double calculateClusterSumSquaredDistance(Point3D centroid, int clusterNo) {
        double sumSquaredDistance = 0.0;

        for (Pixels pixels : pixelColor) {
            if (pixels.clusterNo == clusterNo) {
                sumSquaredDistance += Math.pow(euclideanDistance(centroid, pixels.getPixel()), 2);
            }
        }
        return sumSquaredDistance;
    }

    /**
     * Calculates the sum of distances of every pixel from its nearest centroid for each cluster by using
     * {@link #calculateClusterSumSquaredDistance(Point3D, int) calculateClusterSumSquaredDistance()}
     *
     * @param centroids The {@link LinkedList} containing the centroids
     * @return A {@link Double} - The squared distance of each pixel in all clusters from its centroid
     */
    protected Double calculateTotalSumSquaredDistances(LinkedList<Point3D> centroids) {

        Double sumSquaredError = 0.0;

        for (int i = 0; i < centroids.size(); i++) {
            sumSquaredError += calculateClusterSumSquaredDistance(centroids.get(i), i);
        }
        return sumSquaredError;
    }

    /**
     * ✓ <i>Successfully calculates centroids as far away from each other as possible</i> <br>
     * Calculates a weighted centroid based on roulette wheel selection with the fitness being the distance of a pixel
     * to its nearest centroid
     * <ol>
     *     <li>
     *         Sets the sum of the total fitness of all pixels to 0
     *     </li>
     *     <li>
     *         Calculates each pixel's fitness and adds it to the sum
     *     </li>
     *     <li>
     *         Sets a random threshold between 0 and the total fitness of all pixels, if the threshold is surpassed,
     *         the pixel surpassing it will be selected as a centroid
     *     </li>
     *     <li>
     *         Sets the sum of the fitness of all pixels, that have been added without surpassing the threshold, to 0
     *     </li>
     *     <li>
     *         Iterates through list of all pixels again and adds their fitness to the partial sum
     *     </li>
     *     <li>
     *         If the sum now surpasses the threshold, the current pixel is chosen as the centroid
     *     </li>
     * </ol>
     *
     * @return A {@link Point3D} - The point selected as a centroid <b>OR</b>
     * <code color="#B5B5B5">(x: -1, y: -1, z: -1)</code> if an error occurred and no centroid could be calculated
     */
    protected Point3D calculateWeighedCentroid() {

        //Total sum of fitness
        double totalSum = 0.0;

        //Calculate total sum of fitness: Only pixels that are not centroids will be added to the sum
        for (int currentPxIndex = (pixelColor.size() - 1); currentPxIndex >= 0; currentPxIndex--) {
            if (!indicesOfCentroids.contains(currentPxIndex)) {
                totalSum += fitness(currentPxIndex);
            }
        }

        //Set threshold: If crossed, the element crossing it will be selected
        double threshold = random.nextDouble(0, totalSum);

        //The sum of all elements together until threshold is reached
        double currentSum = 0.0;

        //Select pixel based on chance and fitness: Only pixels that are not centroids may be selected
        for (int currentPxIndex = (pixelColor.size() - 1); currentPxIndex >= 0; currentPxIndex--) {
            if (!indicesOfCentroids.contains(currentPxIndex)) {

                //Add fitness of currently selected element to partial sum of previous elements fitness
                currentSum += fitness(currentPxIndex);

                //Check, if sum added fitness of current element exceeds the threshold and return pixel, if it does
                if (currentSum >= threshold) {
                    indicesOfCentroids.add(currentPxIndex);
                    return pixelColor.get(currentPxIndex).getPixel();
                }
            }
        }
        return new Point3D(-1, -1, -1);
    }

    /**
     * ✓ <i>Successfully calculates a pixels fitness to be a centroid</i> <br>
     * Calculates a pixel's fitness for being selected as a centroid by calculating its distance to the nearest
     * centroid. <br>
     * <b>Used in:</b> {@link #calculateWeighedCentroid()} <br>
     * <b>To:</b> Calculate the fitness of a pixel when determining a weighted centroid <br>
     * <ol>
     *     <li>
     *         Set the value for the fitness (equal to the distance to the closest centroid) to the highest value
     *         possible
     *     </li>
     *     <li>
     *         Checks, if centroids have been determined yet
     *     </li>
     *     <li>
     *         If no centroids have been set, set the pixels value to the maximum
     *     </li>
     *     <li>
     *         Iterate through all centroids and calculate the distance of the current pixel to the current centroid
     *     </li>
     *     <li>
     *         If the value of the distance of the current centroid the current pixel is less than the current fitness,
     *         set the fitness to the distance between current centroid and pixel
     *     </li>
     *     <li>
     *         After determining the fitness, by finding the closest centroid, return the distance to the closest
     *         centroid as the value of the fitness of that pixel
     *     </li>
     * </ol>
     *
     * @param pixelNr The index of the pixel that has its fitness determined in the method
     * @return A {@link Double} - The pixel's fitness for being selected as a centroid
     */
    private double fitness(int pixelNr) {
        double distClosestCentroid = Double.MAX_VALUE;

        //if no centroids have been set yet, return highest number
        //(no centroids = no closest centroid to be found)
        if (indicesOfCentroids.isEmpty()) {
            return distClosestCentroid;
        }
        //iterate through all centroids to find centroid closest to current pixel
        for (int centroid : indicesOfCentroids) {

            //calculate distance between pixel and current centroid
            double dist = euclideanDistance(pixelColor.get(pixelNr).getPixel(), pixelColor.get(centroid).getPixel());

            //closer centroid found
            if (dist < distClosestCentroid) {
                distClosestCentroid = dist;
            }
        }
        return distClosestCentroid;
    }

    /**
     * Gets the {@link LinkedList} {@link #pixelColor}
     *
     * @return A {@link LinkedList} containing all recorded pixels
     */
    protected LinkedList<Pixels> getPixels() {
        return pixelColor;
    }

    /**
     * Get the minimum value of all pixels
     *
     * @return A {@link Point3D} - The minimum
     */
    protected Point3D getMin() {
        return minimum;
    }

    /**
     * Get the maximum value of all pixels
     *
     * @return A {@link Point3D} - The maximum
     */
    protected Point3D getMax() {
        return maximum;
    }

    /**
     * Gets the {@link LinkedList} {@link #indicesOfCentroids}
     *
     * @return A {@link LinkedList} containing all indices of centroids
     */
    protected LinkedList<Integer> getIndicesOfCentroids() {
        return indicesOfCentroids;
    }

    /**
     * Gets the {@link LinkedList} {@link #centroids}
     *
     * @return A {@link LinkedList} containing all centroids
     */
    public LinkedList<Point3D> getCentroids() {
        return centroids;
    }

    /**
     * ✓ <i>Successfully reads data from {@link #pixelColor}</i> <br>
     * Prints a {@link Point3D} as a Pixel with RBG values
     */
    @SuppressWarnings("unused")
    protected void pixelColorToString() {
        double red;
        double green;
        double blue;
        int pixelNr = 0;
        for (Pixels px : pixelColor) {
            Point3D pt = px.pixel;
            red = pt.getX();
            green = pt.getY();
            blue = pt.getZ();
            pixelNr++;
           LOGGER.log(INFO,"Pixel #{0}: R:{1}, G:{2}, B:{3}", new Object[]{pixelNr, red, green, blue});
        }
    }

    /**
     * An inner class, storing:
     * <ul>
     *     <li>
     *         {@link Point3D} - A pixel with its rgb colors as the x, y and z value
     *     </li>
     *     <li>
     *         {@link Integer} - The number of the pixels cluster
     *     </li>
     * </ul>
     * That class also contains:
     * <ul>
     *     <li>
     *         An alternate constructor invocation to set a pixel
     *     </li>
     *     <li>
     *         A setter for the {@link Pixels#clusterNo cluster number}
     *     </li>
     *     <li>
     *         A getter for the {@link Point3D} {@link Pixels#pixel pixel}
     *     </li>
     * </ul>
     */
    static class Pixels {
        /**
         * {@link Point3D} storing a pixel's colour with {@link Color#getRed() red} as the {@link Point3D#getX() x},
         * {@link Color#getGreen() green} as the {@link Point3D#getY() y}
         * and {@link Color#getBlue() blue} as the {@link Point3D#getZ() z} coordinates
         */
        protected Point3D pixel;

        /**
         * Number of the cluster a pixel is assigned to
         */
        protected Integer clusterNo;

        /**
         * Alternate constructor invocation for the {@link Pixels Pixels} class
         *
         * @param pixel - A {@link Point3D}: Stores red value as x, blue as y and green as z coordinates
         **/
        protected Pixels(Point3D pixel) {
            this.pixel = pixel;
        }

        /**
         * Setter for the Integer {@link #clusterNo}
         *
         * @param newClusterNr An {@link Integer}: Sets {@link #clusterNo} to its value
         **/
        protected void setClusterNo(Integer newClusterNr) {
            this.clusterNo = newClusterNr;
        }

        /**
         * Getter for the {@link Point3D} object {@link #pixel}
         **/
        protected Point3D getPixel() {
            return pixel;
        }
    }

    /**
     * Custom Exception for when expected and actual amount of pixels differ
     */
    private static class PixelListSizeException extends Exception {
        public PixelListSizeException(String errorMessage) {
            super(errorMessage);
        }
    }


}
