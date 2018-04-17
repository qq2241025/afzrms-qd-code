

(function(){
	var DRAWING = "drawing";
	
	ol.interaction.Draw.prototype.addToDrawing_ = function(event) {
	  var coordinate = event.coordinate;
	  var geometry = this.sketchFeature_.getGeometry();
	  var coordinates;
	  if (this.mode_ === ol.interaction.DrawMode.LINE_STRING) {
	    this.finishCoordinate_ = coordinate.slice();
	    goog.asserts.assertInstanceof(geometry, ol.geom.LineString);
	    coordinates = geometry.getCoordinates();
	    coordinates.push(coordinate.slice());
	    geometry.setCoordinates(coordinates);
	  } else if (this.mode_ === ol.interaction.DrawMode.POLYGON) {
	    this.sketchPolygonCoords_[0].push(coordinate.slice());
	    goog.asserts.assertInstanceof(geometry, ol.geom.Polygon);
	    geometry.setCoordinates(this.sketchPolygonCoords_);
	  }
	  this.updateSketchFeatures_();
	  this.dispatchEvent(new ol.DrawEvent(DRAWING,this.sketchFeature_));
	};
	
	 /**
	  * hzg add 补丁
	  */
	var sketch;
	var measureTooltip;
	var pointerMoveHandler = function(evt) {
	  if (evt.dragging) {
	    return;
	  }
	  var tooltipCoord = evt.coordinate;
	  if (sketch) {
		    var geom =  sketch.getGeometry();
		    if (geom instanceof ol.geom.LineString) {
		      var output = formatLength(geom.getLength());
		      tooltipCoord = geom.getLastCoordinate();
		      measureTooltip.getElement().innerHTML = output;
		      measureTooltip.setPosition(tooltipCoord);
		    }
	  }
	};


	var draw ,mapObj,vector=null;
	function rectTools(map) {
		  if(!map){
			 throw new Error("map is not null");
			 return ;
		  }
		  clearTools();
		  mapObj = map;
		  vector = new ol.layer.Vector({
		 	 	source: new ol.source.Vector()
		  });
		  mapObj.addLayer(vector);
		  draw = new ol.interaction.Draw({
		    source: vector.getSource(),
		    type:"LineString"
		  });
		  mapObj.addInteraction(draw);
		
		  createMeasureTooltip();
		
		  draw.on('drawstart', function(evt) {
			  sketch = evt.feature; 
		  });
		  
		  draw.on('drawing', function(evt) {
			  pointerMoveHandler(evt);
		  });
		
		  draw.on('drawend', function(evt) {
		  	 measureTooltip.getElement().className = 'ol-map-tooltip ol-map-tooltip-static';
		     mapObj.removeInteraction(draw);
		 });
	}
	

	var formatLength = function(length) {
	  if (length > 100) {
	    	output = (Math.round(length / 1000 * 100) / 100) + 'km';
	  } else {
	    	output = (Math.round(length * 100) / 100)+ 'm';
	  }
	  return output;
	};
	
	function createMeasureTooltip() {
		 var helpTooltipElement = document.getElementById("openlayer_tools_mouse");
		 if(helpTooltipElement==null){
			  helpTooltipElement = document.createElement('div');
			  helpTooltipElement.className = 'ol-map-tooltip';
			  helpTooltipElement.id = "openlayer_tools_mouse";
		  }
		  helpTooltipElement.className = 'ol-map-tooltip tooltip-measure';
		  measureTooltip = new ol.Overlay({
		    element: helpTooltipElement,
		    offset: [0, -10],
		    positioning: 'bottom-center'
		  });
		  mapObj.addOverlay(measureTooltip);
	}
	var clearTools = function(){
		 if(measureTooltip){
	  	   	mapObj.removeOverlay(measureTooltip);
	     }
	     if(vector){
	        mapObj.removeLayer(vector);
	        vector = null;
	     }
	}
	
	
	//
	GAS.RuleTool = rectTools;
	GAS.unRuleTool = clearTools;
	
})();