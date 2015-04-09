package alm.editor.operations

import alm.editor.ALMEditorCanvas
import scala.swing.{MenuItem, Action}
import alm.{Area, XTab}
import java.awt.Dimension
/**
 * This class defines the split area vertically editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * The class extends the Action class and implements the apply method
 * It then instantiates a new MenuItem passing itself as the action in the parameter
 * Thus this MenuItem, when added to the areaPopupMenu and constraintsPopupMenu from ALMEditorCanvas, will perform the action defined in the apply method
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class SplitAreaVertical (canvas: ALMEditorCanvas) extends Action("Split Area Vertically") {
  canvas.areasPopupMenu.contents += new MenuItem(this)
  canvas.constraintsPopupMenu.contents += new MenuItem(this)
  /**
   * Method to split an area vertically, i.e. create a new XTab. Creates two
   * new areas from the old one
   */
  def apply {
    //Create new XTab (vertical line)
    val newTab: XTab = new XTab(canvas.lMLayout.getLayoutSpec, false)
    //Setting the value of the variable to some double number halfway between the left and right borders of the selected area
    newTab.setValue(canvas.selectedArea.left.getValue + (canvas.selectedArea.right.getValue - canvas.selectedArea.left.getValue) / 2)
    //Add a new area to layout spec that has the top, right, bottom borders of the old area and the newTab as the left border. The content is null
    val newArea: Area = canvas.lMLayout.getLayoutSpec.addArea(newTab, canvas.selectedArea.top, canvas.selectedArea.right, canvas.selectedArea.bottom, null)
    //set the preferredContentSize of the new area to be half the width of the old area but the same height
    newArea.preferredContentSize = new Dimension(canvas.selectedArea.preferredContentSize.getWidth.asInstanceOf[Int] / 2, canvas.selectedArea.preferredContentSize.getHeight.asInstanceOf[Int])
    newArea.setAutoPreferredContentSize(false)
    //then set the right border of the selected area to be the new tab
    canvas.selectedArea.right = newTab
    canvas.lMLayout.propertiesWindow.propertiesPanel.update
    canvas.repaint
    canvas.revalidate()
  }
}
