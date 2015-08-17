package org.ankhzet.ergo.db.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SQLGrammar extends Grammar {

  static final String pattern = "columns,from,wheres,groups,orders,limit,offset";

  public SQLGrammar() {
    components.put("from", T_STRING);
    components.put("columns", T_LIST);
    components.put("wheres", T_LIST);
    components.put("groups", T_LIST);
    components.put("orders", T_LIST);
    components.put("limit", T_INTEGER);
    components.put("offset", T_INTEGER);
  }

  /* ==== QUERIES ==== */
  public String compileCreate(Builder query, String schema) {
    schema = schema.trim();
    if (schema.length() > 0)
      schema = " (" + schema + ")";
    return String.format("create table if not exists %s%s",
                         wrapTable(query.from),
                         schema
    );
  }

  public String compileTruncate(Builder query) {
    return String.format("delete %s",
                         compileFrom(query)
    );
  }

  public String compileDrop(Builder query) {
    return String.format("drop table %s",
                         wrapTable(query.from)
    );
  }

  public String compileInsert(Builder query, ObjectsMap record) {
    Strings columns = new Strings(record.keySet());
    Strings values = new Strings();
    for (String column : columns)
      values.add("?");

    return String.format("insert into %s (%s) values (%s)",
                         wrapTable(query.from),
                         columns.join(", "),
                         values.join(", ")
    );
  }

  public String compileUpdate(Builder query, ObjectsMap values) {
    Strings columns = new Strings();
    for (String column : values.keySet())
      columns.add(column + " = " + "?");
    return String.format("update %s set %s %s",
                         wrapTable(query.from),
                         columns.join(", "),
                         compileWheres(query)
    );
  }

  public String compileDelete(Builder query) {
    return String.format("delete from %s %s",
                         wrapTable(query.from),
                         compileWheres(query)
    );
  }

  public String compileSelect(Builder query) {
    return join(query, compileComponents(query));
  }

  /* ==== compilation of SELECT/WHERE-related chunks ==== */
  String compileFrom(Builder query) {
    return "from " + wrapTable(query.from);
  }

  String compileWheres(Builder query) {
    if (query.wheres.isEmpty())
      return "";

    Strings r = new Strings();
    for (Where w : query.wheres)
      r.add(w.join() + " " + w.condition);

    return "where " + r.join(" ")
      .replaceAll("^(or|and)", "")
      .trim();
  }

  String compileGroups(Builder query) {
    return "group by " + query.groups.join(", ");
  }

  String compileOrders(Builder query) {
    ArrayList<Order> ordersList = query.orders;
    Strings r = new Strings();
    for (Order order : ordersList) {
      String o = order.column + " " + (order.desc ? "desc" : "asc");
      r.add(o);
    }

    return "order by " + r.join(", ");
  }

  String compileColumns(Builder query) {
    String select = "select ";

    String columnsList = wrapColumns(query.columns);
    if (!columnsList.equals("*"))
      if (valueOf(query, "distinct", Boolean.FALSE))
        select += "distinct ";

    return select + columnsList;
  }

  String compileLimit(Builder query) {
    return "limit " + valueOf(query, "limit", (Integer) 0);
  }

  String compileOffset(Builder query, Field offset) {
    return "offset " + valueOf(query, "offset", (Integer) 0);
  }

  /* ==== Utils ==== */
  String wrapColumns(Strings columns) {
    return columns.join(", ");
  }

  String wrapTable(String name) {
    return "\"" + name + "\"";
  }

  String join(Builder query, HashMap<String, String> sqls) {
    Strings order = Strings.explode(pattern, ",");
    Strings result = new Strings();
    for (String component : order) {
      String sql = sqls.get(component);
      if (sql != null)
        result.add(sql);
    }
    return result.join(" ");
  }

}
