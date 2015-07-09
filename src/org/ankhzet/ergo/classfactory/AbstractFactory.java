
package org.ankhzet.ergo.classfactory;

import org.ankhzet.ergo.classfactory.exceptions.FactoryException;
import org.ankhzet.ergo.classfactory.builder.Builder;
import java.util.Set;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <IdentifierType> Identifier to select builder with
 * @param <ProducesType> Class, produced by factory
 */
public interface AbstractFactory<IdentifierType, ProducesType> {
  Set<Class> produces();

  ProducesType get(IdentifierType identifier) throws FactoryException;
  ProducesType make(IdentifierType identifier) throws FactoryException;
  Builder register(IdentifierType identifier, Builder<ProducesType> maker);
  Builder register(IdentifierType identifier);
}
