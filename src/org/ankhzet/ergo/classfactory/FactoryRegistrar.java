package org.ankhzet.ergo.classfactory;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FactoryRegistrar {

  static final HashMap<Class, Boolean> counters = new HashMap<>();

  public FactoryRegistrar(Object identifier) {

    Class factoryClass = getFactoryClass(identifier);
    synchronized (counters) {
      Boolean registered = counters.get(factoryClass);
      if (!Objects.equals(registered, Boolean.TRUE)) {
        counters.put(factoryClass, Boolean.TRUE);

        System.out.printf("Registering %s..\n", factoryClass.getName());

        try {
          IoC.register(getInstance(identifier));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

  }

  static Class getFactoryClass(Object identifier) {
    return (identifier instanceof Class) ? (Class) identifier : identifier.getClass();
  }

  static ClassFactory getInstance(Object identifier) throws Exception {
    if (identifier instanceof Class)
      return (ClassFactory) ((Class) identifier).newInstance();
    else
      return (ClassFactory) identifier;

  }

}
