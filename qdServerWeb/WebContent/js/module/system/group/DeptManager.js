(function() {
	// 组织结构管理模块
	/**
	 * * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.DeptManager = Ext.extend(Ext.Window, {
		constructor : function(config) {
			config = config || {};
			this.rootNodeId = 1; // 树形根节点
			this.action = config["action"] || "insert"; // 操作类型 是添加还是修改
			this.updateUrl = GAS.config["globalProxy"] + "/dept/update?_dc="
					+ new Date().getTime(); // 修改的URL地址
			this.addUrl = GAS.config["globalProxy"] + "/dept/add?_dc="
					+ new Date().getTime(); // 添加的URL地址
			this.comboxTreeUrl = GAS.config["globalProxy"] + "/dept/tree?_dc="
					+ new Date().getTime(); // 树形菜单
			this.loadRecord = config["loadRecord"] || {};
			Ext.apply(this, {
						border : false,
						frame : true,
						layout : "fit",
						width : 480,
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
			GAS.DeptManager.superclass.constructor.apply(this, arguments);
		},
		// settting title
		getwinTitle : function() {
			var title = "部门管理"
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
							layout : "fit",
							items : this.getFormPanel()
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
					field.anchor = "92%";
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
							field.anchor = "96%";
						}
						if (field["fullWidth"] || field["fullWidth"] == "true") {
							config["columnWidth"] = 1;
							field.anchor = "96%";
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
		// 反填数据
		loadForm : function() {
			if (this.action == "update") {
				var record = this.loadRecord;
				var id = record["id"], parentId = record["parentId"], duty = record["duty"], remark = record["remark"], name = record["name"], director = record["director"], sortNum = record["sortNum"], parentName = record["parentName"];

				this.deptid.setValue(id); // 部门 主键
				this.deptName.setValue(name); // 部门编号
				this.duty.setValue(duty);// 职责
				this.upDept.setHiddenValue(parentId, parentName);
				this.director.setValue(director), // 责任人
				this.sortNum.setValue(sortNum), // 排列次序
				this.remark.setValue(remark);

			}
		},
		getFormPanel : function() {
			if (!this.formPanel) {
				// 部门名称
				this.deptName = new Ext.form.TextField({
							fieldLabel : "部门名称",
							name : "deptname",
							allowBlank : false,
							anchor : "92%"
						});
				// 序号
				this.sortNum = new Ext.form.NumberField({
							fieldLabel : "排列序号",
							name : "sortNum",
							allowBlank : false,
							anchor : "92%"
						});
				// 部门职责
				this.duty = new Ext.form.TextField({
							fieldLabel : "部门职责",
							name : "deptcount",
							anchor : "92%"
						});
				// 责任人
				this.director = new Ext.form.TextField({
							fieldLabel : "负责人",
							name : "deptcode",
							anchor : "92%"
						});
				// 上级部门
				this.upDept = new Ext.form.ComboBoxTree({
							triggerAction : "all",
							fieldLabel : "上级部门",
							displayField : "text",
							valueField : "id",
							autoLoad : true,
							dataRoot : "result",
							allowBlank : false,
							dataUrl : this.comboxTreeUrl, // 下拉树形菜单URL地址
							baseParams : {
								id : this.rootNodeId
								// 请求参数
							}
						});

				// 主键
				this.deptid = new Ext.form.Hidden({
							fieldLabel : "主键",
							name : "id",
							anchor : "92%"
						});
				// code码
				this.remark = new Ext.form.TextArea({
							rows : 4,
							fieldLabel : "信息备注",
							anchor : "97%"
						});
				var list = [this.deptid, // 部门 主键
						this.deptName, // 部门编号
						this.duty,// 职责
						this.upDept, // 上级部门
						this.director, // 责任人
						this.sortNum, // 排列次序
						this.remark];
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
		// tijiao params
		getSubmitParam : function() {
			var params = {};
			if (this.action == "update") {
				params["id"] = this.deptid.getValue(); // 部门 主键
			}
			params["name"] = this.deptName.getValue();// 部门名称
			params["duty"] = this.duty.getValue(); // 职责
			params["director"] = this.director.getValue(); // 责任人
			params["sortNum"] = this.sortNum.getValue(); // 序号
			params["parentId"] = this.upDept.getHiddenValue(); // 上级部门ID
			params["remark"] = this.remark.getValue(); // 备注信息
			return params;
		},
		saveRecordhandler : function() {
			var form = this.getFormPanel();
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
				if (result["success"]) {
					this.fireEvent("submit"); // 激发submit方法
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
