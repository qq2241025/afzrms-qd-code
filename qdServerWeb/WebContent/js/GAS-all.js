Ext.onReady(function() {
			Ext.MessageBox.wait("", "系统载入中，请稍后...");
			var fielList = ["lib/uxlib/ext-lang-zh_CN.js?v=1.0",
					"lib/uxlib/AOP.js?v=1.0", "lib/uxlib/SearchField.js?v=1.0",
					"lib/uxlib/ComboBoxTree.js?v=1.0",
					"lib/uxlib/ListToTree.js?v=1.0",
					"lib/uxlib/Spinner.js?v=1.0",
					"lib/uxlib/SpinnerField.js?v=1.0",

					"lib/socket.io/socket.io.js?v=1.0", "js/config.js?v=1.0",

					"js/common/BaseTreePanel.js?v=1.0",
					"js/common/BaseOpenlayerMap.js?v=1.0",
					"js/common/VehiceTree.js?v=1.0",

					"js/module/monitor/MonitorMap.js?v=1.0",
					"js/module/monitor/MonitorVehicle.js?v=1.0",

					"js/module/trackplay/TrackMap.js?v=1.0",

					"lib/openlayers/ol.js?v=1.0",
					"lib/openlayers/ol3-popup.js?v=1.0",

					"js/module/alarm/MessageBox.js?v=1.0",
					"js/Frame/loginOut.js?v=1.0",
					"js/Frame/bootToolBar.js?v=1.0",
					"js/Frame/TopMenu.js?v=1.0", "js/Frame/FrameUI.js?v=1.0"];
			GAS.LoadScript.load(fielList, function() {
						Ext.MessageBox.hide();
						GAS.StartMainFrame(); // 启动主界面
						// 加载主题
						var themeKey = GAS.config["themeKeyField"];
						var themeUrl = GAS.Cookie.getCookie(themeKey);
						if (themeUrl) {
							GAS.MainLog.themeSetting(themeKey, themeUrl);
						}
					}, window, true);
		});