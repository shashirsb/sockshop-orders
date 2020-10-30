#!/bin/sh 

SOCKSHOP_CATALOG_ATPSODA=$HOME/helidon/final-code/sockshop-orders/orders-atpsoda

for i in ojdbc8 ucp osdt_core osdt_cert oraclepki javax.json-1.0.4 json-20200518 json-simple-1.1 nio_char orajsoda-1.1.4 org.apache.commons.io  xmlparserv2; do  mvn install:install-file -Dfile=$SOCKSHOP_CATALOG_ATPSODA/src/main/resources/libs/$i.jar -DgroupId=com.oracle.jdbc -DartifactId=$i -Dversion=19.3.0 -Dpackaging=jar  -DgeneratePom=true; done;
