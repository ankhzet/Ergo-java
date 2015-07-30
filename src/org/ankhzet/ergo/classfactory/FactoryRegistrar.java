package org.ankhzet.ergo.classfactory;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <P> Type, produced by factory
 */
public class FactoryRegistrar<P> {

  static final HashMap<Object, Boolean> counters = new HashMap<>();

  public FactoryRegistrar(Object identifier) {
    registerIfNeeded(identifier);
  }

  final void registerIfNeeded(Object identifier) {
    Object factoryIdentifier = getFactoryIdentifier(identifier);
    synchronized (counters) {
      Boolean registered = counters.get(factoryIdentifier);
      if (!Objects.equals(registered, Boolean.TRUE)) {
        counters.put(factoryIdentifier, Boolean.TRUE);
        register(factoryIdentifier, getInstance(factoryIdentifier));
      }
    }

  }

  public void register(Object factoryIdentifier, P factory) {
    throw new RuntimeException(String.format("Don't know how to register %s", factoryIdentifier));
  }

  public Object getFactoryIdentifier(Object identifier) {
    return identifier;
  }

  @SuppressWarnings("unchecked")
  public P getInstance(Object identifier) {
    return (P) identifier;
  }

}
