/****************************************
 *
 * Author: Lee
 * Description: table style handler util
 * Dependence: jQuery
 *
 *****************************************/

var tableUtil = {};

tableUtil.tableStyle = function(tableName, exceptRows, oddOrEven) {
	oddOrEven = oddOrEven || 'odd';

	if(!exceptRows) {
		exceptRows = '.-';
	}
	//隔行背景色设置
	$(tableName + ' tr:not(' + exceptRows + '):' + oddOrEven).css({
		'background': '#EAEAEA'
	});

	//表名为集合
	if($(tableName).length > 1) {
		$(tableName).each(function(i) {
			//行悬浮显示颜色var currentColor, rowIndex;
			$('tr', $(tableName).eq(i)).not(exceptRows).hover(function() {
				rowIndex = $(this).prevAll('tr').length;

				var objStr = $('tr:eq(' + rowIndex + ')', $(tableName).eq(i));
                $(objStr)
                    .not('.table-row-title')
                    .addClass('table-row-highlight');

			}, function() {
				var objStr = $('tr:eq(' + rowIndex + ')', $(tableName).eq(i));
                $(objStr)
                    .not('.table-row-title')
                    .removeClass('table-row-highlight');

                var $hackDiv = $("<div id='brower_render_hack_div'></div>");
                $hackDiv
                    .appendTo(document.body)
                    .remove();
			});
		});
	} else {
		//行悬浮显示颜色var currentColor, rowIndex;
		$('tr', tableName).not(exceptRows).hover(function() {
			//currentColor = $(this).css('background-color');
			rowIndex = $(this).prevAll('tr').length;

			var objStr = tableName + ' tr:eq(' + rowIndex + ')';
			$(objStr)
                .not('.table-row-title')
                .addClass('table-row-highlight');
		}, function() {
			var objStr = tableName + ' tr:eq(' + rowIndex + ')';
			$(objStr)
                .not('.table-row-title')
                .removeClass('table-row-highlight');
		});
	}

};

tableUtil.tableStyle.hasSubTable = function(tableName, subTableName, exceptRows, oddOrEven, isShow, isSameLevel) {
	var currentColor, rowIndex, objStr;
	var sameLevel = isSameLevel || 'false';
	var hasShow = false;
	oddOrEven = oddOrEven || 'odd';

	if(isShow != undefined) {
		hasShow = isShow;
	}

	//奇偶行设置不同的背景色
	var setRowBackgroundColor = function() { /*主表*/
			$(tableName + ' tr:not(' + exceptRows + '):' + oddOrEven).css({
				'background': '#EAEAEA'
			});

			/*副表*/
			$(subTableName + ' tr:not(' + exceptRows + '):' + oddOrEven).css({
				'background': '#EAEAEA'
			});
		};

	//得到对象字符串
	var getObjStr = function(rowIndex, currRow) {
			var ObjectString = '';
			if(sameLevel == 'true') {
				ObjectString = tableName + ' tr:eq(' + rowIndex + '),' + subTableName + ' tr:eq(' + rowIndex + ')';
			} else {
				if($(currRow).parents(tableName).length > 0) {
					ObjectString = tableName + ' tr:eq(' + rowIndex + '),' + subTableName + ' tr:eq(' + rowIndex + ')';
				} else {
					ObjectString = tableName + ' tr:eq(' + rowIndex + '),' + subTableName + ' tr:eq(' + rowIndex + ')';
				}
			}
			return ObjectString;
		};

	setRowBackgroundColor();

	//行悬浮显示颜色
	$('tr', tableName + ',' + subTableName).not(exceptRows).hover(function(event) {
		if(!hasShow) {
			currentColor = $(this).css('background-color');
			rowIndex = $(this).prevAll().length;

			objStr = getObjStr(rowIndex, event.target);
			$(objStr).addClass('table-row-highlight');
		}
	}, function(event) {
		if(!hasShow) {
			objStr = getObjStr(rowIndex, event.target);
			$(objStr).removeClass('table-row-highlight');

			setRowBackgroundColor();
		}
	});
	return {
		//动态设置hasShow用来判断是否启用hover交互效果
		setShow: function(isShow) {
			if(isShow != undefined) {
				hasShow = isShow;
			}
		}
	}
};

