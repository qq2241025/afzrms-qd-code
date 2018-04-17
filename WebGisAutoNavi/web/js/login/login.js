(function(){
	GAS.LOGIN = Ext.extend(Ext.Window,{
	   constructor : function(config){
	   	    this.config = config;
	   	    this.dologinUrl = GAS.config["globalProxy"]+"/login?_dc="+new Date().getTime(); //查询接口
	   	    Ext.apply(this,{
	   	       title:"青岛国际机场飞行区资源管理系统-登陆首页",
			   width:450,
			   height:360,
			   layout:"fit",
			   buttonAlign:"center",
			   closable: false,
			   maximized:false,
			   constrain : true, //是否允许超出范围
		  	   modal: true,//背景遮罩
			   items: this.getMainPanel(),
			   listeners: {
			        scope: this
			   },
			   buttons : [{
							text:'登录',
							handler:this.submit,
							scope:this
						},
						{
							text:'重置',
							type:'reset',
							scope:this,
							handler:function(){
								this.getloginForm().getForm().reset();
							}
						}
					],
			   keys : [
				   {key:Ext.EventObject.ENTER,fn:this.submit,scope:this}
				]});
			GAS.LOGIN.superclass.constructor.call(this,config);
	   },
	  getNorthPanel:function(){
	      if(!this.northPanel){
	          this.northPanel  = new Ext.Panel({
	              //region:"north",
	              //layout:"fit",
			   	  //border:true,
			   	  //margins:"1 1 1 1",
			 	  //height: 146,
				  //layout:"fit",
			   	  //html: '<image src="images/DBLogins.png" style="width:100%;height:100%;">'
	          });
	      }
	      return this.northPanel;
	   },
	   getCenterPanel:function(){
	      if(!this.centerPanel){
	          this.centerPanel  = new Ext.Panel({
	              region:"center",
	              layout:"fit",
			   	  border:true,
			   	  frame:false,
			   	  margins:"1px",
			   	  items:this.getloginForm()
	          });
	      }
	      return this.centerPanel;
	   },
	   getloginForm:function(){
	      if(!this.formpanel){
				this.formpanel = new Ext.FormPanel({
					labelWidth:60,
					labelAlign:"right",
					layout:"form",
					border:false,
					frame:false,
					scope:this,
					bodyStyle:'width:300px;margin-left:80px;margin-right:60px;padding-top:20px',
					items:[
						this.getUserUsername(),
						this.getPassword(),
						this.getverfyCodeSeed()
					]
				});
			}
			return this.formpanel;
	   },
	   getUserUsername:function(){
			if(!this.UserID){
				this.UserID = new Ext.form.TextField({
					scope:this,
					width : 140,
					fieldLabel:"用户名",
					blankText:"请输入用户名",
					emptyText:"请输入用户名",
					allowBlank : false,
					name:'userName',
					bodyStyle:'font-size:14px;'
				});
			}
			return this.UserID;
		},
		getPassword:function(){
			if(!this.Password){
				this.Password = new Ext.form.TextField({
					scope:this,
					width : 140,
					fieldLabel:" 密 码 ",
					allowBlank : false,
					blankText:"请输入密码",
					emptyText:"请输入密码",
					name:'userPass',
					inputType: 'password'
				});
			}
			return this.Password;
		},
		getverfyCodeSeed : function(){
			if(!this.verfyCodeSeed){
				this.verCodeFiled = new Ext.form.TextField({
					 emptyText:"验证码",
					 name:'rand',
					 fieldLabel:"验证码",
					 anchor:"100%"
				});
				this.verfyCodeSeed = new Ext.Panel({
					layout:"column",
					border:false,
					items:[{
							xtype:"panel",
							layout:"form",
							width:140,
							border:false,
							autoScroll:false,
							items: this.verCodeFiled
						},
						this.getCheckCode(),
						{xtype:'hidden',name:'verfyCodeSeed',id:'verfyCodeSeed',value:'1234'}
					]
				});
			}
			return this.verfyCodeSeed;
		},
		getMainPanel:function(){
		    if(!this.centerPanel){
		          this.centerPanel  = new Ext.Panel({
				   	  border:false,
				   	  frame:false,
				   	  layout:"border",
				   	  margins:"1 1 1 1",
				   	  items: [this.getNorthPanel(),this.getCenterPanel()]
		          });
		      }
		     return this.centerPanel;
		},
		getCheckCode : function(){
			if(!this.checkcode){
				this.checkcode = new Ext.form.CheckCode({
					xtype : "checkCode",
					width : 60,
					url   : GAS.config.vercode,
					style : "margin:1px 0px 2px 6px" ,
					height: 20,
					handler : true
				});
			}
			return this.checkcode;
		},
		getLoadMask : function(){
		    if(!this.loginLoad){
		        this.loginLoad  = new Ext.LoadMask(this.getEl(),{
		        	  msg: "正在验证登录信息，请稍后...."
		        });
		    }
		    return this.loginLoad ;
		},
		getLoginParams:function(from){
	      if(from){
	        var data = {
	        	account: GAS.utils.base64encode(this.getUserUsername().getValue()),
	        	password   : GAS.utils.base64encode(this.getPassword().getValue()),
	        	validateCode : this.verCodeFiled.getValue()
	        };
	      	return data;
	      }
	   },
	   submit:function(){
 		     var from = this.getloginForm().getForm();
		     if(!from.isValid()){
		       return ;
		     }
		     var params = this.getLoginParams(from);
		     var callHandler  = function(res,req){
		     	 this.getLoadMask().hide();
		         var res = Ext.decode(res.responseText);
		         var msg = res["info"],tookeId = res["sessionID"];
		         if(res["success"]){
		         	 GAS.Cookie.setCookie("tokenID",tookeId); //写入cookies
		         	 window.location.href=GAS.config["mainPageUrl"]+"?tokenID="+tookeId;
		         	 return;
		         }else{
		        	 this.getCheckCode().onClick();
		         }
		         Ext.Msg.alert("信息提示",msg);
		    } ;
		    this.getLoadMask().show();
		    Ext.Ajax.request({
		        url    : this.dologinUrl,
		        method : "post",
		        params : params,
		        scope  : this,
		        success: callHandler,
		        failure: callHandler
		    });
		 }
		});
})();

Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	var login = new GAS.LOGIN({});
	login.show();
	window.onresize = function(){
		login.center();
	};
});