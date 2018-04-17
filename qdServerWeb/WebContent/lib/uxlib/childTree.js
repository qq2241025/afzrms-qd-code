(function(){
	
	/**
	 * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 * eg : var  tree = new Ext.StoreTreePanel({
			  border : true,
			  frame  : false,
			  width  : 230,
			  height : 400,
			  title  : "测试title",
			  renderTo: Ext.getBody(),
			  dataUrl : "list.json",
			  dataRoot: "data",
			  rootVisible : true,
			  showChecked : true
		});
		// get node and set checked  == true
		tree.on({
		   scope :this,
		   treeloadend:function(){
		         var node = tree.findNodeByTargetId(1002);
		         console.log(node);
				 node.ui.checkbox.checked=true; 
	             node.attributes.checked=true;  
		   }
		});
	 */

	Ext.AjaxTreePanel = Ext.extend(Ext.tree.TreePanel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.dataUrl  = config["dataUrl"];
	      	    if(!config["dataUrl"]){
	      	    	throw new Error("dataUrl 不能为空");
	      	    }
	      	    this.rootNodeId      = config["rootNodeId"] || "id";;
	      	    this.rootNodeText    = config["rootNodeText"] || "root";
	      	    
	      	    this.textField       = config["textField"] || "text";
	      	    this.valueField      = config["valueField"] || "id";
	      	    this.dataRoot        = config["dataRoot"] || "result"; //获取数据的节点
	      	    
	      	    
	      	    this.baseTreeParames = config["baseParams"] || {};　//请求参数
	      	    this.rootVisible     = config["rootVisible"] || false;　//是否显示根节点
	      	    this.showTreeChecked = config["showChecked"] || false;　//是否显示复选框
	      	    this.autoExpand      = config["autoExpand"] || true;　//是否显示复选框
	      	    
	      	    this.treeNodeHashMap = {};
	      	    
	      	    this.addEvents("treeloadend");
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					layout　: "fit",
			        enableDD : true, 
			        containerScroll : true, 
			        rootVisible   : this.rootVisible,  
			        autoScroll    : true,
			        animate 　　　  : true, 
			        root    　　　       : this.getRootNode()
	      	    });
	      	    var  callback = function(res,req){
	      	        res = Ext.decode(res.responseText) || {};
	      	        var data = res[this.dataRoot];
	      	        if(data && Ext.isArray(data) && data.length > 0 ){
	      	             this.dataList = [];
						 Ext.each(data,function(record){
						       this.dataList.push(record) ;
						 },this);
						 console.log(this.dataList);
					     this.loadTreeData();
	      	        }
	      	    }
	      	    Ext.Ajax.request({
	      	        scope  : this,
	      	        method : "POST",
	      	        url    : this.dataUrl,
	      	        params : this.getBaseParams(),
	      	        success : callback,
	      	        failure : callback
	      	    });
				Ext.StoreTreePanel.superclass.constructor.apply(this, arguments);
		   },
		   //刷新树形节点 || 重新加载树形菜单
		   reloadTree:function(){
		   	   this.getRootNode().reload();
		   },
		   getRootNode :function(){
				  if(!this.rootNode){
				        this.rootNode  =  new Ext.tree.TreeNode({
							id:  this.rootNodeId,
							expanded : false,
							text : this.rootNodeText
						}) ;
				  }
				  return this.rootNode;
			},
			//设置请求参数
			setBaseParams:function(params){
			     this.baseTreeParames = params || {};
			},
			//获取请求参数
			getBaseParams : function(){
			    return this.baseTreeParames;
			},
			//地递归发遍历
			eachTreeNode : function(nodeList) {
				var arraylist = [];
				if (Ext.isArray(nodeList) && nodeList.length > 0) {
					for (var index = 0; index < nodeList.length; index++) {
						var data = nodeList[index];
						data["id"]   = data[this.valueField];
						data["text"] = data[this.textField];
						//配置是否显示复选框
						if(this.showTreeChecked){
						    data["checked"] = !this.showTreeChecked;
						} 
						var child  = data["children"],id = node[this.valueField];
						var treeNode =new Ext.tree.TreeNode(data);
						if(child && child.length > 0){
						     data["leaf"] = false;
						     var newList = this.eachTreeNode(child);
						     treeNode.appendChild(newList);
						}else{
							 data["leaf"] = true;
						}
						arraylist.push(treeNode);
					}
					return arraylist;
				}
			},
			getDatalist:function(){
			    return this.dataList;
			},
			getTreeNodeList: function(){
			    return this.treeNodeHashMap;
			},
			findNodeByTargetId:function(target){
			   return this.treeNodeHashMap[target];
			},
			loadTreeData : function(){
				var data = this.getDatalist();
				//递归树形菜单
				var startDate = new Date().getTime();
				var treeNodeList = this.eachTreeNode(data);
				var endDate = new Date().getTime();
				//console.log("递归树形菜单共消耗时间:"+(endDate - startDate));
				this.getRootNode().appendChild(treeNodeList);
				console.log(this.treeNodeHashMap);
				//配置自动展开
				if(this.autoExpand){
					 this.getRootNode().expand();
				}
				this.fireEvent("treeloadend",this,this.getRootNode(),treeNodeList);
			}
	});
	
})();
