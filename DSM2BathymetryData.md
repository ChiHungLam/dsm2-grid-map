# Introduction #

The bathymetry data is a large data set consisting of depth soundings and lidar data. This data has been collected over a period of 1930-present from various agencies ranging from California DWR to NOAA.

To visualize this data on Google maps, tiles were generated using the raw data and color coding the depth. The age of the data was represented by transparency that ranged from 1925 to 2010. The raw data itself was loaded with latitude/longitude index so that it was quickly retrievable for a point around the area of interest. For more information and source code see this project http://code.google.com/p/gmap-tile-generator/.

This data is hosted at http://dsm2bathymetry.appspot.com. The raw data can be obtained by zooming into the area of interest and clicking in an area around it.

The bathymetry data above was converted to a 10mx10m grid using techniques described [here](http://sfbay.wr.usgs.gov/sediment/delta/index.html). The visualization using the same coloring is available as alternate tiles for comparison at http://dsm2bathymetry.appspot.com by selecting the "Interpolated Data" option using the drop down list box

Also added to this work was lidar data collected in 2007 and then converted to a grid. This can be viewed by selecting "Interpolated Data + Lidar" using the drop down list box on the site mentioned above.

_The future plans are to be able to view this data graphically along a line drawn or calculate the average over an area._