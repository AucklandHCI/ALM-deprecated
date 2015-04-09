package alm.editor.operations

import alm.editor.{ComponentInBin, Palette}
import scala.swing.event.{MouseReleased, MousePressed}
import scala.swing._
import scala.util.control.Breaks
import scala.swing.event.MousePressed
import scala.swing.event.MouseReleased
import java.awt.{MouseInfo, Point}
import scala.swing.Point
import alm.Area
import scala.swing.SequentialContainer.Wrapper
import javax.swing.JPopupMenu
import scala.collection.JavaConversions._
import alm.editor.PopupMenu
/**
 * This class defines the inserting editing operation
 * It contains a reference to the Palette {@link alm.editor.Palette} because the class registers reactions for the Palette
 * It registers MousePressed/Released reactions for the Palette to perform the operation
 *
 * @param palette the Palette class added on top of AreaPanel in the containment hierarchy
 *
 */
class Inserting (palette: Palette){
  //Global variable storing the bin item the mouse currently selects
  var componentInBinClickedOn:ComponentInBin = null
  //This menuItem belongs to the popupMenu that appears when right-clicking on the Palette
  //clicking on an item in the bin and then clicking this menu item will cause that item to be removed from the bin and restored into its original area
  var returnToArea: MenuItem = null
  palette.reactions+={
    /** Adds more responses to mouse pressed events for the Palette. If in Area Edit Mode, define a popupMenu with a menuItem which restores a bin item to its original area if clicked and also find the component in the Palette that the mouse clicked on*/
    case e: MousePressed =>
      //Reminder of what happened with removing:
      //When a component is removed, the content of that area is set to null
      //A new instance of ComponentInBin class is created: the insets, content and component is stored in there
      //This instance is added to the list of ComponentInBin in the Palette class
      //Now when mouse is pressed in Palette:
      //Finds the mouse press location in the Palette and finds the component underneath the mouse
      //This goes through all bin items and checks for the one that the mouse clicked on
      for (componentInBin:ComponentInBin <- palette.ComponentInBinList) {
          if (componentInBin.CheckForClick(e.point)) {
          componentInBinClickedOn = componentInBin
        }
      }
      //popupMenu that appears when right-clicking on the Palette
      var pop = new PopupMenu
      returnToArea = new MenuItem(new Action("Return to original area") {
        def apply {
          val loop = new Breaks;
          loop.breakable {
            for (component:ComponentInBin <- palette.ComponentInBinList) {
              if (component.CheckForClick(e.point)) { //when the selected bin item is found (returns a JComponent), find the area in the LayoutSpec which contains the JComponent
                palette.areaPanel.propertiesPanel.aLMEditorCanvas.lMLayout.getLayoutSpec.findAndAdd(componentInBinClickedOn)
                palette.itemsInBin.remove(component.component) //remove the JComponent from the arrayList of JComponents in the bin
                palette.ComponentInBinList.remove(component) //remove the ComponentInBin from the arrayList of ComponentInBin in the bin
                loop.break
              }
            }
          } //end breakable
          //update to show changes in bin and ALMEditorCanvas
          palette.areaPanel.propertiesPanel.aLMEditorCanvas.lMLayout.getLayoutSpec.solve
          palette.areaPanel.propertiesPanel.aLMEditorCanvas.lMLayout.layout( palette.areaPanel.propertiesPanel.aLMEditorCanvas.peer)
          palette.areaPanel.propertiesPanel.update
          palette.areaPanel.propertiesPanel.repaint()
          palette.areaPanel.propertiesPanel.aLMEditorCanvas.repaint()
        }
      })
      palette.repaint
      pop.contents+= returnToArea //add the menu item to the popupMenu and show in the correct position when right-click happens
      if (e.peer.getButton == java.awt.event.MouseEvent.BUTTON3) pop.show(palette, e.point.getX, e.point.getY)

    /** Adds more responses to mouse released events for the Palette. If in Area Edit Mode, either inserts the selected bin item into an empty area or into an area already occupied in which case the component in the bin and component in the canvas gets swapped*/
    case e: MouseReleased => //when mouse is released after being pressed in Palette, you can detect where the release occurs in the ALMEditorCanvas since the timing of the release is the same
      val point: Point = new Point
      //gets the location of the point on the ALMEditorCanvas.
      //TODO: The line below causes a nullpointer exception to occur when the mouse is released outside of the ALMEditorCanvas.. so if you just click on the Palette, it will throw a null pointer.
      point.setLocation(MouseInfo.getPointerInfo.getLocation.getX - palette.areaPanel.propertiesPanel.aLMEditorCanvas.peer.getLocationOnScreen.getX, MouseInfo.getPointerInfo.getLocation.getY - palette.areaPanel.propertiesPanel.aLMEditorCanvas.peer.getLocationOnScreen.getY)
      //selectedAreaOnRelease is the area in the editor canvas which the mouse drags and releases over
      //It is either null, in this case the item in the bin just gets inserted in the area
      //OR there is already a component occupying the area, so the item in the bin replaces the component within the selected area
      val selectedAreaOnRelease: Area = palette.areaPanel.propertiesPanel.aLMEditorCanvas.findSelectedArea(point) //couldn't use mouseOverArea because if you click anything in bin, it will think the release was on the left-most component in the testEditor1 window

      val loop = new Breaks;
      loop.breakable {
      //Go through all the areas in layout spec and find the one corresponding to selectedAreaOnRelease (called areaInLayoutSpec)
        for (areaInLayoutSpec <- palette.areaPanel.propertiesPanel.layout.getLayoutSpec.getAreas) {
          if (areaInLayoutSpec eq selectedAreaOnRelease) {
            //if there is already a component occupying the area
            if(selectedAreaOnRelease.getContent() !=null && componentInBinClickedOn.component!=null){
              //SWAP STEP 1//
              //Create and update the newComponentInBin local variable width, height, area, component, insets to reflect the new item in bin
              var newComponentInBin = new ComponentInBin(selectedAreaOnRelease.getContent(), palette)
              newComponentInBin.area = (selectedAreaOnRelease)
              newComponentInBin.insets = (selectedAreaOnRelease.getInsets)
              newComponentInBin.horizontalAlignment = (selectedAreaOnRelease.getHorizontalAlignment)
              newComponentInBin.verticalAlignment = (selectedAreaOnRelease.getVerticalAlignment)
             // newComponentInBin.childArea = (selectedAreaOnRelease.childArea)
              //ADD the new bin item (newComponentInBin) to the list storing bin items in the Palette
              palette.ComponentInBinList.add(newComponentInBin)
              palette.itemsInBin.add(selectedAreaOnRelease.getContent())
              //SWAP STEP 2//
              //set the areaInlayoutSpec content to the one selected in bin (imageFromBin.component) and update its insets,horizontal/vertical alignment to reflect what the bin item had
              selectedAreaOnRelease.setContent(componentInBinClickedOn.component)
              selectedAreaOnRelease.setInsets(componentInBinClickedOn.insets)
              selectedAreaOnRelease.setHorizontalAlignment(componentInBinClickedOn.horizontalAlignment)
              selectedAreaOnRelease.setVerticalAlignment(componentInBinClickedOn.verticalAlignment)
             //areaInLayoutSpec.setChildArea(componentInBinClickedOn.childArea)
              //SWAP STEP 3 REMOVE the new bin item (newComponentInBin) from the list storing bin items in the Palette
              palette.ComponentInBinList.remove(componentInBinClickedOn)
              palette.itemsInBin.remove(componentInBinClickedOn.component)
              loop.break
            } //end second if
            //if there isn't already a component occupying the area
            if(selectedAreaOnRelease.getContent() ==null && componentInBinClickedOn.component!=null){
              //DO THE EQUIVALENT OF SWAP STEP 2//
              //set the areaInlayoutSpec content to the one selected in bin (imageFromBin.component) and update its insets,horizontal/vertical alignment to reflect what the bin item had
              selectedAreaOnRelease.setContent(componentInBinClickedOn.component)
              selectedAreaOnRelease.setInsets(componentInBinClickedOn.insets)
              selectedAreaOnRelease.setHorizontalAlignment(componentInBinClickedOn.horizontalAlignment)
              selectedAreaOnRelease.setVerticalAlignment(componentInBinClickedOn.verticalAlignment)
              //areaInLayoutSpec.setChildArea(componentInBinClickedOn.childArea)
              //REMOVE the new bin item (newComponentInBin) from the list storing bin items in the Palette
              palette.ComponentInBinList.remove(componentInBinClickedOn) //this never got removed?
              palette.itemsInBin.remove(componentInBinClickedOn.component)
              loop.break
            } //end second if
          }//end first if
        } //end for
      } //end breakable
      //update to show changes in bin and ALMEditorCanvas
      palette.areaPanel.propertiesPanel.aLMEditorCanvas.lMLayout.getLayoutSpec.solve
      palette.areaPanel.propertiesPanel.aLMEditorCanvas.lMLayout.layout(palette.areaPanel.propertiesPanel.aLMEditorCanvas.peer)
      palette.areaPanel.propertiesPanel.aLMEditorCanvas.updateVariables //Added this
      palette.areaPanel.propertiesPanel.update
      palette.areaPanel.propertiesPanel.repaint()
      palette.areaPanel.propertiesPanel.aLMEditorCanvas.repaint()
      palette.repaint
  }


}
