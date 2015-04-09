package alm.editor

import scala.swing._
import alm._

/**
 * This class defines the properties window that appears when in edit mode.
 * It sets up the menu bar with the file menu needed in the Frame with the menu items load, save and exit
 * Also has the restart method to refresh the editing panel and set up the propertiesWindow after it has been shut once.
 * Its main function is to instantiate the PropertiesPanel {@link alm.editor.PropertiesPanel} (which is added to itself).
 *
 * @author Brent Whiteley, Irene
 *
 * @param parentContainer The container being edited.
 * @param layout The ALMLayout defining the container's layout.
 * @throws alm.ALMException
 */

class PropertiesWindow(val parentContainer: java.awt.Container, val layout: ALMLayout) extends Frame {
  //instantiate the propertiesPanel and add to itself
  var propertiesPanel: PropertiesPanel = new PropertiesPanel(parentContainer, layout)
  contents = propertiesPanel
  //set the title of the Frame to be "Properties", its size
  title = "Properties"
  size = new Dimension(800, 400)
  //the next line makes sure the window pops up on the screen directly next to the editing canvas
  location = new Point(parentContainer.getLocation.getX.asInstanceOf[Int] + parentContainer.getWidth, parentContainer.getLocation.getY.asInstanceOf[Int])
  //set up the menu bar with "File" menu with menu items
  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new scala.swing.MenuItem(Action("Load") {
        propertiesPanel.load
      })
      contents += new scala.swing.MenuItem(Action("Save") {
        propertiesPanel.save
      })
      contents += new scala.swing.MenuItem(Action("Exit") {
        exit
      })
    }
  }

  /**
   * Refresh the property window after exited
   */
  def restartWindow {
    parentContainer.removeAll
    propertiesPanel.aLMEditorCanvas.setMode(EditorMode.AreaEdit)
    propertiesPanel.aLMEditorCanvas.peer.setSize(parentContainer.getSize)
    //hAlignBox.addItem(Array[AnyRef](HorizontalAlignment.LEFT, HorizontalAlignment.RIGHT, HorizontalAlignment.CENTER, HorizontalAlignment.FILL, HorizontalAlignment.NONE))
    //vAlignBox.addItem(Array[AnyRef](VerticalAlignment.TOP, VerticalAlignment.BOTTOM, VerticalAlignment.CENTER, VerticalAlignment.FILL, VerticalAlignment.NONE))
    parentContainer.add(propertiesPanel.aLMEditorCanvas.peer)
  }

  /**
   * Gets the areaPanel instance.
   *
   * @return The areaPanel object.
   */
  def getAreaPanel: AreaPanel = {
    return propertiesPanel.areaPanel
  }

  /**
   * Getter for layout object
   *
   */
  def getLayout: ALMLayout = {
    return layout
  }

  /**
   * Gets the ALMEditor instance.
   *
   * @return The ALMEditor object.
   */
  def getALMEditor: ALMEditorCanvas = {
    return propertiesPanel.aLMEditorCanvas
  }

  /**
   * Closes the properties window, exits edit mode and returns the GUI to normal operation.
   */
  def exit {
    peer.setVisible(false)
    this.layout.quitEdit(this.parentContainer)
    this.parentContainer.repaint()
  }

  /**
   * Overrides the method in Scala.swing.window which defines behaviour when window is closed
   *
   */
  override def closeOperation() {
    this.exit
    super.closeOperation()
  }

}