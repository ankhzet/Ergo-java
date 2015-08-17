package org.ankhzet.ergo.db.query;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Order {

  String column;
  boolean desc;

  public Order(String column, boolean desc) {
    this.column = column;
    this.desc = desc;
  }

}
