package org.ankhzet.ergo.ui.pages.home;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.manga.Manga;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.Skin;
import org.ankhzet.ergo.ui.pages.reader.reader.Reader;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker.FilesList;
import org.ankhzet.ergo.ui.xgui.filepicker.CollumnedItemVisitor;

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
  protected void fetchRoot() {
    super.fetchRoot();
    entries.remove(upFolderFile());

    FilesList bookmarked = new FilesList();
    FilesList read = new FilesList();
    FilesList unread = new FilesList();
    FilesList files = new FilesList();

    boolean inRootFolder = false;
    File root = getRootFile();
    for (String r : reader.getMangaRoots())
      if (root.equals(new File(r))) {
        inRootFolder = true;
        break;
      }

    Manga m = new Manga(root.getPath());
    Chapter b = null, last = null;
    if (!inRootFolder) {
      b = m.lastBookmarkedChapter();
      last = m.lastChapter();
    }

    for (File entry : entries)
      if (entry.isDirectory()) {
        if (inRootFolder) {
          m = new Manga(entry.getPath());
          b = m.lastBookmarkedChapter();
          last = m.lastChapter();
        }

        if (b != null) {
          Chapter c = new Chapter(entry.getPath());
          File add = inRootFolder
                     ? m
                     : (c.equals(b) ? c : entry);

          if (b.compare(last) >= 0) {
            if (!skipReaded)
              read.add(add);
          } else
            bookmarked.add(add);
        } else
          unread.add(entry);
      } else
        files.add(entry);

    if (inRootFolder) {
      Comparator<? super File> c = (f1, f2) -> Long.signum(f2.lastModified() - f1.lastModified());
      bookmarked.sort(c);
      unread.sort(c);
      read.sort(c);
    }

    entries.clear();
    entries.add(upFolderFile());
    entries.addAll(bookmarked);
    entries.addAll(unread);
    entries.addAll(read);
    entries.addAll(files);
  }

  @Override
  public void drawItems(Graphics2D g) {
    Shape clip = g.getClip();
    int fontHeight = g.getFont().getSize();

    CollumnedItemVisitor.NodeVisitor<File> nodeVisitor = (Rectangle r, File item) -> {

      if (!clip.contains(r))
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
