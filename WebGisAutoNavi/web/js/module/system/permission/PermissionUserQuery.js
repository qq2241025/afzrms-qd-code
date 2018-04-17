(function(){
	//企业管理模块
	GAS.PermissionUserQuery = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.pageSize = 20;
	      	    this.userListUrl = "data/system/user/userList.json";
	      	    this.deleteUrl   = "data/system/user/userdelete.json";
	      	    this.userManagerUrl = "js/module/system/user/UserManager.js"; //加载的URL地址
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					title  : "用户列表",
					items  : this.getMainPanel()
	      	    });
				GAS.PermissionUserQuery.superclass.constructor.apply(this, arguments);
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
					viewConfig: {
						forceFit : true,
						columnLines: true, 
             			enableColumnMove:true, 
                        enableColumnResize:true
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
				 	fields   : ["username","deptName","phone","email","worker","endserverTime","createTime","desc","id"],
				 	root     : 'data', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.userListUrl
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
			     header:"用户ID",
			     dataIndex:"id",
			     flex :1,
			     hidden : true
			   },{
			     header:"用户名",
			     dataIndex:"username",
			     flex :1
			   },{
			     header:"所属部门",
			     dataIndex:"deptName",
			     flex :1
			   },{
			   	 header:"创建时间",
			     dataIndex:"createTime",
			     flex :1
			   },{
			   	 header:"信息备注",
			     dataIndex:"desc",
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
				      this.userName = new Ext.form.TextField({
					      fieldLabel: "用户名称",
					      name : "username",
					      width : 100,
					      anchor : "92%"
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
							"用户名称:",this.userName,
							querybtn,
							"->"
						].concat(this.getToorBtn())
				});
			}
			return this.gridtoolBar;
		},
		//查询
		doQuerySeach:function(){
		    var username = this.userName.getValue();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,{
		          username : username
		    });
		    //store.load();
		    store.filterBy(function(record) {
		    	var flag = record.get('username') == username ; 
		    	if(Ext.isEmpty(username)  && Ext.isEmpty(username)){
		    	    flag = true;
		    	}
			    return  flag;
			});
		},
		//刷新
		refreshGrid : function(){
		    var store = this.getGridStore();
		    store.load();
		},
		//工具栏
		getToorBtn:function(){
		   return []
		}
	});
	
})();



