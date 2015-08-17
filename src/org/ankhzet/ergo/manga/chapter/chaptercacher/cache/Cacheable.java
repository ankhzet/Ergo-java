package org.ankhzet.ergo.manga.chapter.chaptercacher.cache;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T> Type of data, cached by this cacheable
 */
public class Cacheable<T> {

  private int cached = 0;
  private int stages;
  protected T data;
  protected String key;

  public Cacheable(int stages, T data, String key) {
    this.stages = stages;
    this.data = data;
    this.key = key;
  }

  public T getData() {
    return data;
  }

  public int cached() {
    return cached;
  }

  public synchronized boolean invalid() {
    return cached < stages;
  }

  public synchronized void invalidate(int stage) {
    cached = Math.max(stage - 1, 0);
  }

  public synchronized void validate(int stage) {
    cached = stage;
  }

}
