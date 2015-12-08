/**
 * 此组件用来处理 首次点击， 处理是否全选操作
 *     我们规定语义， 如果你的 text 需要 kissfocus 效果， 那么可以为 input[type='text'] 添加一个自定义属性
 *     kissfocus='on'
 *
 */
;
(function () {
    App.namespace("Module.KissFocus");

    var KissFocus = function () {
        this._$inst = null;
    };

    var handlerMap = {
        "kissClick":function (event) {
            var $this = $(event.currentTarget);
            if (!$this.is("input[type='text']")) {
                return;
            }

            if ($this.hasClass(".J_active")) {
                return;
            } else {
                $this.addClass(".J_active");
                $this.select();
            }
        },
        "kissBlur":function (event) {
            var $this = $(event.currentTarget);
            $this.removeClass(".J_active");
        }
    };

    /**
     * 开启处理：首次点击全选操作
     * @param selector
     */
    KissFocus.prototype.start = function (selector) {
        if (this._$inst) {
            return this;
        }

        this._$inst = $(selector).filter("input[kissfocus='on']");
        if (!this._$inst[0]) {
            return this;
        }

        this._$inst.each(function(index, ele) {
            if(!App.Events.hasBind("click", ele, handlerMap.kissClick)) {
                $(ele).bind("click", handlerMap.kissClick);
            }

            if(!App.Events.hasBind("blur", ele, handlerMap.kissBlur)) {
                $(ele).bind("blur", handlerMap.kissBlur);
            }
        });

        return this;
    };

    /**
     * 停止处理：首次点击全选操作
     */
    KissFocus.prototype.stop = function () {
        if (!this._$inst) {
            return this;
        }

        this._$inst
            .unbind("click", handlerMap.kissClick)
            .unbind("blur", handlerMap.kissBlur);
        this._$inst = null;

        return this;
    };

    App.Module.KissFocus = KissFocus;
})();


/**
 * 检测的部分, 依赖 windjs , 一个 javascript 计算表达式库
 *     这里之所以使用 window load 事件， 主要是考虑到页面的不确定性， 在 window 内容都加载完成后， 再绑定动作，
 *     做到功能的 平稳进化
 **/
$(window).load(function () {
    var kissFocus = new App.Module.KissFocus(),
        interval = 3000;

    kissFocus.autoCheck = true;

    try {
        var focusAsync = eval(Wind.compile("async", function (kissFocus) {
            kissFocus
                .stop(kissFocus)
                .start($("input[type='text']"));
        }));

        var pollingAsync = eval(Wind.compile("async", function (kissFocus, interval) {
            var i = 0;

            $await(focusAsync(kissFocus));
            while (kissFocus.autoCheck) {
                G.debug("kiss focus readd");
                G.debug("This is the " + i + " times' check");
                $await(Wind.Async.sleep(interval));
                $await(focusAsync(kissFocus));
                ++i;
            }
        }));

        pollingAsync(kissFocus, interval).start();
    } catch (e) {
        G.error("Can't found wind.js , please load it before!");
    }

});
