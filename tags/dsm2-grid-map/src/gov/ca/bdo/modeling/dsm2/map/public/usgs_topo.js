/*
 * Borrowed from USGS website 
 * http://store.usgs.gov/b2c_usgs/usgs/maplocator/%28xcm=r3standardpitrex_prd&layout=6_1_61_48&uiarea=2&ctype=areaDetails&carea=%24ROOT%29/.do
 */
USGSTopoTileLayer.prototype = new GTileLayer();

function USGSTopoTileLayer (baseUrl, name, shortName, layers, srs, version, styles, format, copyright, bgcolor, transparent) {

 GTileLayer.call(new GCopyrightCollection(""));

 this.baseUrl = baseUrl;
 this.name = name;
 this.shortName = shortName;
 this.layers = layers;
 this.srs = srs;
 this.version = version;
 if (styles)
 this.styles = styles;
 else
 this.styles = null;
 this.format = format;
 if (copyright)
 this.copyright = copyright;
 else
 this.copyright = "(c) Unknown";
 if (bgcolor)
 this.bgcolor = bgcolor;
 if (transparent)
 this.transparent = transparent;
}

USGSTopoTileLayer.prototype.isPng = function() {
 return false;
}
USGSTopoTileLayer.prototype.getOpacity = function() {
 return 1.0;
}
USGSTopoTileLayer.prototype.getTileUrl = function(a,b) {
 var lULP = new GPoint(a.x*256,(a.y+1)*256);
 var lLRP = new GPoint((a.x+1)*256,a.y*256);
 var lUL = G_NORMAL_MAP.getProjection().fromPixelToLatLng(lULP,b,1);
 var lLR = G_NORMAL_MAP.getProjection().fromPixelToLatLng(lLRP,b,1);

 var ulx;
 var uly;
 var lrx;
 var lry;
 var lBbox;

 if(this.srs.toLowerCase() == "EPSG:4326".toLowerCase()){
 ulx = Math.round(lUL.x*10000000)/10000000;
 uly = Math.round(lUL.y*10000000)/10000000;
 lrx = Math.round(lLR.x*10000000)/10000000;
 lry = Math.round(lLR.y*10000000)/10000000;
 lBbox=ulx+","+uly+","+lrx+","+lry;
 }
 else{
 lBbox = Math.round(this.dd2MercMetersLng(lUL.x)) + "," + Math.round(this.dd2MercMetersLat(lUL.y)) + "," + Math.round(this.dd2MercMetersLng(lLR.x))+ "," + Math.round(this.dd2MercMetersLat(lLR.y));
 }

 var lURL= this.baseUrl;
 lURL+="&REQUEST=GetMap";
 lURL+="&SERVICE=WMS";
 lURL+="&VERSION=" + this.version;
 lURL+="&LAYERS="+this.layers;
 if (this.styles && this.styles != 'default')
 lURL+="&STYLES="+this.styles;
 else
 lURL+="&STYLES=";
 lURL+="&FORMAT="+this.format;
 lURL+="&BGCOLOR=0xFFFFFF";
 lURL+="&TRANSPARENT=TRUE";
 //lURL+="&SRS="+this.srs;
 lURL+="&SRS="+this.srs;
 lURL+="&BBOX="+lBbox;
 lURL+="&WIDTH=256";
 lURL+="&HEIGHT=256";
 lURL+="&reaspect=false";
 //lURL+="&EXCEPTIONS=INIMAGE";
 return lURL;
}
USGSTopoTileLayer.prototype.maxResolution = function() {
 return 17;
}
USGSTopoTileLayer.prototype.minResolution = function() {
 return 0;
}
USGSTopoTileLayer.prototype.dd2MercMetersLng = function(p_lng) {
 //var MAGIC_NUMBER=6356752.3142; jdw, BUG -- this is the pole radius for WGS84
 // use this radius instead, this is the equater radius used by google
 var MAGIC_NUMBER=6378137;
 var DEG2RAD=0.0174532922519943;
 var PI=3.14159267;
 return MAGIC_NUMBER*(p_lng*DEG2RAD);
}

USGSTopoTileLayer.prototype.dd2MercMetersLat = function(p_lat) {
 //var MAGIC_NUMBER=6356752.3142; jdw, BUG -- this is the pole radius for WGS84
 // use this radius instead, this is the equater radius used by google
 var MAGIC_NUMBER=6378137;
 var DEG2RAD=0.0174532922519943;
 var PI=3.14159267;
 if (p_lat >= 85) p_lat=85;
 if (p_lat <= -85) p_lat=-85;
 return MAGIC_NUMBER*Math.log(Math.tan(((p_lat*DEG2RAD)+(PI/2)) /2));
}
