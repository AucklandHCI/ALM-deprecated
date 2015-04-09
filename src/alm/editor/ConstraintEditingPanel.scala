package alm.editor

import scala.swing._
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.util.ArrayList
import java.util.Arrays
import java.util.List
import javax.swing._
import linsolve.Constraint
import linsolve.OperatorType
import linsolve.Summand
import linsolve.Variable
import scala.util.control.Breaks
import scala.collection.JavaConversions._
import scala.swing.event.{ValueChanged, MouseClicked}
import scala.Double
import scala.swing.event.MouseClicked

/**
 * Class defines the panel that replaces a constraint label for editing.
 * <p>
 * This is used in the constraint tab of the properties window.
 *
 * @author bwhi058
 *
 * @param v list of variables currently contained within the LayoutSpec associated with the current editing session
 * @param constraintsPanel the ConstraintsPanel which the ConstraintEditingPanel is added to
 */
class ConstraintEditingPanel (v: List[Variable], constraintsPanel: ConstraintsPanel) extends scala.swing.Panel with ItemListener with ActionListener {

  private var constraint: Constraint = null //the constraint to be edited by this panel
  private var variables: List[Variable] = v //list of variables passed in, which is from the layoutSpec
  private[editor] var constraintCoeffs: ArrayList[JTextField]  = new ArrayList[JTextField] //there is one comboBox for each summand- representing the coefficient
  private[editor] var constraintVars: ArrayList[JComboBox[_]] = new ArrayList[JComboBox[_]] //there is one comboBox for each summand - representing the variables (ie left, right, bottom, top, leftInset, etc)
  private[editor] var operatorCombo: JComboBox[_] = null //operator combo containing either <=, = or >=
  private[editor] var rightSide: TextField =  null //textField to make the "right side" of a constraint editable
  private[editor] var penalty: TextField = null //textField to make the penalty editable

