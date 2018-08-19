Image Tiler
==============

This application splits the given image into multiple tiles of different resolutions.
The generated tiles can be displayed using Google Maps, Leaflet or OpenLayers.

It also provides a simple preview file using Leaflet library.

## Requirements
* Java 8


## Usage
1. Download the runnable jar file [imagetiler-1.0.0.jar](https://github.com/avaneev95/ImageTiler/releases/download/1.0.0/imagetiler-1.0.0.jar).
2. Run it with:
```
$ java -jar imagetiler-1.0.0.jar [filename] 
```
where `filename` - is the image you want to split. It better works with square images which size is a power of two, ex.: 512x512 or 4096x4096.

Then you will need to specify additional properties:
* `tile size` - the size of smallest image chunk, default is 256
* `max zoom level` - the max allowed zoom level of the image. Can be the inetger nuber between 1 and 15, by default application will calculate the optimal zoom level based on image size

Note: For large files you may have to add more memory to the JVM, using `-Xmx` key, ex.: `java -Xmx2g -jar imagetiler [filename]`

## License

[MIT](LICENSE) © Andrei Vaneev
