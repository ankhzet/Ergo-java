package org.ankhzet.ergo.chapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.ankhzet.ergo.utils.Strings;
import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Chapter extends File {

  public static final String PAGE_PATTERN = "^.*?\\.(png|jpe?g|gif|bmp)";

  protected Strings pageFiles;

  public Chapter(String path) {
    super(path);
  }

  public String getMangaFolder() {
    File parent = getParentFile();
    return parent.getName();
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

  public Chapter[] allChapters() {
    String mangaPath = getParent();

    ArrayList<Chapter> chapters = new ArrayList<>();
    String[] chapterNames = list();
    for (String chapterName : chapterNames)
      chapters.add(new Chapter(mangaPath + File.separator + chapterName));

    return (Chapter[]) chapters.toArray();
  }

  public Chapter seekChapter(boolean forward) {
    ArrayList<Chapter> list = new ArrayList<>(Arrays.asList(allChapters()));

    int i = list.indexOf(this);

    int index = Utils.constraint(i + (forward ? 1 : -1), 0, list.size() - 1);
    return list.get(index);
  }

  public float id() {
    return Float.parseFloat(getName());
  }

  public int idx() {
    return (int) (id() * 10);
  }

  public int compare(Chapter chapter) {
    return Integer.signum(idx() - chapter.idx());
  }

}
