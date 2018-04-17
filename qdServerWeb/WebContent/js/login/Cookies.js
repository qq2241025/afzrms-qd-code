(function() {
	/**
	 * zhengang.he@autonavi.com
	 * 
	 * @param {Object}
	 *            config
	 */
	GAS.Cookie = {
		setCookie : function(key, value, days) {
			var date = new Date();
			date.setTime(date.getTime() + (10 * 24 * 60 * 60 * 1000));
			if (days) {
				date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
			}
			var expires = date.toGMTString();
			var val = GAS.toString({
						value : value,
						expires : expires
					});
			document.cookie = key + "=" + val;
		},
		getCookie : function(name) {
			var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)"), res = null;
			if (arr = document.cookie.match(reg)) {
				var str = unescape(arr[2]);
				res = str ? Ext.decode(str)["value"] : res;
			}
			return res;
		},
		deleteCookie : function(key) {
			var exp = new Date();
			exp.setTime(exp.getTime() - 1);
			var cval = GAS.Cookie.getCookie(key);
			if (cval != null)
				var val = "{value:'" + path + "',expires:'" + exp.toGMTString
						+ "'}";
			document.cookie = key + "=" + val;
		}
	};
})();
