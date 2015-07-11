package org.ankhzet.ergo.classfactory.exceptions;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FactoryException extends Exception {

  Object specifier;

  public FactoryException(Object specifier) {
    this.specifier = specifier;
  }

  public FactoryException(Object specifier, Throwable cause) {
    super(cause);
    this.specifier = specifier;
  }

  @Override
  public String getMessage() {
    return messageFromSpecifier(specifier);
  }

  protected String messageFormat() {
    return "%s";
  }

  protected String messageFromSpecifier(Object specifier) {
    return String.format(messageFormat(), specifierName(specifier));
  }

  protected String specifierName(Object specifier) {
    String identifierString;
    if (specifier instanceof Class)
      identifierString = ((Class) specifier).getName();
    else
      identifierString = specifier.toString();

    return identifierString;
  }

}
