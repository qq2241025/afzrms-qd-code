(function(){
	//区域报警查询
	/**
	 * * zhengang.he@autonavi.com
	 * @param {Object} config
	 * QQ:1023553356
	 */
	GAS.AreaBindSetting = Ext.extend(Ext.Window,{
	      constructor : function(config) {
	      	    config =config || {};
	      	    this.pageSize = 20;
	      	    this.action = config["action"] || "insert";        
		     	this.areaListUrl      = GAS.config["globalProxy"]+ "/alarmArea/list?_dc="+new Date().getTime();
		     	this.addAreaRuleUrl   = GAS.config["globalProxy"]+ "/alarmruleArea/add?_dc="+new Date().getTime();
		     	this.updateAreaRuleUrl= GAS.config["globalProxy"]+ "/alarmruleArea/update?_dc="+new Date().getTime();
		     	
		     	this.devideList = config["devideList"] || [];
		     	this.loadRecord = config["loadRecord"] || {};
		     	this.initAreaList(this.loadRecord["areas"]); //数据局整理list-> map
				Ext.apply(this, {
					border : false,
					frame  : true,
					layout : "fit",
					width  : 750,
					height : 400,
					modal  : true,
					contrain : false,
					resize : false,
					maximizable : false,
					title  : this.getwinTitle(),
					closeAction : "hide",
					items  : this.getMainPanel(),
					buttonAlign:"center",
					buttons: this.getActionBtn()
				});
				GAS.AreaBindSetting.superclass.constructor.apply(this, arguments);
		   },
		   initAreaList:function(list){
		   	   this.areaList = {};
		   	   if(list && list.length > 0){
		   	      for (var i = 0; i < list.length; i++) {
		       	     var record = list[i],recordId = record["alarmAreaId"];
		       	     this.areaList[recordId] = record;
		       	  }
		   	   }
		   },
		   // settting title 
		   getwinTitle:function(){
		        var title = "区域规则绑定设置"
		   	    if(this.action=="insert"){
		   	        title += "-"+"添加"
		   	    }else{
		   	        title += "-"+"修改"
		   	    }
		   	    return title;
		   },
		   //关闭
		   windowClose:function(){
			    this.close();
		   },  
		   getActionBtn:function(){
			   var btnList = [],cancle ={
				   text :　"关闭页面",
				   scope: this,
				   handler : this.windowClose
			   };
		       btnList.push({
				   text :　"保存设置",
				   scope: this,
				   handler : this.saveRecordhandler
			   });
			   btnList.push(cancle);
			   return btnList;
			},
			//form面板
		   getFormPanel:function(){
				if(!this.formPanel){
					  this.ruleName = new Ext.form.TextField({
					      fieldLabel: "规则名称",
					      name      : "areaName",
					      width     : 220,
					      allowBlank: false,
					      anchor    : "92%"
					  });
					  //主键
					  this.ruleId = new Ext.form.Hidden({
					  	  fieldLabel: "主键",
					  	  name   : "id",
					  	  width  : 220,
					  	  anchor : "92%"
					  });
					  //code码
					  this.remark = new Ext.form.TextArea({
					  	  rows      : 8,
					  	  fieldLabel: "信息备注",
					  	  anchor : "92%",
					  	  style  : "height:140px"
					  });
					  var list = [
					      this.ruleName,
						  this.ruleId,
						  this.remark
					  ];
					  this.formPanel = new Ext.FormPanel({
							border		: false,
							bodyBorder	: false,
							layout      : "form",
							labelWidth  : 55,
							defaults    : {width: 230},
							bodyStyle   :'padding:3px 2px 0',
							labelAlign  : "right",
							items		: list,
							listeners: {
							   scope:this,
							   render : this.loadForm 
							}
				  });
				}
				return this.formPanel;
		   },
		   //反填数据
		   loadForm : function(){
				if (this.action == "update"){
			    	 var result = this.loadRecord;
			    	 this.ruleId.setValue(result["id"]);
				     this.ruleName.setValue(result["name"]);
					 this.remark.setValue(result["remark"]);
				}
		   },
		   //主面板
		   getMainPanel:function(){
			    if(!this.mianPanel){
			        this.mianPanel = new Ext.Panel({
			          	border : false,
						frame  : false,
						layout : "border",
						items  : [{
						   border: true,
						   frame : false,
						   region: "west",
						   layout: "fit",
						   margins : "1px",
						   width : "230px",
						   title : "规则表单",
						   items : this.getFormPanel()
						},{
						   border: true,
						   frame : false,
						   region: "center",
						   layout: "fit",
						   margins : "1px",
						   title : "区域设置",
						   items : this.getGridPanel()
						}]
			        });
			    }
			   return  this.mianPanel;
		   },
		   getGridPanel : function() {
			  if(!this.gridPanel) {
				  this.gridPanel = new Ext.grid.GridPanel({
					autoScroll:true,
					layout    : "fit",
					frame     : false,
					border    : false,
					margin    : "1px",
					loadMask  : {
					     msg:"查询中..."
					},
					plugins: [this.getEditor()],
					viewConfig: {
						forceFit : true,
						columnLines: true
					},
					store     : this.getGridStore(),
					colModel  : this.getGridColumn(),
					bbar      : this.getGridPageBar(),
					selModel  : this.getCheckboxModel(),
					tbar      : this.getGridToolBar()
				});
			}
			return this.gridPanel;
		},
		getGridPageBar : function() {
			if (!this.pageBar) {
				 this.pageBar = new Ext.PagingToolbar({
					store         : this.getGridStore(),
					pageSize      : this.pageSize,
					displayInfo   : true,
					items:['-','显示', this.getGridPageSize(), '条' ],
					emptyMsg      : "没有数据", 
					displayMsg    : '第{0}条到{1}条记录,共 {2}条'
				});
			}
			return this.pageBar;
		},
		getGridPageSize:function(){
			if(!this.combox){
				this.combox = new Ext.form.ComboBox({
					mode     : "local",
					width    : 55,
					editable : false,
					value    : this.pageSize,
					triggerAction : "all",
					displayField  : "text",
					valueField    : "value",
					store : new Ext.data.SimpleStore({
						fields : ["value", "text"],
						data : [
							[20, "20"],[40, "40"], [80, "80"], [100, "100"]
						]
					}),
					listeners : {
						scope : this,
						select: function(combo,record,index){
							this.pageSize = combo.getValue();
							this.getGridPageBar().pageSize = this.pageSize;
						    this.onBtnRefesh();
						}
					}
				});
			}
			return this.combox;
		},
		//刷新
		onBtnRefesh:function(){
			var  store  = this.getGridStore();
			Ext.apply(store.baseParams,{
			   start : 0,
			   limit : this.pageSize
			});
		    store.load();
		},
		getGridStore : function() {
		     if(!this.gridstore){
		     	 this.gridstore= new Ext.data.JsonStore({
		     		autoLoad : true,
				 	fields   : ["id","isUsed","areaType","speed","createTime","xys","description","name","createBy","remark"],
				 	root     : 'data', 
					totalProperty: 'total', 
				    proxy : new Ext.data.HttpProxy({
						 url : this.areaListUrl
					}),
		            listeners : {
						scope : this,
						beforeload:function(store,record){
							var pageParam = {
								 start:0,
								 limit:this.pageSize
							};
							Ext.apply(store.baseParams,pageParam);
						},
						load : function(store,record,ee){
							if(this.action !="insert"){
								this.setUpdateValue();
								this.setGridSelected();
							}
						}
					}
		     	 });
		     }
		 	 return this.gridstore;
		},
		setUpdateValue:function(){
		   var list =  this.areaList;
		   var newData = [];
		   var store = this.getGridPanel().getStore();
		   store.each(function(record,index){
		   	   var rid = record.get("id"),caData = list[rid];
		       if(caData){
		       	  var speed = caData["overspeedThreshold"],alarmType = caData["alarmType"];
		          record.set("speed",speed);
		          record.set("areaType",alarmType);
		          record.commit();
		       }
		   });
		},
		//根据grid查询获取行索引
		getGridRowIndexById:function(rid){
		     var store = this.getGridPanel().getStore();
		     var dataIndex =  store.findBy(function(rocord,v){
		     	  var vlue = rocord.get("id");
		          if(rid == vlue){
		             return true;
		          }
		     });
		     return dataIndex;
		},
		//设置勾选表格
		setGridSelected:function(){
		   var columMod =  this.getGridPanel().getSelectionModel();
		   var list =  this.areaList,selectedList = [] ;
		   for (var ke in list) {
		   	  var rawIndex = this.getGridRowIndexById(ke);
		   	  selectedList.push(rawIndex);
		   }
		   if(selectedList.length >0){
		      columMod.selectRows(selectedList,true);
		   }
		},
		getEditor:function(){
	   	   if(!this.editor){
	   	       this.editor = new Ext.ux.grid.RowEditor({
			        saveText: '设置',
			        cancelText:"取消",
			        monitorValid : false,
			        clicksToEdit:2,//单击进入编辑状态,
			        listeners:{
			           scope :this,
			           showEditor:function(roweditor, rowIndex,bts){
			               var record = this.getGridStore().getAt(rowIndex),areaType = record.get("areaType");
			           	   var comod=this.getGridPanel().getColumnModel();
			           	   var comboc = comod.columns[4].getEditor();
			           	   var numberRditor= comod.columns[5].getEditor();
			           	   if(areaType != 2){
			           	       numberRditor.hide();
			           	   }
			           	   //下拉列表选中
			           	   comboc.on({
						       scope : this,
						       select : function(storem,record,value){
				                   if(value == 2){
				                   	   numberRditor.markInvalid("不能为空");
							           numberRditor.show();
				                   }else{
				                       numberRditor.hide();
				                       numberRditor.setValue('');
				                   }
				                }
						   })
			           },
			           canceledit:function(roweditor, changes, record, rowIndex){
			                var areaType = changes["areaType"],speed = changes["speed"];
			           	    if(areaType ==2 && GAS.isEmpty(speed)){
				                 roweditor.setRowEditValid(true);
				            }else{
				                 roweditor.setRowEditValid(false);
				            }
			           },
			           afteredit : function(roweditor, changes, record, rowIndex){
			              var areaType = changes["areaType"],speed = changes["speed"];
			              var record = this.getGridStore().getAt(rowIndex);
			              if(areaType ==2 && GAS.isEmpty(speed)){
			                 roweditor.setRowEditValid(true);
			              }else{
			                 roweditor.setRowEditValid(false);
			              }
			              record.commit();
			           }
			           
			        }
			   });
	   	   }
	   	   return this.editor;
	    },
		getGridColumn:function(){
		   var comboc = new Ext.form.ComboBox({
                typeAhead: true,triggerAction: 'all',lazyRender:true,editable : false,allowBlank: false,mode: 'local',
			    store: new Ext.data.ArrayStore({
			        fields: ['id','text'],
			        data: [[0, '进区域'], [1, '出区域'], [2, '进出区域限速'],[3, '预警区域']]
			    }),
			    allBlank : false,
			    valueField: 'id',
			    displayField: 'text'
             });
		    var cls = new Ext.grid.ColumnModel({
		    	columns:[
				       this.getCheckboxModel(),
					   this.getGridLineNum(),
					   {header  : "id",hidden  : true,dataIndex: "id",flex :1},
					   {header:"区域名称",dataIndex:"name",orderable: true,flex :1},
					   {header:"设置进出区域",dataIndex:"areaType",orderable: true,flex :1,editor: comboc,
					     scope :this,
			             renderer: function(val,cls,record){
	             		    if(GAS.isEmpty(val)){
	             		       return "<a href='javascript:void(1);'>设置区域</a>";
	             		    }
	             		    var newValue = val ==0 ?"<b style='color:blue'>进区域</b>" :val ==1 ?"<b style='color:red'>出区域</b>":val ==2 ?"<b style='color:green'>进出区域限速</b>":"<b style='color:green'>预警区域</b>";
			                return newValue;
			             }
					   },
					   {header:"设置速度",dataIndex:"speed", orderable: true,flex :1,scope :this,
					     editor : new Ext.form.NumberField({
			                minValue: 1,
			                value :1,
			                emptyText : "速度不能为空",
			                maxValue: 200
			             }),
			             renderer: function(val,cls,record){
			             	 return val;
			             }
					   }
				]
		    });
		    return cls;
		},
		//复选框
		getCheckboxModel:function(){
		  if(!this.getcheckbox){
			  this.getcheckbox = new Ext.grid.CheckboxSelectionModel({
			  		 singleSelect:false,
				  	 checkOnly: false,
					 getEditor: function(rowIndex){
						return this.editable !== false ? this.editor : null;
					 }
			  });
		  }
		  return this.getcheckbox;
		},
		//行号
		getGridLineNum:function(){
		  if(!this.lineNum){
			  this.lineNum = new Ext.grid.RowNumberer({header:"序号",width:34});
		  }
		  return this.lineNum;
		},
		//grid工具栏
		getGridToolBar : function() {
			if (!this.gridtoolBar) {
				      this.areaName = new Ext.form.TextField({
					      fieldLabel: "区域名称",
					      name : "areaName",
					      width : 100,
					      anchor : "92%"
					  });
				    this.gridtoolBar = new Ext.Toolbar({
					  enableOverflow: true, // 如果tbar溢出自动显示下三角
					  items: [
							"区域名称:",this.areaName,
							{text  : '查询',scope : this,width : 60,iconCls :"icon-searchFind",handler: this.doQuerySeach
							},"->"
						]
				});
			}
			return this.gridtoolBar;
		},
		//查询
		doQuerySeach:function(){
		    var areaName = this.areaName.getValue();
		    var store = this.getGridStore();
		    Ext.apply(store.baseParams,{
		          name : areaName
		    });
		    store.load();
		},
		//行号
		getGridLineNum:function(){
		  if(!this.lineNum){
			  this.lineNum = new Ext.grid.RowNumberer({header:"序号",width:34});
		  }
		  return this.lineNum;
		},
		getAreas:function(){
		     var list = this.getGridPanel().getSelectionModel().getSelections();
		     var newlist = [];
		     if(list &&　list.length > 0){
		     	 var index  =0;
		         for (var i = 0; i < list.length; i++) {
		         	  var dt = list[i],
		         	  id =dt.get("id"),
		         	  areaType = dt.get("areaType"),
		         	  speed = dt.get("speed");
		         	  index ++;  // id , areaType, 编号 ,速度
		         	  if(areaType == 2){
		         	       newlist.push(id+","+areaType+","+index+","+speed);
		         	  }else{
		         	       newlist.push(id+","+areaType+","+index+",");
		         	  }
		         }
		     }
		     GAS.log(newlist);
		     return newlist;
		},
		//提交参数 
		getSubmitParam : function(){
		    var params = {};
		    if(this.devideList.length> 0){
		       params["devides"] = this.devideList.join(",")
		    } 
		    params["id"] = this.ruleId.getValue();
		    params["name"] = this.ruleName.getValue();
		    params["areas"]  =  this.getAreas().join("@");
		    params["remark"] = this.remark.getValue();
		    return  params;
		},
		//验证
		validArea:function(){
			 var valid = true;
		     var list = this.getGridPanel().getSelectionModel().getSelections();
		     if(list && list.length > 0){
		         for (var i = 0; i < list.length; i++) {
		         	var record = list[i];
		         	var areaType = record.get("areaType");
		         	var speed = record.get("speed");
		         	if(areaType == 2 && !speed ){
		         	    valid = false;
		         	    Ext.Msg.alert("信息提示", "请设置进出区域限速");
		         	    break;
		         	}else if(GAS.isEmpty(areaType)){
		         		valid = false;
		         		Ext.Msg.alert("信息提示", "请设置区域类型");
		         	}else{
		         		valid = true;
		         	}
		         }
		     }else{
		         Ext.Msg.alert("信息提示", "请选择区域");
		         valid =false;
		     }
		     return valid;
		},
		saveRecordhandler:function(){
		     	var ruleName = this.ruleName;
				if (!ruleName.isValid()){
					return;
				}
				if(!this.validArea()){
					return ;
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
			//ajax提交参数
			onSubmit:function(params){
			    var callback = function(res,req){
		    	     var result = Ext.decode(res.responseText);
				 	 if(result["success"] && result["success"] == "true"){
				 	 	this.fireEvent("submit"); //激发submit方法
				 		Ext.Msg.alert("信息提示", result["msg"] || result["result"] || "添加成功");
				        this.windowClose(); 
				     }else{
				     	Ext.Msg.alert("信息提示", result["msg"] || result["result"] || "添加失败");
				     }
			    };
			    GAS.AOP.send({
			       url   : this.action=="update"?this.updateAreaRuleUrl:this.addAreaRuleUrl ,
			       params: params,
			       scope : this,
			       callbackFn:callback
			   });
			}
	});
	
	
})();



