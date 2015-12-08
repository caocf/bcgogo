/**
 * Created by IntelliJ IDEA.
 * Description: 该文件依赖于
 *              js/extension/jquery/jquery-1.4.2.min.js,
 *              js/extension/jquery/plugin/Highcharts-2.3.2/js/highcharts.js
 *              js/extension/jquery/plugin/Highcharts-2.3.2/js/modules/exporting.js
 */

function pieChart(options) {
  //图表基本配置
  var chartBasicConfig = {
    container: options.chartBasicConfig.container || 'container',
    titleText: options.chartBasicConfig.titleText || "",
    subtitleText: options.chartBasicConfig.subtitleText || "This is a subtitle",
    percentageDecimals: options.chartBasicConfig.percentageDecimals || 1,
    data: options.chartBasicConfig.data || '[]'
  };

  //图表样式配置
  var chartStyleConfig = {
    colors: options.chartStyleConfig.colors || ['#4572A7', '#AA4643', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92', '#3BB2FD', '#FD3BFB'],

    plotBackgroundColor: options.chartStyleConfig.plotBackgroundColor || null,
    plotBorderWidth: options.chartStyleConfig.plotBorderWidth || null,
    plotShadow: options.chartStyleConfig.plotShadow || false,
    isGradient: options.chartStyleConfig.isGradient || false,
    enableDataLabels: options.chartStyleConfig.enableDataLabels || false,
    dataLabelsColor: options.chartStyleConfig.dataLabelsColor || '#000000',
    dataLabelsConnectorColor: options.chartStyleConfig.dataLabelsConnectorColor || '#FF0000',
    allowPointSelect: options.chartStyleConfig.allowPointSelect || true,
    chartWidth: options.chartStyleConfig.chartWidth || 800,
    chartHeight: options.chartStyleConfig.chartHeight || 800,

    //pie:饼图选项
    pieSize: options.chartStyleConfig.pieSize || 300,
    pieBorderWidth: options.chartStyleConfig.pieBorderWidth || 1,

    //legend:列表
    itemMargin: options.chartStyleConfig.itemMargin || {
      top: 5,
      bottom: 5
    },
    legendPosition: options.chartStyleConfig.legendPosition || {
      x: 0,
      y: 100
    },
    legendLayout: options.chartStyleConfig.legendLayout || '',
    legendWidth: options.chartStyleConfig.legendWidth || 200,
    legendItemWidth: options.chartStyleConfig.legendItemWidth || 200,

    //tooltips:提示
    tooltipHTML: options.chartStyleConfig.tooltipHTML || '<b>{point.name}</b><br /><b>{series.name}</b>: {point.percentage}%',
    tooltipStyle: options.chartStyleConfig.tooltipStyle || {
      color: '#333333',
      fontSize: '12px',
      padding: '5px',
      whiteSpace: 'nowrap'
    }
  };

  //图表设置
  var seriseConfig = {
    name: options.seriseConfig.name || '所占比例',
    type: 'pie'
  };

  //只有一条数据时不显示边框
  if(chartBasicConfig.data && chartBasicConfig.data.length == 1) {
    chartStyleConfig.pieBorderWidth = 0;
  }

  if(chartStyleConfig.isGradient) {
    //设置渐变颜色
    Highcharts.getOptions().colors = $.map(Highcharts.getOptions().colors, function(color) {
      return {
        radialGradient: {
          cx: 0.5,
          cy: 0.3,
          r: 0.7
        },
        stops: [
          [0, color],
          [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] //颜色加深
          ]
      };
    });
  }

  //构建图表
  var buildChart;
  buildChart = new Highcharts.Chart({
    chart: {
      renderTo: chartBasicConfig.container,
      plotBackgroundColor: chartStyleConfig.plotBackgroundColor,
      plotBorderWidth: chartStyleConfig.plotBorderWidth,
      plotShadow: chartStyleConfig.plotShadow,
      width: chartStyleConfig.chartWidth,
      height: chartStyleConfig.chartHeight
    },
    colors: chartStyleConfig.colors,
    title: {
      text: chartBasicConfig.titleText
    },
    subtitle: {
      text: chartBasicConfig.subtitleText,
      align: 'left',
      verticalAlign: 'bottom',
      x: 0,
      y: 100
    },
    tooltip: {
      //pointFormat: '{series.name}: <b>{point.percentage}%</b>',
      useHTML: true,
      percentageDecimals: chartBasicConfig.percentageDecimals,
      formatter: function() {
        var message = chartStyleConfig.tooltipHTML;
        message = message.replace('{point.name}', this.point.name);
        message = message.replace('{series.name}', this.series.name);
        message = message.replace('{point.percentage}', Highcharts.numberFormat(this.point.percentage, chartBasicConfig.percentageDecimals));
        return message;
      },
      style: chartStyleConfig.tooltipStyle
    },
    plotOptions: {
      pie: {
        allowPointSelect: chartStyleConfig.allowPointSelect,
        cursor: 'pointer',
        dataLabels: {
          enabled: chartStyleConfig.enableDataLabels,
          color: chartStyleConfig.dataLabelsColor,
          connectorColor: chartStyleConfig.dataLabelsConnectorColor,
          formatter: function() {
            return '<b>' + this.point.name + '</b>: ' + this.percentage + ' %';
          }
        },
        showInLegend: true,
        size: chartStyleConfig.pieSize,
        borderWidth: chartStyleConfig.pieBorderWidth
      }
    },
    legend: {
      layout: 'vertical',
      align: 'right',
      verticalAlign: 'top',
      width: chartStyleConfig.legendWidth,
      x: chartStyleConfig.legendPosition.x,
      y: chartStyleConfig.legendPosition.y,
      itemWidth: chartStyleConfig.legendItemWidth,
      itemMarginTop: chartStyleConfig.itemMargin.top,
      itemMarginBottom: chartStyleConfig.itemMargin.bottom,
    },
    navigation: {
      buttonOptions: {
        enabled: false
      }
    },
    series: [{
      type: seriseConfig.type,
      name: seriseConfig.name,
      data: chartBasicConfig.data
    }]
  });

  return {
    setSize:function(_width,_height){
      buildChart.setSize(_width,_height, true);
    }
  }
}