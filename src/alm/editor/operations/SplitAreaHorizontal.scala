package alm.editor.operations

import alm.editor.ALMEditorCanvas
import scala.swing.{Action, MenuItem}
import alm.{Area, YTab}
import java.awt.Dimension
/**
 * This class defines the split area horizontally editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * The class extends the Action class and implements the apply method
 * It then instantiates a new MenuItem passing itself as the action in the parameter
 * Thus this MenuItem, when added to the areaPopupMenu and constraintsPopupMenu from ALMEditorCanvas, will perform the action defined in the apply method
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class SplitAreaHorizontal (canvas: ALMEditorCanvas) extends Action("Split Area Horizontally") {
  canvas.areasPopupMenu.contents += new MenuItem(this)
  canvas.constraintsPopupMenu.contents += new MenuItem(this)
  /**
   * Method to split an area horizontally, i.e. create a new YTab. Creates two
   * new areas from the old one
   *
   * @throws LpSolveException
   * @throws Exception
   */
  def apply {
    //Create new YTab (horizontal line)
    val newTab: YTab = new YTab(canvas.lMLayout.getLayoutSpec, false)
    //Setting the value of the variable to some double number halfway between the bottom and top borders of the selected area
    newTab.setValue(canvas.selectedArea.top.getValue + (canvas.selectedArea.bottom.getValue - canvas.selectedArea.top.getValue) / 2)
    //Add a new area to layout spec that has the left, top, right borders of the old area and the newTab as the bottom border. The content is null
    val newArea: Area = canvas.lMLayout.getLayoutSpec.addArea(canvas.selectedArea.left, canvas.selectedArea.top, canvas.selectedArea.right, newTab, null)
    //set the preferredContentSize of the new area to be half the height of the old area but the same width
    newArea.preferredContentSize = new Dimension(canvas.selectedArea.preferredContentSize.getWidth.asInstanceOf[Int] , canvas.selectedArea.preferredContentSize.getHeight.asInstanceOf[Int] / 2)
    newArea.setAutoPreferredContentSize(false)
    //then set the top border of the selected area to be the new tab
    canvas.selectedArea.top = newTab
    //update the properties window so the new tab is added to the comboBox selection
    canvas.lMLayout.propertiesWindow.propertiesPanel.update
    canvas.repaint
    canvas.revalidate()
  }
}
