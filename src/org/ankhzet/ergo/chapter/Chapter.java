package org.ankhzet.ergo.chapter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    File manga = getParentFile();
    Path mangaPath = manga.toPath();

    ArrayList<Chapter> chapters = new ArrayList<>();
    String[] chapterNames = manga.list((dir, name) -> name.matches(CHAP_PATTERN));
    for (String chapterName : chapterNames)
      chapters.add(new Chapter(mangaPath.resolve(chapterName).toString()));

    return chapters.toArray(new Chapter[] {});
  }

  public Chapter seekChapter(boolean forward) {
    List<Chapter> list = Arrays.<Chapter>asList(allChapters());

    int i = list.indexOf(this);

    int index = Utils.constraint(i + (forward ? 1 : -1), 0, list.size() - 1);
    return list.get(index);
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

  public int compare(Chapter chapter) {
    return Integer.signum(idx() - chapter.idx());
  }

}
