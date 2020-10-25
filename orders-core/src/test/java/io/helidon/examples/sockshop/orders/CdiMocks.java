/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.WithAnnotations;

import lombok.extern.java.Log;

/**
 * @RegisterRestClient annotation is processed by Jersey to create a Bean that implements
 * the given interface. Adding test instances that mock interaction with the service causes
 * ambiguity. This Extension finds InjectionPoints and modifies the set of Qualifiers, so
 * the one annotated with @Mock is selected by CDI.
 */
@Log
public class CdiMocks implements Extension {
   protected Set annotated = new HashSet();

   public void collectCdiMocks(@Observes
                               @WithAnnotations({Mock.class})
                               ProcessAnnotatedType<?> pat) {
       Class<?> clazz = pat.getAnnotatedType().getJavaClass();
       if (clazz.getInterfaces().length == 0) {
          log.warning("CdiMocks: FOUND ANNOTATED CLASS, BUT IT DOES NOT IMPLEMENT ANYTHING - WILL NOT MOCK " + clazz);
       }

       for(Class<?> iface: clazz.getInterfaces()) {
          log.info("CdiMocks: FOUND MOCK FOR: " + iface);
          annotated.add(iface);
       }
   }

   public void injectMockQualifier(@Observes ProcessInjectionPoint pip) {
       InjectionPoint ip = pip.getInjectionPoint();
       Type t = ip.getAnnotated().getBaseType();
       if (!annotated.contains(t)) {
           return;
       }

       log.info("CdiMocks: INJECTING MOCK AT: " + ip);
       pip.setInjectionPoint(new InjectionPoint() {
           public Annotated getAnnotated() {
               return ip.getAnnotated();
           }

           public Bean<?> getBean() {
               return ip.getBean();
           }

           public Member getMember() {
               return ip.getMember();
           }

           public Set<Annotation> getQualifiers() {
               Set<Annotation> s = new HashSet<>();
               s.add(Mock.INSTANCE);
               return s;
           }

           public Type getType() {
               return ip.getType();
           }

           public boolean isDelegate() {
               return ip.isDelegate();
           }

           public boolean isTransient() {
               return ip.isTransient();
           }
       });
   }
}
