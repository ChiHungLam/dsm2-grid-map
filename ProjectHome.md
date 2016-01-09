# Introduction #
DSM2 is a hydrodynamic model used by California Department of Water Resources. This project provides a Google maps interface to the model input and output.

The input to DSM2 is defined by text files as described [here](http://baydeltaoffice.water.ca.gov/modeling/deltamodeling/models/dsm2/dsm2.cfm). This user interface uses that as a starting point to define the artifacts being represented on the map.

In order to supplement that with GIS information, [an input file](GISInputFile.md) may be added which contains the locations of the nodes (junctions), interior points of a channel, reservoir and location of a gate.

The model needs to define the cross-section of channels based on bathymetry data. This is a rather large data set of depth soundings and lidar data ( 4+ million points of data). This data is represented at http://dsm2bathymetry.appspot.com as tiles and the raw data for a limited area can be retrieved by clicking around a point of interest.

# Access #
The UI is hosted at http://dsm2grid.appspot.com. You will need to send email to one of this project owners to get access to the application.


# Technologies #
The UI is built on top of Google Maps API using GWT and hosted on Google App Engine. The application is written in Java with some Javascript. The code will be available shortly via the source tab.