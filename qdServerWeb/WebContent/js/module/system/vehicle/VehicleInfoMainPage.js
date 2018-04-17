(function() {
	// 轨迹查询主界面
	GAS.VehicleInfoQueryMainPage = Ext.extend(Ext.Panel, {
				constructor : function(config) {
					config = config || {};
					this.rootDeptId = 1; // 部门根节点
					this.deptListUrl = GAS.config["globalProxy"]
							+ "/dept/tree?_dc=" + new Date().getTime(); // 树形菜单
					this.userQueryUrl = "js/module/system/vehicle/VehicleInfoQuery.js"; // 集团信息列表
					Ext.apply(this, {
								border : false,
								frame : false,
								layout : "fit",
								items : this.getMainPanel()
							});
					GAS.VehicleInfoQueryMainPage.superclass.constructor.apply(
							this, arguments);
				},
				// 主面板
				getMainPanel : function() {
					if (!this.mianPanel) {
						this.mianPanel = new Ext.Panel({
									border : false,
									frame : false,
									layout : "border",
									items : [{
												border : true,
												frame : false,
												layout : "fit",
												width : 200,
												margins : "1px 2px 1px 1px",
												items : this.getBusTreePanel(),
												region : "west"
											}, {
												border : true,
												frame : false,
												layout : "fit",
												margins : "1px",
												items : this.getCenterPanel(),
												region : "center"
											}]
								});
					}
					return this.mianPanel;
				},
				// 中间面板
				getCenterPanel : function() {
					if (!this.centerPanel) {
						this.centerPanel = new Ext.Panel({
									border : false,
									frame : false,
									layout : "fit",
									width : 200,
									margins : "1px",
									listeners : {
										scope : this,
										afterrender : function() {
											this.showQueryGroupList(); // 显示集团信息列表
										}
									}
								});
					}
					return this.centerPanel
				},
				// 显示集团查询信息列表
				showQueryGroupList : function(node) {
					var jsUrl = this.userQueryUrl;
					GAS.importlib(jsUrl, function() {
								this.vehicleInfoQuery = new GAS.VehicleInfoQuery(
										{
											border : false,
											frame : false,
											layout : "fit"
										});
								this.addNewModulePanel(this.vehicleInfoQuery);
							}, this);
				},
				// 显示新的功能模块
				addNewModulePanel : function(newModule) {
					this.getCenterPanel().removeAll();
					this.getCenterPanel().add(newModule);
					this.getCenterPanel().doLayout();
				},
				// 左侧树形面板
				getBusTreePanel : function() {
					if (!this.orgList) {
						this.orgList = new GAS.baseTreePanel({
									dataRoot : "result",
									title : "组织机构列表",
									dataUrl : this.deptListUrl,
									expandAllNode : true,
									baseParams : {
										id : this.rootDeptId
									}
								});
					}
					this.orgList.on({
								scope : this,
								click : this.doqueryGridHandler
							});
					return this.orgList;
				},
				// 点击树形节点查询节点下面的数据
				doqueryGridHandler : function(node) {
					var possbackParame = node.attributes["id"];
					var isRoot = node.parentNode["isRoot"];
					if (isRoot) {
						possbackParame = "";
					}
					if (this.vehicleInfoQuery) {
						this.vehicleInfoQuery
								.onTreeClickhandler(possbackParame);
					}
				}
			});

})();
