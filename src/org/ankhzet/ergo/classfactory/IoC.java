package org.ankhzet.ergo.classfactory;

import java.util.Set;
import org.ankhzet.ergo.classfactory.exceptions.*;
import org.ankhzet.ergo.classfactory.builder.Builder;
import org.ankhzet.ergo.classfactory.builder.DependantClassBuilder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoC {

  private static class Factories extends ClassFactory<ClassFactory> {
  };
  static Factories factories;

  static Factories factories() {
    if (factories == null)
      factories = new Factories();
    return factories;
  }

  public static <C> ClassFactory<C> factory(Class<C> c) {
    try {
      return factories().get(c);
    } catch (FactoryException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static <C> C get(Class<C> c) {
    ClassFactory<C> factory = factory(c);

    if (factory == null)
      return null;

    try {
      return factory.get(c);
    } catch (FactoryException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public static <C> C make(Class<C> c) throws FactoryException {
    ClassFactory<C> factory = factory(c);
    if (factory == null)
      throw new UnknownFactoryProductException(c.getName());

    return factory.make(c);
  }

  public static ClassFactory register(ClassFactory factory) {
    Factories f = factories();

    Builder<ClassFactory> builder = new DependantClassBuilder(factory) {
      @Override
      public Object build(Class c) throws Exception {
        return getDependency();
      }
    };

    ((Set<Class>) factory.produces()).forEach((identifier) -> {
      f.register(identifier, builder);
    });

    return factory;
  }

}
