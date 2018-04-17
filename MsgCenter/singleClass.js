var _instance = null;

module.exports = function(obj){
	function Class(obj){
		this.obj = obj;
	}
	Class.prototype = {
		constructor: Class,
		show: function(){
			console.log("this.obj="+this.obj);
		},
		getObj: function(){
			return this.obj;
		}
	}
	this.getInstance = function(){
		if(_instance == null){
			_instance = new Class(obj);
		}
		return _instance;
	}
}