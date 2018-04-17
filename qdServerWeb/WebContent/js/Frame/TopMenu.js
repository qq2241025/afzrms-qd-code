(function() {
	// 加载的菜单功能
	GAS.TopMenu = Ext.extend(Ext.Panel, {
		constructor : function(config) {
			this.LogoUrl = GAS.config.topUrl;
			this.rootRoleId = "1"; // 属性菜单root节点
			this.loadMenuUrl = GAS.config["globalProxy"]
					+ "/module/treeWithRole?_dc=" + new Date().getTime();
			Ext.apply(this, {
						layout : "fit",
						frame : false,
						border : false,
						items : this.getMainPanel()
					});
			this.addEvents("menuClick");
			GAS.TopMenu.superclass.constructor.apply(this, arguments);
		},
		getMainPanel : function() {
			if (!this.mainpanel) {
				this.mainpanel = new Ext.Panel({
							frame : false,
							border : false,
							margins : '1 0 1 1',
							height : 30,
							tbar : this.getMenubar(),
							listeners : {
								scope : this,
								render : this.LoadMenu
							}
						});
			}
			return this.mainpanel;
		},
		getMenubar : function() {
			if (!this.menuBar) {
				this.menuBar = new Ext.Toolbar({
							items : [
									'<img src=' + this.LogoUrl
											+ ' style= "display:block" />',
									"->"]
						});
			}
			return this.menuBar;
		},
		LoadMenu : function() {
			GAS.AOP.send({
				url : this.loadMenuUrl,
				params : {
					id : this.rootRoleId
				},
				scope : this,
				callbackFn : function(res, req) {
					res = Ext.decode(res.responseText) || {};
					var result = res["result"] || [], other = this
							.loadOtherData();
					var newList = [];
					try {
						var rootList = this.eachTreeNode(result); // 组合菜单
						if (rootList && rootList.length > 0) {
							var rootData = rootList[0], child = rootData["menu"]
									|| [];
							var newList = [];
							newList.push(child);
							newList.push(other[0]);
							newList.push(other[1]);
							this.getMenubar().addButton(newList);
							this.getMainPanel().doLayout();
						}
					} catch (e) {
						newList.push(other[0]);
						newList.push(other[1]);
						this.getMenubar().addButton(newList);
						this.getMainPanel().doLayout();
					}
				}
			});
		},
		// 地递归发遍历
		eachTreeNode : function(nodeList) {
			var arraylist = [];
			if (Ext.isArray(nodeList) && nodeList.length > 0) {
				for (var index = 0; index < nodeList.length; index++) {
					var data = nodeList[index], recordId = data["id"], pid = data["parentId"];
					data["modulename"] = data["jsClassname"];
					data["modulepath"] = data["urlPath"];
					data["text"] = data["text"] || data["name"];
					data["data"] = data;
					data["scope"] = this;
					delete data["checked"]; // 删除属性
					var child = data["children"];
					if (child && child.length > 0) {
						var newList = this.eachTreeNode(child);
						data["menu"] = newList;
					} else {
						data["handler"] = this.onTopMenuClick;
					}
					arraylist.push(data);
				}
			}
			return arraylist;
		},
		loadOtherData : function() {
			var newlist = [];
			newlist.push({
						xtype : 'button',
						iconCls : "icon-theme",
						text : '主题风格',
						menu : [{
									xtype : 'menuitem',
									iconCls : "icon-gray",
									text : '灰色主题',
									themestyle : GAS.config["grayTheme"],
									themeName : "gray",
									handler : this.onChangerTheme
								}, {
									xtype : 'menuitem',
									iconCls : "icon-purple",
									text : '紫色主题',
									themestyle : GAS.config["purpleTheme"],
									themeName : "purple",
									handler : this.onChangerTheme
								}, {
									xtype : 'menuitem',
									iconCls : "icon-default",
									themeName : "default",
									themestyle : GAS.config["defaultTheme"],
									text : '默认风格',
									handler : this.onChangerTheme
								}]
					}, {
						xtype : 'button',
						iconCls : "icon-exit",
						text : '注销登陆',
						menu : [{
									xtype : 'menuitem',
									text : '注销登陆',
									scope : this,
									handler : GAS.MainLog.LoginOut
								}, {
									xtype : 'menuitem',
									text : '修改密码',
									scope : this,
									handler : GAS.MainLog.ChangePassword
								}]
					});
			return newlist;
		},
		// 主题设置
		onChangerTheme : function(menu) {
			var themestyle = menu["themestyle"];
			var themeKey = GAS.config["themeKeyField"];
			GAS.MainLog.themeSetting(themeKey, themestyle);
		},
		// 菜单点击数据
		onTopMenuClick : function(menu) {
			var module = menu["data"];
			this.fireEvent("menuClick", module, menu);
		}
	});

})();