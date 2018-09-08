package ru.avaneev.imagetiler.component.generator;

import lombok.Getter;

import java.awt.image.BufferedImage;

/**
 * @author Andrey Vaneev
 * Creation date: 19.08.2018
 */
@Getter
class Tile {
    private final BufferedImage image;
    private final int x;
    private final int y;
    private final int level;

    Tile(BufferedImage image, int x, int y, int level) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.level = level;
    }
}
