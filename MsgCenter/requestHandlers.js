var exec = require("child_process").exec;
var querystring = require("querystring");
var url = require("url");
var fs = require("fs");
//var formidable = require("/usr/local/lib/node_modules/formidable");
var formidable = require("formidable");
//var io = require("socket.io-client");
var redis = require("./redis");
var server = require("./server");

function start(response,request) { 
	console.log("Request handler 'start' was called."); 
	
	/*exec("ls -lah", function (error, stdout, stderr) 
		{ 
		response.writeHead(200, {"Content-Type": "text/plain"}); 
		response.write(stdout); 
		response.end(); 
		});*/

        response.writeHead(200, {'Content-Type': 'text/html'});
        response.write('<h1>Hello! Try the <a href="/index.html">Socket.io Test</a></h1>');
        response.end();

	} 

function index(response,request)
{
	var path = url.parse(request.url).pathname;


     fs.readFile(__dirname + path, function(err, data){
        if (err)
		{
		 response.writeHead(404);
         response.write('404');
         response.end();

		}
        response.writeHead(200, {'Content-Type': path == 'json.js' ? 'text/javascript' : 'text/html'});
        response.write(data, 'utf8');
        response.end();
        });


}

function upload(response,request) { 
	console.log("Request handler 'upload' was called."); 
	
	var form = new formidable.IncomingForm();
	console.log("about to parse");
	form.parse(request, function(error, fields, files) {
		console.log("parsing done");
		fs.renameSync(files.upload.path, "/data/nodejs/tmp/test.png"); 
		response.writeHead(200, {"Content-Type": "text/html"}); 
		response.write("received image:<br/>");
		response.write("<img src='/show' />"); 
		response.end(); });
	} 


function show(response, request) { 
	
	console.log("Request handler 'show' was called."); 
	fs.readFile("./tmp/test.png", "binary", function(error, file) {
		if(error) { 
			response.writeHead(500, {"Content-Type": "text/plain"}); 
			response.write(error + "\n");
			response.end(); } 
			else { 
				response.writeHead(200, {"Content-Type": "image/png"}); 
				response.write(file, "binary"); 
				response.end(); } }); 
				
}
function javaServlet(response,request)
{
	var postData = ""; //POST & GET ： name=zzl&email=zzl@sina.com
    // 数据块接收中
    request.addListener("data", function (postDataChunk) {
        postData += postDataChunk;
    });
	// 数据接收完毕，执行回调函数
    request.addListener("end", function () {
        //console.log('数据接收完毕');
        //console.log(postData);
        var params = querystring.parse(postData);//GET & POST  ////解释表单数据部分{name="zzl",email="zzl@sina.com"}
        //console.log(params);
        var topicNames = params["topicNames"].replace('[','').replace(']','');
        //console.log(topicNames);
        var data = params["data"].replace('\r\n','');
        //console.log(data);

        var Single = require('./singleClass');
	    var s = new Single(null);
	    var sClass = s.getInstance();
	    // sClass.show();

        var ts = topicNames.split(",");
        if(ts && ts.length>0){
        	for(var i=0; i<ts.length; i++){
        		var topicName = ts[i].replace(' ','');
        		var msg = data;
        		var msgTitle = "title";
        		var message='{"userId":"'+topicName+'","msgType":"ORDER","msgContent":"'+msg+'","msgTitle":"'+msgTitle+'","objectId":null,"property":null,"uuId":"data_'+topicName+'"}';
        		if(topicName.indexOf('gps_')==0){
        			redis.sendMsgToRoomP2P(sClass.getObj(),message,function(){console.log("empty")});
        		}
        		if(topicName.indexOf('alarm_')==0){
        			redis.sendMsgToAll(sClass.getObj(),message,function(){console.log("empty")});
        		}
        	}
        }

        response.writeHead(500, {
            "Content-Type": "text/plain;charset=utf-8"
        });
        response.end("数据提交完毕");
    });
}
	
exports.start = start; 
exports.upload = upload;
exports.show = show;
exports.index = index;
exports.javaServlet = javaServlet;
