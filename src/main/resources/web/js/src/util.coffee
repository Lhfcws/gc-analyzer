class Util
  @getColor: (h) ->
    colors = ['#c23531','#2f4554', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3']
    i = h % colors.length
    return colors[i]

  @ajaxPost: (url, formData, renderer) ->
    jQuery.ajax({
      url: url,
      type: 'POST',
      cache: false,
      data: formData,
      success: (data) ->
        renderer(data)
      ,
      error: (e) ->
        alert('ERROR POST : ' + e)
    })

  @ajaxGet: (url, renderer) ->
    jQuery.ajax({
      url: url,
      type: 'GET',
      cache: false,
      success: (data) ->
        renderer(data)
      ,
      error: (e) ->
        alert('ERROR GET : ' + e)
    })

  @formatDuration: (ts) ->
    [day, hour, minute] = [0, 0, 0]

    ms = ts % 1000
    ts = Math.floor(ts / 1000)

    if ts >= 86400
      day = Math.floor(ts / 86400)
    ts %= 86400

    if ts >= 3600
      hour = Math.floor(ts / 3600)
    ts %= 3600

    if ts >= 60
      minute = Math.floor(ts / 60)
    ts %= 60

    second = ts

    ret = []
    if day > 0
      ret.push("#{day} Day")
    if hour > 0
      ret.push("#{hour} Hour")
    if minute > 0
      ret.push("#{minute} Min")
    if second > 0
      ret.push("#{second} Sec")
    if ms > 0
      ret.push("#{ms} MilliSec")

    return ret.join(", ")

  @mapToXYData: (obj) ->
    xData = []
    yData = []
    for key, value of obj
      xData.push(key)
      yData.push(value)
    return [xData, yData]

  @mapToTable: (obj) ->
    results = []
    results.push([key, value]) for key, value of obj
    return results

  @mergeObject: (obj1, obj2) ->
    obj = {}
    for attrname, v of obj1
      obj[attrname] = v
    for attrname, v of obj2
      obj[attrname] = v
    return obj

  @mergeObject: (obj1, obj2, obj3) ->
    obj = {}
    for attrname, v of obj1
      obj[attrname] = v
    for attrname, v of obj2
      obj[attrname] = v
    for attrname, v of obj3
      obj[attrname] = v
    return obj
