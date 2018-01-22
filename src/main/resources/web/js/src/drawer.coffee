###
@author Lhfcws
Drawer wrapper of ECharts

use CoffeeScript to implements OOP as ES6 is not supported by most of the browsers in this time.
###

###
  画图入口类，draw为静态调用方法，调用姿势 Drawer.draw(htmlId, option, title)
###
class Drawer
  @draw: (htmlId, option, title) ->
    console.log(option)
    app = echarts.init(document.getElementById(htmlId));
    app.title = title
    app.setOption(option)

  @drawTable: (htmlId, tableHtml) ->
    $("#" + htmlId).append(tableHtml)


class Option
  constructor: (@name, @type, @xdata, @ydata) ->
    this.otherSettings = {}

  setOtherSettings: (o) ->
    this.otherSettings = o
    return this

  _init: ->

  formatData: ->
    this.ydata

  template: ->
    {}

class TableOption extends Option
  constructor: (@title, @hasHeader, @data) ->
    super this.title, "table", [], []

  template: ->
    html = []
    start = 0
    if this.hasHeader
      start = 1
      d = this.data[0]
      l = []
      l.push("<th>#{item}</th>") for item in d
      html.push("<tr>#{l.join(" ")}</tr>")

    for i in [start ... this.data.length]
      d = this.data[i]
      l = []
      l.push("<td>#{item}</td>") for item in d
      html.push("<tr>#{l.join(" ")}</tr>")

    return "<h5>#{this.title}</h5><table class='table'>#{html.join(" ")}</table>"

###
  折线图/散点图配置
###
class LineOption extends Option
  _init: ->
    this.color = "blue"
    this.fillArea = false

  formatData: ->
    ret = []
    ret.push([this.xdata[i], this.ydata[i]]) for i in [0 ... this.xdata.length]
    ret

  template: ->
    areaIfConfigured = ""
    if this.fillArea
      areaIfConfigured = {
        areaStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0,
              color: 'rgb(255, 255, 255)'
            }, {
              offset: 1,
              color: '#{this.color}'
            }])
          }
        }
      }

    if this.type == "line"
      return Util.mergeObject(
        {
          name: this.name,
          type: this.type,
          smooth: true,
          itemStyle: {
            normal: {
              color: this.color
            }
          },
          data: this.formatData()
        }, areaIfConfigured, this.otherSettings
      )
    else if this.type == "scatter"
      return Util.mergeObject(
        {
          name: this.name,
          type: this.type,
          itemStyle: {
            normal: {
              color: this.color
            }
          },
          data: this.formatData()
        }, areaIfConfigured, this.otherSettings
      )

class LineOptions
  constructor: (@isTimeXAxis) ->
    this.options = []

  addOption: (option) ->
    this.options.push(option)

  template: ->
    names = []
    names.push(option.name) for option in this.options

    oh = []
    oh.push(option.template()) for option in this.options
    if oh.length == 0
      return {}

    xAxis = {
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: this.options[0].xdata
      }
    }

    if this.isTimeXAxis
      xAxis = {xAxis: {type: 'time', splitLine: {show: false}}}
    return Util.mergeObject(xAxis, {
      yAxis: {
        type: 'value',
        splitLine: {
          show: false
        }
      },
      legend: {
        data: names
      },
      series: oh
    })


###
  StackBar
###
class StackBarOption extends Option
  constructor: (@name, @xdata, @ydata, @yAxisLabels) ->
    super this.name, "bar", this.xdata, this.ydata

  @extractFromObject: (obj) ->
    ya = []
    o = {}
    for key, obj0 of obj
      ya.push(key)
      for k, v of obj0
        if o[k] == undefined
          o[k] = []
        o[k].push(v)

    [xd, yd] = Util.mapToXYData(o)
    return [xd, yd, ya]

  setYAxisLabels: (yLabels) ->
    this.yAxisLabels = yLabels

  template: ->
    ret = Util.mergeObject({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
# 坐标轴指示器，坐标轴触发有效
          type: 'shadow'        # 默认为直线，可选为：'line' | 'shadow'
        }
      },
      legend: {
        data: this.xdata
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: this.yAxisLabels
      },
      series: this.formatData()
    }, this.otherSettings)
    return ret

  formatData: ->
    ret = []
    ret.push({
      name: this.xdata[i],
      type: 'bar',
      stack: '总量',
      label: {
        normal: {
          show: true,
          position: 'insideRight'
        }
      },
      data: this.ydata[i]
    }) for i in [0 ... this.xdata.length]
    return ret

###
  Simple Bar
###
class BarOption extends Option
  template: ->
    return Util.mergeObject({
      grid: {
        left: '20%',
      },
      yAxis: [
        {
          type: 'category',
          data: this.xdata,
          axisTick: {
            alignWithLabel: true
          }
        }
      ],
      xAxis: [
        {
          type: 'value'
        }
      ],
      series: [{
        name: this.name,
        type: 'bar',
        label: {
          normal: {
            show: true,
            position: 'right'
          }
        },
        itemStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1,
              [
                {offset: 0, color: '#83bff6'},
                {offset: 0.5, color: '#188df0'},
                {offset: 1, color: '#188df0'}
              ]
            )
          },
          emphasis: {
            color: new echarts.graphic.LinearGradient(
              0, 0, 0, 1,
              [
                {offset: 0, color: '#2378f7'},
                {offset: 0.7, color: '#2378f7'},
                {offset: 1, color: '#83bff6'}
              ]
            )
          }
        },
        barWidth: '60%',
        data: this.ydata
      }]
    }, this.otherSettings)

###
  饼图配置
###
class PieOption extends Option
  formatData: ->
    ret = []
    ret.push({name: this.xdata[i], value: this.ydata[i]}) for i in [0 ... this.xdata.length]
    ret = ret.sort((o1, o2) ->
      return o1.value < o2.value
    )
    return ret

  template: ->
    ret = Util.mergeObject({
      tooltip: {
        trigger: 'item',
        formatter: "{b}: {c} ({d}%)"
      },
      series: [
        {
          name: this.name,
          type: this.type,
          label: {
            normal: {
              formatter: '{b}\n{d}%'
            }
          },
          markLine: {
            label: {
              normal: {
                position: "start"
              }
            }
          }
          radius: ['50%', '80%'],
          data: this.formatData()
        }
      ]
    }, this.otherSettings)
    console.log(ret)
    return ret