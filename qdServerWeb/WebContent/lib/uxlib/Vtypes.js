// 该文件用户动态创建form表单的各个控件，也可以用户动态查询模块信息
	//目前支持中文 age postcode ip telephone mobile numComma 这几种正则表达式的验证
	Ext.apply(Ext.form.VTypes, { 
	     chinese:function(val,field){    
	       var reg = /[\u4e00-\u9fa5]/;    
	       return reg.test(val);   
		  },    
		 chineseText:'请输入中文',
		 textNumber:function(val,field){
		    var reg = /^[0-9]*$/;
		    return reg.test(val);
		 },
		 textNumberText:"只能输入数字",
		 url:function(val,field){
		 	var reg = /(((https?)|(ftp)):\/\/([\-\w]+\.)+\w{2,3}(\/[%\-\w]+(\.\w{2,})?)*(([\w\-\.\?\\\/+@&#;`~=%!]*)(\.\w{2,})?)*\/?)/i;
		 	return reg.test(val);    
		 },
		 urlText:"请输入有效的URL地址",
		 age: function(val,field) {
		  var red = /^\d+$/;
		   if (!red.test(val)) {
		     return false;
		   }else{
		      var _age = parseInt(val);
		      if (_age < 200)
		        return true;
		      else 
		        return true;
		   }
		 },
		 ageText : '年龄格式出错！！格式例如：20',
		 postcode : function(val,field) {
		 	var reg = /^[1-9]\d{5}$/;
		    return reg.test(val);
		 },
		 postcodeText:"无效的邮政编码，例如：226001",
		 ip : function(val,field) {
		 	var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
			return reg.test(val);
		},
		ipText : "无效的IP地址，例如：222.192.42.12",
		telephone:function(val,field) {
		  var reg = /(^\d{3}\-\d{7,8}$)|(^\d{4}\-\d{7,8}$)|(^\d{3}\d{7,8}$)|(^\d{4}\d{7,8}$)|(^\d{7,8}$)/;
          return reg.test(val);
		},
		telephoneText : "无效的电话号码格式，例如：0513-89500414",
		mobile : function(val,field) {
			var reg = /^0?(13[0-9]|15[012356789]|18[0236789]|14[57])[0-9]{8}$/;
		    return reg.test(val);
		},
		mobileText : "无效的手机号码，例如：13485135075",
		numComma:function(val,field){
		    var limit = field["limit"] || 2;
		    var reg = eval("/^-?\\d+\\.?\\d{0,"+limit+"}$/");
		    if(!reg.test(val))    
	        {    
	            return false;    
	        }     
	        return true; 
		 },
		 numCommaText:"只能输入数字,保留2位小数",
		 object:function(val,field){
		 	var reg = /&nbsp;/ig,blank=/[ ]/g,htmltag=/<[^>]+>/g;
		    val = val.replace(htmltag,''); 
		    val = val.replace(reg,'');
		    val = val.replace(blank,'');
	        return Ext.isObject(val);; 
		 },
		 objectText:"无效的JSON对象"
	});