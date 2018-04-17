(function() {

	Ext.PagingBar = Ext.extend(Ext.PagingToolbar, {
		totalPageText : "",
		curretnPageText : "{0}",
	    initComponent : function(){
	        var pagingItems = [
	        this.first = new Ext.Toolbar.Button({
	            tooltip: this.firstText,
	            overflowText: this.firstText,
	            iconCls: 'x-tbar-page-first',
	            disabled: true,
	            handler: this.moveFirst,
	            scope: this
	        }), 
	        this.prev = new Ext.Toolbar.Button({
	            tooltip: this.prevText,
	            overflowText: this.prevText,
	            iconCls: 'x-tbar-page-prev',
	            disabled: true,
	            handler: this.movePrevious,
	            scope: this
	        }),
	        this.afterTextItem = new Ext.Toolbar.TextItem({
	            text: String.format(this.curretnPageText, 1)
	        }),
	        this.next = new Ext.Toolbar.Button({
	            tooltip: this.nextText,
	            overflowText: this.nextText,
	            iconCls: 'x-tbar-page-next',
	            disabled: true,
	            handler: this.moveNext,
	            scope: this
	        }), 
	        this.last = new Ext.Toolbar.Button({
	            tooltip: this.lastText,
	            overflowText: this.lastText,
	            iconCls: 'x-tbar-page-last',
	            disabled: true,
	            handler: this.moveLast,
	            scope: this
	        }), 
	        this.refresh = new Ext.Toolbar.Button({
	            tooltip: this.refreshText,
	            overflowText: this.refreshText,
	            iconCls: 'x-tbar-loading',
	            handler: this.doRefresh,
	            scope: this
	        }),
	        this.totalPageTextItem = new Ext.Toolbar.TextItem({
	            text: this.totalPageText
	        })];
	
	        var userItems = this.items || this.buttons || [];
	        if (this.prependButtons) {
	            this.items = userItems.concat(pagingItems);
	        }else{
	            this.items = pagingItems.concat(userItems);
	        }
	        delete this.buttons;
	        Ext.PagingToolbar.superclass.initComponent.call(this);
	        this.addEvents(
	            'change',
	            'beforechange'
	        );
	        this.on('afterlayout', this.onFirstLayout, this, {single: true});
	        this.cursor = 0;
	        this.bindStore(this.store, true);
	    },
	    onLoad : function(store, r, o){
	        if(!this.rendered){
	            this.dsLoaded = [store, r, o];
	            return;
	        }
	        var d = this.getPageData(), ap = d.activePage, ps = d.pages;
	        this.afterTextItem.setText(String.format(this.curretnPageText, ap));
	        this.totalPageTextItem.setText(String.format(this.totalPageText, ps));
	        this.first.setDisabled(ap == 1);
	        this.prev.setDisabled(ap == 1);
	        this.next.setDisabled(ap == ps);
	        this.last.setDisabled(ap == ps);
	        this.refresh.enable();
	        this.updateInfo();
	        this.fireEvent('change', this, d);
	    }
	});

})();
