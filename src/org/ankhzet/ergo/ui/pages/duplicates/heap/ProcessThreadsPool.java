package org.ankhzet.ergo.ui.pages.duplicates.heap;

import java.util.ArrayList;
import org.ankhzet.ergo.ui.LoaderProgressListener;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ProcessThreadsPool extends ArrayList<ProcessThread> {

  public int init(ImageHeap heap, ArrayList<String> pages, int threads, LoaderProgressListener progressListener) {
    clear();
    int total = pages.size();

    if (progressListener != null)
      progressListener.onProgress(LoaderProgressListener.STATE_CACHING, 0, total);
    
    int perThread = Math.max(total / threads, 1);
    threads = Math.min(threads, total / perThread);

    ChunksProgress progress = new ChunksProgress();

    int i = threads;
    while (i-- > 0) {
      int from = i * perThread;
      int to = Math.min(from + perThread, pages.size() - 1);

      add(new ProcessThread(
        heap,
        new ProcessChunk<>(
          i, 
          total, 
          progress, 
          progressListener, 
          pages.subList(from, to)
        )
      ));
    }

    return threads;
  }

  public void start() {
    for (ProcessThread thread : this)
      thread.start();

  }

  public void stop() {
    for (ProcessThread thread : this)
      thread.interrupt();
  }

  @Override
  public void clear() {
    stop();
    super.clear();
  }

}
