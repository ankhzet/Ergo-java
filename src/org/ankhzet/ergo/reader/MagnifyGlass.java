package org.ankhzet.ergo.reader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import org.ankhzet.ergo.reader.chapter.page.PageData;
import org.ankhzet.ergo.reader.chapter.page.PageLayout;
import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MagnifyGlass {

  static final int SAMPLED_SIZE = 32;
  boolean activated = false, active = false;
  int posX = 0, posY = 0, width = 32, height = 32;
  int imgX = 0, imgY = 0;
  int projX = 0, projY = 0;
  int magnification = 3;
  int prevPage = -1;
  PageData data = null;
  boolean layouted = false;
  Image sample = null;
  
  Reader reader;

  public void injectDependencies(Reader reader) {
    this.reader = reader;
  }

  public void mouseEvent(MouseEvent e) {
    posX = e.getX();
    posY = e.getY() - Reader.TAB_BAR_HEIGHT;

    switch (e.getID()) {
    case MouseEvent.MOUSE_PRESSED:
      active = !reader.isLoading();
      break;
    case MouseEvent.MOUSE_RELEASED:
      active = false;
      break;
    }

    if (!active || data == null || !layouted || reader.options.originalSize)
      return;

    // translate view coordinates to image coordinates

    PageLayout layout = data.getLayout();
    double dx = layout.newPageW / (double) data.pageW;
    double dy = layout.newPageH / (double) data.pageH;
    imgX = Utils.constraint((int) ((posX - layout.renderX) / dx), width / 2, data.pageW - width / 2);
    imgY = Utils.constraint((int) ((posY - layout.renderY) / dy), height / 2, data.pageH - height / 2);

    projX = layout.renderX + (int) (imgX * dx);
    projY = layout.renderY + (int) (imgY * dy);

    int mw = SAMPLED_SIZE * magnification;
    int mh = SAMPLED_SIZE * magnification;
    Graphics2D g = (Graphics2D)sample.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g.drawImage(data.getImage(), 0, 0, mw, mh, imgX - width / 2, imgY - height / 2, imgX + width / 2, imgY + height / 2, null);
    g.dispose();
  }

  public void process() {
    if (!activated)
      return;

    if (prevPage != reader.currentPage) { // page changed

      active = false;
      layouted = false;
    }

  }

  public void draw(Graphics2D g, int x, int y, int w, int h) {
    if (!active)
      return;

    // source rectangle
    int mw = SAMPLED_SIZE * magnification;
    int mh = SAMPLED_SIZE * magnification;

    g.setColor(Color.WHITE);
    g.drawRect(x + projX - SAMPLED_SIZE / 2, y + projY - SAMPLED_SIZE / 2, SAMPLED_SIZE, SAMPLED_SIZE);
    g.setColor(Color.BLACK);
    g.drawRect(x + projX - SAMPLED_SIZE / 2 + 1, y + projY - SAMPLED_SIZE / 2 + 1, SAMPLED_SIZE - 2, SAMPLED_SIZE - 2);

    int pX = x + projX - mw / 2;
    int pY = y + projY - SAMPLED_SIZE / 2 - 3 - mh;

    pX = Utils.constraint(pX, 0, w - mw);
    if (pY <= 0)
      pY = y + projY + SAMPLED_SIZE / 2 + 4;

    g.setColor(Color.BLACK);
    g.drawRect(pX - 1, pY - 1, mw + 1, mh + 1);
    g.drawImage(sample, pX, pY, null);

  }

  public void layouted() {
    if (reader.isBusy())
      return;
    
    prevPage = reader.currentPage;
    data = reader.getPage(reader.currentPage);

    if (data == null)
      return;

    double dx = data.getLayout().newPageW / (double) data.pageW;
    double dy = data.getLayout().newPageH / (double) data.pageH;

    // get magnification parameters
    width = (int) (SAMPLED_SIZE / dx);
    height = (int) (SAMPLED_SIZE / dy);

    int mw = SAMPLED_SIZE * magnification;
    int mh = SAMPLED_SIZE * magnification;
    if (sample != null) {
      sample.flush();
      sample.getGraphics().dispose();
      sample = null;
    }
    sample = new BufferedImage(mw, mh, BufferedImage.TYPE_BYTE_GRAY);

    layouted = true;
  }
}
