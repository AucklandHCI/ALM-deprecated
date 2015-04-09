package alm.editor.operations

import alm.editor.{PropertiesPanel, EditorMode, ALMEditorCanvas}
import scala.swing.Action
import linsolve.Summand
import scala.swing.event.MousePressed
import scala.swing.MenuItem
/**
 * This class defines the remove selected constraint tab editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * The class extends the Action class and implements the apply method
 * It then instantiates a new MenuItem passing itself as the action in the parameter
 * Thus this MenuItem, when added to the constraintsPopupMenu from ALMEditorCanvas, will perform the action defined in the apply method
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class RemoveSelectedConstraintTab (canvas: ALMEditorCanvas) extends Action("Remove Tab from Constraint") {
  var removeConstraintTabMenuItem = new MenuItem(this)
  //already have listenTo(this.mouse.clicks) in canvas
  canvas.reactions+={
    /** Adds more responses to mouse pressed events for the editor canvas.
      * If a constraint is selected the right click menu is updated to allow the selected tab to be removed from
      * the selected constraint. */
    case e: MousePressed if (canvas.editorMode == EditorMode.ConstraintEdit)=>
      if (canvas.selectedTab == null || canvas.selectedConstraint == null) {
        canvas.constraintsPopupMenu.contents-=(removeConstraintTabMenuItem)
      } else if (canvas.selectedConstraint.leftSideContains(canvas.selectedTab)) {
        if (canvas.selectedConstraint.getLeftSide.length == 1) {
          // Only 1 item left, don't allow them to remove
          canvas.constraintsPopupMenu.contents-=(removeConstraintTabMenuItem)
        } else {
          // 2+ items left, allow remove.
          canvas.constraintsPopupMenu.contents+=(removeConstraintTabMenuItem)
        }
      } else {
        canvas.constraintsPopupMenu.contents-=(removeConstraintTabMenuItem)
      }
  }

  /**
   * Method to remove the selected tab from the selected constraint
   *
   * @throws LinearProgrammingException
   */
  def apply {
    if (canvas.selectedTab != null && canvas.selectedConstraint.leftSideContains(canvas.selectedTab) && canvas.selectedConstraint.getLeftSide.length != 1) {
      //Removes a variable from the selected constraint
      val summands: Array[Summand] = canvas.selectedConstraint.getLeftSide
      var newSummands: Array[Summand] = null
      newSummands = new Array[Summand](summands.length - 1)
      var index: Int = 0
      for (s <- summands) {
        if (s.getVar ne canvas.selectedTab) {
          newSummands(index) = s
          index += 1
        }
      }
      canvas.selectedConstraint.setLeftSide(newSummands)
      //End of removing a variable from selected constraint
      canvas.lMLayout.propertiesWindow.propertiesPanel.update
      canvas.revalidate()
    }
    canvas.repaint()
    if (canvas.selectedTab != null) canvas.lMLayout.propertiesWindow.propertiesPanel.constraintsPanel.updateSelectedConstraintText
    canvas.lMLayout.propertiesWindow.propertiesPanel.repaint()
  }
}
