(function(){
	/**
	 * 天线报警查询
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.AlarmAntennaList = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.pageSize = 20;
	      	    this.vehicleListUrl = GAS.config["globalProxy"]+"/locrecord/getAlarmAntennaList?_dc="+new Date().getTime(); //查询接口
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					title  : "天线报警列表",
					items  : this.getMainPanel()
	      	    });
				GAS.AlarmAntennaList.superclass.constructor.apply(this, arguments);
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
		getGridStore : function() {
		     if(!this.gridstore){
		     	 this.gridstore= new Ext.data.JsonStore({
		     		autoLoad : true,
				 	fields   : ["id","deviceName","deptName","brandName","typeName","gpsTime","startTime","endTime"],
				 	root     : 'result', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.vehicleListUrl
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
		    var cls =  [
			   this.getCheckboxModel(),
			   this.getGridLineNum(),
			   {
			     header:"id",
			     dataIndex:"id",
			     flex :1,
			     hidden : true
			   },{
			     header:"车牌号",
			     dataIndex:"deviceName",
			     sortable:true,
			     flex :1
			   },{
			   	 header:"所属部门",
			   	 sortable:true,
			     dataIndex:"deptName",
			     flex :1
			   },{
			     header:"车辆类型",
			     dataIndex:"typeName",
			     sortable:true,
			     flex :1
			   },{
			     header:"车辆品牌",
			     dataIndex:"brandName",
			     sortable:true,
			     flex :1
			   },{
			   	 header:"GPS时间",
			   	 sortable:true,
			     dataIndex:"gpsTime",
			     width : 60,
			     flex :1
			   },{
			   	 header:"开始时间",
			   	 sortable:true,
			     dataIndex:"startTime",
			     width : 60,
			     flex :1
			   },{
			   	 header:"结束时间",
			   	 sortable:true,
			     dataIndex:"endTime",
			     flex :1
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
				      this.deviceName = new Ext.form.TextField({
					      fieldLabel: "车牌号",
					      name : "username",
					      width : 100,
					      anchor : "92%"
					  });
					  this.deptName = new Ext.form.TextField({
					      fieldLabel: "部门名称",
					      name : "username",
					      width : 100,
					      anchor : "92%"
					  });
					  this.startTime = new Ext.form.DateTimeField({
		    		    width : 145,
		    		    height : 23,
						format : "Y-m-d H:i:s",
						allowBlank : false,
						fieldLabel:"开始时间",
						blankText : "请输入开始时间",
						listeners : {
							scope : this,
							blur : function(sttime) {
								var mivalue = sttime.getValue();
								this.endTime.setMinValue(mivalue);
								this.endTime.focus();
							},
							render:function(comb){
								if(comb.getEl() ){
									comb.getEl().dom.style.color = "black";
								}
							}
						}
					});
					this.endTime = new Ext.form.DateTimeField({
						width  : 145,
						height : 23,
						format : "Y-m-d H:i:s",
						fieldLabel:"结束时间",
						allowBlank : false,
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
							text  : '查询',
							scope : this,
							width : 60,
							iconCls :"icon-searchFind",
							handler: this.doQuerySeach
					});
				 this.gridtoolBar = new Ext.Toolbar({
					enableOverflow: true, // 如果tbar溢出自动显示下三角
					items: [
							"部门名称:",this.deptName,
							"车牌号:",this.deviceName,
							"开始时间:",this.startTime,
							"结束时间:",this.endTime,
							querybtn,
							"->"
						]
				});
			}
			return this.gridtoolBar;
		},
		getParams : function(){
		    var params = {};
		    params["vehicleName"] = this.deviceName.getValue();
		    params["deptName"]  = this.deptName.getValue();
		    params["startTime"] = this.startTime.getValue()?this.startTime.getValue().format("Y-m-d H:i:s"):"";
		    params["endTime"]   = this.endTime.getValue()?this.endTime.getValue().format("Y-m-d H:i:s"):"";
		    return params;
		},
		//查询
		doQuerySeach:function(){
		    var param = this.getParams();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,param);
		    store.load();
		},
		//刷新
		refreshGrid : function(){
		    var store = this.getGridStore();
		    store.load();
		}
	});
	
})();

