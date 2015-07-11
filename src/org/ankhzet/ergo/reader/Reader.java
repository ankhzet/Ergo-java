package org.ankhzet.ergo.reader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.concurrent.locks.ReentrantLock;

import org.ankhzet.ergo.LoaderProgressListener;
import org.ankhzet.ergo.Skin;
import org.ankhzet.ergo.reader.chapter.Chapter;
import org.ankhzet.ergo.utils.Strings;
import org.ankhzet.ergo.utils.Utils;
import org.ankhzet.ergo.reader.chapter.ChapterCacher;
import org.ankhzet.ergo.reader.chapter.ChapterLoader;
import org.ankhzet.ergo.reader.chapter.page.PageData;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Reader {

  private final ReentrantLock lock = new ReentrantLock();

  protected Strings mangaRoots = new Strings();
  protected PageRenderOptions options;
  protected MagnifyGlass magnifier;
  protected ChapterCacher pages = new ChapterCacher();
  protected ChapterLoader loader;

  public Strings pageFiles = new Strings();
  public int currentPage = -1;
  public static final String PAGE_PATTERN = "^.*?\\.(png|jpe?g|gif|bmp)";
  public static final int TAB_BAR_HEIGHT = 8;
  private boolean flushCache = false;
  int scrollPosX = 0, scrollPosY = 0;

  public Reader() {
    mangaRoots.add("F:/myprogs/engines/ErgoProxy/client v. 1.0/bin/manga");
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
      pageFiles.clear();
      pages.clear();
      if (listener != null)
        progressLoading(listener, 0);

      pageFiles.addAll(chapter.fetchPages());
      int pos = 0;
      for (String imageFile : pageFiles) {
        pages.put(imageFile, PageData.load(imageFile));
        if (listener != null && !progressLoading(listener, ++pos))
          return;
      }
      if (listener != null)
        listener.progressDone();
      firstPage();
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
    if (magnifier.activated)
      magnifier.layouted();
    pages.prepareCache(options, listener);
    scroll(0, 0);
  }

  public int firstPage() {
    currentPage = -1;
    return nextPage();
  }

  public int lastPage() {
    scrollPosX = 0;
    scrollPosY = 0;
    currentPage = pageFiles.size() - 1;
    if (magnifier.activated)
      magnifier.layouted();
    return currentPage;
  }

  public int nextPage() {
    scrollPosX = 0;
    scrollPosY = 0;
    int idx = currentPage + 1;
    int last = pageFiles.size() - 1;
    if (idx > last)
      idx = last;

    currentPage = idx;
    if (magnifier.activated)
      magnifier.layouted();
    return currentPage;
  }

  public int prevPage() {
    int idx = currentPage - 1;
    int last = pageFiles.size() - 1;
    if (idx < 0)
      idx = 0;
    if (idx > last)
      idx = last;

    currentPage = idx;
    if (magnifier.activated)
      magnifier.layouted();
    return currentPage;
  }

  public PageData getPage(int idx) {
    return (idx < 0 || idx >= totalPages()) ? null : pages.get(pageFiles.get(idx));
  }

  public int totalPages() {
    return pageFiles.size();
  }

  public void draw(Graphics2D g, int x, int y, int w, int h) {
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
    y--;
    if (currentPage >= 0) {
      int tab = (int) (currentPage * (tabs / (double) pageCount)) + 1;
      int px = (int) ((tab - 1) * pixelsPerTab);
      int pw = (int) (tab * pixelsPerTab) - px;
      g.setColor(Color.LIGHT_GRAY);
      g.fillRect(x + px + 3, y + 3, pw - 1, tabHeight - 1);
    }

    g.setColor(Color.BLACK);
    int tab = 0;
    while (tab++ < tabs) {
      int px = (int) ((tab - 1) * pixelsPerTab);
      int pw = (int) (tab * pixelsPerTab) - px;
      g.drawRoundRect(x + px + 2, y + 2, pw - 0, tabHeight, 4, 4);
      g.drawRoundRect(x + px + 2, y + 2, pw - 0, tabHeight, 4, 4);
    }
    g.setColor(Color.WHITE);
    tab = 0;
    while (tab++ < tabs) {
      int px = (int) ((tab - 1) * pixelsPerTab);
      int pw = (int) (tab * pixelsPerTab) - px;
      g.drawRoundRect(x + px + 3, y + 3, pw - 2, tabHeight - 2, 4, 4);
      g.drawRoundRect(x + px + 3, y + 3, pw - 2, tabHeight - 2, 4, 4);
    }

    magnifier.draw(g, 0, TAB_BAR_HEIGHT, w, h - TAB_BAR_HEIGHT);
  }

  void drawPages(Graphics2D g, int x, int y, int w, int h) {
    //if no pages - we'r done here
    if (currentPage < 0)
      return;

    PageData data = getPage(currentPage);
    if (data == null)
      return;

    Rectangle clip = g.getClipBounds();
    g.clipRect(x, y + 1, w, h);

    int pageCount = totalPages();
    int dx = 0, dy = 0;

    if (!SwipeHandler.done()) { // swipe in process
      int dir = SwipeHandler.direction();
      int next = currentPage + dir;
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


      PageData nextPage = getPage(next);
      if (nextPage != null)
        nextPage.drawPage(g, x - nx, y - ny, 0, 0);
    }

    data.drawPage(g, x - dx - scrollPosX, y - dy - scrollPosY, 0, 0);

    if (options.originalSize) { // draw scrolls if needed
      int scrollbarSize = 4;
      int sx = data.getLayout().scrollX;
      if (sx > 0) {
        int cw = w - scrollbarSize;
        double swRatio = cw / (double) (cw + sx);
        int scrollWidth = (int) (cw * swRatio);
        int scrollPos = x + (int) (scrollPosX * swRatio);
        Skin.drawScrollbar(g, scrollPos, y + h - scrollbarSize - 1, scrollWidth, scrollbarSize);
      }
      int sy = data.getLayout().scrollY;
      if (sy > 0) {
        int ch = h - scrollbarSize;
        double swRatio = ch / (double) (ch + sy);
        int scrollHeight = (int) (ch * swRatio);
        int scrollPos = y + (int) (scrollPosY * swRatio);
        Skin.drawScrollbar(g, x + w - scrollbarSize - 1, scrollPos, scrollbarSize, scrollHeight);
      }

    }

    g.setClip(null);
    g.setClip(clip);
  }

  public void scroll(int dx, int dy) {
    PageData data = getPage(currentPage);
    if (data == null)
      return;

    scrollPosX = Utils.constraint(scrollPosX + dx, 0, data.getLayout().scrollX);
    scrollPosY = Utils.constraint(scrollPosY + dy, 0, data.getLayout().scrollY);
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
    magnifier.process();
  }

  public void mouseEvent(MouseEvent e) {
    magnifier.mouseEvent(e);
  }
  
  // *** di start
  public PageRenderOptions diPageRenderOptions(PageRenderOptions options) {
    return (options != null) ? this.options = options : this.options;
  }
  
  public MagnifyGlass diMagnifyGlass(MagnifyGlass magnifier) {
    return (magnifier != null) ? this.magnifier = magnifier : this.magnifier;
  }

  public ChapterLoader diChapterLoader(ChapterLoader loader) {
    return (loader != null) ? this.loader = loader : this.loader;
  }
  // *** di end
}
