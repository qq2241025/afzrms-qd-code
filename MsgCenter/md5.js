var crypto = require('crypto');

function md5(str){     
var hash = crypto.createHash('md5');     
return hash.update(str+"").digest('hex').toUpperCase(); 
} 


exports.md5 = md5;