package alm.editor

import java.awt.{Polygon, BasicStroke, Stroke, Cursor, Color, Font}
import java.awt.event.{MouseListener, MouseMotionListener}
import java.awt.image.BufferedImage
import java.text.DecimalFormat

import scala.swing._
import scala.collection._
import scala.collection.JavaConversions._
import scala.swing.{Component, Dimension, Graphics2D, MenuItem, Point, Rectangle}
import scala.swing.event._

import alm._
import linsolve._
import scala.swing.event.MousePressed
import scala.swing.event.MouseDragged
import scala.swing.event.MouseClicked
import scala.swing.event.MouseReleased

/**
 * This class defines the canvas which is used for the ALM Editing functions.
 * It contains the instances of the edit operation classes related to editing operations done on the canvas
 * {@link alm.editor.operations.AddSelectedConstraintTab}
 * {@link alm.editor.operations.Inserting}
 * {@link alm.editor.operations.ModifyConstraint}
 * {@link alm.editor.operations.RemoveArea}
 * {@link alm.editor.operations.RemoveSelectedConstraint}
 * {@link alm.editor.operations.Resizing}
 * {@link alm.editor.operations.SplitAreaHorizontal}
 * {@link alm.editor.operations.SplitAreaVertical}
 * {@link alm.editor.operations.SwapAreas}
 *
 *Contains the areaPopupMenu and constraintsPopupMenu, different popupMenus on the canvas depending on the mode
 *Also contains the menuItems to switch to edit mode and show/hide insets
 *Defines the paintComponent method and various drawing methods
 *Has methods for finding area under mouse click and X/YTabs near mouse click
 *Has a method for checking for overlap
 *
 * @author Brent Whiteley, Irene
 *
 *Overall, contains methods that support the editing operations. Some methods/fields are required by several different edit operation classes.
 * @param lMLayout The ALMLayout defining the container's layout.
 *
 */
class ALMEditorCanvas(var lMLayout: ALMLayout) extends Component {
  /** The mode the editor is currently in, e.g. area editing, constraint editing, etc. */
  var editorMode = EditorMode.AreaEdit
  // Context menu parts
  /** The popupMenu for the current editing mode, i.e. for areas or for constraints. */
  var areasPopupMenu = new PopupMenu
  var constraintsPopupMenu = new PopupMenu
  // Menu Item for exiting edit mode
  def addSwitchMenuItem(): MenuItem = {
    new MenuItem(new Action("Switch to Normal Mode") {
      def apply {
        lMLayout.propertiesWindow.exit
      }
    })
  }
 //Add the menu item for exiting edit mode into both popupMenus
  areasPopupMenu.contents += addSwitchMenuItem
  constraintsPopupMenu.contents += addSwitchMenuItem
  // Menu Item for toggling the inset tab display
  def addToggleInsetTabMenuItem(): MenuItem = {
    new MenuItem(new Action("Show Inset Tabs") { //the text is initially "Show Inset tabs" since inset tabs are initially not present
      def apply {
        if (showInsetTabs) {    //If inset tabs are currently showing, then clicking the menu item will make insets disappear
          showInsetTabs = false
          title_=("Show Inset Tabs") //reassign the text to reflect that the next click of the menu item will make the insets appear again
        }
        else {
          showInsetTabs = true   //If inset tabs are NOT currently showing, then clicking the menu item will make insets show up
          title_=("Hide Inset Tabs") //reassign the text to reflect that the next click of the menu item will make the insets disappear again
        }
        ALMEditorCanvas.this.repaint
      }
    })
  }
  //Add the menu item for toggling the inset tab display
  areasPopupMenu.contents += addToggleInsetTabMenuItem
  constraintsPopupMenu.contents += addToggleInsetTabMenuItem

  // Operations that can be used in the editor (Actions) - adds the necessary reactions and actions
  // Adds a menu Item for removing a widget from the panel
  val removeArea = new operations.RemoveArea(this)
  // Adds a menu Item for splitting an area Horizontally
  val splitAreaHorizontal = new operations.SplitAreaHorizontal(this)
  // Adds a menu Item for splitting an area Vertically
  val splitAreaVertical = new operations.SplitAreaVertical(this)
  // Adds a menu Item for adding a tab to a constraint
  val addSelectedConstraintTab = new operations.AddSelectedConstraintTab(this)
  // Adds a menu Item for removing a tab from a constraint
  val removeSelectedConstraintTab = new operations.RemoveSelectedConstraintTab(this)
  val swapAreas = new operations.SwapAreas(this)
  val resize = new operations.Resizing(this)
  val modifyConstraint = new operations.ModifyConstraint(this)

  listenTo(this.mouse.clicks, this.mouse.moves)
  reactions+={
/**
 * Handles the mouse pressed events for the editor canvas.
 * Detects right click and shows the areaPopupMenu or ConstraintPopupMenu if in area or constraints mode, respectively
 */
    case e: MousePressed =>
    if (editorMode == EditorMode.AreaEdit) {
      if (e.peer.getButton == java.awt.event.MouseEvent.BUTTON3) areasPopupMenu.show(this, e.point.getX, e.point.getY)
    }
    else if (editorMode == EditorMode.ConstraintEdit) {
      selectedTab = getTabNearPoint(e.point, TOL) //This is used by operations ModifyConstraint, AddSelectedConstraintTab and removeSelectedConstraint tab; as they all require the detection of the selectedConstraint near a mousePress
      if (e.peer.getButton == java.awt.event.MouseEvent.BUTTON3) constraintsPopupMenu.show(this, e.point.getX, e.point.getY)
    }
    repaint()
    case e: MouseReleased => repaint()
    case e: MouseDragged => repaint()
    case e: MouseMoved =>
      this.revalidate()
      repaint()
  }

