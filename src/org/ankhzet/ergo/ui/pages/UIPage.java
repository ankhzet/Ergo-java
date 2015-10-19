package org.ankhzet.ergo.ui.pages;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import ankh.annotations.DependenciesInjected;
import ankh.annotations.DependencyInjection;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.xgui.XAction;
import org.ankhzet.ergo.ui.xgui.XAction.Action;
import org.ankhzet.ergo.ui.xgui.XActions;
import org.ankhzet.ergo.ui.xgui.XControls;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIPage {

  @DependencyInjection
  protected UILogic ui;

  protected XControls hud;

  protected boolean mouseDown = false;
  Point pressPos = new Point();
  public int mx, my;

  XActions actions = new XActions();

  boolean active = false;

  @DependenciesInjected
  private void di() {
    hud = ui.getHUD();
  }

  public XAction registerAction(String name, Action action) {
    return actions.registerAction(name, action);
  }

  public void registerActions(String[] names, Action action) {
    actions.registerActions(names, action);
  }

  public boolean actionPerformed(XAction a) {
    return actions.performAction(a);
  }

  public void process() {
  }

  public void resized(int x, int y, int w, int h) {
  }

  public void setFocused(boolean focused) {
    if (!focused)
      mouseDown = false;
  }

  public boolean mouseEvent(MouseEvent e) {
    mx = e.getX();
    my = e.getY();

    int dx = pressPos.x - mx;
    int dy = pressPos.y - my;
    switch (e.getID()) {
    case MouseEvent.MOUSE_PRESSED:
      pressPos.setLocation(mx, my);
      mouseDown = true;
      hud.unfocus();
      break;
    case MouseEvent.MOUSE_DRAGGED:
      scroll(dx, dy);
      pressPos.setLocation(mx, my);
      break;
    case MouseEvent.MOUSE_RELEASED:
      mouseDown = false;
      break;
    }

    return mouseDown;
  }

  public void scroll(int x, int y) {
  }

  public void draw(Graphics2D g, int w, int h) {
  }

  public boolean onProgress(int state, int p, int max) {
    return true;
  }

  public void navigateIn(Object... params) {
    active = true;
  }

  public void navigateOut() {
    active = false;
  }

  public String title() {
    return this.toString();
  }

}
