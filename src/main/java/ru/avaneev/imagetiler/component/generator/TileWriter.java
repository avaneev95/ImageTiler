package ru.avaneev.imagetiler.component.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @author Andrey Vaneev
 * Creation date: 19.08.2018
 */
@Slf4j
class TileWriter {
    private final String outputRoot;
    private final String outputDest;
    private final int tileSize;
    private final int maxZoomLevel;

    TileWriter(String outputDir, int tileSize, int maxZoomLevel) {
        this.outputRoot = outputDir;
        this.outputDest = outputDir + "/map";
        this.tileSize = tileSize;
        this.maxZoomLevel = maxZoomLevel;
    }

    void write(Tile tile) {
        String filename = String.format("%s/%d/%d/%d.png", outputDest, tile.getLevel(), tile.getX(), tile.getY());
        try {
            ImageIO.write(tile.getImage(), "png", new File(filename));
        } catch (IOException e) {
            log.error("Failed to save tile {}", filename);
        }
    }

    void createOutputDirectory() {
        File root = new File(outputRoot);
        if (!root.exists()) {
            log.info("Creating output directory {}", root.getPath());
            if (!root.mkdir()){
                log.error("Failed to creating output directory {}", root.getPath());
            }
        } else {
            log.info("Output directory already exists.");
            clearOutputDirectory();
        }

        File dist = new File(outputDest);
        if (dist.mkdir()) {
            log.info("Created output folder: {}", dist.getPath());
            for (int level = 0; level < maxZoomLevel + 1; level++) {
                File zoomDir = new File(dist, String.valueOf(level));
                if (zoomDir.mkdir()) {
                    log.debug("Created folder: {}/{}", dist.getPath(), level);
                }
                for (int x = 0; x < MapHelper.tilesOnSideCount(tileSize, level); x++) {
                    File xDir = new File(zoomDir, String.valueOf(x));
                    if (xDir.mkdir()) {
                        log.debug("Created folder: {}/{}/{}", dist.getPath(), level, x);
                    }
                }
            }
        }
    }

    void clearOutputDirectory() {
        try {
            File dist = new File(outputDest);
            log.info("Deleting output folder: " + dist.getPath());
            FileUtils.deleteDirectory(dist);
        } catch (IOException e) {
            log.error("Failed to delete output folder!", e);
        }
    }
}
