package org.ankhzet.ergo.db.query;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Where {

  static final int JOIN_OR = 0;
  static final int JOIN_AND = 0;

  int join = JOIN_AND;
  String condition;

  public Where(String condition) {
    this.condition = condition;
  }

  public Where(int join, String condition) {
    this.join = join;
    this.condition = condition;
  }

  public String join() {
    return join == JOIN_OR ? "or" : "and";
  }

}
