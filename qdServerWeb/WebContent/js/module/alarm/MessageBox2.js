(function() {

	// GAS.AlarmWindowBox = Ext.extend(Ext.Window, {
	// constructor: function(config){
	// config = config || {};
	// this.positions = []
	// Ext.apply(this, {
	// layout :'fit',
	// modal : false,
	// plain : true,
	// shadow : false, //去除阴影
	// draggable: false, //默认不可拖拽
	// resizable: false,
	// closable : true,
	// autoHide : 4,
	// closeAction:'hide' //默认关闭为隐藏
	// });
	// this.task = new Ext.util.DelayedTask(this.hide, this);
	// GAS.AlarmWindowBox.superclass.constructor.apply(this,arguments);
	// },
	// setMessage: function(msg){
	// this.body.update(msg);
	// },
	// setTitle: function(title, iconCls){
	// GAS.AlarmWindowBox.superclass.setTitle.call(this, title, iconCls ||
	// this.iconCls);
	// },
	// onRender:function(ct, position) {
	// GAS.AlarmWindowBox.superclass.onRender.call(this, ct, position);
	// },
	// onDestroy: function(){
	// this.positions.remove(this.pos);
	// GAS.AlarmWindowBox.superclass.onDestroy.call(this);
	// },
	// afterShow: function(){
	// GAS.AlarmWindowBox.superclass.afterShow.call(this);
	// this.on('move', function(){
	// this.positions.remove(this.pos);
	// this.task.cancel();
	// }, this);
	// var delay = this.autoHide * 1000;
	// this.task.delay(delay);
	// },
	// animShow: function(){
	// this.pos = 0;
	// while(this.positions.indexOf(this.pos)>-1)
	// this.pos++;
	// this.positions.push(this.pos);
	// this.el.alignTo(document, "br-br", [-2,
	// -24-((this.getSize().height+10)*this.pos)]);
	// this.el.slideIn('b', {
	// duration: 2,
	// callback: this.afterShow,
	// scope: this
	// });
	// },
	// animHide: function(){
	// this.positions.remove(this.pos);
	// this.el.ghost("b", {
	// duration: 2,
	// remove: true,
	// scope: this,
	// callback: this.destroy
	// });
	// }
	// });

	GAS.Test = function() {
		var alarm = new GAS.AlarmWindowBox({
					html : '测试信息',
					width : 200,
					height : 120,
					title : "报警信息"
				});
		alarm.show(document);
	}

})();