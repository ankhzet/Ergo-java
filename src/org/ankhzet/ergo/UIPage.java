package org.ankhzet.ergo;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIPage {

  protected boolean mouseDown = false;
  boolean active = false;

  public boolean actionPerformed(String a) {
    return false;
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

  public void navigateIn() {
    active = true;
  }

  public void navigateOut() {
    active = false;
  }
}
