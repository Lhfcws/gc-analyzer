class Col
  ###
    size between 1 .. 12
  ###
  constructor: (@title, @size, @content) ->
    this.id = md5(this.title)

  isTableCol: ->
    return typeof this.content != "object"

  getMinHeight: ->
    if this.isTableCol()
      return "50px"
    else
      return "300px"

  render: ->
    if not this.isTableCol()
      Drawer.draw(this.id, this.content, this.title)
    else
      Drawer.drawTable(this.id, this.content)


class Row
  constructor: (@title, @iconClass, @comments) ->
    this.cols = []

  append: (col) ->
    this.cols.push(col)
    return this

  toHtml: ->
    commentsHtml = ""
    if this.comments != undefined
      commentsHtml = "<div class='col-md-12'><blockquote class='blockquote'><p>#{this.comments}</p></blockquote></div>"

    rowHtml = ""
    for col in this.cols
      rowHtml = rowHtml + "<div class='col-md-#{col.size}'><h4>- #{col.title}</h4><div id='#{col.id}' style='min-height: #{col.getMinHeight()}'></div></div>"
    return """
          <div class="row">
            <div><h2><i class="#{this.iconClass}" aria-hidden="true" style="color:#696969;"></i> #{this.title} </h2></div>
            #{commentsHtml}
            #{rowHtml}
          </div>
          <div class='row'><hr/></div>
          """

  render: ->
    for col in this.cols
      col.render()

