package org.ankhzet.ergo.manga;

import ankh.IoC;
import org.ankhzet.ergo.db.tables.MangaOptionsTable;
import org.ankhzet.ergo.manga.chapter.page.ReadOptions;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MangaOptions extends ReadOptions {

  protected Manga manga;

  public MangaOptions(Manga manga) {
    this.manga = manga;

    MangaOptionsTable t = IoC.get(MangaOptionsTable.class);
    int optionsBits = t.getOptions(manga.uid());

    setOptions(optionsBits);
  }

  public boolean setOptions(ReadOptions options) {
    setOptions(options.hashCode());
    return save();
  }

  public boolean save() {
    MangaOptionsTable t = IoC.get(MangaOptionsTable.class);
    return t.setOptions(manga.uid(), hashCode());
  }

}
