package org.ankhzet.ergo.ui.xgui;

import java.awt.event.KeyEvent;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XAction {

  public interface Action {

    public void perform(XAction action);

  }

  public interface XActionStateListener {

    public boolean isStated(XAction action);

  }

  protected String name;
  protected Action actualAction;
  protected XKeyShortcut shortcut;
  protected XActionStateListener togglable, enabled;
  protected boolean isProcessed;

  public XAction(String actionName, Action actualAction) {
    this.name = actionName;
    this.actualAction = actualAction;
  }

  public String getName() {
    return name;
  }

  public boolean isA(String actionName) {
    return name.equalsIgnoreCase(actionName);
  }

  public boolean keyEvent(KeyEvent e) {
    return (shortcut != null) && shortcut.isKeyEvent(e) && perform();
  }

  public void processed(boolean processed) {
    isProcessed = processed;
  }

  public boolean isProcessed() {
    return isProcessed;
  }

  public boolean perform() {
    processed(true);
    try {
      actualAction.perform(this);
    } catch (Exception ex) {
      ex.printStackTrace();
      processed(false);
    }

    return isProcessed();
  }

  public XAction togglable(XActionStateListener listener) {
    togglable = listener;
    return this;
  }

  public XAction shortcut(XKeyShortcut s) {
    shortcut = s;
    return this;
  }

  public boolean isToggled() {
    return (togglable != null) && togglable.isStated(this);
  }

  public XAction enabledAs(XActionStateListener listener) {
    enabled = listener;
    return this;
  }

  public boolean isEnabled() {
    return (enabled == null) || enabled.isStated(this);
  }

}
