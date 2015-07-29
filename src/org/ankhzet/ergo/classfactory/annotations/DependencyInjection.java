package org.ankhzet.ergo.classfactory.annotations;

import java.lang.annotation.*;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface DependencyInjection {

  boolean instantiate() default false;

}
