package org.ankhzet.ergo.reader.chapter;

import org.ankhzet.ergo.ClassFactory.IoC;
import org.ankhzet.ergo.UILogic;
import org.ankhzet.ergo.reader.Reader;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ChapterLoader {

  String manga;
  int chapter;
  Thread loader, cacher;

  public ChapterLoader() {
    manga = null;
    chapter = 0;
    layout();
  }

  public void load(String m, int c) {
    manga = m;
    chapter = c;
    loader = new Thread(() -> {
      Reader reader = Reader.get();
      UILogic ui = IoC.get(UILogic.class);
      int wait = 0;
      while (reader.isBusy() && wait < 1000)
        try {
          Thread.sleep(10);
          wait += 10;
        } catch (InterruptedException ex) {
        }
      
      if (!reader.isBusy()) {
        reader.prepareForChapter(manga, chapter, ui);
        reader.flushCache(true);
      }
    });

    loader.start();
  }

  final void layout() {
    cacher = new Thread(() -> {
      Reader reader = Reader.get();
      UILogic ui = IoC.get(UILogic.class);
      try {
        while (true) {
          if (reader.flushPending()) {
            while (reader.isBusy())
              Thread.sleep(10);
            reader.flushCache(false);
            reader.calcLayout(ui.clientArea.width, ui.clientArea.height - UILogic.UIPANEL_HEIGHT, ui);
            ui.progressDone();
          }

          Thread.sleep(10);
        }
      } catch (InterruptedException ex) {
      }
//        cacher = null;
    });

    cacher.start();
  }
}
