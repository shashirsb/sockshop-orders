/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * http://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.orders.atpsoda;

import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import io.helidon.config.Config;


import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lombok.extern.java.Log;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import static java.lang.String.format;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

//////////

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

import java.sql.Connection;
import java.sql.DriverManager;

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

/**
 * CDI support for MongoDB.
 */
@ApplicationScoped
@Log
public class AtpSodaProducers {

    
    /**
     * Initialise Connection object
     */
    public static Connection conn = null;

    /**
     * Initialise OracleDatabase object
     */
    public static OracleDatabase db = null;

  

    // /**
    //  * This gets config from application.yaml on classpath
    //  * and uses "app" section.
    //  */
    // private static final Config CONFIG = Config.create().get("app");

    // /**
    //  * The config value for the key {@code version}.
    //  */
    // private static String version = CONFIG.get("version").asString("1.0.0");

    /**
     * In-memory price catalog
     */
    private static java.util.Map < Integer, Double > prices;

    /**
     * In-memory product catalog
     */
    private static JsonObject catalog;

    /**
     * database data
     */
    private static boolean UseDB = true;
    private final static String ATP_CONNECT_NAME = "sockshopdb_medium";
    private final static String ATP_PASSWORD_FILENAME = "atp_password.txt";
    private final static String WALLET_LOCATION = "/home/opc/Wallet_sockshopdb";
    private final static String DB_URL = "jdbc:oracle:thin:@" + ATP_CONNECT_NAME + "?TNS_ADMIN=" + WALLET_LOCATION;
    private final static String DB_USER = "admin";
    private static String DB_PASSWORD;


    public OracleDatabase dbConnect(){
        try {

            /**
             * Connect to ATP and verify database connectivity
             */
            System.out.println("\n**checking DB catalog");

            // load password from file in wallet location
            StringBuilder contentBuilder = new StringBuilder();
            try (Stream<String> stream = Files.lines( Paths.get(WALLET_LOCATION + "/" + ATP_PASSWORD_FILENAME), StandardCharsets.UTF_8)) {
                    stream.forEach(s -> contentBuilder.append(s).append("\n"));
                }
            catch (IOException e) {
                e.printStackTrace();
            }

            DB_PASSWORD = contentBuilder.toString();

            // set DB properties
            Properties props = new Properties();
            props.setProperty("user", DB_USER);
            props.setProperty("password", DB_PASSWORD);


            // Get a JDBC connection to an Oracle instance.
            conn = DriverManager.getConnection(DB_URL, props);

             // Get an OracleRDBMSClient - starting point of SODA for Java application.
             OracleRDBMSClient cl = new OracleRDBMSClient();

             // Get a database.
             db = cl.getDatabase(conn);

            System.out.println("DB Connection established successfully!!!");
         

        } catch (Exception e) {
            e.printStackTrace();
        }
        return db;
    }

}
