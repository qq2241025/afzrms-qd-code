(function(){
	GAS.LOGIN = {
		loginUrl :GAS.config["globalProxy"]+"/login?_dc="+new Date().getTime(), //查询接口
	    init:function(){
	       this.initVercode();
	       this.dologin();
	    },
	    dologin:function(){
	        var loginBtn = Ext.get("loginBtn");
	        var resetBtn = Ext.get("resetForm");
	        loginBtn.on({
	           scope :this,
	           click : function(){
	                this.checklogin();
	           }
	        });
	        resetBtn.on({
	           scope :this,
	           click : function(){
	                this.resetForm();
	           }
	        });
	    },
	    resetForm:function(){
	        Ext.getDom("username").value = "";
	        Ext.getDom("userpass").value = "";
	        Ext.getDom("userpassVercode").value = "";
	    },
	    //校验登录
	    checklogin:function(){
	        var username = Ext.getDom("username").value;
	        var userpass = Ext.getDom("userpass").value;
	        var vercode  =  Ext.getDom("userpassVercode").value;
	        if(GAS.isEmpty(username)){
	            Ext.Msg.alert("信息提示","用户名不能为空");
	        }else  if(GAS.isEmpty(userpass)){
	            Ext.Msg.alert("信息提示","密码不能为空");
	        } else if(GAS.isEmpty(vercode)){
	            Ext.Msg.alert("信息提示","验证码不能为空");
	        }else{
	        	var parame  = {
	        	    account: GAS.utils.base64encode(username),
	        		password   : GAS.utils.base64encode(userpass),
	        		validateCode : vercode
	        	};
	            this.dologinpage(parame);
	        }
	    },
	    dologinpage:function(params){
	    	 var callHandler = function(res){
	    	     var res = Ext.decode(res.responseText);
		         var msg = res["info"],tookeId = res["sessionID"];
		         var isadmin = res["isAdmin"] || false;
		         if(res["success"]){
		         	 GAS.Cookie.setCookie("tokenID",tookeId); //写入cookies
		         	 GAS.Cookie.setCookie("isAdmin",isadmin); //写入cookies
		         	 window.location.href=GAS.config["mainPageUrl"]+"?tokenID="+tookeId;
		         	 return;
		         }else{
		        	 this.initVercode();
		         }
		         Ext.Msg.alert("信息提示",msg);
	    	 };
	         Ext.Ajax.request({
		        url    : this.loginUrl,
		        method : "post",
		        params : params,
		        scope  : this,
		        success: callHandler,
		        failure: callHandler
		    });
	    },
	    //初始化验证码
	    initVercode : function(){
	       var vercode =this.getVercode().dom;
	       vercode.src = this.getVercodeUrl();
	       this.getVercode().on({
	       	    scope : this,
	       	    click : function(e,dom){
	       	       dom.src = this.getVercodeUrl();
	       	    }
	       });
	    },
	    getVercodeUrl :function(){
	       return GAS.config["vercode"] +"?_dc="+ new Date().getTime();
	    },
	    getVercode:function(){
	       return Ext.get("vercode");
	    }  
	}
})();

Ext.onReady(function() {
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	GAS.LOGIN.init();
});
