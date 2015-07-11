package org.ankhzet.ergo.reader.chapter.page;

import org.ankhzet.ergo.reader.PageRenderOptions;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class PageLayout {

  public int//
  clientW = 0,
  clientH = 0,
  renderX = 0,
  renderY = 0,
  oldPageW = 0,
  oldPageH = 0,
  newPageW = 0,
  newPageH = 0,
  scrollX = 0,
  scrollY = 0;

  public PageLayout(int cw, int ch) {
    clientH = ch;
    clientW = cw;
  }

  public boolean calcLayout(int pageW, int pageH, PageRenderOptions ro) {
    oldPageW = newPageW;
    oldPageH = newPageH;

    if (pageW * pageH == 0)
      return false;

    if (ro.manhwaMode) // page under page
      //stretchToFit - fit in view the width of pages
//      if (totalX < pageW)
//        totalX = pageW;
      return true;

    // first, we need to scale page, if needed
    boolean clientPortret = clientW < clientH;
    boolean pagePortret = pageW < pageH;
    newPageW = pageW;
    newPageH = pageH;
    if (ro.rotateToFit && (clientPortret ^ pagePortret)) {
      newPageW = pageH;
      newPageH = pageW;
    }
    double ratio = newPageW / (double) newPageH;
    int posX = 0;
    int posY = 0;
    scrollX = 0;
    scrollY = 0;
    if (!ro.originalSize) { // do scale
      boolean smallW = newPageW < clientW;
      boolean smallH = newPageH < clientH;
      if (ro.stretchToFit && smallW && smallH)// enlarge, if needed
        if (ro.fitHeight) {
          newPageH = clientH;
          newPageW = (int) (newPageH * ratio);
        } else {
          newPageW = clientW;
          newPageH = (int) (newPageW / ratio);
        }

      // fit in view, if needed
      if (newPageW > clientW) {
        newPageW = clientW;
        newPageH = (int) (newPageW / ratio);
      }
      if (newPageH > clientH) {
        newPageH = clientH;
        newPageW = (int) (newPageH * ratio);
      }

      // now, center page in view, if not scrolling
      posX = (clientW - newPageW) / 2;
      posY = (clientH - newPageH) / 2;
    } else {
      //don't scale, center, if possible
      scrollX = newPageW - clientW;
      scrollY = newPageH - clientH;

      if (scrollX < 0) {
        posX = (clientW - pageW) / 2;
        scrollX = 0;
      }
      if (scrollY < 0) {
        posY = (clientH - pageH) / 2;
        scrollY = 0;
      }
    }

    renderX = posX;
    renderY = posY;

    return true;
  }

  public boolean wasResized() {
    return (oldPageW != newPageW) || (oldPageH != newPageH);
  }
}
