package ru.avaneev.imagetiler.component.generator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Andrey Vaneev
 * Creation date: 19.08.2018
 */
public class MapHelperTest {

    @Test
    public void shouldReturnTilesOnSideCount() {
        assertThat(MapHelper.tilesOnSideCount(256, 0)).isEqualTo(1);
        assertThat(MapHelper.tilesOnSideCount(256, 2)).isEqualTo(4);
        assertThat(MapHelper.tilesOnSideCount(256, 3)).isEqualTo(8);
        assertThat(MapHelper.tilesOnSideCount(256, 4)).isEqualTo(16);
    }

    @Test
    public void shouldReturnTotalTilesCount() {
        assertThat(MapHelper.totalTilesCount(0)).isEqualTo(1);
        assertThat(MapHelper.totalTilesCount(1)).isEqualTo(5);
        assertThat(MapHelper.totalTilesCount(2)).isEqualTo(21);
        assertThat(MapHelper.totalTilesCount(4)).isEqualTo(341);
    }

    @Test
    public void shouldReturnMapSize() {
        assertThat(MapHelper.mapSize(256, 0)).isEqualTo(256);
        assertThat(MapHelper.mapSize(256, 3)).isEqualTo(2048);
        assertThat(MapHelper.mapSize(128, 3)).isEqualTo(1024);
        assertThat(MapHelper.mapSize(200, 2)).isEqualTo(800);
    }

    @Test
    public void shouldReturnOptimalZoomLevel() {
        assertThat(MapHelper.optimalZoomLevel(256, 4096)).isEqualTo(4);
        assertThat(MapHelper.optimalZoomLevel(256, 8192)).isEqualTo(5);
        assertThat(MapHelper.optimalZoomLevel(128, 4096)).isEqualTo(5);
        assertThat(MapHelper.optimalZoomLevel(200, 4096)).isEqualTo(4);
        assertThat(MapHelper.optimalZoomLevel(150, 2048)).isEqualTo(4);
    }
}