(function(){
    /**
	 * zhengang.he@autonavi.com
	 * @param {Object} config
	 * 2015-3-20 
	 */
	GAS.MainLog = {
		//主题设置
		themeSetting:function(name,clspath,days){
		    var head = document.getElementsByTagName("head")[0],links = document.getElementsByTagName("link"),len = links.length;
	    	//加载样式文件
			var loadStyleSheet= function(path) {
				var newcls= document.createElement("link");
					newcls.setAttribute("rel", "Stylesheet");
					newcls.setAttribute("type", "text/css");
					newcls.setAttribute("title", "theme");
					newcls.setAttribute("href", path);
				return newcls;
			}
		    for (i = 0; i < len; i++) {
		        var link = links[i];
		        if (link.getAttribute("title") && head) {
		            head.removeChild(link); //移除样式
		        }
		    }
		    if(name !="default"){
		    	var csDom = loadStyleSheet(clspath);
		        head.appendChild(csDom);
		    }
		    days = days || 30;
			GAS.Cookie.setCookie(name,clspath,days); //设置
		},
	    LoginOut:  function(){
			Ext.Msg.show({
			   title:'提示',
			   msg: '请确认需要退出登录吗。',
			   buttons: Ext.Msg.YESNO,
			   fn: function(btn){
					if(btn=='yes'){
						var callHandler  = function(res,req){
					         var res = Ext.decode(res.responseText);
					         var msg = res["status"] == -100 ? "登出失败":"登出成功";
					         if(res["success"]){
					         	 window.location.href= GAS.config["loginURL"];
					         	 return;
					         }else{
					        	 msg = "登出失败";
					        	 Ext.Msg.alert("信息提示",msg);
					         }
					    };
					    Ext.Ajax.request({
					        url    : GAS.config.globalProxy + "/logout",
					        method : "post",
					        params : {},
					        scope  : this,
					        success: callHandler,
					        failure: callHandler
					    });	
					}
				},
			   icon: Ext.MessageBox.QUESTION
			});
		},
		password :function(val1,val2){
			if(val1==val2)
			return true;
			return false;
		},
		ChangePassword:function(){
				var oldpwd = new Ext.form.TextField({
					name       : "OLDPWD",
					inputType  : 'password',
					blankText  : '原密码必须输入',
					allowBlank : false,
					style      : "margin-top:2px",
					fieldLabel : "原密码"
				});
				var newpwd = new Ext.form.TextField({
					name      : "NEWPWD",
					fieldLabel: '新密码',
					inputType : 'password',
					allowBlank: false,
					blankText : '新密码不能为空',
					scope     : this,
					validator	: function(){
						var oldvalue = oldpwd.getValue();
						var value = this.getValue();
						var flag = GAS.MainLog.password(oldvalue,value);
						if(flag){
							return "新密码不能与原密码相同";
						}
						if(value.length<=6){
							return "密码长度不能小于6个字节";
						}
						if(value.length > 20){
							return "密码长度不能大于20个字节";
						}
						return true;
					}
				});
				var renewpwd = new Ext.form.TextField({
					name        : "RENEWPWD",
					fieldLabel  : '重复密码',
					inputType   : 'password',
					allowBlank  : false,
					blankText   : '重复密码不能为空',
					invalidText	: '两次输入密码不一致',
					scope       : this,
					validator	: function(){
						var value = this.getValue();
						var flag = GAS.MainLog.password(value,newpwd.getValue());
						return flag;
					}
				});
				var formPanel = new Ext.FormPanel({
					frame : false,
					bodyBorder: false,
					autoHeight : true,
					labelWidth : 55,
					labelAlign  : "right",
					defaults : {
						anchor : "92%"
					},
					items : [
						oldpwd,
						newpwd,
						renewpwd
					]
				});
				
				var getParams=function(){
					var newPass = renewpwd.getValue();
					var oldPass = oldpwd.getValue();
					var para = {};
					para[GAS.config.queryParamsName] = {
						oldpass: oldPass,
						newPass: newPass
					};
					return para;
				};
				
				var submit = function(){
					var form = formPanel.getForm();
					if (!form.isValid()) return;
					    var parameter = getParams();
						GAS.AOP.send({
							scope      : this,
							modulename : "logout",
							operation  : "update",
							callbackFn : function(data, meta,success){
								if(success && data){
									Ext.MessageBox.alert("信息提示",data["msg"] || data["result"] || '操作成功');
									win.close();
									return ;
								}
							    Ext.MessageBox.alert("信息提示",'请求过程中出现问题，请及时联系管理员');
							}
						},parameter);
				};
				var win = new Ext.Window({
					width : 250,
					border: false,
					modal : true,
					height : 150,
					title : "修改密码",
					items : formPanel,
					buttonAlign: "center",
					buttons : [{
						text : "确认",
						handler : submit
					},{
						text : "取消",
						handler : function(){
							win.close();
						}
					}],
					keys : [
						{key:Ext.EventObject.ENTER,fn:submit,scope:this}
					]
			});
			win.show();
		}
	};
	
})();