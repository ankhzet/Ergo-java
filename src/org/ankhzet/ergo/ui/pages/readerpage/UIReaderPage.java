package org.ankhzet.ergo.ui.pages.readerpage;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.ankhzet.ergo.ui.LoaderProgressListener;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.pages.readerpage.reader.PageRenderOptions;
import org.ankhzet.ergo.ui.pages.readerpage.reader.Reader;
import org.ankhzet.ergo.ui.pages.readerpage.reader.SwipeHandler;
import org.ankhzet.ergo.chapter.Chapter;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.xgui.CommonControl;
import org.ankhzet.ergo.ui.xgui.XAction;
import org.ankhzet.ergo.ui.xgui.XControls;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIReaderPage extends UIPage {

  static String kPrev = "prevpage";
  static String kNext = "nextpage";
  static String kMagnify = "magnify";
  static String kSwipedir = "swipedir";
  static String kRotate = "rotate";
  static String kOriginal = "original";

  class Dependency {

    Reader reader;
    PageRenderOptions options;
  }

  Dependency di;

  CommonControl pgNext, pgPrev, pgOrigSize, pgMagnify;
  Reader reader;
  PageRenderOptions options;
  boolean swipeDirVertical = false;
  Point pressPos = new Point();

  @Override
  public void navigateIn(Object... params) {
    super.navigateIn(params);
    loadHUD();
    if (params.length > 0 && params[0] != null) {
      Chapter chapter = (Chapter) params[0];
      UILogic.log("loading [%s]:%.1f", chapter.getMangaFolder(), chapter.id());
      reader.loadChapter(chapter);
    }
  }

  void loadHUD() {
    XAction.Action swipeAction = action -> swipePage(action.isA(kPrev) ? -1 : 1);
    XAction.XActionStateListener canSwipeSelector = action -> canSwipe();
    hud.putActionAtLeft("Предыдущая страница", registerAction(kPrev, swipeAction).enabledAs(canSwipeSelector));
    hud.putActionAtLeft("Следующая страница", registerAction(kNext, swipeAction).enabledAs(canSwipeSelector));

    hud.putBackAction(XControls.AREA_LEFT, null);

    hud.putActionAtRight("Увелечительное стекло", registerAction(kMagnify, action -> {
      reader.showMagnifier(!reader.magnifierShown());
    }).togglable((XAction action) -> {
      return reader.magnifierShown();
    }));

    hud.putActionAtRight("Листать вертикально", registerAction(kSwipedir, action -> {
      swipeDirVertical = !swipeDirVertical;
    }).togglable((XAction action) -> {
      return swipeDirVertical;
    }));

    hud.putActionAtRight("Подгонка поворота страниц", registerAction(kRotate, action -> {
      options.toggleRotationToFit();
      reader.flushCache(true);
    }).togglable((XAction action) -> {
      return options.rotateToFit();
    }));

    hud.putActionAtRight("Оригинальный размер", registerAction(kOriginal, action -> {
      options.toggleOriginalSize();
      reader.flushCache(true);
    }).togglable((XAction action) -> {
      return options.showOriginalSize();
    }));
  }

  boolean swipePage(int dir) {
    if (reader == null || reader.totalPages() == 0 || reader.isLoading())
      return false;

    if (!options.showOriginalSize())
      return SwipeHandler.makeSwipe(swipeDirVertical, dir, ui.clientArea.width, ui.clientArea.height);
    else
      if (dir > 0)
        reader.nextPage();
      else
        reader.prevPage();

    return true;
  }

  boolean swiping() {
    return !SwipeHandler.done();
  }

  boolean canSwipe() {
    return !(swiping() || reader.totalPages() == 0 || reader.isLoading());
  }

  @Override
  public void process() {
    ui.intensiveRepaint(swiping());
    reader.process();
  }

  @Override
  public void resized(int x, int y, int w, int h) {
    reader.flushCache(true);
  }

  @Override
  public boolean mouseEvent(MouseEvent e) {
    int mx = e.getX();
    int my = e.getY();

    if (reader.magnifierShown()) {
      reader.mouseEvent(e);
      return true;
    }

    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
      pressPos.setLocation(mx, my);
      mouseDown = true;
      ui.getHUD().unfocus();
    }
    if (options.showOriginalSize() && e.getID() == MouseEvent.MOUSE_DRAGGED) {
      int dx = pressPos.x - mx;
      int dy = pressPos.y - my;
      reader.scroll(dx, dy);
      pressPos.setLocation(mx, my);
    }
    if (mouseDown && e.getID() == MouseEvent.MOUSE_RELEASED) {
      int dx = pressPos.x - mx;
      int dy = pressPos.y - my;
      if (options.showOriginalSize()) {
      } else {
        int dir = swipeDirVertical ? dy : dx;
        if (Math.abs(dir) > 5)//(swipeDirVertical ? clientArea.height : clientArea.width) * 0.1)
          swipePage(dir);
      }
      mouseDown = false;
    }
    return mouseDown;
  }

  @Override
  public void draw(Graphics2D g, int w, int h) {
    reader.draw(g, 0, 0, w, h);
  }

  @Override
  public boolean onProgress(int state, int p, int max) {
    switch (state) {
    case LoaderProgressListener.STATE_CACHING:
      if (reader.flushPending())
        //flushCache = false;
        return false;
      break;
    }
    return true;
  }

  // Dependencies injections
  public Reader diReader(Reader reader) {
    if (reader != null)
      this.reader = reader;
    return this.reader;
  }

  public PageRenderOptions diPageRenderOptions(PageRenderOptions options) {
    if (options != null)
      this.options = options;
    return this.options;
  }
  // ...end injections

}
