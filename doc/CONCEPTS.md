# Concepts used in the program

## K-Means Clustering
* method for grouping n observations into k clusters
* each observation belongs to the cluster with the mean value that's closest to the observations value
* aims to minimize variances of the values of the observations within the clusters to the mean value of the cluster
* a set of observations (each observation being multidimensional)
* clusters <= the amount of observations
* mean of a cluster is called a centroid
1. assign each observation to the cluster with the nearest mean value (euclidean distance)
2. an observation can only be assigned to one cluster at once
3. recalculate the mean value (centroid)
4. repeat 1.-3. until the assignments no longer change/the minimum variance has been reached
### K-Means++
* spreading out the k initial cluster centers
1. Choose one center uniformly at random among the data points.
2. For each data point not chosen yet, compute the distance between the point and the nearest center that has already been chosen.
3. Choose one new data point at random as a new center, using a weighted probability distribution where a point is chosen with probability proportional to D(x)2.
4. Repeat Steps 2 and 3 until k centers have been chosen.
5. Now that the initial centers have been chosen, proceed using standard
### Euclidean Distance

## Fitness

## 3D Color Spaces