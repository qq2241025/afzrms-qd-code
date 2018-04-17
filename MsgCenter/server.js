var global_config = require("./config");
var cluster = require('cluster');
var http = require("http"); 
var url  = require("url");
var logger = require("./logger");
var redis = require("./redis");
var RedisStore = require('socket.io/lib/stores/redis');
var io = require("socket.io");
var numCpus = require('os').cpus().length;
var server;
var httpPort = global_config.global_config['httpPort'];
var workers = {};
var this_io;
var thisServer;


function start(route,handle) {
	//Http 请求响应
    function onRequest(request,response) {
		var pathname=url.parse(request.url).pathname;
		logger.info("Request for"+pathname+" received."); 
		route(handle,pathname,response,request);
	}
    //集群配置
    if(cluster.isMaster){
		logger.info("[master] start master.....cpus:"+numCpus+".listen httpPort:"+httpPort);
     	//初始化：开启与CPU数相同的工作进程
     	for(var i=0;i<numCpus;i++){
       		var worker=cluster.fork();
       		workers[worker.process.pid]=worker;
      	}
      	//记录工作进程的关闭过程
      	cluster.on('exit',function(worker, code, signal){ 
        	logger.info('Worker ' + worker.process.pid + ' died.');
        });
      	//当一个工作进程结束时，重启工作进程
       	cluster.on('death',function(worker){
	   		logger.info('Worker ' + worker.process.pid + ' death.');
	        delete workers[worker.process.pid];
	        worker=cluster.fork();
	        workers[worker.process.pid]=worker;
        });
       	//监听worker的启动过程
       	cluster.on('listening',function(worker,address){
        	logger.info('A worker with #'+worker.process.pid+' is now connected to '+address.address + ':'+address.port);
        });
    }else if(cluster.isWorker){
        var pub=redis.createClient();
        var sub=redis.createClient();
        var client=redis.createClient();
		//监听http端口
        server=http.createServer(onRequest).listen(httpPort);
		io = io.listen(server, 
		{
        	'store' : new RedisStore({
          	redisPub : pub,
          	redisSub : sub,
          	redisClient : client}),
		});
    
    	//设置socket io日志级别
     	io.set('log level',2);
		//设置客户端应该在多少时间内接收到一个心跳信号
		io.set('heartbeat timeout',20);
		//设置服务器端每隔多上时间应该发一个心跳信号
		io.set('heartbeat interval',10);
	    //设置客户端在超过多少时间之后进行重新连接
		io.set('close timeout',20);

		io.set('transports', ['websocket']);
		//io.set('force new connection',true);
		io.set('max reconnection attempts',2);
	    io.set('reconnection delay',500);
	 	io.set('reconnect',false);
		//io.set('polling duration',10);
	 	
	 	/*
	  	 *监听connection
	  	 */
	  	io.sockets.on('connection',function(socket){
			logger.info("Connection "+socket.id+" accepted.");
	        //用户信息注册
			socket.on('bind',function(userInfo){
	        	logger.info("Received message:"+userInfo+"-- from client "+socket.id);
				socket.userId=userInfo.userId;
				redis.bind(socket,userInfo);
	        });
            //订阅话题
            socket.on('sub',function(msg,fn){
	        	logger.info("sub message:"+msg+"-- from client "+socket.id);
				redis.sub(socket,msg,fn);
	        });
		  	//取消订阅话题
            socket.on('unsub',function(msg,fn){
	        	logger.info("unsub message:"+msg+"-- from client "+socket.id);
				redis.unsub(socket,msg,fn);
			});
            //向指定人员发送消息
			socket.on('sendMsgToUser',function(msg,fn){
	        	logger.info("Received message:"+msg+"-- from client "+socket.id);
				redis.sendMessageToUser(io,msg,fn);
			});
			//向指定房间发送消息
            socket.on('sendMsgToRoom',function(msg,fn){
	        	logger.info("Received room message:"+msg+"-- from client "+socket.id);
				redis.sendMsgToRoom(socket,msg,fn);
	        });
			//向指定房间发送消息P2P
            socket.on('sendMsgToRoomP2P',function(msg,fn){
		        logger.info("Received room message:"+msg+"-- from client "+socket.id);
				redis.sendMsgToRoomP2P(io,msg,fn);
	        });
			//消息接收成功回调函数
            socket.on('clear',function(msg){
		        logger.info("Received room callback message:"+msg+"-- from client "+socket.id);
				redis.clear(socket,msg);
	        });
			//向所有人发送消息
			socket.on('sendMsgToAll',function(msg,fn){
		        logger.info("Received all message:"+msg+"-- from client "+socket.id);
				redis.sendMsgToAll(io,msg,fn);
	        });
			//监听connect断开连接
			socket.on('disconnect',function(){
				logger.info("Connection "+socket.id+" terminated.");
				redis.unbind(socket);
			});	
		});
		this_io = io;
	}
	//当master关闭时，同时关闭所有worker
    process.on('SIGTERM',function(){
    	for(var pid in workers){
	   		console.info('kill worker pid:'+pid);
       		process.kill(pid);
      	}
    	process.exit(0);
    });

    var Single = require('./singleClass');
    var s = new Single(this_io);
    s.getInstance();
}

function getSocketIo(){
	return this_io;
}
exports.start = start;
exports.getSocketIo = getSocketIo;
