package alm.editor

import java.awt.{MouseInfo, Font}

import scala.swing._

import alm.compatibility.GridBagLayout
import javax.swing._
import java.awt.image.BufferedImage
import java.util
import scala.Predef._
import scala.collection.Map
import scala.swing.Font
import java.awt.font.FontRenderContext
import java.awt.geom.Rectangle2D
import java.util.ArrayList
import alm.Area
import scala.swing.event.{MouseReleased, MousePressed}
import scala.swing.SequentialContainer.Wrapper
import scala.util.control.Breaks
import scala.collection.JavaConversions._
import scala.swing.Action

/**
 * Represents the Bin Area within the Properties Window. The Bin Area displays
 * the widgets that are not currently displayed in the editor. The Bin Area
 * displays an icon representing the widget and it's corresponding area name.
 *
 * @author Irene
 *
 * @param areaPanel the AreaPanel which the Palette is added to
 */
class Palette(var areaPanel: AreaPanel) extends Panel {
 // private var areaPane: JScrollPane = null
  var ComponentInBinList:util.ArrayList[ComponentInBin] = new util.ArrayList() //the main list needed, containing all bin items and each item has instance variables containing information about itself
  val xPosition:Int = 20; //bin items appear in the Palette panel offset by this number of pixels in the x direction (so slightly to the right)
  var fontF:Font = new Font("calibri", Font.BOLD, 15) //font for the drawString method drawing strings to represent the labels
  var itemsInBin = new ArrayList[JComponent] //a list used by the LayoutSpec and ALMLayout classes TODO: needs to be removed because ComponentInBinList should be enough
  class peer extends javax.swing.JPanel()
  
  listenTo(this.mouse.clicks)
  // Operations that can be used in the editor (Actions) - adds the necessary reactions and actions. 
  val insert = new operations.Inserting(this) //inserting operation is instantiated here because the Palette will contain the components that can be inserted (therefore mouse needs to click on the Palette)
  /**
   * Add a JComponent to the InvisibleControl list (the bin list)
   *
   * @param content The JComponent to be added.
   */
  def addToBin(content: JComponent) {
    if (content != null) {
      itemsInBin.add(content)
    }

    areaPanel.propertiesPanel.repaint
  }

  /**
   * Method to paint the bin items
   *
   * @param graphics
	 * The Graphics2D object.
   */
  override def paintComponent(graphics: Graphics2D) {
    super.paintComponent(graphics)
    updateForPaint //call the method which updates the bin components so that can be appropriately drawn in the Palette
    calculateYPositionForAllItems //call the method which updates the starting y position for drawing each bin item
    var maxWidth:Int= 0 //This stores the maximum width out of all bin items, needed to display the scrollPane properly
    //The following loop goes through all the bin items and paints them
    //Use drawString for JLabel and drawImage for everything else
    ////////////////////////////////////////////////////////////////
    //Because the components added to palette are images instead of actual components, the scrollbar doesn't appear when the palette fills up
    //The solution was to change the preferred size of the Palette dynamically as stuff was drawn to it
    //If we set the width and height of preferred size to be the maximum width of all the components showing in the palette
    //And if we set the height to be the combined height of all the components showing in the palette,
    //then JScrollPane will know to show the scroll bar exactly to the end of the last component painted
      for (img :ComponentInBin <- ComponentInBinList) {
        if(img.component.isInstanceOf[JLabel]){
          graphics.setFont(fontF)
          graphics.drawString(img.name, xPosition, img.drawYPosition + img.height) //the height needs to be added to the drawYPosition because drawString starts drawing at the bottom left corner
          maxWidth= math.max(maxWidth,xPosition+img.width)
          preferredSize_= (new Dimension (maxWidth,img.drawYPosition+img.height))
        } else {
          graphics.drawImage(img.bufferedImage, xPosition, img.drawYPosition, null) //drawYPosition (representing the Y position of the end of the last bin item) can be used because drawImage starts drawing at the top left corner
          maxWidth= math.max(maxWidth,xPosition+img.width)
          preferredSize_= (new Dimension (maxWidth,img.drawYPosition+img.height))
        }
      }
    }

  /**
   * Method for updating the bin components as the appropriate images to be drawn in the Palette
   * It is different for labels compared to every other component
   * The label is simply drawn as a String (its name)
   * while every other component is drawn as a buffered image of its exact appearance
   *
   */
  private def updateForPaint = {
  if(ComponentInBinList.size>0){ //only execute if there are bin items
  for (binItem :ComponentInBin <- ComponentInBinList) { 
    if(binItem.component.isInstanceOf[JLabel]){ //if the bin item is a label, then make a rectangle representing the total area it occupies on the screen
      var text:String = binItem.name //the text is the name of the bin item e.g. label1
      var frc: FontRenderContext = new FontRenderContext(null, true, true)
      var bounds:Rectangle2D = fontF.getStringBounds(text, frc)  //using the FontRenderContext instance and the string to create a rectangle with the width and height of the text
      //While you can get the width and height directly from the component if not a label, you need to set up the width and height of the label based on the width and height of the rectangle made in the above line
      binItem.width =  bounds.getWidth.asInstanceOf[Int]
      binItem.height =  bounds.getHeight.asInstanceOf[Int] 
    } else {
      binItem.bufferedImage = new BufferedImage(binItem.width, binItem.height, BufferedImage.TYPE_INT_RGB) //make bin item into bufferedImage
      val controlImage: Graphics2D = binItem.bufferedImage.createGraphics //get the graphics 2d object
      //set up the graphics2D
      controlImage.setPaint(binItem.component.getBackground)
      controlImage.fill(binItem.component.getBounds)
      //invoke the method to print the component
      binItem.component.printAll(controlImage)
    }
    }
    //The following loop updates the position to draw each binItem 
    for (binItem :ComponentInBin <- ComponentInBinList) {
      var combinedHeightOfAllImagesBefore:Int = 0 //local variable storing the Y position representing combined height of all the images before the binItem looked at right now. This is needed so the next bin item starts drawing at this position
      //go through all the heights of the components before it
      for (j <- 0 until ComponentInBinList.indexOf(binItem)) { //go through every image before the bin item looked at in the loop right now
        combinedHeightOfAllImagesBefore = combinedHeightOfAllImagesBefore+ ComponentInBinList.get(j).height //add all the heights together
        //set the top left corner of the image to be drawn
      }
      binItem.drawYPosition = combinedHeightOfAllImagesBefore //update the starting Y point for drawing for the next bin item.. update the instance variable
  }
  }
 }

  /**
   * Method for updating the drawYPosition variable for each bin item
   */
  private def calculateYPositionForAllItems = {
    if(ComponentInBinList.size>0){ //only execute if there are bin items
      //The following loop updates the position to draw each binItem
      for (binItem :ComponentInBin <- ComponentInBinList) {
        var combinedHeightOfAllImagesBefore:Int = 0 //local variable storing the Y position representing combined height of all the images before the binItem looked at right now. This is needed so the next bin item starts drawing at this position
        //go through all the heights of the components before it
        for (j <- 0 until ComponentInBinList.indexOf(binItem)) { //go through every image before the bin item looked at in the loop right now
          combinedHeightOfAllImagesBefore = combinedHeightOfAllImagesBefore+ ComponentInBinList.get(j).height //add all the heights together
          //set the top left corner of the image to be drawn
        }
        binItem.drawYPosition = combinedHeightOfAllImagesBefore //update the starting Y point for drawing for the next bin item.. update the instance variable
      }
    }
  }

}

