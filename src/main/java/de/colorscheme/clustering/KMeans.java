package de.colorscheme.clustering;

import javafx.geometry.Point3D;

import java.util.AbstractCollection;

import static de.colorscheme.app.AppController.*;
import static de.colorscheme.app.NewController.getResBundle;

/**
 * The class providing the methods for the KMeans clustering process used to determine the image's main colours. <br>
 *
 * @author &copy; 2023 Elisa Johanna Woelk | elisa-johanna.woelk@outlook.de | @fenris_22127
 * @version 1.2
 * @since 17.0.1
 */
public class KMeans {

    /**
     * The value, deciding when the amount of changes after recalculating the centroids is low enough to finish the
     * clustering process
     */
    private static final Double PRECISION = 0.0;

    /**
     * The amount of centroids to be generated for KMeans, determining how many colors will be generated from the image
     */
    private static int centroids = 0;

    /**
     * Private constructor to hide the public one
     */
    private KMeans() {
    }

    /**
     * Returns amount of centroids to be generated for KMeans
     *
     * @return An {@link Integer}: The value of {@link #centroids}
     */
    public static int getCentroids() {
        return centroids;
    }

    /**
     * K-Means++ implementation: initializes centroids from data by selecting a
     * {@link ColorData#randomIndexFromPoint3DList() random data point} from the {@link ColorData#pixelColor list}
     * <ol>
     *     <li>
     *         Gets a {@link ColorData#randomIndexFromPoint3DList() random data point} from the
     *         {@link ColorData#pixelColor list} as the first centroid.
     *     </li>
     *     <li>
     *         Adds the index of the first centroid to the
     *         {@link ColorData#getIndicesOfCentroids() list of indices of centroids}.
     *     </li>
     *     <li>
     *         Adds the {@link ColorData.Pixels#pixel pixel} at the randomly chosen index
     *         to the {@link ColorData#getCentroids() list of centroids}.
     *     </li>
     *     <li>
     *         Calculates the specified amount of weighted centroids by using
     *         {@link ColorData#calculateWeighedCentroid() calculateWeighedCentroid()} amd adds them to the
     *         {@link ColorData#getCentroids() list of centroids}.
     *     </li>
     * </ol>
     *
     * @param colorData      An Instance of the class {@link ColorData}
     * @param totalCentroids An {@link Integer} - The number of total centroids to be calculated
     */
    protected static void kMeansPlusPlus(ColorData colorData, int totalCentroids) {
        //Calculate random index for starting centroid
        int randomIndex = colorData.randomIndexFromPoint3DList();
        //Add index of chosen pixel to list with indices of centroids
        colorData.getIndicesOfCentroids().add(randomIndex);
        //Add pixel at previously chosen index to list with centroids
        colorData.getCentroids().add(colorData.pixelColor.get(randomIndex).pixel);
        for (int i = 1; i < totalCentroids; i++) {
            colorData.getCentroids().add(colorData.calculateWeighedCentroid());
        }
    }

    /**
     * Clusters the colors from the image's {@link ColorData.Pixels pixels} and calculates
     * a {@link #centroids number of centroids} to determine the main colors of the image.
     * <ol>
     *     <li>
     *         Sets the {@link #centroids number of centroids} to the passed value.
     *     </li>
     *     <li>
     *         Starts the KMeans algorithm with {@link #kMeansPlusPlus(ColorData, int) KMeans++}.
     *     </li>
     *     <li>
     *         If the {@link java.util.LinkedList list} containing the {@link ColorData#getCentroids() centroids}
     *         {@link AbstractCollection#isEmpty() is empty}, the program is ended and the user is informed.
     *     </li>
     *     <li>
     *         The Sum of Squared Errors is initialized by being set to the {@link Double#MAX_VALUE maximum} value for
     *         a {@link Double}.
     *     </li>
     *     <li>
     *         The method then enters a while-true loop that will only end if the Sum of Squared Errors reaches the
     *         value specified by {@link #PRECISION}.
     *     </li>
     *     <li>
     *         Then, the closest centroid for each pixel will be determined by:
     *         <ul>
     *             <li>
     *                 first setting the distance to the closest centroid to the {@link Double#MAX_VALUE maximum} value
     *                 for a {@link Double}.
     *             </li>
     *             <li>
     *                 then iterating through the {@link ColorData#getCentroids() list of centroids} and determining
     *                 the {@link ColorData#euclideanDistance(Point3D, Point3D) distance} of the pixel to the currently
     *                 selected centroid. If the distance is less than the shortest distance so far, the shortest
     *                 distance is set to the newly calculated distance and the pixels cluster number is
     *                 {@link ColorData.Pixels#setClusterNo(Integer) set} to the index of
     *                 the currently selected centroid.
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         All centroids are then recomputed according to the new cluster assignments.
     *     </li>
     *     <li>
     *         Finally, the current Sum of Squared Errors, meaning the amount of changes after recalculating the
     *         centroids, is calculated and tested, whether, when subtracted from the total Sum of Squared Errors, the
     *         result is equal to or less than the {@link #PRECISION}. If so, the while loop is exited.
     *     </li>
     *     <li>
     *         If the {@link #PRECISION} is not reached, the total Sum of Squared Errors is set to the newly calculated
     *         Sum of Squared Errors and the loop repeats.
     *     </li>
     * </ol>
     *
     * @param colorData      A {@link ColorData} object: The instance used for all processes for the currently inspected
     *                       image
     * @param totalCentroids An {@link Integer int}: The amount of centroids to be calculated
     **/
    public static void kMeans(ColorData colorData, int totalCentroids) {
        centroids = totalCentroids;
        kMeansPlusPlus(colorData, centroids);
        if (colorData.getCentroids().isEmpty()) {
            addToOutputField(getResBundle().getString("emptyCentroidList"), true);
            setCancelled(true);
        }

        Double sumSquaredErrors = Double.MAX_VALUE;

        while (true) {
            for (ColorData.Pixels pixel : colorData.getPixels()) {
                double minDist = Double.MAX_VALUE;

                //Iterate through centroids ...
                for (int i = 0; i < colorData.getCentroids().size(); i++) {
                    //...to find the centroid at a minimum distance from it
                    Double dist = ColorData.euclideanDistance(colorData.getCentroids().get(i), pixel.getPixel());

                    //...and add the color value to its cluster
                    if (dist < minDist) {
                        minDist = dist;
                        pixel.setClusterNo(i);
                    }
                }
            }

            //recompute centroids according to new cluster assignments
            colorData.recomputeCentroids(totalCentroids);

            // exit condition, SSE changed less than PRECISION parameter
            Double newSumSquaredErrors = colorData.calculateTotalSumSquaredDistances(colorData.getCentroids());
            if (sumSquaredErrors - newSumSquaredErrors <= PRECISION) {
                break;
            }

            sumSquaredErrors = newSumSquaredErrors;
        }
    }
}


