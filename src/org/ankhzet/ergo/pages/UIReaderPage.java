package org.ankhzet.ergo.pages;

import org.ankhzet.ergo.reader.chapter.ChapterLoader;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import org.ankhzet.ergo.reader.PageRenderOptions;
import org.ankhzet.ergo.LoaderProgressListener;
import org.ankhzet.ergo.UILogic;
import org.ankhzet.ergo.UIPage;
import org.ankhzet.ergo.reader.Reader;
import org.ankhzet.ergo.reader.SwipeHandler;
import org.ankhzet.ergo.xgui.CommonControl;
import org.ankhzet.ergo.xgui.XButton;
import org.ankhzet.ergo.xgui.XControls;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIReaderPage extends UIPage {

  static String kPrev = "prevpage";
  static String kNext = "nextpage";
  static String kBack = "back";
  static String kMagnify = "magnify";
  static String kSwipedir = "swipedir";
  static String kRotate = "rotate";
  static String kOriginal = "original";

  private interface Action {

    public void perform(String name);
  }

  CommonControl pgNext, pgPrev, pgOrigSize, pgMagnify;
  Reader reader = null;
  boolean swipeDirVertical = true;
  ChapterLoader loader;
  UILogic ui;
  Point pressPos = new Point();


  public void injectDependencies(UILogic ui, Reader reader, ChapterLoader loader, PageRenderOptions options) {
    this.ui = ui;
    this.reader = reader;
    this.loader = loader;
    this.options = options;
  }

  @Override
  public void navigateIn() {
    super.navigateIn();
    XControls hud = ui.getHUD();
    hud.clear();
    pgPrev = hud.putControl(new XButton(kPrev, "Предыдущая страница", "xbutton"), XControls.AREA_LEFT);
    pgNext = hud.putControl(new XButton(kNext, "Следующая страница", "xbutton"), XControls.AREA_RIGHT);

    hud.putControl(new XButton(kBack, "Назад", "xbutton"), XControls.AREA_LEFT);

    pgMagnify = hud.putControl(new XButton(kMagnify, "Увелечительное стекло", "xbutton"), XControls.AREA_RIGHT);
    hud.putControl(new XButton(kSwipedir, "Направление листания", "xbutton"), XControls.AREA_RIGHT);
    hud.putControl(new XButton(kRotate, "Режим подгонки страниц", "xbutton"), XControls.AREA_RIGHT);
    pgOrigSize = hud.putControl(new XButton(kOriginal, "Оригинальный размер", "xbutton"), XControls.AREA_RIGHT);

    registerAction(kPrev, (String name) -> swipePage(-1));
    registerAction(kNext, (String name) -> swipePage(1));

    registerAction(kSwipedir, (String name) -> {
      swipeDirVertical = !swipeDirVertical;
    });
    registerAction(kRotate, (String name) -> {
      reader.options.toggleRotationToFit();
      if (loader != null)
        reader.flushCache(true);
    });

    registerAction(kOriginal, (String name) -> {
       reader.options.toggleOriginalSize();
        if (loader != null)
          reader.flushCache(true);
    });

    registerAction(kMagnify, (String name) -> {
      reader.showMagnifier(!reader.getMagnifying());
    });

  }

  HashMap<String, Action> actions = new HashMap<>();

  void registerAction(String actionName, Action action) {
//    for (String name : actionName)
    actions.put(actionName, action);
  }

  Action performAction(String a) {
    Action action = actions.get(a);
    try {
      action.perform(a);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return action;
  }

  @Override
  public boolean actionPerformed(String a) {
    boolean handled = performAction(a) != null;
    if (!handled) {
      
    }
    
    return handled;
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

  @Override
  public void process() {
    boolean swiping = !SwipeHandler.done();
    boolean canSwipe = !(swiping || reader.totalPages() == 0 || reader.isLoading());
    pgNext.enabled = canSwipe;
    pgPrev.enabled = canSwipe;
    pgOrigSize.toggled = options.showOriginalSize();
    pgMagnify.toggled = reader.getMagnifying();
    ui.intensiveRepaint(swiping);
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

    if (reader.getMagnifying()) {
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

  public void loadChapter(String manga, int chapter) {
    loader.load(manga, chapter);
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
  
}
