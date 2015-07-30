/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ankhzet.ergo.classfactory.builder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <I> Class selector identifier
 * @param <P> Class of produced objects
 */
public interface Builder<I, P> {

  public P build(I identifier) throws Exception;

}
