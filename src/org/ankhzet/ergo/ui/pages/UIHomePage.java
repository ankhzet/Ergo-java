package org.ankhzet.ergo.ui.pages;

import org.ankhzet.ergo.ui.pages.readerpage.UIReaderPage;
import java.awt.Graphics2D;
import java.io.File;
import org.ankhzet.ergo.ui.pages.readerpage.reader.Reader;
import org.ankhzet.ergo.chapter.Chapter;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.ui.xgui.XAction;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIHomePage extends UIPage {

  static final String kLoadChapterAction = "load";
  static final String kLoadChapterLabel = "Load chapter";

  @DependencyInjection
  XPathFilePicker picker;
  @DependencyInjection
  Reader reader;

  @DependenciesInjected
  private void di() {
    fetchMangas();
  }
  
  @Override
  public void navigateIn(Object... params) {
    super.navigateIn(params);
    hud.putActionAtLeft(kLoadChapterLabel, registerAction(kLoadChapterAction, action -> {
      loadChapter();
    }).enabledAs(action -> {
      return !(!picker.hasSelected() || reader.isBusy());
    }));
    
    hud.putActionAtLeft("Filter duplicates", registerAction("dups", action -> {
      ui.navigateTo(UIDuplicatesPage.class, picker.getSelectedPath());
    })).enabledAs(action -> {
      return picker.hasSelected();
    });
    
    hud.add(picker);
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

}
