package org.ankhzet.ergo.db.tables;

import org.ankhzet.ergo.db.Table;
import org.ankhzet.ergo.db.query.ObjectsMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MangaOptionsTable extends Table {

  public static final String TABLE_NAME = "manga_options";

  @Override
  public String tableName() {
    return TABLE_NAME;
  }

  @Override
  protected String schema() {
    return "manga text not null"
      + ", options integer not null default 0"
      + ", unique (manga)";
  }

  public int getOptions(String manga) {
    return tableBuilder().value(b -> {
      return (Integer) b.where("manga", manga).value("options");
    }, 0);
  }

  public boolean setOptions(String manga, int optionsBits) {
    return tableBuilder()
      .insertOrUpdate("manga", ObjectsMap.of(new Object[][]{
        {"manga", manga}, //
        {"options", optionsBits}, //
      })) > 0;
  }

}
