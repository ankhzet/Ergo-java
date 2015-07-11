package org.ankhzet.ergo.pages;

import java.awt.Graphics2D;
import org.ankhzet.ergo.UILogic;
import java.io.File;
import org.ankhzet.ergo.UIPage;
import org.ankhzet.ergo.reader.Reader;
import org.ankhzet.ergo.xgui.CommonControl;
import org.ankhzet.ergo.xgui.XButton;
import org.ankhzet.ergo.xgui.XControls;
import org.ankhzet.ergo.reader.chapter.Chapter;
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
  public void navigateIn(Object... params) {
    super.navigateIn(params);
    XControls hud = ui.getHUD();
    hud.clear();
    pgLoad = hud.putControl(new XButton("load", "Загрузить главу", "xbutton"), XControls.AREA_LEFT);
    hud.add(picker);
  }

  @Override
  public boolean actionPerformed(String a) {
    boolean handled = true;
    if (a.equals("load")) {
      loadChapter();
    } else
      handled = false;

    return handled;
  }

  public boolean loadChapter() {
    String path = picker.getSelectedPath();
    File fsPath = new File(path);

    if (!fsPath.isDirectory())
      fsPath = fsPath.getParentFile();
    if ((fsPath == null) || !fsPath.isDirectory())
      return false;

    Chapter chapter = new Chapter(fsPath.getPath());
    ui.navigateTo(UIReaderPage.class, chapter);
    return true;
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
