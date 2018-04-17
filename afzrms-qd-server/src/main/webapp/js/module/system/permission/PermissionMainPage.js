(function(){
	//轨迹查询主界面
	GAS.PermissionMainPage = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.deptListUrl = "data/system/group/orgTreeList.json";
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
				GAS.PermissionMainPage.superclass.constructor.apply(this, arguments);
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [{
								border : true,
								frame  : false,
								layout :"fit",
								width  : 200,
								margins: "1px 2px 1px 1px",
								items  : this.getBusTreePanel(),
								region : "west"
							},{
								border : true,
								frame  : false,
								layout : "fit",
								margins: "1px",
								items  : this.getCenterPanel(),
								region : "center"
						}]
			        });
			    }
			   return  this.mianPanel;
		   },
		   //中间面板
		   getCenterPanel:function(){
		         if(!this.centLeftPanel){
			         this.centLeftPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [{
								border : false,
								frame  : false,
								layout :"fit",
								width  : 420,
								margins: "1px",
								split  : true,
								collapseMode:"mini",
								items  : this.getMiddlePanel(),
								region : "west"
							},{
								border : true,
								frame  : false,
								layout : "fit",
								margins: "1px",
								items  : this.getRightPanel(),
								region : "center"
						}]
			        });
			    }
			   return  this.centLeftPanel;
		   },
		   //中间面板
		   getMiddlePanel:function(){
		       if(!this.middlePanel){
					this.middlePanel = new GAS.PermissionUserQuery({
						border : false,
						frame  : false,
						layout : "fit"
					});
		       }
		       return this.middlePanel;
		   },
		   //右边地图面板
		   getRightPanel:function(){
		   	  if(!this.rightPanel){
				this.rightPanel = new GAS.PermissionQuery({
					border : false,
					frame  : false,
					layout : "fit"
				});
		   	  }
		   	  return  this.rightPanel;
		   },
		   //左侧树形面板
		   getBusTreePanel:function(){
		       if(!this.BusTree){
		           this.BusTree= new  GAS.baseTreePanel({
		           	     dataRoot : "result",
		           	     title    : "集团列表",
		           	     dataUrl  : this.deptListUrl
		           });
		       }
		       return this.BusTree;
		   }
	});
	
})();
