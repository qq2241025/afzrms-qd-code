(function() {
	
	
	/**
	 * GAS.AOP.send({
	 *     url : "",
	 *  --   method :"get" || "post", //非必须
	 *     params : {}
	 *    -- scops : this //作用域   //非必须
	 *     callbackFn : callback
	 * })
	 */
	GAS.AOP = {
		send : function(metaData) {
//			if (!metaData || !metaData["callbackFn"] || !metaData["params"] || !metaData["url"]) {
//				console.warn("配置信息出错,callbackFn is not config " );
//				return false;
//			}
			var cfg = {
			   method : 'POST',
			   scope : this,
			   params : {},
			   success : metaData["callbackFn"],
			   failure : metaData["callbackFn"]
			}
			Ext.apply(cfg,metaData);
			Ext.Ajax.request(cfg);
			
			Ext.Ajax.on({
				scope : this,
			    requestcomplete:function(conn,response,options){
			       var msg = response.getResponseHeader("sessionstatus");
				   var all = response.getAllResponseHeaders();
				   if(msg && msg =="timeout"){
				   	  Ext.Msg.show({
						title : '信息提示',
						msg : '登录信息已超时,请重新登录',
						buttons : {yes:true, no:false},
						icon : Ext.MessageBox.QUESTION,
						closable : false,
						scope : this,
						fn : function(btn, text) {
							if (btn == 'yes') {
							   window.top.location.href = GAS.config["loginURL"] ;
							}
						}
					 });
				     return false;
			     }else if(msg && msg =="error"){
			     	  var result = Ext.decode(response.responseText);
				   	  Ext.Msg.show({
						title : '信息提示',
						msg : (result["result"] || result["msg"] || '系统异常')+',请及时联系管理员,是否退出',
						buttons : Ext.Msg.YESNO,
						icon : Ext.MessageBox.QUESTION,
						scope : this,
						fn : function(btn, text) {
							if (btn == 'yes') {
							   window.top.location.href = GAS.config["loginURL"] ;
							}
						}
					 });
				     return false;
			     }
			   }
		   });		
		}
	};
})();
