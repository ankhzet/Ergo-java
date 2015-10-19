package org.ankhzet.ergo.db.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import ankh.IoC;
import ankh.annotations.DependencyInjection;
import ankh.exceptions.FactoryException;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Builder {

  @DependencyInjection
  Connection connection;

  @DependencyInjection
  SQLGrammar grammar;

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

  public Builder table(String table) {
    try {
      return IoC.resolve(Builder.class, table);
    } catch (FactoryException ex) {
      throw new RuntimeException(ex);
    }
  }

  <T> T wrapCall(BuilderRunner<T> c) {
    try {
      return c.execute(this);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  public <T> T value(BuilderRunner<T> b, T def) {
    T value = wrapCall(b);
    return (value != null) ? value : def;
  }

  public void create(String schema) throws SQLException {
    prepareSql(grammar.compileCreate(this, schema)).executeUpdate();
  }

  public void truncate() throws SQLException {
    prepareSql(grammar.compileTruncate(this)).executeUpdate();
  }

  public void drop() throws SQLException {
    prepareSql(grammar.compileDrop(this)).executeUpdate();
  }

  public int insert(ObjectsMap record) {
    bindings.addAll(record.values());
    return wrapCall((b)
      -> prepareSql(grammar.compileInsert(this, record)).executeUpdate()
    );
  }

  public int update(ObjectsMap values) {
    bindings.addAll(0, values.values());
    return wrapCall((b)
      -> prepareSql(grammar.compileUpdate(this, values)).executeUpdate()
    );
  }

  public int insertOrUpdate(String column, ObjectsMap values) {
    Object inDB = where(column, values.get(column))
      .value(column);

    if (inDB != null) {
      values = new ObjectsMap(values);
      values.remove(column);
      return update(values);
    } else
      return table(from).insert(values);
  }

  public ResultSet get(String... columns) {
    if (columns.length > 0)
      addSelect(columns);
    else
      addSelect("*");

    return wrapCall((b)
      -> prepareSql(grammar.compileSelect(this)).executeQuery()
    );
  }

  public int delete() {
    return wrapCall((b)
      -> prepareSql(grammar.compileDelete(this)).executeUpdate()
    );
  }

  public int delete(int id) {
    return where("id", id).delete();
  }

  public Object value(String column) {
    return wrapCall((b) -> {
      ResultSet r = first(column);
      if (r != null) {
        Object o = r.getObject(column);
        r.close();
        return o;
      }
      return null;
    });
  }

  public ResultSet first(String... columns) {
    return wrapCall((b) -> {
      ResultSet r = limit(1).get(columns);
      return (r != null && r.next()) ? r : null;
    });
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

  PreparedStatement applyBindings(PreparedStatement statement) throws SQLException {
    int i = 1;
    for (Object o : bindings)
      if (o == null)
        statement.setObject(i++, null);
      else
        if (o instanceof String)
          statement.setString(i++, (String) o);
        else
          if (o instanceof Integer)
            statement.setInt(i++, (Integer) o);
          else
            if (o instanceof Long)
              statement.setLong(i++, (Long) o);
            else
              if (o instanceof Double)
                statement.setDouble(i++, (Double) o);
              else
                if (o instanceof Float)
                  statement.setFloat(i++, (Float) o);
                else
                  if (o instanceof Boolean)
                    statement.setBoolean(i++, (Boolean) o);
                  else
                    statement.setObject(i++, o);

    return statement;
  }

  PreparedStatement prepareSql(String sql) throws SQLException {
    return applyBindings(statement(beforeSQL(sql)));
  }

  public PreparedStatement statement(String sql) throws SQLException {
    return connection.prepareStatement(sql);
  }

  public String beforeSQL(String sql) {
    return sql;
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
