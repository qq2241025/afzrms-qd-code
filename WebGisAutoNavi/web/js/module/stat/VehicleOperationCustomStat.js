(function(){
	//综合查询统计
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.VehicleOperationCustomStat = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config =config || {};
	      	    this.pageSize = 20;
		     	this.vehicleTreeUrl    = GAS.config["globalProxy"]+"/vehicleGroup/treeTerminal?_dc="+new Date().getTime(); //树形菜单
		     	this.statListUrl       = GAS.config["globalProxy"]+"/locrecord/searchlist?_dc="+new Date().getTime(); //规则
				this.exportUrl         = GAS.config["globalProxy"] + "/locrecord/searchlistExport?_dc="+new Date().getTime(); 
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					items  : this.getMainPanel()
				});
				GAS.VehicleOperationCustomStat.superclass.constructor.apply(this, arguments);
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
						   title : "综合查询统计",
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
		           	     title    : "车辆列表",
		           	     dataUrl  : this.vehicleTreeUrl
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
						forceFit : true
					},
					store     : this.getGridStore(),
					columns   : this.getGridColumn(),
					bbar      : this.getGridPageBar(),
					sm        : this.getCheckboxModel(),
					tbar      : this.getGridToolBar()
				});
			}
			return this.gridPanel;
		},
		getGridPageBar : function() {
			if (!this.pageBar) {
				 this.pageBar = new Ext.PagingToolbar({
					store         : this.getGridStore(),
					pageSize      : this.pageSize,
					displayInfo   : true,
					items:['-','每页显示', this.getGridPageSize(), '条' ],
					emptyMsg      : "没有数据", 
					displayMsg    : '显示第 {0} 条到 {1} 条记录，一共 {2} 条'
				});
			}
			return this.pageBar;
		},
		getGridPageSize:function(){
			if(!this.combox){
				this.combox = new Ext.form.ComboBox({
					mode     : "local",
					width    : 45,
					editable : false,
					value    : this.pageSize,
					triggerAction : "all",
					displayField  : "text",
					valueField    : "value",
					store : new Ext.data.SimpleStore({
						fields : ["value", "text"],
						data : [
							[20, "20"],[30, "30"], [40, "40"]
						]
					}),
					listeners : {
						scope : this,
						select: function(combo,record,index){
							this.pageSize = combo.getValue();
							this.getGridPageBar().pageSize = this.pageSize;
						    this.onBtnRefesh();
						}
					}
				});
			}
			return this.combox;
		},
		//刷新
		onBtnRefesh:function(){
			var  store  = this.getGridStore();
			Ext.apply(store.baseParams,{
				   start : 0,
				   limit : this.pageSize
			});
		    store.load();
		},
		getGridStore : function() {
		     if(!this.gridstore){
		     	 this.gridstore= new Ext.data.JsonStore({
		     		autoLoad : false, //不自动加载数据
				 	fields   : ["tjtime", "distance", "deviceName", "tjDate",  "gpsTime","maxspeed", "minspeed", "speedAlarm", "areaAlarm","deviceId"],
				 	root     : 'result', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.statListUrl
					}),
		            listeners : {
						scope : this,
						beforeload:function(store,record){
							var pageParam = {
								 start:0,
								 limit:this.pageSize
							};
							Ext.apply(store.baseParams,pageParam);
						}
					}
		     	 });
		     }
		 	 return this.gridstore;
		},
		getGridColumn:function(){
		    var cls = [
			this.getGridLineNum(), 
			{
				header : "统计时间段",
				dataIndex : "tjtime",
				width :160
			},{
				header : "终端名称",
				dataIndex : "deviceName",
				sortable:true,
				flex : 1
			},{
				header : "行驶里程(km)",
				dataIndex : "distance",
				flex : 1
			},
			{
				header : "最大速度时间点",
				dataIndex : "gpsTime",
				flex : 1
			},
			{
				header : "最大速度(km/h)",
				dataIndex : "maxspeed",
				sortable:true,
				flex : 1
			}, {
				header : "最小速度(km/h)",
				dataIndex : "minspeed",
				sortable:true,
				flex : 1
			}, 
			{
				header : "超速报警次数",
				dataIndex : "speedAlarm",
				sortable:true,
				flex : 1
			}, {
				header : "区域报警次数",
				dataIndex : "areaAlarm",
				sortable:true,
				flex : 1
			}];
			return cls;
		},
		//复选框
		getCheckboxModel:function(){
		  if(!this.getcheckbox){
			  this.getcheckbox = new Ext.grid.CheckboxSelectionModel({});
		  }
		  return this.getcheckbox;
		},
		//行号
		getGridLineNum:function(){
		  if(!this.lineNum){
			  this.lineNum = new Ext.grid.RowNumberer({header:"序号",width:34});
		  }
		  return this.lineNum;
		},
	    //请求参数
	    getQueryParams : function(){
			var params   ={};
		    var  startTime = this.alarmStart.getValue().format("Y-m-d H:i:s");
		    var  endTime = this.alarmEnd.getValue().format("Y-m-d H:i:s");
		    params["beginTime"] = startTime; 
		    params["endTime"] = endTime; 
		    params["limit"] = this.pageSize; 
		    params["start"] = 0 ; 
		    return params;
	    },
		//获取终端的ID列表
		getSelectTreeDevideList:function(){
		    var list = this.getLeftPanel().getSelectTreeNode();
		    var newList = [];
		    if(list && list.length > 0){
		       for (var i = 0; i < list.length; i++) {
		       	   var data = list[i],id = data["id"];
		       	   newList.push(id);
		       }
		    }
		    return newList;
		},
		//grid工具栏
		getGridToolBar : function() {
			if (!this.gridtoolBar) {
				// 开始时间
				this.alarmStart = new Ext.form.DateTimeField({
					width : 145,
					height : 23,
					format : "Y-m-d H:i:s",
					allowBlank : false,
					fieldLabel:"开始时间",
					name : "startTime",
					blankText : "请输入开始时间",
					value : new Date().clearTime(),
					listeners : {
						scope : this,
						blur : function(sttime) {
							var mivalue = sttime.getValue();
							this.alarmEnd.setMinValue(mivalue);
							this.alarmEnd.focus();
						},
						render:function(comb){
							if(comb.getEl() ){
								comb.getEl().dom.style.color = "black";
							}
						}
					}
				});
				// 结束时间
				this.alarmEnd = new Ext.form.DateTimeField({
					width  : 145,
					height : 23,
					format : "Y-m-d H:i:s",
					fieldLabel:"结束时间",
					name : "endTime",
					allowBlank : false,
					value : new Date(),
					blankText : "请输入结束时间",
					listeners : {
						scope : this,
						render:function(comb){
							if(comb.getEl() ){
								comb.getEl().dom.style.color = "black";
							}
						}
					}
				});
				var querybtn = new Ext.Button({
					text : '查询',
					scope : this,
					width : 60,
					iconCls : "icon-searchFind",
					style : "margin-left:2px",
					scope : this, //指定脚本执行作用域函数的范围
					handler : this.doQuerySeach
				});
				this.gridtoolBar = new Ext.Toolbar({
					enableOverflow : true, // 如果tbar溢出自动显示下三角
					items : [ "开始时间:", this.alarmStart, "结束时间:", this.alarmEnd, querybtn,{
							        text : '导出Excel',
									scope : this,
									width : 60,
									iconCls : "icon-excel",
									style : "margin-left:2px",
									handler : this.doExportExcel
							  }]
				});
			}
			return this.gridtoolBar;
		},
		//导出excel
		doExportExcel:function(){
		    var total  = this.getGridStore().getCount();
		    if(total>0){
			      var urlParams =  this.getGridStore().baseParams || {};
			   urlParams["start"] = this.getGridPageBar().cursor; //当前页的start
			   var listParams = [];
			   for (var key in urlParams) {
			   	  listParams.push(key+"="+urlParams[key]);
			   }
			   var url = this.exportUrl +"&"+listParams.join("&");
			   window.open(url);
		     }else{
				Ext.Msg.alert('提示信息','没有需要导出的数据');
			}
		},
		//查询
		doQuerySeach:function(){
		      var list =  this.getSelectTreeDevideList();
		      var startTime = this.alarmStart.getValue();
		      var endTime = this.alarmEnd.getValue();
			  if(list && list.length > 0){
			  	   var mid = endTime.getTime() - startTime.getTime();
			  	   if(mid > 15 * 24 * 3600 * 1000 ){
			  	   	  Ext.Msg.alert("提示信息","查询时间不能超过15天");
			  	      return false;
			  	   }
				    var params  = this.getQueryParams();
					params["deviceIds"] = list.join("@");
					var store = this.getGridStore();
					Ext.apply(store.baseParams,params);
		    		store.load();
			  }else{
				  　Ext.Msg.alert('提示信息', '至少选择一个终端');
			  }
		},
		//刷新
		refreshGrid : function(){
		    var store = this.getGridStore();
		    store.load();
		},
		//获取终端的ID列表
		getSelectTreeDevideList:function(){
		    var list = this.getLeftPanel().getSelectTreeNode();
		    var newList = [];
		    if(list && list.length > 0){
		       for (var i = 0; i < list.length; i++) {
		       	   var data = list[i],id = data["id"];
		       	   newList.push(id);
		       }
		    }
		    return newList;
		}
		
		
	});
	
})();