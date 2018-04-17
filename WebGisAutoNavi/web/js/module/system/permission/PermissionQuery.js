(function(){
	//企业管理模块
	GAS.PermissionQuery = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config = config || {};
	      	    this.pageSize = 20;
				this.VehicleTreeUrl = "data/vehiceTree.json";
	      	    Ext.apply(this,{
	      	        border : false,
					frame  : false,
					layout : "fit",
					title  : "车辆权限列表",
					items  : this.getMainPanel()
	      	    });
				GAS.PermissionQuery.superclass.constructor.apply(this, arguments);
		   },
		   getSelectTreeNode:function(){
		       var checked = this.getMainTreePanel().getChecked(),list =[];
		   	   if(checked && checked.length> 0){
		   	        for (var i = 0; i < checked.length; i++) {
		   	        	var node = checked[i].attributes;
		   	        	list.push(node);
		   	        }
		   	   }
		   	   return list;
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "fit",
						items  : this.getVehicleTreePanel(),
						tbar      : this.getToolBar()
			        });
			    }
			   return  this.mianPanel;
		},
		//工具栏
		getToolBar : function() {
			if (!this.gridtoolBar) {
				 this.gridtoolBar = new Ext.Toolbar({
					enableOverflow: true, // 如果tbar溢出自动显示下三角
					items: [
							"->"
						].concat(this.getToorBtn())
				});
			}
			return this.gridtoolBar;
		},
		//工具栏
		getToorBtn:function(){
		   return [{
		   	   name:"insert",
			   text:"保存",
			   scope:this,
			   iconCls:"icon-save",
			   handler:this.dosave
		   }]
		},
		   //查询的数据面板
		   getVehicleTreePanel:function(){
		        if(!this.vehicleTree){
					this.vehicleTree = new GAS.baseTreePanel({
		           	     dataRoot : "result",
		           	     dataUrl  : this.VehicleTreeUrl
			        });
			    }
			    return this.vehicleTree;
		   }
	});
	
})();