  //UNUSED
  private var removeRowMenuItem: MenuItem = new MenuItem("Remove Row") {
  }
  private var removeColumnMenuItem: MenuItem = new MenuItem("Remove Column") {
  }
  // variables for remembering mouse state
  var mouseBeingDragged: Boolean = false //detecting mouse drag is important to draw the grey arrow representing the constraint being modified as you drag a X/YTab for the Modify Constraint operation. Perhaps move this to modifyConstraint class with the drawSingleRatioConstraint or drawRatioConstraint methods
  var mouseDraggedLocation: Point = null //important to draw the grey arrow representing the constraint being modified as you drag a X/YTab for the Modify Constraint operation. Perhaps move this to modifyConstraint class with the drawSingleRatioConstraint or drawRatioConstraint methods
  // variables for remembering selections of layout parts
  var selectedXTab: Variable = null //used by RemoveArea,Resizing,SwapAreas operations, which all require the detection of selected XTab near mouse
  var selectedYTab: Variable = null //used by RemoveArea,Resizing,SwapAreas operations, which all require the detection of selected YTab near mouse
  var selectedTab: Variable = null //This is used by operations ModifyConstraint, AddSelectedConstraintTab and removeSelectedConstraintTab; as they all require the detection of the selectedConstraint near a mousePress
  var selectedRow: Row = null  //not used
  var selectedColumn: Column = null //not used
  var selectedConstraint: Constraint = null //used by AddSelectedConstraintTab, RemoveSelectedConstraintTab, ModifyConstraint operations as they all need to know which is the current selected constraint to check conditions when adding/removing/modifying constraints and this value is set in the ConstraintsPanel
  var selectedArea: Area = null //used by RemoveArea, Resizing, SplitAreaHorizontal, SplitAreaVertical and SwapAreas operations
  // variables for remembering parts the mouse is currently on
  private var mouseOverRow: Row = null //not used
  private var mouseOverColumn: Column = null //not used
  var mouseOverXTab: Variable = null //mainly used by resizing to detect which XTab/YTab mouse is on to swap the borders of the areas
  var mouseOverYTab: Variable = null
  var mouseOverArea: Area = null //used by removeArea, resizing and swapAreas operations
  // flags to set whether to draw certain tabs
  var showXTab: Boolean = false
  var showYTab: Boolean = false
  var showInsetTabs: Boolean = false
  // Contains all variable that are not inset tabs
  private var usableVariables: mutable.ListBuffer[Variable] = new mutable.ListBuffer[Variable]
  // Contains the inset tabs
  private var insetVariables: mutable.ListBuffer[Variable] = new mutable.ListBuffer[Variable]
  // Boolean values to determine mouse cursor shape when moving an area by its corner
  var moveNESW: Boolean = false
  var moveNWSE: Boolean = false
  private var showHorizontalArrow: Boolean = false
  private var showVerticalArrow: Boolean = false

