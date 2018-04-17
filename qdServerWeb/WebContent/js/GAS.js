if (!window.GAS || !GAS) {
	var GAS = window.GAS = {};
}

GAS.contextPath = "./";
GAS.debug = true;
GAS.iconPath = GAS.contextPath + 'images/';

/** 命名空间管理 */
GAS.ns = GAS.namespace = function() {
	var G = arguments.length, H = 0, E, F, e, J, I, K;
	for (; H < G; ++H) {
		e = arguments[H];
		J = arguments[H].split(".");
		K = window[J[0]];
		if (K === undefined) {
			K = window[J[0]] = {};
		}
		I = J.slice(1);
		E = I.length;
		for (F = 0; F < E; ++F) {
			K = K[I[F]] = K[I[F]] || {};
		}
	}
	return K;
};
// 转换json/array为字符串
GAS.toString = function(o) {
	var self = arguments.callee;
	var arr = [];
	var fmt = function(s) {
		if (typeof s == 'object' && s != null)
			return self(s);
		return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
	};
	if (GAS.isArray(o)) {
		for (var i = 0; i < o.length; i++) {
			arr.push(fmt(o[i]));
		}
		return '[' + arr.join(',') + ']';
	} else {
		for (var i in o) {
			arr.push("'" + i + "':" + fmt(o[i]));
		}
		return '{' + arr.join(',') + '}';
	}
};

// 属性复制
GAS.apply = function(o, c, defaults) {
	if (defaults) {
		GAS.apply(o, defaults);
	}
	if (o && c && typeof c == 'object') {
		for (var p in c) {
			o[p] = c[p];
		}
	}
	return o;
};
GAS.override = function(origclass, overrides) {
	if (overrides) {
		var p = origclass.prototype;
		GAS.apply(p, overrides);
		if (GAS.isIE() && overrides.hasOwnProperty('toString')) {
			p.toString = overrides.toString;
		}
	}
};

// 类的构造器，借用Ext的方法辅助构造
GAS.Class = function(config) {
	var F = function(c) {
		if (GAS.isObject(c)) {
			GAS.apply(this, c);
		}
	};
	return GAS.extend(F, config);
};

GAS.extend = function() {
	var io = function(o) {
		for (var m in o) {
			this[m] = o[m];
		}
	};
	var oc = Object.prototype.constructor;

	return function(sb, sp, overrides) {
		if (typeof sp == 'object') {
			overrides = sp;
			sp = sb;
			sb = overrides.constructor != oc
					? overrides.constructor
					: function() {
						sp.apply(this, arguments);
					};
		}
		var F = function() {
		}, sbp, spp = sp.prototype;

		F.prototype = spp;
		sbp = sb.prototype = new F();
		sbp.constructor = sb;
		sb.superclass = spp;
		if (spp.constructor == oc) {
			spp.constructor = sp;
		}
		sb.override = function(o) {
			GAS.override(sb, o);
		};
		sbp.superclass = sbp.supr = (function() {
			return spp;
		});
		sbp.override = io;
		GAS.override(sb, overrides);
		sb.extend = function(o) {
			return GAS.extend(sb, o);
		};
		return sb;
	};
}();

GAS.emptyFn = function() {
};

GAS.isArray = function(o) {
	return Object.prototype.toString.apply(o) === '[object Array]';
};

GAS.isFunction = function(o) {
	return Object.prototype.toString.apply(o) === '[object Function]';
};

GAS.isDate = function(o) {
	return toString.apply(o) === '[object Date]';
};

GAS.isObject = function(o) {
	return !!o && Object.prototype.toString.call(o) === '[object Object]';
};

GAS.DynamicLoadJS = function(param) {
	if (!param["url"] || !param["callback"]) {
		param.callback.call(param.scope, param);
		return;
	}
	GAS.importlib(param["url"], param["callback"], param.scope);
},

GAS.cacheUrl = {};
GAS.importlib = function(url, callback, scope) {
	url = url.replace(/[ ]/g, "");
	// 不重复加载
	GAS.log('开始下载:' + url);
	if (GAS.cacheUrl[url]) {
		console.info("callback");
		callback.call(scope, url, scope);
		return;
	} else {
		GAS.cacheUrl[url] = true;
		var head = document.getElementsByTagName("head")[0];
		var script = GAS.LoadScript.buildScriptTag(url, function() {
					GAS.log('下载完成:' + url);
					callback.call(scope, url, scope || this);
				});
		head.appendChild(script);
	}
};
GAS.loadJs = GAS.importlib;

