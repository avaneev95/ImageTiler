package ru.avaneev.imagetiler.component.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import ru.avaneev.imagetiler.model.Options;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author Andrey Vaneev
 * Creation date: 19.08.2018
 */
@Slf4j
public class PreviewGenerator {

    private Options options;

    public PreviewGenerator(Options options) {
        this.options = options;
    }

    /**
     * Generates a preview HTML file using Leaflet and places it to the output folder.
     */
    public void generate() {
        log.info("Generating Leaflet preview...");
        ClassLoader classLoader = getClass().getClassLoader();
        File outputDir = new File(options.getOutputDir() + "/map");
        File file = new File(outputDir, "leaflet.html");
        try (InputStream is = classLoader.getResourceAsStream("leaflet.html")) {
            HashMap<String, String> values = new HashMap<>();
            values.put("maxZoomLevel", String.valueOf(options.getZoomLevel()));
            values.put("imageSize", String.valueOf(options.getImageSize()));
            values.put("tileSize", String.valueOf(options.getTileSize()));
            values.put("height", String.valueOf(options.getPreviewHeight()));
            values.put("width", String.valueOf(options.getPreviewWidth()));

            StringSubstitutor substitutor = new StringSubstitutor(values, "${", "}");
            FileUtils.writeStringToFile(file, substitutor.replace(IOUtils.toString(is)));
        } catch (IOException e) {
            log.error("Error occurred while generating Leaflet preview", e);
        }
        log.info("Leaflet preview generated in {}", outputDir.getAbsolutePath());
    }
}
