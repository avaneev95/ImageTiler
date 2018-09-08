package ru.avaneev.imagetiler.component.generator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.avaneev.imagetiler.model.Options;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andrey Vaneev
 * Creation date: 19.08.2018
 */
public class TileGeneratorTest {

    private Options options;
    private BufferedImage source;
    private Path outputDir;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUpOptions() throws IOException {
        this.outputDir = tempFolder.getRoot().toPath().resolve("dest").toAbsolutePath();
        this.source = ImageIO.read(new File("src/test/resources/fff.png"));
        this.options = Options.builder()
                .zoomLevel(1)
                .tileSize(256)
                .sourceFile(source)
                .outputDir(outputDir.toString())
                .build();
    }

    @Test
    public void shouldCreateTiles() throws IOException {
        TileGenerator generator = TileGenerator.newInstance(options);
        generator.run();
        long tiles = Files.walk(outputDir).filter(Files::isRegularFile).count();
        assertThat(tiles).isEqualTo(5);
    }
}