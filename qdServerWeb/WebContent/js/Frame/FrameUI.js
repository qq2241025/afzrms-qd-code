(function() {

	GAS.MainFrame = Ext.extend(Ext.Panel, {
				constructor : function(config) {
					// 调用基类构造函数
					this.moduleCache = {}; // 打开模块的缓存变量
					Ext.apply(this, {
								border : false,
								layout : 'fit',
								frame : false,
								items : this.getViewPanel()
							});
					GAS.MainFrame.superclass.constructor.apply(this, arguments);
				},
				getNorthPanel : function() {
					if (!this.northPanel) {
						this.northPanel = new GAS.TopMenu({
									name : "GAS.TopMenu",
									frame : false,
									border : false,
									layout : "fit"
								});
					}
					this.northPanel.on({
								scope : this,
								menuClick : this.openNewModule
							});
					return this.northPanel;
				},
				// 上面的工具栏
				getButtonBar : function() {
					if (!this.buttomBar) {
						this.buttomBar = new GAS.StatusBar({
									name : "GAS.StatusBar"
								});
					}
					return this.buttomBar;
				},
				openNewModule : function(module) {
					var moduleName = module["modulename"]
							|| module["modulename"];
					var modulePath = module["modulepath"]
							|| module["modulePath"];
					var moduleTitle = module["text"] || module["modulename"];
					if (modulePath && moduleName) {
						var jsUrl = GAS.contextPath + modulePath;
						// 动态加载js脚本 ，回调函数自己控制
						GAS.importlib(jsUrl, function() {
									this.loadOpenQueryModule(moduleName,
											moduleTitle); // 加载功能模块
								}, this);
					} else {
						Ext.MessageBox.alert("提示", "模块加载异常...");
					}
				},
				// 主界面功能
				getViewPanel : function() {
					if (!this.viewpanel) {
						this.viewpanel = new Ext.Panel({
									border : false,
									layout : 'border',
									bbar : this.getButtonBar(),
									items : [{
												region : "north",
												layout : "fit",
												frame : false,
												border : false,
												height : 30,
												style : "border-width:0px 0px 1px 0px",
												items : this.getNorthPanel()
											}, {
												region : "center",
												layout : "fit",
												frame : false,
												border : false,
												items : this.getCenterPanel()
											}]
								});
					}
					return this.viewpanel;
				},
				// 获取当期打开的功能模块
				getOpenModule : function() {
					return this.openModule;
				},
				// 打开模块
				loadOpenQueryModule : function(moduleName, moduleTitle, closed) {
					// 实例化该模块
					var type = eval(moduleName);
					if (!type) {
						Ext.MessageBox.alert("提示", "模块加载失败!");
						return;
					}
					var modPanel = new type({
								layout : "fit",
								border : false,
								frame : false
							});
					if (!modPanel) {
						Ext.MessageBox.alert("提示", "模块实例化失败!");
						return;
					}
					if (this.moduleCache[moduleName]) {
						var panel = this.moduleCache[moduleName];
						this.getCenterPanel().setActiveTab(panel);
					} else {
						var openModule = new Ext.Panel({
									name : moduleName,
									title : moduleTitle || "",
									layout : "fit",
									border : false,
									closable : closed == false ? false : true,
									frame : false,
									items : modPanel,
									modName : moduleName
								});
						this.moduleCache[moduleName] = openModule;
						this.getCenterPanel().add(openModule);
						this.getCenterPanel().setActiveTab(openModule);
					}
					this.getCenterPanel().doLayout(true);
				},
				// 加载监控
				loadMonitorPlat : function() {
					var monitorUrl = "js/module/monitor/BusMonitorMainPage.js";
					GAS.importlib(monitorUrl, function() {
								var moduleName = "GAS.BusMonitor", moduleTitle = "车辆监控"
								this.loadOpenQueryModule(moduleName,
										moduleTitle, false);
							}, this);
				},
				getCenterPanel : function() {
					if (!this.mainFrame) {
						this.mainFrame = new Ext.TabPanel({
									frame : false,
									border : false,
									plain : false,
									bodyBorder : false,
									activeTab : 0,
									enableTabScroll : true,
									listeners : {
										scope : this,
										afterrender : this.loadMonitorPlat,
										remove : function(tabpanel, panel) {
											var modname = panel["modName"];
											if (this.moduleCache[modname]) {
												delete this.moduleCache[modname];
											}
										}
									}
								});
					}
					return this.mainFrame;
				}

			});

})();
// 启动主界面
GAS.StartMainFrame = function() {
	Ext.BLANK_IMAGE_URL = GAS.contextPath + 'images/s.gif';
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	// 获取浏览器的地址
	var urlParams = {};
	var querystr = window.location.search.substr(1);
	querystr.replace(/([^=&]+)=([^=&]+)/g, function(s, key, val) {
				urlParams[key] = val;
			});
	var loginTokenID = GAS.Cookie.getCookie("tokenID"); // 获取登陆的tokenID
	var urlTokenId = urlParams["tokenID"], isOpenMain = false;
	if (urlTokenId && urlTokenId == loginTokenID) {
		isOpenMain = true;
	} else {
		window.location.href = GAS.config["loginURL"];
		return;
	}
	// 如果允许显示
	if (isOpenMain) {
		GAS.MainFrameUI = new GAS.MainFrame({});
		new Ext.Viewport({
					layout : 'fit',
					border : false,
					margins : "1px",
					items : GAS.MainFrameUI
				});
	}
	// window.onbeforeunload = function() {
	// return "你确定要离开该页面吗？";
	// };
}
