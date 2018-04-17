(function(){

	
	GAS.cacheAlarmCache = {
		cacheAlarmList : {},
		getCacheUrl :function(){
		   return  GAS.config["globalProxy"]+"/alarmArea/cacheAreaList?_dc="+new Date().getTime();
		},
		//加载所有的缓存数据
	    loadAllCacheAlarm:function(){
	    	  this.removeAllCache();
	    	  var callback = function(res){
	    	     var jsonRes = Ext.decode(res.responseText);
	    	     if(jsonRes){
	    	         var result  =jsonRes["result"];
	    	         if(result && result.length > 0){
	    	             for (var i = 0; i < result.length; i++) {
	    	             	  var obj = result[i];
	    	             	  var areaId = obj["areaId"],areaName = obj["areaName"];
	    	             	  this.addAlarmCache(areaId,areaName);
	    	             }
	    	         }
	    	     }
	    	  };
		      Ext.Ajax.request({
		      	   url : this.getCacheUrl(),
		           method : 'POST',
				   scope : this,
				   success : callback,
				   failure : callback
		      });
	    },
	    //清除所有的缓存
	    removeAllCache : function(){
	       this.cacheAlarmList = {};
	    },
	    //添加缓存
	    addAlarmCache : function(areaId,areaName){
	        this.cacheAlarmList[areaId]= areaName;
	    },
	    //更新缓存
	    updateAlarmCache:function(areaId,areaName){
	    	 var alarm = this.getCacheAlarm(areaId);
	         if(alarm){
	         	 this.cacheAlarmList[areaId]= areaName;
	         }
	    },
	    //删除缓存
	    deleteAlarmCache:function(areaId){
	         var alarm = this.getCacheAlarm(areaId);
	         if(alarm){
	         	delete this.cacheAlarmList[areaId]
	         }
	    },
	    //批量删除缓存数据
	    deleteAlarmCacheIds:function(areaIds){
	         if(areaIds && areaIds.length > 0){
	         	for (var i = 0; i < areaIds.length; i++) {
	         		var areaId = areaIds[i];
	         		delete this.cacheAlarmList[areaId];
	         	}
	         }
	    },
	    //通过键值对拿到缓存缓存对象
	    getCacheAlarm:function(areaId){
	        return  this.cacheAlarmList[areaId];
	    }
	
	};
	
})();