//设置隔行背景色
tableUtil.setRowBackgroundColor = function(tableName, subTableName, exceptRows, rowSplit) {
	$(tableName + ' tr:not(' + exceptRows + ')').removeAttr('style');
	$(tableName + ' tr:not(' + exceptRows + '):' + rowSplit).css({
		'background': '#EAEAEA'
	});

	if(subTableName) {
		$(subTableName + ' tr:not(' + exceptRows + ')').removeAttr('style');
		$(subTableName + ' tr:not(' + exceptRows + '):' + rowSplit).css({
			'background': '#EAEAEA'
		});
	}
}

//推拉
tableUtil.sliderBar = function(options) {
	options.mainLayer = options.mainLayer || '.slider-main-area';
	options.mainTable = options.mainTable || '.slider-main-table';
	options.mainTableTitleRow = options.mainTableTitleRow || '.table_title';

	options.subLayer = options.subLayer || '.slider-sub-area';
	options.subLayerWidth = options.subLayerWidth || 400;
	options.subTable = options.subTable || '.slider-sub-table';

	options.slider = options.slider || '.slider-btnExpand';
	options.slideSpeed = options.slideSpeed || 'normal';

	//表内多余的长度
	options.slideExtraLength = options.slideExtraLength || 60;
	//组件的高度
	options.slideExtraTop = options.slideExtraTop || 60;

	var lastTdLeft, sliderWidth, slideStartPosition, _inition;

    $(document).ready(function(){
        //DOM ready后初始化
        _inition = function() {

            //当存在推拉按钮的时候进行初始化
            if($(options.slider).length > 0) {

                //是否存在表头行
                if($(options.mainTableTitleRow).length <= 0) {
                    //若不存在则在主表的子节点内找寻含有"title"的行
                    lastTdLeft = $(options.mainTable + ' tr[class*="title"]').children(":visible").last().position().left;
                } else {
                    lastTdLeft = $(options.mainTable + ' ' + options.mainTableTitleRow).children(":visible").last().position().left;
                }


                sliderWidth = $(options.slider).width();
                slideStartPosition = lastTdLeft - sliderWidth;
                //slideStartPosition = lastTdLeft - sliderWidth/2;
                //初始化推拉按钮的样式
                $(options.slider).css({
                    'top': options.slideExtraTop + 'px',
                    'left': slideStartPosition + 'px',
                    'display': 'block',
                    'position': 'absolute'
                });

                //初始化副表层的样式
                $(options.subLayer).css({
                    'top': (options.slideExtraTop) + 'px',
                    'left': (lastTdLeft - options.subLayerWidth) + 'px',
                    'width': options.subLayerWidth,
                    'position': 'absolute',
                    'margin': 0


                });

                //初始化副表的样式
                $(options.subTable, options.subLayer).css({
                    'left': options.subLayerWidth + 'px',
                    'width': options.subLayerWidth,
                    'margin':0,
                    'top':"-2px"
                });

                var $subTableTitleRow = $(options.mainTableTitleRow, options.subLayer);
                $subTableTitleRow.css({
                    "background-position":"0 1px"
                });
            }
        };
        _inition();
    });


	//设置推拉按钮的高度
	var _setSliderHeight = function() {
			$(options.slider).css({
				'height': ($(options.mainTable).outerHeight() - options.slideExtraLength) + 'px',
				'display': 'block'
			});
		};

	//设置副层高度
	var _setSubLayerHeight = function() {
			$(options.subLayer).css({
				'height': ($(options.mainTable).outerHeight() - options.slideExtraLength) + 'px'
			});
			_setSliderHeight();
		};


	//设置各行高度
	var _setRowHeight = function() {

			//表头行是否存在
			if($(options.mainTableTitleRow).length > 0) {
				var mainTableRowCollection = $(options.mainTableTitleRow, options.mainTable).nextAll();
				var subTableRowCollection = $('tr:not(:first)', options.subTable);


				mainTableRowCollection.each(function(i) {
					subTableRowCollection.eq(i).height(mainTableRowCollection.eq(i).outerHeight());
				});
			} else {
				$('tr:not(:first)', options.mainTable).each(function(i) {
					$('tr:eq(' + i + ')', options.subTable).height($(this).outerHeight());
				});
			}
		};

	//绑定推拉按钮
	var _bindButton = function() {

			//推拉按钮
			$(options.slider).toggle(function(event) {
				$(options.subLayer).css({
					"display": "block"
				});
				//主表的z-index
				$(options.mainTable).css({
					'z-index': '0'
				});
				//副层的z-index
				$(options.subLayer).css({
					'z-index': '1'
				});

				//推拉按钮的移动
				$(options.slider).animate({
					'left': (slideStartPosition - options.subLayerWidth) + 'px'
				}, options.slideSpeed);
				//副表的移动
				$(options.subTable).animate({
					'left': '0px'
				}, options.slideSpeed, function() {
					$('i', options.slider).addClass('expand');
				});

                // 解决 事件没有冒泡到顶端的问题， 因为 toggle 函数会阻止时间默认行为 , fixed bug 6461
                $(document.body).click();
			}, function(event) {
				$(options.subLayer).css({
					"display": "block"
				});
				//推拉按钮的移动
				$(options.slider).animate({
					'left': slideStartPosition + 'px'
				}, options.slideSpeed, function() {
					//主表的z-index
					$(options.subLayer).css({
						'z-index': '0'
					});
					//副层的z-index
					$(options.mainTable).css({
						'z-index': '1'
					});
				});
				//副表的移动
				$(options.subTable).animate({
					'left': options.subLayerWidth + 'px'
				}, options.slideSpeed, function() {
					$('i', options.slider).removeClass('expand');
				});

                // 解决 事件没有冒泡到顶端的问题， 因为 toggle 函数会阻止时间默认行为 , fixed bug 6461
                $(document.body).click()
			});
		};

		//重新定位:为了避免dom ready之后动态渲染的dom元素影响定位
	var _resetPosition = function() {
			_inition();
		};

	return {
		setSubLayerHeight: _setSubLayerHeight,
		setRowHeight: _setRowHeight,
		bindButton: _bindButton,
		resetPosition: _resetPosition
	};
};

