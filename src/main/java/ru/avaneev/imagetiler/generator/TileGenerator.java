package ru.avaneev.imagetiler.generator;

import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import ru.avaneev.imagetiler.Options;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Generates the map tiles from the provided image.
 * Can be configured with tile size, max zoom level and output directory.
 *
 * @author Andrey Vaneev
 * Creation date: 18.08.2018
 */
@Slf4j
public class TileGenerator {

    private final int tileSize;
    private final int maxZoomLevel;
    private final BufferedImage source;
    private final TileWriter tileWriter;

    private ProgressBar pb;

    private Map<Integer, BufferedImage> chunks = new ConcurrentHashMap<>();

    private TileGenerator(int tileSize, String outputDir, int maxZoomLevel, BufferedImage source) {
        this.tileSize = tileSize;
        this.maxZoomLevel = maxZoomLevel;
        this.source = source;
        this.tileWriter = new TileWriter(outputDir, tileSize, maxZoomLevel);
    }

    /**
     * Creates new instance of {@code TileGenerator}.
     *
     * @param source the source image to tile
     * @param options the options to configure generator
     * @see Options
     */
    public static TileGenerator newInstance(BufferedImage source, Options options) {
        return new TileGenerator(options.getTileSize(), options.getOutputDir(), options.getZoomLevel(), source);
    }

    /**
     * Runs a tiles generation from image.
     */
    public void run() {
        log.info("Starting to generate tiles...");
        pb = getProgressBar();
        try {
            tileWriter.resetOutputDirectory();

            // Creating image chunks for the further cropping
            for (int level = maxZoomLevel; level >= 0; level--) {
                if (level == maxZoomLevel) {
                    chunks.put(level, source);
                } else {
                    int mapSize = MapHelper.mapSize(tileSize, level);
                    BufferedImage value = resizeImage(source, mapSize, mapSize);
                    chunks.put(level, value);
                }
            }

            ForkJoinPool.commonPool().invoke(new ZoomLevelTask(0, maxZoomLevel));
        } catch (RuntimeException e) {
            log.error("Ann error occurred: ", e);
            tileWriter.clearOutputDirectory();
        } finally {
            pb.close();
            chunks.clear();
        }
        log.info("Tiles successfully generated!");
    }

    private Tile cropTile(BufferedImage image, int x, int y, int level) {
        return new Tile(image.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize), x, y, level);
    }

    private BufferedImage resizeImage(BufferedImage toResize, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(toResize, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private ProgressBar getProgressBar() {
        return new ProgressBarBuilder()
                .setStyle(ProgressBarStyle.ASCII)
                .setInitialMax(MapHelper.totalTilesCount(maxZoomLevel))
                .setTaskName("Generating tiles...")
                .build();
    }

    class ZoomLevelTask extends RecursiveAction {
        private final int lStart;
        private final int lEnd;

        ZoomLevelTask(int lStart, int lEnd) {
            this.lStart = lStart;
            this.lEnd = lEnd;
        }

        @Override
        protected void compute() {
            if (lEnd - lStart > 1) {
                int middle = lStart + (lEnd - lStart) / 2;
                ZoomLevelTask t1 = new ZoomLevelTask(lStart, middle);
                ZoomLevelTask t2 = new ZoomLevelTask(middle, lEnd);
                invokeAll(t1, t2);
                return;
            }

            // Special task for 0 zoom level
            if (lStart == 0 && lEnd - lStart == 1) {
                new TileTask(0, 1, 0, 1, 0).fork();
            }
            int onSideCount = MapHelper.tilesOnSideCount(tileSize, lEnd);
            new TileTask(0, onSideCount, 0, onSideCount, lEnd).invoke();
        }
    }

    class TileTask extends RecursiveAction {
        private final int xStart;
        private final int xEnd;
        private final int yStart;
        private final int yEnd;
        private final int level;

        private final int xThreshold = 8;
        private final int yThreshold = 8;

        TileTask(int xStart, int xEnd, int yStart, int yEnd, int level) {
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
            this.level = level;
        }

        @Override
        @SuppressWarnings("Duplicates")
        protected void compute() {
            // Check x coordinate threshold
            if (xEnd - xStart > xThreshold) {
                int middle = getMiddle(xStart, xEnd);
                TileTask t1 = new TileTask(xStart, middle, yStart, yEnd, level);
                TileTask t2 = new TileTask(middle, xEnd, yStart, yEnd, level);
                invokeAll(t1, t2);
                return;
            }

            // Check y coordinate threshold
            if (yEnd - yStart > yThreshold) {
                int middle = getMiddle(yStart, yEnd);
                TileTask t1 = new TileTask(xStart, xEnd, yStart, middle, level);
                TileTask t2 = new TileTask(xStart, xEnd, middle, yEnd, level);
                invokeAll(t1, t2);
                return;
            }

            for (int x = xStart; x < xEnd; x++) {
                for (int y = yStart; y < yEnd; y++) {
                    tileWriter.write(cropTile(chunks.get(level), x, y, level));
                }
            }
            pb.stepBy((xEnd - xStart) * (yEnd - yStart));
        }

        private int getMiddle(int start, int end) {
            return start + (end - start) / 2;
        }
    }
}
