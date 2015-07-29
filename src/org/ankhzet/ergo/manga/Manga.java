
package org.ankhzet.ergo.manga;

import java.util.List;
import org.ankhzet.ergo.manga.chapter.Chapter;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Manga {
  
  protected String folder;

  public Manga(String folder) {
    this.folder = folder;
  }
  
  public String uid() {
    return folder;
  }
  
  public Bookmark putBookmark(Chapter c) {
    ChapterBookmark b = new ChapterBookmark();
    b.uid = c.getMangaFolder();
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
}

