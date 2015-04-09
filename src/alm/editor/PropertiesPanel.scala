package alm.editor

import java.io.File

import scala.swing._
import scala.swing.TabbedPane.Page

import alm._
import javax.swing.{JFileChooser, JOptionPane}
import scala.collection.mutable.ListBuffer

/**
 * This class defines the properties panel that gets added to the {@link alm.editor.PropertiesWindow} Frame
 * Within this class, the editing canvas {@link alm.editor.ALMEditorCanvas}, areaPanel {@link alm.editor.AreaPanel},
 * constraintsPanel {@link alm.editor.ConstraintsPanel} are instantiated.
 *
 * Area and Constraint panels are added to a tabbedPane and shown
 * Defines the load,save and exit methods in the menu within PropertiesWindow
 * Has update method that references the update methods in area and constraints panels
 *
 * @author Brent Whiteley, Irene
 *
 * @param parentContainer The container being edited.
 * @param layout The ALMLayout defining the container's layout.
 * @throws alm.ALMException
 */

class PropertiesPanel(val parentContainer: java.awt.Container, val layout: ALMLayout) extends Panel {

  /** Editor canvas widget that replaces th real widgets in the container that is edited */
  var aLMEditorCanvas: ALMEditorCanvas = new ALMEditorCanvas(layout)
  preferredSize = new Dimension(800,600)
  //For loading and saving files
  final val fc: JFileChooser = new JFileChooser

  // Panels for tabbed pane in properties window, for selecting and using the different editing modes
  var areaPanel: AreaPanel = new AreaPanel(PropertiesPanel.this, aLMEditorCanvas)
  var constraintsPanel: ConstraintsPanel = new ConstraintsPanel(PropertiesPanel.this, aLMEditorCanvas)
  peer.add(new TabbedPane {
    pages += new Page("Areas", areaPanel)
    pages += new Page("Constraints", constraintsPanel)
    //because listenTo(selection) only calls the cases when a tab is pressed, have to make sure that we start off with areaMode by default
    listenTo(selection)
    reactions += {
      case e => //don't have to have case e: ChangeEvent here because we are only listening to the selections anyway (whenever a page is pressed
        selection.page.title match { //gets the page from the selection, and shows whatever page whose title matches the selection.page.title
          case "Areas" => aLMEditorCanvas.setMode(EditorMode.AreaEdit)
          case "Constraints" => aLMEditorCanvas.setMode(EditorMode.ConstraintEdit)
        }
        //update
        aLMEditorCanvas.repaint()
    }
    peer.setVisible(true)
  }.peer)

  // replace the real layout with the aLMEditorCanvas
  parentContainer.removeAll
  parentContainer.add(aLMEditorCanvas.peer)
  aLMEditorCanvas.peer.setSize(parentContainer.getSize) //set the width and height of alMEditorCanvas so that width and height of component is not zero
  update

  /**
   * Method to load an XML file
   * @throws alm.ALMException
   */
  private[editor] def load {

    val returnVal: Int = fc.showOpenDialog(this.peer)
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      val file: File = fc.getSelectedFile
      layout.load(file)
    }
    //constraintEditingPanel.updateVariableList(layout.getLayoutSpec.getVariables) //TODO: Does an instance of constraintEditingPanel belong in this class?
    constraintsPanel.updateConstraints //changed from this.updateConstraints
    aLMEditorCanvas.repaint()
    this.update
  }

  /**
   * Method to save an XML file
   */
  private[editor] def save {
    var defaultFile: File = new File("myconfig.xml")
    fc.setSelectedFile(defaultFile)
    val returnVal: Int = fc.showSaveDialog(this.peer)
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      defaultFile = fc.getSelectedFile
      if (defaultFile.exists) {
        JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
      }
      layout.save(defaultFile)
    }
  }

  /**
   * Updates the edit form by detecting any changes made in the controls of
   * the editor(eg the currently selected area )and adjusting the attributes
   * of the various property boxes etc.
   */
  def update {
    if (aLMEditorCanvas.getMode eq EditorMode.AreaEdit) {
      areaPanel.updateAreas
    }
    else if (aLMEditorCanvas.getMode eq EditorMode.ConstraintEdit) {
      aLMEditorCanvas.revalidate
    }
  }



}