(function(){
	//组织结构管理模块
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.AreaManager = Ext.extend(Ext.Window,{
		     constructor : function(config) {
		     	config =config || {};
		     	this.action = config["action"] || "insert";
		     	this.updateUrl = GAS.config["globalProxy"]+ "/alarmArea/update?_dc="+new Date().getTime();
				this.addUrl    = GAS.config["globalProxy"]+ "/alarmArea/add?_dc="+new Date().getTime();
		     	this.selectParams = config["loadRecord"] || "";
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					width  : 840,
					height : 420,
					modal  : true,
					contrain : false,
					resize : false,
					maximizable : true,
					title  : this.getwinTitle(),
					closeAction : "hide",
					items  : this.getMainPanel(),
					buttonAlign:"center",
					buttons: this.getActionBtn()
				});
				this.addEvents("submit");
				GAS.AreaManager.superclass.constructor.apply(this, arguments);
		   },
		   // settting title 
		   getwinTitle:function(){
		        var title = "区域管理"
		   	    if(this.action=="insert"){
		   	        title += "-"+"添加"
		   	    }else{
		   	        title += "-"+"修改"
		   	    }
		   	    return title;
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : true,
						frame  : false,
						layout : "border",
						items  : [{
						   border : true,
						   frame  : false,
						   region : "west",
						   width  : 240,
						   margins: "1px",
						   layout : "fit",
						   title  : "区域表单",
						   items  : this.getFormPanel()
						},{
							border : true,
						    frame  : false,
						    region : "center",
						    margins: "1px 0px 1px 0px",
						    layout : "fit",
						    items  : this.getCenterPanel()
					    }]
			        });
			    }
			   return  this.mianPanel;
		   },
		   //form面板
		   getFormPanel:function(){
				if(!this.formPanel){
					   //区域名称
					  this.formAreaname = new Ext.form.TextField({
					      fieldLabel: "区域名称",
					      name      : "areaName",
					      width     : 220,
					      allowBlank: false,
					      anchor    : "92%"
					  });
					  //用途说明
					  this.descript = new Ext.form.TextArea({
					  	  rows      : 8,
					  	  fieldLabel: "用途说明",
					  	  width     : 220,
					  	  style  : "height:80px",
					  	  anchor    : "92%"
					  });
					  //主键
					  this.areaId = new Ext.form.Hidden({
					  	  fieldLabel: "主键",
					  	  name   : "id",
					  	  width  : 220,
					  	  anchor : "92%"
					  });
					  //code码
					  this.remark = new Ext.form.TextArea({
					  	  rows      : 8,
					  	  fieldLabel: "信息备注",
					  	  anchor : "92%",
					  	  style  : "height:90px"
					  });
					  var list = [
					      this.formAreaname,
						  this.descript,
						  this.areaId,
						  this.remark
					  ];
					  this.formPanel = new Ext.FormPanel({
							border		: false,
							bodyBorder	: false,
							layout      : "form",
							labelWidth  : 60,
							defaults    : {width: 230},
							bodyStyle   :'padding:3px 5px 0',
							labelAlign  : "right",
							items		: list,
							listeners: {
							   scope:this,
							   render : this.loadForm 
							}
				  });
				}
				return this.formPanel;
		   },
		   getCenterPanel:function(){
		       if(!this.centerPanel){
			        this.centerPanel = new GAS.BaseMap({
			          	border : false,
						frame  : false,
						layout : "fit",
						showClearBtn : true, //启用清除功能
						hideBaseMapBtn : true, //启用清除功能
						drawgon : true //启用多变形
			        });
			    }
			   return  this.centerPanel;
		   },
		   //反填数据
		   loadForm : function(){
				if (this.action == "update"){
			    	 var result = this.selectParams;
			    	 this.areaId.setValue(result["id"]);
				     this.formAreaname.setValue(result["name"]);
				     this.descript.setValue(result["description"]);
					 this.remark.setValue(result["remark"]);
					 var xys = result["xys"];
					 //地图记载完毕之后再添加覆盖物
					 this.getCenterPanel().on({
					    scope :this,
					    maprender:function(){
					    	 this.getCenterPanel().showDrawPolugon(xys);
					    }
					 });
				}
		   },
		   //关闭
			windowClose:function(){
			    this.close();
			},
			//操作按钮
			getActionBtn:function(){
			   var btnList = [],cancle ={
				   text :　"关闭",
				   scope: this,
				   handler : this.windowClose
				};
			   if(this.action == "update"){
			       btnList.push({
					   text :　"修改记录",
					   scope: this,
					   handler : this.saveRecordhandler
					});
			   }else{
			  		 btnList.push({
					   text :　"保存记录",
					   scope: this,
					   handler : this.saveRecordhandler
					});
			   }
			   btnList.push(cancle);
			   return btnList;
			},
			//获取经纬度坐标列表
			getMapXys:function(){
				var points = this.getCenterPanel().getDrawPolugonLatLngList();
				var parame = points.join("@");
			    return parame;
			},
			//提交参数 
			getSubmitParam : function(){
			    var params = {};
			    params["id"] = this.areaId.getValue();
			    params["name"] = this.formAreaname.getValue();
			    params["xys"]  =  this.getMapXys();
			    params["description"] = this.descript.getValue();
			    params["remark"] = this.remark.getValue();
			    return  params;
			},
			saveRecordhandler:function(){
			     	var areaName = this.formAreaname;
					if (!areaName.isValid()){
						return;
					}
					var points = this.getCenterPanel().getDrawPolugonLatLngList()
					if(points.length==0){
					   Ext.Msg.alert("信息提示", "请绘制区域");
					   return false;
					}
					var params = this.getSubmitParam();
					Ext.Msg.show({
						title : '请确认操作？',
						msg : '确认想要保存记录吗?',
						buttons : Ext.Msg.YESNO,
						icon : Ext.MessageBox.QUESTION,
						scope : this,
						fn : function(btn, text) {
							if (btn == 'yes') {
								this.onSubmit(params);
							}
						}
					});
			},
			//ajax提交参数
			onSubmit:function(params){
			    var callback = function(res,req){
		    	     var result = Ext.decode(res.responseText);
				 	 if(result["success"]){
				 	 	this.fireEvent("submit"); //激发submit方法
				 		Ext.Msg.alert("信息提示", result["msg"] || result["result"] || "添加成功");
				 		
				 		if(this.action=="update"){
				 		    GAS.cacheAlarmCache.updateAlarmCache(params["id"],params["name"]); //同步缓存
				 		}else{
				 			GAS.cacheAlarmCache.loadAllCacheAlarm(); //同步缓存
				 		}
				        this.windowClose(); 
				     }else{
				     	Ext.Msg.alert("信息提示", result["msg"] || result["result"] || "添加失败");
				     }
			    };
			    GAS.AOP.send({
			       url   : this.action=="update"?this.updateUrl:this.addUrl ,
			       params: params,
			       scope : this,
			       callbackFn:callback
			   });
			}
	});
	
})();




