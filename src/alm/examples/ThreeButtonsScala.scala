package alm.examples

import javax.swing.{JButton, JFrame}

import alm.{ALMLayout, LayoutSpec}

import scala.swing._
import scala.swing.MainFrame

// TODO finish this...
object ThreeButtonsScala extends SimpleSwingApplication {
  def top = new MainFrame {
    val le = new ALMLayout
    val ls = new LayoutSpec
    val button1 = new JButton("button1")
    val button2 = new JButton("button2")
    val button3 = new JButton("button3")


    val x1 = ls.addXTab("x1")
    val y1 = ls.addYTab("y1")
    ls.addArea(ls.getLeft, ls.getTop, x1, ls.getBottom, button1)
    ls.addArea(x1, ls.getTop, ls.getRight, ls.getBottom, button2)
    ls.addArea(ls.getLeft, y1, ls.getRight, ls.getBottom, button3)
    le.setLayoutSpec(ls)

    //  def actionPerformed(arg0: ActionEvent) {
    //      le.edit(this)
  }
}