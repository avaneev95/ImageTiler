Image Tiler
==============
![version](https://img.shields.io/github/release/avaneev95/ImageTiler.svg)
![license](https://img.shields.io/github/license/avaneev95/ImageTiler.svg)

This application splits the given image into multiple tiles of different resolutions.
The generated tiles can be displayed using Google Maps, Leaflet or OpenLayers.

It also provides a simple preview file using Leaflet library.

## Requirements
* Java 8

## Usage
1. Download the runnable jar file [ImageTiler.jar](https://github.com/avaneev95/ImageTiler/releases/download/1.1.0/ImageTiler.jar). 
You can also download an executable [ImageTiler.exe](https://github.com/avaneev95/ImageTiler/releases/download/1.1.0/ImageTiler.exe).
2. Run
3. Choose image
4. Specify settings
5. Generate tiles

## Settings
* `Tile size` - the size of smallest image chunk, default is 256.
* `Max zoom level` - the max allowed zoom level of the image. Can be the integer number between 1 and 10, by default application will calculate the optimal zoom level based on image size and tile size.
* `Output directory` - the directory where generated tiles will be saved. If the directory doesn't exist, it will be created. The tiles wil be placed in a `map` subfolder.
* `Generate preview` - whether is required to generate Leaflet preview. 
* `Preview height` - the preview window height. Default is 720.
* `Preview width` - the preview window width. Default is 720.

## Limitations
* It better works with square images which size is a power of two, ex.: 512x512 or 4096x4096.
* It can run out of memory processing images large than 16384x16384 px.

## License

[MIT](LICENSE) © Andrei Vaneev
