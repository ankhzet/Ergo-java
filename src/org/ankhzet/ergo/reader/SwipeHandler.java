
package org.ankhzet.ergo.reader;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SwipeHandler implements Runnable {

  static SwipeHandler swipe = null;
  public boolean inprocess = false;
  Thread worker = null;
  boolean vertical = true;
  int direction;
  int cw, ch;
  double progress;
  long start, end;
  public static final int ANIMATE_FOR = 1000;
  public static final double MIN_SPEED = 0.1;
  public static final double MAX_SPEED = 0.8;

  static boolean makeSwipe(boolean vertical, int direction, int cw, int ch) {
    if (swipe == null)
      swipe = new SwipeHandler();
    if (swipe.inprocess)
      return false;
    swipe.vertical = vertical;
    swipe.direction = direction;
    swipe.cw = cw;
    swipe.ch = ch;
    swipe.progress = 0.0;
    swipe.start = System.currentTimeMillis();
    swipe.end = swipe.start + (long)((1.0 - swipeSpeed()) * ANIMATE_FOR);
    swipe.worker = new Thread(swipe);
    swipe.worker.start();
    return true;
  }

  @Override
  public void run() {
    if (inprocess)
      return;

    inprocess = true;
    long t;
    while ((t = System.currentTimeMillis()) <= end) {
      long delta = end - t;
      try {
        Thread.sleep(delta);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    inprocess = false;
    switch (direction()) {
    case -1:
      Reader.get().prevPage();
      break;
    case 01:
      Reader.get().nextPage();
      break;
    }
  }

  public static boolean done() {
    if (swipe == null)
      return true;

    return !(swipe.inprocess || (System.currentTimeMillis() < swipe.end));
  }

  public static int direction() {
    return swipe == null ? 0 : Integer.signum(swipe.direction);
  }

  public static boolean vertical() {
    return swipe == null ? false : swipe.vertical;
  }

  public static double getProgress() {
    long t = System.currentTimeMillis();
    if (t >= swipe.end)
      return 1.0;

    double delta = (t - swipe.start) / (double) (swipe.end - swipe.start);

    return Math.max(Math.min(delta, 1.), 0.);
  }

  public static double swipeSpeed() {
    double delta = vertical() ? swipe.ch : swipe.cw;
    delta = Math.abs((double)swipe.direction) / delta;
    
    return Math.max(Math.min(delta * 2.0, MAX_SPEED), MIN_SPEED);
  }
}
