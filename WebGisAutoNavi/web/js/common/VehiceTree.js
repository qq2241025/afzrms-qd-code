(function(){
	/**
	 * 通用的树形车辆菜单
	 */
	GAS.VehiceTreePanel= Ext.extend(Ext.Panel,{
	       constructor : function(config) {
	      	    config = config || {};
	      	    this.moduleTreeUrl = GAS.config["globalProxy"]+"/vehicleGroup/treeTerminal?_dc="+new Date().getTime(); //树形菜单
	      	    this.treeParamsData = config["TreeParams"] || {};
	      	    this.hideAllChecked = config["hideAllChecked"] || false;
	      	    this.showRootChecked = config["showRootChecked"] || false;
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					title  : "车辆列表",
					items  : this.getMainPanel()
	      	    });
				GAS.VehiceTreePanel.superclass.constructor.apply(this, arguments);
		   },
		   getSelectTreeNode:function(){
		       var checked = this.getMainTreePanel().getChecked(),list =[];
		   	   if(checked && checked.length> 0){
		   	        for (var i = 0; i < checked.length; i++) {
		   	        	var treenode = checked[i];
		   	        	if(treenode.isLeaf()){
		   	        	    var node = treenode.attributes;
		   	        		list.push(node);
		   	        	}
		   	        }
		   	   }
		   	   return list;
		   },
		   //主页面
		   getMainPanel:function(){
			    if(!this.mainPanel){
			        this.mainPanel = new Ext.Panel({
		           	     frame  : false,
		           	     border : false,
		           	     layout: "fit",
		           	     tbar  : this.getSearchBar(),
		           	     items : this.getMainTreePanel()
			        });
			    }
			    return this.mainPanel;
		   },
		   //主要数据菜单
		   getMainTreePanel:function(){
		        if(!this.treePanel){
			        this.treePanel = new GAS.baseTreePanel({
		           	     dataRoot : "result",
		           	     showChecked: true, //默认有复选框
		           	     hideAllChecked  : this.hideAllChecked,
		           	     showRootChecked : this.showRootChecked,
		           	     leafIcon : GAS.config["veHicleIcon"],
		           	     dataUrl  : this.moduleTreeUrl,
		           	     baseParams : this.treeParamsData
			        });
			    }
			    return this.treePanel;
		   },
		   //设置节点图标问题
		   setNodeIcon:function(targetId,iconImage){
		      var node = this.getMainTreePanel().getNodeById(targetId);
		      if(node){
		      	  node.setIcon(iconImage);
		      }
		   },
		    //获取节点对象
		   getNodeByTargetId:function(targetId){
		      var node = this.getMainTreePanel().findNodeByTargetId(targetId);
		      return node;
		   },
		    //获取节点对象
		   setNodeTextColor:function(targetId,color){
		      var node = this.getMainTreePanel().getNodeById(targetId);
		      if(node && node.ui){
		      	  var nodeDOm = node.ui.textNode;
		      	  nodeDOm.style.color = color;
		      	  nodeDOm.style.fontWeight= 'bold';
		      }
		   },
		   //输入查询条件
		   doQueryField:function(searchFiled){
		   	    this.searchTxt = searchFiled.el.dom.value; //搜索查询的数据值
		   	    var rootTreeNode = this.getMainTreePanel().getRootNode();
		   	    if(!Ext.isEmpty(this.searchTxt)){ //不为空
		   	    	 this.searchTreeData(); //查询数据
		   	    }else{
		   	    	 this.getMainTreePanel().reloadLocalTree();
		   	    }
		   },
		   //查询模糊查询的结果
		   searchTreeData:function(){
		        var condText = this.searchTxt; //查询条件内容
                var reslist = []; //查询结果列表
                var alllist = this.getMainTreePanel().getAllTreeNodeData(); //所有的属性节点的数据
                if(alllist && alllist.length > 0 ){
                	Ext.each(alllist,function(record,index){
                	    var targetId = record["id"], 
	                	    textMsg = record["text"],
	                	    isLeaf = record["leaf"];
            	        if(textMsg.indexOf(condText) >= 0 && isLeaf ){
                    		var treeNode = new Ext.tree.TreeNode(record)
                    		reslist.push(treeNode);
                    	}
                	},this);
                }
                if(reslist.length>0){
                	var rootTreeNode = this.getMainTreePanel().getRootNode();
                    rootTreeNode.removeAll();
                	rootTreeNode.appendChild(reslist);
                	rootTreeNode.expand();
                }
		   },
		   //工具栏
		   getSearchBar:function(){
		      if(!this.searchBus){
		      	  var me = this;
		      	  var  handlerCall = function(e,dom){
		      	      me.doQueryField.call(me,this);
		      	  };
		      	  this.searchFiled = new Ext.form.SearchField({
		      	      store : [],
		      	      width : 140,
		      	      emptyText : "车牌号",
		      	      scope : this,
		      	      onTrigger1Click : handlerCall,
		      	      onTrigger2Click : handlerCall
		      	  });
		          this.searchBus  = new Ext.Toolbar({
		              items : ["车牌号:", this.searchFiled]
		          });
		      }
		      return this.searchBus;
		   }
	});
	
})();



