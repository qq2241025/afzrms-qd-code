(function() {
	var globalProxy = ".";
	// 请求配置
	GAS.config = {
		webSocketUrl : "http://123.57.70.174:8088",
		globalProxy : globalProxy,
		rootNodeId : "1",
		rootNodeText : "青岛国际机场管理系统",
		copyright : "青岛国际机场飞行区资源管理系统V1.0",
		vercode : globalProxy + "/validateCode/image",
		blanUrl : GAS.contextPath + '/ext3.4.1/resources/images/default/s.gif',
		topUrl : GAS.iconPath + "topbg.png",
		mainPageUrl : GAS.contextPath + "FrameUI.html",
		loginURL : GAS.contextPath + "index.html",
		debug : false,
		rootPath : "",
		// mapTileUrl : "http://127.0.0.1/mapBack/{z}/{x}/{x}_{y}.png",
		mapTileUrl : "http://123.57.70.174:8280/maptile-service/maptile?x={x}&y={y}&z={z}",
		mapCenter : [36.264892, 120.388773],
		leafIcon : GAS.iconPath + "leaf.gif", // 默认树形节点图标
		veHicleIcon : GAS.iconPath + "taxi.png", // 树形节点图标,
		loadendIcon : GAS.iconPath + "loadend.gif", // 加载完毕图标
		loadIcon : GAS.iconPath + "loading.gif",
		carIcon : GAS.iconPath + "gps/car_normal_0_1.gif",
		gpsimagePath : GAS.iconPath + "gpscar/",
		OpenMousehandler : GAS.iconPath + "mousehandler/openhand.cur",
		CloseMouseHandler : GAS.iconPath + "mousehandler/closedhand.cur",
		grayTheme : GAS.contextPath + "lib/resources/css/ext-all-gray.css",
		purpleTheme : GAS.contextPath + "lib/resources/css/ext-all-purple.css",
		defaultTheme : GAS.contextPath + "lib/resources/css/ext-all.css",
		themeKeyField : "Ext_theme"
	};
	// 地图配置
	GAS.TitleURL = {
		tiandituUrl : "http://123.57.70.174:8280/GooglemapBack/{z}/{x}/{x}_{y}.png",
		autoNaviUrl : "http://emap{0,1,2,3}.mapabc.com/mapabc/maptile?x={x}&y={y}&z={z}",
		googleUrl : "http://mt0.google.cn/vt/lyrs=m@260000000&hl=zh-CN&gl=CN&src=app&x={x}&y={y}&z={z}&s=Gal",
		aliTileUrl : "http://img.ditu.aliyun.com/get_png?v=v6&x={x}&y={y}&z={z}"
	};

})();