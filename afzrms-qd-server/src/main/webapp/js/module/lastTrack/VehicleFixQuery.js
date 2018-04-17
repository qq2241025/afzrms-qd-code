(function(){
	//车辆报修查询
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.VehicleFixQuery = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config =config || {};
	      	    this.pageSize = 20;
	      	    this.deleteUrl   = GAS.config["globalProxy"]+"/repair/delete?_dc="+new Date().getTime(); //删除接口
		     	this.statListUrl = GAS.config["globalProxy"]+"/repair/list?_dc="+new Date().getTime(); //规则
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					items  : this.getMainPanel()
				});
				GAS.VehicleFixQuery.superclass.constructor.apply(this, arguments);
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "fit",
						title  : "车辆报修查询",
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
		     		autoLoad : true, //不自动加载数据
				 	fields   : ["vehicleTypeId", "simcard","deptName", "id", "createTime", "username","descText","terName","vehicleTypeName","deviceId"],
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
		    this.getCheckboxModel(),
			this.getGridLineNum(), 
			{
				header : "部门名称",
				dataIndex : "deptName",
				width :80
			},{
				header : "车辆牌号",
				dataIndex : "terName",
				sortable:true,
				flex : 1
			},
			{
				header : "车辆类型",
				dataIndex : "vehicleTypeName",
				flex : 1
			},
			{
				header : "用户",
				dataIndex : "username",
				sortable:true,
				flex : 1
			}, {
				header : "SIM号码",
				dataIndex : "simcard",
				sortable:true,
				flex : 1
			}, 
			{
				header : "创建时间",
				dataIndex : "createTime",
				sortable:true,
				flex : 1
			}, {
				header : "备注信息",
				dataIndex : "descText",
				sortable:true,
				width : 140
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
		    params["limit"] = this.pageSize; 
		    params["start"] = 0 ; 
		    params["tername"]  = this.vehicleName.getValue() ; 
		    params["deptName"] = this.deptName.getValue(); 
		    return params;
	    },
		//grid工具栏
		getGridToolBar : function() {
			if (!this.gridtoolBar) {
				this.deptName = new Ext.form.TextField({
					fieldLabel : "部门名称",
					width : 100,
					algin : "left",
					value  : "",
					anchor : "92%"
				});
				this.vehicleName = new Ext.form.TextField({
					fieldLabel : "车辆牌号",
					name : "dept",
					width : 100,
					anchor : "92%"
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
					items : [ "部门名称:", this.deptName, "车辆牌号:", this.vehicleName, querybtn,"->",{
				   	   name:"remove",
					   text:"删除",
					   scope:this,
					   iconCls:"icon-remove",
					   handler:this.doremove
				   }]
				});
			}
			return this.gridtoolBar;
		},
		//删除记录
		doremove:function(){
			var list = this.getGridPanel().getSelectionModel().getSelections(), selectList = [];
			if (list.length > 0) {
				for (var i = 0, len = list.length; i < len; i++) {
					var record = list[i], id = record.get("id");
					selectList.push(id);
				}
				Ext.Msg.show({
					title : '请确认操作？',
					msg : '确认想要删除记录吗?',
					buttons : Ext.Msg.YESNO,
					icon : Ext.MessageBox.QUESTION,
					scope : this,
					fn : function(btn, text) {
						if (btn == 'yes') {
							this.removeAjaxHandler(selectList);
						}
					}
				});
			} else {
				 Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		removeAjaxHandler:function(list){
			 GAS.AOP.send({
				url  : this.deleteUrl, // 加载模块
				params : {
				  ids: list.join(",")
				}, // 操作方法
				scope : this,
				callbackFn : function(res) {
					res = Ext.decode(res.responseText);
					if (res["success"]) {
						Ext.Msg.alert('提示信息', res["result"] || res["msg"] || '成功删除数据');
						this.refreshGrid(); //刷新
					} else {
						Ext.Msg.alert('提示信息', res["result"] || res["msg"] || '删除数据失败');
					}
				}
			});
		},
		//查询
		doQuerySeach:function(){
			  var params  = this.getQueryParams();
		      var store = this.getGridStore();
			  Ext.apply(store.baseParams,params);
	    	  store.load();
		},
		//刷新
		refreshGrid : function(){
		    var store = this.getGridStore();
		    store.load();
		}
	});
	
})();