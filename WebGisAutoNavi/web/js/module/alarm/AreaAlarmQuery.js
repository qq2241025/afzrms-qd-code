(function(){
	/**
	 * 越界报警查询
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.AreaAlarmQuery = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.pageSize = 20;
	      	    this.alarmAreaQueryUrl  = GAS.config["globalProxy"] + "/tj/areaAlarm?_dc="+new Date().getTime(); 
	      	    this.exportUrl          = GAS.config["globalProxy"] + "/tj/areaAlarmExport?_dc="+new Date().getTime();
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					title  : "区域报警查询",
					items  : this.getMainPanel()
	      	    });
				GAS.AreaAlarmQuery.superclass.constructor.apply(this, arguments);
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
					stripeRows : true,
					enableHdMenu:false,
					columnLines : true,
					viewConfig: {
						forceFit : true,
						columnLines: true
					},
					store     : this.getGridStore(),
					columns   : this.getGridColumn(),
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
				 	fields   : ["deptName","termName","areaNo","simcard","id","alarmSubType","distance","gpsTime","height","speed","direction","deviceStatus","alarmType","deviceId"],
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
			   this.getGridLineNum(),
			    {
			   	 header:"车牌号码",
			   	 sortable:true,
			     dataIndex:"termName",
			     flex :1
			   },{
			   	 header:"设备序列号",
			   	 hidden: true,
			     dataIndex:"deviceId",
			     sortable:true,
			     flex :1
			   },{
			   	 header:"所属部门",
			   	 sortable:true,
			     dataIndex:"deptName",
			     flex :1
			   },{
			   	 header:"SIM号码",
			   	 sortable:true,
			     dataIndex:"simcard",
			     flex :1
			   },{
			   	 header:"报警时间",
			   	 sortable:true,
			     dataIndex:"gpsTime",
			     flex :1
			   },{
			   	 header:"行驶车速【km/h】",
			   	 sortable:true,
			     dataIndex:"speed",
			     flex :1
			   },{
			   	 header:"区域名称",
			   	 sortable:true,
			     dataIndex:"areaNo",
			     flex :1,
			     renderer:function(val){
			        return GAS.cacheAlarmCache.getCacheAlarm(val) || "暂无数据";
			     }
			   },{
			   	 header:"区域类型",
			   	 sortable:true,
			     dataIndex:"alarmSubType",
			     flex :1,
			     renderer: function(val){
			        //[0, '进区域'], [1, '出区域'], [2, '进出区域限速']
			      	return val ==0 ? "进区域" : val ==1 ?  "出区域": val ==2 ? "进出区域限速":"";
			     }
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
				      //开始时间
				   	  var startDateV = new Date().add(Date.DAY, -3);
					  startDateV=Ext.util.Format.date(startDateV, 'Y-m-d 00:00:00');
				      this.alarmStart = new Ext.form.DateTimeField({
					      fieldLabel: "开始时间",
					      name : "roleName",
					      format: "Y-m-d H:i:s",
					      width : 140,
					      value : startDateV,
					      allowBlank:false,
					      anchor : "92%"
					  });
					  //结束时间
					  this.alarmEnd = new Ext.form.DateTimeField({
					      fieldLabel: "结束时间",
					      name : "roleName",
					      format: "Y-m-d H:i:s",
					      width : 140,
					      value : new Date(),
					      allowBlank:false,
					      anchor : "92%"
					  });
				     //车辆牌号
				     this.vehicleNum = new Ext.form.TextField({
					      fieldLabel: "车辆牌号",
					      name : "roleName",
					      width : 100,
					      anchor : "92%"
					  });
					  //报警类型
					  this.alarmType =new Ext.form.ComboBox({
					  	    title :"报警类型",
					  	    width : 80,
						    typeAhead: true,
						    editable : false,
						    triggerAction: 'all',
						    lazyRender:true,
						    mode: 'local',
						    store: new Ext.data.ArrayStore({
						        fields: ['id','type'],
						        data: [[-1,'全部'],[0,'进区域报警'],[1,'出区域报警'],[2,"进出区域限速"]]
						    }),
						    valueField: 'id',
						    displayField: 'type'
						});
					 var querybtn = new Ext.Button({
							text  : '查询',
							scope : this,
							width : 60,
							iconCls :"icon-searchFind",
							style : "margin-left:2px",
							handler: this.doQuerySeach
					});
				   this.gridtoolBar = new Ext.Toolbar({
						enableOverflow: true, // 如果tbar溢出自动显示下三角
						items: [
							"开始时间:",this.alarmStart,
							"结束时间:",this.alarmEnd,
							"车牌号码:",this.vehicleNum,
							"报警类型:",this.alarmType,
							querybtn,
							{
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
		   var  startTime = this.alarmStart.getValue().format("Y-m-d H:i:s");
		   var  endTime = this.alarmEnd.getValue().format("Y-m-d H:i:s");
		   params["start"] = 0;
		   params["limit"] = this.pageSize;
		   params["beginTime"] = startTime;
		   params["endTime"] = endTime;
		   if(this.alarmType.getValue() == -1){
		   		params["alarmSubType"] = "";
		   }else{
		        params["alarmSubType"] = this.alarmType.getValue();
		   }
		   params["name"]  =  this.vehicleNum.getValue();
		   return params;
		},
		//报警查询
		doQuerySeach:function(){
			var params = this.getQueryParams();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,params);
		    store.load(params);
		}
	});
	
})();



