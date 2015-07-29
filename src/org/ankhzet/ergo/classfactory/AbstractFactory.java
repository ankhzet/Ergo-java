/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ankhzet.ergo.classfactory;

import java.util.Set;
import org.ankhzet.ergo.classfactory.builder.Builder;
import org.ankhzet.ergo.classfactory.exceptions.FactoryException;

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
