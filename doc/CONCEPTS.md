# Concepts used in the program

## K-Means Clustering
K-Means Clustering is a method for grouping a number of values into a set amount of groups. 
The amount of values has to be smaller or equal to the amount of groups. 
Each value belongs to the group with the mean value that's closest to the value. This mean is called a centroid.
K-Means aims to minimize the difference of the values within the groups to the mean value of the group to end up with groups of values that are similar to each other.
### Algorithm
The algorithm repeats until the minimum variance has been reached and works as follows:
1. Define an amount of groups
2. Select random values as the initial centroids
3. Each observation is assigned to the cluster with the nearest mean value, in this case, this is determined by calculating the Euclidean distance. This is a method to determine the distance between two points in a two- or three-dimensional space. An observation can only be assigned to one cluster at once.
4. Now, the centroids will be recalculated, as this value will have changed after the assignment of the observations. The new centroid is the mean of all observations that are assigned to the cluster.
5. Repeat step 3 and 4, until the assignments no longer change or the minimum variance has been reached

### K-Means++
K-Means++ is an algorithm that is used to determine the initial centroids. This algorithm is used to prevent the initial centroids from being chosen poorly, meaning they might be close to each other, which can result in a bad clustering. The algorithm works as follows:
1. Choose one centroid at random from the values
2. For each value that is not a centroid, calculate the distance between the point and the nearest centroid.
3. The value that's furthest away from the centroids is chosen as the next centroid. This is determined by calculating the fitness of each value using a method called Roulette Wheel Selection.
4. Repeat Steps 2 and 3 until the chosen amount of centroids has been chosen.
5. Now that the initial centroids have been chosen, proceed using standard K-Means.

### Euclidean Distance

## Fitness
The fitness of a value is a value that determines how likely it is to be chosen. The higher the fitness, the more likely it is to be chosen. The fitness is calculated using a method called Roulette Wheel Selection. It is inspired by a roulette wheel, but has some important differences. 
A regular roulette wheel has slots of the same size, meaning each slot has the same chance of being chosen. 
In Roulette Wheel Selection, the size of the slots is determined by the fitness of the values. The higher the fitness, the bigger the slot. This means that values with a higher fitness are more likely to be chosen.
This fitness will be calculated by setting the fitness of the currently inspected value to the highest number possible. If there are no other values, this value will be chosen as there are no other values to choose from. 
If there are other values, the distance between the currently inspected value and the centroids will be calculated and set as the values' fitness. If another centroid is closer to the value, the fitness will be set to the distance between the value and this centroid. This ensures, that a values' fitness depends on the distance to all centroids.
To determine the new centroid, the fitness of each value, that is not a centroid will be summed up. Then, a random number between 0 and this sum will be chosen. This number is now the threshold. Now, all values will be summed up again, one after another. If adding a values fitness to the sum would surpass the threshold, this centroid will be selected as the new centroid.

## 3D Color Spaces