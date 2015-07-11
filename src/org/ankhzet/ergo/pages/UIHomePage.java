package org.ankhzet.ergo.pages;

import java.awt.Graphics2D;
import java.io.File;
import org.ankhzet.ergo.UIPage;
import org.ankhzet.ergo.reader.Reader;
import org.ankhzet.ergo.reader.chapter.Chapter;
import org.ankhzet.ergo.xgui.XAction;
import org.ankhzet.ergo.xgui.XPathFilePicker;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIHomePage extends UIPage {

  static final String kLoadChapterAction = "load";
  static final String kLoadChapterLabel = "Load chapter";

  XPathFilePicker picker;
  Reader reader;

  @Override
  public void navigateIn(Object... params) {
    super.navigateIn(params);
    hud.putActionAtLeft(kLoadChapterLabel, registerAction(kLoadChapterAction, action -> {
      loadChapter();
    }).enabledAs(action -> {
      return !(!picker.hasSelected() || reader.isBusy());
    }));
    hud.add(picker);
  }

  @Override
  public boolean actionPerformed(XAction a) {
    boolean handled = true;
    if (a.isA(kLoadChapterAction))
      loadChapter();
    else
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
