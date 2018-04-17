(function(){
	//驾驶员信息查询列表
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.VehicleDriverList = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config =config || {};
	      	    this.pageSize = 20;
	      	    this.driverListUrl = GAS.config["globalProxy"]+"/driver/list?_dc="+new Date().getTime(); //查询接口
	      	    this.deleteUrl     = GAS.config["globalProxy"]+"/driver/delete?_dc="+new Date().getTime(); //删除接口
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					items  : this.getMainPanel()
				});
				GAS.VehicleDriverList.superclass.constructor.apply(this, arguments);
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "fit",
						title  : "驾驶员信息列表",
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
				 	fields   : ["driverName", "driverage","driverSex", "driverAddress", "driverLeval", "education","driverHeight","deviceId","terName","deptId","deptName","remark"],
				 	root     : 'result', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.driverListUrl
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
				header : "司机姓名",
				dataIndex : "driverName",
				flex : 1
			},
			{
				header : "部门名称",
				dataIndex : "deptName",
				width :80
			},{
				header : "车辆牌号",
				dataIndex : "deptName",
				sortable:true,
				flex : 1
			},
			{
				header : "驾驶年龄",
				dataIndex : "driverage",
				sortable:true,
				flex : 1
			}, {
				header : "性别",
				dataIndex : "driverSex",
				sortable:true,
				flex : 1
			}, 
			{
				header : "驾驶员地址",
				dataIndex : "driverAddress",
				sortable:true,
				flex : 1
			}, 
			{
				header : "身高",
				dataIndex : "driverAddress",
				sortable:true,
				flex : 1
			}, 
			{
				header : "驾照等级",
				dataIndex : "driverAddress",
				sortable:true,
				flex : 1
			}, 
			{
				header : "文化程度",
				dataIndex : "driverAddress",
				sortable:true,
				flex : 1
			}, {
				header : "备注信息",
				dataIndex : "remark",
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
				this.driverName = new Ext.form.TextField({
					fieldLabel : "驾驶员姓名",
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
					items : [
						"驾驶员姓名", this.driverName, 
						"部门名称:",  this.deptName, 
						"车辆牌号:",  this.vehicleName, querybtn,"->"
					].concat(this.getToorBtn())
				});
			}
			return this.gridtoolBar;
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
		},
		//添加修改公共方法
		doCommonHandler:function(config){
		    var jsUrl = this.userManagerUrl;
	        GAS.importlib(jsUrl,function(){
	        	var window = new GAS.UserManager(config);
	        	window.show();
	        	/**
	        	 * 监听 submit 提交参数 就刷新当前的列表
	        	 */
	        	window.on({
	        	   scope :this,
	        	   submit: function(){
	        	       this.refreshGrid();
	        	   }
	        	});
	        },this);
		},
		//添加
		toAdd :function(){
		    this.doCommonHandler({
		         action  : "insert"
		    });
		},
		//修改数据
		update :function(){
	        var list = this.getGridPanel().getSelectionModel().getSelections();
			if (list.length==1) {
				 var data = list[0].data;
				 this.doCommonHandler({
			         action  : "update",
			         loadRecord : data
			     });
			} else {
				 Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		//删除记录
		doremove:function(){
			var list = this.getGridPanel().getSelectionModel().getSelections(), selectList = [];
			if (list.length > 0) {
				for (var i = 0, len = list.length; i < len; i++) {
					var record = list[i], id = record.get("id");
					selectList.push("ids="+id);
				}
				GAS.AOP.send({
					url  : this.deleteUrl, // 加载模块
					params : selectList.join("&"), // 操作方法, // 操作方法
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
			} else {
				 Ext.Msg.alert('提示信息', '请选择一条记录');
			}
		},
		//工具栏
		getToorBtn:function(){
		   return [{
			   name:"insert",
			   text:"添加",
			   scope:this,
			   iconCls:"icon-add",
			   handler:this.toAdd
		   },{
		       name:"insert",
			   text:"修改",
			   scope:this,
			   iconCls:"icon-update",
			   handler:this.update
		   },{
		   	   name:"insert",
			   text:"删除",
			   scope:this,
			   iconCls:"icon-remove",
			   handler:this.doremove
		   }]
		}
	});
	
})();