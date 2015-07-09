
package org.ankhzet.ergo.classfactory.builder;


/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface Builder<Type> {

  public Type build(Class<? extends Type> c) throws Exception;
}
