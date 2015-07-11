package org.ankhzet.ergo.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ProgressRenderer {

  long show = 0;
  String label = "Progress...";
  int progress = 65, max = 100;

  void draw(Graphics2D g, int cw, int ch) {
    if (show == 0)
      return;

    int d = (int) (0.1 * (cw < ch ? cw : ch));
    if (d < 32)
      d = 32;

    int x = (cw - d) / 2;
    int y = (ch - d) / 2;

    g.setColor(Color.DARK_GRAY);
    g.fillOval(x, y, d, d);

    g.setColor(Color.WHITE);
    g.drawOval(x, y, d - 1, d - 1);

    g.setColor(Color.LIGHT_GRAY);
    g.fillOval(x + 4, y + 4, d - 8, d - 8);
    g.setColor(Color.DARK_GRAY);
    g.fillArc(x + 2, y + 2, d - 4, d - 4, 90, 360 - (int) (360 * progress / (double) max));
    g.setColor(Color.DARK_GRAY);
    g.fillOval(x + 12, y + 12, d - 24, d - 24);

    Font f = g.getFont();
    FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
    Rectangle2D r = f.getStringBounds(label, frc);
    int tw = (int) r.getWidth();
    d -= f.getSize();
    g.setColor(Color.BLACK);
    g.drawString(label, (cw - tw) / 2 - 1, ch / 2 + d);
    g.drawString(label, (cw - tw) / 2 + 1, ch / 2 + d);
    g.drawString(label, (cw - tw) / 2, ch / 2 + d - 1);
    g.drawString(label, (cw - tw) / 2, ch / 2 + d + 1);
    g.setColor(Color.WHITE);
    g.drawString(label, (cw - tw) / 2, ch / 2 + d);
    g.drawString(label, (cw - tw) / 2, ch / 2 + d);
  }

  void setProgress(String label, int p, int m) {
    this.label = label;
    progress = p;
    max = m;
    show = System.currentTimeMillis();
  }

  void hide() {
    show = 0;
  }
}
