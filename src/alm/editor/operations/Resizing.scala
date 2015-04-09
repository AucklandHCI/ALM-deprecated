package alm.editor.operations

import alm.editor.ALMEditorCanvas
import scala.swing.event.{MouseMoved, MouseDragged, MouseReleased, MousePressed}
import alm.editor.{EditorMode, ALMEditorCanvas}
import java.awt.{Point, Cursor}
import alm.{YTab, XTab}
/**
 * This class defines the Resizing editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * It registers MousePressed/Released/Moved/Dragged reactions for the canvas to perform the operation
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class Resizing (canvas: ALMEditorCanvas){

  canvas.reactions+={
    /** Adds more responses to mouse pressed events for the editor canvas. If in Area Edit Mode, updates the selectedXTab, selectedYTab, showXTab, showYTab variables. */
    case e: MousePressed if(canvas.editorMode == EditorMode.AreaEdit) =>
    //When you press mouse near an XTab or YTab, the selectedXTab or selectedYTab variables would be updated to be the closest XTab or YTab
    //bounding the selectedArea (the click has to be within a given tolerance - so quite close)
    if (canvas.selectedArea != null) {
      canvas.selectedXTab = canvas.getAreaXTabSelected(canvas.selectedArea, new Point(e.point))
      canvas.selectedYTab = canvas.getAreaYTabSelected(canvas.selectedArea, new Point(e.point))
    }
    //If a XTab is selected, then showXTab boolean would become true and within the paint method, all the XTabs would be painted blue
    //so that the user can see which XTab they can drag to, to resize the component
    if (canvas.selectedXTab != null) {
      canvas.showXTab = true
    }
    //If a YTab is selected, then showYTab boolean would become true and within the paint method, all the YTabs would be painted blue
    //so that the user can see which YTab they can drag to, to resize the component
    if (canvas.selectedYTab != null) {
      canvas.showYTab = true
    }
     canvas.revalidate()
     canvas.repaint
    /** Adds more responses to mouse dragged events for the editor canvas. If in Area Edit Mode, detect the X/YTabs below the mouse point. */
    case e: MouseDragged if(canvas.editorMode == EditorMode.AreaEdit)=>
      //The tabs near the cursor are detected as mouse is being dragged
      //This is so that later the area can be resized to the new XTab or YTab that the mouse is dragging to
      canvas.mouseOverXTab = canvas.getXTabSelected(new Point(e.point))
      canvas.mouseOverYTab = canvas.getYTabSelected(new Point(e.point))
      canvas.repaint
    /** Adds more responses to mouse released events for the editor canvas. If in Area Edit Mode,
      * If a tab is selected and the cursor is near another tab
      * in the GUI, the selected tab of the area is changed to the other tab,
      * resizing the area. */
    case e: MouseReleased if(canvas.editorMode == EditorMode.AreaEdit) =>
      canvas.peer.setCursor(Cursor.getDefaultCursor)
      canvas.moveNESW = false
      canvas.moveNWSE = false
      if (canvas.selectedArea != null) {
      // If a XTab has been selected by pressing the mouse close to it, replace it with the mouseoverXTab, so resizing the component width
      if (canvas.selectedXTab != null && canvas.mouseOverXTab != null) {
      // First case, the selectedXTab is actually the left border of the selectedArea
        if (canvas.selectedXTab eq canvas.selectedArea.left) {
          //Store the previous selectedArea left border in a constant
          val oldLeft: XTab = canvas.selectedArea.getLeft
          //replace the selectedArea left border with the XTab that the cursor has dragged to
          canvas.selectedArea.setLeft(canvas.mouseOverXTab.asInstanceOf[XTab])
          //If the XTab the cursor has dragged to is within the boundaries of another component's widget (checkForOverlap method returns true)
          //Then set the selectedArea left border back to oldLeft
          if (canvas.checkForOverlap(canvas.selectedArea)) {
            canvas.selectedArea.setLeft(oldLeft)
          }
        }
        else { // Second case, the selectedXTab is actually the right border of the selectedArea
          //Store the previous selectedArea right border in a constant
          val oldRight: XTab = canvas.selectedArea.getRight
          //replace the selectedArea right border with the XTab that the cursor has dragged to
          canvas.selectedArea.setRight(canvas.mouseOverXTab.asInstanceOf[XTab])
          //If the XTab the cursor has dragged to is within the boundaries of another component's widget
          //Then set the selectedAre right border to oldRight
          if (canvas.checkForOverlap(canvas.selectedArea)) {
            canvas.selectedArea.setRight(oldRight)
          }
        }
      }
        // If a YTab has been selected by pressing the mouse close to it, replace it with the mouseoverYTab, so resizing the component width
      if (canvas.selectedYTab != null && canvas.mouseOverYTab != null) {
        // First case, the selectedYTab is actually the top border of the selectedArea
        if (canvas.selectedYTab eq canvas.selectedArea.top) {
          val oldTop: YTab = canvas.selectedArea.getTop
          canvas.selectedArea.setTop(canvas.mouseOverYTab.asInstanceOf[YTab])
          if (canvas.checkForOverlap(canvas.selectedArea)) {
            canvas.selectedArea.setTop(oldTop)
          }
        }
        else {
        // Second case, the selectedYTab is actually the bottom border of the selectedArea
          val oldBottom: YTab = canvas.selectedArea.getBottom
          canvas.selectedArea.setBottom(canvas.mouseOverYTab.asInstanceOf[YTab])
          if (canvas.checkForOverlap(canvas.selectedArea)) {
            canvas.selectedArea.setBottom(oldBottom)
          }
        }
       }
      } //end of if selectedArea!=null
      //Once the resizing has been done, don't need to showYTab or showXTab in blue anymore. Tells the paint method to stop showing them
      canvas.showYTab = false
      canvas.showXTab = false
      //Once resizing has been done (mouse released), need to reset the XTabs and YTabs that are detected when the mouse is being dragged
      canvas.mouseOverXTab = null
      canvas.mouseOverYTab = null
      //The selectedXTab and selectedYTab are reset until the next time mouse press is near a XTab or YTab
      canvas.selectedXTab = null
      canvas.selectedYTab = null
      canvas.revalidate()
      //Updates the comboBoxes in the propertiesWindow so they are showing the correct number selections for areas, components, left, right, top and bottom borders
      canvas.lMLayout.propertiesWindow.propertiesPanel.update
      //Repaint the canvas because we have done resizing
      canvas.repaint
    /** Adds more responses to mouse released events for the editor canvas. If in Area Edit Mode, changes the cursor to the appropriate resize cursor if near a tab.*/
    case e: MouseMoved if(canvas.editorMode == EditorMode.AreaEdit) =>
    //The cursor changes if it is close to the edge of components, while the mouse is being moved
    if (canvas.selectedArea != null) {
      //ShowXTab means that a XTab has been selected (see MousePressed method) so the horizontal arrow cursor should be displayed
      if (canvas.showXTab && !canvas.showYTab) {
        canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
      }
      //ShowYTab means that a YTab has been selected (see MousePressed method) so the vertical arrow cursor should be displayed
      else if (!canvas.showXTab && canvas.showYTab) {
        canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
      }
      //If both a XTab and YTab has been selected, it means the mouse is being pressed on the corner of a particular component
      //This means cursor should be the diagonal one
      else if (canvas.showXTab && canvas.showYTab) {
        if (canvas.moveNESW) canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))
        else if (canvas.moveNWSE) canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))
      }
      else {
        canvas.peer.setCursor(Cursor.getDefaultCursor)
      }
     }
      //Not sure
      if (e.peer.getButton == java.awt.event.MouseEvent.NOBUTTON && canvas.mouseOverArea != null) {
        canvas.isNearAreaBorder(new Point(e.point), canvas.mouseOverArea)
      }
      canvas.revalidate()
      canvas.repaint()
  }
}
