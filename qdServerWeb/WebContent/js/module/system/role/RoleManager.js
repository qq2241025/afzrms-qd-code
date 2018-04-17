(function() {
	// 角色管理
	/**
	 * * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.RoleManager = Ext.extend(Ext.Window, {
		constructor : function(config) {
			config = config || {};
			this.action = config["action"] || "insert";
			this.rootNodeId = 1;
			this.modulePID = 1; // 模块根节点
			this.updateUrl = GAS.config["globalProxy"] + "/role/update?_dc="
					+ new Date().getTime(); // 修改的URL地址
			this.addUrl = GAS.config["globalProxy"] + "/role/add?_dc="
					+ new Date().getTime(); // 添加的URL地址
			this.comboxTreeUrl = GAS.config["globalProxy"] + "/dept/tree?_dc="
					+ new Date().getTime(); // 部门下拉列表
			this.moduleTreeUrl = GAS.config["globalProxy"]
					+ "/module/tree?_dc=" + new Date().getTime(); // 模块菜单

			this.loadRecord = config["loadRecord"] || {};
			Ext.apply(this, {
						border : false,
						frame : true,
						layout : "fit",
						width : 520,
						height : 320,
						modal : true,
						contrain : false,
						resize : false,
						title : this.getwinTitle(),
						closeAction : "hide",
						items : this.getMainPanel(),
						buttonAlign : "center",
						buttons : this.getActionBtn()
					});
			this.addEvents("submit");
			GAS.RoleManager.superclass.constructor.apply(this, arguments);
		},
		// settting title
		getwinTitle : function() {
			var title = "角色管理"
			if (this.action == "insert") {
				title += "-" + "添加"
			} else {
				title += "-" + "修改"
			}
			return title;
		},
		// 主面板
		getMainPanel : function() {
			if (!this.mianPanel) {
				this.mianPanel = new Ext.Panel({
							border : false,
							frame : false,
							layout : "border",
							items : [{
										region : "west",
										frame : false,
										border : true,
										width : 200,
										layout : "fit",
										margins : "1px",
										items : this.getModuleTree()
									}, {
										region : "center",
										frame : false,
										border : true,
										layout : "fit",
										margins : "1px",
										title : "角色表单",
										items : this.getFormPanel()
									}]
						});
			}
			return this.mianPanel;
		},
		// 模块数据数据组件
		getModuleTree : function() {
			if (!this.moduleList) {
				this.moduleList = new GAS.baseTreePanel({
							title : "模块列表",
							dataUrl : this.moduleTreeUrl,
							dataRoot : "result",
							expandAllNode : true,
							baseParams : {
								id : this.modulePID
							}
						});
				this.moduleList.on({
					scope : this,
					checkchange : function(node, checked) {
						node.expand();
						node.attributes.checked = checked;
						node.on('expand', function(node) {
									node.eachChild(function(child) {
												child.ui.toggleCheck(checked);
												child.attributes.checked = checked;
												child.fireEvent('checkchange',
														child, checked);
											});
								});
						node.eachChild(function(child) {
									child.ui.toggleCheck(checked);
									child.attributes.checked = checked;
									child.fireEvent('checkchange', child,
											checked);
								});
					}
				});
			}
			return this.moduleList;
		},
		// 获取选中的node
		getModuleSelected : function() {
			var checked = this.getModuleTree().getChecked(), newList = [];
			if (checked && checked.length > 0) {
				for (var i = 0; i < checked.length; i++) {
					var record = checked[i];
					var nodeId = record.attributes["id"];
					newList.push(nodeId);
				}
			}
			return newList;
		},
		// 反填数据
		loadForm : function() {
			if (this.action == "update") {
				this.on({
					scope : this,
					show : function() {
						var record = this.loadRecord;
						this.roleId.setValue(record["id"]);
						this.roleName.setValue(record["name"]);
						this.upDept.setHiddenValue(record["deptId"],
								record["deptName"]); // 上级部门
						this.roleType.setValue(record["roleType"]);
						this.descript.setValue(record["description"]);
						this.remark.setValue(record["remark"]);

						var moduleList = record["moduleList"];
						var setNodeValue = function() {
							if (moduleList && moduleList.length > 0) {
								Ext.each(moduleList, function(vale) {
											var intNodeId = parseInt(vale);
											var record = this
													.getModuleTree()
													.findNodeByTargetId(intNodeId);
											if (record && record.getUI) {
												if (record.ui.checkbox) {
													record.ui.checkbox.checked = true;
												}
												record.attributes.checked = true;
											}
										}, this);
							}
						}
						this.getModuleTree().on({
									scope : this,
									treeloadend : setNodeValue
								});
					}
				});
			}
		},
		// form表单
		getFormPanel : function() {
			if (!this.formPanel) {
				this.roleName = new Ext.form.TextField({
							fieldLabel : "角色名称",
							name : "roleName",
							allowBlank : false,
							anchor : "95%",
							style : "margin-top:2px;"
						});
				// 主键
				this.roleId = new Ext.form.Hidden({
							fieldLabel : "主键",
							name : "id",
							anchor : "95%"
						});
				// 上级部门
				this.upDept = new Ext.form.ComboBoxTree({
							triggerAction : "all",
							title : "所属部门",
							fieldLabel : "所属部门",
							displayField : "text",
							valueField : "id",
							autoLoad : true,
							dataRoot : "result",
							anchor : "95%",
							style : "padding-top:2px;",
							dataUrl : this.comboxTreeUrl, // 下拉树形菜单URL地址
							baseParams : {
								id : this.rootNodeId
								// 请求参数
							}
						});

				// 启用禁用
				this.roleType = new Ext.form.ComboBox({
							fieldLabel : "管理员类型",
							title : "管理员类型",
							typeAhead : true,
							style : "padding-top:2px;",
							anchor : "95%",
							triggerAction : 'all',
							lazyRender : true,
							mode : 'local',
							store : new Ext.data.JsonStore({
										fields : ["value", "text"],
										root : 'data',
										data : {
											data : [{
														value : true,
														text : "系统管理员"
													}, {
														value : false,
														text : "普通管理员"
													}]
										}
									}),
							value : true,
							valueField : 'value',
							displayField : 'text'
						});
				// 信息备注
				this.descript = new Ext.form.TextArea({
							rows : 3,
							fieldLabel : "角色描述",
							anchor : "95%"
						});
				// 信息备注
				this.remark = new Ext.form.TextArea({
							rows : 4,
							fieldLabel : "信息备注",
							anchor : "95%"
						});
				var list = [
						this.roleId, // 用户ID
						this.roleName, this.roleType, this.upDept,
						this.descript, this.remark];
				this.formPanel = new Ext.FormPanel({
							autoHeight : true,
							border : false,
							bodyBorder : false,
							items : list,
							labelWidth : 80,
							labelAlign : "right",
							listeners : {
								scope : this,
								render : this.loadForm
							}
						});
			}
			return this.formPanel;
		},
		// 关闭
		windowClose : function() {
			this.close();
		},
		// 操作按钮
		getActionBtn : function() {
			var btnList = [], cancle = {
				text : "关闭",
				scope : this,
				handler : this.windowClose
			};
			if (this.action == "update") {
				btnList.push({
							text : "修改记录",
							scope : this,
							handler : this.saveRecordhandler
						});
			} else {
				btnList.push({
							text : "保存记录",
							scope : this,
							handler : this.saveRecordhandler
						});
			}
			btnList.push(cancle);
			return btnList;
		},
		// 获取提交的参数
		getSubmitParam : function() {
			var params = {};
			params["id"] = this.roleId.getValue();
			params["name"] = this.roleName.getValue();
			params["deptId"] = this.upDept.getHiddenValue();
			params["description"] = this.descript.getValue();
			params["remark"] = this.remark.getValue();
			params["moduleIds"] = this.getModuleSelected().join(",");
			return params;
		},
		saveRecordhandler : function() {
			var form = this.getFormPanel();
			var modList = this.getModuleSelected();
			if (modList && modList.length == 0) {
				Ext.Msg.alert("信息提示", "请选择功能模块");
				return;
			}
			if (!form || !form.getForm().isValid()) {
				return;
			}
			var params = this.getSubmitParam();
			Ext.Msg.show({
						title : '请确认操作？',
						msg : '确认想要保存记录吗?',
						buttons : Ext.Msg.YESNO,
						icon : Ext.MessageBox.QUESTION,
						scope : this,
						fn : function(btn, text) {
							if (btn == 'yes') {
								this.onSubmit(params);
							}
						}
					});
		},
		// ajax提交参数
		onSubmit : function(params) {
			var callback = function(res, req) {
				var result = Ext.decode(res.responseText);
				if (result["success"] && result["success"] == "true") {
					this.fireEvent("submit");
					Ext.Msg.alert("信息提示", result["result"] || result["msg"]
									|| "提交成功");
					this.windowClose();
				} else {
					Ext.Msg.alert("信息提示", result["result"] || result["msg"]
									|| "提交失败");
				}
			};
			GAS.AOP.send({
						url : this.action == "update"
								? this.updateUrl
								: this.addUrl,
						params : params,
						scope : this,
						callbackFn : callback
					});
		}
	});

})();
