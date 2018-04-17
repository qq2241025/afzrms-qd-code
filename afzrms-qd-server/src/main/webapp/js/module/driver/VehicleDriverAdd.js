(function(){
	//添加司机的
	GAS.VehicleInfoManager = Ext.extend(Ext.Window,{
		     constructor : function(config) {
		     	config =config || {};
		     	this.action = config["action"] || "insert";
		     	this.rootNodeId = 1; //树形根节点
		     	this.updateUrl    = GAS.config["globalProxy"]+"/driver/update?_dc="+new Date().getTime(); //修改的URL地址
				this.addUrl       = GAS.config["globalProxy"]+"/driver/add?_dc="+new Date().getTime(); //添加的URL地址
		     	this.comboxTreeUrl= GAS.config["globalProxy"]+"/dept/tree?_dc="+new Date().getTime(); //树形菜单
		     	this.loadRecord   = config["loadRecord"] || {};
		     	
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					width  : 480,
					modal  : true,
					constrain  : true, //控制win在范围内显示
					resizable  : false,
					title  : this.getwinTitle(),
					closeAction : "hide",
					items  : this.getMainPanel(),
					buttonAlign:"center",
					buttons: this.getActionBtn()
				});
				 this.addEvents("submit");
				 GAS.VehicleInfoManager.superclass.constructor.apply(this, arguments);
		   },
		    // settting title 
		   getwinTitle:function(){
		        var title = "车辆驾驶员管理"
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
			          	border : false,
						frame  : false,
						layout : "fit",
						items  : this.getFormPanel()
			        });
			    }
			   return  this.mianPanel;
		   },
		   //form面板
		   getFormItems : function(formItems) {
				if (formItems){
					var items = [];
					for(var index=0,len =formItems.length;index<len;index++){
						    var field = formItems[index];
						    if(!field){
						    	return ;
						    }
						    field.anchor = "92%";
							if(field.getXType() != "hidden"){
								var config = {
									xtype 		: "container",
									layout		: "form",
									border		: false,
									hidden		: field.hidden == true ? true : false,
									labelWidth	: 80,
									columnWidth : 0.5,
									width       : field["width"] || 230,
									labelAlign  : "right",
									style		: "padding-top:2px;",
									items		: [field]
								};
								if(field.getXType() == "textarea"){
									config["columnWidth"] = 1;
									field.anchor = "96%";
								}
								if(field["fullWidth"] || field["fullWidth"]=="true"){
									config["columnWidth"] = 1;
									field.anchor = "96%";
								}
								items.push(config);
							}else{
								items.push(field);
							}
					}
					this.formitems = new Ext.Container({
						border:true,
						autoHeight: true,
						layout:"column",
						items : items
					});
				}
				return this.formitems;
			},
		   //反填数据
		   loadForm : function(){
				if (this.action == "update"){
					 function loadrecord(){
					    var result = this.loadRecord;
				    	 // <1>
				    	 this.upDept.setHiddenValue(result["deptId"],result["deptName"]); //上级部门
				    	 // <2>
					     this.vehicleNumber.setValue(result["name"]); //车牌号
						 // <3>车辆类型ID
						 this.vehicleType.getStore().on({
						      scope : this,
						      load  : function(store){
						          this.vehicleType.setValue(result["vehicleTypeId"]);
						      }
						 })
						 // <3>车辆品牌ID
						 this.vehicleBrand.getStore().on({
						      scope : this,
						      load  : function(store){
						          this.vehicleBrand.setValue(result["vehicleBrandId"]);
						      }
						 })
						 // <4>
						 this.simcard.setValue(result["simcard"]); //sim
						 // <5>
						 this.deviceId.setValue(result["deviceId"]); //终端序列号
						 // <6>
						 this.locateType.setValue(result["locateType"]); //定位类型
						 // <7>
						 this.protocalType.getStore().on({
						      scope : this,
						      load  : function(store){
						          this.protocalType.setValue(result["protocalType"]);
						      }
						 })
						 // <8>
						 this.remarker.setValue(result["remark"]);
					 }
					 this.on({
					    scope : this,
					    show  : function(){
					       loadrecord.defer(10,this);
					    }
					 });
				}
		   },
		   getFormPanel:function(){
				if(!this.formPanel){
					  //终端序列号
					  this.deviceId = new Ext.form.TextField({
					      fieldLabel: "终端序列号",
					      name : "deviceId",
					      allowBlank:false,
					      disabled  : this.action=="insert"?false:true ,
					      anchor : "92%"
					  });
					  this.vehicleNumber = new Ext.form.TextField({
					      fieldLabel: "车牌号",
					      name : "vehicleNumber",
					      allowBlank:false,
					      anchor : "92%"
					  });
					  this.vehicleType =new Ext.form.ComboBox({
				  	    fieldLabel :"车辆类型",
				  	    title      :"车辆类型",
					    typeAhead: true,
					    triggerAction: 'all',
					    lazyRender:true,
					    mode  : 'local',
					    editable : false,
					    store : new Ext.data.JsonStore({
				     		autoLoad : true,
						 	fields   : ["id","name"], 
						 	root     : 'data', 
							totalProperty: 'total', 
							baseParams:{
							  limit : 200
							},
						    proxy : new Ext.data.HttpProxy({
								 url : this.verTypeUrl
							})
				     	 }),
					    valueField: 'id',
					    displayField: 'name'
					  });
					  this.vehicleBrand =new Ext.form.ComboBox({
				  	    fieldLabel :"车辆品牌",
				  	    title      :"车辆品牌",
					    typeAhead: true,
					    triggerAction: 'all',
					    lazyRender:true,
					    mode  : 'local',
					    editable : false,
					    store : new Ext.data.JsonStore({
				     		autoLoad : true,
						 	fields   : ["id","name"], 
						 	root     : 'data', 
							totalProperty: 'total', 
						    proxy : new Ext.data.HttpProxy({
								 url : this.verBrandUrl
							})
				     	 }),
					    valueField: 'id',
					    displayField: 'name'
					  });
					  //取值
					  this.simcard= new Ext.form.TextField({
					      fieldLabel: "SIM卡号",
					      name : "simcard",
					      allowBlank:false,
					      anchor : "92%"
					  });
					  //定位类型
					  this.locateType =   new Ext.form.ComboBox({
				  	    fieldLabel :"定位类型",
				  	    title      :"定位类型",
					    typeAhead: true,
					    triggerAction: 'all',
					    lazyRender:true,
					    mode  : 'local',
					    editable : false,
					    store : new Ext.data.JsonStore({
				     		autoLoad : true,
						 	fields   : ["id","localtype"], 
						 	root     : 'data', 
							totalProperty: 'total', 
						    data:{
						       data : [{id:true,localtype:"GPS协议"}]
						    }
				     	}),
					    valueField: 'id',
					    value: true,
					    displayField: 'localtype'
					 });
					  //终端协议类型
					  this.protocalType = new Ext.form.ComboBox({
				  	    fieldLabel :"协议类型",
				  	    title      :"协议类型",
					    typeAhead: true,
					    triggerAction: 'all',
					    lazyRender:true,
					    mode  : 'local',
					    editable : false,
					    store :  new Ext.data.JsonStore({
				     		autoLoad : true,
						 	fields   : ["name","protocalName"], 
						 	root     : 'data', 
							totalProperty: 'total', 
						    proxy : new Ext.data.HttpProxy({
								 url : this.protocalUrl
							})
				     	}),
					    valueField: 'protocalName',
					    displayField: 'name'
					 });
					  //上级部门
					  this.upDept = new Ext.form.ComboBoxTree({
							triggerAction : "all",
							fieldLabel    : "所属部门",
							displayField  : "text",
							valueField    : "id",
							autoLoad      : true,
							dataRoot      : "result",
							dataUrl       : this.comboxTreeUrl,
							baseParams    : {
							    id : this.rootNodeId  //请求参数
							}
					  });
					   //code码
					  this.remarker = new Ext.form.TextArea({
					  	  rows : 4,
					  	  fieldLabel: "信息备注",
					  	  anchor : "97%"
					  });

					  var list = [
					      this.deviceId,
						  this.vehicleNumber,
						  this.vehicleType,
						  this.vehicleBrand,
						  this.simcard,
						  this.locateType,
						  this.protocalType,
						  this.upDept,
						  this.remarker
					  ];
					  var formItems = this.getFormItems(list);
					  this.formPanel = new Ext.FormPanel({
				            autoHeight	: true,
							border		: false,
							bodyBorder	: false,
							items		: formItems,
							listeners: {
							   scope:this,
							   render : this.loadForm 
							}
				  });
				}
				return this.formPanel;
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
			//提交参数
			getSubmitParam : function(){
			    var params = {};
			    params["deviceId"] = this.deviceId.getValue();
			    params["name"] = this.vehicleNumber.getValue();
			    params["deptId"] = this.upDept.getHiddenValue();
			    params["vehicleTypeId"] = this.vehicleType.getValue();
				params["vehicleBrandId"] = this.vehicleBrand.getValue();
			    params["protocalType"] = this.protocalType.getValue();
			    params["simcard"] = this.simcard.getValue();
			    params["locateType"] = this.locateType.getValue();
			    params["remark"] = this.remarker.getValue();
			    return  params;
			},
			saveRecordhandler:function(){
			     	var form = this.getFormPanel();
					if (!form || !form.getForm().isValid()){
						return;
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
				 	 if(result["success"] && result["success"]=="true"){
				 	 	this.fireEvent("submit");
				 		Ext.Msg.alert("信息提示", result["result"] || result["msg"] || "添加成功");
				        this.windowClose(); 
				     }else{
				     	Ext.Msg.alert("信息提示", result["result"] || result["msg"] || "添加失败");
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




