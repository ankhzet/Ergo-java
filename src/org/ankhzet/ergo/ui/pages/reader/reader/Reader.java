package org.ankhzet.ergo.ui.pages.reader.reader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import ankh.annotations.DependenciesInjected;
import ankh.annotations.DependencyInjection;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.manga.chapter.chaptercacher.ScanCache;
import org.ankhzet.ergo.manga.chapter.chaptercacher.ScansCache;
import org.ankhzet.ergo.manga.chapter.chaptercacher.cache.CacheTask;
import org.ankhzet.ergo.manga.chapter.page.PageData;
import org.ankhzet.ergo.manga.chapter.page.ReadOptions;
import org.ankhzet.ergo.ui.LoaderProgressListener;
import org.ankhzet.ergo.ui.Skin;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.utils.DelayableAction;
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
  protected UILogic ui;
  @DependencyInjection
  protected ReadOptions options;
  @DependencyInjection
  protected MagnifyGlass magnifier;
  @DependencyInjection
  protected ScansCache cache;

  private final ReentrantLock lock = new ReentrantLock();

  protected Strings mangaRoots = new Strings();
  Chapter chapter;

  public Strings scanFileNames = new Strings();
  int hilitedScan = 0;
  Rectangle clientRect = new Rectangle();
  Point scrollPos = new Point();

  public Chapter chapter() {
    return chapter;
  }

  public void setMangaRoots(Strings roots) {
    mangaRoots.clear();
    mangaRoots.addAll(roots);
  }

  public Strings getMangaRoots() {
    return mangaRoots;
  }

  public boolean isBusy() {
    return lock.isLocked();
  }

  public boolean isLoading() {
    return lock.isLocked();
  }

  public void flushLayout() {
    DelayableAction.enqueue("reader-resize", () -> {
      try {
        while (isBusy())
          Thread.sleep(50);
      } catch (InterruptedException ex) {
      }

      cache.invalidateAll(2);
    });
  }

  public synchronized void LoadPages(Chapter chapter) {
    lock.lock();
    try {
      this.chapter = chapter;
      scanFileNames.clear();
      cache.clear();
      scanFileNames.addAll(chapter.fetchPages());

      progressLoading(ui, 0);

      int pos = 0;
      for (String imageFile : scanFileNames) {
        cache.cacheData(imageFile, new PageData(imageFile));

        if (!progressLoading(ui, ++pos))
          return;

        if (pos == 1)
          toFirstPage();
      }
      ui.progressDone();

    } finally {
      lock.unlock();
      System.gc();
    }
  }

  public void loadChapter(Chapter chapter) {
    cache.detatch(() -> {
      LoadPages(chapter);
    });
  }

  private boolean progressLoading(LoaderProgressListener listener, int p) {
    return listener.onProgress(LoaderProgressListener.STAGE_LOADING, p, totalPages());
  }

  public void resized(int x, int y, int w, int h) {
    clientRect.setSize(w, h - TAB_BAR_HEIGHT);
    flushLayout();
    if (magnifier.activated)
      magnifier.layouted();

    PageData page = getCurrentPageData();
    if (page != null)
      page.layout(clientRect.width, clientRect.height, options);

    scroll(0, 0);
  }

  @Override
  public int totalPages() {
    return scanFileNames.size();
  }

  @Override
  public int setPage(int page) {
    page = super.setPage(page);
    scrollPos.move(0, 0);
    if (magnifier.activated)
      magnifier.layouted();
    return page;
  }

  public PageData getPageData(int idx) {
    return (idx < 0 || idx >= totalPages()) ? null : cache.cachedData(scanFileNames.get(idx));
  }

  public PageData getCurrentPageData() {
    return getPageData(currentPage());
  }

  public void draw(Graphics2D g, int x, int y, int w, int h) {
    lock.lock();
    try {
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

      g.setColor(Color.WHITE);
      g.drawRoundRect(x, y, w - 1, TAB_BAR_HEIGHT, 5, 5);

      x--;
      y++;
      int tab = 0;
      int current = currentPage();
      while (tab++ < tabs) {
        int px = (int) ((tab - 1) * pixelsPerTab);
        int pw = (int) (tab * pixelsPerTab) - px;

        int page = pageFromTab(tab, tabs);
        int next = pageFromTab(tab + 1, tabs);
        ScanCache scanCache = cache.get(scanFileNames.get(page));

        // is tab current? (LIGHT_GRAY) cached? (DARK_GRAY/GRAY)
        if (current >= page && current < next)
          g.setColor(Color.LIGHT_GRAY);
        else
          g.setColor(isCached(scanCache));

        g.fillRect(x + px + 3, y + 1, pw - 1, tabHeight - 1);

        // is tab loaded? (RED/BLACK)
        g.setColor(isLoaded(scanCache));
        g.drawRoundRect(x + px + 2, y, pw - 0, tabHeight, 4, 4);

        g.setColor(Color.WHITE);
        g.drawRoundRect(x + px + 3, y + 1, pw - 2, tabHeight - 2, 4, 4);

        if (tab == hilitedScan) {
          String pageFile = (new File(scanFileNames.get(pageFromTab(tab, tabs)))).getName();

          Skin.drawLabel(g, pageFile, px + pw / 2, y + 1 + tabHeight + 32, clientRect.width, clientRect.height);
        }
      }

    } finally {
      lock.unlock();
    }

    if (magnifierShown())
      magnifier.draw(g, 0, TAB_BAR_HEIGHT, w, h - TAB_BAR_HEIGHT);
  }

  int pageFromTab(int tab, int totalTabs) {
    float denom = totalPages() / (float) totalTabs;
    return Utils.constraint((int) ((tab - 1) * denom), 0, tab - 1);
  }

  Color isLoaded(ScanCache scanCache) {
    return ((scanCache != null) && scanCache.cached() > 0)
           ? Color.BLACK
           : Skin.get().UI_SCAN_LOADING;
  }

  Color isCached(ScanCache scanCache) {
    return ((scanCache != null) && scanCache.cached() > 2)
           ? Color.GRAY
           : Skin.get().UI_SCAN_CACHING;
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

    page.draw(g, x - dx - scrollPos.x, y - dy - scrollPos.y);

    if (options.originalSize()) { // draw scrolls if needed
      int scrollbarSize = 4;
      int sx = page.scrollX;
      if (sx > 0) {
        int cw = w - scrollbarSize;
        double swRatio = cw / (double) (cw + sx);
        int scrollWidth = (int) (cw * swRatio);
        int pos = x + (int) (scrollPos.x * swRatio);
        Skin.drawScrollbar(g, pos, y + h - scrollbarSize - 1, scrollWidth, scrollbarSize);
      }
      int sy = page.scrollY;
      if (sy > 0) {
        int ch = h - scrollbarSize;
        double swRatio = ch / (double) (ch + sy);
        int scrollHeight = (int) (ch * swRatio);
        int pos = y + (int) (scrollPos.y * swRatio);
        Skin.drawScrollbar(g, x + w - scrollbarSize - 1, pos, scrollbarSize, scrollHeight);
      }

    }

    g.setClip(clip);
  }

  public void scroll(int dx, int dy) {
    PageData page = getCurrentPageData();
    if (page == null)
      return;

    scrollPos = page.constraintScroll(scrollPos, dx, dy);
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

  @DependenciesInjected()
  private void di() {
    cache.registerTask((CacheTask<ScanCache>) (ScanCache cacheable) -> {
      return cacheable.getData().load();
    });
    cache.registerTask((CacheTask<ScanCache>) (ScanCache cacheable) -> {
      PageData data = cacheable.getData();
      return data.layout(clientRect.width, clientRect.height, options);
    });
    cache.registerTask((CacheTask<ScanCache>) (ScanCache cacheable) -> {
      PageData data = cacheable.getData();
      return data.prepare(options);
    });

  }

}
