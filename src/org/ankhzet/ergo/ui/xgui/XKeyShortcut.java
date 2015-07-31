package org.ankhzet.ergo.ui.xgui;

import java.awt.event.KeyEvent;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XKeyShortcut {

  int eventMask;
  String keyShortcut;

  public XKeyShortcut(int eventMask, String keyShortcut) {
    this.eventMask = eventMask;
    this.keyShortcut = keyShortcut.toLowerCase();
  }

  public static XKeyShortcut press(String shortcut) {
    return new XKeyShortcut(KeyEvent.KEY_PRESSED, shortcut);
  }

  public static XKeyShortcut release(String shortcut) {
    return new XKeyShortcut(KeyEvent.KEY_RELEASED, shortcut);
  }

  public boolean isKeyEvent(KeyEvent e) {
    if (e.getID() != eventMask)
      return false;

    String modifiers = KeyEvent.getKeyModifiersText(e.getModifiers()) + '+';
    String key = KeyEvent.getKeyText(e.getKeyCode());
    modifiers = modifiers.replace(key + '+', "");
    if (modifiers.equals("+"))
      modifiers = "";

    String text = modifiers + key;

    return text.toLowerCase().equals(keyShortcut);
  }

}
