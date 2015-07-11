package org.ankhzet.ergo.classfactory.builder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type> Class to produce
 * @param <Dependency> Class, builder is dependent of
 */
public class DependantClassBuilder<Type, Dependency> extends ClassBuilder<Type> {

  Dependency dependency;

  public DependantClassBuilder(Dependency dependency) {
    this.dependency = dependency;
  }

  public Dependency getDependency() {
    return dependency;
  }

}
