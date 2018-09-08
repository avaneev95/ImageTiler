package ru.avaneev.imagetiler.model;

import lombok.*;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author Andrey Vaneev
 * Creation date: 15.08.2018
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Options {
    private BufferedImage sourceFile;
    private String sourceFileName;
    @Builder.Default
    private int tileSize = 256;
    @Builder.Default
    private String outputDir = new File("").getAbsolutePath();
    private int zoomLevel;
    private int imageSize;
    private boolean generatePreview;
    @Builder.Default
    private int previewWidth = 720;
    @Builder.Default
    private int previewHeight = 720;
}
