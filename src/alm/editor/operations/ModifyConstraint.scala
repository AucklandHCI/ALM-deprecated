package alm.editor.operations

import alm.editor.{EditorMode, ALMEditorCanvas}
import scala.swing.event.{MouseMoved, MouseDragged, MousePressed, MouseReleased}
import alm.{YTab, XTab}
import linsolve.{OperatorType, Variable, Summand}
import java.awt.Cursor

/**
 * This class defines the Modify constraint editing operation
 * It contains a reference to the ALMEditorCanvas {@link alm.editor.ALMEditorCanvas} because the class registers reactions for the canvas related to its editing operation
 * It registers MousePressed/Released/Moved/Dragged reactions for the canvas to perform the operation
 *
 * @param canvas the editor canvas associated with the PropertiesPanel
 *
 */
class ModifyConstraint (canvas: ALMEditorCanvas){

  canvas.reactions += {
    /** Adds more responses to mouse pressed events for the editor canvas. If in Constraint Edit Mode, keep track of the position of the mouse*/
    case e: MousePressed if (canvas.editorMode == EditorMode.ConstraintEdit)=>
      canvas.mouseDraggedLocation = e.point
      canvas.constraintsPopupMenu.revalidate()
    /** Adds more responses to mouse released events for the editor canvas. If in Constraint Edit Mode, releasing the mouse while having a distance constraint selected will change the right-side
      * the code for ratioConstraint is not yet used since it's never true*/
    case e: MouseReleased if (canvas.editorMode == EditorMode.ConstraintEdit)=>
      // Check if selected Tab is a part of the selected constraint. In
      // the case of a single variable in the constraint, change the value
      // of the right hand side to be the value of the tab.
    if (canvas.isFixedDistanceConstraint && canvas.selectedConstraint.leftSideContains(canvas.selectedTab)) {
          try {
            if (canvas.selectedTab.isInstanceOf[XTab]) {
              if (canvas.mouseDraggedLocation.x >= 0 && canvas.mouseDraggedLocation.x <= canvas.peer.getWidth) {
                canvas.selectedConstraint.setRightSide(canvas.mouseDraggedLocation.x)
              }
            }
            else if (canvas.selectedTab.isInstanceOf[YTab]) {
              if (canvas.mouseDraggedLocation.y >= 0 && canvas.mouseDraggedLocation.y <= canvas.peer.getHeight) {
                canvas.selectedConstraint.setRightSide(canvas.mouseDraggedLocation.y)
              }
            }
          }
          catch {
            case e1: Exception => {
              e1.printStackTrace
            }
          }
        }
        else if (canvas.isRatioConstraint) {
          try {
            val negativeSummands: Array[Summand] = canvas.selectedConstraint.getLeftSideWithNegativeCoefficients
            assert((negativeSummands.length == 0))
            val middleVar: Variable = negativeSummands(0).getVar
            if (canvas.selectedTab eq middleVar) {
              val positiveSummands: Array[Summand] = canvas.selectedConstraint.getLeftSideWithPositiveCoefficients
              assert((positiveSummands.length == 2))
              var leftLineSum: Summand = positiveSummands(0)
              var rightLineSum: Summand = positiveSummands(1)
              if (leftLineSum.getVar.getValue > rightLineSum.getVar.getValue) {
                leftLineSum = positiveSummands(1)
                rightLineSum = positiveSummands(0)
              }
              val leftSidePos: Double = leftLineSum.getVar.getValue
              val rightSidePos: Double = rightLineSum.getVar.getValue
              var rightSideRatio: Double = 1.0
              if (canvas.selectedTab.isInstanceOf[XTab]) {
                rightSideRatio = (canvas.mouseDraggedLocation.x - leftSidePos) / (rightSidePos - leftSidePos)
              }
              else if (canvas.selectedTab.isInstanceOf[YTab]) {
                rightSideRatio = (canvas.mouseDraggedLocation.y - leftSidePos) / (rightSidePos - leftSidePos)
              }
              if (rightSideRatio >= 0 && rightSideRatio <= 1) {
                val leftSideRatio: Double = 1 - rightSideRatio
                leftLineSum.setCoeff(leftSideRatio)
                rightLineSum.setCoeff(rightSideRatio)
              }
            }
          }
          catch {
            case e1: Exception => {
              e1.printStackTrace
            }
          }
        }
        if (canvas.selectedConstraint != null){
          canvas.lMLayout.propertiesWindow.propertiesPanel.constraintsPanel.constraintEditingPanel.setConstraint(canvas.selectedConstraint)
          canvas.lMLayout.propertiesWindow.propertiesPanel.constraintsPanel.updateSelectedConstraintText
        }
        canvas.lMLayout.propertiesWindow.propertiesPanel.update
        canvas.lMLayout.propertiesWindow.propertiesPanel.repaint
      canvas.revalidate()
      canvas.mouseBeingDragged = false
      canvas.repaint()
    /** Adds more responses to mouse dragged events for the editor canvas. If in Constraint Edit Mode, change the status of mouseBeingDragged and keep track of drag location*/
    case e: MouseDragged if (canvas.editorMode == EditorMode.ConstraintEdit) =>
      canvas.mouseBeingDragged = true
      canvas.mouseDraggedLocation = e.point
    /** Adds more responses to mouse moved events for the editor canvas. If in Constraint Edit Mode, change the cursor depending on if the tab selected is X or YTab*/
    case e: MouseMoved if (canvas.editorMode == EditorMode.ConstraintEdit) =>
      val vv: Variable = canvas.getTabNearPoint(e.point, canvas.TOL)
      if (vv != null) {
        if (vv.isInstanceOf[XTab]) {
          canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
        }
        else {
          canvas.peer.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
        }
      }
      else {
        canvas.peer.setCursor(Cursor.getDefaultCursor)
      }
  }


}
