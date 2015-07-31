package org.ankhzet.ergo.ui.xgui;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.ui.Skin;
import org.ankhzet.ergo.ui.UILogic;

class CtlMap extends HashMap<Integer, CommonControl> {
};

public class XControls extends ArrayList<CommonControl> {

  public static final String kBackAction = "back";
  static final String kBackLabel = "Back";
  public static final int AREA_LEFT = 1; //
  public static final int AREA_RIGHT = 2;
  static final int SIDE_MARGIN = 3; //
  static final int TOP_MARGIN = 2; //
  static final int BTN_SPACING = 5;

  CommonControl focused = null;
  CtlMap left = new CtlMap();
  CtlMap right = new CtlMap();

  public void Draw(Graphics2D g, int x, int y, int cw, int ch) {
    ch--;
    g.setColor(Skin.get().UI_PANEL);
    g.fillRect(0, 0, cw, ch - 1);

    synchronized (this) {
      for (CommonControl c : this)
        if (c.isVisible())
          c.Draw(g);
    }

    Skin.drawBevel(g, x, y - 5, cw, ch + 5);
  }

  public void Process() {
    synchronized (this) {
      for (CommonControl c : this)
        if (c.isVisible())
          c.Process();
    }
  }

  public boolean mouseEvent(MouseEvent e) {
    focused = null;
    synchronized (this) {
      this.stream().filter(c
        -> (c.isVisible() && c.mouseEvent(e, focused == null) && focused == null)
      ).forEach((c) -> {
        focused = c;
      });
    }

    Process();
    if (focused == null)
      IoC.<UILogic>get(UILogic.class).tooltip(null, 0, 0);
    return focused != null;
  }

  public void unfocus() {
    focused = null;
  }

  public CommonControl putControl(CommonControl c, int area) {
    synchronized (this) {
      add(c);
      CtlMap map = (area == AREA_LEFT) ? left : right;
      map.put(map.size(), c);
      return c;
    }
  }

  public CommonControl getControl(XAction action) {
    synchronized (this) {
      for (CommonControl c : this)
        if (c.action == action)
          return c;
    }

    return null;
  }

  public CommonControl putAtLeft(CommonControl c) {
    return putControl(c, AREA_LEFT);
  }

  public CommonControl putAtRight(CommonControl c) {
    return putControl(c, AREA_RIGHT);
  }

  public XAction putActionAtLeft(String caption, XAction action) {
    return putAction(caption, AREA_LEFT, action);
  }

  public XAction putActionAtRight(String caption, XAction action) {
    return putAction(caption, AREA_RIGHT, action);
  }

  public XAction putAction(String caption, int area, XAction action) {
    XActionButton button = (XActionButton) putControl(new XActionButton(action, caption), area);
    return button.action;
  }

  public XAction putBackAction(int area, XAction action) {
    if (action == null)
      action = new XAction(kBackAction, null);
    return putAction(kBackLabel, area, action);
  }

  public XAction putSpacer(int area) {
    XAction xAction = new XAction("", null);
    putControl(new XButton(xAction, null, "xspacer"), area);
    return xAction;
  }

  public XAction putMover(int area) {
    XAction xAction = new XAction("", null);
    putControl(new XButton(xAction, null, "xmover"), area);
    return xAction;
  }

  public void pack(int cw, int ch) {
    synchronized (this) {
      packArea(left, SIDE_MARGIN, (c, dx) -> {
        c.x = dx;
        c.y = TOP_MARGIN;
        return dx + (c.w + BTN_SPACING);
      });
      packArea(right, cw - SIDE_MARGIN, (c, dx) -> {
        c.x = dx - c.w;
        c.y = TOP_MARGIN;
        return dx - (c.w + BTN_SPACING);
      });
    }
  }

  void packArea(CtlMap controls, int dx, CtlEnum enumerate) {
    ArrayList<Integer> l = new ArrayList<>(controls.keySet());
    Collections.sort(l);
    for (Integer idx : l) {
      CommonControl control = controls.get(idx);
      dx = enumerate.withCtl(control, dx);
    }
  }

  public void keyEvent(KeyEvent e) {
    synchronized (this) {
      for (CommonControl c : this)
        if (c.isEnabled() && c.keyEvent(e))
          break;
    }
  }

  @Override
  public void clear() {
    synchronized (this) {
      super.clear();
      left.clear();
      right.clear();
    }
  }

  public int onLeft() {
    return left.size();
  }

  public int onRight() {
    return right.size();
  }

  private interface CtlEnum {

    int withCtl(CommonControl c, int dx);

  }

}
