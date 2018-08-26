package ru.avaneev.imagetiler;


import ru.avaneev.imagetiler.generator.PreviewGenerator;
import ru.avaneev.imagetiler.generator.TileGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Andrey Vaneev
 * Creation date: 18.08.2018
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Source file name not provided!");
            System.out.println("Expected command: java -jar image-tiler.jar [filename]");
            return;
        }
        File sourceFile = new File(args[0]);

        BufferedImage original;
        Options options = new Options();
        try (OptionsReader optionsReader = new OptionsReader(options)) {
            if (sourceFile.exists()) {
                System.out.print("Loading image...");
                original = ImageIO.read(sourceFile);
                System.out.println("Done");
                options.setImageSize(original.getWidth());
                System.out.println("Specify additional properties to generate tiles:");

                optionsReader
                        .readTileSize()
                        .readZoomLevel();

                TileGenerator.newInstance(original, options).run();
                new PreviewGenerator(options).generate();
                System.out.print("Generated map tiles and preview are placed in " + new File(options.getOutputDir()).getAbsolutePath());
            } else {
                System.err.println("File " + args[0] + " not found!");
            }
        } catch (IOException e) {
            System.err.println("Cannot read file " + args[0] + "!");
        }
    }
}
