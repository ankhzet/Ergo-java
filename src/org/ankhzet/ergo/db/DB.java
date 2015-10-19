package org.ankhzet.ergo.db;

import org.ankhzet.ergo.Config;
import ankh.annotations.DependencyInjection;
import org.ankhzet.ergo.db.query.Builder;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DB extends Builder {

  @DependencyInjection()
  protected Config config;

  public DB() {
    super();
  }

  public DB(String from) {
    super(from);
  }

  @Override
  public String beforeSQL(String sql) {
    if (config.get("db.log", false))
      logSQL(sql);

    return sql;
  }

  void logSQL(String sql) {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();

    Strings filtered = new Strings();

    for (StackTraceElement element : stack) {
      if (element.isNativeMethod() || element.getLineNumber() < 0)
        continue;

      String c = element.getClassName();
      if (!c.contains(".ergo.") || c.contains(".db.") || c.contains(".classfactory."))
        continue;

      filtered.add(element.toString());
    }

    UILogic.log("SQL => %s \n\t[ %s\n\t]", subtitutedSQL(sql), filtered.join("\n\t| "));
  }

}
