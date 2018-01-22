class Main
  @cms_layout: (ct, ajaxData) ->
    if (ajaxData["_meta"]["gctype"] != "CMS")
      console.log("Not a CMS GC analysis report")
      return ct

    rows = []

    # Summary Row
    key = "Summary"
    if ajaxData[key]
      ajaxData[key]["duration"] = Util.formatDuration(ajaxData[key]["duration"])
      rowSummary = new Row(key, "fa fa-navicon", ajaxData[key]["_comments"])
      data = Util.mapToTable(ajaxData[key])
      tbl = new TableOption("", false, data)
      rowSummary.append(new Col(key, 12, tbl.template()))
      rows.push(rowSummary)

    # Heap
    key = "JVM Heap"
    if ajaxData[key]
      rowHeap = new Row(key, "fa fa-industry", ajaxData[key]["_comments"])

      name = "summary"
      [xd, yd, ya] = StackBarOption.extractFromObject(ajaxData[key][name])
      rowHeap.append(new Col(name, 12, new StackBarOption(name, xd, yd, ya).template()))

      makeHeapLineOptions = (name) ->
        if ajaxData[key][name]
          lo = new LineOptions(true)
          [xd, yd] = Util.mapToXYData(ajaxData[key][name])
          lo.addOption(new LineOption("allocated", "line", ajaxData[key][name]["xdata"], ajaxData[key][name]["allocated"]))
          lo.addOption(new LineOption("Before GC", "line", ajaxData[key][name]["xdata"], ajaxData[key][name]["Before GC"]))
          lo.addOption(new LineOption("After GC", "line", ajaxData[key][name]["xdata"], ajaxData[key][name]["After GC"]))
          rowHeap.append(new Col(name, 12, lo.template()))

      makeHeapLineOptions("YoungGen")
      makeHeapLineOptions("OldGen")
      makeHeapLineOptions("Heap")

      rows.push(rowHeap)

    # GC Stats
    key = "GC Stats"
    if ajaxData[key]
      rowGC = new Row(key, "fa fa-eraser", ajaxData[key]["_comments"])

      lo = new LineOptions(true)
      gcDuration = ajaxData[key]["GC Duration"]
      name = "YoungGen"
      lo.addOption(new LineOption(name, "line", gcDuration[name]["xdata"], gcDuration[name]["mb"]))
      name = "OldGen"
      lo.addOption(new LineOption(name, "line", gcDuration[name]["xdata"], gcDuration[name]["mb"]))
      rowGC.append(new Col("GC Duration", 12, lo.template()))

      for name in ["Total GC Stats", "Minor GC Stats", "Full GC Stats", "Paused GC Stats"]
        data = Util.mapToTable(ajaxData[key][name])
        rowGC.append(new Col(name, 3, new TableOption("", false, data).template()))

      rows.push(rowGC)

    # CMS Stats
    key = "CMS Stats"
    if ajaxData[key]
      rowCMS = new Row(key, "fa fa-paint-brush", ajaxData[key]["_comments"])

      o = {}
      for k, v of ajaxData[key]
        if k != "_comments"
          o[k] = Util.mapToXYData(v)

      name = "Total Time"
      if o[name]
        rowCMS.append(new Col(name, 5, new PieOption(name, "pie", o[name][0], o[name][1]).setOtherSettings({ grid: {left: '30%'}}).template()))
      name = "Avg Time"
      if o[name]
        rowCMS.append(new Col(name, 7, new BarOption(name, "bar", o[name][0], o[name][1]).setOtherSettings({ grid: {left: '30%'}}).template()))
      name = "CMS Total Pause Time"
      if o[name]
        rowCMS.append(new Col(name, 5, new PieOption(name, "pie", o[name][0], o[name][1]).template()))
      name = "CMS Avg Pause Time"
      if o[name]
        rowCMS.append(new Col(name, 7, new BarOption(name, "bar", o[name][0], o[name][1]).template()))

      rows.push(rowCMS)

    Main.appendToContainer(ct, rows)
    Main.renderAll(ct, rows)

  @appendToContainer: (container, rows) ->
    for row in rows
      rowHtml = row.toHtml()
      container.append(rowHtml)
    return container

  @renderAll: (container, rows) ->
    for row in rows
      row.render()
