package org.ankhzet.ergo.manga;

import java.io.File;
import java.util.List;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Manga extends Chapter {

  public Manga(String path) {
    super(path);
  }

  public String uid() {
    return getName();
  }

  public File rootFolder() {
    return getParentFile();
  }

  @Override
  public File getMangaFile() {
    return this;
  }

  @Override
  public String getMangaFolder() {
    return this.getPath();
  }

  public Bookmark putBookmark(Chapter c) {
    Bookmark last = lastBookmark();
    if (last != null)
      last.delete();
    
    ChapterBookmark b = new ChapterBookmark();
    b.uid = uid();
    b.chapter = c.id();
    b.save();
    return b;
  }

  public boolean hasBookmarks() {
    List<Bookmark> bookmarks = bookmarks();
    return (bookmarks != null) && (bookmarks.size() > 0);
  }

  public Bookmark lastBookmark() {
    List<Bookmark> bookmarks = bookmarks();
    if (bookmarks == null)
      return null;

    int s = bookmarks.size();
    if (s <= 0)
      return null;

    return bookmarks.get(s - 1);
  }

  public List<Bookmark> bookmarks() {
    return Bookmark.forManga(uid(), () -> {
      return new ChapterBookmark();
    });
  }

  public Chapter lastBookmarkedChapter() {
    Bookmark bookmark = lastBookmark();
    return (bookmark == null)
           ? null
           : Chapter.chapterFromBookmark(rootFolder().toPath(), bookmark);
  }

  public String title() {
    return Strings.toTitleCase(uid());
  }

  @Override
  public int hashCode() {
    return getPath().hashCode();
  }

}
