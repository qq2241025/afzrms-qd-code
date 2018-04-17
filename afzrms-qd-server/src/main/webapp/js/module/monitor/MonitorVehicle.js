(function(){
	/**
	 * 地图基类扩展
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.MonitorVehicle = Ext.extend(Ext.Panel,{
		   constructor : function(config) {
	      	    config = config || {};
	      	    this.taskList = {};
	      	    GAS.taskMgr = this.taskList;
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
	      	    this.addEvents("mintorClick","alarmClick","tabChange");
				GAS.MonitorVehicle.superclass.constructor.apply(this, arguments);
		   },
		   //监控store
		   getMintorStore:function(){
		       if(!this.mintorStore){
		           this.mintorStore = new Ext.data.JsonStore({
					 	fields   : ["id","lng","lat","vehicleNum","vehicleBds","status","vehicleType","time","deptname","direct","simcard","speed","address"],
					 	root     : 'data', 
						totalProperty: 'total'
			     	});
		       }
		       return this.mintorStore;
		   },
		   //store 查找对象
		   getMintorRecordById:function(targetId){
		       var obj = null;
		       this.getMintorStore().findBy(function(record, id) {  
			       var flag = false;
			       if(record.get("id") == targetId){
			          obj = record;
			       	  flag = true;
			       }
			       return flag;
			   });
			   return obj;
		   },
		   //监控页面
		   getMintorPanel:function(){
		   	   if(!this.intorgridPanel){
		   	        var column = [
			            new Ext.grid.RowNumberer({header:"序号",width:34}),
				       		{header:"车辆牌号",dataIndex:"vehicleNum",flex :1},
						    {header:"SIM卡号",dataIndex:"simcard", flex :1,hidden : true}, //隐藏
						    {header:"所属部门",dataIndex:"deptName", flex :1},
						    {header:"车辆型号",dataIndex:"vehicleType", flex :1},
						    {header:"车辆品牌",dataIndex:"vehicleBrand", flex :1},
						   	{header:"行驶速度(km/h)",dataIndex:"speed", flex :1},
						   	{header:"车辆状态",dataIndex:"status", flex :1,renderer:function(val,meta){
						   	    if("LOC" == val){
						   	       return "<span style='color:blue'><b>定位</b></span>";
						   	    }else {
						   	    	return "<span style='color:red'><b>报警</b></span>";
						   	    }
						   	}},
						   	{header:"行驶时间",dataIndex:"time", flex :1},
						   	{header:"经度",dataIndex:"lat", flex :1,hidden : true}, //隐藏
						   	{header:"纬度",dataIndex:"lng", flex :1,hidden : true}, //隐藏
						   	{header:"行驶方向",dataIndex:"direct", flex :1}
					   	];
					  this.intorgridPanel = new Ext.grid.GridPanel({
						autoScroll: true,
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
						store     : this.getMintorStore(),
						columns   : column,
						listeners :{
						    scope : this,
						    rowclick : function(grid,index){
						        var record = grid.getStore().getAt(index);
						        var targetId = record.get("id");
						        this.fireEvent("mintorClick",targetId,record);
						    }
						}
					}); 
		   	   }
			   return this.intorgridPanel;
		   },
		    //监控store
		   getAlarmStore:function(){
		       if(!this.alarmStore){
		           this.alarmStore = new Ext.data.JsonStore({
					 	fields   : ["id","lng","lat","areaNo","areaName","vehicleNum","deviceId","vehicleType","speed","vehicleColor","deptname","alarmType","simcard","address","direct"],
					 	root     : 'data', 
						totalProperty: 'total'
		     	  });
		       }
		       return this.alarmStore;
		   },
		   //任务高亮显示一闪一闪
		   taskHight:function(targetId,htmlEl){
		         var counts =0,total = 8;
   	        	 var callFn = function(row){
				 	 if(counts>total) {
				 		 row.style.backgroundColor = "";
				 		 var task = this.taskList[targetId];
				 		 if(task){
				 		     Ext.TaskMgr.stop(task);
				 		 }
				 	     return ;
				 	 }
				 	 if(counts%2 == 0){
				 		 row.style.backgroundColor = "#FF5151";
					 }else{
						 row.style.backgroundColor = "";
				     }
				 };
		   	     var task = {  
		            run: function() {  
		                counts ++;
				 	 	callFn.call(this,htmlEl);
		            },
		            scope : this,
		            interval: 200  
		        };
		        Ext.TaskMgr.start(task); //启动  
		        this.taskList[targetId] = task;
		   },
		   //刷新grid视图
		   refreshMitorGrid:function(){
		       try{
			   		var gridview = this.getMintorPanel().getView();
		       		gridview.refresh();
			   	}catch(e){}
		   },
		   //刷新grid视图
		   refreshAlarmGrid:function(){
			   	try{
			   		var gridview = this.getAlarmPanel().getView();
			       gridview.refresh();
			   	}catch(e){}
		   },
		   //设置目标高亮显示
		   setMitorRecordHightShow:function(targetId){
		   	     var index = this.getMintorStore().find("id",targetId);
		   	     if(index != null){
		   	        var row = this.getMintorPanel().getView().getRow(index);
		   	        if(row){
		   	        	row.style.backgroundColor = "#00DB00";
				        window.setTimeout(function(){
				            row.style.backgroundColor = "";
				        },2000);
		   	        }
		   	    }
		   },
		    //报警页面
		   getAlarmPanel:function(){
		   	   if(!this.alarmGrid){
		   	   	   var column = [
			            new Ext.grid.RowNumberer({header:"序号",width:34}),
			            {header:"车辆牌号",dataIndex:"vehicleNum",width :70},
			            {header:"SIM卡号",dataIndex:"simcard", flex :1,hidden : true}, //隐藏
					    {header:"所属部门",dataIndex:"deptName",width :55},
					    {header:"车辆型号",dataIndex:"vehicleType",width :60},
					    {header:"车辆品牌",dataIndex:"vehicleBrand",width :60},
					   	{header:"行驶速度(km/h)",dataIndex:"speed",width :70},
					   	{header:"行驶时间",dataIndex:"time", flex :1},
					   	{header:"经度",dataIndex:"lat", flex :1,hidden : true}, //隐藏
					   	{header:"纬度",dataIndex:"lng", flex :1,hidden : true}, //隐藏
					   	{header:"行驶方向",dataIndex:"direct", width :60},
						{header:"报警类型", dataIndex:"alarmType",width :60 },
						{header:"超速阀值(km/h)", dataIndex:"speedThreshold", flex :1 }
						//{header:"报警区域编号", dataIndex:"areaNo", flex :1 },
						//{header:"报警区域名称", dataIndex:"areaName", flex :1}
					    ];
					  this.alarmGrid = new Ext.grid.GridPanel({
						autoScroll: true,
						layout    : "fit",
						frame     : false,
						border    : false,
						stripeRows : true,
						enableHdMenu:false,
						columnLines : true,
						loadMask  : {
						     msg:"查询中..."
						},
						viewConfig: {
							forceFit : true,
							columnLines: true
						},
						store     : this.getAlarmStore(),
						columns   : column,
						listeners :{
						    scope : this,
						    rowclick : function(grid,index){
						        var record = grid.getStore().getAt(index);
						        var targetId = record.get("id");
						        this.fireEvent("alarmClick",targetId,record);
						    }
						}
					});
		   	   }
		   	   return this.alarmGrid;
		   },
		    //设置目标高亮显示
		   setAlarmRecordHightShow:function(targetId){
		   	     var index = this.getAlarmStore().find("id",targetId);
		   	     if(index != null){
		   	        var row = this.getAlarmPanel().getView().getRow(index),me= this;
		   	        if(row){
		   	        	row.style.backgroundColor = "red";
				        window.setTimeout(function(){
				            row.style.backgroundColor = "";
				        },3000);
		   	        }
		   	    }
		   },
		    //激活报警面板显示报警的记录
		   activeAlarmPanelRecord:function(targetId){
		       this.getMainPanel().setActiveTab(1); //激活报警面板
		       var index = this.getAlarmStore().find("id",targetId);
		   	   if(index != null){
		   	        var rowRecord = this.getAlarmPanel().getView().getRow(index);
		   	        if(this.rowRecord){ //清除样式
		   	           this.rowRecord.style.backgroundColor = "";
		   	        }
		   	        if(rowRecord){
		   	            this.rowRecord = rowRecord;
		   	        	rowRecord.style.backgroundColor = "#FF0000";
		   	        }
		   	    }
		   },
		   getNorthPanel:function(){
		      if(!this.mainNorthPanel){
		          this.mainNorthPanel = new Ext.Panel({
	          	   		frame  : false,
		                border : false,
		                layout : "fit",
		                title  : '<span style="color:#15428b;" id="monTitle">监控车辆列表</span>',
		                items  : this.getMintorPanel()
		          });
		      }
		      return this.mainNorthPanel;
		   },
		   setNorthTitle:function(title){
		      var titleDom = Ext.getDom("monTitle");
		      titleDom.innerHTML = title;
		   },
		   //覆盖主面板
		   getMainPanel: function() {
				if (!this.mainpanel) {
		            this.mainpanel = new Ext.TabPanel({
		               frame  : false,
		               border : false,
		               activeTab : 0,
		               tabPosition: 'bottom',
		               items:[this.getNorthPanel(),{
			                title: '报警车辆列表',
			                frame  : false,
			                border : false,
			                layout : "fit",
			                items  : this.getAlarmPanel()
			            }],
			            listeners:{
			               scope :this,
			               tabchange:function(tab,culpanel){
			                   this.fireEvent("tabChange",tab,culpanel);
			               }
			            }
		         });
		      }
			  return this.mainpanel;
		   }
	});
})();



