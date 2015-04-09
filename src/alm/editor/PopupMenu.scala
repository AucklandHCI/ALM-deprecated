package alm.editor

import scala.swing._
import javax.swing.JPopupMenu

/**
 * Self-made wrapper for JPopupMenu since PopupMenu no longer exists in scala 2.10.3
 *
 */
class PopupMenu extends Component with SequentialContainer.Wrapper {
  override lazy val peer: JPopupMenu = new JPopupMenu with SuperMixin

  /**
   * Method to show the popupMenu in a particular scala.swing.Component at the x and y positions specified
   *
   * @param component the component the popupMenu will show up in
   * @param xPos the x position
   * @param yPos the y position
   */
  def show(component: Component, xPos: Double, yPos: Double): Unit = peer.show(component.peer, xPos.asInstanceOf[Int], yPos.asInstanceOf[Int])
  /* Create any other peer methods here */
}