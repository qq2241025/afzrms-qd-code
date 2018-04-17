(function(){
	//组织查询列表
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.DeptQuery = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.pageSize = 20;
	      	    this.rootDeptId = 1; //部门根节点
	      	    this.deptListUrl = GAS.config["globalProxy"]+"/dept/list?_dc="+new Date().getTime(); //查询接口
	      	    this.deleteUrl  =  GAS.config["globalProxy"]+"/dept/delete?_dc="+new Date().getTime(); //删除接口
	      	    this.deptManagerUrl = "js/module/system/group/DeptManager.js"; //加载的URL地址
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					title  : "组织结构列表",
					items  : this.getMainPanel()
	      	    });
	      	    this.addEvents("doSubmit");
				GAS.DeptQuery.superclass.constructor.apply(this, arguments);
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
		   //右键菜单
		   getGridRightMenu : function(){
		      var rightClick = new Ext.menu.Menu({ 
				    items: [{ 
				        iconCls:'icon-add',
			            text: '添加数据',
			            scope :this,
			            handler : function(e){
			                this.toAdd();
			            }
			        },{ 
			            text: '修改数据',
			            iconCls:'icon-update',
			            scope :this,
			            handler:function(e){
			               this.update();
			            }
			        },{ 
			            text: '删除数据',
			            scope :this,
			            iconCls:'icon-delete',
			            handler:function(e){
			               this.doremove();
			            }
			        }] 
				}); 
		       return rightClick;
		   },
		   //双击修改
		   onDblClickHandler : function(grid,rowindex,e){
		   	     var record  = this.getGridStore().getAt(rowindex);
		   	     var recordData = record.data;
		         this.doCommonHandler({
			         action  : "update",
			         loadRecord : recordData
			     });
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
						forceFit : true,
						columnLines: true
					},
					store     : this.getGridStore(),
					columns   : this.getGridColumn(),
					bbar      : this.getGridPageBar(),
					sm        : this.getCheckboxModel(),
					tbar      : this.getGridToolBar(),
					listeners: {
					   scope : this,
					   rowcontextmenu:function(grid,rowindex,e){
					      	e.preventDefault(); 
    						this.getGridRightMenu().showAt(e.getXY()); 
					   },
					   //双击修改
					   rowdblclick: this.onDblClickHandler
					}
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
							[10, "10"], 
							[20, "20"], 
							[30, "30"]
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
				 	fields   : ["id","parentId","duty","remark","name","director","sortNum","parentName"],
				 	root     : 'data', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.deptListUrl
					}),
		            listeners : {
						scope : this,
						beforeload:function(store,record){
							var params = {
						        start  : 0,
							    limit  : this.pageSize,
							    parentId :this.getDeptRootId()
						    };
							Ext.apply(store.baseParams,params);
						}
					}
		     	 });
		     }
		 	 return this.gridstore;
		},
		getDeptRootId:function(){
		    return  this.rootDeptId;
		},
		setDeptRootId:function(deptId){
		    this.rootDeptId = deptId;
		},
		getGridColumn:function(){
		    var cls =  [
			   this.getCheckboxModel(),
			   this.getGridLineNum(),
			   {
			     header  : "id",
			     hidden  : true,
			     dataIndex: "id",
			     menuDisabled: false,
			     sortable:true,
			     flex :1
			   },
			   {
			     header:"部门名称",
			     dataIndex:"name",
			     sortable:true,
			     flex :1
			   },
			   {
			     header:"上级部门",
			     dataIndex:"parentName",
			     sortable:true,
			     flex :1
			   },
			   {
			     header:"部门职务",
			     dataIndex:"duty",
			     sortable:true,
			     flex :1
			   },{
			     header:"部门领导",
			     dataIndex:"director",
			     sortable:true,
			     flex :1
			   },{
			   	 header:"排列次序",
			     dataIndex:"sortNum",
			     sortable:true,
			     flex :1
			   },{
			   	 header:"信息备注",
			     dataIndex:"remark",
			     sortable:true,
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
					   this.deptname = new Ext.form.TextField({
					      fieldLabel: "部门名称",
					      name : "deptname",
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
							"部门名称:",this.deptname,
							querybtn,
							"->"
						].concat(this.getToorBtn())
				});
			}
			return this.gridtoolBar;
		},
		//查询
		doQuerySeach:function(){
		    var deptname = this.deptname.getValue();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,{
		          name : deptname
		    });
		    store.load();
		},
		//刷新
		refreshGrid : function(){
		    var store = this.getGridStore();
		    store.load();
		},
		//暴露点击树形菜单查看的接口
		onTreeClickhandler : function(parentId){
			this.setDeptRootId(parentId);
			var store  = this.getGridStore();
		    store.load();
		},
		//添加修改的公共接口
		doCommonHandler:function(config){
		    var jsUrl = this.deptManagerUrl;
	        GAS.importlib(jsUrl,function(){
	        	var window = new GAS.DeptManager(config);
	        	window.show();
	        	//提交数据完毕之后
	        	window.on({
	        	    scope  : this,
	        	    submit : function(){
	        	        this.refreshGrid(); 
	        	        this.fireEvent("doSubmit");
	        	    }
	        	});
	        },this);
		},
		//添加 操作
		toAdd :function(){
		    this.doCommonHandler({
		       action  : "insert"
		    });
		},
		//修改数据
		update :function(){
			var list = this.getGridPanel().getSelectionModel().getSelections();
			if (list.length==1) {
				 var record = list[0].data;
				 this.doCommonHandler({
			       	   action  : "update",
		        	   loadRecord : record
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
				params : list.join("&"), // 操作方法
				scope : this,
				callbackFn : function(res) {
					res = Ext.decode(res.responseText);
					if (res["success"] && res["success"]=="true") {
						this.fireEvent("doSubmit");
						this.refreshGrid(); //刷新
						Ext.Msg.alert('提示信息', res["result"] || res["msg"] || '成功删除数据');
					} else {
						Ext.Msg.alert('提示信息', res["result"] || res["msg"] || '删除数据失败');
					}
				}
			});
		},
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



