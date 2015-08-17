package org.ankhzet.ergo.ui.pages.reader;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.manga.Manga;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.manga.chapter.page.PageRenderOptions;
import org.ankhzet.ergo.ui.LoaderProgressListener;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.pages.reader.reader.PageNavigator;
import org.ankhzet.ergo.ui.pages.reader.reader.Reader;
import org.ankhzet.ergo.ui.pages.reader.reader.SwipeHandler;
import org.ankhzet.ergo.ui.xgui.CommonControl;
import org.ankhzet.ergo.ui.xgui.XAction;
import org.ankhzet.ergo.ui.xgui.XKeyShortcut;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIReaderPage extends UIPage implements PageNavigator.NavigationListener {

  static String kPrev = "prevpage";
  static String kNext = "nextpage";
  static String kMagnify = "magnify";
  static String kSwipedir = "swipedir";
  static String kRotate = "rotate";
  static String kOriginal = "original";

  @DependencyInjection
  Reader reader;
  @DependencyInjection
  PageRenderOptions options;

  CommonControl pgNext, pgPrev, pgOrigSize, pgMagnify;
  boolean swipeDirVertical = false;
  Point pressPos = new Point();

  int keyScroll = 0;
  long keyPress = 0;

  @Override
  public void navigateIn(Object... params) {
    super.navigateIn(params);
    loadHUD();
    if (params.length > 0 && params[0] != null) {
      Chapter chapter = (Chapter) params[0];
      UILogic.log("loading [%s: %s]:%.1f", chapter.getMangaFolder(), chapter.toString(), chapter.id());
      loadChapter(chapter);
    }
  }

  void loadHUD() {
    XAction.Action swipeAction = action -> swipePage(action.isA(kPrev) ? -1 : 1);
    XAction.XActionStateListener canSwipeSelector = action -> canSwipe();
    hud.putActionAtLeft("Предыдущая страница", registerAction(kPrev, swipeAction).enabledAs(canSwipeSelector))
      .shortcut(XKeyShortcut.press("Left"));
    hud.putActionAtLeft("Следующая страница", registerAction(kNext, swipeAction).enabledAs(canSwipeSelector))
      .shortcut(XKeyShortcut.press("Right"));

    hud.putActionAtRight("Увелечительное стекло", registerAction(kMagnify, action -> {
      reader.showMagnifier(!reader.magnifierShown());
    }).togglable((XAction action) -> {
      return reader.magnifierShown();
    })).shortcut(XKeyShortcut.press("Shift"));

    hud.putActionAtRight("Листать вертикально", registerAction(kSwipedir, action -> {
      swipeDirVertical = !swipeDirVertical;
    }).togglable((XAction action) -> {
      return swipeDirVertical;
    }));

    hud.putActionAtRight("Подгонка поворота страниц", registerAction(kRotate, action -> {
      options.toggleRotationToFit();
      reader.flushLayout();
    }).togglable((XAction action) -> {
      return options.rotateToFit();
    }));

    hud.putActionAtRight("Оригинальный размер", registerAction(kOriginal, action -> {
      options.toggleOriginalSize();
      reader.flushLayout();
    }).togglable((XAction action) -> {
      return options.showOriginalSize();
    }));

    hud.shortcut("Scroll up", XKeyShortcut.press("Up"), registerAction("scroll-up", action -> scroll(-1)));
    hud.shortcut("Scroll down", XKeyShortcut.press("Down"), registerAction("scroll-down", action -> scroll(1)));
  }

  void bookmark(Chapter c) {
    Manga m = c.getManga();
    if (m != null)
      m.putBookmark(c);
  }

  void loadChapter(Chapter c) {
    reader.loadChapter(c);
  }

  boolean swipePage(int dir) {
    if (!canSwipe())
      return false;

    if (!options.showOriginalSize())
      return SwipeHandler.makeSwipe(swipeDirVertical, dir, ui.clientArea.width, ui.clientArea.height);
    else
      reader.changePage(dir < 0);

    return true;
  }

  boolean swiping() {
    return !SwipeHandler.done();
  }

  boolean canSwipe() {
    return !(swiping() || reader == null || reader.totalPages() == 0 || reader.isLoading());
  }

  @Override
  public void process() {
    ui.intensiveRepaint(swiping());
    reader.process();

    if (keyScroll != 0)
      reader.scroll(0, currentScrollSpeed());
  }

  void scroll(int delta) {
    keyScroll = (20 * delta + currentScrollSpeed()) / 2;
    keyPress = System.currentTimeMillis();
  }

  int currentScrollSpeed() {
    if (keyScroll == 0)
      return 0;
    
    long scroll = (System.currentTimeMillis() - keyPress);
    double d = 1. - Math.min(1., scroll / 500.);

    if (d <= 0.01)
      keyScroll = 0;

    return (int) (keyScroll * d);
  }

  @Override
  public void resized(int x, int y, int w, int h) {
    reader.resized(x, y, w, h);
  }

  @Override
  public boolean mouseEvent(MouseEvent e) {
    if (reader.mouseEvent(e))
      return true;

    int mx = e.getX();
    int my = e.getY();

    int dx = pressPos.x - mx;
    int dy = pressPos.y - my;
    switch (e.getID()) {
    case MouseEvent.MOUSE_PRESSED:
      pressPos.setLocation(mx, my);
      mouseDown = true;
      hud.unfocus();
      break;
    case MouseEvent.MOUSE_DRAGGED:
      if (!options.showOriginalSize())
        break;
      reader.scroll(dx, dy);
      pressPos.setLocation(mx, my);
      break;
    case MouseEvent.MOUSE_RELEASED:
      if (!mouseDown)
        break;

      if (!options.showOriginalSize()) {
        boolean vertSwipe = Math.abs(dy) > Math.abs(dx);
        if (vertSwipe == swipeDirVertical) {
          int dir = swipeDirVertical ? dy : dx;
          if (Math.abs(dir) > 5)//(swipeDirVertical ? clientArea.height : clientArea.width) * 0.1)
            swipePage(dir);
        }
      }
      mouseDown = false;
      break;
    }

    return mouseDown;
  }

  @Override
  public void draw(Graphics2D g, int w, int h) {
    reader.draw(g, 0, 0, w, h);
  }

  @DependenciesInjected(suppressInherited = false, beforeInherited = false)
  private void diInjected() {
    reader.setNavListener(this);
  }

  @Override
  public void pageSet(int requested, int set) {
    keyScroll = 0;
    Chapter current = reader.chapter();
    if (requested != set) {
      Chapter load = current.seekChapter(requested > set);
      if (!load.equals(current))
        loadChapter(load);
      else {
        String dir = (requested > set) ? "last" : "first";
        ui.message(String.format("This is %s available chapter (%s >> %s)", dir, current.getMangaFolder(), current.idShort()));
      }

    } else {
      boolean b = requested > 0;
      if (!b) {
        File m = current.getMangaFile();
        b = (new Manga(m.getPath())).hasBookmarks();
        if (!b) {
          Path chPath = m.toPath().resolve(new Chapter("0.1").idLong());
          current = new Chapter(chPath.toString());
          b = true;
        } else
          b = false;
      }

      if (b)
        bookmark(current);
    }
  }

  @Override
  public String title() {
    Chapter current = reader.chapter();
    if (current == null)
      return "Read chapter";

    String manga = Strings.toTitleCase(current.getMangaFolder());
    String chapter = current.idShort();
    String last = current.lastChapter().idShort();

    return String.format("%s [%s/%s]", manga, chapter, last);
  }

}
