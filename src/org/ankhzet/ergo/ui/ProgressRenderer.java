package org.ankhzet.ergo.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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

  CommonProgressRenderer r = new CornerProgressRenderer();

  void draw(Graphics2D g, int cw, int ch) {
    if (show == 0)
      return;

    r.draw(this, g, cw, ch);
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

class CommonProgressRenderer {

  Rectangle2D labelSize(ProgressRenderer r, Graphics2D g) {
    Font f = g.getFont();
    FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
    Rectangle2D s = f.getStringBounds(r.label, frc);
    Rectangle rect = new Rectangle(s.getBounds());
    rect.height = f.getSize();
    return rect;
  }

  void draw(ProgressRenderer r, Graphics2D g, int cw, int ch) {

  }

  void drawCircles(ProgressRenderer r, Graphics2D g, int x, int y, int size) {
    g.setColor(Color.DARK_GRAY);
    g.fillOval(x - 1, y - 1, size + 2, size + 2);

    g.setColor(Color.WHITE);
    g.drawOval(x, y, size - 1, size - 1);

    g.setColor(Color.LIGHT_GRAY);
    g.fillOval(x + 4, y + 4, size - 8, size - 8);
    g.setColor(Color.DARK_GRAY);
    g.fillArc(x + 2, y + 2, size - 4, size - 4, 90, 360 - (int) (360 * r.progress / (double) r.max));
    g.setColor(Color.DARK_GRAY);
    g.fillOval(x + 12, y + 12, size - 24, size - 24);
  }

  void drawLabel(ProgressRenderer r, Graphics2D g, Point p) {
    g.setColor(Color.BLACK);
    g.drawString(r.label, p.x - 1, p.y);
    g.drawString(r.label, p.x + 1, p.y);
    g.drawString(r.label, p.x, p.y - 1);
    g.drawString(r.label, p.x, p.y + 1);
    g.setColor(Color.WHITE);
    g.drawString(r.label, p.x, p.y);
    g.drawString(r.label, p.x, p.y);
  }

}

class CenterProgressRenderer extends CommonProgressRenderer {

  static final int MIN_SIZE = 32;

  @Override
  void draw(ProgressRenderer r, Graphics2D g, int cw, int ch) {
    int d = (int) (0.1 * (cw < ch ? cw : ch));
    if (d < MIN_SIZE)
      d = MIN_SIZE;

    int x = (cw - d) / 2;
    int y = (ch - d) / 2;

    drawCircles(r, g, x, y, d);

    Rectangle2D rect = labelSize(r, g);
    int tw = (int) rect.getWidth();
    d -= rect.getHeight();

    drawLabel(r, g, new Point((cw - tw) / 2, ch / 2 + d));
  }

}

class CornerProgressRenderer extends CenterProgressRenderer {

  @Override
  void draw(ProgressRenderer r, Graphics2D g, int cw, int ch) {
    int d = (int) (0.1 * (cw < ch ? cw : ch));
    if (d < MIN_SIZE)
      d = MIN_SIZE;

    Rectangle2D rect = labelSize(r, g);
    int tw = (int) rect.getWidth();
    int th = (int) rect.getHeight();

    int x = cw - d;
    int y = ch - d - th;

    drawCircles(r, g, x, y, d);

    drawLabel(r, g, new Point(cw - tw, y + d + th));
  }

}
