package org.ankhzet.ergo.classfactory.annotations;

import java.lang.annotation.*;

/**
 * Mark following field as dependency autoinject target.<br/><br/>
 * Set {@code instantiate} parameter to {@code true} if you want IoC to
 * {@code make} instance for dependency instead of {@code get} it.
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface DependencyInjection {

  /**
   * Set to {@code true} if you want IoC to {@code make} instance for dependency
   * instead of {@code get} it.
   *
   * @return
   */
  boolean instantiate() default false;

}
