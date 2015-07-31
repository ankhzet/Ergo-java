package org.ankhzet.ergo.ui.pages.home;

import java.awt.Graphics2D;
import java.io.File;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.manga.Manga;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.pages.duplicates.UIDuplicatesPage;
import org.ankhzet.ergo.ui.pages.reader.UIReaderPage;
import org.ankhzet.ergo.ui.pages.reader.reader.Reader;
import org.ankhzet.ergo.ui.xgui.XButton;
import org.ankhzet.ergo.ui.xgui.XControls;
import org.ankhzet.ergo.ui.xgui.XKeyShortcut;

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
      Manga manga = selectedManga();
      if (manga == null)
        return;

      Chapter chapter = manga.lastBookmarkedChapter();
      Chapter next = chapter.seekChapter(true);
      if (next == null)
        next = chapter;
      ui.navigateTo(UIReaderPage.class, next);
    })).enabledAs(action -> {
      Manga manga = selectedManga();
      if (manga == null)
        return false;

      Chapter c = manga.lastBookmarkedChapter();

      XButton cntButton = (XButton) hud.getControl(action);
      if (c != null)
        cntButton.setCaption(String.format("Continue from: %s [%s]", manga.title(), c.idShort()));
      else
        cntButton.setCaption(kContinueLabel);

      return c != null;
    });

    hud.putSpacer(XControls.AREA_LEFT);

    hud.putActionAtLeft("Filter duplicates", registerAction("dups", action -> {
      ui.navigateTo(UIDuplicatesPage.class, picker.getSelectedPath());
    })).enabledAs(action -> {
      return picker.hasSelected();
    });

    hud.getControl(
      hud.putActionAtLeft("Mark as readed", registerAction("readed", action -> {
        Manga manga = selectedManga();
        if (manga == null)
          return;

        Chapter c = manga.lastChapter();
        if (c != null)
          if (manga.putBookmark(c) != null) {
            ui.message(String.format("Manga marked as readed: %s\n", manga.title()));
            picker.fetchRoot();
          } else
            ui.message(String.format("Failed to put bookmark for \"%s\" [%s]!", manga.title(), c.idShort()));
      })).enabledAs(action -> {
        return hasMangaSelected();
      }).shortcut(XKeyShortcut.press("Shift+R"))
    ).setVisible(false);

    hud.getControl(
      hud.putActionAtLeft("Refresh", registerAction("refresh", action -> picker.fetchRoot()))
        .shortcut(XKeyShortcut.press("F5"))
    ).setVisible(false);

    hud.add(picker);
  }

  Chapter pickedChapter() {
    return new Chapter(picker.getSelectedPath());
  }

  Manga selectedManga() {
    Chapter c = pickedChapter();
    if (c.allChapters().length == 0)
      return null;

    return new Manga(c.getMangaFile().getPath());
  }

  boolean hasMangaSelected() {
    return pickedChapter().allChapters().length > 0;
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
    picker.move(0, UILogic.UIPANEL_HEIGHT, w, h - UILogic.UIPANEL_HEIGHT);
  }

  @Override
  public String title() {
    return "Ergo manga reader";
  }

}