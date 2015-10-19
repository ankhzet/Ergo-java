package org.ankhzet.ergo.ui.pages.reader.reader;

import ankh.IoC;
import ankh.annotations.DependencyInjection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SwipeHandler implements Runnable {

  public static final int ANIMATE_FOR = 500;
  public static final double MIN_SPEED = 0.1;
  public static final double MAX_SPEED = 0.8;
  static SwipeHandler swiper;

  public static SwipeHandler swipe() {
    if (swiper == null)
      swiper = IoC.get(SwipeHandler.class);
    return swiper;
  }

  public static boolean makeSwipe(boolean vertical, int direction, int cw, int ch) {
    SwipeHandler swipe = swipe();
    if (swipe.inprocess)
      return false;

    swipe.vertical = vertical;
    swipe.direction = direction;
    swipe.cw = cw;
    swipe.ch = ch;
    swipe.progress = 0.0;
    swipe.start = System.currentTimeMillis();
    swipe.end = swipe.start + (long) ((1.0 - swipeSpeed()) * ANIMATE_FOR);
    swipe.worker = new Thread(swipe);
    swipe.worker.start();
    return true;
  }

  public static boolean done() {
    SwipeHandler swipe = swipe();
    if (swipe == null)
      return true;

    return !(swipe.inprocess || (System.currentTimeMillis() < swipe.end));
  }

  public static int direction() {
    return Integer.signum(swipe().direction);
  }

  public static boolean vertical() {
    return swipe().vertical;
  }

  public static double getProgress() {
    SwipeHandler swipe = swipe();
    long t = System.currentTimeMillis();
    if (t >= swipe.end)
      return 1.0;

    double delta = (t - swipe.start) / (double) (swipe.end - swipe.start);

    return Math.max(Math.min(delta, 1.), 0.);
  }

  public static double swipeSpeed() {
    SwipeHandler swipe = swipe();
    double delta = vertical() ? swipe.ch : swipe.cw;
    delta = Math.abs((double) swipe.direction) / delta;

    return Math.max(Math.min(delta * 2.0, MAX_SPEED), MIN_SPEED);
  }

  public boolean inprocess = false;
  Thread worker = null;
  boolean vertical = true;
  int direction;
  int cw, ch;
  double progress;
  long start, end;

  @DependencyInjection
  Reader reader;

  @Override
  public void run() {
    if (inprocess)
      return;

    inprocess = true;
    long t;
    while ((t = System.currentTimeMillis()) <= end) {
      long delta = Math.max(end - t, 0);
      if (delta > 0)
        try {
          Thread.sleep(delta);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
    }
    inprocess = false;
    switch (direction()) {
    case -1:
      reader.prevPage();
      break;
    case 01:
      reader.nextPage();
      break;
    }
  }

}
