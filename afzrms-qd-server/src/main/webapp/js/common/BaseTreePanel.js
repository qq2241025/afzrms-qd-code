(function(){
	
	//基础的模块树形模块
	GAS.baseTreePanel = Ext.extend(Ext.tree.TreePanel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.dataUrl= config["dataUrl"];
	      	    this.debugable = false;
	      	    this.leafIcon = config["leafIcon"] ||  GAS.config["leafIcon"];
	      	    if(!config["dataUrl"]){
	      	    	throw new Error("dataUrl 不能为空");
	      	    	return ;
	      	    }
	      	    this.dataRoot        = config["dataRoot"] || "result";
	      	    this.textField       = config["textField"] || "text";
	      	    this.valueField      = config["valueField"] || "id";
	      	    this.showTreeChecked = config["showChecked"] || false;
	      	    this.baseTreeParames = config["baseParams"] || {};　//请求参数
	      	    this.nodeParameter   = config["paramsName"] || "id";　//请求参数名称
	      	    this.autoExpand      = config["autoExpand"] || true;　//是否展开
	      	    this.hideAllChecked  = config["hideAllChecked"] || false;　//是否所有的显示check
	      	    this.expandAllNode   = config["expandAllNode"] || false;　//是否展开
	      	    this.showRootChecked   = config["showRootChecked"] || false;　//是否展开
	      	    this.treeNodeHashMap = {};
	      	    var listeners = {
					 scope: this,
					 render :this.loadAjaxTree
	      	    }
	      	    if(this.showTreeChecked){
	      	        listeners["checkchange"]=function(node,checked){
	                 	node.expand();
					    node.attributes.checked = checked;
					    node.on('expand',function(node){
							node.eachChild(function(child){
						    	child.ui.toggleCheck(checked);
						        child.attributes.checked = checked;
						        child.fireEvent('checkchange',child,checked);
						    });
						});
					    node.eachChild(function(child){
					    	child.ui.toggleCheck(checked);
					        child.attributes.checked = checked;
					        child.fireEvent('checkchange',child,checked);
					    });
		            }
	      	    }
	      	    Ext.apply(this,{
	      	    	border : false,
					frame  : false,
					layout : "fit",
					useArrows : false,
					rootVisible : true,
					autoScroll : true,
					containerScroll : true,
					root :   this.getRootNode(),
					listeners:listeners
				});
	      	    this.addEvents("treeloadend");
				GAS.baseTreePanel.superclass.constructor.apply(this, arguments);
		   },
		   //刷新树形节点 || 重新加载树形菜单
		   reloadTree:function(){
		   	   this.getRootNode().removeAll();
		   	   this.getRootNode().expand();
		   	   this.loadAjaxTree();
		   },
		   //重新加载本地树形菜单
		   reloadLocalTree:function(){
		        this.getRootNode().removeAll();
		   	    this.getRootNode().expand();
		   	    this.loadTreeData();
		   },
		   getLoadMask : function() {
				if (!this.loadMask) {
					var bodyEL = this.findParentByType("panel");
					if(bodyEL){
						this.loadMask = new Ext.LoadMask(bodyEL.getEl(), {
							msg : "正在加载中..."
						});
					}
				}
				return this.loadMask;
		   },
		   getRootNode :function(){
				  if(!this.rootNode){
				  	  var cfg= {
							id: GAS.config["rootNodeId"],
							expanded : true,
							text : GAS.config["rootNodeText"]
					  };
				  	  if(this.showRootChecked){
				  	      cfg["checked"] = false;
				  	  }
				      this.rootNode  =  new Ext.tree.TreeNode(cfg) ;
				  }
				  return this.rootNode;
			},
			getLoadBaseParame :function(){
			    return this.baseTreeParames;
			},
			//ajax 请求树形菜单
			loadAjaxTree:function(){
			    var  callback = function(res,req){
			    	this.getLoadMask().hide();
	      	        res = Ext.decode(res.responseText) || {};
	      	        var data = res[this.dataRoot];
	      	        if(data && Ext.isArray(data) && data.length > 0 ){
	      	             this.dataList = [];
						 Ext.each(data,function(record){
						       this.dataList.push(record) ;
						 },this);
					     this.loadTreeData();
	      	        }
	      	    }
	      	    this.getLoadMask().show();
	      	    Ext.Ajax.request({
	      	        scope  : this,
	      	        method : "POST",
	      	        url    : this.dataUrl,
	      	        params : this.getLoadBaseParame(),
	      	        success : callback,
	      	        failure : callback
	      	    });
			},
			//地递归发遍历
			eachTreeNode : function(nodeList) {
				var arraylist = [];
				if (Ext.isArray(nodeList) && nodeList.length > 0) {
					for (var index = 0; index < nodeList.length; index++) {
						var data = nodeList[index];
						data["id"]   = data[this.valueField];
						data["text"] = data[this.textField];
						data["name"] = data[this.textField];
						//配置是否显示复选框
						if(this.showTreeChecked){
						    data["checked"] = !this.showTreeChecked;
						} 
						//配置所有的不显示复选框
						if(this.hideAllChecked){
						   delete data["checked"];
						}
						var child  = data["children"],id = data[this.valueField];
						//hasBindAreaAlarm
						if(!GAS.isEmpty(data["hasBindAreaAlarm"]) ){
							if(data["hasBindAreaAlarm"] ==true){
							    data["text"] = "<span style='color:blue;font-weight:bold;'>"+data["text"]+"</span>";
							}
						}
						if(data["color"]){
						   var color = data["color"] ? "#"+data["color"]:"#000000";
						   var title = data["vehicleTypeName"] || "";
						   data["text"] = "<span style='color:"+color+";font-weight:bold;' title='"+title+"'>"+data["text"]+"</span>";
						}
						var treeNode =new Ext.tree.TreeNode(data);
						this.treeNodeHashMap[id] = treeNode;
						if(child && child.length > 0){
						     //data["leaf"] = false;
						     var newList = this.eachTreeNode(child);
						     treeNode.appendChild(newList);
						}else{
							 data["icon"] = this.leafIcon; //自定义叶子节点的图标
							 //["leaf"] = true;
						}
						if(data["leaf"]== false &&  data["children"].length==0 ){
						    data["icon"] = GAS.config["folderIcon"];
						}
						arraylist.push(treeNode);
					}
					return arraylist;
				}
			},
			//获取原始数据
			getDatalist:function(){
			    return this.dataList;
			},
			//获取树形菜单的map对象
			getTreeNodeList: function(){
			    return this.treeNodeHashMap;
			},
			//获取树形菜单的列表集合
			getAllTreeNodeData:function(){
				var list = [],allMap = this.getTreeNodeList() ;
			    for (var index in allMap) {
                	var record  = allMap[index];
                	list.push(record.attributes);
                }
                return list;
			},
			//根据ID获取节点的方法
			findNodeByTargetId:function(id){
			   var node = this.treeNodeHashMap[id];
			   return node;
			},
			//获取所有的属性节点列表
			getAllTreeNodeList:function(){
			   return this.treeNodeList;
			},
			//展开一级
			expandFirstLevel:function(){
				this.getRootNode().expand();
			},
			expandAllLevel:function(){
			    var nodeArray = this.getRootNode().childNodes;
				Ext.each(nodeArray,function(node){
				   if(node){
				      node.expand();
				   }
				},this);
			},
			loadTreeData : function(){
				var data = this.getDatalist();
				var treeNodeList = this.eachTreeNode(data);
				this.treeNodeList = treeNodeList;
				//递归树形菜单
				if(this.debugable){
					var startDate = new Date().getTime();
					var endDate = new Date().getTime();
					GAS.log("递归树形菜单共消耗时间:"+(endDate - startDate));
				}
				this.getRootNode().appendChild(treeNodeList);
				//配置自动展开
				if(this.autoExpand){
					//默认之展开一级
					this.expandFirstLevel();
				}
				//展开所有的节点
				if(this.expandAllNode){
				    this.expandAllLevel(); 
				}
				this.fireEvent("treeloadend",this,this.getRootNode(),this.treeNodeHashMap);
			}
	});
	
})();
