package org.ankhzet.ergo.manga.chapter.chaptercacher;

import java.util.ArrayList;
import java.util.Set;
import org.ankhzet.ergo.manga.chapter.chaptercacher.cache.Cache;
import org.ankhzet.ergo.manga.chapter.chaptercacher.cache.CacheTask;
import org.ankhzet.ergo.manga.chapter.page.PageData;
import org.ankhzet.ergo.ui.UILogic;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ScansCache extends Cache<PageData, ScanCache> {

  Thread processor;

  class CacheTaskList<C> extends ArrayList<CacheTask<C>> {
  }

  CacheTaskList<ScanCache> tasks = new CacheTaskList<>();

  public ScansCache() {
    startWorker();
  }

  public ScanCache cacheData(String key, PageData data) {
    ScanCache cacheable = new ScanCache(tasks.size(), data, key);
    invalidate(cacheable);
    return cacheable;
  }

  boolean processTask(ScanCache scan, CacheTask<ScanCache> task) {
    int idx = tasks.indexOf(task) + 1;
    int cid = scan.cached();
    if (cid > idx)
      return false;
    
    boolean r = true;
    if (cid < idx) {
      r = task.process(scan);
      scan.validate(idx);
    }
    return r;
  }

  void processTaks() {
    processInvalid((cache) -> {
      ArrayList<String> keys = new ArrayList<>(this.keySet());
      keys.sort(null);
      for (String key : keys)
        for (CacheTask<ScanCache> task : tasks)
          if (!processTask(get(key), task))
            return;
    });

  }

  private void startWorker() {
    processor = new Thread() {

      @Override
      public void run() {

        while (true) {
          processTaks();

          try {
            Thread.sleep(50);
          } catch (InterruptedException ex) {
            break;
          }
        }

      }

    };
    processor.start();
  }

  public void registerTask(CacheTask<ScanCache> task) {
    tasks.add(task);
  }

}
