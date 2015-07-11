package org.ankhzet.ergo.pages;

import java.awt.Graphics2D;
import org.ankhzet.ergo.UILogic;
import org.ankhzet.ergo.UIPage;
import org.ankhzet.ergo.reader.Reader;
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
  
  XPathFilePicker picker;
  Reader reader;

  @Override
    XControls hud = ui.getHUD();
    hud.clear();
    pgLoad = hud.putControl(new XButton("load", "Загрузить главу", "xbutton"), XControls.AREA_LEFT);
  public void navigateIn(Object... params) {
    super.navigateIn(params);
    hud.add(picker);
  }

  @Override
  public boolean actionPerformed(String a) {
    boolean handled = true;
    if (a.equals("load")) {
      String path = picker.getSelectedPath();
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
    UIReaderPage page = (UIReaderPage) ui.navigateTo(UIReaderPage.class);
    page.loadChapter(manga, chapter);
  }

  @Override
  public void process() {
    pgLoad.enabled = !(!picker.hasSelected() || reader.isBusy());
  }

  @Override
  public void draw(Graphics2D g, int w, int h) {
  }

  private void fetchMangas() {
    picker.setList(reader.getMangaRoots());
  }

  @Override
  public void resized(int x, int y, int w, int h) {
    picker.move(0, y, w, h - y);
  }

  // Dependencies injections
  public Reader diReader(Reader reader) {
    if (reader != null) {
      this.reader = reader;
      if (this.picker != null)
        fetchMangas();
    }
    return this.reader;
  }

  public XPathFilePicker diXPathFilePicker(XPathFilePicker picker) {
    if (picker != null) {
      this.picker = picker;
      if (this.reader != null)
        fetchMangas();
    }
    return this.picker;
  }
  // ...end injections
  
}
