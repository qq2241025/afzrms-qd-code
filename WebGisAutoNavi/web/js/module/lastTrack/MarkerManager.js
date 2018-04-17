(function(){
	/**
	 * 添加修改标注信息
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.MarkerManager = Ext.extend(Ext.Window,{
		     constructor : function(config) {
		     	config =config || {};
		     	this.action = config["action"] || "insert";
		     	this.rootNodeId = 1; //树形根节点
		     	this.pointerUrl   = GAS.iconPath + "flager.gif"
		     	this.updateUrl    = GAS.config["globalProxy"]+"/marker/update?_dc="+new Date().getTime(); //修改的URL地址
				this.addUrl       = GAS.config["globalProxy"]+"/marker/add?_dc="+new Date().getTime(); //添加的URL地址
		     	this.loadRecord   = config["loadRecord"] || {};
		     	this.loadRecordId  = this.loadRecord["id"];
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					width  : 680,
					modal  : true,
					height : 400,
					maximizable:true,
					constrain  : true, //控制win在范围内显示
					resizable  : false,
					title  : this.getwinTitle(),
					closeAction : "hide",
					items  : this.getMainPanel(),
					buttonAlign:"center",
					buttons: this.getActionBtn(),
					listeners : {
						 scope : this,
						 show : function(){
						 	
						 }
					}
				});
				this.addEvents("submit");
				GAS.MarkerManager.superclass.constructor.apply(this, arguments);
		   },
		    // settting title 
		   getwinTitle:function(){
		        var title = "车辆类型管理"
		   	    if(this.action=="insert"){
		   	        title += "-"+"添加"
		   	    }else{
		   	        title += "-"+"修改"
		   	    }
		   	    return title;
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
									 width  : 240,
									 margins: "1px",
									 split : true,
									 title : "表单信息",
								     items : this.getFormPanel()
							},{
								border : true,
								frame  : false,
								layout : "fit",
								tbar   : [{
									   name:"pointMarker",
									   text:"标注",
									   scope:this,
									   iconCls:"icon-add",
									   handler:this.pointerHander
								}],
								items  : this.getMapPanel(),
								margins: "1px",
								region : "center"
						}]
					});
				}
			    return this.mainpanel;
		   },
		   addPointer : function(cfg){
		   	    var point= cfg["coordinate"];
            	if(!this.pointerMarker){
            		 this.pointerMarker = this.getMapPanel().addMarker({
		            	 lnglat   : point,
		            	 iconSize : [16,28],
		            	 iconAnchor:[9,28],
		            	 iconUrl  : cfg["iconUrl"]
	            	});
            	}
            	this.pointerMarker.setPosition(point);
		   },
		   pointerHander:function(){
			   this.getMapPanel().MarkerPointer(function(data){
			        var cfg = {
			           iconUrl: this.pointerUrl ,
			           coordinate:data["coordinate"]
			        }  
			        this.Markerlng = data["lng"];
			        this.Markerlat = data["lat"];
			        this.addPointer(cfg);
			   },this);
		   },
		   getMarkerLng:function(){
		   	   return this.Markerlng;
		   },
		   getMarkerLat:function(){
		   	　　return this.Markerlat;
		   },
		   getMapPanel :function(){
		       if(!this.mapPanel){
		       	   var me = this;
			       this.mapPanel = new GAS.BaseMap({
			           hideTools: true,
			           mapRender : function(){
			              me.doReSetForm();
			           }
			       });
			    }
			    return  this.mapPanel;
		   },
		   //反填数据
		   getFormPanel:function(){
				if(!this.formPanel){
					  //名称
					  this.nameFiled = new Ext.form.TextField({
					      fieldLabel: "名称",
					      name      : "name",
					      allowBlank: false,
					      fullWidth : true,
					      anchor    : "92%"
					  });
					   //信息备注
					  this.remarker = new Ext.form.TextArea({
					  	  rows : 4,
					  	  fieldLabel: "信息备注",
					  	  anchor : "92%"
					  });
					  this.formPanel =  new Ext.FormPanel({
				    	labelWidth: 55, 
				        frame:false,
				        border: false,
				        bodyStyle:'padding:5px 5px 0',
				        defaults: {
				        	width: 190
				        },
				        labelAlign  : "right",
				        defaultType: 'textfield',
				        items: [
				              this.nameFiled,
						      this.remarker
			            ]
					});
				}
				return this.formPanel;
			},
			doReSetForm:function(){
				if(this.action!="insert"){
					var x = this.loadRecord["x"],y=this.loadRecord["y"];
				    this.nameFiled.setValue(this.loadRecord["name"]);
				    this.remarker.setValue(this.loadRecord["remarker"]);
				    this.Markerlng = x;
				    this.Markerlat = y;
	                var pointer = this.getMapPanel().createPoint(x,y);
	                var cfg = {
			           iconUrl: this.pointerUrl ,
			           coordinate:pointer
			        } 
		   			this.addPointer(cfg);
		   			this.getMapPanel().moveToCenter(pointer);
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
			//提交参数
			getSubmitParam : function(){
			    var params = {};
			    if(this.action!="insert"){
			        params["id"] = this.loadRecordId;
			    }
			    params["name"] = this.nameFiled.getValue();
			    params["lat"] = this.getMarkerLat();
			    params["lng"] = this.getMarkerLng();
			    params["desc"] = this.remarker.getValue();
			    return  params;
			},
			saveRecordhandler:function(){
			     	var form = this.getFormPanel();
					if (!form || !form.getForm().isValid()){
						return;
					}
					var lng = this.getMarkerLng(),lat= this.getMarkerLat()
					if(!lng || !lat){
					   Ext.Msg.alert("信息提示", "请标注位置");
					   return ;
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




