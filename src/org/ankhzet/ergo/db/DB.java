package org.ankhzet.ergo.db;

import org.ankhzet.ergo.db.query.Builder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DB extends Builder {

  public DB() {
    super();
  }

  public DB(String from) {
    super(from);
  }

  @Override
  public String beforeSQL(String sql) {
    return sql;
  }

}
