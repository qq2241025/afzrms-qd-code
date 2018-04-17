(function() {
	//车辆统计
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.AlarmStat = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.VehicleStatUrl = GAS.config["globalProxy"] + "/tj/termAlarm?_dc="+new Date().getTime();
			this.exportUrl      = GAS.config["globalProxy"] + "/tj/termAlarmExport?_dc="+new Date().getTime(); 
			Ext.apply(this, {
				border : false,
				frame : false,
				layout : "fit",
				title : "车辆报警统计【详细】",
				items : this.getMainPanel()
			});
			GAS.AlarmStat.superclass.constructor.apply(this, arguments);
		},
		// 主面板
		getMainPanel : function() {
			if (!this.mianPanel) {
				this.mianPanel = new Ext.Panel({
					border : false,
					frame : false,
					layout : "border",
					items  : [{
						border : true,
						frame  : false,
						layout : "fit",
						margins: "1px",
						items  : this.getGridPanel(),
						region : "center"
					},this.getSouthPanel()]
				});
			}
			return this.mianPanel;
		},
		getSouthPanel:function(){
			if(!this.southPanel){
				this.southPanel = new Ext.Panel({
					border : true,
					frame  : false,
					layout : "fit",
					height : 220,
					collapsible:true,
					collapsed:true,
					split : true,
					collapseMode:"mini",
					margins: "1px",
					title  : "报警车辆详情列表",
					items  : this.getAlramRecordPanel(),
					region : "south"
				});
			}
			return this.southPanel;
		},
		getAlramRecordPanel : function(){
			if(!this.alarmRecord){
				this.alarmRecord = new  GAS.AlarmRecord({
				    queryMod : this
				});
			}
	        return this.alarmRecord ;
		},
		// GIRD列表
		getGridPanel : function() {
			if (!this.gridPanel) {
				this.gridPanel = new Ext.grid.GridPanel({
					autoScroll : true,
					layout : "fit",
					frame : false,
					border : false,
					margin : "1px",
					loadMask : {
						msg : "查询中..."
					},
					stripeRows : true,
					enableHdMenu:false,
					columnLines : true,
					viewConfig: {
						forceFit : true
					},
					store : this.getGridStore(),
					columns : this.getGridColumn(),
					bbar : this.getGridPageBar(),
					sm : this.getCheckboxModel(),
					tbar : this.getGridToolBar(),
					listeners :{
						scope :this,
						rowclick : function(grid,rowId,events){
							 var record  = grid.getStore().getAt(rowId).data||{};
							 this.doClickQuery(record);
						}
					}
				});
			}
			return this.gridPanel;
		},
		doClickQuery:function(record){
		    this.clickDeviceId = record["deviceId"];
		    this.queryTime = record["tjDate"];
		    this.clickRecord = record;
		    this.getSouthPanel().expand(); //展开页面
		    this.getAlramRecordPanel().doQuerySeach();
		},
		getClickQueryRecord:function(){
		    return this.clickRecord ;
		},
		getQueryTime : function(){
			var date = new Date(Date.parse(this.queryTime.replace(/-/g, "/"))).format("Y-m-d");
		    return date;
		},
		getClickRowValue : function(){
		    return this.clickDeviceId;
		},
		// 分页
		getGridPageBar : function() {
			if (!this.pageBar) {
				this.pageBar = new Ext.PagingToolbar({
					store : this.getGridStore(),
					pageSize : this.pageSize,
					displayInfo : true,
					items : [ '-', '每页显示', this.getGridPageSize(), '条' ],
					emptyMsg : "没有数据",
					displayMsg : '显示第 {0} 条到 {1} 条记录，一共 {2} 条'
				});
			}
			return this.pageBar;
		},
		// 分页下拉
		getGridPageSize : function() {
			if (!this.combox) {
				this.combox = new Ext.form.ComboBox({
					mode : "local",
					width : 45,
					editable : false,
					value : this.pageSize,
					triggerAction : "all",
					displayField : "text",
					valueField : "value",
					store : new Ext.data.SimpleStore({
						fields : [ "value", "text" ],
						data : [ [ 20, "20" ], [ 30, "30" ], [ 40, "40" ] ]
					}),
					listeners : {
						scope : this,
						select : function(combo, record, index) {
							this.pageSize = combo.getValue();
							this.getGridPageBar().pageSize = this.pageSize;
							this.onBtnRefesh();
						}
					}
				});
			}
			return this.combox;
		},
		// 刷新
		onBtnRefesh : function() {
			var store = this.getGridStore();
			Ext.apply(store.baseParams, {
				start : 0,
				limit : this.pageSize
			});
			store.load();
		},
		// GRID仓储
		getGridStore : function() {
			if (!this.gridstore) {
				this.gridstore = new Ext.data.JsonStore({
					autoLoad : false,
					fields : [ "deviceId", "areaAlarmCount","vetypeName","vehicleBrand","deptName", "speedAlarmCount","tjDate","areaSpeedAlarmCount","name"],
					root : 'data',
					totalProperty : 'total',
					proxy : new Ext.data.HttpProxy({
						url : this.VehicleStatUrl
					}),
					listeners : {
						scope : this,
						beforeload : function(store, record) {
							var pageParam = this.getQueryParams();
							Ext.apply(store.baseParams, pageParam);
						}
					}
				});
			}
			return this.gridstore;
		},
		// 列
		getGridColumn : function() {
			var cls = [ this.getCheckboxModel(), this.getGridLineNum(), 
		    {
				header : "统计时间",
				dataIndex : "tjDate",
				sortable:true,
				flex : 1
			},{
				header : "车牌号",
				dataIndex : "name",
				sortable:true,
				flex : 1
			},{
				header : "所属部门",
				dataIndex : "deptName",
				sortable:true,
				flex : 1
			},{
				header : "所属部门",
				dataIndex : "vehicleBrand",
				sortable:true,
				hidden :true,
				flex : 1
			},{
				header : "所属部门",
				hidden :true,
				dataIndex : "vetypeName",
				sortable:true,
				flex : 1
			}, {
				header : "区域报警次数",
				dataIndex : "areaAlarmCount",
				sortable:true,
				flex : 1,
				renderer :function(va){
					return va+"次";
				}
			}, {
				header : "超速报警次数",
				dataIndex : "speedAlarmCount",
				sortable:true,
				flex : 1,
				renderer :function(va){
					return va+"次";
				}
			}, {
				header : "区域超速报警次数",
				dataIndex : "areaSpeedAlarmCount",
				sortable:true,
				flex : 1,
				renderer :function(va){
					return va+"次";
				}
			}];
			return cls;
		},
		// 复选框
		getCheckboxModel : function() {
			if (!this.getcheckbox) {
				this.getcheckbox = new Ext.grid.CheckboxSelectionModel({});
			}
			return this.getcheckbox;
		},
		// 行号
		getGridLineNum : function() {
			if (!this.lineNum) {
				this.lineNum = new Ext.grid.RowNumberer({
					header : "序号",
					width : 34
				});
			}
			return this.lineNum;
		},
		// grid工具栏
		getGridToolBar : function() {
			if (!this.gridtoolBar) {
				// 开始时间
				this.alarmStart = new Ext.form.DateField({
					fieldLabel : "开始时间",
					name : "startTime",
					format: "Y-m-d",
			        width : 100,
			        value : new Date().add(Date.DAY, -3),
					allowBlank : false,
					anchor : "92%"
				});
				// 结束时间
				this.alarmEnd = new Ext.form.DateField({
					fieldLabel : "结束时间",
					name : "endTime",
					format: "Y-m-d",
					width : 100,
					value : new Date(),
					allowBlank : false,
					anchor : "92%"
				});
				this.deptName = new Ext.form.TextField({
					fieldLabel : "部门名称",
					width : 100,
					algin : "left",
					value  : "",
					anchor : "92%"
				});
				//部门
				this.vehicleName = new Ext.form.TextField({
					fieldLabel : "车辆牌号",
					name : "dept",
					width : 100,
					allowBlank : false,
					anchor : "92%"
				});
				var querybtn = new Ext.Button({
					text : '查询',
					scope : this,
					width : 60,
					iconCls : "icon-searchFind",
					style : "margin-left:2px",
					handler : this.doQuerySeach
				});
				this.gridtoolBar = new Ext.Toolbar({
					enableOverflow : true, // 如果tbar溢出自动显示下三角
					items : [ "开始时间:", this.alarmStart, 
							  "结束时间:", this.alarmEnd,
							  "部门名称:", this.deptName,
							  "车辆牌号:", this.vehicleName,
							  querybtn,{
							        text : '导出Excel',
									scope : this,
									width : 60,
									iconCls : "icon-excel",
									style : "margin-left:2px",
									handler : this.doExportExcel
							  }
					]
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
		//获取查询参数
		getQueryParams : function(){
		   var params = {};
		   var  startTime = this.alarmStart.getValue().format("Y-m-d");
		   var  endTime = this.alarmEnd.getValue().format("Y-m-d");
		   params["start"] = 0;
		   params["limit"] = this.pageSize;
		   params["beginTime"] = startTime;
		   params["endTime"] = endTime;
		   params["name"] = this.vehicleName.getValue();
		   params["deptName"] = this.deptName.getValue();
		   return params;
		},
		//报警查询
		doQuerySeach:function(){
			var startTime = this.alarmStart.getValue();
			if(!startTime){
			     return ;
			}
			var params = this.getQueryParams();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,params);
		    store.load();
		}
	});
	
	
	
	GAS.AlarmRecord = Ext.extend(Ext.Panel, {
			constructor : function(config) {
	      	    config = config || {};
	      	    this.queryMod = config["queryMod"];
	      	    this.pageSize = 20;
	      	    this.alarmAreaQueryUrl    = GAS.config["globalProxy"] + "/tj/alarmOnce?_dc="+new Date().getTime(); 
	      	    this.cancleSpeedAlarmUrl  = GAS.config["globalProxy"] + "/terminalInstruction/cancleArea?_dc="+new Date().getTime();
	      	    this.exportUrl            = GAS.config["globalProxy"] + "/tj/exportAlarmOnce?_dc="+new Date().getTime(); 
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
	      	  GAS.AlarmRecord.superclass.constructor.apply(this, arguments);
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "fit",
						items  : this.getGridPanel()
			        });
			    }
			   return  this.mianPanel;
		  },
		  //GIRD列表
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
					stripeRows : false,
					enableHdMenu:false,
					columnLines : true,
					viewConfig: {
						forceFit : true
					},
					store     : this.getGridStore(),
					columns   : this.getGridColumn(),
					sm        : this.getCheckboxModel(),
					bbar      : this.getGridPageBar(),
					tbar      : this.getGridToolBar()
				});
			}
			return this.gridPanel;
		},
		//分页
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
		//分页下拉
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
							[20, "20"], 
							[30, "30"], 
							[40, "40"]
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
		//GRID仓储
		getGridStore : function() {
		     if(!this.gridstore){
		     	 this.gridstore= new Ext.data.JsonStore({
		     		autoLoad : false,
				 	fields   : ["id","x","y","deptName","termName","simcard","speedThreshold","alarmType","alarmSubType","distance","gpsTime","height","speed","direction","deviceStatus","alarmType","deviceId"],
				 	root     : 'data', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.alarmAreaQueryUrl
					})
		     	 });
		     }
		 	 return this.gridstore;
		},
		//列
		getGridColumn:function(){
		    var cls =  [
		       this.getCheckboxModel(),
			   this.getGridLineNum(),
			   {
			   	 header:"deviceId",
			   	 sortable:true,
			   	 hidden: true,
			     dataIndex:"deviceId",
			     flex :1
			   },
			   {
			   	 header:"x",
			   	 sortable:true,
			   	 hidden: true,
			     dataIndex:"x",
			     flex :1
			   },
			    {
			   	 header:"y",
			   	 sortable:true,
			   	 hidden: true,
			     dataIndex:"y",
			     flex :1
			   },
			    {
			   	 header:"id",
			   	 sortable:true,
			   	 hidden: true,
			     dataIndex:"id",
			     flex :1
			   },
			   {
			   	 header:"车牌号码",
			   	 sortable:true,
			     dataIndex:"termName",
			     flex :1
			   },{
			   	 header:"所属部门",
			   	 sortable:true,
			     dataIndex:"deptName",
			     flex :1
			   },
			   {
			   	 header:"报警时间",
			   	 sortable:true,
			     dataIndex:"gpsTime",
			     flex :1
			   },{
			   	 header:"报警类型",
			   	 sortable:true,
			     dataIndex:"alarmType",
			     flex :1,
			     renderer :function(val){
			        val = val == 1 ? "<span style='color:red'>超速</span>":val == 2 ? "<span style='color:blue'>区域报警</span>":"";
			        return val;
			     }
			   },{
			   	 header:"行驶车速【km/h】",
			   	 sortable:true,
			     dataIndex:"speed",
			     flex :1
			   },{
			   	 header:"行驶方向",
			   	 sortable:true,
			     dataIndex:"direction",
			     flex :1,
			     renderer :function(val){
			        var fid = GAS.getGPSDirection(val || 0);
			        return fid["value"];
			     }
			   }
		   ];
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
		//grid工具栏
		getGridToolBar : function() {
			if (!this.gridtoolBar) {
					//部门
					this.minSpeed = new Ext.form.NumberField({
						fieldLabel : "最小速度",
						width : 60,
						algin : "left",
						minValue : 0,
						value  : 0,
						allowBlank : false,
						anchor : "92%"
					});
					this.maxSpeed = new Ext.form.TextField({
						fieldLabel : "最大速度",
						width : 60,
						algin : "left",
						minValue : 0,
						value  : 0,
						allowBlank : false,
						anchor : "92%"
					});
					this.startTime = new Ext.form.TimeField({  
					    fieldLabel:'时间', 
					    width : 70,
					    empty:'请选择时间',  
					    minValue:'00:00',  
					    maxValue:'23:59', 
					    format  :"H:s",
					    increment:60,  
					    title : "开始时间",
					    invalidText:'日期格式无效'  
					}); 
					this.endTime = new  Ext.form.TimeField({  
					    fieldLabel:'时间',  
					    empty:'请选择时间',  
					    minValue:'00:00',  
					    maxValue:'23:59', 
					    format :"H:s",
					    title : "结束时间",
					    width  : 70,
					    increment:60,  
					    invalidText:'日期格式无效'  
					});
					
					var querybtn = new Ext.Button({
						text : '过滤',
						scope : this,
						width : 60,
						iconCls : "icon-searchFind",
						style : "margin-left:2px",
						handler : this.doQuerySeach
					});
					var showTruckBtn = new Ext.Button({
						text : '查询轨迹',
						scope : this,
						width : 60,
						iconCls : "icon-searchFind",
						style : "margin-left:2px",
						handler : this.doQueryTrack
					});
				   this.gridtoolBar = new Ext.Toolbar({
						enableOverflow: true, // 如果tbar溢出自动显示下三角
						items: ["最小速度",this.minSpeed,"最大速度",this.maxSpeed,
						        "开始时间",this.startTime,"结束时间",this.endTime,
						        querybtn,{
							        text : '导出Excel',
									scope : this,
									width : 60,
									iconCls : "icon-excel",
									style : "margin-left:2px",
									handler : this.doExportExcel
							  },showTruckBtn
						]
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
		//取消超速报警
		cancleSpeedAlarm:function(){
			var list = this.getGridPanel().getSelectionModel().getSelections(), selectList = [];
			if (list.length > 0) {
				for (var i = 0, len = list.length; i < len; i++) {
					var record = list[i], id = record.get("deviceId");
					selectList.push(id);
				}
				GAS.AOP.send({
					url  : this.cancleSpeedAlarmUrl, // 加载模块
					params : {
					   deviceIds: selectList.join(";")
					},
					scope : this,
					callbackFn : function(res) {
						res = Ext.decode(res.responseText);
						if (res["success"] && res["success"]=="true") {
							this.doQuerySeach(); //刷新
							Ext.Msg.alert('提示信息',  '操作成功');
						} else {
							Ext.Msg.alert('提示信息',  '操作失败');
						}
					}
				});
			} else {
				 Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		//获取查询参数
		getQueryParams : function(deviceId,startTime,endTime){
		   var params = {};
		   params["start"] = 0;
		   params["limit"] = this.pageSize;
		   params["deviceId"] = this.getQueryMod().getClickRowValue();
		   params["queryTime"] = this.getQueryMod().getQueryTime(); 
		   params["startDate"] = this.startTime.getValue() ? this.startTime.getValue():"";
		   params["endDate"]  = this.endTime.getValue() ? this.endTime.getValue():"";
		   params["minSpeed"] = this.minSpeed.getValue();
		   params["maxSpeed"] = this.maxSpeed.getValue();
		   return params;
		},
		getQueryMod : function(){
			return this.queryMod;
		},
		
		doQueryTrack:function(){
		    var list = this.getGridPanel().getSelectionModel().getSelections(), selectList = [];
			if (list.length == 1) {
				 var record = list[0].data;
				 var clickRecord = this.getQueryMod().getClickQueryRecord();
				 var wind = new GAS.ScanInfo({
				     deviceId : record["deviceId"],
				     alarmTime: record["gpsTime"],
				     termName: record["termName"],
				     deptName: record["deptName"],
				     vehicleType: clickRecord["vetypeName"],
				     vehicleBrand: clickRecord["vehicleBrand"],
				     speed: record["speed"],
				     trackId: record["id"],
				     lng: record["x"],
				     lat: record["y"]
				 });
				 wind.show();
			} else {
				 Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		//报警查询
		doQuerySeach:function(){
			var devideId= this.getQueryMod().getClickRowValue();
			if(!devideId){
				return ;
			}
			var params = this.getQueryParams();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,params);
		    store.load();
		}
	});
	
	
	GAS.ScanInfo = Ext.extend(Ext.Window, {
		constructor : function(config){
	   	    this.config = config || {};
	   	    this.deviceId  = config["deviceId"];
	   	    this.alarmTime = config["alarmTime"];
	   	    this.termName = config["alarmTime"];
	   	    this.deptName = config["deptName"];
	   	    this.vehicleType = config["deptName"];
	   	    this.vehicleBrand = config["vehicleBrand"];
	   	    this.speed = config["speed"];
	   	    this.queryTime  = 3;
	   	    this.trackId = config["trackId"];
	   	    this.locaionX =  config["lng"];
	   	    this.locaionY =  config["lat"];
	   	    this.trackIcon = GAS.iconPath + "trackPoint.png";//终点图标
	      	this.trackAlarmIcon = GAS.iconPath + "red.png";//终点速度为零\
	      	this.AlarmIcon = GAS.iconPath + "AlarmMarker.png";//终点速度为零\
	   	    this.trackDataUrl = GAS.config["globalProxy"]+"/locrecord/list?_dc="+new Date().getTime(); 
	      	//this.trackDataUrl = "/demo/track.json?_dc="+new Date().getTime(); 
	   	    Ext.apply(this,{
		   	       title:"报警点轨迹查询",
				   width:880,
				   height:480,
				   layout:"fit",
				   buttonAlign:"center",
				   closable: true,
				   maximizable: true,
				   minimizable: false,
				   constrain : true, //是否允许超出范围
			  	   modal: true,//背景遮罩
				   items: this.getMainPanel()
			   });
			GAS.ScanInfo.superclass.constructor.call(this,config);
	   },
	   //主面板
	   getMainPanel:function(){
		    if(!this.mianPanel){
		    	//部门
				this.combox = new Ext.form.ComboBox({
					mode : "local",
					width : 60,
					editable : false,
					value : this.pageSize,
					triggerAction : "all",
					displayField : "text",
					valueField : "value",
					value : this.queryTime,
					store : new Ext.data.SimpleStore({
						fields : [ "value", "text" ],
						data : [ [ 3, "3" ], [ 5, "5" ], [ 8, "8" ] ]
					})
				});
				var querybtn = new Ext.Button({
					text : '查询',
					scope : this,
					width : 60,
					iconCls : "icon-searchFind",
					style   : "margin-left:2px",
					handler : this.doTrackQuery
				});
		        this.mianPanel = new Ext.Panel({
		          	border : false,
					frame  : false,
					layout : "fit",
					tbar   : new Ext.Toolbar({
						items : ["时间范围:", this.combox,querybtn]
					}),
					items  : this.getMapPanel()
		        });
		    }
		   return  this.mianPanel;
	  },
	  getMainLoadMask : function() {
		if (!this.mainMaskMsg) {
		 	 this.mainMaskMsg = new Ext.LoadMask(this.getMainPanel().getEl(), {
				 msg : "数据请求处理中,请稍后....."
			 });
		}
		return this.mainMaskMsg;
	  },
	  getMapPanel :function(){
	       if(!this.mapPanel){
		       this.mapPanel = new GAS.BaseMap({
		           title : "青岛地图",
		           hideTools: true
		       });
		       this.mapPanel.on({
		           scope  : this,
		           maprender : function(){
		           	 var me = this;
		           	 setTimeout(function(){
		           	     me.doTrackQuery();
		           	 },5000);
				   }
		       });
		   }
		 return  this.mapPanel;
	  },
	  getCurrentDate : function(){
	  	 var dateStr = this.alarmTime.replace(/-/g, "/");
	     var date = new Date(dateStr);  
		 return date;
	  },
	  getStartTime:function(){
	  	  var date = this.getCurrentDate();
	  	  var time  = this.combox.getValue() || 0;
	  	  var second = time * 60 /2 ;
	  	  var newdate = date.getTime() - 1000* second;
	  	  return   new Date(newdate).format("Y-m-d H:i:s");
	  },
	  getendTime:function(){
	      var date = this.getCurrentDate();
	  	  var time  = this.combox.getValue() || 0;
	  	  var second = time * 60 /2 ;
	  	  var newdate = date.getTime() + 1000* second;
	  	  return  new Date(newdate).format("Y-m-d H:i:s");
	  },
	  getQueryParams:  function(){
	     var params = {};
	     params["beginTime"] = this.getStartTime();
	  	 params["endTime"] = this.getendTime();
	  	 params["deviceId"] = this.deviceId;
	  	 params["start"] = 0;
	  	 params["limit"] = 69935;
	  	 return params;
	  },
	  queryCallback : function(res,req){
	      this.getMainLoadMask().hide();
	      var result = Ext.decode(res.responseText);
	 	  if(result && result["data"] && result["data"].length == 0){
	 		Ext.Msg.alert("信息提示", "没有查询到轨迹数据数据");
	      }else{
	     	var data = result["data"] || [];
	     	this.drawLineOnMap(data);
	     }
	  },
	  clearAllMap:function(){
	  	 this.getMapPanel().hideMapInfoWindow();
	  	//清除轨迹
	  	 if(this.TrackLine){
	  	     this.getMapPanel().removeLayers(this.TrackLine);
	  	 }
	  	 //清除轨迹点
		 var listSE = this.TrackPoint;
		 if( listSE &&listSE.length > 0){
			 for (var i = 0; i < listSE.length; i++) {
			 	  var overlayer = listSE[i];
			 	  this.getMapPanel().removeOverLayer(overlayer);
			 }
		 }
	  },
	  createPoint:function(lng,lat){
	       var point = this.getMapPanel().createPoint(lng,lat);
   	       return point;
	  },
	  drawAllPoint : function(data){
	  	    this.TrackPoint = [];
	      	for (var i = 0; i < data.length; i++) {
	  			  var record =  data[i];
	  			  var marker = this.addTrackPoint(record);
	      	      this.TrackPoint.push(marker);
	  		}
	  },
	  //添加轨迹点
	  addTrackPoint :function(record){
         var lng = parseFloat(record.x),lat = parseFloat(record.y);
         var point = this.createPoint(lng,lat);
         var speed = record["speed"],alarmTime= record["gpsTime"];
		 var msg =  [
				'<b>车牌号码: </b> ' + this.termName,
				'<br><b>所属部门: </b> ' + this.deptName,
				'<br><b>车辆类型: </b> ' + this.vehicleType,
				'<br><b>车辆品牌: </b> ' + this.vehicleBrand ,
				'<br><b>行驶车速: </b> ' + speed +"km/h",
				'<br><b>行驶时间: </b> ' + alarmTime
			].join("");
		  var trackicon =  this.trackIcon;
//  		  if(record["speed"] && Math.round(record["speed"])<=1){
//  		     trackicon =this.trackAlarmIcon;
//  		  }	
		  //
		  if(record["alarmTypes"]!=0){
      		trackicon =this.trackAlarmIcon;
      	  }
		  var midCfg = {
	             lnglat      : point,
            	 iconSize    : [8,8],
            	 width       : 230,
            	 msg         : msg, 
            	 iconUrl     : trackicon,
            	 clickEnable : true,
            	 iconAnchor  : [3, 4]
	      };
	      if(record["id"] ==this.trackId ){
  		      midCfg["iconUrl"] =this.AlarmIcon;
  		      midCfg["isOpened"] = true;
  		  }
	      var marker = this.getMapPanel().addMarker(midCfg);
	      return marker;
	  },
	  addAlarmMarker: function(){
	      
	  	  var lng = parseFloat(this.locaionX),lat = parseFloat(this.locaionY);
          var point = this.createPoint(lng,lat);
	  	
           var msg =  [
				'<b>车牌号码: </b> ' + this.termName,
				'<br><b>所属部门: </b> ' + this.deptName,
				'<br><b>车辆类型: </b> ' + this.vehicleType,
				'<br><b>车辆品牌: </b> ' + this.vehicleBrand ,
				'<br><b>行驶车速: </b> ' + this.speed  +"km/h",
				'<br><b>行驶时间: </b> ' + this.alarmTime
			].join("");
          var trackicon =this.trackAlarmIcon;
	  	  var midCfg = {
	             lnglat      : point,
            	 iconSize    : [8,8],
            	 width       : 23,
            	 msg         : msg, 
            	 iconUrl     : trackicon,
            	 clickEnable : true,
            	 iconAnchor  : [3, 4]
	      };
	      var marker = this.getMapPanel().addMarker(midCfg);
	      return marker;
	  },
	  drawLineOnMap : function(data){
	  		this.clearAllMap();
	  		var datalist = [];
	  		for (var i = 0; i < data.length; i++) {
	  			 var record =  data[i];
	  			 var lng = parseFloat(record.x),lat = parseFloat(record.y);
	  			 var point = this.createPoint(lng,lat);
			     datalist.push(point);
	  		}
	  		this.TrackLine = this.getMapPanel().addPolyLine({
	  			points:datalist
	  		});  //先划线
	  	    this.drawAllPoint(data); //化点
	  },
	  doTrackQuery: function(){
	      this.getMainLoadMask().show();
		  GAS.AOP.send({
		       url   : this.trackDataUrl,
		       method : "post",
		       params: this.getQueryParams(),
		       timeout : 2 * 60 * 1000,
		       scope : this,
		       callbackFn:this.queryCallback
		   });
	  }
	});

})();
