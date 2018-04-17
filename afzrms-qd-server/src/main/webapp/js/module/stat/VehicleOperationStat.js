(function() {
	//车辆运行统计
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.VehicleOperationStat = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.workingStatUrl = GAS.config["globalProxy"] + "/tj/termOperation?_dc="+new Date().getTime(); 
			this.exportUrl      = GAS.config["globalProxy"] + "/tj/termOperationExport?_dc="+new Date().getTime(); 
			Ext.apply(this, {
				border : false,
				frame : false,
				layout : "fit",
				items : this.getMainPanel()
			});
			GAS.VehicleOperationStat.superclass.constructor.apply(this, arguments);
		},
		
		//点击叶子节点定位当前的车辆
		onTreeNodeClick:function(node){
		  if(node && node.isLeaf()){
		      var record = node.attributes;
		      var deviceid= record["id"];
		      this.moveCarToCenterAndTip(deviceid);
		  }
		},
		//左侧树形菜单
	   getBusTreePanel:function(){
	       if(!this.BusTree){
	           this.BusTree= new  GAS.VehiceTreePanel({
	           	    frame  : false,
	           	    layout : "fit",
	           	    border : false,
	           	    title : "车辆运行统计"
	           });
	       }
	       return this.BusTree;
	   },
	   getSelectTreeNodeRecord : function(){
	   	    var list = [];
	   	    var check =  this.getBusTreePanel().getSelectTreeNode();
		    if(check  && check.length > 0){
		     	   for (var i = 0; i < check.length; i++) {
		      	    	var node   = check[i];
		      	    	var deveid  = node["id"];
		      	    	list.push(deveid);
		      	   }
		    }
	   	   return list;
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
							 region : "west",
							 width  : 215,
							 margins: "1px",
							 split : true,
							 collapseMode:"mini",
						     items : this.getBusTreePanel()
						},{
							border : true,
							frame  : false,
							layout : "fit",
							items  : this.getGridPanel(),
							margins: "1px",
							region : "center"
					}]
				});
			}
			return this.mianPanel;
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
					viewConfig : {
						forceFit : true,
						columnLines : true
					},
					store : this.getGridStore(),
					columns : this.getGridColumn(),
					bbar : this.getGridPageBar(),
					sm : this.getCheckboxModel(),
					tbar : this.getGridToolBar()
				});
			}
			return this.gridPanel;
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
					fields : [ "travelTime", "distance", "tjDate", "maxSpeed", "name", "engineRunningTime", "averageSpeed","deviceId","maxSpeedTime"],
					root : 'data',
					totalProperty : 'total',
					proxy : new Ext.data.HttpProxy({
						url : this.workingStatUrl
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
		 getTimer:function(value){
	        	var result= "";
	        	var theTime = parseInt(value);// 分 
				var hour = 0;// 小时  
				var minu = 0;// 分 
				if(theTime>=0 && theTime<10){
				     result = "0"+theTime+"分";
				}else{
				     if(theTime >= 60) { 
				    	 hour = parseInt(theTime/60); 
				    	 minu = parseInt(theTime%60); 
				    	 if(theTime % 60 ==0){
	    					 result= hour+"小时";
	    					 return result;
	    				 }
    					 if(minu >= 0 && minu < 10){
          				    minu = "0"+minu+"分";
          				 }else{
          				    minu = minu+"分";
          				 }
    					 if(hour >0 && hour< 10){
        				     result= "0"+hour+"小时"+ minu;
        				 }else{
        					 result= hour+"小时"+ minu; 
        				 }
    				 }else{
    				     if(theTime >= 0 && theTime < 10){
    				    	 result = "0"+theTime+"分";
        				 }else{
        					 result = theTime+"分";
        				 }
    				 } 
				}
				return result; 
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
			}, {
				header : "行驶有效时间",
				dataIndex : "travelTime",
				sortable:true,
				width : 100,
				scope : this,
				renderer:function(v){
				  v = v || 0;
				  var time = this.getTimer(v);
				  return time;
				}
			}, {
				header : "行驶里程(km)",
				dataIndex : "distance",
				flex : 1,
				renderer:function(v){
				   v=  Number(v || 0).toFixed(3);
				   return v;
				}
			}, {
				header : "最大速度时间点",
				dataIndex : "maxSpeedTime",
				sortable:true,
				width : 80
			}, {
				header : "最大速度(km/h)",
				dataIndex : "maxSpeed",
				sortable:true,
				flex : 1,
				renderer:function(v){
				    v=  Number(v || 0).toFixed(3);
				   return v;
				}
			}, {
				header : "平均速度(km/h)",
				dataIndex : "averageSpeed",
				sortable:true,
				flex : 1,
				renderer:function(v){
				   v=  Number(v || 0).toFixed(3);
				   return v;
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
					width : 100,
					format : "Y-m-d",
					value : new Date().add(Date.DAY, -3),
					allowBlank : false,
					anchor : "92%"
				});
				// 结束时间
				this.alarmEnd = new Ext.form.DateField({
					fieldLabel : "结束时间",
					name : "endTime",
					width : 100,
					format : "Y-m-d",
					value : new Date(),
					allowBlank : false,
					anchor : "92%"
				});
				this.exportFile = new Ext.Button({
					text : "导出",
					scope : this,
					width : 60,
					iconCls : "icon-exportTable",
					style : "margin-left:2px",
					handler : this.doExportFile
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
					items : [ "开始时间:", this.alarmStart, "结束时间:", this.alarmEnd, querybtn,{
							        text : '导出当前Excel',
									scope : this,
									width : 60,
									iconCls : "icon-excel",
									style : "margin-left:2px",
									handler : this.doExportExcel
							  },{
							        text : '导出全部Excel',
									scope : this,
									width : 60,
									iconCls : "icon-excel",
									style : "margin-left:2px",
									handler : this.doExportAllExcel
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
		//全部导出excel
		doExportAllExcel:function(){
		     var total  = this.getGridStore().getCount();
		     if(total>0){
			       var urlParams =  this.getGridStore().baseParams || {};
				   urlParams["start"] = this.getGridPageBar().cursor; //当前页的start
				   urlParams["limit"] = 20000;
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
		   params["deviceId"] = this.getSelectTreeNodeRecord().join("");
		   return params;
		},
		//报警查询
		doQuerySeach:function(){
		 	 var list =  this.getSelectTreeNodeRecord();
	      	 if( list.length ==  1 || list.length == 0){
	      	     var params = this.getQueryParams();
			     var store = this.getGridStore();
			     Ext.apply(store.baseParams,params);
			     store.load();
	      	 }else{
	      	     Ext.Msg.alert('提示信息', '选择一个终端');
	     	 }
		}
	});

})();
