# Introduction #
DSM2 model input is in text files that are loosely structured. The data is represented by tables that have the following format
```
<TABLE_NAME><END_OF_LINE>
<HEADER1><WHITESPACE><HEADER2><WHITESPACE>....<HEADER_?><END_OF_LINE>
<VALUE1><WHITESPACE><VALUE2><WHITESPACE>...<VALUE?><END_OF_LINE>
<<MORE VALUE ROWS>><END_OF_LINE>
...
END<END_OF_LINE>
```

# Download #
The latest version is available [here](http://dsm2-grid-map.googlecode.com/files/dsm2-input-model.jar)
# Details #

The javadocs are a good place to start are [here](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/index.html). The main class is
[Parser](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/Parser.html) which is used as shown in the jython session below.

The parser maintains the in memory model in a [Tables](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/Tables.html) structure that consists of [InputTables](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/InputTable.html) which are a one-to-one representation of the structure defined [above](#Introduction.md).

[Tables](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/Tables.html) also has converters [to](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/Tables.html#toDSM2Model()) and [from](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/Tables.html#fromDSM2Model(gov.ca.dsm2.input.model.DSM2Model)) a higher level model structure [DSM2Model](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/DSM2Model.html). This model structure represents [nodes](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/Nodes.html), [channels](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/Channels.html), [reservoirs](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/Reservoirs.html), [gates](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/Gates.html), [boundary inputs](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/BoundaryInputs.html) and [outputs](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/Outputs.html) at a top level with associated information such as [xsection](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/XSection.html) at the appropriate object such as a [channel](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/model/Channel.html)

To output the in memory representation of [InputTable](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/InputTable.html), it supports a method [toStringRepresentation()](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/InputTable.html#toStringRepresentation()) which writes out a well formatted string representation of the table. One can then loop over all the [InputTables](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/InputTable.html) from [Tables](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/Tables.html) [getTables()](http://dsm2-grid-map.googlecode.com/svn/trunk/dsm2-input-model/doc/gov/ca/dsm2/input/parser/Tables.html#getTables()) method and output the all the tables in the order desired.

If you are familiar with jython then adding the jar to the classpath, the session in jython below can help you get started
```
>>> from gov.ca.dsm2.input.parser import *
>>> from gov.ca.dms2.input.model import *
>>> p=Parser()                                 
>>> tables = p.parseModel('hydro.inp')
[Input Table: ENVVAR, Input Table: SCALAR, Input Table: IO_FILE, Input Table: CHANNEL, Input Table: XSECT, Input Table: XSECT_LAYER, Input Table: RESERVOIR, Input Table: RESERVOIR_CONNECTION, Input Table: GATE, Input Table: GATE_WEIR_DEVICE, Input Table: GATE_PIPE_DEVICE, Input Table: TRANSFER, Input Table: CHANNEL_IC, Input Table: RESERVOIR_IC, Input Table: BOUNDARY_STAGE, Input Table: BOUNDARY_FLOW, Input Table: SOURCE_FLOW, Input Table: SOURCE_FLOW_RESERVOIR, Input Table: INPUT_GATE, Input Table: INPUT_TRANSFER_FLOW, Input Table: OPERATING_RULE, Input Table: OPRULE_EXPRESSION, Input Table: OPRULE_TIME_SERIES, Input Table: OUTPUT_CHANNEL, Input Table: OUTPUT_RESERVOIR, Input Table: OUTPUT_GATE]
>>> channel = tables.getTableNamed('CHANNEL')
>>> channels = tables.toChannels()
>>> channels.getChannel('432')
gov.ca.dsm2.input.model.Channel@6ce931d9
>>> ch432 = channels.getChannel('432')
>>> ch432.length
15812.0
>>> ch432.mannings
0.018
>>> ch432.id
u'432'
>>> ch432.xsections
[gov.ca.dsm2.input.model.XSection@1786ed7a, gov.ca.dsm2.input.model.XSection@1b50f0a7, gov.ca.dsm2.input.model.XSection@5ee771f3]
>>> ch432.xsections[0].channelId
u'432'
>>> ch432.xsections[0].distance 
0.243
>>> 
```

## Creating Model in memory and rendering to Tables ##
One can create a model in memory using the classes and methods defined and output them to their table representation. One such example is below
```
from gov.ca.dsm2.input.parser import Parser
from gov.ca.dsm2.input.model import *
c1 = Channel()
c1.setId("1"); c1.setUpNodeId("2"); c1.setDownNodeId("3"); c1.setLength(12500); c1.setMannings(0.02); c1.setDispersion(1.0)
xsect1 = XSection();
x1layer1 = XSectionLayer(); x1layer1.setArea(0); x1layer1.setElevation(-10.0); x1layer1.setTopWidth(10); x1layer1.setWettedPerimeter(10);
x1layer2 = XSectionLayer(); x1layer2.setArea(50); x1layer2.setElevation(-5.0); x1layer2.setTopWidth(10); x1layer2.setWettedPerimeter(20);
xsect1.setChannelId(c1.getId()); xsect1.setDistance(0.25); xsect1.addLayer(x1layer1); xsect1.addLayer(x1layer2)
c1.addXSection(xsect1)
channels = Channels()
channels.addChannel(c1)
from gov.ca.dsm2.input.parser import Tables
tables = Tables()
channel_input_tables = tables.fromChannels(channels)
for table in channel_input_tables:
 print table.toStringRepresentation()

>>>>>>>> output >>>>>>>>>>
CHANNEL
CHAN_NO	LENGTH	MANNING	DISPERSION	UPNODE	DOWNNODE
1	12500.0	0.02	1.0	2	3
END

XSECT_LAYER
CHAN_NO	DIST	ELEV	AREA	WIDTH	WET_PERIM
1	0.25	-10.0	0.0	10.0	10.0
1	0.25	-5.0	50.0	10.0	20.0
END

CHANNEL_GIS
ID	INTERIOR_LAT_LNG
1	
END
```