// 动态加载
GAS.require = function(urlList, callback, scope) {
	if (GAS.isArray(urlList) && urlList.length > 0) {
		GAS.LoadScript.load(urlList, callback || GAS.emptyFn(), scope || this,
				false);
	}
};

GAS.AinB = function(A, B) {
	if (!A)
		return true;
	if (!B)
		return false;
	for (var a in A) {
		if (A[a] !== B[a]) {
			return false;
		}
	}
	return true;
};

GAS.isIE = function() {
	return /msie/.test(navigator.userAgent.toLowerCase());
};

GAS.isString = function(v) {
	return typeof v === 'string';
};
GAS.isBoolean = function(v) {
	return typeof v === 'boolean';
};
GAS.isElement = function(v) {
	return v ? !!v.tagName : false;
};
GAS.isDefined = function(v) {
	return typeof v !== 'undefined';
};

/**
 * 这是一个异步队列处理程序
 * 
 * @param list这个必须是数组或者arguments
 * @param fn
 *            每元素处理的方法，fn默认会传入当前操作的元素以及元素的索引下标
 * @param num
 *            单次处理数量
 * @param scope作用域
 * @param interval队列处理间隔时长（单位：毫秒）
 */

GAS.asyncEach = function(list, fn, num, scope, interval, pushsize) {
	if (!list || list.lengh == 0 || !fn) {
		return;
	}
	var len = list.length, num = num || 5, scope = scope || this, interval = interval
			|| 1, start = pushsize || 0, pushsize = pushsize || 1, end = len > num
			? start + num
			: len, process = function(l, s, e) {
		for (var i = s; i < e; i++) {
			if (fn.call(scope, list[i], i) === false) {
				return;
			}
		}
		if (e == len)
			return;
		setTimeout(function() {
					process(l, e, len - e > num ? e + num : len);
				}, interval);
	};
	process(list, start, end);
};

/**
 * 获取JsonArray 中指定字段的值
 * 
 */
GAS.getArray = function(jsonarray, field) {
	var arr = [];
	var len = jsonarray.length;
	for (var i = 0; i < len; i++) {
		arr.push(jsonarray[i][field]);
	}
	return arr;
};

GAS.log = function(val) {
	if (GAS.debug && window.console) {
		console.log(val);
	}
};
GAS.Warn = function(val) {
	if (GAS.debug && window.console) {
		console.warn(val);
	}
};
GAS.info = function(val) {
	if (GAS.debug && window.console) {
		console.info(val);
	}
};
GAS.error = function(val) {
	if (GAS.debug && window.console) {
		console.error(val);
	}
};

GAS.isEmpty = function(v, allowBlank) {
	return v === null || v === undefined || ((GAS.isArray(v) && !v.length))
			|| (!allowBlank ? v === '' : false);
};

GAS.each = function(array, fn, scope) {
	if (GAS.isEmpty(array, true)) {
		return;
	}
	for (var i = 0, len = array.length; i < len; i++) {
		if (fn.call(scope || array[i], array[i], i, array) === false) {
			return i;
		};
	}
};

// 获取GPS方向
GAS.getGPSDirection = function(val) {
	var dir = {};
	if ((val >= 338 && val <= 360) || (val >= 0 && val < 23)) {
		dir.key = "up";
		dir.value = "正北";
	} else if (val >= 23 && val < 68) {
		dir.key = "ne";
		dir.value = "东北";
	} else if (val >= 68 && val < 113) {
		dir.key = "go";
		dir.value = "东北";
	} else if (val >= 113 && val < 158) {
		dir.key = "es";
		dir.value = "东南";
	} else if (val >= 158 && val < 203) {
		dir.key = "down";
		dir.value = "正南";
	} else if (val >= 203 && val < 248) {
		dir.key = "sw";
		dir.value = "西南";
	} else if (val >= 248 && val < 293) {
		dir.key = "back";
		dir.value = "正西";
	} else if (val >= 293 && val < 338) {
		dir.key = "wn";
		dir.value = "西北";
	} else {
		dir.key = "unknown";
		dir.value = "未知方向";
	}
	return dir;
};
GAS.GUID = function() {
	function S4() {
		return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
				.toLowerCase();
	}
	return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4()
			+ S4() + S4());
};
/**
 * GAS动态加载js的模块
 */
