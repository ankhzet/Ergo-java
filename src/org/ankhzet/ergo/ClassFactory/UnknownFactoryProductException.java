
package org.ankhzet.ergo.ClassFactory;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UnknownFactoryProductException extends Exception {
  public UnknownFactoryProductException(Object identifier) {
    super(messageFromProductIdentifier(identifier));
  }

  static String messageFromProductIdentifier(Object identifier) {
    String identifierString = null;

    if (identifier instanceof Class)
      identifierString = ((Class) identifier).getName();
    else
      identifierString = identifier.toString();

    return String.format("Doesn't know, how to produce %s", identifierString);
  }
}