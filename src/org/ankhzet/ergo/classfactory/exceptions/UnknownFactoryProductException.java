package org.ankhzet.ergo.classfactory.exceptions;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UnknownFactoryProductException extends FactoryException {

  public UnknownFactoryProductException(Object specifier) {
    super(specifier);
  }

  public UnknownFactoryProductException(Object specifier, Throwable cause) {
    super(specifier, cause);
  }

  @Override
  protected String messageFormat() {
    return "Doesn't know, how to produce %s";
  }

}
