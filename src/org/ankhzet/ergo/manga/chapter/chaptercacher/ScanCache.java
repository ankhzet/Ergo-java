package org.ankhzet.ergo.manga.chapter.chaptercacher;

import org.ankhzet.ergo.manga.chapter.chaptercacher.cache.Cacheable;
import org.ankhzet.ergo.manga.chapter.page.PageData;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ScanCache extends Cacheable<PageData> {

  public ScanCache(int stages, PageData data, String key) {
    super(stages, data, key);
  }

}
