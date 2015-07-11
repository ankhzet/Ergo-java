package org.ankhzet.ergo.reader.chapter;

import org.ankhzet.ergo.UILogic;
import org.ankhzet.ergo.reader.Reader;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ChapterLoader {

  Chapter chapter;
  Thread loader, cacher;
  UILogic ui;
  Reader reader;

  public void load(Chapter chapter) {
    this.chapter = chapter;

    loader = new Thread(() -> {
      int wait = 0;
      while (reader.isBusy() && wait < 100)
        try {
          Thread.sleep(10);
          wait += 1;
        } catch (InterruptedException ex) {
        }
      
      if (!reader.isBusy()) {
        reader.cacheChapter(chapter, ui);
        reader.flushCache(true);
      }
    });

    loader.start();
  }

  final void layout() {
    cacher = new Thread(() -> {
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

  public UILogic diUILogic(UILogic ui) {
    if (ui != null) {
      this.ui = ui;
      if (this.reader != null)
        layout();
    }
    return this.ui;
  }

  public Reader diReader(Reader reader) {
    if (reader != null) {
      this.reader = reader;
      if (this.ui != null)
        layout();
    }
    return this.reader;
  }

}
