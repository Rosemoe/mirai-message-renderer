package io.github.rosemoe.msgRenderer.render

import java.awt.Color
import java.awt.Graphics2D

class MsgBalloonRenderNode(x:Int,y:Int,val width:Int,val height:Int, val arcSize:Int, val ballonColor: Color) : ElementRenderNode(x,y) {

    override fun renderInto(g: Graphics2D) {
        g.color = ballonColor
        g.fillRoundRect(x, y, width, height, arcSize, arcSize)
    }

}