/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idv.john.gwt.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author johnson
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface GwtEventRegistration {
    
    String name();
    
    EventAction [] actions();
    
}
