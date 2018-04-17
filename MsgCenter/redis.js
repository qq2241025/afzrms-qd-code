var global_config = require("./config");
// var genericPool  = require("/usr/local/lib/node_modules/generic-pool");
// var redis = require("/usr/local/lib/node_modules/socket.io/node_modules/redis");
var genericPool  = require("generic-pool");
var redis = require("socket.io/node_modules/redis");
var logger = require("./logger");
var md5 =  require("./md5")
var port=global_config.global_config['redisPort'];
var url=global_config.global_config['redisUrl'];
var password=global_config.global_config['redisPassword'];

//redis连接池配置
var pool = genericPool.Pool({
	name    :'redis',
 	create  :function(callback) //创建连接实例
	{
    	var client=redis.createClient(port,url);
        client.auth(password);
		callback(null,client);
	},
 	destroy  :function(client){client.quit();}, //超时则释放连接
 	max      :10, //最大连接数
 	min      :5,//最小连接数
 	reapIntervalMillis :300000, //每隔多长时间去检查一次连接
 	idleTimeoutMillis : 600000,  //超时时间
 	log      : true,
});

/**
 *创建客户端连接
 */
var createClient = function(){
	var redisClient = redis.createClient(port,url);
	redisClient.auth(password);
	return redisClient;
};


/**
 * 将userID 同  socket ID绑定  {"userId":"lty"}
 */
var bind = function(socket, user) {
	user=JSON.parse(user);
	logger.info("============bind user=============="+user);
	pool.acquire(function(err, client) {
		//保存用户信息到redis
		if (user && user.userId) {
			client.set('SocketId_' + user.userId, socket.id, function(error) {
				if (error) {
					logger.error("set userid to redis error");
				}
			});

			// 读取个人缓存池中的消息
			client.lrange('MsgList_' + user.userId, 0, -1, function(error, msgIds) {
				if (error) {
					logger.error("range message from redis error");
				}
                //logger.info("================msgIds:"+msgIds);
			
			       if(msgIds && 0 < msgIds.length)
						{
						client.mget(msgIds,function(error,msgs ){
							if (error) {
							logger.error("multiGet message from redis error!msgIds="+msgIds);
						}
                            //logger.info("*******************msgs="+msgs);
							if(msgs)
							{
						 	socket.emit('message', '['+msgs+']', function(result) {
							//如果客户已经接收到推送的消息
							logger.info("**********client had reply! clear msgIds");
							for(var i in result)
							{
							 client.lrem('MsgList_' + user.userId,0, result[i]);
							 client.del(result[i]);
							}
						   });
						    }

						});
					   }


			});
			pool.release(client);
		}
	});
};

var unbind = function(socket){
	logger.info("============unbind user=============="+socket.id);
	pool.acquire(function(err, client) {
                //删除用户链接信息到redis
		// TODO
		client.keys('SocketId_*',function(error,keys){
			if(error){
				logger.error("keys SocketId_* from redis error");
			}
			for(var i = 0, len = keys.length; i < len; i++) {
   	 			var keyId = keys[i];
				//console.log("keyId========"+keyId);
				var userId = keyId.substring('SocketId_'.length);
				//console.log("userId======="+userId);
				var flag = false;
				client.mget(keyId,function(error,socketId){
					if(error){
						console.log("mget from redis error");
					}
					//console.log("socketId====="+socketId);
					if(socketId){
						//console.log("socketId====="+socketId);
						if (socketId == socket.id) {
							client.del('SocketId_' + userId, function(error) {
								if (error) {
									logger.error("del socket from redis error");
								}
							});

							// 删除个人缓存池中的消息
							client.del('MsgList_' + userId, function(error, msgIds) {
								if (error) {
									logger.error("del message from redis error");
								}
							});
							//console.log("flag=====true");
							flag = true;
						}
					}
				});
				//console.log("flag======----="+flag);
				if(flag)break;
			}
		});
		pool.release(client);
        });
};

/**
 * 订阅topic  {"userId":"lty","tags":["123","456"]}
 */
var sub =function(socket,topic,fn)
{
 logger.info("============sub topic=============="+topic);
 topic=JSON.parse(topic);
 if(topic.userId && topic.tags )
	{
    fn("success");
    pool.acquire(function(err,client){
    
	  for(var i in topic.tags)    
		{
           var tag=topic.tags[i];
         
			 if(tag)
			  {
			   //订阅主题
			   socket.join(tag);
			   //房间添加用户信息
			   client.sadd(tag,topic.userId);
			   // 查是否有gps数据，有直接先推一条
			   msgTag = "gps_" + tag;
			   client.smembers(msgTag,function(err,replies){
					logger.info("replies:"+replies.length);
					replies.forEach(function(reply,i){
						// 读取个人缓存池中的消息
						client.get('SocketId_' + reply, function(err, socketId) {
							//logger.info(reply+ "'s redis socketId is " + socketId);
							if(socketId){
								msgId = "data_gps_"+tag;
								client.get(msgId,function(error,msgs){
									if (error) {
										//logger.error("Get message from redis error!msgId="+msgId);
									}else{
										//logger.info("*******************msgs="+msgs);
										socket(socketId.toString()).emit('message', '['+msgs+']', function(result) {
											//如果客户已经接收到推送的消息
											logger.info("**********client had reply! clear msgId");
										});
									}
								});
							}
						});
					});
			   });
			  }
		}
		
		   pool.release(client);
 });
 }
};

