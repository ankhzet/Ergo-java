package org.ankhzet.ergo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public final class Skin {

  public final Color//
  BG_COLOR, UI_PANEL, UI_SCROLLBG, UI_SCROLLBORDER;
  static Skin i = null;

  private Skin() {
    BG_COLOR = Color.WHITE;
    UI_PANEL = getColor("#DDDDDD");
    UI_SCROLLBG = getColor("#AA000000");
    UI_SCROLLBORDER = getColor("#AAFFFFFF");
  }

  public static Skin get() {
    if (i == null)
      i = new Skin();

    return i;
  }

  Color getColor(String code) {
    if (code.length() == 3) {
      String r = code.substring(0, 1), g = code.substring(1, 2), b = code.substring(2, 3);
      code = r + r + g + g + b + b;
    }
    Long intval = Long.decode(code);
    return new Color(intval.intValue(), code.length() > 6);
  }

  public static void drawScrollbar(Graphics2D g, int x, int y, int w, int h) {
    Skin skin = get();
    g.setColor(skin.UI_SCROLLBG);
    g.fillRoundRect(x, y, w, h, 6, 6);
    g.setColor(skin.UI_SCROLLBORDER);
    g.drawRoundRect(x, y, w, h, 6, 6);
  }
}