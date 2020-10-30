/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders.atpsoda;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import io.helidon.examples.sockshop.orders.*;


import com.mongodb.client.MongoCollection;
import org.eclipse.microprofile.opentracing.Traced;

import static com.mongodb.client.model.Filters.eq;
import static javax.interceptor.Interceptor.Priority.APPLICATION;


//////////////////

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.opentracing.Traced;


import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import java.io.*;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import oracle.soda.rdbms.OracleRDBMSClient;
import oracle.soda.OracleDatabase;
import oracle.soda.OracleCursor;
import oracle.soda.OracleCollection;
import oracle.soda.OracleDocument;
import oracle.soda.OracleException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import org.apache.commons.lang3.StringUtils;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * An implementation of {@link io.helidon.examples.sockshop.orders.OrderRepository}
 * that that uses MongoDB as a backend data store.
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION)
@Traced
public class AtpSodaOrderRepository implements OrderRepository {
    public static AtpSodaProducers asp = new AtpSodaProducers();
    public static OracleDatabase db = asp.dbConnect();

    @Inject
    AtpSodaOrderRepository() {
        try {
            String UserResponse = createData();
            System.out.println(UserResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order get(String orderId) {
        // return orders.find(eq("orderId", orderId)).first();
        
        Order orders = new Order();

        org.json.simple.JSONObject _jsonObject = new JSONObject();
        org.json.simple.parser.JSONParser _parser = new JSONParser();


        try {
      
            OracleCollection col = this.db.admin().createCollection("orders");

            // Find a documents in the collection.
            OracleDocument filterSpec =
                this.db.createDocumentFromString("{ \"orderId\" : \"" + orderId + "\"}");
            OracleCursor c = col.find().filter(filterSpec).getCursor();
            String jsonFormattedString = null;
            try {
                OracleDocument resultDoc;

                while (c.hasNext()) {

                    // String orderId, String carrier, String trackingNumber, LocalDate deliveryDate
                    resultDoc = c.next();
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(resultDoc.getContentAsString());
                    JSONObject jsonObject = (JSONObject) obj;

                    
                    //orders.customer = jsonObject.get("customer").toString(); // Convert to Customer
                    obj = parser.parse(jsonObject.get("customer").toString());
                    JSONObject _customer = (JSONObject) obj;
                    orders.customer =  Customer.builder().id(_customer.get("id").toString()).firstName(_customer.get("firstName").toString()).lastName(_customer.get("lastName").toString()).email(_customer.get("email").toString()).build();

                   // orders.address = jsonObject.get("address").toString();  // Convert to Address 

                   obj = parser.parse(jsonObject.get("address").toString());
                   JSONObject _address = (JSONObject) obj;
                   Address address =  Address.builder().number(_address.get("number").toString()).street(_address.get("street").toString()).city(_address.get("city").toString()).postcode(_address.get("postcode").toString()).country(_address.get("country").toString()).build();

                   // orders.card = jsonObject.get("card").toString();        // Convert to Card

                   obj = parser.parse(jsonObject.get("card").toString());
                   JSONObject _card = (JSONObject) obj;
                    orders.card =  Card.builder().longNum(_card.get("longNum").toString()).expires(_card.get("expires").toString()).ccv(_card.get("ccv").toString()).build();

                    orders.orderId = jsonObject.get("orderId").toString();

                    // String str = "2020-10-29T14:17:02.216+00:00"; 
                    String strDatewithTime = jsonObject.get("$date").toString();
                    LocalDateTime aLDT = LocalDateTime.parse(strDatewithTime);
                    orders.date = aLDT;

                    orders.total = Float.parseFloat(jsonObject.get("total").toString());

                    //orders.items = jsonObject.get("items").toString();       // Convert to Collection<Item>
                    JSONArray _itemsArray = (JSONArray)jsonObject.get("items");            
                    List<Item> items = new ArrayList<>();

                    // Iterator<String> it = _itemsArray.iterator();
                    // while (it.hasNext()) {
                    //     System.out.println("City = " + it.next());
                    // }
                    
                    for(Object o: _itemsArray){
                        if ( o instanceof JSONObject ) {
                            JSONObject _itemsObject = (JSONObject) o;
                            items.add(Item.builder()
                            .itemId(_itemsObject.get("itemId").toString())
                            .quantity(Integer.parseInt(_itemsObject.get("quantity").toString()))
                            .unitPrice(Float.parseFloat(_itemsObject.get("unitPrice").toString()))
                            .build());
                        }
                    }
                    orders.items = items;

                    

                    //orders.payment = jsonObject.get("payment").toString();   // Convert to Payment
                    obj = parser.parse(jsonObject.get("payment").toString());
                   JSONObject _payment = (JSONObject) obj;
                    orders.payment =   Payment.builder().authorised(Boolean.parseBoolean(_payment.get("authorised").toString())).message(_payment.get("message").toString()).build();

                    //orders.shipment = jsonObject.get("shipment").toString(); //Convert to Shipment
                    obj = parser.parse(jsonObject.get("shipment").toString());
                   JSONObject _shipment = (JSONObject) obj;
                    orders.shipment =   Shipment.builder().carrier(_shipment.get("carrier").toString()).trackingNumber(_shipment.get("trackingNumber").toString()).deliveryDate( LocalDate.parse(_shipment.get("deliveryDate").toString())).build();

                                   
                    Order.Status status = Order.Status.valueOf(jsonObject.get("status").toString());
                    orders.status =  status;    

                    orders.links = Links.order(orderId);        // Convert to Link

                                    }
            } finally {
                // IMPORTANT: YOU MUST CLOSE THE CURSOR TO RELEASE RESOURCES.
                if (c != null) c.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Shipment getShipment.. GET Request 200OK");
        return orders;

    }

    @Override
    public Collection<? extends Order> findOrdersByCustomer(String customerId) {
        ArrayList<Order> results = new ArrayList<>();

        // orders.find(eq("customer._id", customerId))
        //         .forEach((Consumer<? super Order>) results::add);

        // return results;

        org.json.simple.JSONObject _jsonObject = new JSONObject();
        org.json.simple.parser.JSONParser _parser = new JSONParser();


        try {

            // Get a collection with the name "socks".
            // This creates a database table, also named "socks", to store the collection.
            OracleCollection col = this.db.admin().createCollection("orders");

            // Find all documents in the collection.
            OracleCursor c = null;
            String jsonFormattedString = null;
            try {

                // OracleDocument filterSpec = this.db.createDocumentFromString("{ \"customerId\" : \"" + customerId + "\"}");

                // c = col.find().filter(filterSpec).getCursor();

                // OracleDocument resultDoc;
                // while (c.hasNext()) {
                //     Order order = new Order();
                //     resultDoc = c.next();

                //     JSONParser parser = new JSONParser();
                //     Object obj = parser.parse(resultDoc.getContentAsString());
                //     JSONObject jsonObject = (JSONObject) obj;


                //     //Customer(String id, String firstName, String lastName, String email 
                //     obj = parser.parse(jsonObject.get("customer").toString());
                //     JSONObject _customer = (JSONObject) obj;
                //     order.customer = Customer.builder().id(_customer.get("_id").toString()).firstName(_customer.get("firstName").toString()).lastName(_customer.get("lastName").toString()).email(_customer.get("email").toString()).build();

            
                //   //String number, String street, String city, String postcode, String country
                //     obj = parser.parse(jsonObject.get("address").toString());
                //     JSONObject _address = (JSONObject) obj;
                //     order.address = Adress(_address.get("number").toString(),_address.get("street").toString(),_address.get("city").toString(),_address.get("postcode").toString(),_address.get("country").toString());
                //     //String longNum, String expires, String ccv
                //     obj = parser.parse(jsonObject.get("card").toString());
                //     JSONObject _card = (JSONObject) obj;
                //     order.card = Card(_card.get("longNum").toString(),_card.get("expires").toString(),_card.get("ccv").toString());
                    
                //      //orders.items = jsonObject.get("items").toString();       // Convert to Collection<Item>
                //      JSONArray _itemsArray = jsonObject.getJSONArray("items");            
                //      List<Item> items = new ArrayList<>();
 
                //      for(Object o: _itemsArray){
                //          if ( o instanceof JSONObject ) {
                //              items.add(new Item(o.itemId.toString(i),Integer.valueOf(o.quantity), Integer.valueOf(o.unitPrice).floatValue()));
                //          }
                //      }
                //      orders.items = items;                

                //    // String str = "2020-10-29T14:17:02.216+00:00"; 
                //    String strDatewithTime = jsonObject.get("time").toString();
                //    LocalDateTime aLDT = LocalDateTime.parse(strDatewithTime);


                //    order.date = aLDT;
                //    order.orderId = jsonObject.get("orderId").toString();

                //    Order.Status status = Order.Status.valueOf(jsonObject.get("status").toString());
                //    orders.status =  status;   

                //     results.add(order);
                // }
                


            } finally {
                // IMPORTANT: YOU MUST CLOSE THE CURSOR TO RELEASE RESOURCES.
                if (c != null) c.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Collection<? extends Order> findOrdersByCustomer GET Request 200OK");
        
        return results;
    }

    @Override
    public void saveOrder(Order order) {
        //orders.insertOne(order);

        
        try {

            // OracleCollection col = this.db.admin().createCollection("orders");
           

            // JSONObject objaddress = new JSONObject();
            // objaddress.put("city", order.address.city.toString());
            // objaddress.put("country", order.address.country.toString());
            // objaddress.put("number", order.address.number.toString());
            // objaddress.put("postcode", order.address.postcode.toString());
            // objaddress.put("street", order.address.street.toString());

            // JSONObject objcard = new JSONObject();
            // objcard.put("ccv", order.card.ccv.toString());
            // objcard.put("expires", order.card.expires.toString());
            // objcard.put("longNum", order.card.longNum.toString());

            // JSONObject objcustomer = new JSONObject();
            // objcustomer.put("_id", order.customer._id.toString());
            // objcustomer.put("email", order.customer.email.toString());
            // objcustomer.put("firstName", order.customer.firstName.toString());
            // objcustomer.put("lastName", order.customer.lastName.toString());

            // JSONObject obj$date = new JSONObject();
            // obj$date.put("$date", order.date.$date.toString());

            // JSONObject objdate = new JSONObject();
            // objdate.put("$date", bj$date.toString());

            // JSONObject objhref = new JSONObject();
            // objhref.put("href", "http://orders/orders/"+ order.orderId.toString());

            // JSONObject objlinks = new JSONObject();
            // objlinks.put("self", objhref.toString());

            // JSONObject objpayment = new JSONObject();
            // objpayment.put("authorised", order.payment.authorised);
            // objpayment.put("message", order.payment.message.toString());

            // JSONObject objdeliveryDate = new JSONObject();
            // objdeliveryDate.put("$date", order.shipment.deliveryDate.date.toString());

            // JSONObject objshipment = new JSONObject();
            // objshipment.put("carrier", order.shipment.carrier.toString());
            // objshipment.put("deliveryDate", objdeliveryDate.toString());
            // objshipment.put("trackingNumber", order.shipment.trackingNumber.toString());

            // JSONArray arrayitems = new JSONArray();
            // Collection<Item> items = order.shipment.items;
            //     for (Item item : items) {
            //         JSONObject objitems= new JSONObject();
            //         objitems.put("itemId", item.itemId.toString());
            //         objitems.put("quantity", item.quantity);
            //         objitems.put("unitPrice", item.unitPrice);
            //         arrayitems.add(objitems);
            //     }

  
            // JSONObject objmain = new JSONObject();
            // objmain.put("orderId", order.orderId.toString());
            // objmain.put("status", order.status.toString());
            // objmain.put("total", order.total);
            // objmain.put("address", objaddress.toString());
            // objmain.put("card", objcard.toString());
            // objmain.put("customer", objcustomer.toString());
            // objmain.put("date", objdate.toString());
            // objmain.put("items", arrayitems.toString());
            // objmain.put("links", objlinks.toString());
            // objmain.put("payment", objpayment.toString());
            // objmain.put("shipment", objshipment.toString());           


            // String _document = objmain.toString();
            // System.out.println(_document);
    
            // // Create a JSON document.
            // OracleDocument doc =
            //     this.db.createDocumentFromString(_document);

            // // Insert the document into a collection.
            // col.insert(doc);
            // System.out.println("saveOrder .... 200OK");
        } catch (OracleException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String createData() {

        try {

            OracleCollection col = this.db.admin().createCollection("orders");

            col.admin().truncate();


        } catch (OracleException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "successfully created orders collection !!!";
    }
}
