package org.ankhzet.ergo;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
class UIContainerListener extends JFrame {

  UILogic ui = null;

  UIContainerListener(UILogic ui) {
    super("Ergo reader");
    this.ui = ui;
    UILogic.container = this;
    enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
//    setUndecorated(true);
  }

  @Override
  public void paint(Graphics g) {
    ui.paint(g);
  }

  @Override
  public void processComponentEvent(ComponentEvent e) {
    super.processComponentEvent(e);
    switch (e.getID()) {
    case ComponentEvent.COMPONENT_RESIZED:
      Insets i = this.getInsets();
      int w = getWidth();
      int h = getHeight();
      ui.resize(i.left, i.top, w - (i.left + i.right), h - (i.top + i.bottom));
    }
  }

  @Override
  public void processMouseEvent(MouseEvent e) {
    ui.mouseEvent(e);
  }

  @Override
  public void processMouseMotionEvent(MouseEvent e) {
    ui.mouseEvent(e);
  }
}
