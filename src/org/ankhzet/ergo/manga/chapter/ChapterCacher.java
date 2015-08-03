package org.ankhzet.ergo.manga.chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.ankhzet.ergo.manga.chapter.page.PageData;
import org.ankhzet.ergo.manga.chapter.page.PageRenderOptions;
import org.ankhzet.ergo.ui.LoaderProgressListener;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ChapterCacher extends HashMap<String, PageData> {

  public final ReentrantLock lock = new ReentrantLock();

  public boolean isBusy() {
    return lock.isLocked();
  }

  public synchronized void calcLayout(int w, int h, PageRenderOptions ro, LoaderProgressListener listener) {
    if (isBusy())
      return;

    lock.lock();
    try {
      if (!progressLayout(listener, 0))
        return;

      int pos = 0;
      for (PageData page : this.values()) {
        page.calcLayout(w, h, ro);

        if (!progressLayout(listener, ++pos))
          return;
      }

      progressDone(listener);
    } finally {
      lock.unlock();
    }
  }

  public synchronized void prepareCache(PageRenderOptions options, LoaderProgressListener listener) {
    if (isBusy())
      return;

    lock.lock();
    try {
      if (!progressCache(listener, 0))
        return;

      int pos = 0;
      List<String> l = new ArrayList<>(this.keySet());
      Collections.sort(l);
      for (String fileKey : l) {
        PageData page = get(fileKey);
        page.makeCache(options);

        if (!progressCache(listener, ++pos))
          return;
      }

      progressDone(listener);

      System.gc();
    } finally {
      lock.unlock();
    }
  }

  private boolean progressLayout(LoaderProgressListener listener, int p) {
    return onProgress(listener, LoaderProgressListener.STATE_LAYOUTING, p, this.size());
  }

  private boolean progressCache(LoaderProgressListener listener, int p) {
    return onProgress(listener, LoaderProgressListener.STATE_CACHING, p, this.size());
  }

  public boolean onProgress(LoaderProgressListener listener, int state, int progress, int max) {
    return (listener == null) || listener.onProgress(state, progress, max);
  }

  public void progressDone(LoaderProgressListener listener) {
    if (listener != null)
      listener.progressDone();
  }

}
