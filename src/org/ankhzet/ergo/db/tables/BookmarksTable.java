package org.ankhzet.ergo.db.tables;

import java.sql.ResultSet;
import org.ankhzet.ergo.db.Table;
import org.ankhzet.ergo.db.query.Builder;
import org.ankhzet.ergo.db.query.ObjectsMap;

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
    return "manga text not null"
      + ", chapter float not null"
      + ", unique (manga, chapter)";
  }

  public ResultSet fetch(String manga) {
    return tableBuilder()
      .where("manga", manga)
      .get();
  }

  public int save(ObjectsMap values) {
    String key = values.keySet().iterator().next();
    return tableBuilder().insertOrUpdate(key, values);
  }

  public int delete(ObjectsMap values) {
    Builder db = tableBuilder();

    for (String column : values.keySet())
      db = db.where(column, values.get(column));

    return db.delete();
  }

}
