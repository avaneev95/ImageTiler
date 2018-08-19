package ru.avaneev.imagetiler.generator;

/**
 * The helper class to calculate the values ​​needed during tiles generation.
 *
 * @author Andrey Vaneev
 * Creation date: 18.08.2018
 */
public class MapHelper {

    private MapHelper() {
    }

    /**
     * Returns a number of tiles on a side on the specific zoom level.
     */
    static int tilesOnSideCount(int tileSize, int zoomLevel) {
        return mapSize(tileSize, zoomLevel) / tileSize;
    }

    /**
     * Return the total count of tiles on the specific zoom level.
     */
    static int totalTilesCount(int maxZoomLevel) {
        return (int) (1 + 4 * (1 - Math.pow(4, maxZoomLevel)) / -3);
    }

    /**
     * Return the size of the image on the specific zoom level.
     */
    static int mapSize(int tileSize, int zoomLevel) {
        return tileSize * (int) Math.pow(2, zoomLevel);
    }

    /**
     * Calculates the optimal zoom level based on provided tile size and image width/height
     */
    public static int optimalZoomLevel(int tileSize, int imageWidth) {
        return (int) (Math.ceil(log2(imageWidth)) - Math.round(log2(tileSize)));
    }

    private static double log2(double n) {
        return Math.log(n) / Math.log(2);
    }
}
