(function(){
	//车辆报修查询
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.MapMarkers = Ext.extend(Ext.Panel,{
	      constructor : function(config) {
	      	    config =config || {};
	      	    this.pageSize = 20;
		     	this.statListUrl = GAS.config["globalProxy"]+"/marker/list?_dc="+new Date().getTime(); //规则
		     	this.statdeleteUrl = GAS.config["globalProxy"]+"/marker/delete?_dc="+new Date().getTime(); //规则
	      	    this.markerUrl = "js/module/lastTrack/MarkerManager.js"; //加载的URL地址
	      	    this.trackIcon = GAS.iconPath + "flager.gif";
		     	this.listMarkerPoints = {} ; //所有的点覆盖物的集合
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					items  : this.getMainPanel()
				});
				GAS.MapMarkers.superclass.constructor.apply(this, arguments);
		   },
		    //覆盖主面板
		   getMainPanel: function() {
				if (!this.mainpanel) {
					 this.mainpanel = new Ext.Panel({
						border : false,
						frame  : false,
						layout : "border",
						items  : [{ 
						             border : true,
									 frame  : false,
									 layout : "fit",
									 region : "west",
									 width  : 450,
									 margins: "1px",
									 split : true,
								     items : this.getLeftPanel()
							},{
								border : true,
								frame  : false,
								layout : "fit",
								items  : this.getMapPanel(),
								margins: "1px",
								region : "center"
						}]
					});
				}
			    return this.mainpanel;
		   },
		   getMapPanel :function(){
		       if(!this.mapPanel){
			       this.mapPanel = new GAS.BaseMap({
			           title : "青岛地图",
			           hideTools: true
			       });
			    }
			    return  this.mapPanel;
		   },
		   //主面板
		   getLeftPanel:function(){
			    if(!this.leftPanel){
			        this.leftPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "fit",
						title  : "地图标注",
						items  : this.getGridPanel()
			        });
			    }
			   return  this.leftPanel;
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
					tbar      : this.getGridToolBar(),
					listeners: {
					   scope : this,
					   //双击修改
					   rowclick: this.onRowClickHandler
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
		//行点击事件
		onRowClickHandler:function(grid,rowIndex,a,b){
		   var record = grid.getStore().getAt(rowIndex); //Get the Record 
		   var targetId = record["id"];
		   this.moveCarToCenterAndTip(targetId);
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
				 	fields   : ["id","name","x","y","remarker","createTime","userId"],
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
						},
						load:function(store,data){
							if(data && data.length >0 ){
							    this.showResultOnMap(data);
							}
						}
					}
		     	 });
		     }
		 	 return this.gridstore;
		},
		 //多个marke居中
		 setFitView:function(){
		      var list = this.listMarkerPoints;
		      //只有一个点的情况
		       if(list){
		      	  var listPoints = [];
		      	  for (var i in list) {
		      	  	 var marker = list[i];
		      	  	 if(marker){
		      	  	 	var markerlnglat = marker.getPosition();
		      	  	 	listPoints.push(markerlnglat);
		      	  	 }
		      	  }
		      	  if(listPoints.length >0){
		      	  	  var polyline = this.getMapPanel().createPolyLine({points:listPoints});
		      	 	  this.getMapPanel().polylineFitInView(polyline);
		      	  }
		      }
		},
		showResultOnMap: function(list){
	        //显示车辆的位置信息
	        if(list && list.length > 0 ){
	               this.clearAllMap();
	               for (var i = 0; i < list.length; i++) {
	               	       var record = list[i].data;
	               	       var deviceId = record["id"];
		            	   var lng = record["x"],lat = record["y"];
		            	   var termName  =  record["name"];//车牌号
		            	   var createTime  =  record["createTime"];//车牌号
		            	   var remark  =  record["remarker"] || "";//车牌号
		            	   var point = this.getMapPanel().createPoint(parseFloat(lng),parseFloat(lat));
		            	   var contentText = [
							'<b>标注名称: </b> ' + termName,
							'<br><b>标注时间: </b> ' + createTime,
							'<br><b>备注信息: </b> ' + remark
							]
							var cfg = {
								 lnglat      : point,
            	 				 iconSize : [16,28],
		            	 		 iconAnchor:[9,28],
				            	 iconUrl     : this.trackIcon,
				            	 clickEnable : true,
				            	 width       : 230,
				            	 infoTop : 20,
				            	 msg  : contentText.join("")
							 }
							 var marker = this.getMapPanel().addMarker(cfg);
							 marker["config"] = cfg;
							 this.listMarkerPoints[deviceId] = marker ;
	               }
	               this.setFitView(); //居中显示
	        }
		},
		//清除所有的车辆覆盖物
		clearAllMap:function(){
		   	  this.getMapPanel().hideMapInfoWindow();  //隐藏气泡
		      var maplist = this.listMarkerPoints;
		      if(maplist){
		          for (var i in maplist) {
		          	  var marker = maplist[i];
		          	  if(marker){
		          	  	 this.getMapPanel().removeOverLayer(marker); //调用地图删除覆盖物的方法
		          	  }
		          }
		          this.listMarkerPoints = {};  //清空所有的对象
		      }
		},
	  //根据目标ID居中显示
	   moveCarToCenterAndTip:function(targetId){
	       var marker = this.listMarkerPoints[targetId];
	       if(marker){
	       	    var cfg = marker["config"];
	            var postion = marker.getPosition();
	        	this.getMapPanel().showMapInfo(postion,"",cfg["msg"]);//
	        	var info = this.getMapPanel().getMapInfoWindow();
	   	    	info.setTargetId(targetId);
	   	    	if(cfg["infoTop"]){
			    	var infoTop = cfg["infoTop"];
			    	var pnode = info.getElement().parentNode;
			    	if(pnode){
			    		var top = parseFloat(pnode.style.top);
			    		pnode.style.top = (top-infoTop) + "px";
			    	}
			        
			    }
	       }
	   },
	   getGridColumn:function(){
		    var cls = [
		    this.getCheckboxModel(),
			this.getGridLineNum(), 
			{
				header : "地址名称",
				dataIndex : "name",
				flex : 1
			},
			{
				header : "经度",
				dataIndex : "x",
				hidden : true,
				sortable:true,
				flex : 1
			},
			{
				header : "纬度",
				dataIndex : "y",
				hidden : true,
				sortable:true,
				flex : 1
			},
			{
				header : "创建时间",
				dataIndex : "createTime",
				sortable:true,
				width : 120,
				flex : 1
			}, {
				header : "备注信息",
				dataIndex : "remarker",
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
		    params["poiName"]  = this.pointerName.getValue() ; 
		    return params;
	    },
		//grid工具栏
		getGridToolBar : function() {
			if (!this.gridtoolBar) {
				this.pointerName = new Ext.form.TextField({
					fieldLabel : "标注名称",
					width : 100,
					algin : "left",
					value  : "",
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
					items : [ "标注名称:", this.pointerName,querybtn,"->"].concat(this.getToorBtn())
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
		doCommonHandler:function(config){
		    var jsUrl = this.markerUrl;
	        GAS.importlib(jsUrl,function(){
	        	var window = new GAS.MarkerManager(config);
	        	window.show();
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
				url  : this.statdeleteUrl, // 加载模块
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