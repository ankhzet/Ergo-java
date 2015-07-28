package org.ankhzet.ergo.ui;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIContainerListener extends JFrame {

  @DependencyInjection
  UILogic ui;

  public UIContainerListener() {
    super("Ergo reader");
    enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
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

  @Override
  protected void processKeyEvent(KeyEvent e) {
    ui.keyEvent(e);
  }
  
}
