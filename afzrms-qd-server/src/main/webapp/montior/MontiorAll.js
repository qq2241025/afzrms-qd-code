(function() {

	GAS.MainFrame = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			// 调用基类构造函数
			this.moduleCache = {}; //打开模块的缓存变量
			this.LogoUrl = GAS.config.topUrl;
			Ext.apply(this,{
			     border :  false,
			     layout : 'fit',
			     frame  : false,
				 items  : this.getViewPanel()
			});
			GAS.MainFrame.superclass.constructor.apply(this, arguments);
		},
		getNorthPanel:function(){
			if(!this.northPanel){
		       var meubar=new  Ext.Toolbar({
					items : ['<img src=' + this.LogoUrl + ' style= "display:block" />',"->"]
		       });
			    this.northPanel= new Ext.Panel({
					frame : false,
					border : false,
					margins : '1 0 1 1',
					height : 30,
					tbar: meubar,
					html:""
				});
			}
			return this.northPanel;
		},
		//上面的工具栏
		getButtonBar : function(){
			if(!this.buttomBar){
				this.buttomBar = new GAS.StatusBar({
					 name :"GAS.StatusBar"
				});
			}
			return this.buttomBar;
		},
		openNewModule:function(module){
	         var  moduleName =   module["modulename"] || module["modulename"];
		     var  modulePath =   module["modulepath"] || module["modulePath"];
		     var  moduleTitle =   module["text"] || module["modulename"];
		     if(modulePath && moduleName){
		         var jsUrl = GAS.contextPath+modulePath;
			     //动态加载js脚本 ，回调函数自己控制
	             GAS.importlib(jsUrl,function(){
		             this.loadOpenQueryModule(moduleName,moduleTitle); //加载功能模块
		         },this);
		     }else{
		         Ext.MessageBox.alert("提示","模块加载异常...");
		     }
		},
		//主界面功能
		getViewPanel : function() {
			if (!this.viewpanel) {
				this.viewpanel = new Ext.Panel({
					border :  false,
					layout : 'border',
					items  :  [{
					    region : "north",
					    layout : "fit",
					    frame  : false,
					    border : false,
					    height : 30,
					    style  : "border-width:0px 0px 1px 0px",
					    items  : this.getNorthPanel()
					},{
					    region : "center",
					    layout : "fit",
					    frame  : false,
					    border : false,
					    items  : this.getCenterPanel()
					}]
				});
			}
			return this.viewpanel;
		},
		//获取当期打开的功能模块
		getOpenModule:function(){
		   return this.openModule;
		},
		getMontorPanel:function(){
			if(!this.montiorPanel){
			    this.montiorPanel= new GAS.BusMonitor({
					frame : false,
					border : false,
					margins : '1 0 1 1',
					height : 30, 
					title: "监控页面"
				});
			}
			return this.montiorPanel;
		},
		getCenterPanel : function() {
			if(!this.mainFrame){
				this.mainFrame = new Ext.TabPanel({
		       	    frame   : false,
		       	    border  : false,
		       	    plain   : false,
		       	    bodyBorder :false,
		       	    activeTab: 0,
		       	    enableTabScroll: true,
		       	    items:[this.getMontorPanel()]
				});
			}
			return this.mainFrame;
		}

	});
	
})();



Ext.onReady(function(){
	
		
 	Ext.Ajax.timeout = 300000; //5分钟超时  
 	Ext.BLANK_IMAGE_URL = GAS.contextPath + 'images/s.gif';
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	//如果允许显示
    GAS.MainFrameUI = new GAS.MainFrame({});
	new Ext.Viewport({
		layout  : 'fit',
		border  : false,
		margins : "1px",
		items   :  GAS.MainFrameUI
	});
	      
});


