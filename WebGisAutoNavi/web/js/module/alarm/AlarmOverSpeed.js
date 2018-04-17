(function(){
	/**
	 * 超速报警查询
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.AlarmOverSpeedQuery = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.pageSize = 20;
	      	    this.vehicleTreeUrl       = GAS.config["globalProxy"]+"/vehicleGroup/treeTerminal?_dc="+new Date().getTime(); //树形菜单
	      	    this.alarmAreaQueryUrl    = GAS.config["globalProxy"] + "/tj/overspeedAlarm?_dc="+new Date().getTime(); 
	      	    this.cancleSpeedAlarmUrl  = GAS.config["globalProxy"] + "/terminalInstruction/cancleArea?_dc="+new Date().getTime();
	      	    this.exportUrl            = GAS.config["globalProxy"] + "/tj/overspeedAlarmExport?_dc="+new Date().getTime(); 
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					//title  : "超速报警查询",
					items  : this.getMainPanel()
	      	    });
				GAS.AlarmOverSpeedQuery.superclass.constructor.apply(this, arguments);
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
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [ {
						   border: true,
						   frame : false,
						   region:"west",
						   width : 240,
						   margins : "1px",
						   layout: "fit",
						   items : this.getLeftPanel()
						},{
						   border: true,
						   frame : false,
						   region: "center",
						   layout: "fit",
						   margins : "1px",
						   title : "超速报警列表",
						   items : this.getGridPanel()
						}]
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
				 	fields   : ["deptName","termName","simcard","speedThreshold","id","alarmSubType","distance","gpsTime","height","speed","direction","deviceStatus","alarmType","deviceId"],
				 	root     : 'data', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.alarmAreaQueryUrl
					}),
		            listeners : {
						scope : this,
						beforeload:function(store,record){
							var pageParam =this.getQueryParams();
							Ext.apply(store.baseParams,pageParam);
						}
					}
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
			   	 header:"车牌号码",
			   	 sortable:true,
			     dataIndex:"termName",
			     flex :1
			   },{
			   	 header:"设备序列号",
			   	 sortable:true,
			     dataIndex:"deviceId",
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
			   	 header:"超速阀值【km/h】",
			   	 sortable:true,
			     dataIndex:"speedThreshold",
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
				      //开始时间
				   	  var startDateV = new Date().add(Date.HOUR, -6);
					  startDateV=Ext.util.Format.date(startDateV, "Y-m-d H:i:s");
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
		   var  startTime = this.alarmStart.getValue().format("Y-m-d H:i:s");
		   var  endTime = this.alarmEnd.getValue().format("Y-m-d H:i:s");
		   params["start"] = 0;
		   params["limit"] = this.pageSize;
		   params["beginTime"] = startTime;
		   params["endTime"] = endTime;
		   params["deviceId"]  =  this.getSelectTreeDevideList()[0];
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
		//报警查询
		doQuerySeach:function(){
		    var list =  this.getSelectTreeDevideList();
		    if(list && list.length == 0){
			  　Ext.Msg.alert('提示信息', '至少选择一个终端');
			   return ;
		    }
			var params = this.getQueryParams();
 			var  startTime = this.alarmStart.getValue();
		    var  endTime = this.alarmEnd.getValue();
		    var  midTIme = 6 * 3600 * 1000; //查询6个小时以内的
		    var  difTime = endTime.getTime()  - startTime.getTime();
		    if(difTime > midTIme){
                Ext.Msg.alert('提示信息', '请选择6个小时以内的数据,<br/>数据量大,可能会导致查询超时,无法返回结果');
			    return ;
		    }else{
			     var store = this.getGridStore();
		    	 Ext.apply(store.baseParams,params);
			     Ext.Msg.confirm('提示信息', '查询数据量大,可能会导致你的查询超时，<br/>无法返回结果，你确定这样做吗?',function(btn){
                    if(btn=="yes"){
						store.load();    
                    }
			     });
		    }
		}
	});
	
})();