  peer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 3)) //set the flow layout so each component is added after each other in a line
  peer.setBackground(Color.lightGray)
  peer.setPreferredSize(new Dimension(200, 25))

  private[editor] var addVariableButton: Button = new Button("+"){ //button to add a variable (a textField representing the coefficient and a comboBox containing a list of X/YTabs) to the constraint to be edited by this panel
    listenTo(this.mouse.clicks)
    reactions+= {
      case e:MouseClicked =>
        //Looks through and looks for the variables that have already been added to the list of summands, adds the next one in the list that isn't already displaying (has already been added)
      var defaultVar: Variable = variables.get(0) //get the first variable in the variable list
      val isSelected: Array[Boolean] = new Array[Boolean](variables.size) //make array of booleans the same size as variables
      for (j <- constraintVars) { //for each summand comboBox
        isSelected(variables.indexOf(j.getSelectedItem)) = true //set the position of isSelected for the current selected item to be true
      }
      val loop = new Breaks
      loop.breakable {
        for (i <- 0 until isSelected.length) { //go each item in isSelected
          if (!isSelected(i)) { //if false, not selected
            defaultVar = variables.get(i) //then get the corresponding variable and make the constraintVars comboBox display this
            loop.break
          }
        } //end for
      } //end breakable
      val summands: scala.collection.mutable.Buffer[Summand] = (constraint.getLeftSide).toBuffer
      summands.+=(new Summand(0.0, defaultVar)) //get the current summands from the constraint, and then add a new summand with 0.0 as coefficient and defaultVar appearing in the comboBox
      constraint.setLeftSide(summands.toArray) //reset the summands
      refreshGUIComponents //remove all the widgets and re-insert everything with the added variable
     }
  }

  private[editor] var removeVariableButton: Button =  new Button("-"){ //button to remove the last variable in the constraint to be edited by this panel
    listenTo(this.mouse.clicks)
    reactions+= {
      case e:MouseClicked =>
        val summands: scala.collection.mutable.Buffer[Summand]  = (constraint.getLeftSide).toBuffer //gets the current list of summands
        summands.remove(summands.size - 1) //removes the last one added
        constraint.setLeftSide(summands.toArray) //resets the summand list to the constraint
        refreshGUIComponents //remove all the widgets and re-insert everything minus the removed variable
    }
  }

  /**
   * Set the constraint to be edit by this panel.
   *
   * @param c The constraint to be edited.
   */
  def setConstraint(c: Constraint) {
    constraint = c
    if (c != null) {
      refreshGUIComponents
    }
  }

  /**
   * Get the constraint being edited by this panel.
   *
   * @return The constraint being edited.
   */
  def getConstraint: Constraint = {
    return constraint
  }

  /**
   * Updates the values in the constraint to match the changes made in the panel.
   * Called when constraintVars, constraintCoeffs or operatorCombo is edited
   *
   */
  def finalizeConstraintValues {
    try {
      val newSummands = new Array[Summand](constraintCoeffs.size)  //make an array of the same size as constraintCoeffs
        for (i <- 0 until newSummands.length) { //go through each one
            val coeff: Double = (constraintCoeffs.get(i).getText).toDouble //get the value of the coefficient
            val v: Variable = constraintVars.get(i).getSelectedItem.asInstanceOf[Variable] //and get the value of the variable from the comboBox that is currently being selected
            newSummands(i) = new Summand(coeff, v) //create the new summand
        }
      //update the constraint to have the new changes as updated in the Panel
      constraint.setLeftSide(newSummands)
      val opType: OperatorType = StringToOperator(operatorCombo.getSelectedItem.asInstanceOf[String])
      constraint.setOp(opType)
      val rightSideValue: Double = (rightSide.peer.getText).toDouble
      constraint.setRightSide(rightSideValue)
      val penaltyValue: Double = (penalty.peer.getText).toDouble
      constraint.setPenalty(penaltyValue)
    }
    catch {
      case numForErr: NumberFormatException => {
        System.out.println(numForErr)
      }
    }
  }

  /**
   * Method called when a drop down box item is altered, refreshes the GUI
   */
  def itemStateChanged(e: ItemEvent) {
    if (e.getSource eq operatorCombo) { //when another operator is selected from the comboBox, update the constraint to reflect the changes and repaint the canvas
      finalizeConstraintValues
      //refreshGUIComponents //Don't need to remove all the widgets and re-insert everything just because selection changed
      constraintsPanel.aLMEditorCanvas.revalidate
      constraintsPanel.aLMEditorCanvas.repaint
    }
    else if (constraintVars.contains(e.getSource)) { //if the option clicked on is within the list of constraint variables, update the constraint to reflect the changes and repaint the canvas
      if (e.getStateChange == ItemEvent.SELECTED && constraintVars.contains(e.getSource)) { //if the state change indicates that the item was selected
        finalizeConstraintValues
        //refreshGUIComponents //Don't need to remove all the widgets and re-insert everything just because selection changed
        constraintsPanel.aLMEditorCanvas.revalidate
        constraintsPanel.aLMEditorCanvas.repaint
      }
    }
    this.repaint()
  }

  /**
   * Method called when one of the Buttons is pressed or enter pressed in a text field.
   */
  def actionPerformed(e: ActionEvent) {
    if (constraintCoeffs.contains(e.getSource)) { //if the option clicked on is within the list of constraintCoeffs (one of the variable coefficients in the constraint this panel is editing), update the constraint to reflect the changes and repaint the canvas
      val actionField: JTextField = e.getSource.asInstanceOf[JTextField]
      try {
        finalizeConstraintValues
        //refreshGUIComponents //Don't need to remove all the widgets and re-insert everything just because selection changed
        constraintsPanel.aLMEditorCanvas.revalidate
        constraintsPanel.aLMEditorCanvas.repaint
      }
      catch {
        case err: NumberFormatException => {
          val indexOfActionField: Int = constraintCoeffs.indexOf(e.getSource)
          val summands: Array[Summand] = constraint.getLeftSide
          actionField.setText("" + summands(indexOfActionField).getCoeff)
        }
      }
    }
  }

  /**
   * Removes a variable from the Variables ComboBoxes
   *
   * @param v The variable to be removed
   * @param excludeBox The combo box to be excluded from the removal.
   */
  private def removeVariableFromComboBoxes(v: Variable, excludeBox: JComboBox[_]) {
    import scala.collection.JavaConversions._
    for (combo <- constraintVars) {
      if (combo ne excludeBox) combo.removeItem(v) //removes this variable from every variable comboBox in the constraintVars list except for the excludeBox
    }
  }

  /**
   * Updates the GUI components to match the selected constraint.
   * It removes all the widgets previously on the constraintEditingPanel
   * and adds everything again in a line
   */
  def refreshGUIComponents {
    this.peer.removeAll //remove all of the components from the panel editing the currently selected constraint
    ////This section adds the components of the left-side of the constraint////
    constraintCoeffs.clear
    constraintVars.clear
    if (constraint != null) {
      val summands: Array[Summand] = constraint.getLeftSide
      var varColor: Color = ColorChooser.getInstance.getColor(summands(0).getVar)
      //set up the first textField for the first coefficient, add it to the panel and then to the list
      var coeffText: JTextField = new JTextField((summands(0).getCoeff).toString, 3)
      coeffText.addActionListener(this)
      coeffText.setForeground(varColor)
      peer.add(coeffText)
      constraintCoeffs.add(coeffText)
      //set up the first textField for the first variable, add it to the panel and then to the list
      var varsCombo: JComboBox[_] = createVariableComboBox(summands, varColor, 0)
      peer.add(varsCombo)
      constraintVars.add(varsCombo) //add one comboBox
      //Go from the second summand (because the first has already been added). For each summand, add a "+" JLabel, add each coeff textField, then variable
        for (i <- 1 until summands.length) {
                      peer.add(new JLabel(" + ")) //then between each comboBox add a plus label
            varColor = ColorChooser.getInstance.getColor(summands(i).getVar)
            coeffText = new JTextField((summands(i).getCoeff).toString(), 3) //each summand is checked for their coefficient
            coeffText.addActionListener(this)
            coeffText.setForeground(varColor)
            peer.add(coeffText)
            constraintCoeffs.add(coeffText)
            varsCombo = createVariableComboBox(summands, varColor, i)
            peer.add(varsCombo)
            constraintVars.add(varsCombo)
        }
      //double nested for loop, need to find out what this does and optimize
      for (combo <- constraintVars) {
        removeVariableFromComboBoxes(combo.getSelectedItem.asInstanceOf[Variable], combo)
      }
      ////This section adds the operator between the left-side and right-side of the constraint////
      peer.add(new JLabel(" "))
      operatorCombo = buildOperatorComboBox(constraint.getOp)
      operatorCombo.setPreferredSize(new Dimension(operatorCombo.getPreferredSize.getWidth.asInstanceOf[Int], 20))
      operatorCombo.addItemListener(this)
      peer.add(operatorCombo)
      peer.add(new JLabel(" "))
      ////This section adds the right-side of the constraint////
      //instead of making a new TextField each time, you just set the text?
      rightSide= new TextField((constraint.getRightSide).toString) {
        listenTo(this)
        reactions+={
          case e: ValueChanged =>
            try {
              val rightSideValue: Double = (rightSide.peer.getText).toDouble
              constraint.setRightSide(rightSideValue)
              constraintsPanel.aLMEditorCanvas.revalidate
              constraintsPanel.aLMEditorCanvas.repaint
            }
            catch {
              case err: NumberFormatException => {
                //rightSide.peer.setText("" + constraint.getRightSide)
              }
            }
        }
      }
      peer.add(rightSide.peer)
      ////This section adds the penalty component////
      peer.add(new JLabel(" Penalty = "))
      penalty = new TextField((constraint.getPenalty).toString) {
        listenTo(this)
        reactions+={
          case e: ValueChanged =>   try {
            val penaltyValue: Double = (penalty.peer.getText).toDouble
            constraint.setPenalty(penaltyValue)
            constraintsPanel.aLMEditorCanvas.revalidate
            constraintsPanel.aLMEditorCanvas.repaint
          }
          catch {
            case err: NumberFormatException => {
              //penalty.peer.setText("" + constraint.getPenalty)
            }
          }
        }
      }
      peer.add(penalty.peer)
      ////This section adds the two buttons for adding and removing a variable////
      if (variables.size == constraintVars.size) { //if we have added all the variables in the layoutSpec, don't allow adding of anymore
        addVariableButton.peer.setEnabled(false)
      }
      else {
        addVariableButton.peer.setEnabled(true)
      }
      peer.add(addVariableButton.peer)
      if (summands.length <= 1) { //if there are no summands, don't allow removal
        removeVariableButton.peer.setEnabled(false)
      }
      else {
        removeVariableButton.peer.setEnabled(true)
      }
      peer.add(removeVariableButton.peer)
    }
  }

  /**
   * Creates and sets up a Variable JComboBox representing a summand variable.
   * <p>
   * Calls {@link #populateVariablesComboBox()} to create and populate the JComboBox
   *
   * @param summands The list of summands for the constraint.
   * @param varColor The color to display the variable label.
   * @param i The index of the summand being displayed.
   *
   * @return VariableComboBox The populated JComboBox.
   */
  private def createVariableComboBox(summands: Array[Summand], varColor: Color, i: Int): JComboBox[_] = {
    val varsCombo: JComboBox[_] = populateVariablesComboBox
    varsCombo.setSelectedItem(summands(i).getVar)
    if (!((varsCombo.getSelectedItem.asInstanceOf[Variable]).toString == summands(i).getVar.toString)) {
        for (j <- 0 until varsCombo.getItemCount) {
            if ((varsCombo.getItemAt(j).asInstanceOf[Variable]).toString == summands(i).getVar.toString) varsCombo.setSelectedIndex(j)
        }
    }
    varsCombo.setPreferredSize(new Dimension(varsCombo.getPreferredSize.getWidth.asInstanceOf[Int], 20))
    varsCombo.setForeground(varColor)
    varsCombo.addItemListener(this)
    return varsCombo
  }

  /**
   * Gets the color that represents the variable.
   *
   * @param variableName The name of the variable.
   * @return The color representing the variable.
   */
  private def getVariableColor(variableName: String): Color = {
    import scala.collection.JavaConversions._
    for (v <- variables) {
      if (v.toString == variableName) {
        return ColorChooser.getInstance.getColor(v)
      }
    }
    return Color.BLACK
  }

  /**
   * Creates and populates a Variable JComboBox
   * <p>
   * When using to represent a summand {@link #createVariableComboBox(Summand[], Color, int)} should be called instead.
   *
   * @return The created and populated Variable JComboBox
   */
  private def populateVariablesComboBox: JComboBox[_] = {
    val varsCombo: JComboBox[Variable] = new JComboBox[Variable]
    for (v <- variables) {
      varsCombo.addItem(v)
    }
    //Makes sure every item in comboBox has a color corresponding to the variable //TODO: Overriding a method in Scala with generic parameters is difficult
//    object TaskCellRenderer extends ListCellRenderer[ConstraintEditingPanel] {
//
//      val peerRenderer = new DefaultListCellRenderer{
//
//      override def getListCellRendererComponent (list: JList[_ <: ConstraintEditingPanel], task: ConstraintEditingPanel, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component = {
//        val retval: JLabel = super.getListCellRendererComponent(list, task, index, isSelected, cellHasFocus).asInstanceOf[JLabel]
//        retval.setForeground(getVariableColor(task.toString))
//        return retval.asInstanceOf[JLabel]
//      }
//      }
//      varsCombo.setRenderer(peerRenderer)
//    }
//
    return varsCombo
  }

  /**
   * Creates and sets up the operator JComboBox
   *
   * @param o The OperatorType to set as selected.
   * @return The operator JComboBox.
   */
  private def buildOperatorComboBox(o: OperatorType): JComboBox[_] = {
    val opCombo: JComboBox[String] = new JComboBox[String]
    opCombo.addItem("=")
    opCombo.addItem("<=")
    opCombo.addItem(">=")
    if (o eq OperatorType.EQ) opCombo.setSelectedIndex(0)
    else if (o eq OperatorType.LE) opCombo.setSelectedIndex(1)
    else if (o eq OperatorType.GE) opCombo.setSelectedIndex(2)
    return opCombo
  }

  /**
   * Converts an OperatorType to a String.
   * <p>
   * Refer to {@link Constraint#OperatorToString(OperatorType)}
   *
   * @param o The OperatorType to be converted.
   * @return The resulting String.
   */
  @SuppressWarnings(Array("unused")) private def OperatorToString(o: OperatorType): String = {
    return this.constraint.OperatorToString(o)
  }

  /**
   * Converts a String into an OperatorType.
   * <p>
   * Refer to {@link Constraint#StringToOperator(String)}
   *
   * @param s The string to be converted
   * @return The resulting OperatorType.
   */
  private def StringToOperator(s: String): OperatorType = {
    return this.constraint.StringToOperator(s)
  }

}