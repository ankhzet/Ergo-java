package org.ankhzet.ergo.classfactory.exceptions;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FailedFactoryProductException extends FactoryException {

  public FailedFactoryProductException(Object specifier) {
    super(specifier);
  }

  public FailedFactoryProductException(Object specifier, Throwable cause) {
    super(specifier, cause);
  }

  @Override
  protected String messageFormat() {
    return "Doesn't know, how to produce %s";
  }
  
}
