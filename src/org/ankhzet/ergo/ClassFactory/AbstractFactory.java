
package org.ankhzet.ergo.ClassFactory;

import org.ankhzet.ergo.ClassFactory.Builder.Builder;
import java.util.Set;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface AbstractFactory<IdentifierType, ProducesType> {
  interface ClassSet extends Set<Class> {};

  ClassSet produces();

  ProducesType get(IdentifierType identifier) throws UnknownFactoryProductException;
  ProducesType make(IdentifierType identifier) throws UnknownFactoryProductException;
  Builder register(IdentifierType identifier, Builder<ProducesType> maker);
}