/**
 * 解除订阅topic  {"userId":"lty","tags":["123","456"]}
 */
var unsub =function(socket,topic,fn)
{
  logger.info("============unsub topic=============="+topic);
  topic=JSON.parse(topic);
 if(topic.userId && topic.tags )
	{
    fn("success");
    pool.acquire(function(err,client){
    
	  for(var i in topic.tags)    
		{
           var tag=topic.tags[i];
         
			 if(tag)
			  {
			   //解除订阅主题
			   socket.leave(tag);
			   //房间删除用户信息
			   client.srem(tag,topic.userId);
			  }
			}
		
		   pool.release(client);
 });
 }
};

/**
 * 向指定用户推送消息  msg:{"userId":"lty","msgType":"ORDER","msgContent":"11223344","msgTitle":"11223344","objectId":null,"property":null,"uuId":"ed0c3481-ca7b-42b3-9a25-656835a1a8baadmin"}
 */
var sendMessageToUser = function(io, msg,fn) {
	logger.info("============send message=============="+msg);
	msg=JSON.parse(msg);
	if (msg && msg.userId) {
		fn(msg.uuId);
		var userId = msg.userId;
		pool.acquire(function(e, client) {
			client.get('SocketId_' + userId, function(err, socketId) {
				logger.info(userId+ "'s redis socketId is " + socketId);
				//消息推送
				if (socketId && msg.uuId) {
					logger.info("send message <" + msg + "> to " + socketId);
					
					// 保存用户消息列表
					client.lpush('MsgList_' + userId,msg.uuId, function(error, reply) {
						if (error) {
							logger.error("set message uuId to redis error");
						}
					});
                    //保存消息详情
					client.set(msg.uuId,JSON.stringify(msg),function(err,reply)
					{
					   	if(err) {
							logger.error("set msg detail to redis error! uuId="+msg.uuId);
					   	}
					}
					);

					// 读取个人缓存池中的消息
					client.lrange('MsgList_' + userId, 0, -1, function(error, msgIds) {
						if (error) {
							logger.error("range message from redis error! userId="+userId);
						}
                          
						logger.info("==============uuId List:"+msgIds);
                         
						if(msgIds && 0 < msgIds.length) {
							client.mget(msgIds,function(error,msgs){
								if (error) {
									logger.error("multiGet message from redis error!msgIds="+msgIds);
								}
	                            logger.info("*******************msgs="+msgs);
								if(msgs) {
								 	io.sockets.socket(socketId.toString()).emit('message', '['+msgs+']', function(result) {
										//如果客户已经接收到推送的消息
										logger.info("**********client had reply! clear msgIds");
										for(var i in result) {
											client.lrem('MsgList_' + msg.userId,0, result[i]);
											client.del(result[i]);
										}
									});
								}
							});
						}
					});
				}
			});
			pool.release(client);
		});
	}
};

/**
 * 向指定房间推送消息msg--msg:{"userId":"4430001","msgType":"ORDER","msgContent":"11223344","msgTitle":"11223344","objectId":null,"property":null,"uuId":"ed0c3481-ca7b-42b3-9a25-656835a1a8ba"}
 */
var sendMsgToRoom = function(socket,msg,fn)
{
	logger.info("============sendMsgToRoom=============="+msg);
   	msg=JSON.parse(msg);
   	if(msg && msg.userId)
   	{
		fn(msg.uuId);
	  	var msgTag=msg.userId;
	  	//将群发消息 保存到每个用户的消息list中
	  	pool.acquire(function(err,client){
		  	client.smembers(msg.userId,function(err,replies){
			  	logger.info("*************replies:"+replies.length);
			  	replies.forEach(function(reply,i){
			   		msg.userId=reply;
		       		msg.uuId=msg.uuId+reply;
			   		logger.info("*************userId:"+reply);
			   		// 保存用户消息列表
					client.lpush('MsgList_' + reply,msg.uuId, function(error, reply) {
						if (error) {
							logger.error("set message uuId to redis error");
						}
					});

			     	//保存消息详情
					client.set(msg.uuId,JSON.stringify(msg),function(err,reply) {
						if (err) {
							logger.error("set msg detail to redis error! uuId="+msg.uuId);
						}
					});
			   	});
			});
		  	pool.release(client);
		});
	socket.broadcast.to(msgTag).emit('message','['+JSON.stringify(msg)+']');
	logger.info("============sendMsgToRoom end==============");
   	}

 }





