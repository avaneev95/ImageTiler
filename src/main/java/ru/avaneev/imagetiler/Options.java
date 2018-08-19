package ru.avaneev.imagetiler;

import lombok.*;

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
    private String sourceFile;
    @Builder.Default
    private int tileSize = 256;
    @Builder.Default
    private String outputDir = "dist";
    private int zoomLevel;
    private int imageSize;
}