GAS.LoadScript = GAS.apply({}, {
	load : function(fileList, callback, scope, preserveOrder) {
		var scope = scope || this, head = document.getElementsByTagName("head")[0], fragment = document
				.createDocumentFragment(), numFiles = fileList.length, loadedFiles = 0, me = this;
		var loadFileIndex = function(index) {
			head.appendChild(me.buildScriptTag(fileList[index], onFileLoaded));
		};
		var onFileLoaded = function() {
			loadedFiles++;
			if (numFiles == loadedFiles && typeof callback == 'function') {
				callback.call(scope);
			} else {
				if (preserveOrder === true) {
					loadFileIndex(loadedFiles);
				}
			}
		};

		if (preserveOrder === true) {
			loadFileIndex.call(this, 0);
		} else {
			GAS.each(fileList, function(file, index) {
						fragment.appendChild(this.buildScriptTag(file,
								onFileLoaded));
					}, this);
			head.appendChild(fragment);
		}
	},
	buildScriptTag : function(filename, callback) {
		var script = document.createElement('script');
		script.type = "text/javascript";
		script.src = filename;
		if (script.readyState) {
			script.onreadystatechange = function() {
				if (script.readyState == "loaded"
						|| script.readyState == "complete") {
					script.onreadystatechange = null;
					callback();
				}
			};
		} else {
			script.onload = callback;
		}
		return script;
	}
});

// JSON对象和String对象转化
GAS.JSONUtil = {
	// 格式化数字<10的前面补0
	pad : function(n) {
		return n < 10 ? "0" + n : n;
	},
	// 把字符串转化为JSON格式
	decode : function(json) {
		return eval("(" + json + ')');
	},
	// 把JSON对象转化为字符串
	encode : function(o) {
		if (typeof o == "undefined" || o === null) {
			return "null";
		} else if (GAS.isArray(o)) {// 数组
			var a = ["["], b, i, l = o.length, v;
			for (i = 0; i < l; i++) {
				v = o[i];
				switch (typeof v) {
					case "undefined" :
					case "function" :
					case "unknown" :
						break;
					default :
						if (b) {
							a.push(',');
						}
						a.push(v === null ? "null" : GAS.JSONUtil.encode(v));
						b = true;
				}
			}
			a.push("]");
			return a.join("");
		} else if (GAS.isDate(o)) {// 日期对象
			return '"' + o.getFullYear() + "-"
					+ GAS.JSONUtil.pad(o.getMonth() + 1) + "-"
					+ GAS.JSONUtil.pad(o.getDate()) + " "
					+ GAS.JSONUtil.pad(o.getHours()) + ":"
					+ GAS.JSONUtil.pad(o.getMinutes()) + ":"
					+ GAS.JSONUtil.pad(o.getSeconds()) + '"';
		} else if (typeof o == "string") {// 字符串,转义回车换行,双引号,反斜杠...等
			var m = {
				"\b" : '\\b',
				"\t" : '\\t',
				"\n" : '\\n',
				"\f" : '\\f',
				"\r" : '\\r',
				'"' : '\\"',
				"\\" : '\\\\'
			};
			if (/["\\\x00-\x1f]/.test(o)) {
				return '"' + o.replace(/([\x00-\x1f\\"])/g, function(a, b) {
							var c = m[b];
							if (c) {
								return c;
							}
							c = b.charCodeAt();
							return "\\u00" + Math.floor(c / 16).toString(16)
									+ (c % 16).toString(16);
						}) + '"';
			}
			return '"' + o + '"';
		} else if (typeof o == "number") {
			return isFinite(o) ? String(o) : "null";
		} else if (typeof o == "boolean") {
			return String(o);
		} else {// json格式的对象
			var a = ["{"], b, i, v;
			for (i in o) {
				v = o[i];
				switch (typeof v) {
					case "undefined" :
					case "function" :
					case "unknown" :
						break;
					default :
						if (b) {
							a.push(',');
						}
						a.push(GAS.JSONUtil.encode(i), ":", v === null
										? "null"
										: GAS.JSONUtil.encode(v));
						b = true;
				}
			}
			a.push("}");
			return a.join("");
		}
	}
};
GAS.encode = GAS.JSONUtil.encode;
GAS.decode = GAS.JSONUtil.decode;

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
			res = str ? GAS.decode(str)["value"] : res;
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
