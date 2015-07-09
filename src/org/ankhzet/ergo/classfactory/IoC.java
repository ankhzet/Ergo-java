package org.ankhzet.ergo.classfactory;

import org.ankhzet.ergo.classfactory.builder.Builder;

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
    } catch (UnknownFactoryProductException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public static <C> C get(Class<C> c) {
    ClassFactory<C> factory = factory(c);

    if (factory != null)
      return null;

    try {
      return factory.get(c);
    } catch (UnknownFactoryProductException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public static <C> C make(Class<C> c) throws UnknownFactoryProductException {
    ClassFactory<C> factory = factory(c);
    if (factory == null)
      throw new UnknownFactoryProductException(c.getName());

    return factory.make(c);
  }

  public static ClassFactory register(ClassFactory factory) {
    Factories f = factories();

    Builder<ClassFactory> builder = new DependantBuilder(factory);

    for (Class identifier : factory.produces())
      f.register(identifier, builder);

    return factory;
  }

  private static class DependantBuilder extends Builder<ClassFactory> {

    ClassFactory dependency;

    public DependantBuilder(ClassFactory dependency) {
      this.dependency = dependency;
    }

    @Override
    public ClassFactory call() throws Exception {
      return this.dependency;
    }
  }
}
