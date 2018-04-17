(function() {
	Ext.form.CheckCode = Ext.extend(Ext.BoxComponent, {
				constructor : function(config) {
					this.config = config;
					Ext.apply(this, config);
					Ext.form.CheckCode.superclass.initComponent.call(this,
							config);
					this.addEvents('click');
				},
				getUrl : function() {
					return this.config["url"];
				},
				onRender : function(ct, position) {
					if (!this.el) {
						this.el = document.createElement('img');
						if (this.config["style"]) {
							Ext.apply(this.el.style, this.config["style"]);
						} else {
							this.el.style.margin = "1px 0px 2px 5px";
						}
						this.el.src = this.getSrc();
					}
					Ext.form.Label.superclass.onRender.call(this, ct, position);
				},
				getSrc : function() {
					return this.getUrl() + '?_dc=' + new Date().getTime();
				},
				afterRender : function() {
					Ext.form.CheckCode.superclass.afterRender.call(this);
					this.el.on("click", this.onClick, this);
				},
				onClick : function() {
					this.el.set({
								src : this.getSrc()
							});
				}
			});
	Ext.reg('checkCode', Ext.form.CheckCode);

})();