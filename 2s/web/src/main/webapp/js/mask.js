/**
 * 此文件依赖 js/$-1.4.2.js 或者 js/$-1.4.2.min.js
 *
 * 修改者: 潘震 2012-06-19
 * email:   karelpan@gmail.com
 */
(function() {
    var shadow = {
        getWidth: function() {
            return document.documentElement.clientWidth>document.documentElement.scrollWidth?document.documentElement.clientWidth:document.documentElement.scrollWidth;
        },
        getHeight: function() {
            return document.documentElement.scrollHeight;
        }
    };
    window.Mask = {
        cover: function() {
            var foo = $("#mask");
            if(foo[0]) {
                foo.attr('className', 'b');
                foo.css({
                    'display': 'block',
                    'position': 'fixed',
                    'top': '0px',
                    'left': '0px',
                    //修改在Chrome下的兼容 by liyi
//                    'width': shadow.getWidth() + "px",
//                    'height': shadow.getHeight() + "px",
                    'width':"100%",
                    "height":"100%",
                    'z-index': '3',
                    'filter': "alpha(opacity=40)",
                    'opacity': '0.4',
                    'background-color': '#000000'
                });
            } else {
                throw new Error("No id called \"mask\" ")
            }
        },
        Login: function () {
            window.Mask.cover();
        },
        Logout: function () {
            var foo = $("#mask");
            if (foo[0]) {
                foo.removeAttr('className');
                foo.hide();
            }
        }
    };
})();