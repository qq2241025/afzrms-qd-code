(function() {
	//车辆违章统计
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.VehicleViolatingStat = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			config = config || {};
			this.pageSize = 20;
			this.VehicleStatUrl = GAS.config["globalProxy"] + "/tj/termAlarm?_dc="+new Date().getTime();
			this.exportUrl      = GAS.config["globalProxy"] + "/tj/termAlarmExport?_dc="+new Date().getTime(); 
			
			Ext.apply(this, {
				border : false,
				frame : false,
				layout : "fit",
				title : "车辆违章统计",
				items : this.getMainPanel()
			});
			GAS.VehicleViolatingStat.superclass.constructor.apply(this, arguments);
		},
		// 主面板
		getMainPanel : function() {
			if (!this.mianPanel) {
				this.mianPanel = new Ext.Panel({
					border : false,
					frame : false,
					layout : "fit",
					items : this.getGridPanel()
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
					fields : [ "deviceId", "areaAlarmCount", "speedAlarmCount","tjDate","areaSpeedAlarmCount","name"],
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
				// 车辆牌号
				this.vehicleNum = new Ext.form.TextField({
					fieldLabel : "车辆牌号",
					name : "vehicleNumber",
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
					items : [ "开始时间:", this.alarmStart, "结束时间:", this.alarmEnd,
							"车牌号码:", this.vehicleNum,
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
		   params["name"] = this.vehicleNum.getValue();
		   return params;
		},
		//报警查询
		doQuerySeach:function(){
			var params = this.getQueryParams();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,params);
		    store.load();
		}
	});

})();
