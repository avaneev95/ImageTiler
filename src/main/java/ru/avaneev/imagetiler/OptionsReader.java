package ru.avaneev.imagetiler;

import ru.avaneev.imagetiler.generator.MapHelper;

import java.util.Scanner;

/**
 * Class to read configuration properties from stdout.
 *
 * @author Andrey Vaneev
 * Creation date: 18.08.2018
 */
// TODO: 19.08.2018 Refactor
public class OptionsReader implements AutoCloseable {
    private Scanner scanner;
    private Options options;

    public OptionsReader(Options options) {
        this.options = options;
        scanner = new Scanner(System.in);
    }

    public OptionsReader readZoomLevel() {
        int optimalZoomLevel = MapHelper.optimalZoomLevel(options.getTileSize(), options.getImageSize());
        String msg = "Expected an integer value between 1 and 15.";
        while (true) {
            System.out.print("Zoom level: (" + optimalZoomLevel + ") ");
            String size = scanner.nextLine();
            if (size.isEmpty()) {
                options.setZoomLevel(optimalZoomLevel);
                return this;
            }
            try {
                int zoomLevel = Integer.parseInt(size);
                if (zoomLevel < 1 || zoomLevel >= 15) {
                    printError(msg);
                } else {
                    options.setZoomLevel(zoomLevel);
                    return this;
                }
            } catch (NumberFormatException e) {
                printError(msg);
            }
        }
    }

    public OptionsReader readTileSize() {
        while (true) {
            System.out.print("Tile size: (256) ");
            String size = scanner.nextLine();
            if (size.isEmpty()) {
                return this;
            }
            try {
                options.setTileSize(Integer.parseInt(size));
                return this;
            } catch (NumberFormatException e) {
                printError("Expected an integer value.");
            }
        }
    }

    private void printError(String s) {
        System.out.println("Invalid value!");
        System.out.println(s);
        System.out.println();
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
