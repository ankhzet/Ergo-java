
package org.ankhzet.ergo.reader.chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.ankhzet.ergo.LoaderProgressListener;
import org.ankhzet.ergo.reader.PageRenderOptions;
import org.ankhzet.ergo.reader.chapter.page.PageData;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ChapterData extends HashMap<String, PageData> {
  public final ReentrantLock lock = new ReentrantLock();

  public boolean isBusy() {
    return lock.isLocked();
  }

  public synchronized void calcLayout(int w, int h, PageRenderOptions ro, LoaderProgressListener listener) {
    if (isBusy())
      return;
    
    lock.lock();
    try {
      if (listener != null && !progressLayout(listener, 0))
        return;
      int pos = 0;
      for (PageData page : this.values()) {
        page.calcLayout(w, h, ro);
        if (listener != null && !progressLayout(listener, ++pos))
          return;
      }
      if (listener != null)
        listener.progressDone();
    } finally {
      lock.unlock();
    }
  }

  public synchronized void prepareCache(PageRenderOptions options, LoaderProgressListener listener) {
    if (isBusy())
      return;
    lock.lock();
    try {
      if (listener != null && !progressCache(listener, 0))
        return;
      int pos = 0;
      List<String> l = new ArrayList<>(this.keySet());
      Collections.sort(l);
      for (String fileKey : l) {
        PageData page = get(fileKey);
        page.makeCache(options);
        if (listener != null && !progressCache(listener, ++pos))
          return;
      }
      if (listener != null)
        listener.progressDone();
      System.gc();
    } finally {
      lock.unlock();
    }
  }

  private boolean progressLayout(LoaderProgressListener listener, int p) {
    return listener.onProgress(LoaderProgressListener.STATE_LAYOUTING, p, this.size());
  }

  private boolean progressCache(LoaderProgressListener listener, int p) {
    return listener.onProgress(LoaderProgressListener.STATE_CACHING, p, this.size());
  }

}
