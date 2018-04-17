(function() {
	// 用户管理
	/**
	 * * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config QQ:1023553356
	 */
	GAS.UserManager = Ext.extend(Ext.Window, {
		constructor : function(config) {
			config = config || {};
			this.action = config["action"] || "insert";
			this.rootNodeId = 1; // 树形根节点
			this.updateUrl = GAS.config["globalProxy"] + "/user/update?_dc="
					+ new Date().getTime(); // 修改的URL地址
			this.addUrl = GAS.config["globalProxy"] + "/user/add?_dc="
					+ new Date().getTime(); // 添加的URL地址
			this.comboxTreeUrl = GAS.config["globalProxy"] + "/dept/tree?_dc="
					+ new Date().getTime(); // 树形菜单
			this.loadRecord = config["loadRecord"] || {};

			this.userRoleUrl = GAS.config["globalProxy"] + "/role/list?_dc="
					+ new Date().getTime(); // 查询接口

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
			GAS.UserManager.superclass.constructor.apply(this, arguments);
		},
		// settting title
		getwinTitle : function() {
			var title = "用户管理"
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
							labelWidth : 80,
							columnWidth : 0.5,
							width : field["width"] || 230,
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
				this.on({
							scope : this,
							show : function() {
								var record = this.loadRecord;
								this.userId.setValue(record["id"]); // 用户ID
								this.useraccount.setValue(record["account"]); // 账号
								this.userPhone.setValue(record["contact"]); // 联系方式
								this.realname.setValue(record["name"]);
								this.upDept.setHiddenValue(record["deptId"],
										record["deptName"]); // 上级部门
								this.UserRole.getStore().on({
									scope : this,
									load : function() {
										this.UserRole
												.setValue(record["roleId"]); // 用户角色
									}
								});
								this.descEield.setValue(record["id"]);
							}
						})
			}
		},
		getFormPanel : function() {
			if (!this.formPanel) {
				this.useraccount = new Ext.form.TextField({
							fieldLabel : "用户账号",
							name : "deptname",
							allowBlank : false,
							anchor : "92%"
						});
				this.userpassword = new Ext.form.TextField({
							fieldLabel : "用户密码",
							name : "deptname",
							inputType : 'password',
							allowBlank : this.action == "insert" ? false : true,
							disabled : this.action == "insert" ? false : true,
							anchor : "92%"
						});

				// 真实姓名
				this.realname = new Ext.form.TextField({
							fieldLabel : "真实姓名",
							name : "name",
							anchor : "92%"
						});

				// 用户电话
				this.userPhone = new Ext.form.TextField({
							fieldLabel : "用户电话",
							name : "contact",
							anchor : "92%"
						});

				// 部门代码
				this.email = new Ext.form.TextField({
							fieldLabel : "用户邮箱",
							name : "deptcode",
							anchor : "92%"
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
							dataUrl : this.comboxTreeUrl, // 下拉树形菜单URL地址
							baseParams : {
								id : this.rootNodeId
								// 请求参数
							}
						});

				// 主键
				this.userId = new Ext.form.Hidden({
							fieldLabel : "主键",
							name : "id",
							anchor : "92%"
						});

				// 用户角色
				this.UserRole = new Ext.form.ComboBox({
							fieldLabel : "用户角色",
							title : "用户角色",
							allowBlank : false,
							typeAhead : true,
							triggerAction : 'all',
							lazyRender : true,
							mode : 'local',
							editable : false,
							store : new Ext.data.JsonStore({
										autoLoad : true,
										fields : ["id", "name"],
										root : 'data',
										totalProperty : 'total',
										proxy : new Ext.data.HttpProxy({
													url : this.userRoleUrl
												})
									}),
							valueField : 'id',
							displayField : 'name'
						});
				// //启用禁用
				// this.disenabled =new Ext.form.ComboBox({
				// fieldLabel :"启用",
				// title :"启用禁用",
				// typeAhead: true,
				// triggerAction: 'all',
				// lazyRender:true,
				// mode : 'local',
				// store : new Ext.data.JsonStore({
				// fields : ["value","text"],
				// root : 'data',
				// data : {
				// data:[{
				// value: "1",
				// text : "启用"
				// },{
				// value: "0",
				// text : "禁用"
				// }]
				// }
				// }),
				// value : true,
				// valueField: 'value',
				// displayField: 'text'
				// });
				// code码
				this.descEield = new Ext.form.TextArea({
							rows : 4,
							fieldLabel : "信息备注",
							anchor : "97%"
						});
				var list = [
						this.userId, // 用户ID
						this.useraccount, this.userpassword, this.userPhone,
						this.realname, this.upDept, this.UserRole,
						// this.disenabled,
						this.descEield];
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
		getSubmitParam : function() {
			var params = {};
			params["id"] = this.userId.getValue();
			params["account"] = this.useraccount.getValue();
			params["passwd"] = this.userpassword.getValue();
			// params["isUsed"] = this.disenabled.getValue();
			params["name"] = this.realname.getValue();
			params["contact"] = this.userPhone.getValue();
			params["roleId"] = this.UserRole.getValue();
			params["deptId"] = this.upDept.getHiddenValue();
			params["remark"] = this.descEield.getValue();
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
