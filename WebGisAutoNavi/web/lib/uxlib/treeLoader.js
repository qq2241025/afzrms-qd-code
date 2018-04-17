(function(){

		Ext.tree.JsonTreeLoader = Ext.extend(Ext.tree.TreeLoader,　{
			 constructor:function(config){
			 	 this.rootName=config["root"]||'root';
		　　		 Ext.tree.JsonTreeLoader.superclass.constructor.apply(this,arguments);
			 },
		　　 processResponse:function(response,node,callback)　{
		　　　　 var　json=response.responseText;
		　　　　 try　{
		　　　　　　 var o=response.responseData||Ext.decode(json);
		　　　　　　 if(Ext.type(o)==　'object')　{
		　　　　　　　　 o　=　o[this.rootName];
		　　　　　　 }
		　　　　　　 node.beginUpdate();
		　　　　　　 for(var　i=0,len=o.length;i<len;i++)　{
		　　　　　　　　 var　n=this.createNode(o[i]);
		　　　　　　　　 if(n){
		　　　　　　　　　　 node.appendChild(n);
		　　　　　　　　 }
		　　　　　　 }
		　　　　　　 node.endUpdate();
		　　　　　　 if (typeof callback == "function") {
		                callback(this, node);
		            }
		　　　　 }catch(e){
		　　　　　　 this.handleFailure(response);
		　　　　 }
		　　 }
		});
	
})();

