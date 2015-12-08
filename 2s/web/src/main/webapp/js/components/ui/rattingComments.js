/**
 * 此组件是用于
 * @author zhen.pan
 */
(function () {
    App.namespace("Module.Data.rattingComments");

    App.Module.Data.rattingTipContents = {
        qualityTip: {
            contents: [
                {
                    scope: [0, 2],
                    htmlText: "<span style=\"color:red;\">1分 很不满意</span><br>产品质量差的很离谱，无法正常使用，非常不满意"
                },
                {
                    scope: [3, 4],
                    htmlText: "<span style=\"color:red;\">2分 不满意</span><br> 部分产品有破损，质量差不满意"
                },
                {
                    scope: [5, 6],
                    htmlText: "<span style=\"color:red;\">3分 一般</span><br> 产品质量马马虎虎，没有供应商说的那么好"
                },
                {
                    scope: [7, 8],
                    htmlText: "<span style=\"color:red;\">4分 满意</span><br> 产品质量还不错，跟供应商说的差不多，挺满意的"
                },
                {
                    scope: [9, 10],
                    htmlText: "<span style=\"color:red;\">5分 非常满意</span><br> 产品质量非常好，非常好用，很满意"
                }
            ]
        },
        performanceTip: {
            contents: [
                {
                    scope: [0, 2],
                    htmlText: "<span style=\"color:red;\">1分 很不满意</span><br>产品质量差价格贵，性价比很低，非常不满意"
                },
                {
                    scope: [3, 4],
                    htmlText: "<span style=\"color:red;\">2分 不满意</span><br>产品质量不是很好，价格稍贵，感觉不值不满意"
                },
                {
                    scope: [5, 6],
                    htmlText: "<span style=\"color:red;\">3分 一般</span><br> 产品质量一般，价格还行，整体马马虎虎"
                },
                {
                    scope: [7, 8],
                    htmlText: "<span style=\"color:red;\">4分 满意</span><br> 产品质量不错，价格公道，性价比较高，挺满意的"
                },
                {
                    scope: [9, 10],
                    htmlText: "<span style=\"color:red;\">5分 非常满意</span><br> 产品质量非常好，价格便宜，性价比很高，非常满意"
                }
            ]
        },

        speedTip: {
            contents: [
                {
                    scope: [0, 2],
                    htmlText: "<span style=\"color:red;\">1分 很不满意</span><br>再三提醒下，供应商才发货，耽误我的时间，包装也很马虎"
                },
                {
                    scope: [3, 4],
                    htmlText: "<span style=\"color:red;\">2分 不满意</span><br> 供应商发货有点慢，催了好几次终于发货了"
                },
                {
                    scope: [5, 6],
                    htmlText: "<span style=\"color:red;\">3分 一般</span><br> 供应商发货速度一般，提醒后才发货的"
                },
                {
                    scope: [7, 8],
                    htmlText: "<span style=\"color:red;\">4分 满意</span><br> 供应商发货挺及时的，货品很快就到了"
                },
                {
                    scope: [9, 10],
                    htmlText: "<span style=\"color:red;\">5分 非常满意</span><br> 供应商发货非常快，包装也很好严实仔细"
                }
            ]
        },
        attitudeTip: {
            contents: [
                {
                    scope: [0, 2],
                    htmlText: "<span style=\"color:red;\">1分 很不满意</span><br>供应商态度很差，骂人简直不把客户当回事"
                },
                {
                    scope: [3, 4],
                    htmlText: "<span style=\"color:red;\">2分 不满意</span><br> 供应商有点不耐烦，承诺的服务也不能兑现"
                },
                {
                    scope: [5, 6],
                    htmlText: "<span style=\"color:red;\">3分 一般</span><br> 供应商态度一般，沟通不算很顺畅"
                },
                {
                    scope: [7, 8],
                    htmlText: "<span style=\"color:red;\">4分 满意</span><br> 供应商态度挺好的，沟通顺畅，总体感觉不错"
                },
                {
                    scope: [9, 10],
                    htmlText: "<span style=\"color:red;\">5分 非常满意</span><br>供应商服务太好了，非常周到，超出了我的期望"
                }
            ]
        }

    }
})();
