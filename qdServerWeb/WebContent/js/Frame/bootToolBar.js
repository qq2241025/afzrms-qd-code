(function() {

	GAS.StatusBar = Ext.extend(Ext.Toolbar, {
		constructor : function(config) {
			// 调用基类构造函数
			this.statHeight = 23;
			this.settingIcon = GAS.iconPath + "set.gif";
			this.copyRight = GAS.config["copyright"];
			this.settingClass = "settingAlarm";
			Ext.apply(this, {
				height : this.statHeight,
				border : false,
				frame : false,
				items : [this.getcomback(), this.getSystime(), '->',
						// this.getSettingAlarm(),
						this.getCopyright()],
				listeners : {
					scope : this,
					afterrender : function(panel) {
						var container = panel.container, me = this;
						container.on({
									scope : this,
									mousedown : function(e, t) {
										var flg = Ext.fly(t);
										if (flg
												&& flg
														.hasClass(this.settingClass)) {
											me.doAlarmSettinghandler(); // 报警设置
										}
									}
								});
					}
				}
			});
			GAS.StatusBar.superclass.constructor.call(this, config);
		},
		// 报警弹框设置
		doAlarmSettinghandler : function() {
			var setAv = GAS.Cookie.getCookie("AlarmSetting"), checked = false;
			if (setAv || setAv == "true") {
				checked = true;
			}
			this.checkBox = new Ext.form.Checkbox({
						boxLabel : "每次弹出消息",
						anchor : "90%",
						inputValue : checked,
						style : "margin-left:15px",
						checked : checked
					});
			this.Alarmwin = new Ext.Window({
						title : "报警设置",
						iconCls : "icon-set",
						width : 190,
						height : 90,
						border : false,
						modal : true,
						contrain : false,
						resize : false,
						items : this.checkBox,
						buttonAlign : "center",
						buttons : [{
									handler : this.sureHandler,
									width : 60,
									scope : this,
									text : "确定"
								}, {
									handler : this.cancleHandler,
									scope : this,
									width : 60,
									text : "取消"
								}]
					});
			this.Alarmwin.show();
		},
		// 确定
		sureHandler : function() {
			var check = this.checkBox.getValue();
			GAS.Cookie.setCookie("AlarmSetting", check);
			if (this.Alarmwin) {
				this.Alarmwin.close();
			}
		},
		// 取消
		cancleHandler : function() {
			if (this.Alarmwin) {
				this.Alarmwin.close();
			}
		},
		// 版权设置
		getCopyright : function() {
			if (!this.copyrightText) {
				this.copyrightText = "<span style='margin-left:4px;'>"
						+ this.copyRight + "</span>";
			}
			return this.copyrightText;
		},
		// 报警弹框设置
		getSettingAlarm : function() {
			if (!this.setMsgWindowImg) {
				var title = "报警弹窗设置";
				this.setMsgWindowImg = "<img style='cursor:pointer;margin-left:4px;margin-right:1px;' class='"
						+ this.settingClass
						+ "' alt='"
						+ title
						+ "' title='"
						+ title + "' src=" + this.settingIcon + ">";
			}
			return this.setMsgWindowImg;
		},
		getcomback : function() {
			if (!this.comback) {
				this.comback = new Ext.form.Label({
							width : 48,
							height : this.statHeight,
							text : "欢迎回来"
						});
			}
			return this.comback;
		},
		getSystime : function() {
			if (!this.systime) {
				this.systime = new Ext.form.Label({
					width : 180,
					height : this.statHeight,
					listeners : {
						render : function(lab) {
							var task = {
								run : function() {
									var time = new Date().format('Y-m-d H:i:s');
									lab.setText(time);
									lab.getEl().setStyle("color", "blue");
								},
								interval : 1000
							};
							Ext.TaskMgr.start(task);
						}
					}
				});
			}
			return this.systime;
		}
	});
})();