  /**
   * Tolerance value for mouse clicks, i.e. the distance the mouse needs to be
   * to an object for selecting it by clicking.
   */
  val TOL: Int = 4
  //removed [editor] // style objects for drawing
  private var dashedStroke: Stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, Array[Float](3, 2), 0)
  private var boldStroke: Stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)
  private var boldDashedStroke: Stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, Array[Float](3, 2), 0)
  private var constraintStroke: Stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)
  private var boldFont: Font = new Font("TimesRoman", Font.BOLD, 12)

  // If no area is selected when the editor is started, select the first
  // area if one exists
  if (selectedArea == null && selectedXTab == null && selectedYTab == null && lMLayout.getLayoutSpec.getAreas.size != 0) {
    selectedArea = lMLayout.getLayoutSpec.getAreas.get(0)
    selectedRow = selectedArea.getRow
    selectedColumn = selectedArea.getColumn
  }

  //Initially set up the variable lists
  updateVariables

  /**
   * Updates the variable lists.
   */
  def updateVariables {
    usableVariables.clear()
    insetVariables.clear()
    //go through each variable in the layout spec and re-enter into the usable list
    for (v <- lMLayout.getLayoutSpec.getVariables) {
      usableVariables.+=(v)
    }
    //but then go through each area in the layout spec get the variables that belong to childAreas
    //remove the childArea variables from the usable variables list
    //put the childArea variables into the inset variables list
    for (a <- lMLayout.getLayoutSpec.getAreas) {
      if (a.hasChildArea) {
        insetVariables.+=(a.getChildArea.getLeft)
        insetVariables.+=(a.getChildArea.getRight)
        insetVariables.+=(a.getChildArea.getTop)
        insetVariables.+=(a.getChildArea.getBottom)
        usableVariables.-=(a.getChildArea.getLeft)
        usableVariables.-=(a.getChildArea.getRight)
        usableVariables.-=(a.getChildArea.getBottom)
        usableVariables.-=(a.getChildArea.getTop)
      }
    }
  }

  /**
   * Sets the specified area as the selected area.
   *
   * @param areaName
	 * The name of the area to be selected.
   */
  def selectArea(areaName: String) {
    for (area <- lMLayout.getLayoutSpec.getAreas) {
      if (area.getContent != null) {
        if (area.getContent.getName == areaName) {
          selectedArea = area
          mouseOverArea = null
          selectedXTab = null
          selectedYTab = null
          showXTab = true
          showYTab = true
          this.revalidate()
          this.repaint()
          return
        }
      }
    }
  }


  /**
   * Method to determine the cursor type when near a border of an area
   *
   * @param p
	 * point of cursor
   * @param a
	 * area to which the borders belong.
   */
  def isNearAreaBorder(p: Point, a: Area) {
    //used to be private method
    showHorizontalArrow = false
    showVerticalArrow = false
    if (p.getY >= a.getTop.getValue - TOL && p.getY <= a.getBottom.getValue + TOL) {
      if (p.getX <= a.getLeft.getValue + TOL && p.getX >= a.getLeft.getValue) showHorizontalArrow = true
      else if (p.getX <= a.getRight.getValue && p.getX >= a.getRight.getValue - TOL) showHorizontalArrow = true
    }
    // check whether the mouse is close to the top or bottom tab
    if (p.getX >= a.getLeft.getValue - TOL && p.getX <= a.getRight.getValue + TOL) { // we are between the left and right tab
      if (p.getY <= a.getTop.getValue + TOL && p.getY >= a.getTop.getValue) showVerticalArrow = true //we are just below the top tab
      else if (p.getY <= a.getBottom.getValue && p.getY >= a.getBottom.getValue - TOL) showVerticalArrow = true
    }
    if (showHorizontalArrow) peer.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR))
    if (showVerticalArrow) peer.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))
    if (showHorizontalArrow && showVerticalArrow) {
      if (p.getY >= a.getTop.getValue - TOL && p.getX >= a.getRight.getValue - TOL && p.getY < a.getBottom.getValue - TOL && p.getX >= a.getLeft.getValue + TOL || (p.getY >= a.getBottom.getValue - TOL && p.getX >= a.getLeft.getValue - TOL && p.getY > a.getTop.getValue + TOL && p.getX <= a.getRight.getValue - TOL)) {
        peer.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR))
        moveNESW = true
      }
      else {
        peer.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR))
        moveNWSE = true
      }
    }
  }

  /**
   * Find closest tabs enclosing the cursor and creates an area.
   *
   * @param point
	 * position of cursor
   * @return the created area
   */
  def findTabbedArea(point: Point): Area = {
    //used to be protected method
    // Return null if the specified point is not on the screen
    val r: Rectangle = new Rectangle
    r.setRect(0, 0, peer.getSize().getWidth, peer.getSize().getHeight)
    if (!r.contains(point)) {
      return null
    }
    // Defines edges of the new area as the panel borders.
    var xa: XTab = lMLayout.getLeft
    var xb: XTab = lMLayout.getRight
    var ya: YTab = lMLayout.getTop
    var yb: YTab = lMLayout.getBottom
    // Loops through all tabs and sets the sides of the area to the closest
    // tab seen for each side.
    for (v <- usableVariables) {
      if (v.isInstanceOf[XTab]) {
        if (point.getX > v.getValue) {
          if (point.getX - xa.getValue > point.getX - v.getValue) xa = v.asInstanceOf[XTab] //left
        }
        else {
          if (xb.getValue - point.getX > v.getValue - point.getX) xb = v.asInstanceOf[XTab] //right
        }
      }
      else if (v.isInstanceOf[YTab]) {
        if (point.getY > v.getValue) {
          if (point.getY - ya.getValue > point.getY - v.getValue) ya = v.asInstanceOf[YTab] //top
        }
        else {
          if (yb.getValue - point.getY > v.getValue - point.getY) yb = v.asInstanceOf[YTab] //bottom
        }
      }
    }
    val a: Area = lMLayout.getLayoutSpec.addArea(xa, ya, xb, yb, null, new Dimension)
    return a
  }

  /**
   * Finds the area underneath the current cursor position.
   *
   * @param point
	 * position of cursor
   * @return the area in that position, null if no area is found.
   */
  def findSelectedArea(point: Point): Area = {
    //was protected method
    // Check whether the mouse is in the layout and return null if not
    val r: Rectangle = new Rectangle
    r.setRect(0, 0, peer.getSize().getWidth, peer.getSize().getHeight)
    if (!r.contains(point)) return null
    // go through all areas and check whether the mouse is inside
    //Goes through each area in the layoutSpec and creates a rectangle using its coordinates
    //If the rectangle contains the point, then return he area
    for (a <- lMLayout.getLayoutSpec.getAreas) {
      if (new Rectangle(a.getLeft.getValue.asInstanceOf[Int], a.getTop.getValue.asInstanceOf[Int], (a.getRight.getValue - a.getLeft.getValue).asInstanceOf[Int], (a.getBottom.getValue - a.getTop.getValue).asInstanceOf[Int]).contains(point)) {
        return a
      }
    }
    return null
  }

  /**
   * Finds the row underneath the current cursor position
   *
   * @param point
	 * position of the cursor
   * @return the row in that position, null if no row is found.
   */
  protected def findSelectedRow(point: Point): Row = {
    for (row <- lMLayout.getLayoutSpec.getRows) {
      // if (this.GetYTabNearPoint(point, TOL) != null &&
      // this.GetYTabNearPoint(point, TOL).value == row.top.value)
      if (new Rectangle(0, row.getTop.getValue.asInstanceOf[Int], peer.getWidth - 1, row.getBottom.getValue.asInstanceOf[Int] - row.getTop.getValue.asInstanceOf[Int]).contains(point)) {
        return row
      }
    }
    return null
  }

  /**
   * Finds the column underneath the current cursor position
   *
   * @param point
	 * position of the cursor
   * @return the column in that position, null if no column is found.
   */
  protected def findSelectedColumn(point: Point): Column = {
    for (column <- lMLayout.getLayoutSpec.getColumns) {
      // if (this.GetYTabNearPoint(point, TOL) != null &&
      // this.GetYTabNearPoint(point, TOL).value == row.top.value)
      if (new Rectangle(column.left.getValue.asInstanceOf[Int], 0,
        column.right.getValue.asInstanceOf[Int] - column.left.getValue.asInstanceOf[Int], peer.getHeight - 1).contains(point)) {
        return column
      }
    }
    return null
  }

  /**
   * Find the closest XTab to a given point, within a given tolerance.
   * TOL: max distance from the point the XTab may be
   * @param point
	 * The point being checked
   * @return the closest XTab, null if no XTab is found within the tolerance
   *         limit
   */
  def getXTabSelected(point: Point): Variable = {
    //used to be private method
    var selectedTab: Variable = null
    var proximity: Int = Integer.MAX_VALUE
    for (v <- usableVariables) { //goes through all the xTabs and see how far it is away from the Point and always finds the closest
      if (v.isInstanceOf[XTab] && v.getValue + TOL > point.getX && point.getX > v.getValue - TOL) {
        if (Math.abs(v.getValue - point.getX) < proximity) {
          selectedTab = v
          proximity = Math.abs(v.getValue - point.getX).asInstanceOf[Int]
          return selectedTab
        }
      }
    }
    return selectedTab
  }

  /**
   *
   * Find the closest YTab to a given point, within a given tolerance.
   * TOL: max distance from the point the tab may be
   * 
   * @param point
	 * The point being checked
   * @return the closest YTab, null if no XTab is found within the tolerance
   *         limit
   */
  def getYTabSelected(point: Point): Variable = {
    //used to be private method
    var selectedTab: Variable = null
    var proximity: Int = Integer.MAX_VALUE
    for (v <- usableVariables) {
      if (v.isInstanceOf[YTab] && v.getValue + TOL > point.getY && point.getY > v.getValue - TOL) {
        if (Math.abs(v.getValue - point.getY) < proximity) {
          selectedTab = v
          proximity = Math.abs(v.getValue - point.getY).asInstanceOf[Int]
          return selectedTab
        }
      }
    }
    return selectedTab
  }

  /**
   * Finds the XTab of an area closest to a given point within a given
   * boundary.
   * TOL: max distance from the point the XTab may be
   * @param selectedArea
	 * The area being checked
   * @param point
	 * The point being checked
   * 
   * @return the XTab of the area closest to the point, null if there is no
   *         XTab within the tolerance limit
   * 
   */
  def getAreaXTabSelected(selectedArea: Area, point: Point): Variable = {
    //was private method
    // test if it is the left tabstop
    if (point.x >= selectedArea.getLeft.getValue && point.x <= selectedArea.getLeft.getValue + TOL) return selectedArea.getLeft
    // test if it is the right tabstop
    if (point.x >= selectedArea.getRight.getValue - TOL && point.x <= selectedArea.getRight.getValue) return selectedArea.getRight
    // otherwise: no tabstop
    return null
  }

  /**
   * Finds the YTab of an area closest to a given point within a given
   * boundary.
   * TOL: max distance from the point the YTab may be
   * @param selectedArea
	 * The area being checked
   * @param point
	 * The point being checked
   * @return the YTab of the area closest to the point, null if there is no
   *         XTab within the tolerance limit
   */
  def getAreaYTabSelected(selectedArea: Area, point: Point): Variable = {
    //was private method
    // test if it is the top tabstop
    if (point.y >= selectedArea.getTop.getValue && point.y <= selectedArea.getTop.getValue + TOL) return selectedArea.getTop
    // test if it is the bottom tabstop
    if (point.y >= selectedArea.getBottom.getValue - TOL && point.y <= selectedArea.getBottom.getValue) return selectedArea.getBottom
    // otherwise: no tabstop
    return null
  }

  /**
   * Finds the closest tab to a point within a given tolerance.
   *
   * @param p
	 * The point being checked
   * @param tolerance
	 * max distance from the point the tab may be
   * @return the closest tab to a point, null if there is no tab within the
   *         tolerance limit
   */
  def getTabNearPoint(p: Point, tolerance: Int): Variable = {
    var selectedTab: Variable = null
    var proximity: Int = Integer.MAX_VALUE
    for (v <- usableVariables) {
      if (v.isInstanceOf[XTab] && v.getValue + tolerance > p.getX && p.getX > v.getValue - tolerance) {
        if (Math.abs(v.getValue - p.getX) < proximity) {
          selectedTab = v
          proximity = Math.abs(v.getValue - p.getX).asInstanceOf[Int]
        }
      }
      if (v.isInstanceOf[YTab] && v.getValue + tolerance > p.getY && p.getY > v.getValue - tolerance) {
        if (Math.abs(v.getValue - p.getY) < proximity) {
          selectedTab = v
          proximity = Math.abs(v.getValue - p.getY).asInstanceOf[Int]
        }
      }
    }
    return selectedTab
  }

  /**
   * Method to paint the graphical user interface components and all editor
   * related visualisations.
   *
   * @param graphics
	 * The Graphics object.
   */
  override def paintComponent(graphics: Graphics2D) {
    // TODO super.paintComponent(graphics)
    val g: Graphics2D = graphics.asInstanceOf[Graphics2D]
    peer.setSize(peer.getParent.getSize)
    lMLayout.layoutContainer(this.peer)

    // Paint all Areas
    for (a <- lMLayout.getLayoutSpec.getAreas
         if a.getContent != null && (a.getContent.getWidth > 0 && a.getContent.getHeight > 0)) {
      val control: javax.swing.JComponent = a.getContent
      val image: BufferedImage = new BufferedImage(control.getWidth, control.getHeight, BufferedImage.TYPE_INT_RGB)
      val controlImage: Graphics2D = image.createGraphics
      controlImage.setPaint(peer.getBackground)
      controlImage.fill(peer.getBounds)
      /* TODO // Updates scroll pane to include the table header.
      if (control.isInstanceOf[JScrollPane]) {
        val c: Component = (control.asInstanceOf[JScrollPane]).getViewport.getView
        if (c.isInstanceOf[JTable]) {
          val header: JTableHeader = (c.asInstanceOf[JTable]).getTableHeader
          (control.asInstanceOf[JScrollPane]).getColumnHeader.add(header)
          header.setSize(c.peer.getWidth, header.getHeight) //c.getWidth
        }
      }
      */
      control.printAll(controlImage)
      g.drawImage(image, control.getLocation().getX.asInstanceOf[Int], control.getLocation().getY.asInstanceOf[Int], null)
    }
    // Paint the components which are area mode specific
    if (editorMode == EditorMode.AreaEdit) {
      // Paint tabs if the option is selected
      if (showXTab || showYTab) {
        for (v <- usableVariables) {
          if (showXTab && v.isInstanceOf[XTab]) {
            g.setColor(Color.BLUE)
            g.drawLine(v.getValue.asInstanceOf[Int], 0, v.getValue.asInstanceOf[Int], peer.getHeight - 1)
          }
          else if (showYTab && v.isInstanceOf[YTab]) {
            g.setColor(Color.BLUE)
            g.drawLine(0, v.getValue.asInstanceOf[Int], peer.getWidth - 1, v.getValue.asInstanceOf[Int])
          }
        }
      }
      // Paints tab lines
      for (i <- 0 until usableVariables.size) {
        val v: Variable = usableVariables.apply(i)
        if (v.isInstanceOf[XTab]) {
          DrawTabLine(g, Color.gray, v, true, true)
        }
        else if (v.isInstanceOf[YTab]) {
          DrawTabLine(g, Color.gray, v, true, false)
        }
      }
      // Paints inset tab lines if show option is selected.
      if (showInsetTabs) {
        for (i <- 0 until insetVariables.size) {
          val v: Variable = insetVariables.apply(i)
          if (v.isInstanceOf[XTab]) {
            DrawTabLine(g, Color.red, v, true, true)
          }
          else if (v.isInstanceOf[YTab]) {
            DrawTabLine(g, Color.red, v, true, false)
          }
        }
      }
      // Paint selected area
      if (selectedArea != null) {
        var left: Double = selectedArea.left.getValue
        var top: Double = selectedArea.top.getValue
        var right: Double = selectedArea.right.getValue
        var bottom: Double = selectedArea.bottom.getValue
        // Draw selectedArea selection rectangle
        g.setColor(Color.RED)
        g.drawRect(selectedArea.left.getValue.asInstanceOf[Int], selectedArea.top.getValue.asInstanceOf[Int], (selectedArea.right.getValue - selectedArea.left.getValue).asInstanceOf[Int], (selectedArea.bottom.getValue - selectedArea.top.getValue).asInstanceOf[Int])
        // Whole selectedArea is dragged
        if (selectedXTab == null && selectedYTab == null && mouseOverArea != null && mouseOverArea != selectedArea) {
          // Draw destination rectangle
          g.setColor(Color.GREEN)
          g.drawRect(mouseOverArea.left.getValue.asInstanceOf[Int], mouseOverArea.top.getValue.asInstanceOf[Int], (mouseOverArea.right.getValue - mouseOverArea.left.getValue).asInstanceOf[Int], (mouseOverArea.bottom.getValue - mouseOverArea.top.getValue).asInstanceOf[Int])
        }
        // Tab(s) of selectedArea is dragged
        else if (mouseOverXTab != null || mouseOverYTab != null) {
          // Ensure the area can be resized into this space
          var valid: Boolean = true
          // A XTab of the selectedArea is being dragged
          if (selectedXTab != null && mouseOverXTab != null) {
            if (selectedXTab eq selectedArea.left) {
              left = mouseOverXTab.getValue
              selectedArea.setLeft(mouseOverXTab.asInstanceOf[XTab])
              if (checkForOverlap(selectedArea)) {
                valid = false
              }
              selectedArea.setLeft(selectedXTab.asInstanceOf[XTab])
            }
            else {
              right = mouseOverXTab.getValue
              selectedArea.setRight(mouseOverXTab.asInstanceOf[XTab])
              if (checkForOverlap(selectedArea)) {
                valid = false
              }
              selectedArea.setRight(selectedXTab.asInstanceOf[XTab])
            }
          }
          // A YTab of the selectedArea is being dragged
          if (selectedYTab != null && mouseOverYTab != null) {
            if (selectedYTab eq selectedArea.top) {
              top = mouseOverYTab.getValue
              selectedArea.setTop(mouseOverYTab.asInstanceOf[YTab])
              if (checkForOverlap(selectedArea)) {
                valid = false
              }
              selectedArea.setTop(selectedYTab.asInstanceOf[YTab])
            }
            else {
              bottom = mouseOverYTab.getValue
              selectedArea.setBottom(mouseOverYTab.asInstanceOf[YTab])
              if (checkForOverlap(selectedArea)) {
                valid = false
              }
              selectedArea.setBottom(selectedYTab.asInstanceOf[YTab])
            }
          }
          // Draw destination rectangle
          if (valid) {
            g.setColor(Color.GREEN)
            g.drawRect(left.asInstanceOf[Int], top.asInstanceOf[Int], (right - left).asInstanceOf[Int], (bottom - top).asInstanceOf[Int])
          }
        }
      }
    }
    // Paint the components which are constrain mode specific
    else if (editorMode == EditorMode.ConstraintEdit) {
      for (i <- 0 until usableVariables.size) {
        val v: Variable = usableVariables.apply(i)
        if (selectedConstraint != null && selectedConstraint.leftSideContains(v)) {
          if (v.isInstanceOf[XTab]) {
            DrawTabLine(g, ColorChooser.getInstance.getColor(v), v, false, true)
          }
          else if (v.isInstanceOf[YTab]) {
            DrawTabLine(g, ColorChooser.getInstance.getColor(v), v, false, false)
          }
        }
        else {
          if (v.isInstanceOf[XTab]) {
            DrawTabLine(g, Color.gray, v, true, true)
          }
          else if (v.isInstanceOf[YTab]) {
            DrawTabLine(g, Color.gray, v, true, false)
          }
        }
      }
      // Paints inset tab lines if show option is selected.
      if (showInsetTabs) {
        for (i <- 0 until insetVariables.size) {
          val v: Variable = insetVariables.apply(i)
          if (v.isInstanceOf[XTab]) {
            DrawTabLine(g, Color.red, v, true, true)
          }
          else if (v.isInstanceOf[YTab]) {
            DrawTabLine(g, Color.red, v, true, false)
          }
        }
      }
      if (isFixedDistanceConstraint) {
        drawSingleVariableConstraint(g)
      }
      if (isRatioConstraint) {
        drawRatioConstraint(g)
      }
    }
    // Paint the components which are row mode specific
    else if (editorMode == EditorMode.RowEdit) {
      import scala.collection.JavaConversions._
      for (row <- lMLayout.getLayoutSpec.getRows) {
        if (row eq mouseOverRow) {
          g.setColor(Color.green)
          g.drawLine(0, mouseOverRow.top.getValue.asInstanceOf[Int], peer.getWidth, mouseOverRow.top.getValue.asInstanceOf[Int])
        }
        if (row eq selectedRow) {
          g.setColor(Color.red)
          g.drawRect(0, row.top.getValue.asInstanceOf[Int] + 2, peer.getWidth - 2, row.bottom.getValue.asInstanceOf[Int] - row.top.getValue.asInstanceOf[Int] - 5)
        }
        else {
          g.setColor(Color.blue)
          g.drawRect(0, row.top.getValue.asInstanceOf[Int] + 2, peer.getWidth - 2, row.bottom.getValue.asInstanceOf[Int] - row.top.getValue.asInstanceOf[Int] - 5)
        }
      }
    }
    // Paint the components which are column mode specific
    else if (editorMode == EditorMode.ColumnEdit) {
      import scala.collection.JavaConversions._
      for (col <- lMLayout.getLayoutSpec.getColumns) {
        if (col eq mouseOverColumn) {
          g.setColor(Color.green)
          g.drawLine(col.left.getValue.asInstanceOf[Int], 0, col.left.getValue.asInstanceOf[Int], peer.getHeight - 2)
        }
        if (col eq selectedColumn) {
          g.setColor(Color.red)
          g.drawRect(col.left.getValue.asInstanceOf[Int] + 2, 0, col.right.getValue.asInstanceOf[Int] - col.left.getValue.asInstanceOf[Int] - 5, peer.getHeight - 2)
        }
        else {
          g.setColor(Color.blue)
          g.drawRect(col.left.getValue.asInstanceOf[Int] + 2, 0, col.right.getValue.asInstanceOf[Int] - col.left.getValue.asInstanceOf[Int] - 5, peer.getHeight - 2)
        }
      }
    }
    else if (editorMode == EditorMode.WidgetEdit) {
      val oldColour: Color = g.getColor
      g.setColor(Color.BLUE)
      import scala.collection.JavaConversions._
      for (a <- lMLayout.getLayoutSpec.getAreas
           if a.getContent != null && a.getContent.getWidth > 0 || a.getContent.getHeight > 0) {
        val control: javax.swing.JComponent = a.getContent
        // if (control == null) {
        //    continue
        //  }
        // if (control.getWidth <= 0 || control.getHeight <= 0) continue
        // Display the value source information in text
        val left: Double = Math.rint(a.left.getValue)
        val bottom: Double = Math.rint(a.bottom.getValue)
        val text: String = control.getToolTipText
        // Display the ToolTip over each widget
        if (text != null) {
          val textParts: Array[String] = text.split("; ")
          val lineHeight: Int = g.getFontMetrics.getHeight
          val topLine: Int = bottom.asInstanceOf[Int] - 2 - lineHeight * (textParts.length - 1)
          for (a <- 0 to textParts.length) {
            g.drawString(textParts(a), left.asInstanceOf[Int], topLine + a * lineHeight)
          }
        }
      }
      g.setColor(oldColour) // Set the colour back
    }
  }

  /**
   * Method to draw a single variable constraint
   *
   * @param g
	 * The Graphics2D object.
   */
  private def drawSingleVariableConstraint(g: Graphics2D) {
    // Draw the visual constraint
    val leftSide: Array[Summand] = selectedConstraint.getLeftSide
    val v: Variable = leftSide(0).getVar
    val coefficient: Double = leftSide(0).getCoeff
    val rightSide: Double = selectedConstraint.getRightSide
    val fixedValue: Double = Math.abs(rightSide / coefficient)
    val prevF: Font = g.getFont
    val prevS: Stroke = g.getStroke
    g.setColor(Color.red)
    g.setFont(boldFont)
    // Draw appropriate arrow
    if (v.isInstanceOf[XTab]) {
      g.drawString("" + fixedValue, (v.getValue / 2).asInstanceOf[Int], peer.getHeight / 2 - 10)
      drawArrow(g, new Point(0, peer.getHeight / 2), new Point(v.getValue.asInstanceOf[Int], peer.getHeight / 2))
    }
    else if (v.isInstanceOf[YTab]) {
      g.drawString("" + fixedValue, peer.getWidth / 2 + 10, (v.getValue / 2).asInstanceOf[Int])
      drawArrow(g, new Point(peer.getWidth / 2, 0), new Point(peer.getWidth / 2, v.getValue.asInstanceOf[Int]))
    }
    // If being tab is being dragged, draw new constraint
    if (mouseBeingDragged && selectedConstraint.leftSideContains(selectedTab)) {
      g.setColor(Color.gray)
      if (v.isInstanceOf[XTab]) {
        if (mouseDraggedLocation.x >= 0 && mouseDraggedLocation.x <= peer.getWidth) {
          val arrowStart: Point = new Point(0, peer.getHeight / 2 + 30)
          val arrowEnd: Point = new Point(mouseDraggedLocation.x.asInstanceOf[Int], peer.getHeight / 2 + 30)
          g.drawString("" + mouseDraggedLocation.x, mouseDraggedLocation.x / 2, peer.getHeight / 2 + 20)
          drawArrow(g, arrowStart, arrowEnd)
          g.setStroke(boldStroke)
          g.drawLine(mouseDraggedLocation.x, 0, mouseDraggedLocation.x, peer.getHeight)
        }
      }
      else if (v.isInstanceOf[YTab]) {
        if (mouseDraggedLocation.y >= 0 && mouseDraggedLocation.y <= peer.getHeight) {
          val arrowStart: Point = new Point(peer.getWidth / 2 - 30, 0)
          val arrowEnd: Point = new Point(peer.getWidth / 2 - 30, mouseDraggedLocation.y.asInstanceOf[Int])
          g.drawString("" + mouseDraggedLocation.y, peer.getWidth / 2 - 20, mouseDraggedLocation.y / 2)
          drawArrow(g, arrowStart, arrowEnd)
          g.setStroke(boldStroke)
          g.drawLine(0, mouseDraggedLocation.y, peer.getWidth, mouseDraggedLocation.y)
        }
      }
    }
    g.setFont(prevF)
    g.setStroke(prevS)
  }

  /**
   * Method to draw a ratio constraint
   *
   * @param g
	 * The Graphics2D object.
   */
  private def drawRatioConstraint(g: Graphics2D) {
    val negativeSummands: Array[Summand] = selectedConstraint.getLeftSideWithNegativeCoefficients
    assert((negativeSummands.length == 0))
    val middleSummand: Summand = negativeSummands(0)
    val middleLinePos: Double = middleSummand.getVar.getValue
    val positiveSummands: Array[Summand] = selectedConstraint.getLeftSideWithPositiveCoefficients
    assert((positiveSummands.length == 2))
    var leftLineSum: Summand = positiveSummands(0)
    var rightLineSum: Summand = positiveSummands(1)
    if (leftLineSum.getVar.getValue > rightLineSum.getVar.getValue) {
      leftLineSum = positiveSummands(1)
      rightLineSum = positiveSummands(0)
    }
    val leftLinePos: Double = leftLineSum.getVar.getValue
    val rightLinePos: Double = rightLineSum.getVar.getValue
    val leftLineCoeff: Double = leftLineSum.getCoeff
    val rightLineCoeff: Double = rightLineSum.getCoeff
    val isHorizontal: Boolean = middleSummand.getVar.isInstanceOf[XTab]
    val prevF: Font = g.getFont
    val prevS: Stroke = g.getStroke
    g.setColor(Color.red)
    g.setFont(boldFont)
    val twoDForm: DecimalFormat = new DecimalFormat("#.##")
    val rightSideText: String = twoDForm.format(rightLineCoeff * 100.0) + "%"
    val leftSideText: String = twoDForm.format(leftLineCoeff * 100.0) + "%"
    if (isHorizontal) {
      g.drawString(rightSideText, ((leftLinePos + middleLinePos) / 2).asInstanceOf[Int] - 15, peer.getHeight / 2 - 20)
      g.drawString(leftSideText, ((rightLinePos + middleLinePos) / 2).asInstanceOf[Int] - 15, peer.getHeight / 2 - 20)
      drawArrow(g, new Point(leftLinePos.asInstanceOf[Int], peer.getHeight / 2), new Point(middleLinePos.asInstanceOf[Int], peer.getHeight / 2))
      drawArrow(g, new Point(middleLinePos.asInstanceOf[Int], peer.getHeight / 2), new Point(rightLinePos.asInstanceOf[Int], peer.getHeight / 2))
    }
    else {
      g.drawString(rightSideText, peer.getWidth / 2 + 10, ((leftLinePos + middleLinePos) / 2).asInstanceOf[Int])
      g.drawString(leftSideText, peer.getWidth / 2 + 10, ((rightLinePos + middleLinePos) / 2).asInstanceOf[Int])
      drawArrow(g, new Point(peer.getWidth / 2, leftLinePos.asInstanceOf[Int]), new Point(peer.getWidth / 2, middleLinePos.asInstanceOf[Int]))
      drawArrow(g, new Point(peer.getWidth / 2, middleLinePos.asInstanceOf[Int]), new Point(peer.getWidth / 2, rightLinePos.asInstanceOf[Int]))
    }
    // If being tab is being dragged, draw new constraint
    val middleVar: Variable = middleSummand.getVar
    var leftPercentage: Double = .0
    var rightPercentage: Double = .0
    if (mouseBeingDragged && selectedConstraint.leftSideContains(middleVar) && selectedTab == middleVar) {
      g.setColor(Color.gray)
      if (middleVar.isInstanceOf[XTab]) {
        leftPercentage = (mouseDraggedLocation.x - leftLinePos) / (rightLinePos - leftLinePos)
        rightPercentage = 1 - leftPercentage
        if (leftPercentage >= 0 && leftPercentage <= 1) {
          val rightPercentageText: String = twoDForm.format(rightPercentage * 100.0) + "%"
          val leftPercentageText: String = twoDForm.format(leftPercentage * 100.0) + "%"
          g.drawString(leftPercentageText, ((leftLinePos + mouseDraggedLocation.x) / 2).asInstanceOf[Int] - 15, peer.getHeight / 2 + 20)
          g.drawString(rightPercentageText, ((rightLinePos + mouseDraggedLocation.x) / 2).asInstanceOf[Int] - 15, peer.getHeight / 2 + 20)
          drawArrow(g, new Point(leftLinePos.asInstanceOf[Int], peer.getHeight / 2 + 30), new Point(mouseDraggedLocation.x, peer.getHeight / 2 + 30))
          drawArrow(g, new Point(mouseDraggedLocation.x, peer.getHeight / 2 + 30), new Point(rightLinePos.asInstanceOf[Int], peer.getHeight / 2 + 30))
          g.setStroke(boldStroke)
          g.drawLine(mouseDraggedLocation.x, 0, mouseDraggedLocation.x, peer.getHeight)
        }
      }
      else if (middleVar.isInstanceOf[YTab]) {
        leftPercentage = (mouseDraggedLocation.y - leftLinePos) / (rightLinePos - leftLinePos)
        rightPercentage = 1 - leftPercentage
        if (leftPercentage >= 0 && leftPercentage <= 1) {
          val rightPercentageText: String = twoDForm.format(rightPercentage * 100.0) + "%"
          val leftPercentageText: String = twoDForm.format(leftPercentage * 100.0) + "%"
          g.drawString(leftPercentageText, peer.getWidth / 2 - 30, ((leftLinePos + mouseDraggedLocation.y) / 2).asInstanceOf[Int])
          g.drawString(rightPercentageText, peer.getWidth / 2 - 30, ((rightLinePos + mouseDraggedLocation.y) / 2).asInstanceOf[Int])
          drawArrow(g, new Point(peer.getWidth / 2 - 40, leftLinePos.asInstanceOf[Int]), new Point(peer.getWidth / 2 - 40, mouseDraggedLocation.y.asInstanceOf[Int]))
          drawArrow(g, new Point(peer.getWidth / 2 - 40, mouseDraggedLocation.y.asInstanceOf[Int]), new Point(peer.getWidth / 2 - 40, rightLinePos.asInstanceOf[Int]))
          g.setStroke(boldStroke)
          g.drawLine(0, mouseDraggedLocation.y, peer.getWidth, mouseDraggedLocation.y)
        }
      }
    }
    g.setFont(prevF)
    g.setStroke(prevS)
  }

  /**
   * Method to draw a tab line
   *
   * @param g
	 * The Graphics2D object.
   * @param c
	 * The colour of the line to be drawn
   * @param var
	 * The tab to be drawn
   * @param dashed
	 * Boolean indicating if the line is dashed.
   * @param isVertical
	 * Boolean indicating if the line is vertical.
   */
  private def DrawTabLine(g: Graphics2D, c: Color, `var`: Variable, dashed: Boolean, isVertical: Boolean) {
    g.setColor(c)
    val curStroke: Stroke = g.getStroke
    if (selectedTab eq `var`) {
      if (dashed) g.setStroke(boldDashedStroke)
      else g.setStroke(boldStroke)
    }
    else {
      if (dashed) g.setStroke(dashedStroke)
      else g.setStroke(constraintStroke)
    }
    if (isVertical) g.drawLine(`var`.getValue.asInstanceOf[Int], 0, `var`.getValue.asInstanceOf[Int], peer.getHeight - 1)
    else g.drawLine(0, `var`.getValue.asInstanceOf[Int], peer.getWidth - 1, `var`.getValue.asInstanceOf[Int])
    g.setStroke(curStroke)
  }

  /**
   * Method to draw an arrow.
   *
   * @param g
	 * The Graphics2D object.
   * @param arrowHead
	 * The position of the arrow head
   * @param arrowTail
	 * The position of the arrow tail
   */
  private def drawArrow(g: Graphics2D, arrowHead: Point, arrowTail: Point) {
    val length: Int = arrowHead.distance(arrowTail).asInstanceOf[Int]
    if (length < 20) {
      val previousStroke: Stroke = g.getStroke
      g.setStroke(boldStroke)
      g.drawLine(arrowHead.x, arrowHead.y, arrowTail.x, arrowTail.y)
      g.setStroke(previousStroke)
    }
    else {
      val x1: Int = arrowTail.x
      val y1: Int = arrowTail.y
      val x2: Int = arrowHead.x
      val y2: Int = arrowHead.y
      val ahb: Int = 5 // arrowHeadBuffer - gap distance before arrow starts
      val atb: Int = 5 // arrowTailBuffer - gap distance before arrow ends
      val bladeWidth: Int = 5
      val lineWidth: Int = 2
      // Defines an arrow head and body as a triangle and box on the x
      // axis
      val head1X: Array[Int] = Array(bladeWidth + ahb, ahb, bladeWidth + ahb) //changed these to Array[Int]
      val head1Y: Array[Int] = Array(-bladeWidth, 0, bladeWidth)
      val bodyX: Array[Int] = Array(bladeWidth + ahb, length - bladeWidth - atb, length - bladeWidth - atb, bladeWidth + ahb)
      val bodyY: Array[Int] = Array(lineWidth, lineWidth, -lineWidth, -lineWidth)
      val head2X: Array[Int] = Array(length - bladeWidth - atb, length - atb, length - bladeWidth - atb)
      val head2Y: Array[Int] = Array(-bladeWidth, 0, bladeWidth)
      // Translates and rotates the arrow into the correct position
      g.translate(x2, y2)
      var angle: Double = .0
      if ((x2 - x1) == 0) {
        angle = Math.PI / 2
      }
      else {
        angle = Math.atan((y2 - y1).asInstanceOf[Double] / (x2 - x1).asInstanceOf[Double])
      }
      g.rotate(angle)
      // Draws the polygon
      g.fillPolygon(new Polygon(head1X, head1Y, head1X.length))
      g.fillPolygon(new Polygon(bodyX, bodyY, bodyX.length))
      g.fillPolygon(new Polygon(head2X, head2Y, head2X.length))
      // Reverts the translation
      g.rotate(-angle)
      g.translate(-x2, -y2)
    }
  }

  /**
   * Method to remove a component from the layout. Does not add it to the bin.
   *
   * @param target
	 * The name of the component to be removed.
   */
  def removeContent(target: String) {
    var c: javax.swing.JComponent = null
    var changedArea: Area = null
    import scala.collection.JavaConversions._

    if (changedArea != null && c != null) {
      changedArea.remove
      lMLayout.getLayoutSpec.solve
      lMLayout.getParent.remove(c)
      lMLayout.savedControls.remove(c)
      peer.removeAll
      this.repaint()
    } //Moved this one up

    for (a <- lMLayout.getAreas) {
      if (a.getContent.getName == target) {
        c = a.getContent
        changedArea = a
        return //todo: break is not supported //changed break to return
      }
    }

  }


  /**
   * Method to set the current editing mode.
   *
   * //@param mode1
   * The mode to change to.
   */
  def setMode(modeVariable: EditorMode) {
    this.editorMode = modeVariable
    if (modeVariable == EditorMode.ConstraintEdit) {
      mouseBeingDragged = false
      mouseDraggedLocation = new Point(0, 0)
    }
    this.repaint()
  }

  /**
   * Gets the current mode.
   *
   * @return The current mode.
   */
  def getMode: EditorMode = {
    return editorMode
  }

  /**
   * Checks if the selected constraint is a fixed constraint. I.e. Modifier *
   * Variable = position
   *
   * @return Boolean indicating if it is a fixed constraint.
   */
  def isFixedDistanceConstraint: Boolean = {
    if (selectedConstraint != null) {
      val leftSide: Array[Summand] = selectedConstraint.getLeftSide
      // Single variable means fixed from edge, draw visual constraint to match
      if (leftSide.length == 1 && selectedConstraint.getOp == OperatorType.EQ) {
        return true
      }
    }
    return false
  }

  /**
   * Checks if the selected constraint is a ratio constraint. [Not Yet
   * Implemented]
   *
   * @return Boolean indicating if it is a ratio constraint. Currently always
   *         false because method is NYI.
   */
   def isRatioConstraint: Boolean = {
    if (selectedConstraint != null) {
    }
    return false
  }
  /**
   * Get the usable variable list. Usable variables are the ones that are not
   * part of defining area insets.
   *
   * @return the usable variables list
   */
  def getUsableVariables: mutable.ListBuffer[Variable] = {
    return usableVariables
  }


  /**
   * Check to see if the area overlaps any editor area.
   *
   * @param area
	 * The area being checked
   * @return True if there is an overlap, false otherwise.
   */
  def checkForOverlap(area: Area): Boolean = {
    //used to be private method
    import scala.collection.JavaConversions._
    for (other <- lMLayout.getLayoutSpec.getAreas if area != other && other.content != null) {
      // Itself and areas with null contrant are not checked.
      // if (area ==editor || editor.content == null) continue
      var XOverlap: Boolean = false
      // Does the space between area.left and area.right contain the left
      // or right tabs of the other area, or one of the left/right tabs
      // match.
      if (other.getLeft.getValue >= area.getLeft.getValue && other.getLeft.getValue < area.getRight.getValue) {
        XOverlap = true
      }
      else if (other.getRight.getValue > area.getLeft.getValue && other.getRight.getValue <= area.getRight.getValue) {
        XOverlap = true
      }
      // Does the space between other.left and other.right contain
      // both area XTabs, or one or both tabs are the same.
      else if (other.getLeft.getValue <= area.getLeft.getValue && other.getRight.getValue >= area.getRight.getValue) {
        XOverlap = true
      }
      var YOverlap: Boolean = false
      // Does the space between area.top and area.bottom contain the top
      // or bottom tabs of the other area, or one of the top/bottom tabs
      // match.
      if (other.getTop.getValue >= area.getTop.getValue && other.getTop.getValue < area.getBottom.getValue) {
        YOverlap = true
      }
      else if (other.getBottom.getValue > area.getTop.getValue && other.getBottom.getValue <= area.getBottom.getValue) {
        YOverlap = true
      }
      // Does the space between other.top and other.bottom contain
      // both area YTabs, or one or both tabs are the same.
      else if (other.getTop.getValue <= area.getTop.getValue && other.getBottom.getValue >= area.getBottom.getValue) {
        YOverlap = true
      }
      // If the area overlaps in both the X and Y direction it overlaps in
      // 2D, method returns true.
      if (XOverlap && YOverlap) {
        return true
      }
    }
    return false
  }
}