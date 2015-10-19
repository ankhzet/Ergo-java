package org.ankhzet.ergo.ui.pages.home;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import ankh.annotations.DependencyInjection;
import org.ankhzet.ergo.manga.Manga;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.Skin;
import org.ankhzet.ergo.ui.pages.reader.reader.Reader;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker;
import org.ankhzet.ergo.ui.xgui.filepicker.CollumnedItemVisitor;
import org.ankhzet.ergo.ui.xgui.filepicker.FilesList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MangaChapterPicker extends XPathFilePicker {

  @DependencyInjection()
  protected Reader reader;

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
  public int itemHeight() {
    return 25;
  }

  @Override
  protected void fetchRoot() {
    super.fetchRoot();
    entries.remove(upFolderFile());

    boolean inRootFolder = false;
    File root = getRootFile();
    for (String r : reader.getMangaRoots())
      if (root.equals(new File(r))) {
        inRootFolder = true;
        break;
      }

    FileFetcher fetcher = inRootFolder ? new MangaFetcher() : new ChapterFetcher();
    FilesList fetched = fetcher.fetch(entries, this);

    entries.clear();
    entries.add(upFolderFile());
    entries.addAll(fetched);
  }

  @Override
  public void drawItems(Graphics2D g) {
    Shape clip = g.getClip();
    int fontHeight = g.getFont().getSize();

    CollumnedItemVisitor.NodeVisitor<File> nodeVisitor = (Rectangle r, File item) -> {

      if (!clip.intersects(r))
        return false;

      boolean isHilited = (higlited == item);// && item.equals(higlited);
      boolean isSelected = (selected == item);// && item.equals(selected);
      boolean isAiming = (aiming == item);// && item.equals(aiming);
      boolean showBtn = isAiming && item.isDirectory();

      g.setClip(clip);
      g.clipRect(r.x, r.y, r.width, r.height + 1);

      r = (Rectangle) r.clone();
      r.grow(-1, -1);

      int btnWidth = r.height;
      int tw = r.width - (showBtn ? btnWidth : 0) - 1;

      if (isHilited) {
        g.setColor(Color.GRAY);
        Skin.fillBevel(g, r.x + 1, r.y + 1, r.width - 1, r.height - 1);
      }

      Skin.drawBevel(g, r.x, r.y, tw, r.height);

      if (showBtn) {
        Skin.drawBevel(g, r.x + tw, r.y, btnWidth, r.height);
        r.grow(-btnWidth / 2, 0);
        r.translate(-btnWidth / 2, 0);
      }

      r.grow(-8, 0);
      g.clipRect(r.x, r.y, r.width, r.height + 1);
      g.setColor(Color.LIGHT_GRAY);
      g.drawString(itemCaption(item), r.x, 1 + r.y + fontHeight + (r.height - fontHeight) / 2 - 1);

      Color c;
      if (isSelected)
        c = Color.BLUE;
      else
        c = cachedColor(item);

      g.setColor(c);
      g.drawString(itemCaption(item), r.x, r.y + fontHeight + (r.height - fontHeight) / 2 - 1);

      return false;
    };

    CollumnedItemVisitor<File> v = itemVisitor(w, h);
    v.walkItems(entries, nodeVisitor);
  }

  HashMap<File, Color> cachedColors = new HashMap<>();

  Color cachedColor(File item) {
    Color c = cachedColors.get(item);
    if (c == null) {
      if (item instanceof Chapter)
        c = Color.MAGENTA;
      else
        c = Color.BLACK;
      cachedColors.put(item, c);
    }
    return c;
  }

}

class FileFetcher {

  public FilesList fetch(FilesList entries, MangaChapterPicker picker) {
    FileFilter filter = filter(picker);

    LinkedHashMap<Bucket, FilesList> map = filterFiles(entries, filter);

    FilesList result = new FilesList();
    ArrayList<Bucket> buckets = new ArrayList<>(map.keySet());
    buckets.sort(null);
    buckets.forEach((bucket) -> {
      result.addAll(sortEntries(map.get(bucket)));
    });

    return result;
  }

