package org.ankhzet.ergo.ui.pages.reader.reader;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.manga.chapter.ChapterCacher;
import org.ankhzet.ergo.manga.chapter.ChapterLoader;
import org.ankhzet.ergo.manga.chapter.page.PageData;
import org.ankhzet.ergo.manga.chapter.page.PageRenderOptions;
import org.ankhzet.ergo.ui.LoaderProgressListener;
import org.ankhzet.ergo.ui.Skin;
import org.ankhzet.ergo.utils.Strings;
import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Reader extends PageNavigator {

  public static final String PAGE_PATTERN = "^.*?\\.(png|jpe?g|gif|bmp)";
  public static final int TAB_BAR_HEIGHT = 8;

  @DependencyInjection
  protected PageRenderOptions options;
  @DependencyInjection
  protected MagnifyGlass magnifier;
  @DependencyInjection
  protected ChapterLoader loader;

  private final ReentrantLock lock = new ReentrantLock();

  protected Strings mangaRoots = new Strings();
  protected ChapterCacher pages = new ChapterCacher();
  Chapter chapter;

  public Strings pageFiles = new Strings();
  private boolean flushCache = false;
  int scrollPosX = 0, scrollPosY = 0;
  int hilitedScan = 0;
  Rectangle clientRect = new Rectangle();

  public Reader() {
    mangaRoots.add("H:/manga/manga");
  }

  public Chapter chapter() {
    return chapter;
  }

  public Strings getMangaRoots() {
    return mangaRoots;
  }

  public boolean isBusy() {
    return lock.isLocked() || pages.isBusy();
  }

  public boolean isLoading() {
    return lock.isLocked();
  }

  public void flushCache(boolean flush) {
    flushCache = flush;
  }

  public boolean flushPending() {
    return flushCache;
  }

  public synchronized void cacheChapter(Chapter chapter, LoaderProgressListener listener) {
    lock.lock();
    try {
      this.chapter = chapter;
      pageFiles.clear();
      pages.clear();
      pageFiles.addAll(chapter.fetchPages());

      if (listener != null)
        progressLoading(listener, 0);

      int pos = 0;
      for (String imageFile : pageFiles) {
        pages.put(imageFile, new PageData(imageFile));
        if (listener != null && !progressLoading(listener, ++pos))
          return;

        if (pos == 1)
          toFirstPage();
      }
      if (listener != null)
        listener.progressDone();

    } finally {
      lock.unlock();
      System.gc();
    }
  }

  public void loadChapter(Chapter chapter) {
    loader.load(chapter);
  }

  private boolean progressLoading(LoaderProgressListener listener, int p) {
    return listener.onProgress(LoaderProgressListener.STAGE_LOADING, p, totalPages());
  }

  public void calcLayout(int cw, int ch, LoaderProgressListener listener) {
    if (pages.isBusy())
      return;

    pages.calcLayout(cw, ch - TAB_BAR_HEIGHT, options, listener);
    pages.prepareCache(options, listener);
    if (magnifier.activated)
      magnifier.layouted();
    scroll(0, 0);
  }

  @Override
  public int totalPages() {
    return pageFiles.size();
  }

  @Override
  public int setPage(int page) {
    page = super.setPage(page);
    scrollPosX = 0;
    scrollPosY = 0;
    if (magnifier.activated)
      magnifier.layouted();
    return page;
  }

  public PageData getPageData(int idx) {
    return (idx < 0 || idx >= totalPages()) ? null : pages.get(pageFiles.get(idx));
  }

  public PageData getCurrentPageData() {
    return getPageData(currentPage());
  }

  public void draw(Graphics2D g, int x, int y, int w, int h) {
    clientRect.setBounds(x, y, w, h);
    drawPages(g, x, y + TAB_BAR_HEIGHT, w, h - TAB_BAR_HEIGHT);
    //some gui draw here
    int pageCount = totalPages();
    if (pageCount <= 0)
      return;

    x++;
    w -= 2;

    int tabs = pageCount;
    int spaceforTabs = w - 3;
    int minTabWidth = 6;
    int tabHeight = TAB_BAR_HEIGHT - 2;
    double pixelsPerTab = spaceforTabs / (double) tabs;
    if (pixelsPerTab < minTabWidth) {
      tabs = (int) Math.ceil(spaceforTabs / minTabWidth);
      pixelsPerTab = spaceforTabs / (double) tabs;
    }

    g.setColor(Color.GRAY);
    g.fillRect(x + 1, y + 2, w - 2, TAB_BAR_HEIGHT - 3);
    g.setColor(Color.WHITE);
    g.drawRoundRect(x, y, w - 1, TAB_BAR_HEIGHT, 5, 5);

    x--;
    y++;
    if (currentPage() >= 0) {
      int tab = (int) (currentPage() * (tabs / (double) pageCount)) + 1;
      int px = (int) ((tab - 1) * pixelsPerTab);
      int pw = (int) (tab * pixelsPerTab) - px;
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(x + px + 3, y + 1, pw - 1, tabHeight - 1);
    }

    g.setColor(Color.BLACK);
    int tab = 0;
    while (tab++ < tabs) {
      int px = (int) ((tab - 1) * pixelsPerTab);
      int pw = (int) (tab * pixelsPerTab) - px;
      g.drawRoundRect(x + px + 2, y, pw - 0, tabHeight, 4, 4);
      g.drawRoundRect(x + px + 2, y, pw - 0, tabHeight, 4, 4);
    }
    g.setColor(Color.WHITE);
    tab = 0;
    while (tab++ < tabs) {
      int px = (int) ((tab - 1) * pixelsPerTab);
      int pw = (int) (tab * pixelsPerTab) - px;
      g.drawRoundRect(x + px + 3, y + 1, pw - 2, tabHeight - 2, 4, 4);
      g.drawRoundRect(x + px + 3, y + 1, pw - 2, tabHeight - 2, 4, 4);

      if (tab == hilitedScan) {
        float denom = pageCount / (float) tabs;
        int page = Utils.constraint((int) ((tab - 1) * denom), 0, pageCount - 1);
        String pageFile = (new File(pageFiles.get(page))).getName();

        drawLabel(g, pageFile, px + pw / 2, y + 1 + tabHeight + 32);
      }
    }

    if (magnifierShown())
      magnifier.draw(g, 0, TAB_BAR_HEIGHT, w, h - TAB_BAR_HEIGHT);
  }

  void drawLabel(Graphics2D g, String text, int x, int y) {
    Rectangle2D r = labelSize(g, text);
    int tw = (int) r.getWidth();
    int th = (int) r.getHeight();
    x = Utils.constraint(x - tw / 2, 0, clientRect.width - tw);
    Color c = g.getColor();
    g.setColor(Color.WHITE);
    g.drawString(text, x, y + th + 1);
    g.setColor(Color.BLACK);
    g.drawString(text, x, y + th);
    g.setColor(c);
  }

  Rectangle2D labelSize(Graphics2D g, String text) {
    Font f = g.getFont();
    FontRenderContext frc = g.getFontRenderContext();
    Rectangle2D s = f.getStringBounds(text, frc);
    Rectangle rect = new Rectangle(s.getBounds());
    rect.height = f.getSize();
    return rect;
  }

  void drawPages(Graphics2D g, int x, int y, int w, int h) {
    //if no pages - we'r done here
    PageData page = getCurrentPageData();
    if (page == null)
      return;

    Rectangle clip = g.getClipBounds();
    g.clipRect(x, y + 1, w, h);

    int pageCount = totalPages();
    int dx = 0, dy = 0;

    if (!SwipeHandler.done()) { // swipe in process
      int dir = SwipeHandler.direction();
      int next = currentPage() + dir;
      if (next >= pageCount) // we'r on next chapter
        ;
      if (next < 0) // we'r on prev chapter
        ;

      int nx = 0, ny = 0;
      double progress = SwipeHandler.getProgress();
      dx = dir * (int) (w * progress);
      dy = dir * (int) (h * progress);
      if (SwipeHandler.vertical())
        dx = 0;
      else
        dy = 0;

      if (SwipeHandler.vertical())
        ny = dy - dir * h;
      else
        nx = dx - dir * w;

      PageData nextPage = getPageData(next);
      if (nextPage != null)
        nextPage.draw(g, x - nx, y - ny);
    }

    page.draw(g, x - dx - scrollPosX, y - dy - scrollPosY);

    if (options.originalSize) { // draw scrolls if needed
      int scrollbarSize = 4;
      int sx = page.getLayout().scrollX;
      if (sx > 0) {
        int cw = w - scrollbarSize;
        double swRatio = cw / (double) (cw + sx);
        int scrollWidth = (int) (cw * swRatio);
        int scrollPos = x + (int) (scrollPosX * swRatio);
        Skin.drawScrollbar(g, scrollPos, y + h - scrollbarSize - 1, scrollWidth, scrollbarSize);
      }
      int sy = page.getLayout().scrollY;
      if (sy > 0) {
        int ch = h - scrollbarSize;
        double swRatio = ch / (double) (ch + sy);
        int scrollHeight = (int) (ch * swRatio);
        int scrollPos = y + (int) (scrollPosY * swRatio);
        Skin.drawScrollbar(g, x + w - scrollbarSize - 1, scrollPos, scrollbarSize, scrollHeight);
      }

    }

    g.setClip(clip);
  }

  public void scroll(int dx, int dy) {
    PageData page = getCurrentPageData();
    if (page == null)
      return;

    scrollPosX = Utils.constraint(scrollPosX + dx, 0, page.getLayout().scrollX);
    scrollPosY = Utils.constraint(scrollPosY + dy, 0, page.getLayout().scrollY);
  }

  public boolean showMagnifier(boolean show) {
    boolean old = magnifier.activated;
    magnifier.activated = show;
    if (show)
      magnifier.layouted();
    return old;
  }

  public boolean magnifierShown() {
    return magnifier.activated;
  }

  public void process() {
    if (magnifierShown())
      magnifier.process();
  }

  public boolean mouseEvent(MouseEvent e) {
    if (magnifierShown())
      return magnifier.mouseEvent(e);

    return (hilitedScan = calcHilitedScan(e.getX(), e.getY())) > 0;
  }

  int calcHilitedScan(int x, int y) {
    if (y > TAB_BAR_HEIGHT)
      return 0;

    int scanCount = totalPages();
    if (scanCount <= 0)
      return 0;

    x -= 2;
    int tabs = scanCount;
    int spaceforTabs = clientRect.width - 4;
    int minTabWidth = 6;
    double pixelsPerTab = spaceforTabs / (double) tabs;
    if (pixelsPerTab < minTabWidth) {
      tabs = (int) Math.ceil(spaceforTabs / minTabWidth);
      pixelsPerTab = spaceforTabs / (double) tabs;
    }

    int tab = 0, sel = 0;
    while (tab++ < tabs) {
      int px = (int) ((tab - 1) * pixelsPerTab);
      int pw = (int) (tab * pixelsPerTab) - px;
      if ((px <= x) && (px + pw > x)) {
        sel = tab;
        break;
      }
    }

    return sel;
  }

}
