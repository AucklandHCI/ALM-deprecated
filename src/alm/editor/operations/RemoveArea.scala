package alm.editor.operations

import alm.editor.{ComponentInBin, EditorMode, ALMEditorCanvas}
import scala.swing.{Action, MenuItem}
import scala.swing.event.{MousePressed, MouseReleased}
import java.awt.{Point, Cursor}
import alm.Area

/**
 * This class defines the remove area editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * The class extends the Action class and implements the apply method
 * It then instantiates a new MenuItem passing itself as the action in the parameter
 * Thus this MenuItem, when added to the areaPopupMenu from ALMEditorCanvas, will perform the action defined in the apply method
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class RemoveArea(canvas: ALMEditorCanvas) extends Action("Remove area content") {
  canvas.areasPopupMenu.contents += new MenuItem(this)
  // install reactions for direct manipulation invocation of this operation
  canvas.reactions += {
    /** Adds more responses to mouse pressed events for the editor canvas. If in Area Edit Mode, the area underneath the mouse cursor is found and assigned to selectedArea. */
    case e: MousePressed if (canvas.editorMode == EditorMode.AreaEdit)=>
      //selectedArea = canvas.findSelectedArea(new Point(e.point)) //already defined somewhere else
      canvas.revalidate()
    /** Adds more responses to the mouse released events for the editor canvas. If in Area Edit Mode, if the mouse is released outside of the main window, remove the widget and add to bin */
    case e: MouseReleased if (canvas.editorMode == EditorMode.AreaEdit) =>
      if (canvas.selectedArea != null &&
        canvas.selectedXTab == null && canvas.selectedYTab == null && canvas.mouseOverArea == null &&
        canvas.selectedArea.getContent != null)
        apply
  }

  /**
   * Removes the area, adds the content to the bin and refreshes the GUI.
   *
   * @throws LinearProgrammingException
   */
  def apply {
    if (canvas.selectedArea == null) return // There's nothing to remove if selectedArea is not set
    canvas.lMLayout.propertiesWindow.getAreaPanel.palette.addToBin(canvas.selectedArea.content) // Add the current areas content to the list of JComponents in the bin
    //make new instance of ComponentInBin class and set area,insets and horizontal/vertical alignments so it contains all the information for the area
    var componentInBin = new ComponentInBin(canvas.selectedArea.content, canvas.lMLayout.propertiesWindow.getAreaPanel.palette)
    componentInBin.setArea(canvas.selectedArea)
    componentInBin.setInsets(canvas.selectedArea.getInsets)
    componentInBin.setHorizontalAlignment(canvas.selectedArea.getHorizontalAlignment)
    componentInBin.setVerticalAlignment(canvas.selectedArea.getVerticalAlignment)
    //Add the instance of ComponentInBin to the arrayList of bin items in the Palette class and repaint the entire propertiesWindow to make the bin item show up in the Palette
    canvas.lMLayout.propertiesWindow.getAreaPanel.palette.ComponentInBinList.add(componentInBin)
    canvas.lMLayout.propertiesWindow.repaint
    //canvas.selectedArea.remove //removing area from the specification.. but I'm not sure you need to do that
    canvas.lMLayout.getLayoutSpec.findAndRemove(canvas.selectedArea.getContent.getName) //replacing canvas.selectedArea.content = null
    //code for updating the changes
    canvas.lMLayout.layout(canvas.peer)
    canvas.revalidate
    canvas.updateVariables
    canvas.lMLayout.propertiesWindow.propertiesPanel.update
    canvas.lMLayout.propertiesWindow.repaint
    canvas.repaint
  }
}
