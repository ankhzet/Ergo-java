/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ankhzet.ergo.classfactory.annotations;

import java.lang.annotation.*;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
@Target(value=ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(DependenciesInjecteds.class)
public @interface DependenciesInjected {
  boolean suppressInherited() default false;
  boolean beforeInherited() default false;
}
