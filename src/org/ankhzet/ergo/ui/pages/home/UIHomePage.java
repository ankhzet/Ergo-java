package org.ankhzet.ergo.ui.pages.home;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
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
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIHomePage extends UIPage {

  static final String kLoadChapterAction = "load";
  static final String kLoadChapterLabel = "Load chapter";

  static final String kContinueAction = "continue";
  static final String kContinueLabel = "Read";

  @DependencyInjection
  MangaChapterPicker picker;
  @DependencyInjection
  Reader reader;

  Chapter chapter;
  
  @DependenciesInjected
  private void di() {
    String root = "H:/manga/manga";
    reader.setMangaRoots(new Strings(new String[]{root}));
    picker.setRoot(root);
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

      Chapter next, bookmark = manga.lastBookmarkedChapter();
      if (bookmark == null)
        next = manga.firstChapter();
      else {
        next = bookmark.seekChapter(true);
        if (next == null)
          next = chapter;
      }

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

      return manga.valid();
    });

    hud.putSpacer(XControls.AREA_LEFT);

    hud.putActionAtLeft("Filter duplicates", registerAction("dups", action -> {
      ui.navigateTo(UIDuplicatesPage.class, picker.getSelectedPath());
    })).enabledAs(action -> {
      return picker.hasSelected();
    });

    hud.shortcut("Mark as readed", XKeyShortcut.press("Shift+R"), registerAction("readed", action -> {
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
    });

    hud.shortcut("Refresh", XKeyShortcut.press("F5"), registerAction("refresh", action -> picker.fetchRoot()));
    hud.shortcut("Open folder in folder browser", XKeyShortcut.press("Ctrl+E"), registerAction("browse-folder", action -> {
      try {
        Desktop.getDesktop().open(picker.getSelectedFile());
      } catch (IOException ex) {

      }
    })).enabledAs(action -> {
      return hasMangaSelected();
    });

    hud.add(picker);
  }

  Chapter pickedChapter() {
    Chapter t = new Chapter(picker.getSelectedPath());
    
    if ((chapter == null) || !chapter.equals(t))
      chapter = t;
    
    return chapter;
  }

  Manga selectedManga() {
    Chapter c = pickedChapter();
    return (c != null) ? c.getManga() : null;
  }

  boolean hasMangaSelected() {
    Manga m = selectedManga();
    return (m != null) && m.valid();
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
    Manga m = selectedManga();
    if (m != null)
      return Strings.toTitleCase(m.getName());
    
    return "Ergo manga reader";
  }

}
