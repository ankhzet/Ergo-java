package org.ankhzet.ergo.ui.pages.duplicates.heap;

import java.util.ArrayList;
import java.util.Collection;
import org.ankhzet.ergo.ui.LoaderProgressListener;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Item>
 */
public class ProcessChunk<Item> extends ArrayList<Item> {

  int index;
  int total;

  final ChunksProgress chunks;
  LoaderProgressListener listener;

  public ProcessChunk(int index, int total, ChunksProgress chunks, LoaderProgressListener listener, Collection<? extends Item> c) {
    super(c);
    this.index = index;
    this.total = total;
    this.chunks = chunks;
    this.listener = listener;
    chunks.put(index, 0.);
  }

  synchronized void progress(double progress) {
    synchronized (chunks) {
      chunks.put(index, progress);

      if (listener == null)
        return;

      double totalProgress = 0.;
      for (Double chunkProgress : chunks.values())
        totalProgress += chunkProgress;

      totalProgress /= chunks.size();
      int intProgress = (int) (totalProgress * total);
      if (intProgress < total - 1)
        listener.onProgress(LoaderProgressListener.STATE_CACHING, intProgress, total);
      else
        listener.progressDone();
    }
  }

}
