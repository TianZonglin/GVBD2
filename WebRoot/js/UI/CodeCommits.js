Ext.require([
//在实际环境中我们都会用 ext-all.js, 但是在开发调试的时候，
//我们使用 require 的话它可以动态加载单个的 js 文件，便于我们定位错误。
//意思就是这个页面需要用到哪些组件,然后就预先加载,多余不用加载的组件就不管他了提高运行速度
    'Ext.form.*',
    'Ext.tab.*',
    'Ext.tip.QuickTipManager',
    'Ext.PagingToolbar',
    'Ext.chart.*'
]);

Ext.onReady(function() { //整个程序全部都在此空间内
//Ext的onReady是用来注册在Ext框架及页面的html代码加载完后，所要执行的函数。
//调用onReady方法时可以带三个参数，第一个参数是必须的，表示要执行的函数或匿名函数，
//第二参数表示函数的作用域，第三个参数表示函数执行的一些其它特性，
//比如延迟多少毫秒执行等，大多数情况下只需要第一个参数即可。
    var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
    
    var win,import_val,production_val,kmeans_val;
    var match = window.location.href;		//match = ipaddr?xiaomi
    var s=match.indexOf("?");				//s = 6
    var jsonfile=match.substring(s+1);		// jsonfile = xiaomi 即?后面的东西
    var datafile= 'data/'+jsonfile+'.json'; //datefile是整个传入的数据文件，需要原数据就要用它
    var filename=jsonfile,filenumber;		//jsonfile已初始化，filenumber未初始化
    
	var storeChart = Ext.create('Ext.data.JsonStore', { //JsonStore类
	//方便从JSON数据创建Ext.data.Store的小巧的帮助类。 
	//JsonStore将自动配置一个Ext.data.reader.Json
	//生成格式: data:[{name:'Tom', data:'18'},{name:'Sam',data:'20'}]
        fields: ['name', 'data'],	//fields值应该是一个Ext.data.Field属性对象的集合
        autoLoad: true,		//如果data属性未定义, 并且autoLoad值为'true'或对象, 则此store的load方法将在创建后自动执行.
		remoteSort: true,		//设置为 true 则将所有的排序操作推迟到服务器. 如果设置为 false, 则在客户端本地排序.  
		proxy: {
		      type: 'ajax',
		      url: 'dataLevel/'+jsonfile+'.json',
		      reader: {
		      	root: "data",	
				//对JSON Reader来说是一个属性名称(或其根为嵌套时的一个圆点分隔的属性名称列表)，默认为''
				//一般的，数据的自带的根都是有用的，例如JSON数组的根, XML元素的根
		      }
		  }

    });
    // create the Data Store

    var graphDataStore=Ext.create('Ext.data.Store',{
	// Store类封装了一个客户端缓存,用于存储 Model 对象. Stores 通过一个代理 Proxy 来加载数据, 
	// 并提供函数来 排序, 过滤 以及查询 内部所包含的 model 实例.
    	fields:[
                {name:'name',type:'string'},
                {name:'data',type:'int'}
            ],

		  autoLoad: true,   //如果data属性未定义, 并且autoLoad值为'true'或对象, 则此store的load方法将在创建后自动执行.
		  remoteSort: true,   //设置为 true 则将所有的排序操作推迟到服务器. 如果设置为 false, 则在客户端本地排序.
		  proxy: {  //代理
			  type: 'ajax',
			  url: 'dataTopK/'+jsonfile+'.json',
			  reader: {
				root: "data", //同上。数组的根
			  }
		  }
    });
    var store = Ext.create('Ext.data.Store', { //dataStore类
        fields : [
			{name : 'name',type : 'int'},
			{name : 'value',type : 'string'},
			{name : 'cx',type : 'string'}, 
			{name : 'cy',type : 'string'}
		],
		  autoLoad: true, 
		  remoteSort: true,  
		  pageSize: 15, // pageSize就是一次请求加载到cache中的行数. 这不会影响到buffered grid的渲染(rendering), 但是page size大意味着加载次数少.
		  proxy: {
		      type: 'ajax',
		      url: datafile,//源数据文件
		      reader: {
		      	root: "nodes", //同上。数组的根
		      }
		  }
		});
    var pager = Ext.create("Ext.PagingToolbar", { //PagingToolbar类
        //重要，指定分页所使用的store
        store:store,	//dataStore类，表明所使用的的数据源即为刚刚初始化的store类
        displayInfo:true,	//是否显示信息 true 显示消息
        displayMsg:"第 {0}-{1}条 / 共 {2} 条",
		//显示消息 显示分页状态的消息。 注意这个字符串里大括号中的数字是一种标记,其中的数字分别代表起始值,结束值和总数。 
		//当重写这个字符串的时候，如果这些值是要显示的，那么这些标识应该被保留下来。
        emptyMsg:"暂无记录"	 //空消息 没有找到记录时，显示该消息
    });
    var store1 = Ext.create('Ext.data.Store', { //dataStore类
    	fields : [
			{name:'id',type : 'int'},
			{name:'value',type : 'string'}
		],
    	autoLoad: true,
		  proxy: {
		      type: 'ajax',
		      url: 'servlet/getAlreadyData', //【【【【【【【【【【【【【【【【【【【【【【【触发，下拉框中的选项】---> gvbd.servlet.getAlreadyData
		      reader: {
			      	root: "data"
			 }
		  }  	
    });
    function importfun(action) {    //////导入数据->servlet/importData.class
        if (!import_val) {
        	 var uploadForm=new Ext.FormPanel({ 
			 //FormPanel 为 form 表单提供了一个标准的容器. 本质上还是一个标准的 Ext.panel.Panel, 只是自动创建了一个 BasicForm 
			 //来管理所有添加到 Panel中的 Ext.form.field.Field 对象. 可以快捷方便地进行 配置以及处理 BasicForm 和 表单域.
        	        id:'uploadForm',  
					//当前组件实例唯一的ID。它不应该是必要的，除了在你的应用程序使用这个配置单例对象。
					//用一个 id 创建的组件可以使用 Ext.getCmp 进行全局访问
        	        width:520,  //此组件的宽度以像素为单位。
        	        frame:true,  //True为Panel填充画面,默认为false.
        	        fileUpload: true,    
        	        autoHeight:true,  
        	        bodyStyle:'10px 10px 0px 10px',  //用户自定义CSS样式被应用到panel的body元素上，这个样式可以是一个有效的CSS样式串， 可以是一个包含样式属性名/值的对象或者是一个方法返回一个字符串或对象
        	        labelWidth:50,  
        	        enctype: 'multipart/form-data',    ////////定义编码格式，用于传递完整的文件数据
        	        defaults:{  
        	            anchor: '95%',  //容器的相对定位值
        	            allowBlank: false  
        	        },  
        	        items:[{  //单个组件,或者是以数组形式定义的子组件集合 将会自动添加到容器中去
        	                xtype:'fileuploadfield',  //提供了一个较短的替代全类型创建对象.特别是在一个容器使用xtype来定义组件实例
        	                emptyText: '请选择上传文件...',  
        	                fieldLabel: '文件',   
        	                id:'uploadFile',   //组件实例唯一的ID
        	                name: 'upload',    //在全局命名空间中属性的名称,可以引用当前Ext的实例
        	                allowBlank: false,    //设置为false时确保按钮组里面至少有一个组件被选中,如果在验证时没有按钮被选中, blankText将作为错误提示出现，默认True
        	                blankText: '文件名称不能为空.',     
        	                buttonCfg: {  
        	                            text: '选择...'// 上传文件时的本地查找按钮  
        	                }  
        	            },{ //??????????????
        	            	xtype: 'numberfield',  //numberfield是带增减的数字型控件
                       	    id:'number1',
                            fieldLabel: '顶点数',
                            name: 'number',
                            value: 1,
                            minValue: 1,
                            allowNegative:false,
                            allowBlank: false
        	            } 
        	        ],  
        	        buttons: [{  
					//包括两个按钮，[上传] [重置]
					//每个按钮都包括'名称'+'绑定函数'
							text: '上传',  
							handler: function(){  //进行全局访问uploadForm类
								if(uploadForm.getForm().isValid()){  //使用uploadForm.getForm()获取此Panel包含的Form.
									uploadForm.getForm().submit({  //默认使用AJAX提交表单。如果standardSubmit配置可用，将使用标准表单元素提交
										url:'servlet/importData',  //【触发】
										method:'POST',  //表单使用的方法（默认表单的method，未定义时为POST）
										waitTitle: '请稍后',  
										waitMsg: '正在上传文档文件 ...',  
										success: function(fp, action){  //成功相应后将被调用的回调函数
											filenumber = Ext.getCmp("number1").getValue();	//获得number1控件的输入值，即fielnumber为顶点数
											filename = action['result']['fileName'].substring(0,action['result']['fileName'].indexOf("."));
											//result????????????????
											//Ext.Function.pass(importfun, '导入数据')	
											Ext.MessageBox.alert('成功', '导入数据成功'); 
											Ext.getCmp("uploadFile").reset();   //重置uploadFile控件的内容  
											
										},  
										failure: function(fp, action){  
											Ext.MessageBox.alert('警告', '导入数据失败');    
											import_val.hide();  
										}  
									});  
								}  
							}  
						},{  
							text: '重置',  
							handler: function(){  
								uploadForm.getForm().reset();  
							}  
						}]  
        	          
        	    });  
        	      
        	 
            import_val = Ext.widget('window', {
			//便于速记创建一个微件的xtype配置对象
                title: action,
                closeAction: 'hide',
                width: 200,
                height: 150,
                layout: 'fit',
                resizable: true,
                modal: true,
                items: uploadForm
            });
        }
        import_val.show();
    }
    function kmeans(action) { //聚类的实现
        if (!kmeans_val) {
		//如果kmeans_val控件不存在，则先要生成之
        	 var kmeansForm=new Ext.FormPanel({  //显示一个窗口
        	        id:'kmeansForm',  
        	        width:520,  
        	        frame:true,    
        	        autoHeight:true,  
        	        bodyStyle:'10px 10px 0px 10px',  
        	        labelWidth:50,  
        	        items:[{
        	            	xtype: 'numberfield',
                       	    id:'number',
                            fieldLabel: 'K值',
                            name: 'number',
                            value: 1,
                            minValue: 1,
                            allowNegative:false,
                            allowBlank: false
        	            },{ 
        	            	xtype:'textfield',
        	            	id:'jsonname',
        	            	fieldLabel:'文件名',
        	            	name:'jsonname',
        	            	value:jsonfile,
                            hidden: true, //默认此控件被隐藏
                            hideLabel:true,

    				  }
        	        ],  
        	        buttons: [{ //包含两个按钮。[提交]和[重置] 
							text: '提交',  
							handler: function(){  
								if(kmeansForm.getForm().isValid() && jsonfile){
									console.log(jsonfile);
									kmeansForm.getForm().submit({  
									//默认使用AJAX提交表单。 如果standardSubmit配置可用，将使用标准表单元素提交，如果api配置存在， 它会使用Ext.direct.Direct 提交操作。
										url:'servlet/kmeans',  //【触发】
										method:'POST',
										success: function(fp, action){   

											Ext.MessageBox.alert('Ok!', '聚类生产中,请稍后.');
											if(jsonfile){
												window.location.href="UI/desktop.jsp?kmeans_"+jsonfile; 
											}else{
												document.location.reload();
											}       	                                        
											kmeans_val.hide();
										},  
										failure: function(fp, action){  
											Ext.MessageBox.alert('警告', '数据聚类失败');    
											kmeans_val.hide();  
										}  
									});  
								}  
							}  
						},{  
							text: '重置',  
							handler: function(){  
								kmeansForm.getForm().reset();  
							}  
						}]  
        	    });  
        	      
        	//此时kmeans_val不存在，新建，结果为300*100的窗口
            kmeans_val = Ext.widget('window', {
                title: action,
                closeAction: 'hide',
                width: 300,
                height: 100,
                layout: 'fit',
                resizable: true,
                modal: true,
                items: kmeansForm
            });
        }
        kmeans_val.show(); // 显示聚类结果
    }


    function production(action) { 
	//数据生成的实现。四文本框+2按钮
        if (!production_val) {
            var form = Ext.widget('form', {
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                border: false,
                bodyPadding: 10,
                fieldDefaults: {
                    labelAlign: 'top',
                    labelWidth: 100,
                    labelStyle: 'font-weight:bold'
                },
                items: [{
                    fieldLabel: '文件名称',
                    id : 'filename',
                    afterLabelTextTpl: required,
                    xtype:'textfield', //文本框
                    name: 'name',
                    allowBlank: false
                },{
                	xtype: 'numberfield', //带自增自减的数字框
                	 id:'number',
                     fieldLabel: '顶点数',
                     name: 'number',
                     value: 1,
                     minValue: 1,
                     allowNegative:false,
                     allowBlank: false
                },{
                	xtype: 'numberfield',
                	id:'avg',
                    fieldLabel: '基础度',
                    name: 'avg',
                    value: 1,
                    minValue: 1,
                    maxValue: 125,
                    allowBlank: false
                },{
                	xtype: 'numberfield',
                	id:'random',
                    fieldLabel: '随机度',
                    name: 'random',
                    value: 1,
                    minValue: 1,
                    maxValue: 125,
                    allowBlank: false
                }],

                buttons: [{
                    text: '取消',
                    handler: function() {
                        this.up('form').getForm().reset(); //先重置所有form内容
                        this.up('window').hide(); //再将当前显示的窗口隐藏
                    }
                }, {
                    text: '提交',
                    handler: function() {
                    	
                        if (this.up('form').getForm().isValid()) {
                        	Ext.Ajax.request({
                        	    url: 'servlet/proData', //【触发】
                        	    params: { //提交四个框里填的值
									avg:Ext.getCmp('avg').getValue(),
									filename:Ext.getCmp('filename').getValue(),
									number:Ext.getCmp('number').getValue(),
									random:Ext.getCmp('random').getValue()
								},
                        	    async: false, //当async:true时ajax请求是异步的,false为同步
                        	    success: function(response){
                        	    	filename = Ext.getCmp('filename').getValue(); //更新filename文件名
                        	    	filenumber = Ext.getCmp("number").getValue(); //更新filenumber顶点数
                        	    	Ext.MessageBox.alert('ok!','数据生成成功');  
                        	    },
                        	    error:function(response){
                        	    	Ext.MessageBox.alert('error!','数据生成失败');
                        	    }
                        	});
                            this.up('form').getForm().reset();
                            this.up('window').hide(); //先重置所有form内容,再将当前显示的窗口隐藏
                        }
                    }
                }]
            });
			//如果成功的话，也会出现一个200*300的窗口，第一次建立的窗口就在此位置
            production_val = Ext.widget('window', {
                title: action,
                closeAction: 'hide',
                width: 200,
                height: 300,
                layout: 'fit',
                resizable: true,
                modal: true,
                items: form
            });
        }//if
        production_val.show(); 
    }
    
    ///////////////////////////////////////////////////自定义的显示[类]，重要！
    Ext.define('EB.view.content.SingleView', {
		extend : 'Ext.panel.Panel', //继承Pannel
		alias : 'widget.singleview', //别名 类名称简短的别名列表
		layout : 'fit',
		title : '图',
		autoScroll:true, //[Panel]'true'使用溢出：'自动'的组件布局元素，并在必要时自动显示滚动条
		frame :true,//[Panel]true则为Panel填充图片，即背景

		initComponent : function() {
			this.callParent(arguments);
		},//[Ext]在初始化组件模板方法是一个重要的组件的初始化步骤。它的目的 
		//是要实现Ext.Component提供任何所需的构造逻辑函数每个子类的。在初始化组件模板被创建的类的方法，

		onRender : function() { //在初始化渲染时新建对象
			var me = this;
			me.doc = Ext.getDoc();
			me.callParent(arguments);
			me.drawMap(); 
		},

		//自定义的画图函数drawMap(),其中使用了d3.js
		drawMap : function() {
			var width = 5000, height = 5000;
			//var radius = d3.scale.sqrt().range([0, 6]);//值域  
			var color = d3.scaleOrdinal().range(d3.schemeCategory20);
			var svg = d3.select("#" + this.id + "-body") //第一步，产生画布，整个画布为5000*5000
						.append("svg")
						.attr("width", width)
						.attr("height", height);
			var tooltip = d3.select("#" + this.id + "-body")
						.append("div")
						.attr("class","tooltip")
						.style("opacity",0.0);
		    transform = d3.zoomIdentity;
			d3.json(datafile, function(json){ //源数据文件
				var circles_group = svg.append("g");
				var lines = circles_group.selectAll("line").data(json.links).enter().append("line"); //在画布上划横线	
				//selectAll[挖坑]->data[匹配数据]->enter[填入数据]->append[确定坑的样式]
				var lineAttribute=lines
				.attr("x1",function(d){return d.x1})
				.attr("y1",function(d){return d.y1})
				.attr("x2",function(d){return d.x2})
				.attr("y2",function(d){return d.y2})
				.attr("strokeh,"#000"); //黑色，画节点之间的连线
				var circles = circles_group.selectAll("circle").data(json.nodes).enter().append("circle");//在画布上划点
				var circleAttributes = circles
				.attr("cx",function(d){
					if(d.name==1)console.log(d.cx);
					return d.cx}
				)
				.attr("cy",function(d){
					if(d.name==1)console.log(d.cy);
					return d.cy
					}
				)
				.attr("r", 5)  //点的半径为5px，即作为节点的标注点
				.on("mousemove", mousemove)
				.on("mouseout", mouseout)
				//.attr("fill","#6495ed");
				.style("fill", function(d, i) { 
					if(d.color){
						return color(d.color);  	
					}else{
						return color(1); 
					}
					//console.log(i);
					
					
				});
				
					
				var lines = circles_group.selectAll("line").data(json.links).enter().append("line");						
				var lineAttribute=lines
				.attr("x1",function(d){return d.x1})
				.attr("y1",function(d){return d.y1})
				.attr("x2",function(d){return d.x2})
				.attr("y2",function(d){return d.y2})
				.attr("fill","#000");		
				svg.call(d3.zoom()
			    	    .scaleExtent([1 / 8, 8])
			    	    .on("zoom", zoomed));
				function mousemove(d, i){
					tooltip.html(d.value)
					.style("opacity",1.0);
				}
				function mouseout(d, i){
					tooltip.html(d.value)
					.style("opacity",0.0);
				}
			    function zoomed() {
			    	circles_group.attr("transform", d3.event.transform);
			    }

			    function dragged(d) {
			    	  d3.select(this).attr("cx", d.x = d3.event.x).attr("cy", d.y = d3.event.y);
			    }

			   function phyllotaxis(radius) {
			    	  var theta = Math.PI * (3 - Math.sqrt(5));
			    	  return function(i) {
			    	    var r = radius * Math.sqrt(i), a = theta * i;
			    	    return {
			    	      x: width / 2 + r * Math.cos(a),
			    	      y: height / 2 + r * Math.sin(a)
			    	    };
			    	  };
			   }
			});
			

		}

	});   

   
  //面板代码
  //Viewport渲染自身到网页的documet body区域， 并自动将自己调整到适合浏览器窗口的大小，在窗口大小发生改变时自动适应大小。 一个页面中只能创建一个Viewport
  //任何的Container容器都可以作为一个Viewport 的子组件，开发者使用一个Viewport作为父容器配置布局layout， 并管理他们的大小和位置.
  //几个子项在一个父容器中，要显示全尺寸显示其中一个“激活”的子项,使用 card layout布局.
  //所有的内部布局可以通过Panel 添加到Viewport,或者通过配置items,或者通过添加 add 方法添加panels，这些子组件本身可能就存在自身的布局方式.
  //The Viewport本身不提供滚动条,所以如果内部的子面板要实现滚动条， 需要配置autoScroll属性.
  //一个典型应用的 [ border ] layout布局的面板例子(上下左右中):  
	var windowExt = Ext.create('Ext.container.Viewport', {
		layout : 'border', //布局类型将会被容器使用,如果没有指定的话 默认使用布局类型Ext.layout.container.Auto.
		items : [{ //单个组件,或者是以数组形式定义的子组件集合 将会自动添加到容器中去
			
			///////////////////////////////////////////////////////// 上侧面板
			region : 'north',
			xtype : 'panel',  //这一部分的组件全部用的是panel类的属性
		    //此属性提供了一个较短的替代全类型创建对象. 使用 xtype 是最常见的方式来定义组件实例, 特别是在一个容器中
			//如果子组件是指定的,它的实际组件的类型将根据xtype选项进行实例化. 每个组件都有各自的xtype
			//如果没有指定 xtype, 那么将会使用 defaultType，默认是panel
			bodyPadding : 5,
			title : 'GVBD-Graph Visualization Base on  Distributed system',
			renderTo : Ext.getBody(), //指定元素的id, 一个DOM元素或现有的元素，这个组件将被渲染;将当前document的body对象当作Ext.Element返回。
			tbar : [{
			//便利的配置,'Top Bar'的缩略形式
						xtype : 'button',
						text : '文件',
						// arrowAlign : 'bottom',
						menu : [{
							text : '导入数据', //菜单下拉栏名称；
							handler: Ext.Function.pass(importfun, '导入数据') //当点击按钮是触发的函数（可以用于代替click事件），字符为弹出窗口的标题
						},{ 
						text : '生成数据',
						handler: Ext.Function.pass(production, '生成数据')}
					]}, {
						xtype : 'button',
						text : '工具',
						menu : [{
							text : '聚类',
							handler: Ext.Function.pass(kmeans, '聚类')}
					]}, {
						xtype : 'button',
						text : '工作区'
					}, {
						xtype : 'button',
						text : '视图'
					}, {
						xtype : 'button',
						text : '窗口'
					}, {
						xtype : 'button',
						text : '插件'
					}, {
						xtype : 'button',
						text : 'xxxxxxx'
					}, {
						xtype : 'button',
						text : '帮助',
						handler: Ext.Function.pass(aboutus)
					}]

		  }, {	///////////////////////////////////////////////////////// 左侧面板
					region : 'west',
					collapsible : true, //设置为true是panel具有可折叠功能并且有一个展开/折叠的切换按钮被添加到panel的标题头区域。
					split : true,
					title : '数据操作',
					items : [{
					    width:          250,
					    xtype:          'combo', //用户可以自由的在域中键入，或从下拉选择列表中选择值。 默认用户可以输入认可值，即使它没有出现在选择列表中
					    height :        20,
					    margin:         '5 10 5 10',
					    emptyText:          '请选择一种已有数据集',
						name:           'title',
						id:           'title1',
						displayField:   'value',
						valueField:     'value',
						//store中的数据项被分别映射在每个选项的显示文本和隐藏值，通过valueField和displayField配置
						fieldLabel: '数据集',
						mode:'local',  //如果store非远程，例如：仅依靠本地数据并从前端被加载，应该确保设置queryMode为“'local'”， 因为这样会给用户提高相应
						store:store1, //dataStore类,选择列表中的项是从任何Ext.data.Store（包括远程store）填充。 
						
						listeners:{ //一个配置对象，包含一个或多个事件处理函数，在对象初始化时添加到对象，可一次添加多个处理函数
							'change':function(thisField,newValue,oldValue,epots){ 
								  window.location.href="UI/desktop.jsp?"+newValue; 
							   }
							}
				  },{
						xtype : 'gridpanel', //Grid是在客户端上显示大量的表格数据的极佳方式。它本质上是一个超级统计表<!-- <table> -->， GridPanel使其更容易地进行获取、排序和筛选大量的数据
						width : 380,
						height : 400,
						store :store, //dataStore类
						columns: [
					                { header: '索引值',  dataIndex: 'name',width:60},
					                { header: '节点名称', dataIndex: 'value',width:120},
					                { header: 'X坐标', dataIndex: 'cx'},
					                { header: 'Y坐标', dataIndex: 'cy'}
				                ],

				      bbar:pager //PagingToolbar类,最末行显示信息
					},{ /////下半部分面板
						xtype : 'form', //自动创建了一个 BasicForm 来管理所有添加到 Panel中的 Ext.form.field.Field 对象. 可以快捷方便地进行 配置以及处理 BasicForm 和 表单域
						width : 380,
						height : 500,
						title : '布局',
						collapsible: true, //可折叠
					    items:[{
					      	    width:          250,
							    xtype:          'combo',
							    margin:         10,
							    emptyText:          '--请选择一种布局方法--',
								name:           'title',
								id:           'title',
								displayField:   'name',
								valueField:     'value',
								fieldLabel: '布局方法',
								mode:'local', //本地加载Store内容，此处Store内容即在下方为新建的类内容
								store:Ext.create('Ext.data.Store', { //自定义新类
									fields : ['name', 'value'],
									data   : [
										   {name : 'ChengLayout',   value: 'ChengLayout'},
										   {name : 'FRLayout',  value: 'FRLayout'},
										   ]
								  }),
								 listeners:{ //监听各个子控件选中的值
										'change':function(thisField,newValue,oldValue,epots){  
										        if(newValue === 'ChengLayout' ){        	
										                	Ext.getCmp("kvalue").setVisible(true);  										                	
										                	Ext.getCmp("isDirected").setVisible(true);
										                	Ext.getCmp("cool").setVisible(true);
										                	Ext.getCmp("temperature").setVisible(true);
										                	Ext.getCmp("deep").setVisible(true);
										                	Ext.getCmp("times").setVisible(true);
										                	Ext.getCmp('save').setDisabled(false); //显示，原先为不显示 
										        }else if (newValue === 'FRLayout'){	
										                	Ext.getCmp("kvalue").setVisible(true);  
										                	Ext.getCmp("isDirected").setVisible(true);
										                	Ext.getCmp("cool").setVisible(true);
										                	Ext.getCmp("temperature").setVisible(true);
										                	Ext.getCmp("deep").setVisible(false); //没有deep控件
										                	Ext.getCmp("times").setVisible(true);
										                	Ext.getCmp('save').setDisabled(false); //显示，原先为不显示 
										        }else{ //初始状态下控件都不显示    	
										                	Ext.getCmp("kvalue").setVisible(false);                  	
										                	Ext.getCmp("isDirected").setVisible(false);
										                	Ext.getCmp("cool").setVisible(false);
										                	Ext.getCmp("temperature").setVisible(false);
										                	Ext.getCmp("deep").setVisible(false);
										                	Ext.getCmp("times").setVisible(false);
										                	Ext.getCmp('save').setDisabled(true); //设为不显示
										         }//else
								         }//change
						           }//listeners
					           },{
					               xtype: 'numberfield', //数字控件，带增减，默认步长为1
					               margin:10,
					               name: 'kvalue',
					               id:'kvalue',
					               fieldLabel: 'K 值',
					               value: 1, //初始值
					               minValue: 0,
					               maxValue: 1,
					               step:0.1, //步长为0.1
					               allowDecimals: true,
					               decimalPrecision: 2,
					               allowBlank: false, //不允许为空
					               hidden: true  //默认不显示
					           },{
					               xtype: 'numberfield',
					               margin:10,
					               name: 'times',
					               id:'times',
					               fieldLabel: 'Times',
					               value: 200,
					               minValue: 1,
					               maxValue: 500,
					               allowBlank: false,
					               hidden: true  //默认不显示
					           },{
					               xtype: 'checkboxfield', //选择控件
					               name: 'isDirected',
					               id:'isDirected',
					               margin:10,
					               fieldLabel: '是否是有向图',
					               boxLabel: '是',
					               hidden: true  
					           },{
					                xtype: 'numberfield',
					                fieldLabel: 'Cool 值',
					                name: 'cool',
					                id: 'cool',
					                value:0.95, //初始值
					                margin:10,
					                minValue: 0,
					                maxValue: 1,
					                allowDecimals: true,
					                decimalPrecision: 2,
					                step: 0.1, //步长为0.1
					                allowBlank: false,
					                hidden: true  //默认不显示 
					            },{
					                xtype: 'numberfield',
					                fieldLabel: 'Temperature',
					                name: 'temperature',
					                id: 'temperature',
					                margin:10,
					                minValue: 1,
					                value:140,
					                allowDecimals: true,
					                allowBlank: false,
					                hidden: true  //默认不显示 
					            },{ //特殊:动态的出现
						               xtype: 'numberfield',
						               margin:10,
						               name: 'deep',
						               id: 'deep',
						               fieldLabel: 'Deep 值',
						               value: 3,
						               minValue: 1,
						               allowBlank: false,
						               hidden: true  //默认不显示 
						           },{ 
								   //////////最下方的两个键[Save]和[Cancel]
						        	   xtype: "button", 
						        	   text: "Save",
						        	   id:"save",
						        	   margin:10,
						        	   width:100,
						        	   disabled:true,  //默认不显示
						        	   handler: function() {
						        		   console.log(filenumber); //filenumber为顶点数
						        		   if (this.up('form').getForm().isValid() && filename && filenumber) {
										   ////沿着ownerCt查找匹配简单选择器的祖先容器.ownerCt是组件所属的Container(当前组件被添加到一个容器中时此值被自动设置)
					                        	Ext.Ajax.request({ 
												//向一个远程服务器发送HTTP请求. Ajax服务器请求是异步的, 就是说此函数将在服务器响应前就已经返回. 任何对响应 数据的处理需要使用回调函数来实现.
					                        	    url: 'servlet/postData', //【【【【【【【【【【【【【【【【【【【【【触发】
													//发送请求的URL, 或一个返回URL字符串的函数, 函数的作用域为scope属性值. 默认为当前Connection对象的url属性值.
					                        	    params: {
													//一个包含所有发送给服务器的请求参数的属性对象, 或一个url编码 字符串, 或一个能返回前二者之一的函数
					                        	    	title:Ext.getCmp('title').getValue(),
					                        	    	kvalue:Ext.getCmp('kvalue').getValue(),
					                        	    	isDirected:Ext.getCmp('isDirected').getValue(),  
					                        	    	cool:Ext.getCmp('cool').getValue(), 
					                        	    	temperature:Ext.getCmp('temperature').getValue(), 
					                        	    	deep:Ext.getCmp('deep').getValue(), 
					                        	    	times:Ext.getCmp('times').getValue(),
					                        	    	filenumber:filenumber,
					                        	    	filename:filename
					                        	    },
					                        	    async: false, //当async:true时ajax请求是异步的,false为同步
					                        	    success: function(response){ //在请求成功返回时调用此函数
					                        	    	Ext.MessageBox.alert('Ok!', '布局生产中,请稍后.');
					                        	    	if(filename){
					                        	    		window.location.href="UI/desktop.jsp?"+filename; 
					                        	    	}else{
					                        	    		document.location.reload();
					                        	    	}
					                        	    },
					                        	    error:function(response){
					                        	    	Ext.MessageBox.alert('error!', '布局出错了.');
					                        	    }
					                        	}); //ajax传输+布局结束
					                            this.up('form').getForm().reset(); 
												//getForm().reset()获取此Panel包含的Form并将其属性值重置为零.
						               }
						             }
						           },{
						        	   xtype: "button", 
						        	   text: "Cancel",
						        	   margin:10,
						        	   width:100,
						        	   handler: function() {
						                   this.up('form').getForm().reset();
						               }
						           }],
						      
					          
					}]
				}, {///////////////////////////////////////////////////////// 右侧面板
					region : 'east',
					collapsible : true, //可以隐藏，会显示一个小按钮
					split : true, //会产生分割
					title : '数据信息', //整个侧边栏的题头，此侧边栏包含2个pannel
					items : [{
						xtype : 'panel',
						width : 300,
						height : 500,
						title : '统计',
						collapsible: true, //可折叠
					    items:[{
							    	xtype : 'panel',
									id: 'datainfoid', //柱状图id
									width : 300,
									height : 40,
									html : '',
					        	},{
								//创建一个简单的图表,每个图表有三个关键部分 
								//一个Store包含数据, 一个 Axes数组定义图表边界, 和一个或更多的 Series 处理可视化呈现的数据点
									xtype: 'chart',
									width : 300,
									height : 400,
						            store: storeChart, //JsonStore类,即dataLevel/xxx.json，['data']等均为json文件中的格式
						            shadow: true,
						            axes: [{ //表边界:左侧竖轴(数字)+下侧横轴(字)
						                type: 'Numeric',
						                position: 'left',
						                fields: ['data'],
						                grid: true,
						                minimum: 0
						            }, {
						                type: 'Category',
						                position: 'bottom',
						                fields: ['name'],
						                title: 'Month' //写死的永远的Month
						            }],
						            series: [{
						            	type: 'column', //系列的类型。[column为矩形]
						            	axis: 'left',
						            	highlight: true, //如果设置为真，当鼠标移动到上面时，markers或者series高亮
						                tips: { 
										//为可视化标记添加工具栏。栏的参数与Ext.tip.ToolTip使用的配置相同
										//当在一个某些元素上或者页面上的元素时，显示一个工具提示框
						                    trackMouse: true,
						                    width: 100,
						                    height: 28,
						                    renderer: function(storeItem, item) { //在框中显示name+data
						                      this.setTitle(storeItem.get('name') + ': ' + storeItem.get('data'));
						                    }
						                  },
						                  xField: 'name', //该域用来从数据源的项目中存取x坐标的值
						                  yField: 'data'//该域用来从数据源的项目中存取y坐标的值
						            }]
									
					    		}]	
							},{
								xtype : 'panel',
								width : 300,
								height : 400,
								title : '分析',
								collapsible: true, //可折叠
							    items:[{
											xtype: 'chart',

											width : 300,
											height : 300,
								            store: graphDataStore, //dataStore类
								            shadow: false,
								            series: [{
								            	type: 'pie', //[pie为圆形]
								                field: 'data',
								                label: {//这里能够使拼饼上面显示，该拼饼属于的部分
								                    field: 'name',
								                    display: 'rotate',
								                    font: '14px Arial'
								                },
								                highlight: {//这里是突出效果的声明，margin越大，鼠标悬停在拼饼上面，拼饼突出得越多
								                    segment: {
								                        margin: 5
								                    }
								                },
								                tips: { //鼠标悬停效果同上
								                    trackMouse: true,
								                    width: 170,
								                    height: 28,
								                    renderer: function(storeItem, item) {
								                      //calculate percentage.
								                      var total = 0;
								                      graphDataStore.each(function(rec) { //dataStore类
								                          total += rec.get('data');
								                      });
								                      this.setTitle(storeItem.get('name') + ': ' + Math.round(storeItem.get('data') / total * 100) + '%');
													  //相关占比是直接在页面算出来的
								                    }
								                  },
								                animate: true //为true时默认动画 (easing: 'ease' 和 duration: 500) 或标准动画配置对象将用于默认图表动画。 默认为 false.
								            }]
											
							    		}]	
									}] //2个panel全部完成
				}, { ///////////////////////////////////////////////////////// 中间面板
					region : 'center',
					xtype : 'tabpanel', // TabPanel itself has no title
					items :	Ext.widget('singleview', {
								width : 5000,height : 5000
					})
			}]
	});
	
	
	
	function handleActivate() {
		
		$.getJSON(
			datafile, function(data) //callback函数，datafile为源数据文件
			{ 
				filenumber = data.nodes.length;
				var link = data.links.length;
				Ext.getCmp('datainfoid').body.update("顶点数为："+filenumber+"<br />边数为："+link);
				//柱状图id
			}
		); 

	}
	handleActivate();
	var  index = 0;
	 
	   
	//没用到这个函数
	function addTab () {
		var tabs = windowExt.getComponent('tabpanel');
	    console.log(tabs);
		++index;
		tabs.add({
			html: 'Tab Body ' + index + '<br/><br/>',
			iconCls: 'tabs',
			title: 'New Tab ' + index
		}).show();
	}
	function  aboutus(){
		  aboutus = Ext.widget('window', {
              title: '关于',
              modal: true,
              html: '1111he boxLabel contains a HTML link to the Terms of Use page; a special click listener opens this'+
              'he boxLabel contains a HTML link to the Terms of Use page; a special click listener opens this',
              width: 700,
              height: 400,
              bodyStyle: 'padding: 10px 20px;',
              autoScroll: true,
             
          });
		  aboutus.show(); //显示About提示框
		  
		  
	  }
	
})




//说明：
//左上侧下拉框<->sevlet/getAreadyData...用的是data/xxx.json文件
//左下侧下拉框<->sevlet/postData...用的是dataSimple/xxx.txt文件，会产生或者重写data/xxx.json
//右上侧柱状图<->直接读 dataLevel/xxx.json 分层后每层的节点数
//右下侧饼状图<->直接读 dataTopK/xxx.json 统计数据中的较多者并画饼
//
//点击生成数据之后->
//产生xxx.txt 与原始数据格式相同，如xiaomi.txt
//产生xxx.json 画饼图的相关数据
//
//
//
//
//
//
//
//
//