tableUtil.limitSpanWidth = function($span, _distance) {
	_distance = _distance || 0;
	if($span.length > 0) {
		$span.each(function(i) {
            var tdW = $span.eq(i).closest("td").width();
			$span
                .eq(i)
                .width(tdW - _distance);
		});
	} else {
		$span = $('.limit-span');
		$span.each(function(i) {
            var tdW = $span.eq(i).closest("td").width();
			$span
                .eq(i)
                .width(tdW - _distance);
		});
	}
};


(function($) {
	$.fn.maskLoading = function(method) {
		if(methods[method]) {
			return methods[method].apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.maskLoading');
		}
	};
	var methods = {
		show: function() {
			if($('.table-loading-container').length <= 0) {
				var _loadLayer, _maskLayer, _containerLayer, _thisHeight = $(this).outerHeight() || 100,
					_thisWidth = $(this).outerWidth() || 300;

				_loadLayer = '<div class="table-loading" style="z-index:3;position:absolute;top:50%;left:50%;margin-top:-16px;margin-left:-16px;">' + '<img src="images/loadinglit.gif" alt="加载中,请稍后..." />' + '</div>';

				_maskLayer = '<div class="table-loading-mask" style="background:#000000;opacity:0.5;position:absolute;z-index:2;width:' + _thisWidth + 'px;height:' + _thisHeight + 'px;">' + _loadLayer + '</div>';

				_containerLayer = '<div class="table-loading-container">' + _maskLayer + '</div>';

				$(this).after(_containerLayer);
			}


			//    $('#table_productNo').maskLoading('show');
		},
		hide: function() {
			$('.table-loading-container').animate({
				opacity: "0"
			}, 800, function() {
				$('.table-loading-container').hide();
			});
		}
	};
})(jQuery);

tableUtil.limitLen = function (str, limitLen) {
    if (str.length > limitLen) {
        return str.substr(0, limitLen) + "...";
    } else {
        return str;
    }
};