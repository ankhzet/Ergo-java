package org.ankhzet.ergo.xgui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public class CommonControl {

  protected boolean overed = false,
  clicked = false;
  protected int x, y, w, h;
  protected XAction action = null;
  private XActionListener l = null;
  public boolean visible = true;
  public boolean toggled = false;

  CommonControl(int x, int y, int w, int h) {
    move(x, y, w, h);
    setAction(null);
  }

  final public void move(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  public boolean mouseEvent(MouseEvent e, boolean process) {
    if (!visible)
      return false;

    overed = ptInRect(e.getX(), e.getY());
    if (!process) {
      clicked = false;
      return overed;
    }

    if (!isEnabled())
      return false;

    if (e.getButton() != MouseEvent.BUTTON1)
      return false;


    switch (e.getID()) {
    case MouseEvent.MOUSE_RELEASED:
      if (clicked && overed)
        doClick();
      clicked = false;
      break;
    case MouseEvent.MOUSE_PRESSED:
      clicked = overed;
      break;
    }
    return clicked;
  }

  public void doClick() {
    try {
      if (l != null)
        l.actionPerformed(action);
    } catch (Exception e) {
    }
  }

  public void Process() {
  }

  public void DoDraw(Graphics2D g) {
  }

  public void Draw(Graphics2D g) {
    if (visible)
      DoDraw(g);
  }

  protected boolean ptInRect(int px, int py) {
    return ((px >= x) && (py >= y) && (px < x + w) && (py < y + h));
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return visible;
  }

  public boolean isEnabled() {
    return action.isEnabled();
  }

  public final void setAction(XAction action) {
    if (action == null)
      action = new XAction("", null);

    this.action = action;
  }

  public void setActionListener(XActionListener listener) {
    l = listener;
  }
}
