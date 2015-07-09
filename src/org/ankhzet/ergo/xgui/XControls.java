package org.ankhzet.ergo.xgui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.ankhzet.ergo.ClassFactory.IoC;
import org.ankhzet.ergo.Skin;
import org.ankhzet.ergo.UILogic;

class CtlMap extends HashMap<Integer, CommonControl> {
};

public class XControls extends ArrayList<CommonControl> {

  CommonControl focused = null;
  CtlMap left = new CtlMap();
  CtlMap right = new CtlMap();
  public static final int//
  AREA_LEFT = 1,//
  AREA_RIGHT = 2//
  ;

  public void Draw(Graphics2D g, int x, int y, int cw, int ch) {
    ch--;
    g.setColor(Skin.get().UI_PANEL);
    g.fillRect(0, 0, cw, ch - 1);

    for (CommonControl c : this)
      if (c.isVisible())
        c.Draw(g);

    g.setColor(Color.GRAY);
    g.drawRoundRect(x, y - 3, cw - 1, ch + 2, 6, 6);
  }

  public void Process() {
    for (CommonControl c : this)
      c.Process();
  }

  public boolean mouseEvent(MouseEvent e) {
    focused = null;
    for (CommonControl c : this)
      if (c.isVisible() && c.mouseEvent(e, focused == null) && focused == null)
        focused = c;

    /*    for (CommonControl c : this)
    if (c != focused)
    c.mouseEvent(e, false);*/

    Process();
    if (focused == null)
      IoC.<UILogic>get(UILogic.class).tooltip(null, 0, 0);
    return focused != null;
  }

  public void unfocus() {
    focused = null;
  }

  public CommonControl putControl(CommonControl c, int area) {
    add(c);
    switch (area) {
    case AREA_LEFT:
      left.put(left.size(), c);
      break;
    case AREA_RIGHT:
      right.put(right.size(), c);
      break;
    }
    return c;
  }

  public void pack(int cw, int ch) {
    ArrayList<Integer> l;
    int dx;

    l = new ArrayList<Integer>(left.keySet());
    Collections.sort(l);
    dx = 3;
    for (Integer idx : l) {
      CommonControl control = left.get(idx);
      control.x = dx;
      control.y = 2;
      dx += control.w + 5;
    }

    l = new ArrayList<Integer>(right.keySet());
    Collections.sort(l);
    dx = cw - 3;
    for (Integer idx : l) {
      CommonControl control = right.get(idx);
      control.x = dx - control.w;
      control.y = 2;
      dx -= control.w + 5;
    }

  }

  @Override
  public void clear() {
    super.clear();
    left.clear();
    right.clear();
  }

  public int onLeft() {
    return left.size();
  }
  public int onRight() {
    return right.size();
  }
}
