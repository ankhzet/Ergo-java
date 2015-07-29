package org.ankhzet.ergo.ui.xgui.filepicker;

import java.awt.Rectangle;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T> type of underlying node object
 */
public class PickedNode<T> {

  public Rectangle r;
  public T node;

  public PickedNode(Rectangle r, T node) {
    this.r = r;
    this.node = node;
  }

}
