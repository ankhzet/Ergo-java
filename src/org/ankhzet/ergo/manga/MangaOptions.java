package org.ankhzet.ergo.manga;

import org.ankhzet.ergo.manga.chapter.page.ReadOptions;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MangaOptions extends ReadOptions {

  protected Manga manga;

  public MangaOptions(Manga manga) {
    this.manga = manga;
  }

  public boolean setOptions(ReadOptions options) {
    setOptions(options.hashCode());
  }

}

