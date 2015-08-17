package org.ankhzet.ergo.db.query;

import java.util.ArrayList;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Builder {

  ArrayList<Where> wheres = new ArrayList<>();
  ArrayList<Order> orders = new ArrayList<>();
  Strings groups = new Strings();

  Strings columns;

  Boolean distinct = false;
  String from;
  Integer limit = 0;
  Integer offset = 0;

  ArrayList<Object> bindings = new ArrayList<>();

  public Builder() {
  }

  public Builder(String from) {
    this.from = from;
  }

  public Builder select(String... withColumns) {
    columns = new Strings(withColumns);
    return this;
  }

  public Builder addSelect(String... withColumns) {
    if (columns == null)
      columns = new Strings(withColumns);
    else {
      columns.remove("*");
      columns.addAll(new Strings(withColumns));
    }
    return this;
  }

  public Builder limit(int limit) {
    this.limit = limit;
    return this;
  }

  public Builder offset(int offset) {
    this.offset = offset;
    return this;
  }

  public Builder distinct() {
    this.distinct = true;
    return this;
  }

  public Builder groupBy(String column) {
    groups.add(column);
    return this;
  }

  public Builder orderBy(String column) {
    return orderBy(column, false);
  }

  public Builder orderBy(String column, boolean desc) {
    orders.add(new Order(column, desc));
    return this;
  }

  public Builder where(String column, String operator, Object value) {
    return where(Where.JOIN_AND, column, operator, value);
  }

  public Builder orWhere(String column, String operator, Object value) {
    return where(Where.JOIN_OR, column, operator, value);
  }

  public Builder where(String column, Object value) {
    return where(Where.JOIN_AND, column, value);
  }

  public Builder orWhere(String column, Object value) {
    return where(Where.JOIN_OR, column, value);
  }

  public Builder where(int join, String column, Object value) {
    return where(join, column, "=", value);
  }

  public Builder where(int join, String column, String operator, Object value) {
    String placeholder = "?";
    if (value == null)
      if (Strings.explode("=,!=,<>,==", ",").contains(operator)) {
        operator = "is";
        placeholder = (Strings.explode("!=,<>", ",").contains(operator) ? "not " : "") + "null";
      }

    Where w = new Where(join, String.format("\"%s\" %s %s", column, operator, placeholder));

    wheres.add(w);

    if (placeholder.equals("?"))
      bindings.add(value);

    return this;
  }

  public String subtitutedSQL(String sql) {
    int idx = 0;

    String result = sql;
    while (result.contains("?")) {
      Object o = bindings.get(idx++);
      if (o == null)
        o = "null";
      else
        if (o instanceof String)
          o = "\"" + o + "\"";
        else
          o = o.toString();

      result = result.replaceFirst("\\?", (String) o);
    }

    return result;
  }

}

