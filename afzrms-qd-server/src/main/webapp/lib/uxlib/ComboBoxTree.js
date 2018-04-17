(function() {
	/**
	 * var treelist = new  Ext.form.ComboBoxTree({
			triggerAction : "all",
			fieldLabel : "测试数据",
			displayField  : "text",
			valueField    : "id",
			autoLoad      : true,
			dataRoot      : "result",
			dataUrl       : "data/org/orgTreeList.json"
	});
	 */
	Ext.form.ComboBoxTree = Ext.extend(Ext.form.ComboBox, {
		triggerAction : "all",
		mode		  : "local",
		editable	  : false,
		emptyText	  : '请选择...',
		 title        : "下拉树形菜单",
		store: new Ext.data.SimpleStore({ 
			fields: [], 
			data: [[]] 
		}),
		trigger1Class:'x-form-clear-trigger',
	    trigger2Class:'x-form-arrow-trigger',
	    showClearBtn : false,
		constructor : function(config){
			config = config || {};
			if(!config["dataUrl"]){
      	    	throw new Error("dataUrl 不能为空");
      	    	return ;
      	    }
      	    
		    this.dataUrl         = config["dataUrl"] ;
			this.baseTreeParames = config["baseParams"] || {};　//请求参数
			this.nodeParameter   = config["paramsName"] || "id";　//请求参数名称
			this.rootNodeId      = config["rootNodeId"] ||  1; //默认为1
			this.rootNodeText    = config["rootNodeText"] || "下拉根节点"; //默认为1
			this.maxHeight       = config["maxHeight"] || 150;
			this.displayField    = config["displayField"] || "text";
	      	this.valueField      = config["valueField"] || "id";
			this.dataRoot        = config["dataRoot"] || "result";
			this.rootVisible     = config["rootVisible"] || false; //默认为false
			this.showClearBtn    = config["showClearBtn"] || false; //是否显示清除按钮
			this.autoExpand      = config["autoExpand"] || true;　//是否展开
	      	this.expandAllNode   = config["expandAllNode"] || false;　//是否展开
	      	
			this.treeNodeHashMap = {};
			
			this.tree = new Ext.tree.TreePanel({ 
                  border : false,
				  frame  : false,
				  layout : "fit",
				  useArrows : false,
				  autoScroll : true,
				  containerScroll : true,
                  root        : this.getRootNode(),  
				  rootVisible : this.rootVisible
            });
            this.loadAjaxTree(); //Ajax 请求数据
            
			this.tree.on({
			   scope : this,
			   click:function(node){
            	 this.fireEvent('select',this, node);
			   }
            });
            this.addEvents("loadend");
			Ext.form.ComboBoxTree.superclass.constructor.apply(this, arguments);
			var btn =[]; 
			if(this.showClearBtn){ //是否显示清除按钮
			   btn.push({tag: "img", src: Ext.BLANK_IMAGE_URL, alt: "清除", cls: "x-form-trigger " + this.trigger1Class});
			}
			btn.push({tag: "img", src: Ext.BLANK_IMAGE_URL, alt: "选择", cls: "x-form-trigger " + this.trigger2Class});
			
			this.triggerConfig = {tag:'span', cls:'x-form-twin-triggers', cn:btn};
		},
		//覆盖onViewClick 方法解决当点击空白处的时候自动收缩菜单的BUG
		onViewClick : function(doFocus){
	        var index = this.view.getSelectedIndexes()[0],
	            s = this.store,
	            r = s.getAt(index);
	        if(r){
	            this.onSelect(r, index);
	        }
	        if(doFocus !== false){
	            this.el.focus();
	        }
	    },
		//
		initComponent : function(ct, position) { 
            this.divId = 'tree-' + Ext.id();  
            this.tpl= '<tpl>' + '<div style="height:' + this.maxHeight + 'px;"  id="' + this.divId + '"></div></tpl>';
            this.on("expand", function() {
               if (!this.tree.rendered) {
                    this.tree.render(this.divId); 
                   	 //配置自动展开
					if(this.autoExpand){
						//默认之展开一级
						var nodeArray = this.getRootNode().childNodes;
						Ext.each(nodeArray,function(node){
						   if(node){
						      node.expand();
						   }
						},this);
					}
					//展开所有的节点
					if(this.expandAllNode){
					    this.expandAll(); //性能较差
					}
               }  
            }, this);
            this.on({
            	scope :this,
                select :function (panel, node) { 
					var dispText = node[this.displayField]; 
					var code = node[this.valueField]; 
					this.setHiddenValue(code, dispText);
					this.collapse(); 
	             }
			}); 
			Ext.form.ComboBoxTree.superclass.initComponent.call(this);
        },  
        initTrigger : function(){
	        var triggers = this.trigger.select('.x-form-trigger', true);
	        triggers.each(function(trigger, all, index){
	              if(trigger && trigger.hasClass(this.trigger2Class)){
	                   this.searchBtn =   trigger;
	                   //下拉按钮
				        this.searchBtn.on({
				            scope : this,
				            click :  this.onTriggerClick
				        });
	              }
	              if(trigger && trigger.hasClass(this.trigger1Class)){
	                    this.clearBtn =   trigger;
	                    //清除按钮
				        this.clearBtn.on({
				            scope : this,
				            click :  function(){
				               this.setHiddenValue("","");
				               this.collapse();
				            }
				        });
	              }
	        }, this);
	        this.trigger.addClassOnOver('x-form-trigger-over');
	        this.trigger.addClassOnClick('x-form-trigger-click');
	    },
	    getRootNode:function(){
	       if(!this.rootNode){
	           this.rootNode =  new Ext.tree.TreeNode({
					text	: this.rootNodeText,
					id		: this.rootNodeId,
					expanded: true
				});
	       }
	       return this.rootNode;
	    },
	    setHiddenValue: function (value, dispText) { 
	    	 this.setValue(value); 
			 Ext.form.ComboBoxTree.superclass.setValue.call(this, dispText); 
			 this.hiddenValue = value;
			 this.on({
				scope :this,
			    loadend:function(){
					 var node = this.findNodeByTargetId(value);
			    	 if(node){
			    	 	  node.setText("<span style='color:red'>"+dispText+"</span>");
			    	 }
			   }
			});
		}, 
		setHideValue: function (value, dispText) { 
			this.hiddenValue = value; 
		}, 
		getHiddenValue: function () { 
			return this.hiddenValue; 
		}, 
		getValue: function () { //增加适用性，与原来combo组件一样 
			return this.hiddenValue; 
		}, 
		getValue: function() {
			return typeof this.value != 'undefined' ? this.value : '';
		},
		getLoadBaseParame :function(){
		    return this.baseTreeParames;
		},
		//ajax 请求树形菜单
		loadAjaxTree:function(){
		    var  callback = function(res,req){
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
					data["text"] = data[this.displayField];
					var child  = data["children"],id = data[this.valueField];
					var treeNode =new Ext.tree.TreeNode(data);
					this.treeNodeHashMap[id] = treeNode;
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
		//获取原始数据
		getDatalist:function(){
		    return this.dataList;
		},
		//获取树形菜单的节点对象
		getTreeNodeList: function(){
		    return this.treeNodeHashMap;
		},
		//根据ID获取节点的方法
		findNodeByTargetId:function(id){
		   var node = this.treeNodeHashMap[id];
		   return node;
		},
		loadTreeData : function(){
			var data = this.getDatalist();
			//递归树形菜单
			var startDate = new Date().getTime();
			var treeNodeList = this.eachTreeNode(data);
			var endDate = new Date().getTime();
			this.getRootNode().appendChild(treeNodeList);
			this.fireEvent("loadend",this,this.getRootNode(),this.treeNodeHashMap);
		}
	});
			
	
	
})();
