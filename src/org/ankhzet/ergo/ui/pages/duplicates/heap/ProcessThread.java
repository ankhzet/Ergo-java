package org.ankhzet.ergo.ui.pages.duplicates.heap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.Thread.interrupted;
import org.ankhzet.ergo.utils.FColor;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ProcessThread extends Thread {

  ImageHeap heap;
  ProcessChunk<String> chunk;

  public ProcessThread(ImageHeap heap, ProcessChunk<String> chunk) {
    this.heap = heap;
    this.chunk = chunk;
  }

  @Override
  public void run() {
    for (String page : chunk)
      if (interrupted())
        break;
      else
        try {
          processPage(page);
          chunk.progress(chunk.indexOf(page) / (double) chunk.size());
          Thread.sleep(10);
        } catch (IOException | InterruptedException ex) {
          ex.printStackTrace();
        }

    System.gc();
  }

  void processPage(String page) throws IOException {
    BufferedImage image = null;
    try {
      image = heap.thumbnail(page);
    } catch (Exception ex) {

    }
    if (image == null) {
      System.err.printf("Failed to generate thumbnail for \"%s\"\n", page);
      return;
    }
    FColor average = heap.imageAverage(page, image);
    heap.putImage(page, average, image);
  }

}
