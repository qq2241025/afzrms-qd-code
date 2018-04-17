

1. 修改js/config.js下的文件即可
 1.1  修改webSocketUrl 地址
 1.2  修改 mapTileUrl 地图取图地址URL
 1.3  使用nginx 代理 修改 globalProxy = "aa" 参数
 
 nginx 配置
   location /aa {
        proxy_set_header  Host $host;
        proxy_set_header  X-Real-IP  $remote_addr;
	    proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
	    proxy_set_header  Cookie $http_cookie;
	    proxy_cookie_path /afzrms-qd-server /;  //配置验证码
	    #proxy_pass http://218.58.56.113:8080/afzrms-qd-server;
	    #proxy_pass http://10.17.131.2:8080/afzrms-qd-server;
	    proxy_pass  http://127.0.0.1:8080/afzrms-qd-server;
	}
	
	location /demo {
            alias F:\SoToWorkspace\DWebGis\web;
	    index  index.html index.htm;
	    error_page 405 =200 $uri; //配置nginx get 获取本地化文件的bug
	}