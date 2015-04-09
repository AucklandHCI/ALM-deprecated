package alm.editor

import java.awt.{Insets, MouseInfo, Point, Cursor}
import alm._
import linsolve.{Variable, OperatorType}
import scala.swing._
import java.util.{HashMap, ArrayList}
import scala.collection.mutable
import scala.collection.JavaConversions._
import javax.swing._
import scala.util.control.Breaks
import scala.swing.SequentialContainer.Wrapper
import java.awt.Insets
import scala.swing.event.MousePressed
import scala.swing.event.MouseDragged

/**
 * Area Panel in the property window.
 * It defines all the components within it (labels,comboBoxes,textFields, the ScrollPane, and the Palette)
 * Also sets up all their reactions
 * Has a pointer to the Palette which stores bin items
 *
 * Contains the updateAreas method which is the update method of this class
 *
 * @param propertiesPanel the PropertiesPanel, the Panel to which ConstraintPanel is added
 * @param aLMEditorCanvas the editor canvas associated with the PropertiesPanel
 * @throws ALMException
 */
class AreaPanel(var propertiesPanel: PropertiesPanel, aLMEditorCanvas: ALMEditorCanvas) extends Panel {
  //The ALMLayout defining the areaPanel layout
  private[editor] var areaLayout: ALMLayout = new ALMLayout
  preferredSize = new Dimension(800, 400)
  // widgets declaration and instantiation
  //BinPanel (extends Panel) for holding the widgets in the bin
  var palette: Palette = new Palette(this)
  //The ScrollPane for the BinPanel, so it's scrollable
  var areaScrollPane: ScrollPane = new ScrollPane (palette)
  areaScrollPane.peer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED)
  areaScrollPane.peer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
  //variable for determining whether the comboBoxes content is currently being changed/activated
  var updateActivated: Boolean = false
  //area label, comboBox and list
  var areaLabel: Label = new Label {text = "Area"}
  var areaBox: ComboBox[String] = null
  var areaBoxList = mutable.Buffer[String]()
  //control label, comboBox and list
  var controlLabel: Label = new Label {text = "Content"}
  var controlBox: ComboBox[String] = null
  var controlBoxList = mutable.Buffer[String]()
  //Row label, comboBox and list
  var areaRowLabel: Label = new Label {text = "Row"}
  var areaRowBox: JComboBox[Object] = new JComboBox()
  //Column label, comboBox and list
  var areaColumnLabel: Label = new Label {text = "Column"}
  var areaColumnBox: JComboBox[Object] = new JComboBox()
  //Left label, combobox and list
  var leftLabel: Label = new Label {text = "Left"}
  var leftBox: ComboBox[AnyRef] = null
  var leftBoxList = mutable.Buffer[AnyRef]()
  //Right label, combobox and list
  var rightLabel: Label = new Label {text = "Right"}
  var rightBox: ComboBox[AnyRef] = null
  var rightBoxList = mutable.Buffer[AnyRef]()
  //Top label, combobox and list
  var topLabel: Label = new Label {text = "Top"}
  var topBox: ComboBox[AnyRef] = null
  var topBoxList = mutable.Buffer[AnyRef]()
  //Bottom label, combobox and list
  var bottomLabel: Label = new Label {text = "Bottom"}
  var bottomBox: ComboBox[AnyRef] = null
  var bottomBoxList = mutable.Buffer[AnyRef]()
  //Width/Height label and the two textfields
  var prefWHLabel: Label = new Label {text = "Width/Height"}
  var prefWidthBox: TextField = new TextField
  prefWidthBox.peer.setEditable(false)
  var prefHeightBox: TextField = new TextField
  prefHeightBox.peer.setEditable(false)
  //ArrayList for the JComponents in the bin, hashMaps which are changed when widgets are put in bin
  //TODO: Need to remove these as they are not used anymore
  private var binAreaInsets: HashMap[JComponent, Insets] = new HashMap[JComponent, Insets]
  private var binAlignments: HashMap[JComponent, Array[AnyRef]] = new HashMap[JComponent, Array[AnyRef]] //map of JComponent to an array of alignments

  /**
   * Change the selected area to the one picked in the combo box.
   */
  areaBox = new ComboBox(areaBoxList) {
    listenTo(selection)
    reactions += {
      case e =>
        if (!updateActivated) {
          val selectedAreaName: String = areaBox.peer.getSelectedItem.toString
          if (selectedAreaName != null) {
            aLMEditorCanvas.selectArea(selectedAreaName)
          }
          aLMEditorCanvas.showXTab = false
          aLMEditorCanvas.showYTab = false
        }
    }
  }

  /**
   * Change the selected area to the one containing the component chosen in the combo box.
   */
  controlBox = new ComboBox(controlBoxList) {
    listenTo(selection)
    reactions += {
      case e =>
        if (!updateActivated) {
          val selectedAreaName: String = controlBox.peer.getSelectedItem.toString
          if (selectedAreaName != null) {
            aLMEditorCanvas.selectArea(selectedAreaName)
          }
          aLMEditorCanvas.showXTab = false
          aLMEditorCanvas.showYTab = false
        }
    }
  }

  /**
   * Updates the left tab of the selected area to the X-Tab chosen in the combo box.
   */
  leftBox = new ComboBox(leftBoxList) {
    listenTo(selection)
    reactions += {
      case e => val selectedItem = leftBox.peer.getSelectedItem
        if (!updateActivated) {
          if (selectedItem eq rightBox.peer.getSelectedItem) {
            leftBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getLeft)
            //return
          }
        }
        //Ensure that the tab selected is not the same as the tab selected in right.
        if (!updateActivated && !(selectedItem eq rightBox.peer.getSelectedItem)) {
          if (selectedItem == "New Tab") {
            val newTab: Variable = createNewTab('X')
            if (newTab.isInstanceOf[XTab]) {
              aLMEditorCanvas.selectedArea.setLeft(newTab.asInstanceOf[XTab])
              aLMEditorCanvas.repaint()
              updateAreas
            }
          } else {
            aLMEditorCanvas.selectedArea.setLeft(selectedItem.asInstanceOf[XTab])
            aLMEditorCanvas.repaint()
          }
        }
    }
  }

  /**
   * Updates the right tab of the selected area to the X-Tab chosen in the combo box.
   */
  rightBox = new ComboBox(rightBoxList) {
    listenTo(selection)
    reactions += {
      case e => val selectedItem = rightBox.peer.getSelectedItem //use the wrapper method to get the item selected by mouse
        //It is either an XTab or a String if the user selected "New Tab"
        if (!updateActivated) {
          if (selectedItem eq rightBox.peer.getSelectedItem) {
            rightBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getRight)
            //return
          }
        }
        //Ensure that the tab selected is not the same as the tab selected in left.
        if (!updateActivated && !(selectedItem eq rightBox.peer.getSelectedItem)) {
          if (selectedItem == "New Tab") { //selectedItem instanceof String && selectedItem.equals("New Tab") //If the user wants to create a new XTab
            val newTab: Variable = createNewTab('X')
            if (newTab.isInstanceOf[XTab]) { //Check that it is a valid XTab and set the right side of the area to be that XTab
              aLMEditorCanvas.selectedArea.setRight(newTab.asInstanceOf[XTab])
              aLMEditorCanvas.repaint()
              updateAreas
            }
          } else { //it is an existing XTab
            aLMEditorCanvas.selectedArea.setRight(selectedItem.asInstanceOf[XTab]) //set the area's rightTab as the one selected in the comboBox
            aLMEditorCanvas.repaint()
          }
        }
    }
  }

  /**
   * Updates the top tab of the selected area to the Y-Tab chosen in the combo box.
   */
  topBox = new ComboBox(topBoxList) {
    listenTo(selection)
    reactions += {
      case e => val selectedItem = topBox.peer.getSelectedItem //use the wrapper method to get the item selected by mouse
        //It is either an YTab or a String if the user selected "New Tab"
        if (!updateActivated) {
          if (selectedItem eq topBox.peer.getSelectedItem) {
            topBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getTop)
            //return
          }
        }
        //Ensure that the tab selected is not the same as the tab selected in bottom.
        if (!updateActivated && !(selectedItem eq topBox.peer.getSelectedItem)) {
          if (selectedItem == "New Tab") { //selectedItem instanceof String && selectedItem.equals("New Tab") //If the user wants to create a new YTab
          val newTab: Variable = createNewTab('Y')
            if (newTab.isInstanceOf[YTab]) { //Check that it is a valid XTab and set the right side of the area to be that XTab
              aLMEditorCanvas.selectedArea.setTop(newTab.asInstanceOf[YTab])
              aLMEditorCanvas.repaint()
              updateAreas
            }
          } else { //it is an existing XTab
            aLMEditorCanvas.selectedArea.setTop(selectedItem.asInstanceOf[YTab]) //set the area's rightTab as the one selected in the comboBox
            aLMEditorCanvas.repaint()
          }
        }
    }
  }

  /**
   * Updates the bottom tab of the selected area to the Y-Tab chosen in the combo box.
   */
  bottomBox = new ComboBox(bottomBoxList) {
    listenTo(selection)
    reactions += {
      case e => val selectedItem = bottomBox.peer.getSelectedItem //use the wrapper method to get the item selected by mouse
        //It is either an YTab or a String if the user selected "New Tab"
        if (!updateActivated) {
          if (selectedItem eq bottomBox.peer.getSelectedItem) {
            bottomBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getBottom)
            //return
          }
        }
        //Ensure that the tab selected is not the same as the tab selected in bottom.
        if (!updateActivated && !(selectedItem eq bottomBox.peer.getSelectedItem)) {
          if (selectedItem == "New Tab") { //selectedItem instanceof String && selectedItem.equals("New Tab") //If the user wants to create a new YTab
          val newTab: Variable = createNewTab('Y')
            if (newTab.isInstanceOf[YTab]) { //Check that it is a valid XTab and set the right side of the area to be that XTab
              aLMEditorCanvas.selectedArea.setBottom(newTab.asInstanceOf[YTab])
              aLMEditorCanvas.repaint()
              updateAreas
            }
          } else { //it is an existing XTab
            aLMEditorCanvas.selectedArea.setBottom(selectedItem.asInstanceOf[YTab]) //set the area's rightTab as the one selected in the comboBox
            aLMEditorCanvas.repaint()
          }
        }
    }
  }

  //propertiesPanel.repaint()
  repaint

  //layout stuff
  areaLayout.setFromPropertyWindow(true)
  peer.setLayout(areaLayout)
  val xMidTab: XTab = areaLayout.addXTab
  val xLeftHalf: XTab = areaLayout.addXTab
  val xRightHalf: XTab = areaLayout.addXTab
  val y1: YTab = areaLayout.addYTab
  val y2: YTab = areaLayout.addYTab
  val y3: YTab = areaLayout.addYTab
  val y4: YTab = areaLayout.addYTab
  val y5: YTab = areaLayout.addYTab
  val y6: YTab = areaLayout.addYTab
  val y7: YTab = areaLayout.addYTab
  areaLayout.addYTab
  areaLayout.addYTab
  areaLayout.addYTab
  areaLayout.addYTab
  areaLayout.addArea(areaLayout.getLeft, areaLayout.getTop, xLeftHalf, y1, areaLabel.peer) //the addArea method expects a JComponent, but instead gets a scala component. So use the peer
  areaLayout.addArea(xLeftHalf, areaLayout.getTop, areaLayout.getRight, y1, (areaBox.peer).asInstanceOf[JComponent])
  areaLayout.addArea(areaLayout.getLeft, y1, xLeftHalf, y2, controlLabel.peer)
  areaLayout.addArea(xLeftHalf, y1, areaLayout.getRight, y2, (controlBox.peer).asInstanceOf[JComponent])
  areaLayout.addArea(areaLayout.getLeft, y2, xLeftHalf, y3, areaRowLabel.peer)
  areaLayout.addArea(xLeftHalf, y2, areaLayout.getRight, y3, areaRowBox)
  areaLayout.addArea(areaLayout.getLeft, y3, xLeftHalf, y4, areaColumnLabel.peer)
  areaLayout.addArea(xLeftHalf, y3, areaLayout.getRight, y4, areaColumnBox)
  areaLayout.addArea(areaLayout.getLeft, y4, xLeftHalf, y5, leftLabel.peer)
  areaLayout.addArea(xLeftHalf, y4, xMidTab, y5, (leftBox.peer).asInstanceOf[JComponent])
  areaLayout.addArea(areaLayout.getLeft, y5, xLeftHalf, y6, topLabel.peer)
  areaLayout.addArea(xLeftHalf, y5, xMidTab, y6, (topBox.peer).asInstanceOf[JComponent])
  areaLayout.addArea(xMidTab, y4, xRightHalf, y5, rightLabel.peer)
  areaLayout.addArea(xRightHalf, y4, areaLayout.getRight, y5, (rightBox.peer).asInstanceOf[JComponent])
  areaLayout.addArea(xMidTab, y5, xRightHalf, y6, bottomLabel.peer)
  areaLayout.addArea(xRightHalf, y5, areaLayout.getRight, y6, (bottomBox.peer).asInstanceOf[JComponent])
  areaLayout.addArea(areaLayout.getLeft, y6, xMidTab, y7, prefWHLabel.peer)
  areaLayout.addArea(xMidTab, y6, xRightHalf, y7, prefWidthBox.peer)
  areaLayout.addArea(xRightHalf, y6, areaLayout.getRight, y7, prefHeightBox.peer)
  areaLayout.addArea(areaLayout.getLeft, y7, areaLayout.getRight, areaLayout.getBottom, areaScrollPane.peer)
  areaLayout.addConstraint(2.0, xMidTab, -1.0, areaLayout.getRight, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(4.0, xLeftHalf, -1.0, areaLayout.getRight, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(4.0, xRightHalf, -3.0, areaLayout.getRight, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(12.0, y1, -1.0, areaLayout.getBottom, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(12.0, y2, -2.0, areaLayout.getBottom, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(12.0, y3, -3.0, areaLayout.getBottom, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(12.0, y4, -4.0, areaLayout.getBottom, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(12.0, y5, -5.0, areaLayout.getBottom, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(12.0, y6, -6.0, areaLayout.getBottom, OperatorType.EQ, 0.0)
  areaLayout.addConstraint(12.0, y7, -7.0, areaLayout.getBottom, OperatorType.EQ, 0.0)

  /**
   * Update method for AreaPanel
   */
  def updateAreas {
    //Store any variables related to childAreas. This list is Variable because it has to store XTabs or YTabs and both extend Variable
    val childTabs = new ArrayList[Variable]
    // Prevents the areaBox and controlBox action listeners from
    // activating while this method is running
    updateActivated = true
    //updates the variable lists.
    aLMEditorCanvas.updateVariables
    // reset all the items presented in the combo boxes for area properties
    areaBoxList.clear
    controlBoxList.clear
    leftBoxList.clear
    bottomBoxList.clear
    rightBoxList.clear
    topBoxList.clear
    areaRowBox.removeAllItems
    areaColumnBox.removeAllItems
    // Add the names of the available controls to the list of available controls
    val areas = propertiesPanel.layout.getLayoutSpec.getAreas

    for (area <- areas) {
      val control: JComponent = area.getContent
      if (control != null) {
        // Add area names to the areaBox JComboBox
        areaBoxList += control.getName
        // Add widget content to the controlBox JComboBox
        if (control.isInstanceOf[JButton]) {
          val widget: JButton = control.asInstanceOf[JButton]
          controlBoxList += widget.getText
        } else if (control.isInstanceOf[JTextArea]) {
          val widget: JTextArea = control.asInstanceOf[JTextArea]
          controlBoxList += widget.getText
        } else if (control.isInstanceOf[JLabel]) {
          val widget: JLabel = control.asInstanceOf[JLabel]
          controlBoxList += widget.getText
        } else {
          controlBoxList += "Unable to get label"
        }
      }
      //If there is a child area store the bounding tabs in the childTabs list.
      if (area.hasChildArea) {
        childTabs.add(area.getChildArea.getLeft)
        childTabs.add(area.getChildArea.getRight)
        childTabs.add(area.getChildArea.getTop)
        childTabs.add(area.getChildArea.getBottom)
      }
    }
    // Add the new Tab option for the Tab Boxes
    leftBoxList += "New Tab"
    rightBoxList += "New Tab"
    topBoxList += "New Tab"
    bottomBoxList += "New Tab"
    // Update the tab property boxes with the current tabs
    for (v <- propertiesPanel.layout.getLayoutSpec.getVariables if !childTabs.contains(v)) { //exclude variables that define child areas.
      //if (childTabs.contains(v)) {
      //  continue
      //}
      if (v.isInstanceOf[XTab]) {
        leftBoxList += v
        rightBoxList += v
      }
      else if (v.isInstanceOf[YTab]) {
        topBoxList += v
        bottomBoxList += v
      }
    }
    // Set the values of the boxes to the corresponding properties of
    // the currently selected Area if selected Area has no content
    if (aLMEditorCanvas.selectedArea == null || aLMEditorCanvas.selectedArea.getContent == null) {
      areaBox.peer.setSelectedItem("New")
      controlBox.peer.setSelectedItem("None")
      prefWidthBox.peer.setText("0")
      prefHeightBox.peer.setText("0")
    }
    // Display the properties of the content of the selected area
    else {
      areaBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getContent.getName)
      controlBox.peer.setSelectedIndex(areaBox.peer.getSelectedIndex)
      prefWidthBox.peer.setText("" + aLMEditorCanvas.selectedArea.getContent.getWidth)
      prefHeightBox.peer.setText("" + aLMEditorCanvas.selectedArea.getContent.getHeight)
    }
    if (aLMEditorCanvas.selectedArea == null) {
      return
    }
    // Show properties of the selected Area
    leftBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getLeft)
    topBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getTop)
    rightBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getRight)
    bottomBox.peer.setSelectedItem(aLMEditorCanvas.selectedArea.getBottom)
    // Initialize the areaRow box with the available rows
    for (row <- propertiesPanel.layout.getLayoutSpec.getRows) {
      areaRowBox.addItem(row)
    }
    // Initialize the areaColumn box with the available Columns
    for (column <- propertiesPanel.layout.getLayoutSpec.getColumns) {
      areaColumnBox.addItem(column)
    }
    if (aLMEditorCanvas.selectedArea.getRow != null) areaRowBox.setSelectedItem(aLMEditorCanvas.selectedArea.getRow)
    if (aLMEditorCanvas.selectedArea.getColumn != null) areaColumnBox.setSelectedItem(aLMEditorCanvas.selectedArea.getColumn)
    updateActivated = false
  }


  /**
   * Add the component to the controls combo box.
   *
   * //@param name The name of the component.
   */
  def addControl(name: String) {
    //controlBox.addItem(name)
    controlBoxList += name
    controlBox = new ComboBox(controlBoxList)
  }


  /**
   * Create a new tab.
   *
   * The type of tab the method should return. 'X' if XTab or 'Y' if YTab.
   * @return The tab which was created.
   */
  private def createNewTab(`type`: Char): Variable = {
    val result: Int = JOptionPane.showConfirmDialog(this.peer, "Are you sure you want to create a new tab?", "New Tab?", JOptionPane.YES_NO_OPTION)
    if (result == JOptionPane.YES_OPTION) {
      if (`type` == 'X') {
        return new XTab(propertiesPanel.layout.getLayoutSpec, true)
      }
      else {
        return new YTab(propertiesPanel.layout.getLayoutSpec, true)
      }
    }
    else {
      return null
    }
  }


  /**
   * Get the invisible area insets map.
   * <p>
   * This defines the insets (if any) for components contained in {@link #invisibleControls}.
   *
   * @return The invisible area insets map.
   */
  def getInvisibleAreaInsets: HashMap[JComponent, Insets] = {
    return binAreaInsets
  }

  /**
   * Get the invisible area alignments map.
   * <p>
   * This defines the alignments for components contained in {@link #invisibleControls}.
   *
   * @return The invisible area alignments map.
   */
  def getInvisibleAlignments: HashMap[JComponent, Array[AnyRef]] = {
    return binAlignments
  }


}

