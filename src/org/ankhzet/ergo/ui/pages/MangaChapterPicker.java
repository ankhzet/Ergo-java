package org.ankhzet.ergo.ui.pages;

import java.io.File;
import org.ankhzet.ergo.manga.Bookmark;
import org.ankhzet.ergo.manga.Manga;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker.FilesList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MangaChapterPicker extends XPathFilePicker {

  boolean skipReaded = true;

  public MangaChapterPicker(String caption) {
    super(caption);
  }

  public boolean skipReaded() {
    return skipReaded;
  }

  public void setSkipReaded(boolean skip) {
    if (skipReaded != skip) {
      skipReaded = skip;
      fetchRoot();
    }
  }

  @Override
  protected void fetchRoot() {
    super.fetchRoot();

    FilesList bookmarked = new FilesList();
    FilesList unread = new FilesList();
    FilesList files = new FilesList();

    for (File entry : entries)
      if (entry.isDirectory()) {
        Manga m = new Manga(entry.getName());
        Bookmark b = m.lastBookmark();
        if (b != null) {
          if (skipReaded) {
            Chapter c = new Chapter(entry.getPath());
            c = c.lastChapter();
            if (Chapter.chapterFromBookmark(null, b).compare(c) >= 0)
              continue;
          }

          bookmarked.add(entry);
        } else
          unread.add(entry);
      } else
        files.add(entry);

    entries.clear();
    entries.addAll(bookmarked);
    entries.addAll(unread);
    entries.addAll(files);
  }

}
