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

  MangaOptions options;

  List<Bookmark> bookmarksCache;

  public Manga(String path) {
    super(path);
  }

  @Override
  public boolean valid() {
    return allChapters().length > 0;
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

  public MangaOptions options() {
    if (options == null)
      options = new MangaOptions(this);
    return options;
  }

  public Bookmark putBookmark(Chapter c) {
    List<Bookmark> bookmarks = bookmarks();
    if (bookmarks.size() > 0) {
      ChapterBookmark last = (ChapterBookmark) bookmarks.get(bookmarks.size() - 1);
      if (last.uid.equals(uid()) && (Float.compare(last.chapter, c.id()) == 0))
        return last;
      
      bookmarks.remove(last);
    }

    ChapterBookmark b = new ChapterBookmark();
    b.uid = uid();
    b.chapter = c.id();
    b.save();

    bookmarks.add(b);

    long timestamp = System.currentTimeMillis();
    c.setLastModified(timestamp);
    c.getMangaFile().setLastModified(timestamp);
    return b;
  }

  public boolean hasBookmarks() {
    List<Bookmark> bookmarks = bookmarks();
    return (bookmarks != null) && (bookmarks.size() > 0);
  }

  public Bookmark lastBookmark() {
    List<Bookmark> bookmarks = bookmarks();

    int s = bookmarks.size();
    if (s <= 0)
      return null;

    return bookmarks.get(s - 1);
  }

  public List<Bookmark> bookmarks() {
    if (bookmarksCache == null)
      bookmarksCache = Bookmark.forManga(uid(), () -> {
        return new ChapterBookmark();
      });

    return bookmarksCache;
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

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    final Manga other = (Manga) obj;

    return other.hashCode() == hashCode();
  }

}
