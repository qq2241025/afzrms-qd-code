(function(){
	//车辆品牌管理页面
	/**
	 * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.VerchileTypManager = Ext.extend(Ext.Window,{
		     constructor : function(config) {
		     	config =config || {};
		     	this.action = config["action"] || "insert";
		     	this.rootNodeId = 1; //树形根节点
		     	this.updateUrl    = GAS.config["globalProxy"]+"/vehicleBrand/update?_dc="+new Date().getTime(); //修改的URL地址
				this.addUrl       = GAS.config["globalProxy"]+"/vehicleBrand/add?_dc="+new Date().getTime(); //添加的URL地址
		     	this.loadRecord   = config["loadRecord"] || {};
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					width  : 360,
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
				 GAS.VerchileTypManager.superclass.constructor.apply(this, arguments);
		   },
		    // settting title 
		   getwinTitle:function(){
		        var title = "车辆品牌管理"
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
									field.anchor = "95%";
								}
								if(field["fullWidth"] || field["fullWidth"]=="true"){
									config["columnWidth"] = 1;
									field.anchor = "95%";
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
				    	this.primaryId.setValue(result["id"]);
					    this.nameFiled.setValue(result["name"]);
					    this.desc.setValue(result["description"]);
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
					  //主键
					  this.primaryId = new Ext.form.Hidden({
					  	  fieldLabel: "主键",
					  	  name : "id",
					  	  anchor : "92%"
					  });
					  //名称
					  this.nameFiled = new Ext.form.TextField({
					      fieldLabel: "名称",
					      name      : "name",
					      allowBlank: false,
					      fullWidth : true,
					      anchor    : "92%"
					  });
					  //描述
					  this.desc = new Ext.form.TextArea({
					      fieldLabel: "描述",
					      name      : "description",
					      rows      : 4,
					      anchor    : "92%"
					  });
					   //信息备注
					  this.remarker = new Ext.form.TextArea({
					  	  rows : 4,
					  	  fieldLabel: "信息备注",
					  	  anchor : "97%"
					  });

					  var list = [
					  	  this.primaryId,
					      this.nameFiled,
						  this.desc,
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
			    params["id"] = this.primaryId.getValue();
			    params["name"] = this.nameFiled.getValue();
			    params["description"] = this.desc.getValue();
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




