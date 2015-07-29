package org.ankhzet.ergo.ui.xgui.filepicker;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Batch<T> extends ArrayList<T> {

  int column = 0, dy, batch;
  float columnWidth;
  Rectangle r = new Rectangle();

  public Batch(float columnWidth, float columnHeight, int dy, int batch) {
    this.columnWidth = columnWidth;
    this.dy = dy;
    this.batch = batch;
    r.height = (int) columnHeight;
    r.width = (int) columnWidth;
  }

  boolean ready() {
    return size() >= batch;
  }

  void nextBatch() {
    clear();
    column++;
  }

}
