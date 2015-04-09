package alm.editor

import java.awt.{Dimension, FontMetrics, GridLayout}
import java.util.ArrayList

import scala.collection.mutable
import scala.collection.JavaConversions._
import scala.swing.{Button, Label, Panel, ScrollPane}
import scala.swing.event.MouseClicked

import alm._
import javax.swing.JPanel
import linsolve.{Constraint, OperatorType, Variable}

/**
 * Constraints Panel in the property window.
 * It defines all the components within it (labels, the JPanel which stores the labels, the ScrollPane, the two buttons and the constraintEditingPanel)
 * Also sets up all their reactions
 * Has a pointer to the ConstraintEditingPanel which replaces a Label whenever it is clicked
 *
 * Contains the updateConstraint method which is the update method of this class
 *
 * @param propertiesPanel the PropertiesPanel, the Panel to which ConstraintPanel is added
 * @param aLMEditorCanvas the editor canvas associated with the PropertiesPanel
 * @throws ALMException
 */
class ConstraintsPanel(propertiesPanel: PropertiesPanel, var aLMEditorCanvas: ALMEditorCanvas) extends Panel {
  //The ALMLayout defining the constraintsPanel layout
  private var constraintsLayout: ALMLayout = new ALMLayout
  preferredSize = new Dimension(800, 400)
  // widgets declaration and instantiation
  //constraintLabels (list of constraint Label widgets) corresponding to userConstraints (list of Constraints)
  var constraintLabels = mutable.Buffer[Label]()
  var userConstraints = mutable.Buffer[Constraint]()
  //Each of the Labels is added to this panel, keeps track of the index of each selection and updated when removeSelectedConstraintIndex or addSelectedConstraintIndex is called
  var constraintListPanel: JPanel = new JPanel
  //JScrollPane on the Panel so you can scroll through the list of Constraints if it becomes larger than the constraintListPanel
  var constraintScrollPane: ScrollPane = new ScrollPane
  //Integer to keep track of each constraint
  var selectedConstraintIndex: Int = -1 //index of the constraint currently being changed to the editing panel
  //Replaces the label with editable line when you click on it, called in changeSelectedConstraint
  var constraintEditingPanel: ConstraintEditingPanel = new ConstraintEditingPanel(propertiesPanel.layout.getLayoutSpec.getVariables,this)
  //Button for adding another constraint to the list which gets displayed initially as an editable ConstraintEditingPanel
  var addNewConstraintButton: Button = new Button("Add New Constraint") {
    listenTo(this.mouse.clicks)
    reactions += {
      case e: MouseClicked =>
        //when button is clicked, a default variable with default values is made and added to userConstraintsList
        val defaultVar: Variable = propertiesPanel.layout.getLayoutSpec.getVariables.get(0)
        val newConstraint: Constraint = propertiesPanel.layout.addConstraint(-1.0, defaultVar, OperatorType.EQ, 0)
        userConstraints.+=(newConstraint)
        //A new label is also made (with its reaction set up to respond to mouse click and change itself to editable form)
        val newLabel: Label = new Label(newConstraint.toString){
          listenTo(this.mouse.clicks)
          reactions += {
            case e: MouseClicked => changeSelectedConstraint(constraintLabels.indexOf(e.source))
          }
        }
        newLabel.peer.setPreferredSize(new Dimension(200, CONSTRAINT_LIST_ITEM_HEIGHT))
        constraintLabels.+=(newLabel) //add one more constraint to the arrayList storing all the labels
        constraintListPanel.add(newLabel.peer) //add to the actual panel containing all the labels
        changeSelectedConstraint(constraintLabels.size - 1) //change the last-added constraint label to the editable form
        constraintScrollPane.peer.getVerticalScrollBar.setValue(constraintScrollPane.peer.getVerticalScrollBar.getMaximum) //reset the scrollbar length
        //propertiesPanel.repaint()
        repaint
    }
  }
  //Button for removing the constraint clicked on from the list and also removes the label from the panel
  var removeConstraintButton: Button = new Button("Remove Selected Constraint") {
    listenTo(this.mouse.clicks)
    reactions += {
      case e: MouseClicked =>
        //if (selectedConstraintIndex == -1) return
        propertiesPanel.layout.getLayoutSpec.removeConstraint(userConstraints.get(selectedConstraintIndex))
        //remove from the list of constraints, list of labels and from showing up in the constraintListPanel
        userConstraints.remove(selectedConstraintIndex)
        constraintLabels.remove(selectedConstraintIndex)
        constraintListPanel.remove(selectedConstraintIndex)
        //Now need to re-assign the text on each constraintLabel, using the string representation of the userConstraint
        for (i <- 0 until userConstraints.size /*- constraintLabels.size*/) {
          constraintLabels.apply(i).text_=(userConstraints.apply(i).toString)
        }
        //tempIndex represents the index of the constraint in the list that will get changed into editable mode after the button has been clicked
        //it is either the index before or after the current constraint removed
        //If selected constraint is the last one in the list, then make the tempIndex the one before the index of the constraint removed
        //Otherwise, make it the same index as the constraint removed (which will appar as if the constraint after the one removed in the panel turns into editable form)
        val tempIndex: Int = if (selectedConstraintIndex == userConstraints.size) selectedConstraintIndex - 1 else selectedConstraintIndex
        selectedConstraintIndex = -1
        //When there is only one constraint left, selectedConstraintIndex would be 0 and userConstraint.size would be 1
        //Therefore the above block would go into else and give tempIndex of 0
        //Therefore when tempIndex reaches 0, it is down to its last index and you can change it to editable form
        if (tempIndex >= 0) {
          changeSelectedConstraint(tempIndex)
        }
        else { //but if tempIndex is below 0, means there is no constraint left and the button should not be pressed anymore
          removeConstraintButton.peer.setEnabled(false)
        }
        //propertiesPanel.repaint()
      repaint
    }
  }
  removeConstraintButton.peer.setEnabled(false)

