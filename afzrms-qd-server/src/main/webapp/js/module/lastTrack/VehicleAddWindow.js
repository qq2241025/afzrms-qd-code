(function() {
	/**
	 * 添加车辆维修备案
	 * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.VehicleAddWindow = Ext.extend(Ext.Window, {
		constructor : function(config) {
			config = config || {};
			this.repairAddUrl = GAS.config["globalProxy"]+"/repair/add?_dc="+new Date().getTime(); //规则
			this.vehicleRecord = config["vehicleRecord"] || {};
			Ext.apply(this, {
				border : false,
				frame  : true,
				layout : "fit",
				width  : 400,
				height : 268,
				modal  : true,
				contrain : true,
				resize : false,
				title  : "车辆维修备案",
				closeAction : "close",
				items  : this.getMainPanel(),
				buttonAlign:"center",
				buttons:[{
				   text  :　"确定",
				   scope : this,
				   handler: this.savehandler
				},{
				   text :　"关闭",
				   scope: this,
				   handler : this.windowClose
				}]
			});
			GAS.VehicleAddWindow.superclass.constructor.apply(this, arguments);
		},
		getVerhicleNum : function(){
		    return this.vehicleRecord["name"] || this.vehicleRecord["text"];
		},
		getDeptName : function(){
		    return this.vehicleRecord["deptName"];
		},
		getDevideId :  function(){
		    return this.vehicleRecord["deviceId"] || this.vehicleRecord["id"];
		},
		getDeviceText :  function(){
		    return this.noteContent.getValue();
		},
		windowClose:function(){
		   this.close();
		},
		getMainLoadMask : function() {
		 	if(!this.loadMask){
		 		  var bodyEl = this.getMainForm().findParentByType("panel").body;
			      this.loadMask = new Ext.LoadMask(bodyEl, {
						msg : "正在提交数据......."
				  });
		 	}
			return this.loadMask;
		},
		getParams:function(){
		    var params = {};
		    params["deviceId"] = this.getDevideId();
		    params["desc"] = this.getDeviceText();
			return params;
		},
		getMainForm : function(){
		   if(!this.noteForm){
		   	    this.verhicleNum = new Ext.form.TextField({
		   	       fieldLabel: '车牌号',
		   	       disabled : true,
		   	       value : this.getVerhicleNum(),
		   	       name : "verhicleNum",
		   	       anchor: "95%"
		   	    });
		   	    this.deptCo = new Ext.form.TextField({
		   	       fieldLabel: '所属部门',
		   	       name : "dept",
		   	       value : this.getDeptName(),
		   	       disabled :true,
		   	       anchor: "95%"
		   	    });
		   	    this.noteContent = new Ext.form.TextArea({
		   	       height: 100,
		   	       style  : "font-size: 10pt; font-weight: bold; font-family: Courier New,Sans Serif,Times New Roman;",
		   	       fieldLabel: '备注内容',
		   	       name : "content",
		   	       allowBlank : false,
		   	       blankText :"备注内容不能为空",
             	   emptyText:"请输入备注内容",
		   	       anchor: "95%"
		   	    });
		   	    
		       this.noteForm = new Ext.FormPanel({
			        labelWidth:60,
			        labelAlign:"right",
			        frame:false,
			        border: false,
			        layout : "form",
			        fileUpload:true,
			        bodyStyle:"margin-top:2px;",
			        defaults:{width:200},
			        items: [
			        	this.verhicleNum,
			            this.deptCo,
			            this.noteContent
			         ]
			    });
		   }
		   return this.noteForm;
		},
		//提交保存
		savehandler:function(){
		     var form = this.getMainForm().getForm();
		     var callback =function(resp){
		     	this.getMainLoadMask().hide();
		        var res = Ext.decode(resp.responseText) ||{};
	            if(res["success"] ==true || res["success"] =="true"){
	            	this.windowClose();
	                Ext.Msg.alert("信息提示","操作成功");
	            }else{
	            	Ext.Msg.alert("信息提示","操作失败");
	            }
		     };
		     if(form && form.isValid()){
		     	   this.getMainLoadMask().show();
		           GAS.AOP.send({
				       url    : this.repairAddUrl,
				       method : "post",
				       params : this.getParams(),
				       scope  : this,
				       callbackFn: callback
				   });
		     }
		},
		getMainPanel: function() {
		   if(!this.mainPanel){
		       this.mainPanel = new Ext.Panel({
		           frame : false,
		           borde : false,
		           layout: "fit",
		           items : this.getMainForm()
		       });
		   }
		   return this.mainPanel;
		}
	});
})();
