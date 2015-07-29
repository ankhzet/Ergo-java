package org.ankhzet.ergo.manga;

import java.io.File;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.db.DBLayer;
import org.ankhzet.ergo.db.Table;
import org.ankhzet.ergo.db.tables.BookmarksTable;
import org.ankhzet.ergo.manga.chapter.Chapter;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Bookmark {

  public interface BookmarkGenerator {

    Bookmark instantiate();

  }

  public interface StatementBuilder {

    void build(PreparedStatement s);

  }

  public Path path(Path parent) {
    return (parent != null) ? parent : (new File("")).toPath();
  }

  protected StatementBuilder saveClause(int index, StringBuilder c, StringBuilder v) {
    return null;
  }

  public void save() {
    try {
      StringBuilder c = new StringBuilder();
      StringBuilder v = new StringBuilder();

      StatementBuilder builder = saveClause(1, c, v);
      DBLayer db = IoC.get(DBLayer.class);
      BookmarksTable t = IoC.get(BookmarksTable.class);

      PreparedStatement statement = db.prepareStatement(Table.insert(t.tableName(), c.toString(), v.toString()));

      if (builder != null)
        builder.build(statement);

      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  protected StatementBuilder deleteClause(int index, StringBuilder s) {
    return null;
  }

  public void delete() {
    try {
      StringBuilder s = new StringBuilder();

      StatementBuilder builder = deleteClause(1, s);
      DBLayer db = IoC.get(DBLayer.class);
      BookmarksTable t = IoC.get(BookmarksTable.class);

      PreparedStatement statement = db.prepareStatement(Table.delete(t.tableName()) + " where " + s.toString());

      if (builder != null)
        builder.build(statement);

      statement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  public static List<Bookmark> forManga(String manga, BookmarkGenerator gen) {
    ArrayList<Bookmark> list = new ArrayList<>();

    try {
      ResultSet rs = IoC.get(BookmarksTable.class).fetch(manga);

      while (rs.next()) {
        Bookmark bookmark = gen.instantiate();
        if (bookmark.getData(rs))
          list.add(bookmark);
      }

    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return list;
  }

  boolean getData(ResultSet rs) throws SQLException {
    return true;
  }

}

class MangaBookmark extends Bookmark {

  protected String uid;

  @Override
  public Path path(Path parent) {
    parent = super.path(parent);
    return parent.resolve(uid);
  }

  @Override
  boolean getData(ResultSet rs) throws SQLException {
    return super.getData(rs) && (uid = rs.getString("manga")) != null;
  }

  @Override
  protected StatementBuilder saveClause(int index, StringBuilder c, StringBuilder v) {
    if (c.length() > 0)
      c.append(", ");

    c.append("manga");

    if (v.length() > 0)
      v.append(", ");

    v.append("?");

    StatementBuilder b = super.saveClause(index + 1, c, v);

    return (statement) -> {
      try {
        statement.setString(index, uid);
      } catch (SQLException ex) {
        ex.printStackTrace();
      }

      if (b != null)
        b.build(statement);
    };
  }

  @Override
  protected StatementBuilder deleteClause(int index, StringBuilder s) {
    if (s.length() > 0)
      s.append(" and ");

    s.append("manga = ?");

    StatementBuilder b = super.deleteClause(index + 1, s);

    return (statement) -> {
      try {
        statement.setString(index, uid);
      } catch (SQLException ex) {
        ex.printStackTrace();
      }

      if (b != null)
        b.build(statement);
    };
  }

}

class ChapterBookmark extends MangaBookmark {

  protected float chapter;

  @Override
  public Path path(Path parent) {
    parent = super.path(parent);
    String folder = (new Chapter(Float.toString(chapter))).idLong();
    return parent.resolve(folder);
  }

  @Override
  boolean getData(ResultSet rs) throws SQLException {
    return super.getData(rs) && (chapter = rs.getFloat("chapter")) > 0.f;
  }

  @Override
  protected StatementBuilder saveClause(int index, StringBuilder c, StringBuilder v) {
    if (c.length() > 0)
      c.append(", ");

    c.append("chapter");

    if (v.length() > 0)
      v.append(", ");

    v.append("?");

    StatementBuilder b = super.saveClause(index + 1, c, v);

    return (statement) -> {
      try {
        statement.setFloat(index, chapter);
      } catch (SQLException ex) {
        ex.printStackTrace();
      }

      if (b != null)
        b.build(statement);
    };
  }

  @Override
  protected StatementBuilder deleteClause(int index, StringBuilder s) {
    if (s.length() > 0)
      s.append(" and ");

    s.append("chapter = ?");

    StatementBuilder b = super.deleteClause(index + 1, s);

    return (statement) -> {
      try {
        statement.setFloat(index, chapter);
      } catch (SQLException ex) {
        ex.printStackTrace();
      }

      if (b != null)
        b.build(statement);
    };
  }

}
