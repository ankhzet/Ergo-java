package org.ankhzet.ergo.ui.pages.reader.reader;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class PageRenderOptions {

  public boolean manhwaMode = false;
  public boolean stretchToFit = true;
  public boolean originalSize = false;
  public boolean fitHeight = true;
  public boolean rotateToFit = false;
  public boolean turnClockwise = true;

  public PageRenderOptions() {

  }

  public PageRenderOptions(boolean manhwaMode, boolean stretchToFit, boolean originalSize) {
    this.manhwaMode = manhwaMode;
    this.stretchToFit = stretchToFit;
    this.originalSize = originalSize;
  }

  public boolean toggleRotationToFit() {
    rotateToFit = !rotateToFit;
    return !rotateToFit;
  }

  public boolean toggleOriginalSize() {
    originalSize = !originalSize;
    return !originalSize;
  }

  public boolean showOriginalSize() {
    return originalSize;
  }

  public boolean rotateToFit() {
    return rotateToFit;
  }

}
