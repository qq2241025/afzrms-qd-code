/**
 * OpenLayers 3 Popup Overlay.
 * See [the examples](./examples) for usage. Styling can be done via CSS.
 * @constructor
 * @extends {ol.Overlay}
 * @param {Object} opt_options Overlay options, extends olx.OverlayOptions adding:
 *                              **`panMapIfOutOfView`** `Boolean` - Should the
 *                              map be panned so that the popup is entirely
 *                              within view.
 */
ol.Overlay.Popup = function(opt_options) {

    var options = opt_options || {};

    this.panMapIfOutOfView = options.panMapIfOutOfView;
    if (this.panMapIfOutOfView === undefined) {
        this.panMapIfOutOfView = true;
    }

    this.ani = options.ani;
    if (this.ani === undefined) {
        this.ani = ol.animation.pan;
    }

    this.ani_opts = options.ani_opts;
    if (this.ani_opts === undefined) {
        this.ani_opts = {'duration': 250};
    }

    this.container = document.createElement('div');
    this.container.className = 'ol-popup';
    this.container.style.width = 150;
    
     var top = document.createElement("div");
	 top.className = "infoWindow-top";
	 this.title = document.createElement("div");
	 this.title.innerHTML  = "标题";
	 this.closer = document.createElement('a');
     this.closer.className = 'ol-popup-closer';
     this.closer.href = '#';
	 top.appendChild(this.title);
	 top.appendChild(this.closer);
	 this.container.appendChild(top);
    

    

    var that = this;
    this.closer.addEventListener('click', function(evt) {
        that.container.style.display = 'none';
        that.closer.blur();
        //注册close关闭事件
        if(that.hideCallback){
           that.hideCallback.call(this);
        }
        evt.preventDefault();
    }, false);

    this.content = document.createElement("div");
	this.content.className = "infoWindow-middle";
	this.content.style.backgroundColor='white';
	this.content.innerHTML =  "";
	this.container.appendChild(this.content);

    ol.Overlay.call(this, {
        element: this.container,
        stopEvent: true
    });

};

ol.inherits(ol.Overlay.Popup, ol.Overlay);

/**
 * Show the popup.
 * @param {ol.Coordinate} coord Where to anchor the popup.
 * @param {String} html String of HTML to display within the popup.
 */
ol.Overlay.Popup.prototype.show = function(coord, cfg) {
	var title = cfg["title"],content = cfg["content"],width = cfg["width"];
    this.setPosition(coord);
    if(title){
         this.title.innerHTML = title;
    }
    this.content.innerHTML = content;
    if(width){
        this.container.style.width = width;
    }
    this.container.style.display = 'block';
    if (this.panMapIfOutOfView) {
        this.panIntoView(coord);
    }
    return this;
};



ol.Overlay.Popup.prototype.isHide = function() {
	return this.container.style.display == 'none';
};

ol.Overlay.Popup.prototype.OnHideInfo = function(callback) {
	 if(callback && typeof callback == 'function'){
	     this.hideCallback = callback;
	 }
};
//更新文本内容
ol.Overlay.Popup.prototype.setContent = function(content) {
	  this.content.innerHTML = content;
};


ol.Overlay.Popup.prototype.setTargetId = function(targetId) {
	  this.targetId = targetId
};

ol.Overlay.Popup.prototype.getTargetId = function() {
	  return this.targetId
};

ol.Overlay.Popup.prototype.updatePostion = function(coord) {
	  this.setPosition(coord);
};

/**
 * @public
 */
ol.Overlay.Popup.prototype.panIntoView = function(coord) {

    var popSize = {
            width: this.getElement().clientWidth + 20,
            height: this.getElement().clientHeight + 20
        },
        mapSize = this.getMap().getSize();

    var tailHeight = 20,
        tailOffsetLeft = 60,
        tailOffsetRight = popSize.width - tailOffsetLeft,
        popOffset = this.getOffset(),
        popPx = this.getMap().getPixelFromCoordinate(coord);

    var fromLeft = (popPx[0] - tailOffsetLeft),
        fromRight = mapSize[0] - (popPx[0] + tailOffsetRight);

    var fromTop = popPx[1] - popSize.height + popOffset[1],
        fromBottom = mapSize[1] - (popPx[1] + tailHeight) - popOffset[1];

    var center = this.getMap().getView().getCenter(),
        curPx = this.getMap().getPixelFromCoordinate(center),
        newPx = curPx.slice();

    if (fromRight < 0) {
        newPx[0] -= fromRight;
    } else if (fromLeft < 0) {
        newPx[0] += fromLeft;
    }

    if (fromTop < 0) {
        newPx[1] += fromTop;
    } else if (fromBottom < 0) {
        newPx[1] -= fromBottom;
    }

    if (this.ani && this.ani_opts) {
        this.ani_opts.source = center;
        this.getMap().beforeRender(this.ani(this.ani_opts));
    }

    if (newPx[0] !== curPx[0] || newPx[1] !== curPx[1]) {
        this.getMap().getView().setCenter(this.getMap().getCoordinateFromPixel(newPx));
    }

    return this.getMap().getView().getCenter();

};

/**
 * Hide the popup.
 */
ol.Overlay.Popup.prototype.hide = function() {
    this.container.style.display = 'none';
    return this;
};
