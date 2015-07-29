package org.ankhzet.ergo.manga.chapter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ankhzet.ergo.manga.Bookmark;
import org.ankhzet.ergo.utils.Strings;
import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Chapter extends File {

  public static final String PAGE_PATTERN = "^.*?\\.(png|jpe?g|gif|bmp)";
  public static final String CHAP_PATTERN = "^[\\d\\.]+";

  protected Strings pageFiles;

  public Chapter(String path) {
    super(path);
  }
  
  public static Chapter chapterFromBookmark(Path root, Bookmark bookmark) {
    return new Chapter(bookmark.path(root).toString());
  }

  public File getMangaFile() {
    File parent = valid() ? getParentFile() : this;
    return parent;
  }

  public String getMangaFolder() {
    return getMangaFile().getName();
  }

  public Strings fetchPages() {
    if (pageFiles != null)
      return pageFiles;

    pageFiles = new Strings();
    String path = getPath();
    String[] files = list((dir, name) -> name.toLowerCase().matches(PAGE_PATTERN));
    for (String fileName : files)
      pageFiles.add(path + File.separator + fileName);

    return pageFiles;
  }

  public Chapter firstChapter() {
    List<Chapter> list = Arrays.<Chapter>asList(allChapters());
    return (list.size() > 0) ? list.get(0) : null;
  }

  public Chapter lastChapter() {
    List<Chapter> list = Arrays.<Chapter>asList(allChapters());
    return (list.size() > 0) ? list.get(list.size() - 1) : null;
  }

  public Chapter[] allChapters() {
    File manga = getMangaFile();

    ArrayList<Chapter> chapters = new ArrayList<>();
    File[] chapterNames = manga.listFiles((dir, name) -> name.matches(CHAP_PATTERN));
    if (chapterNames != null)
      for (File chapter : chapterNames)
        chapters.add(new Chapter(chapter.getPath()));

    return chapters.toArray(new Chapter[] {});
  }
  
  public Chapter seekChapter(boolean forward) {
    List<Chapter> list = Arrays.<Chapter>asList(allChapters());

    int i = list.indexOf(this);

    int index = Utils.constraint(i + (forward ? 1 : -1), 0, list.size() - 1);
    return list.get(index);
  }
  
  public String idShort() {
    return isBonus() ? String.format("%.1f", idx() / 10.f) : String.format("%d", idx() / 10);
  }

  public float id() {
    try {
      return Float.parseFloat(getName());
    } catch (Exception e) {
      return 0.f;
    }
  }

  public int idx() {
    return (int) (id() * 10);
  }
  
  public boolean isBonus() {
    return (idx() % 10) > 0;
  }
  
  public boolean valid() {
    return idx() > 0;
  }

  public int compare(Chapter chapter) {
    return Integer.signum(idx() - chapter.idx());
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Chapter))
      return false;
    
    return idx() == ((Chapter)o).idx();
  }

  @Override
  public int hashCode() {
    return idx();
  }

}