/**
 *
 * 点对点向指定房间推送消息msg--msg:{"userId":"4430001","msgType":"ORDER","msgContent":"11223344","msgTitle":"11223344","objectId":null,"property":null,"uuId":"ed0c3481-ca7b-42b3-9a25-656835a1a8ba"}
 */
var sendMsgToRoomP2P = function(io,msg,fn)
{
	logger.info("============sendMsgToRoomP2P=============="+msg);
   	msg=JSON.parse(msg);
   	if(msg && msg.userId){
		var msgTag=msg.userId;
	  	//将群发消息 保存到每个用户的消息list中
	  	pool.acquire(function(err,client){
			client.smembers(msgTag,function(err,replies){
				logger.info("replies:"+replies.length);
				replies.forEach(function(reply,i){
					msg.userId=reply;
					//msg.uuId=msg.uuId+reply;
					fn(msg.uuId);
					// 判断是否还在连接
					client.get('SocketId_' + reply, function(err, socketId) {
                                                //logger.info("error======"+err+",reply==="+reply+ "'s redis socketId is " + socketId);
						if(err){
							logger.error("get SocketId_ error");
						}
                                                if(!socketId){
							logger.error("socket is already disconnect,delete");
							client.srem(msgTag,reply,function(error, reply){
								if(error){
									logger.error("srem topic from gps_ error");
								}
							});
							return;
						}
					});
					// 保存用户消息列表
					// TODO
					client.lpop('MsgList_' + reply, function(error, reply) {
                                                if (error) {
                                                        logger.error("pop message from redis error");
                                                }
                                        });
					client.lpush('MsgList_' + reply,msg.uuId, function(error, reply) {
						if (error) {
							logger.error("set message uuId to redis error");
						}
					});
					//保存消息详情
					client.set(msg.uuId,JSON.stringify(msg),function(err,reply){
						if(err){
							logger.error("set msg detail to redis error! uuId="+msg.uuId);
						}
					});
					// 读取个人缓存池中的消息
					client.get('SocketId_' + reply, function(err, socketId) {
						//logger.info(reply+ "'s redis socketId is " + socketId);
						if(socketId){
							client.lrange('MsgList_' + reply, 0, -1, function(error, msgIds) {
								if (error) {
									logger.error("range message from redis error! userId="+userId);
								}
								//logger.info("==============uuId List:"+msgIds);
								if(msgIds && 0 < msgIds.length ){
									client.mget(msgIds,function(error,msgs ){
										if (error) {
											logger.error("multiGet message from redis error!msgIds="+msgIds);
										}
										//logger.info("*******************msgs="+msgs);
										io.sockets.socket(socketId.toString()).emit('message', '['+msgs+']', function(result) {
											//如果客户已经接收到推送的消息
											logger.info("**********client had reply! clear msgIds");
											for(var i in result){
												client.lrem('MsgList_' + reply,0, result[i]);
												client.del(result[i]);
											}
										});
									});
								}
							});
						}
					});
				});
			});
	  		pool.release(client);
	  	});
	  	logger.info("============sendMsgToRoomP2P end==============");
   	}
};




/**
 * 清空 userID 已经收到的消息  {"userId":"lty","uuIds":["123","456"]}
 */
var clear = function(socket, msg) {
	//logger.info("============clear message=============="+msg);
	msg=JSON.parse(msg);
	//logger.info("============msg.userId=============="+msg.userId);
	pool.acquire(function(err, client) {
		if (msg && msg.userId && msg.uuIds) {
			for(var i in msg.uuIds)
			// 从个人消息缓存池中移除
			{
				client.lrem('MsgList_' + msg.userId, 0, msg.uuIds[i]);
				client.del(msg.uuIds[i]);
			}
		}
		pool.release(client);
	});
};


/**
 *
 * 向所有人推送消息msg--{"userId":"4430001","msgType":"ORDER","msgContent":"11223344","msgTitle":"11223344","objectId":null,"property":null,"uuId":"ed0c3481-ca7b-42b3-9a25-656835a1a8ba"}
 */
var sendMsgToAll = function(io,msg,fn)
{
	logger.info("============sendMsgToAll=============="+msg);
   	msg=JSON.parse(msg);
   	fn(msg.uuId);
   	io.sockets.emit('message','['+JSON.stringify(msg)+']');
};

//模块导出
exports.bind = bind;
exports.unbind = unbind;
exports.sendMessageToUser = sendMessageToUser;
exports.sendMsgToRoom = sendMsgToRoom;
exports.sendMsgToRoomP2P = sendMsgToRoomP2P;
exports.sendMsgToAll = sendMsgToAll;
exports.redis = redis;
exports.sub = sub;
exports.unsub = unsub;
exports.clear = clear;
exports.pool = pool;
exports.createClient=createClient;