  enum Bucket {

    SKIP,
    BOOKMARKED,
    UNREAD,
    READ,
    FILE,

  }

  interface FileFilter {

    Bucket filter(File f);

  }

  protected FilesList sortEntries(FilesList list) {
    list = (FilesList) list.clone();
    list.sort(null);
    return list;
  }

  protected LinkedHashMap<Bucket, FilesList> filterFiles(FilesList files, FileFilter f) {
    LinkedHashMap<Bucket, FilesList> map = new LinkedHashMap<>();
    for (File entry : files) {
      Bucket bucket = f.filter(entry);
      if (bucket == Bucket.SKIP)
        continue;

      FilesList list = map.get(bucket);
      if (list == null)
        map.put(bucket, list = new FilesList());

      list.add((bucket == Bucket.READ || bucket == Bucket.BOOKMARKED) ? new Chapter(entry.getPath()) : entry);
    }
    return map;
  }

  protected FileFilter filter(MangaChapterPicker picker) {
    return (file) -> file.isFile() ? Bucket.FILE : Bucket.UNREAD;
  }

}

class MangaFetcher extends FileFetcher {

  @Override
  protected FilesList sortEntries(FilesList list) {
    list = (FilesList) list.clone();
    HashMap<File, Long> hashed = new HashMap<>(list.size());
    list.sort((f1, f2) -> {
      Long l1 = hashed.get(f1);
      if (l1 == null) {
        l1 = f1.lastModified();
        hashed.put(f1, l1);
      }
      Long l2 = hashed.get(f2);
      if (l2 == null) {
        l2 = f2.lastModified();
        hashed.put(f2, l2);
      }
      return Long.signum(l2 - l1);
    });
    return list;
  }

  @Override
  protected FileFilter filter(MangaChapterPicker picker) {
    return (file) -> {
      if (file.isFile())
        return Bucket.FILE;

      Manga m = new Manga(file.getPath());
      Chapter b = m.lastBookmarkedChapter();
      Chapter last = m.lastChapter();

      if (b == null)
        return Bucket.UNREAD;

      if (b.compare(last) < 0)
        return Bucket.BOOKMARKED;

      return picker.skipReaded ? Bucket.SKIP : Bucket.READ;
    };
  }

}

class ChapterFetcher extends FileFetcher {

  Manga manga = null;
  Chapter bookmarked = null, last = null;
  
  boolean unread = false;
  
  @Override
  protected FilesList sortEntries(FilesList list) {
    list = (FilesList) list.clone();
    HashMap<File, Long> hashed = new HashMap<>(list.size());
    list.sort((f1, f2) -> {
      Long l1 = hashed.get(f1);
      if (l1 == null) {
        l1 = (long) (new Chapter(f1.getPath())).idx();
        hashed.put(f1, l1);
      }
      Long l2 = hashed.get(f2);
      if (l2 == null) {
        l2 = (long) (new Chapter(f2.getPath())).idx();
        hashed.put(f2, l2);
      }
      return Long.signum(l2 - l1);
    });
    return list;
  }

  @Override
  protected FileFilter filter(MangaChapterPicker picker) {
    manga = null;
    return (file) -> {
      if (file.isFile())
        return Bucket.FILE;
      
      Chapter c = null;
      if (manga == null) {
        manga = (c = new Chapter(file.getPath())).getManga();
        bookmarked = manga.lastBookmarkedChapter();
        last = manga.lastChapter();
        unread = bookmarked == null;
      }
      
      if (unread)
        return Bucket.UNREAD;

      if (c == null) 
        c = new Chapter(file.getPath());
      
      int compared = bookmarked.compare(c);
      
      switch (compared) {
      case -1: return Bucket.UNREAD;
      default: return picker.skipReaded ? Bucket.SKIP : Bucket.READ;
      }
    };
  }

}
