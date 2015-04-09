package alm.editor.operations

import alm.editor.{PropertiesPanel, EditorMode, ALMEditorCanvas}
import scala.swing.Action
import linsolve.Summand
import scala.swing.event.MousePressed
import scala.swing.MenuItem

/**
 * This class defines the Add selected constraint tab editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * The class extends the Action class and implements the apply method
 * It then instantiates a new MenuItem passing itself as the action in the parameter
 * Thus this MenuItem, when added to the constraintsPopupMenu from ALMEditorCanvas, will perform the action defined in the apply method
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class AddSelectedConstraintTab (canvas: ALMEditorCanvas) extends Action("Add Tab to Constraint"){
  var addConstraintTabMenuItem = new MenuItem(this)
  //already have listenTo(this.mouse.clicks) in canvas
  canvas.reactions+={
    /** Adds more responses to mouse pressed events for the editor canvas.
      * If a constraint is selected the right click menu is updated to allow the selected tab to be added to
      * the selected constraint. */
    case e: MousePressed if (canvas.editorMode == EditorMode.ConstraintEdit)=>
      if (canvas.selectedTab == null || canvas.selectedConstraint == null) {
        // No tab selected - don't show menu item
        canvas.constraintsPopupMenu.contents-=(addConstraintTabMenuItem)
      } else if (canvas.selectedConstraint.leftSideContains(canvas.selectedTab)) {
      //If already contains the tab as a variable, don't allow them to add the tab to constraint
      canvas.constraintsPopupMenu.contents-=(addConstraintTabMenuItem)
      } else { //otherwise, allow adding of the menu item to the popupMenu
        canvas.constraintsPopupMenu.contents+=(addConstraintTabMenuItem)
      }
  }

  /**
   * Adds the currently selected tab to the selected constraint
   *
   * @throws LinearProgrammingException
   */
  def apply {
    if (canvas.selectedTab != null && !canvas.selectedConstraint.leftSideContains(canvas.selectedTab)) {
      // Adds a variable to the selected constraint.
      val summands: Array[Summand] = canvas.selectedConstraint.getLeftSide
      var newSummands: Array[Summand] = null
      newSummands = new Array[Summand](summands.length + 1)
      var index: Int = 0
      for (s <- summands) {
        newSummands(index) = s
        index += 1
      }
      newSummands(summands.length) = new Summand(1.0, canvas.selectedTab)
      canvas.selectedConstraint.setLeftSide(newSummands)
      //End adding a variable to the selected constraint
      canvas.lMLayout.propertiesWindow.propertiesPanel.update
      canvas.revalidate()
    } //end if
    canvas.repaint()
    if (canvas.selectedTab != null) canvas.lMLayout.propertiesWindow.propertiesPanel.constraintsPanel.updateSelectedConstraintText
    canvas.lMLayout.propertiesWindow.propertiesPanel.repaint()
  }
}
