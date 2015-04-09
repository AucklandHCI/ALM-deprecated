package alm.editor.operations

import scala.swing.event.{MouseMoved, MouseDragged, MouseReleased, MousePressed}
import alm.editor.{EditorMode, ALMEditorCanvas}
import java.awt.{Point, Cursor}
import alm.{YTab, XTab}
/**
 * This class defines the Swap areas editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * It registers MousePressed/Released/Moved/Dragged reactions for the canvas to perform the operation
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class SwapAreas(canvas: ALMEditorCanvas){
  //Should probably have selectedArea and mouseOverArea here as local variables, less confusing this way?
  canvas.reactions+={
    /** Adds more responses to mouse pressed events for the editor canvas. If in Area Edit Mode, the mouse cursor is changed to MOVE_CURSOR, the area underneath the mouse cursor is found and assigned to mouseOverArea. */
    case e: MousePressed if (canvas.editorMode == EditorMode.AreaEdit)=>
      canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
      canvas.selectedArea = canvas.findSelectedArea(new Point(e.point))
      canvas.mouseOverArea = canvas.selectedArea
      canvas.revalidate()
    /** Adds more responses to the mouse released events for the editor canvas. If in Area Edit Mode, the area under the initial mouse click (selectedArea)
      * and the area the mouse is currently over which is also the area at the position of mouse release (mouseOverArea)*/
    case e: MouseReleased if(canvas.editorMode == EditorMode.AreaEdit && canvas.selectedArea != null) =>
      if (canvas.selectedXTab == null && canvas.selectedYTab == null && canvas.mouseOverArea != null && canvas.mouseOverArea != canvas.selectedArea) {
        // If there are no tabs selected (so as long as resizing is not happening, swap the X/YTabs bordering the area)
        val left: XTab = canvas.selectedArea.left
        val right: XTab = canvas.selectedArea.right
        val top: YTab = canvas.selectedArea.top
        val bottom: YTab = canvas.selectedArea.bottom
        canvas.selectedArea.setTop(canvas.mouseOverArea.top)
        canvas.selectedArea.setLeft(canvas.mouseOverArea.left)
        canvas.selectedArea.setRight(canvas.mouseOverArea.right)
        canvas.selectedArea.setBottom(canvas.mouseOverArea.bottom)
        canvas.mouseOverArea.setTop(top)
        canvas.mouseOverArea.setLeft(left)
        canvas.mouseOverArea.setRight(right)
        canvas.mouseOverArea.setBottom(bottom)
      }
    /** Adds more responses to mouse dragged events for the editor canvas. If in Area Edit Mode, the area underneath the mouse cursor when being dragged is found and assigned to mouseOverArea. */
    case e: MouseDragged if (canvas.editorMode == EditorMode.AreaEdit) =>
      canvas.mouseOverArea = canvas.findSelectedArea(new Point(e.point))
      if (canvas.mouseOverArea == null) {
        canvas.mouseOverArea = canvas.findTabbedArea(e.point) //find the closest tabs enclosing the cursor and makes an area from it
      }
      //Show the MOVE_CURSOR if not resizing
      if (!canvas.showXTab && !canvas.showYTab) canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))
      canvas.repaint
    /** Adds more responses to mouse moved events for the editor canvas. If in Area Edit Mode, the area under the mouse while moving is detected and assigned to mouseOverArea. */
    case e: MouseMoved if (canvas.editorMode == EditorMode.AreaEdit) =>
      canvas.mouseOverArea = canvas.findSelectedArea(new Point(e.point))
  }
}
