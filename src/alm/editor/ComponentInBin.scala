package alm.editor

import javax.swing.JComponent
import java.awt.image.BufferedImage
import java.awt.{Point, Rectangle}
import alm.{VerticalAlignment, HorizontalAlignment, Area}
import java.awt.Insets

/**
 * Class for storing information about each item in the bin
 */
class ComponentInBin (c: JComponent, palette:Palette) {
  //Component that used to occupy an area, corresponds to area.content
  var component:JComponent = c
  var width:Int= c.getWidth //width of the component
  var height:Int = c.getHeight //height of the component
  var name: String = c.getName //name of the component e.g. button1
  //Each component in bin is drawn as a bufferedImage. This stores the Y position in which the bufferedImage needs to be drawn in the palette
  var drawYPosition:Int = 10
  //It is the bufferedImage representing the component.. it looks exactly like the component before being added to the bin
  var bufferedImage:BufferedImage =null
  //The area (in the layoutSpec) that the component was the content of before being added to the bin
  var area:Area = null
  //The inset information of the component before being added to the bin
  var insets:Insets = null
  //var childArea: Area = null //TODO: This information needs to be added to distinguish between userVariables and insetVariables
  //The horizontal/verticalAlignment information of the component before being added to the bin
  var horizontalAlignment:HorizontalAlignment = null
  var verticalAlignment: VerticalAlignment = null
  /** Method for checking whether a particular point is contained within the rectangle representing the component*/
  def CheckForClick (point:Point):Boolean = {
    //make a Rectangle object based on the position, width and height of the component so that the rectangle exactly overlaps the component
    var rectangle:Rectangle = new Rectangle(palette.xPosition,drawYPosition, width,height)
    //then check if a mouse click point is contained within that rectangle, in other words if the component is being selected
    rectangle.contains(point)
  }
  /** Method for setting the area instance variable */
  def setArea(areaPassed:Area) = {
    area= areaPassed
  }
  /** Method for setting the insets instance variable */
  def setInsets (i :Insets) = {
    println("Within ComponentInBin and setting inset "+ i)
  insets = i
  }
  /** Method for setting the horizontalAlignment instance variable */
  def setHorizontalAlignment (h :HorizontalAlignment) = {
    println("Within ComponentInBin and setting horizontal alignment "+ h)
    horizontalAlignment = h
  }
  /** Method for setting the verticalAlignment instance variable */
  def setVerticalAlignment (v :VerticalAlignment) = {
    println("Within ComponentInBin and setting vertical alignment "+ v)
    verticalAlignment = v
  }
  /** Method for setting the childArea instance variable */
//  def setChildArea(child:Area) = {
//    println("Within ComponentInBin and setting child area "+ child)
//    childArea= child
//  }
}

