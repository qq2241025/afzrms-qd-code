// var log4js = require('/usr/local/lib/node_modules/log4js');
var log4js = require('log4js');

log4js.configure({  
       appenders: [  
       { type: 'console' }, {  
         type: 'dateFile',  
         filename: './logs/access.log',  
         pattern: "_yyyy-MM-dd",  
         maxLogSize: 1024,  
         alwaysIncludePattern: false,  
         backups: 4,  
         category: 'normal'  
      }  
],  
replaceConsole: true  
});  
 
 var logger=log4js.getLogger('normal');

 function info(msg)
 {
 logger.info(msg);
 }

  function error(error)
 {
 logger.error(error);
 }

exports.info = info;
exports.error = error;
