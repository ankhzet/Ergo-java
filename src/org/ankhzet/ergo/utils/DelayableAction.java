package org.ankhzet.ergo.utils;

import java.util.HashMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DelayableAction extends Thread {

  public interface Worker {

    public void process();

  }

  static final HashMap<String, DelayableAction> queues = new HashMap<>();

  public String name;
  public boolean imperative;
  protected Worker enqueued;

  public DelayableAction(String name, boolean imperative) {
    super(name);
    this.name = name;
    this.imperative = imperative;
  }

  public static DelayableAction of(String identifier, boolean imperative) {
    synchronized (queues) {
      DelayableAction queue = queues.get(identifier);
      if (queue == null) {
        queue = new DelayableAction(identifier, imperative);
        queues.put(identifier, queue);
      }
      return queue;
    }
  }

  public static DelayableAction imperative(String identifier) {
    return of(identifier, true);
  }

  public static boolean enqueue(String identifier, Worker worker) {
    return of(identifier, false).enqueue(worker);
  }

  synchronized public boolean enqueue(Worker worker) {
    if (imperative && enqueued != null)
      return false; // rejected

    enqueued = worker;

    notify();

    return true;
  }

  @Override
  public void run() {
    try {
      while (true)
        synchronized (this) {
          wait();

          Worker picked = pickWorker();
          if (picked != null)
            picked.process();
        }

    } catch (InterruptedException ex) {
    }
  }

  synchronized Worker pickWorker() {
    Worker worker = enqueued;
    enqueued = null;
    return worker;
  }

}