  val CONSTRAINT_LIST_ITEM_HEIGHT: Int = 25

  // content, i.e. constraint data
  updateConstraints //so now the constraintLabel and userConstraints arrayLists are filled with the correct constraints and corresponding labels

  // layout stuff
  val y1: YTab = constraintsLayout.addYTab
  val x1: XTab = constraintsLayout.addXTab
  constraintsLayout.setFromPropertyWindow(true)
  peer.setLayout(constraintsLayout)
  constraintsLayout.addArea(constraintsLayout.getLeft, constraintsLayout.getTop, constraintsLayout.getRight, y1, constraintScrollPane.peer)
  constraintsLayout.addArea(constraintsLayout.getLeft, y1, x1, constraintsLayout.getBottom, addNewConstraintButton.peer)
  constraintsLayout.addArea(x1, y1, constraintsLayout.getRight, constraintsLayout.getBottom, removeConstraintButton.peer)
  font = addNewConstraintButton.peer.getFont
  val fontMetrics: FontMetrics = addNewConstraintButton.peer.getFontMetrics(font)
  val addConstraintsHeight: Int = fontMetrics.getHeight + 30
  constraintsLayout.addConstraint(1, constraintsLayout.getBottom, -1, y1, OperatorType.EQ, addConstraintsHeight)
  constraintsLayout.addConstraint(2, x1, -1, constraintsLayout.getRight, OperatorType.EQ, 0, 100)
  constraintScrollPane.peer.getViewport
  constraintListPanel.setLayout(new GridLayout(0, 1))

  for (l <- constraintLabels) {
    constraintListPanel.add(l.peer) //add each label to the panel using the add method from container
    //Each label is assigned an index starting from zero so it'll be easy-ish to remove them later using the indices
  }
  constraintScrollPane.peer.getViewport.add(constraintListPanel) //add the panel to the scrollpane

  /**
   * Initialises the constraints and updates them
   */
  def updateConstraints {
    //userConstraints is the list of constraints, only get the ones with an owner
    userConstraints = propertiesPanel.layout.getLayoutSpec.getConstraints filter (_.Owner == null)

    // make sure there are enough Labels for all the user constraints to show
    //go through each constraint in the list and make a new Label
    //this label reacts to mouse clicks on itself
    //and when you press it, the selected constraint becomes an editable line
    //This is done in the changeSelectedConstraint method which is passed the index of the position in the userConstraints arraylist
    for (i <- 0 until userConstraints.size /*- constraintLabels.size*/) {
      val newLabel = new Label(userConstraints.apply(i).toString) {
        preferredSize = new Dimension(200, CONSTRAINT_LIST_ITEM_HEIGHT)
        listenTo(this.mouse.clicks)
        reactions += {
          case e: MouseClicked =>
            changeSelectedConstraint(constraintLabels.indexOf(e.source))
        }
      }
      constraintLabels.+=(newLabel) //add each label to the constraintLabel list, which is a corresponding list to userConstraints
    }

  }

  /**
   * Change the selected constraint and replaces the label with the editing panel.
   *
   * @param newSelectionIndex The index of the selected constraint.
   */
  def changeSelectedConstraint(newSelectionIndex: Int) {
    //update the label information with new information
    if (selectedConstraintIndex != -1) {      //if the selectedConstraint is within the list
      constraintListPanel.remove(selectedConstraintIndex) //the label is removed from the panel showing the list of constraints equations, using the remove method in container
      val replacementLabel: Label = constraintLabels.apply(selectedConstraintIndex) //get the exact same label from the list of constraint labels
      constraintEditingPanel.finalizeConstraintValues //updates the constraint to match the changes made in the editing line
      replacementLabel.peer.setText(constraintEditingPanel.getConstraint.toString) //now update the label with the changed text
      constraintListPanel.add(replacementLabel.peer, selectedConstraintIndex) //now add it back to the panel
    }
    aLMEditorCanvas.selectedConstraint_$eq(userConstraints.apply(newSelectionIndex)) //change the selected constraint in almEditorCanvas
    constraintEditingPanel.setConstraint(userConstraints.apply(newSelectionIndex))
    //This is actually swapping out the GUI of the Label with the GUI of the editing line
    constraintListPanel.remove(newSelectionIndex)
    constraintListPanel.add(constraintEditingPanel.peer, newSelectionIndex)
    removeConstraintButton.peer.setEnabled(true)
    selectedConstraintIndex = newSelectionIndex
    //propertiesPanel.repaint()
    repaint
    this.revalidate
    aLMEditorCanvas.repaint()
  }

  /**
   * Alter the text of the selected constraint's JLabel.
   * This is called whenever a constraint is added to the list {@link alm.editor.operations.AddSelectedConstraintTab}
   * or removed {@link alm.editor.operations.RemoveSelectedConstraint}
   * or modified {@link alm.editor.operations.ModifyConstraint}
   *
   * This will change the text of the Label to reflect whatever changes was performed to it by adding/removing/modifying
   */
  def updateSelectedConstraintText {
    if (selectedConstraintIndex > -1) {
      constraintEditingPanel.refreshGUIComponents
      val replacementLabel: Label = constraintLabels.get(selectedConstraintIndex)
      replacementLabel.peer.setText(constraintEditingPanel.getConstraint.toString)
    }
  }
}