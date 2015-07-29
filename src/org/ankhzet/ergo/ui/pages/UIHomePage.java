package org.ankhzet.ergo.ui.pages;

import java.awt.Graphics2D;
import java.io.File;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.manga.Bookmark;
import org.ankhzet.ergo.manga.Manga;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.pages.readerpage.UIReaderPage;
import org.ankhzet.ergo.ui.pages.readerpage.reader.Reader;
import org.ankhzet.ergo.ui.xgui.XButton;
import org.ankhzet.ergo.ui.xgui.XControls;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIHomePage extends UIPage {

  static final String kLoadChapterAction = "load";
  static final String kLoadChapterLabel = "Load chapter";

  static final String kContinueAction = "continue";
  static final String kContinueLabel = "Continue";

  @DependencyInjection
  MangaChapterPicker picker;
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
      if (reader.isBusy())
        return false;

      return pickedChapter().valid();
    }));

    hud.putActionAtLeft(kContinueLabel, registerAction(kContinueAction, action -> {
      Chapter c = new Chapter(picker.getSelectedPath());
      Manga manga = new Manga(c.getMangaFolder());
      Bookmark bookmark = manga.lastBookmark();
      Chapter chapter = Chapter.chapterFromBookmark(c.toPath().getParent(), bookmark);
      Chapter next = chapter.seekChapter(true);
      if (next == null)
        next = chapter;
      ui.navigateTo(UIReaderPage.class, next);
    })).enabledAs(action -> {
      Chapter c = pickedChapter();
      Manga manga = new Manga(c.getMangaFolder());
      Bookmark b = manga.lastBookmark();
      XButton cntButton = (XButton) hud.getControl(action);
      if (cntButton != null)
        if (b != null)
          cntButton.setCaption("Continue from: " + b.path(null).toString());
        else
          cntButton.setCaption(kContinueLabel);
      return b != null;
    });

    hud.putSpacer(XControls.AREA_LEFT);

    hud.putActionAtLeft("Filter duplicates", registerAction("dups", action -> {
      ui.navigateTo(UIDuplicatesPage.class, picker.getSelectedPath());
    })).enabledAs(action -> {
      return picker.hasSelected();
    });

    hud.add(picker);
  }

  Chapter pickedChapter() {
    return new Chapter(picker.getSelectedPath());
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

  @Override
  public String title() {
    return "Ergo manga reader";
  }

}
