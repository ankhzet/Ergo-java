package org.ankhzet.ergo.manga.chapter.chaptercacher.cache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <D> Type of data, holded by cacheable entries in this cache
 * @param <T> Actual type of cacheable objects
 */
public class Cache<D, T extends Cacheable<D>> extends HashMap<String, T> {

  public interface Processor<D, T extends Cacheable<D>> {

    void process(Cache<D, T> cacheables);

  }

  synchronized public void detatch(Runnable r) {
    Thread loader = new Thread(() -> {
      synked(() -> r.run());
    });

    loader.start();
  }

  synchronized void synked(Runnable r) {
    r.run();
  }

  synchronized public HashMap<Integer, ArrayList<Cacheable>> invalidOfStage(int stage) {
    HashMap<Integer, ArrayList<Cacheable>> r = new HashMap<>();
    for (Cacheable cacheable : this.values())
      if (cacheable.invalid()) {
        int id = cacheable.cached();
        ArrayList<Cacheable> list = r.get(id);
        if (list == null) {
          list = new ArrayList<>();
          r.put(id, list);
        }
        list.add(cacheable);
      }

    return r;
  }

  synchronized public void processInvalid(Processor<D, T> p) {
    Cache invalid = new Cache<>();
    for (String key : this.keySet()) {
      Cacheable cacheable = this.get(key);
      if (cacheable.invalid())
        invalid.put(key, cacheable);
    }

    if (invalid.size() > 0)
      p.process(invalid);
  }

  synchronized public void remove(T cacheable) {
    super.remove(cacheable.key);
  }

  @Override
  synchronized public void clear() {
    super.clear();
  }

  synchronized public void invalidate(T cacheable, int stage) {
    if (!containsValue(cacheable))
      put(cacheable.key, cacheable);

    cacheable.invalidate(stage);
  }

  public void invalidate(T cacheable) {
    invalidate(cacheable, 0);
  }

  synchronized public void invalidateAll(int stage) {
    this.forEach((k, c) -> c.invalidate(stage));
  }

  public void invalidateAll() {
    invalidateAll(0);
  }

  public D cachedData(String key) {
    T cacheable = get(key);
    return (cacheable != null) ? cacheable.data : null;
  }

}
