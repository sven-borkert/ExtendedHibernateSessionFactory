package net.borkert.persistence;

public class ExtendedSessionFactoryException
    extends RuntimeException {

  public ExtendedSessionFactoryException(String message) {
    super(message);
  }

  public ExtendedSessionFactoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExtendedSessionFactoryException(Throwable cause) {
    super(cause);
  }

}
