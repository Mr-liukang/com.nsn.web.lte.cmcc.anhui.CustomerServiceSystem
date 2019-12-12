var N = {
    version: "1.0"
};
/*
 * @namespace Util
 *
 * Various utility functions, used by nsn do web ui.
 */
N.Util = {
    // @function extend(dest: Object, src?: Object): Object
    // Merges the properties of the `src` object (or multiple objects) into `dest` object and returns the latter.
    extend: function(dest) {
        var i, j, len, src;

        for (j = 1, len = arguments.length; j < len; j++) {
            src = arguments[j];
            for (i in src) {
                dest[i] = src[i];
            }
        }
        return dest;
    },

    // @function create(proto: Object, properties?: Object): Object
    // Compatibility polyfill for [Object.create](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Object/create)
    create: Object.create || (function() {
        function F() {}
        return function(proto) {
            F.prototype = proto;
            return new F();
        };
    })(),

    // @function formatNum(num: Number, digits?: Number): Number
    // Returns the number `num` rounded to `digits` decimals, or to 5 decimals by default.
    formatNum: function(num, digits) {
        if (!num) {
            return "";
        } else if (typeof num == "number") {
            return num.toFixed(digits);
        } else {
            return num;
        }
    },

    // formatDate(date,"yyyy-MM-dd HH:mm"); optput  "2015-10-14 16:50"
    formatDate: function(now, mask) {
        var d = now;
        var zeroize = function(value, length) {
            if (!length) length = 2;
            value = String(value);
            for (var i = 0, zeros = ''; i < (length - value.length); i++) {
                zeros += '0';
            }
            return zeros + value;
        };

        return mask.replace(/"[^"]*"|'[^']*'|\b(?:d{1,4}|m{1,4}|yy(?:yy)?|([hHMstT])\1?|[lLZ])\b/g, function($0) {
            switch ($0) {
                case 'd':
                    return d.getDate();
                case 'dd':
                    return zeroize(d.getDate());
                case 'ddd':
                    return ['Sun', 'Mon', 'Tue', 'Wed', 'Thr', 'Fri', 'Sat'][d.getDay()];
                case 'dddd':
                    return ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][d.getDay()];
                case 'M':
                    return d.getMonth() + 1;
                case 'MM':
                    return zeroize(d.getMonth() + 1);
                case 'MMM':
                    return ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][d.getMonth()];
                case 'MMMM':
                    return ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'][d.getMonth()];
                case 'yy':
                    return String(d.getFullYear()).substr(2);
                case 'yyyy':
                    return d.getFullYear();
                case 'h':
                    return d.getHours() % 12 || 12;
                case 'hh':
                    return zeroize(d.getHours() % 12 || 12);
                case 'H':
                    return d.getHours();
                case 'HH':
                    return zeroize(d.getHours());
                case 'm':
                    return d.getMinutes();
                case 'mm':
                    return zeroize(d.getMinutes());
                case 's':
                    return d.getSeconds();
                case 'ss':
                    return zeroize(d.getSeconds());
                case 'l':
                    return zeroize(d.getMilliseconds(), 3);
                case 'L':
                    var m = d.getMilliseconds();
                    if (m > 99) m = Math.round(m / 10);
                    return zeroize(m);
                case 'tt':
                    return d.getHours() < 12 ? 'am' : 'pm';
                case 'TT':
                    return d.getHours() < 12 ? 'AM' : 'PM';
                case 'Z':
                    return d.toUTCString().match(/[A-Z]+$/);
                    // Return quoted strings with the surrounding quotes removed
                default:
                    return $0.substr(1, $0.length - 2);
            }
        });
    },

    // @function trim(str: String): String
    // Compatibility polyfill for [String.prototype.trim](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/String/Trim)
    trim: function(str) {
        return str.trim ? str.trim() : str.replace(/^\s+|\s+$/g, '');
    },

    // @function splitWords(str: String): String[]
    // Trims and splits the string on whitespace and returns the array of parts.
    splitWords: function(str) {
        return N.Util.trim(str).split(/\s+/);
    },

    // @function setOptions(obj: Object, options: Object): Object
    // Merges the given properties to the `options` of the `obj` object, returning the resulting options. See `Class options`.
    setOptions: function(obj, options) {
        if (!obj.hasOwnProperty('options')) {
            obj.options = obj.options ? N.Util.create(obj.options) : {};
        }
        for (var i in options) {
            obj.options[i] = options[i];
        }
        return obj.options;
    },

    // @function getParamString(obj: Object, existingUrl?: String, uppercase?: Boolean): String
    // Converts an object into a parameter URL string, e.g. `{a: "foo", b: "bar"}`
    // translates to `'?a=foo&b=bar'`. If `existingUrl` is set, the parameters will
    // be appended at the end. If `uppercase` is `true`, the parameter names will
    // be uppercased (e.g. `'?A=foo&B=bar'`)
    getParamString: function(obj, existingUrl, uppercase) {
        var params = [];
        for (var i in obj) {
            params.push(encodeURIComponent(uppercase ? i.toUpperCase() : i) + '=' + encodeURIComponent(obj[i]));
        }
        return ((!existingUrl || existingUrl.indexOf('?') === -1) ? '?' : '&') + params.join('&');
    },
    queryString : function(val) {
		var uri = window.location.search;
		var re = new RegExp("" +val+ "=([^&?]*)", "ig");
		return ((uri.match(re))?decodeURIComponent(uri.match(re)[0].substr(val.length+1)):null);
	},
    // @function template(str: String, data: Object): String
    // Simple templating facility, accepts a template string of the form `'Hello {a}, {b}'`
    // and a data object like `{a: 'foo', b: 'bar'}`, returns evaluated string
    // `('Hello foo, bar')`. You can also specify functions instead of strings for
    // data values — they will be evaluated passing `data` as an argument.
    template: function(str, data) {
        return str.replace(N.Util.templateRe, function(str, key) {
            var value = data[key];

            if (value === undefined) {
                throw new Error('No value provided for variable ' + str);

            } else if (typeof value === 'function') {
                value = value(data);
            }
            return value;
        });
    },

    templateRe: /\{ *([\w_\-]+) *\}/g,

    // @function isArray(obj): Boolean
    // Compatibility polyfill for [Array.isArray](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Array/isArray)
    isArray: Array.isArray || function(obj) {
        return (Object.prototype.toString.call(obj) === '[object Array]');
    },
    loadForm: function(form, data) {
        this.clearForm(form);
        var $form = $('#' + form);
        for (var name in data) {
            var val = data[name];
            var dom = $form.find('input[name="' + name + '"][type=radio], input[name="' + name + '"][type=checkbox]');
            if (dom.length) {
                dom.prop('checked', false);
                dom.each(function() {
                    var v = $(this).val();
                    if (v == String(val) || $.inArray(v, $.isArray(val) ? val : [val]) >= 0) {
                        $(this).prop('checked', true);
                    }
                });
            } else {
                $form.find('input[name="' + name + '"]').val(val);
                $form.find('textarea[name="' + name + '"]').val(val);
                $form.find('select[name="' + name + '"]').val(val);
            }
        }
    },
    clearForm: function(form) {
        var $form = $('#' + form);
        $('input,select,textarea', $form).each(function() {
            var t = this.type,
                tag = this.tagName.toLowerCase();
            if (t == 'text' || t == 'hidden' || t == 'password' || tag == 'textarea') {
                this.value = '';
            } else if (t == 'file') {
                var file = $(this);
                var newfile = file.clone().val('');
                newfile.insertAfter(file);
                file.remove();
            } else if (t == 'checkbox' || t == 'radio') {
                this.checked = false;
            } else if (tag == 'select') {
                this.selectedIndex = -1;
            }
        });
    },
    //重新加载DataTable数据
    reloadData: function(dataTable) {
        $("#" + dataTable).DataTable().ajax.reload();
    },
    //form表单转json对象
    serializeJson: function(form) {
        var $form = $('#' + form);
        var serializeObj = {};
        var array = $form.serializeArray();
        $(array).each(function() {
            if (serializeObj[this.name]) {
                if ($.isArray(serializeObj[this.name])) {
                    serializeObj[this.name].push(this.value);
                } else {
                    serializeObj[this.name] = [serializeObj[this.name], this.value];
                }
            } else {
                serializeObj[this.name] = this.value;
            }
        });
        return serializeObj;
    },
    //校验是否整数
    chkInt: function(str) {
        var patrn = /^\d+$/;
        return patrn.test(str);
    },
    //校验是数字
    chkNum: function(str) {
        var patrn = /^[+-]?\d+(\.\d+)?$/;
        return patrn.test(str);
    },
    num: function(num) {
        if (!N.DESENSITIZATION) { //不脱敏
            return num;
        }
        if (typeof num == 'number') {
            num = num.toString();
        }
        if (num.length == 11) {
            return num.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
        } else if (num.length == 13) {
            return num.replace(/(\d{5})\d{4}(\d{4})/, "$1****$2");
        } else if (num.length == 14) {
            return num.replace(/(\d{6})\d{4}(\d{4})/, "$1****$2");
        } else if (num.length == 15) {
            return num.replace(/(\d{7})\d{4}(\d{4})/, "$1****$2");
        } else if (num.length > 4) {
            return num.substr(0, num.length - 4) + '****';
        } else {
            var str = num.substr(0, 1);
            for (var i = 1; i < num.length; i++) {
                str += '*'
            }
            return str;
        }
    },
    download: function(url, data, method) {
        // 获得url和data
        if (!url) {
            return;
        }
        layer.load(1, {shade: [0.3,'#000'] });
        $.post(url, data, function(json) {
            var inputs = '';
            if (json) {
                for (var key in json) {
                    inputs += "<input type='hidden' name='" + key + "' value='" + json[key] + "' />";
                }
            };
            $form = $('<form action="'+ctx+'/page/download" method="' + (method || 'post') + '">' + inputs + '</form>').appendTo('body').submit().remove();
            layer.closeAll('loading');
        }, "json");
        /*var inputs = '';
        if (data) {
            for (var key in data) {
                inputs += "<input type='hidden' name='" + key + "' value='" + data[key] + "' />";
            }
        };
        // request发送请求
        var $form = $('<form action="' + url + '" method="' + (method || 'post') + '">' + inputs + '</form>').appendTo('body');
        $form.submit().remove();*/
    },
    cities: function(htmlid) {
        var cts = [];
        $.each(N.CITIES, function(i, city) {
            cts.push("<option value='" + city.city_id + "'>" + city.city_cn + "</option>");
        });
        $("#" + htmlid).append(cts.join('<br>'));
    }
};
N.Class = function() {};
N.Class.extend = function(props) {
    // @function extend(props: Object): Function
    // [Extends the current class](#class-inheritance) given the properties to be included.
    // Returns a Javascript function that is a class constructor (to be called with `new`).
    var NewClass = function() {
        // call the constructor
        if (this.initialize) {
            this.initialize.apply(this, arguments);
        }
    };

    var parentProto = NewClass.__super__ = this.prototype;

    var proto = N.Util.create(parentProto);
    proto.constructor = NewClass;

    NewClass.prototype = proto;

    // inherit parent's statics
    for (var i in this) {
        if (this.hasOwnProperty(i) && i !== 'prototype') {
            NewClass[i] = this[i];
        }
    }

    // mix static properties into the class
    if (props.statics) {
        N.Util.extend(NewClass, props.statics);
        delete props.statics;
    }

    // mix includes into the prototype
    if (props.includes) {
        N.Util.extend.apply(null, [proto].concat(props.includes));
        delete props.includes;
    }

    // merge options
    if (proto.options) {
        props.options = N.Util.extend(N.Util.create(proto.options), props.options);
    }

    // mix given properties into the prototype
    N.Util.extend(proto, props);

    return NewClass;
};
