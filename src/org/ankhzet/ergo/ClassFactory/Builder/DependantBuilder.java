
package org.ankhzet.ergo.ClassFactory.Builder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class DependantBuilder<Type, Dependency> extends Builder<Type> {
  Dependency dependency;

  public DependantBuilder(Dependency dependency) {
    this.dependency = dependency;
  }

  public Dependency getDependency() {
    return dependency;
  }

}
