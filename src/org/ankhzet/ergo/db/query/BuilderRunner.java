/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ankhzet.ergo.db.query;

import java.sql.SQLException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface BuilderRunner<T> {

  public T execute(Builder builder) throws SQLException;

}
