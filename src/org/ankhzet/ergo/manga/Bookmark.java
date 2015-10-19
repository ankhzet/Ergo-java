package org.ankhzet.ergo.manga;

import java.io.File;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ankh.IoC;
import org.ankhzet.ergo.db.query.ObjectsMap;
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

  protected ObjectsMap selectClause() {
    return new ObjectsMap();
  }

  public void save() {
    IoC.get(BookmarksTable.class)
      .save(selectClause());
  }

  public void delete() {
    IoC.get(BookmarksTable.class)
      .delete(selectClause());
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
  protected ObjectsMap selectClause() {
    return super.selectClause()
      .put("manga", uid);
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
  protected ObjectsMap selectClause() {
    return super.selectClause()
      .put("chapter", chapter);
  }

}
