package org.ankhzet.ergo;

import java.awt.Graphics2D;
import org.ankhzet.ergo.ClassFactory.IoC;
import org.ankhzet.ergo.reader.Reader;
import org.ankhzet.ergo.reader.UIReaderPage;
import org.ankhzet.ergo.xgui.CommonControl;
import org.ankhzet.ergo.xgui.XButton;
import org.ankhzet.ergo.xgui.XControls;
import org.ankhzet.ergo.xgui.XPathFilePicker;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIHomePage extends UIPage {

  CommonControl pgLoad;

  public UIHomePage() {
    fetchMangas();
  }

  UILogic ui() {
    return IoC.<UILogic>get(UILogic.class);
  }

  XPathFilePicker picker() {
    return IoC.get(XPathFilePicker.class);
  }


  @Override
  public void navigateIn() {
    super.navigateIn();
    XControls hud = ui().hud;
    hud.clear();
    pgLoad = hud.putControl(new XButton("load", "Загрузить главу", "xbutton"), XControls.AREA_LEFT);
    hud.add(picker());
  }

  @Override
  public boolean actionPerformed(String a) {
    boolean handled = true;
    if (a.equals("load")) {
      String path = picker().getSelectedPath();
      String[] parts = path.replace('\\', '/').split("/");
      int l = parts.length - 1;
      if (l < 0)
        return true;

      if (parts[l].isEmpty())
        l--;

      if (parts[l].matches(".*\\.(png|gif|bmp|jpe?g)"))
        l--;

      int chapter = 1;
      try {
        chapter = Integer.parseInt(parts[l]);
        l--;
      } catch (Exception e) {
      }


      UILogic.log("loading [%s]:%d", parts[l], chapter);
      loadChapter(parts[l], chapter);

    } else
      handled = false;

    return handled;
  }

  void loadChapter(String manga, int chapter) {
    UIReaderPage page = (UIReaderPage) ui().navigateTo(UIReaderPage.class);
    page.loadChapter(manga, chapter);
  }

  @Override
  public void process() {
    pgLoad.enabled = !(!picker().hasSelected() || Reader.get().isBusy());
  }

  @Override
  public void draw(Graphics2D g, int w, int h) {
  }

  private void fetchMangas() {
    picker().setList(Reader.get().getMangaRoots());
  }

  @Override
  public void resized(int x, int y, int w, int h) {
    picker().move(0, y, w, h - y);
  }
}
