package org.ankhzet.ergo.ui.pages;

import java.io.File;
import org.ankhzet.ergo.manga.Manga;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker.FilesList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MangaChapterPicker extends XPathFilePicker {

  boolean skipReaded = false;

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
    FilesList read = new FilesList();
    FilesList unread = new FilesList();
    FilesList files = new FilesList();

    for (File entry : entries)
      if (entry.isDirectory()) {
        Manga m = new Manga(entry.getPath());
        Chapter b = m.lastBookmarkedChapter();
        if (b != null) {
          Chapter c = m.lastChapter();
          if (b.compare(c) >= 0) {
            if (!skipReaded)
              read.add(m);
          } else
            bookmarked.add(m);
        } else
          unread.add(entry);
      } else
        files.add(entry);

    entries.clear();
    entries.addAll(bookmarked);
    entries.addAll(unread);
    entries.addAll(read);
    entries.addAll(files);
  }

}
