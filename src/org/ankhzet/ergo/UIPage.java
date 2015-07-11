package org.ankhzet.ergo;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import org.ankhzet.ergo.xgui.XActions;
import org.ankhzet.ergo.xgui.XAction;
import org.ankhzet.ergo.xgui.XAction.Action;
import org.ankhzet.ergo.xgui.XControls;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIPage {

  protected UILogic ui;
  protected XControls hud;

  XActions actions = new XActions();

  protected boolean mouseDown = false;
  boolean active = false;

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

  public boolean mouseEvent(MouseEvent e) {
    return false;
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

  // *** di
  public UILogic diUILogic(UILogic ui) {
    if (ui != null) {
      this.ui = ui;
      this.hud = ui.getHUD();
    }
    return this.ui;
  }
  // *** end di

}
