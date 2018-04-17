(function() {
	/**
	 * 车辆分组管理 * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.VehicleGroupManager = Ext.extend(Ext.Window, {
		constructor : function(config) {
			config = config || {};
			this.action = config["action"] || "insert";

			this.updateUrl = GAS.config["globalProxy"]
					+ "/vehicleGroup/update?_dc=" + new Date().getTime(); // 查询接口
			this.addUrl = GAS.config["globalProxy"] + "/vehicleGroup/add?_dc="
					+ new Date().getTime(); // 删除接口

			this.termTreeUrl = GAS.config["globalProxy"]
					+ "/dept/treeTerminal?_dc=" + new Date().getTime(); // 终端树形列表

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
			GAS.VehicleGroupManager.superclass.constructor.apply(this,
					arguments);
		},
		// settting title
		getwinTitle : function() {
			var title = "车辆分组管理"
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
										items : this.getVehicleTreePanel()
									}, {
										region : "center",
										frame : false,
										border : true,
										layout : "fit",
										margins : "1px",
										title : "分组表单",
										items : this.getFormPanel()
									}]
						});
			}
			return this.mianPanel;
		},
		// form面板
		getFormItems : function(formItems) {
			if (formItems) {
				var items = [];
				for (var index = 0, len = formItems.length; index < len; index++) {
					var field = formItems[index];
					if (!field) {
						return;
					}
					field.anchor = "94%";
					if (field.getXType() != "hidden") {
						var config = {
							xtype : "container",
							layout : "form",
							border : false,
							hidden : field.hidden == true ? true : false,
							labelWidth : 65,
							columnWidth : 0.5,
							width : field["width"] || 200,
							labelAlign : "right",
							style : "padding-top:2px;",
							items : [field]
						};
						if (field.getXType() == "textarea") {
							config["columnWidth"] = 1;
						}
						if (field["fullWidth"] || field["fullWidth"] == "true") {
							config["columnWidth"] = 1;
						}
						items.push(config);
					} else {
						items.push(field);
					}
				}
				this.formitems = new Ext.Container({
							border : true,
							autoHeight : true,
							layout : "column",
							items : items
						});
			}
			return this.formitems;
		},
		// 模块数据数据组件
		getVehicleTreePanel : function() {
			if (!this.verhicleTreeList) {
				this.verhicleTreeList = new GAS.baseTreePanel({
							title : "终端信息列表",
							dataUrl : this.termTreeUrl,
							expandAllNode : true,
							dataRoot : "result"
						});
			}
			return this.verhicleTreeList;
		},
		// 获取选中的node
		getVehicleSelected : function() {
			var checked = this.getVehicleTreePanel().getChecked(), newList = [];
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
						var record = this.loadRecord;
						this.groupId.setValue(record["id"]);
						this.vehicleGroupName.setValue(record["name"]);
						this.userd.setValue(record["uses"]);
						this.desc.setValue(record["remark"]);

						var vehicleList = record["vehicleList"];
						var setNodeValue = function() {
							if (vehicleList && vehicleList.length > 0) {
								Ext.each(vehicleList, function(vale) {
											var intNodeId = parseInt(vale);
											var record = this
													.getVehicleTreePanel()
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
						this.getVehicleTreePanel().on({
									scope : this,
									treeloadend : setNodeValue
								});
					}
				});
			}
		},
		getFormPanel : function() {
			if (!this.formPanel) {
				this.vehicleGroupName = new Ext.form.TextField({
							fieldLabel : "分组名称",
							name : "vehicleGroupName",
							allowBlank : false,
							fullWidth : true,
							anchor : "92%"
						});
				// 取值
				this.userd = new Ext.form.TextArea({
							rows : 4,
							fieldLabel : "用途说明",
							anchor : "97%"
						});

				// 主键
				this.groupId = new Ext.form.Hidden({
							fieldLabel : "主键",
							name : "id",
							anchor : "92%"
						});
				// code码
				this.desc = new Ext.form.TextArea({
							rows : 4,
							fieldLabel : "信息备注",
							anchor : "97%"
						});
				var list = [this.groupId, this.vehicleGroupName, this.userd,
						this.desc];
				var formItems = this.getFormItems(list);
				this.formPanel = new Ext.FormPanel({
							autoHeight : true,
							border : false,
							bodyBorder : false,
							items : formItems,
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
		// 获取form提交参数
		getSubmitParam : function() {
			var params = {};
			params["id"] = this.groupId.getValue();
			params["deviceIds"] = this.getVehicleSelected().join(",");
			params["name"] = this.vehicleGroupName.getValue();
			params["uses"] = this.userd.getValue();
			params["remark"] = this.desc.getValue();
			return params;
		},
		// 提交form验证信息
		saveRecordhandler : function() {
			var form = this.getFormPanel();
			var vehicleList = this.getVehicleSelected();
			if (vehicleList && vehicleList.length == 0) {
				Ext.Msg.alert("信息提示", "请选择车辆");
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
									|| "添加成功");
					this.windowClose();
				} else {
					Ext.Msg.alert("信息提示", result["result"] || result["msg"]
									|| "添加失败");
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
