package ru.avaneev.imagetiler.component.generator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andrey Vaneev
 * Creation date: 19.08.2018
 */
public class TileWriterTest {

    private Path outputDir;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        this.outputDir = tempFolder.getRoot().toPath().resolve("dest").toAbsolutePath();
    }

    @Test
    public void shouldCreateOutputDirectoryWithSubFolders() throws IOException {
        TileWriter writer = new TileWriter(outputDir.toString(), 256, 4);

        writer.createOutputDirectory();

        Path dest = outputDir.resolve("map");
        long directories = Files.walk(dest).filter(Files::isDirectory).count();
        assertThat(directories).isEqualTo(37);
        assertThat(dest.resolve("0").toFile().exists()).isEqualTo(true);
        assertThat(dest.resolve("0").resolve("0").toFile().exists()).isEqualTo(true);
        assertThat(dest.resolve("4").resolve("8").toFile().exists()).isEqualTo(true);
    }

    @Test
    public void shouldRemoveOutputDirectory() throws IOException {
        TileWriter writer = new TileWriter(outputDir.toString(), 256, 4);
        writer.createOutputDirectory();
        long directories = Files.walk(outputDir.resolve("map")).filter(Files::isDirectory).count();
        assertThat(directories).isEqualTo(37);

        writer.clearOutputDirectory();

        assertThat(outputDir.resolve("map").toFile().exists()).isEqualTo(false);
    }
}
