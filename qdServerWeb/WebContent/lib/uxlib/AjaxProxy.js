(function(){
	/**
	 * zhengang.he@autonavi.com
	 * @param {Object} config
	 */
	Ext.data.AsyncProxy = Ext.extend(Ext.data.HttpProxy, {
            constructor:function(conn){
                 var api = {};
				 api[Ext.data.Api.actions.read] = true;
				 Ext.data.AsyncProxy.superclass.constructor.call(this, {
							api : api,
							actionType : conn["actionType"]
						});
				 this.actionType = conn["actionType"];
            },
			doRequest : function(action, rs, params, reader, callback, scope,arg) {
				var o = {
					method : (this.api[action])
							? this.api[action]['method']
							: undefined,
					request : {
						callback : callback,
						scope : scope,
						arg : arg
					},
					reader : reader,
					callback : this.createCallback(action, rs),
					scope : this
				};
				if (params.jsonData) {
					o.jsonData = params.jsonData;
				} else if (params.xmlData) {
					o.xmlData = params.xmlData;
				} else {
					o.params = params || {};
				}
				var actionType = this.actionType;
				
			    DBS.AOP.send({
					modulename : actionType.modulename,
					operation : actionType.operation,
					scope : this,
					callbackFn : function(data, meta, success) {
						var response = {
							responseText : Ext.encode(data)
						};
						if (!success) {
							if (action === Ext.data.Api.actions.read) {
								this.fireEvent('loadexception', this,
										o, response, meta);
							}
							this.fireEvent('exception', this,
									'response', action, o, response,
									meta);
							o.request.callback.call(o.request.scope,
									null, o.request.arg, false);
							return;
						}
						if (action === Ext.data.Api.actions.read) {
							this.onRead(action, o, response);
						} else {
							this.onWrite(action, o, response, rs);
						}
					}
				}, o.params)
			}
		});
	
})();
