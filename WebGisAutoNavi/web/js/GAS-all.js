Ext.onReady(function(){
	Ext.MessageBox.wait("","系统载入中，请稍后...");
	var fielList = [
	    "lib/uxlib/ext-lang-zh_CN.js",
	    "lib/uxlib/AOP.js",
	    "lib/uxlib/SearchField.js",
	    "lib/uxlib/ComboBoxTree.js",
	    "lib/uxlib/ListToTree.js",
	    "lib/uxlib/Spinner.js",
	    "lib/uxlib/SpinnerField.js",
	    "lib/uxlib/CheckComBox.js",
	    "lib/uxlib/Ext.ux.ColorField.js",
	    "js/websocket/web_socket.js",
	    
	    "lib/socket.io/socket.io.js",
	    "js/config.js",
	    
	    "js/common/BaseTreePanel.js",
	    "js/common/BaseOpenlayerMap.js",
	    "js/common/VehiceTree.js",
	    
	    "js/module/cacheArea/AlarmAreaCache.js?",
	    
	    "js/module/monitor/MonitorMap.js",
	    "js/module/monitor/MonitorVehicle.js",
	   
	    
	    "js/module/trackplay/TrackMap.js",
	    
	    "lib/openlayers/ol.js",
	    "lib/openlayers/ol-tools.js",
	    "lib/openlayers/ol3-popup.js",
	    
	    
	    
	    "js/module/alarm/MessageBox.js",
		"js/Frame/loginOut.js",
	    "js/Frame/bootToolBar.js",
		"js/Frame/TopMenu.js",
		"js/Frame/FrameUI.js"
	];
	
	
	GAS.LoadScript.load(fielList,function(){
		
		 Ext.Ajax.timeout = 300000; //5分钟超时  
		
	     Ext.MessageBox.hide();
	     GAS.StartMainFrame();  //启动主界面
	     
	     GAS.cacheAlarmCache.loadAllCacheAlarm(); //缓存区域对象
	     //加载主题
	     var themeKey = GAS.config["themeKeyField"];
	     var themeUrl = GAS.Cookie.getCookie(themeKey);
	     if(themeUrl){
	         GAS.MainLog.themeSetting(themeKey,themeUrl);
	     }
	},window,true);
});