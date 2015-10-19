package org.ankhzet.ergo.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Skin {

  public final Color//
    BG_COLOR,
    UI_SCAN_LOADING,
    UI_SCAN_CACHING,
    UI_PANEL,
    UI_OUTLINEO,
    UI_OUTLINEI,
    UI_SCROLLBG,
    UI_SCROLLBORDER;

  public final int UI_C;

  {
    UI_C = 6;

    BG_COLOR = Color.WHITE;

    UI_PANEL = getColor("#DDDDDD");

    UI_SCAN_LOADING = getColor("#80000");
    UI_SCAN_CACHING = getColor("#202020");

    UI_SCROLLBG = getColor("#AA000000");
    UI_SCROLLBORDER = getColor("#AAFFFFFF");

    UI_OUTLINEO = Color.BLACK;
    UI_OUTLINEI = Color.WHITE;
  }

  static Skin i = null;

  public static Skin get() {
    if (i == null)
      i = new Skin();

    return i;
  }

  final Color getColor(String code) {
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
    g.fillRoundRect(x, y, w, h, skin.UI_C, skin.UI_C);
    g.setColor(skin.UI_SCROLLBORDER);
    g.drawRoundRect(x, y, w, h, skin.UI_C, skin.UI_C);
  }

  public static void drawBevel(Graphics2D g, int x, int y, int w, int h) {
    Skin skin = get();

    g.setColor(skin.UI_OUTLINEO);
    g.drawRoundRect(x, y, w, h, skin.UI_C, skin.UI_C);
//    g.setColor(skin.UI_OUTLINEI);
//    g.drawRoundRect(x + 1, y + 1, w - 2, h - 2, skin.UI_C, skin.UI_C);
  }

  public static void fillBevel(Graphics2D g, int x, int y, int w, int h) {
    Skin skin = get();

    g.fillRoundRect(x, y, w, h, skin.UI_C, skin.UI_C);
  }

  public static void drawLabel(Graphics2D g, String text, int x, int y, int w, int h) {
    Rectangle2D r = labelSize(g, text);
    int tw = (int) r.getWidth();
    int th = (int) r.getHeight();
    x = Utils.constraint(x - tw / 2, 0, w - tw);
    Color c = g.getColor();
    g.setColor(Color.WHITE);
    g.drawString(text, x, y + th + 1);
    g.setColor(Color.BLACK);
    g.drawString(text, x, y + th);
    g.setColor(c);
  }

  public static Rectangle2D labelSize(Graphics2D g, String text) {
    Font f = g.getFont();
    FontRenderContext frc = g.getFontRenderContext();
    Rectangle2D s = f.getStringBounds(text, frc);
    Rectangle rect = new Rectangle(s.getBounds());
    rect.height = f.getSize();
    return rect;
  }

}
