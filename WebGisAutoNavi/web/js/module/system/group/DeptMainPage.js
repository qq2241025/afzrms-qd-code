(function(){
	//组织机构主界面
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.deptMainPage = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	     config = config || {};
	      	     this.rootDeptId = 1; //部门根节点
	      	     this.deptListUrl = GAS.config["globalProxy"]+"/dept/tree?_dc="+new Date().getTime(); //树形菜单
	      	     this.deptQueryUrl= "js/module/system/group/DeptQuery.js"; //集团信息列表
	      	     Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					items  : this.getMainPanel()
	      	    });
				GAS.deptMainPage.superclass.constructor.apply(this, arguments);
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [{
								border : true,
								frame  : false,
								layout :"fit",
								width  : 200,
								margins: "1px",
								items  : this.getDicTreePanel(),
								region : "west"
							},{
								border : true,
								frame  : false,
								layout :"fit",
								margins: "1px",
								items  : this.getCenterPanel(),
								region :"center"
						}]
			        });
			    }
			   return  this.mianPanel;
		   },
		   //中间面板
		   getCenterPanel:function(){
		        if(!this.centerPanel){
	                this.centerPanel =new Ext.Panel({
	                	border : false,
						frame  : false,
						layout : "fit",
						width  : 200,
						margins: "1px",
						listeners:{
						    scope: this,
						    afterrender : function(){
						        this.showQueryGroupList(); //显示集团信息列表
						    }
						}
					});
		        }
		        return this.centerPanel
		   },
		   //集团列表树形菜单
		   getDicTreePanel:function(){
		       if(!this.dicTreePanel){
		           this.dicTreePanel= new  GAS.baseTreePanel({
		           	     dataRoot : "result",
		           	     title    : "组织机构列表",
		           	     dataUrl  : this.deptListUrl,
		           	     expandAllNode: true,
		           	     baseParams : {
		           	     	id : this.rootDeptId 
		           	     }
		           });
		       }
		       this.dicTreePanel.on({
           	        scope: this,
					click : this.doqueryGridHandler
		       });
		       return this.dicTreePanel;
		   },
		   //点击树形节点查询节点下面的数据
		   doqueryGridHandler:function(node){
		        var possbackParame = node.attributes["id"];
		        var isRoot = node.parentNode["isRoot"];
		        if(isRoot){
		             possbackParame = this.rootDeptId;
		        }
		        if(this.deptQuery){
		            this.deptQuery.onTreeClickhandler(possbackParame); 
		        }
		   },
		   //显示集团查询信息列表
		   showQueryGroupList:function(node){
		        var jsUrl = this.deptQueryUrl;
		        GAS.importlib(jsUrl,function(){
		        	this.deptQuery = new GAS.DeptQuery({});
		        	this.deptQuery.on({
		        	   scope : this,
		        	   doSubmit :function(){
		        	        this.dicTreePanel.reloadTree();//树形数据菜单
		        	   }
		        	});
		            this.addNewModulePanel(this.deptQuery);
		        },this);
		   },
		   //显示新的功能模块
		   addNewModulePanel:function(newModule){
	            this.getCenterPanel().removeAll();
	            this.getCenterPanel().add(newModule);
	            this.getCenterPanel().doLayout();
		   }
	});
	
})();
