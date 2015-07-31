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
 * @param <I> Identifier to select builder with
 * @param <P>
 */
public interface AbstractFactory<I, P> {

  Set<I> produces();

  P get(I identifier) throws FactoryException;

  P make(I identifier, Object... args) throws FactoryException;

  Builder<I, P> register(I identifier, Builder<I, P> maker);

  Builder<I, P> register(I identifier);

}
