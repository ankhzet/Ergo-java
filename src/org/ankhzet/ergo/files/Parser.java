package org.ankhzet.ergo.files;

import java.io.FileReader;
import java.io.IOException;

public class Parser {

  private int MAX_BUFF_SIZE = 1024 * 64;
  private char[] buffer = new char[MAX_BUFF_SIZE];
  private int length = 0;
  private int readed = 0;
  private int start = 0;
  private int position = 0;
  private FileReader f;
  public String Token = "";

  public Parser(String src) {
    src = src.replace("file:", "");
    try {
      f = new FileReader(src);
      length = (int) f.skip(Long.MAX_VALUE);
      f.close();
      f = new FileReader(src);
    } catch (Throwable e) {
    }
    position = 0;
    readed = 0;
    next();
  }

  public void close() {
    try {
      f.close();
    } catch (IOException e) {
    }
  }

  private char c() {
    if (position >= length) {
      return 0;
    }
    if ((readed <= position) && (readed < length)) {
      start = position;
      try {
        int r = f.read(buffer, 0, (length - readed < MAX_BUFF_SIZE) ? length - readed : MAX_BUFF_SIZE);
        if (r >= 0) {
          readed += r;
        } else {
          readed = length;
        }
      } catch (IOException e) {
      }
    }
    return (position >= length) ? (char) 0 : buffer[position - start];
  }

  public int nextInt() {
    return Integer.parseInt(next());
  }

  public String next() {
    Token = "";
    char chr, tc;
    while (true) {
      chr = c();
      switch (chr) {
      case 0:
        break;
      case '\\':
        position++;
        chr = c();
        if (chr != '\\') {
          Token += '\\';
          break;
        }

        while ((chr = c()) != 0) {
          position++;
          if ((chr == 13) || (chr == 10)) 
            break;
          
        }
        continue;
      case '\t':
      case ' ':
        while (((chr = c()) == ' ') || (chr == '\t') || (chr == 13) || (chr == 10)) 
          position++;
        
        continue;
      case '\'':
      case '"':
        tc = chr;
        position++;
        while ((chr = c()) != 0) {
          position++;
          if (chr == tc) 
            break;
          
          Token += chr;
        }
        ;
        break;
      default:
        if ((chr >= '0') && (chr <= '9')) {
          while ((((chr = c()) >= '0') && (chr <= '9')) || (chr == '.')) {
            Token += chr;
            position++;
          }
        } else if (((chr >= 'a') && (chr <= 'z')) || ((chr >= 'A') && (chr <= 'Z')) || (chr == '_')) {
          while ((((chr = c()) >= '0') && (chr <= '9')) || ((chr >= 'a') && (chr <= 'z')) || ((chr >= 'A') && (chr <= 'Z')) || (chr == '_')) {
            Token += chr;
            position++;
          }
        } else if (((chr >= 1) && (chr <= 31))) {
          position++;
          continue;
        } else {
          Token += chr;
          position++;
        }

      }
      break;
    }
    return Token;
  }

  public int getLength() {
    return length;
  }

  public boolean aboveEnd() {
    return position >= length;
  }

  /**
   * Return integer between left and right tokens (current token
   * must be {@code left}).
   *
   * @returns int
   */
  public int getInt(String left, String right) throws Throwable {
    return Integer.parseInt(getValue(left, right));
  }

  /**
   * If token != parser current token - throws exception.
   *
   */
  public void check(String token) throws Throwable {
    if (!token.equalsIgnoreCase(Token)) {
      throw new Throwable(String.format("[%s] expected, but [%s] found", token, Token));
    }
  }

  /**
   * If token != parser current token - throws exception, else returns
   * next token.
   *
   * @return {@code String} next token.
   */
  public String checkNext(String token) throws Throwable {
    if (!token.equalsIgnoreCase(Token)) {
      throw new Throwable(String.format("[%s] expected, but [%s] found", token, Token));
    }
    return next();
  }

  /**
   * If token = parser current token - returns true and goes to next token.
   *
   * @return {@code true} if current token == {@code token};
   * 
   *         {@code false} current token != {@code token}
   */
  public boolean isToken(String token) throws Throwable {
    return token.equalsIgnoreCase(Token) ? !next().isEmpty() : false;
  }

  /**
   * Returns value between {@code left} and {@code right} tokens.
   * Current token must be equal to {@code left}. After that current token
   * points on {@code right} word.
   *
   * @return {@code String} token.
   */
  public String getValue(String left, String right) throws Throwable {
    String r = checkNext(left);
    nextCheck(right);
    return r;
  }

  /**
   * if next token != {@code token} - throws exception.
   *
   * @return {@code String} token.
   */
  public void nextCheck(String token) throws Throwable {
    if (!token.equalsIgnoreCase(next())) {
      throw new Throwable(String.format("[%s] expected, but [%s] found", token, Token));
    }
  }
}
