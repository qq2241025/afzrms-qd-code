(function(){
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 * 终端绑定规则查询
	 */
	GAS.deviceBingRuleQuery = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.vehicleTreeUrl    = GAS.config["globalProxy"]+"/vehicleGroup/treeTerminal?_dc="+new Date().getTime(); //树形菜单
	      	    this.areaListUrl = GAS.config["globalProxy"]+ "/alarmArea/select?_dc="+new Date().getTime();
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
				GAS.deviceBingRuleQuery.superclass.constructor.apply(this, arguments);
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [{
						   border: true,
						   frame : false,
						   region:"west",
						   width : 200,
						   margins : "1px",
						   layout: "fit",
						   items : this.getLeftPanel()
						},{
						   border: true,
						   frame : false,
						   region: "center",
						   layout: "fit",
						   margins : "1px",
						   title : "终端规则绑定查询【点击左侧车辆查询】",
						   items : this.getGridPanel()
						}]
			        });
			    }
			   return  this.mianPanel;
		   },
		   getLeftPanel:function(){
		   		if(!this.leftRreePanel){
		            this.leftRreePanel= new  GAS.VehiceTreePanel({
		           	     dataRoot : "result",
		           	     hideAllChecked: true,
		           	     title    : "车辆列表",
		           	     dataUrl  : this.vehicleTreeUrl
		           });
		           this.leftRreePanel.getMainTreePanel().on({
		               scope : this,
		               click : this.queryBind
		           });
		       }
		       return this.leftRreePanel;
		   },
		   getGridPanel : function() {
			  if(!this.gridPanel) {
				  this.gridPanel = new Ext.grid.GridPanel({
					autoScroll:true,
					layout    : "fit",
					frame     : false,
					border    : false,
					margin    : "1px",
					loadMask  : {
					     msg:"查询中..."
					},
					stripeRows : true,
					enableHdMenu:false,
					columnLines : true,
					viewConfig: {
						forceFit : true,
						columnLines: true
					},
					store     : this.getGridStore(),
					columns   : this.getGridColumn()
				});
			}
			return this.gridPanel;
		},
		queryBind:function(node){
		   if(node && node.isLeaf()){
		       var deviceId = node.attributes["id"];
		       var store = this.getGridStore();
		       Ext.apply(store.baseParams,{
					deviceId :　deviceId
			   });
			   store.load();
		   }
		},
		getGridStore : function() {
		     if(!this.gridstore){
		     	 this.gridstore= new Ext.data.JsonStore({
		     		autoLoad : false,
				 	fields   : ["areaId","rulename","areaname","areaRemark","createTime"],
				 	root     : 'result', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.areaListUrl
					})
		     	 });
		     }
		 	 return this.gridstore;
		},
		getGridColumn:function(){
		    var cls =  [
			   this.getGridLineNum(),
			   {
			     header:"areaId",
			     dataIndex:"areaId",
			     hidden  : true,
			     orderable: true,
			     flex :1
			   },
			   {
			     header:"规则名称",
			     dataIndex:"rulename",
			     orderable: true,
			     flex :1
			   },
			   {
			     header:"区域名称",
			     dataIndex:"areaname",
			     orderable: true,
			     flex :1
			   },
			   {
			   	 header:"区域备注信息",
			     dataIndex:"areaRemark",
			     flex :1
			   },{
			   	 header:"创建时间",
			     dataIndex:"createTime",
			     flex :1
			   }
		   ];
		   return cls;
		},
		//行号
		getGridLineNum:function(){
		  if(!this.lineNum){
			  this.lineNum = new Ext.grid.RowNumberer({header:"序号",width:34});
		  }
		  return this.lineNum;
		}
	});

	
})();
