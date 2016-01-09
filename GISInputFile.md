# Introduction #

In order to supplement the model with GIS information, this file may be provided along with the needed input files as defined [here](http://baydeltaoffice.water.ca.gov/modeling/deltamodeling/models/dsm2/dsm2.cfm)

The format of this input file follows the format of the existing input files


# Details #

The input file simply consists of a text file with losely structured text data.
Each input file has a number of tables where each table has a name and uses the "END" keyword to define the boundaries

The first line after the name is the name of the headers of the table separated by white space.

Subsequent lines upto the "END" keyword are then the values of headers separated by whitespace

Within each value the "|" symbol may be used to demarcated multiple entities in a single value.

A simple input file is defined below
```
CHANNEL_GIS
ID	INTERIOR_LAT_LNG
1	
2	
3	
4	(37.89219554724434,-121.4154052734375)
17	
END
NODE_GIS
ID	LAT_LNG
1	(37.68394014,-121.2625714)
2	(37.71019633,-121.2702599)
3	(37.72268565,-121.2747321)
4	(37.72727968,-121.2979608)
5	(37.993998198369574,-121.3330078125)
17	(37.65849617,-121.2553017)
END
RESERVOIR_GIS
ID	LAT_LNG	INTERIOR_LAT_LNG
clifton_court	(37.84341033205656,-121.57539367675781)	(37.89436302930203,-121.62139892578125)|(37.80327385185865,-121.5692138671875)|(37.84232584933157,-121.47308349609375)|(37.89436302930203,-121.62139892578125)
END
GATE_GIS
ID	LAT_LNG
7_mile@3_mile	(38.11686649394057,-121.6299819946289)
END
```