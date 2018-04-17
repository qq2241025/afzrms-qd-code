(function(){
	/**
	 * zhengang.he@autonavi.com
	 * ps: 采用插件法覆盖原始的方法,打开自定义化
	 */
	L.Tooltip = L.Class.extend({
		initialize: function (map) {
			this._map = map;
			this._popupPane = map._panes.popupPane;
			this._singleLineLabel = false;
		},
		dispose: function () {
			if (this._container) {
				this._popupPane.removeChild(this._container);
				this._container = null;
			}
		},
	
		updateContent: function (labelText) {},
	
		updatePosition: function (latlng) {},
	
		showAsError: function () {},
	
		removeError: function () {}
	});
	
})();