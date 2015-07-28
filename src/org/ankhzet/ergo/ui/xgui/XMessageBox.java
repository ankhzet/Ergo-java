package org.ankhzet.ergo.ui.xgui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XMessageBox {

  public static final int MSGBOX_TIMEOUT = 2000;

  long shownTill = 0;
  protected String message;

  public void show(String message) {
    this.message = message;
    shownTill = System.currentTimeMillis() + MSGBOX_TIMEOUT;
  }
  
  public boolean isShown() {
    return System.currentTimeMillis() < shownTill;
  }

  public void draw(Graphics2D g, Rectangle clientArea) {
    if (!isShown())
      return;
    
    Font f = g.getFont();
    FontRenderContext frc = g.getFontRenderContext();
    Rectangle2D r;
    String c, t;
    int ch = f.getSize();
    int th = 0, tp = 0, tw = 0;
    c = message.replaceAll("(^[\10\40\09]+)|([\10\09\40]+$)", "");
    while (!c.isEmpty()) {
      th += ch;
      int cp = c.indexOf('\n');
      if (cp < 0)
        cp = c.length() - 1;

      r = f.getStringBounds(c, 0, cp, frc);
      int cw = (int) r.getWidth();
      if (cw > tw)
        tw = cw;

      if (c.length() - cp > 1)
        c = c.substring(cp + 1);
      else
        break;
    }

    int rw = tw + 100;
    if (rw < 200)
      rw = 200;

    int x = clientArea.x;
    int y = clientArea.y;
    int w = clientArea.width;
    int h = clientArea.height;

    int rh = th + 100;
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(x + (w - rw) / 2, y + (h - rh) / 2, rw, rh);
    g.setColor(Color.BLACK);
    g.drawRect(x + (w - rw) / 2, y + (h - rh) / 2, rw, rh);

    String s = message + "";
    while (!s.isEmpty()) {
      tp += ch;
      int len = s.length(), cp = s.indexOf('\n');
      if (cp < 0)
        cp = len;

      t = s.substring(0, cp);
      r = f.getStringBounds(t, frc);
      int cw = (int) r.getWidth();
      g.drawString(t, x + (w - cw) / 2, y + (h - th) / 2 + tp);

      if (len - cp > 1)
        s = s.substring(cp + 1);
      else
        break;
    }
  }

}
