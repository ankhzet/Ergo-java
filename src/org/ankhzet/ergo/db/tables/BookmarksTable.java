package org.ankhzet.ergo.db.tables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.ankhzet.ergo.db.Table;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class BookmarksTable extends Table {

  public static final String TABLE_NAME = "bookmark_page";

  @Override
  public String tableName() {
    return TABLE_NAME;
  }

  @Override
  protected String schema() {
    return tableName() + " ("
      + "  manga text not null"
      + ", chapter float not null"
      + ", unique (manga, chapter)"
      + ")";
  }

  public ResultSet fetch(String manga) throws SQLException {
    PreparedStatement ps = db.prepareStatement("select * from " + tableName() + " where manga = ?");
    ps.setString(1, manga);

    return ps.executeQuery();
  }

}
