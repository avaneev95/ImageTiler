package ru.avaneev.imagetiler.component.generator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.avaneev.imagetiler.model.Options;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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
    private final TileWriter tileWriter;
    private final int totalTiles;

    private BufferedImage source;
    private Map<Integer, BufferedImage> chunks = new ConcurrentHashMap<>();

    private AtomicInteger progress;
    private Consumer<ProgressEvent> progressListener;

    private TileGenerator(int tileSize, String outputDir, int maxZoomLevel, BufferedImage source) {
        this.tileSize = tileSize;
        this.maxZoomLevel = maxZoomLevel;
        this.source = source;
        this.tileWriter = new TileWriter(outputDir, tileSize, maxZoomLevel);
        this.progress = new AtomicInteger(0);
        this.totalTiles = MapHelper.totalTilesCount(maxZoomLevel);
    }

    /**
     * Creates new instance of {@code TileGenerator}.
     *
     * @param options the options to configure the generator
     * @see Options
     */
    public static TileGenerator newInstance(Options options) {
        return new TileGenerator(options.getTileSize(), options.getOutputDir(), options.getZoomLevel(), options.getSourceFile());
    }

    public TileGenerator onProgressUpdate(Consumer<ProgressEvent> handler) {
        this.progressListener = handler;
        return this;
    }

    /**
     * Runs a tiles generation from image.
     */
    public void run() {
        try {
            tileWriter.createOutputDirectory();
            List<TileTask> tasks = new ArrayList<>();

            // Creating image chunks for the further cropping
            log.info("Preparing image...");
            for (int level = maxZoomLevel; level >= 0; level--) {
                if (level == maxZoomLevel) {
                    chunks.put(level, source);
                } else {
                    int mapSize = MapHelper.mapSize(tileSize, level);
                    BufferedImage value = resizeImage(source, mapSize, mapSize);
                    chunks.put(level, value);
                }
                int onSideCount = MapHelper.tilesOnSideCount(tileSize, level);
                tasks.add(new TileTask(0, onSideCount, 0, onSideCount, level));
            }

            log.info("Starting to generate tiles...");
            ForkJoinTask.invokeAll(tasks);

        } catch (Exception e) {
            tileWriter.clearOutputDirectory();
            throw new CompletionException(e);
        } finally {
            chunks.clear();
            source = null;
        }
        log.info("Tiles successfully generated!");
    }

    public CompletableFuture<Void> runAsync() {
       return CompletableFuture.runAsync(this::run);
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
                    if (progressListener != null) {
                        progressListener.accept(new ProgressEvent(progress.incrementAndGet(), totalTiles));
                    }
                }
            }
        }

        private int getMiddle(int start, int end) {
            return start + (end - start) / 2;
        }
    }

    @Getter
    public static class ProgressEvent {
        private final double totalWork;
        private final double workDone;

        ProgressEvent(double workDone, double totalWork) {
            this.totalWork = totalWork;
            this.workDone = workDone;
        }

        public double getProgress() {
            return this.workDone / this.totalWork;
        }

        public double getPercents() {
            return this.getProgress() * 100.0;
        }
    }
}
