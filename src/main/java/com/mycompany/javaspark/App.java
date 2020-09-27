/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.javaspark;

import static spark.Spark.*;
import java.util.*;
import com.google.gson.Gson;
import static com.mycompany.tables.Users.USERS;
import com.mycompany.tables.records.UsersRecord;
import java.sql.Connection;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;


/**
 *
 * @author Hariharan
 */
public class App {
    
    private static final Gson gson = new Gson();
    public static Connection conn = null;
    
    public Connection GetConnection() throws SQLException
    {
        
    HikariDataSource dataSource;
    dataSource = new HikariDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
    dataSource.setUsername("postgres");
    dataSource.setPassword("debl");
    
    System.out.println("Connection Created Successfully");
    
    return dataSource.getConnection();
        
    }
    
    public static void main(String args[]) throws SQLException
    {
        
        port(8500);
        App obj = new App();
        conn = obj.GetConnection();
        
        get("/",(request , response)-> {
            
            response.status(200);
           return "hello"; 
           
        });
        get("/users",(request , response)-> {
            
            DSLContext create = DSL.using(conn ,SQLDialect.POSTGRES);
            
            UsersRecord record = create.selectFrom(USERS).where(USERS.USERNAME.eq(request.body())).fetchAny();
            return record;
        });
        
        post("/users", (request, response) -> {
            
            
            
            
               
                
                String json_user = request.body();
                //System.out.println(json_user);
               
                
                
           
                DSLContext create = DSL.using(conn ,SQLDialect.POSTGRES);
                
            
                
                UserDetails user = gson.fromJson ( json_user , UserDetails.class);
               
                if(user==null)
                {
                    response.status(400);
                    return "Unable to create User";
                }
          
                
                String id = UUID.randomUUID().toString();
                user.setId(id);
                
               // System.out.println(user.getId()+user.getUserName()+user.getEmail());
                
                create.insertInto(USERS,USERS.ID, USERS.USERNAME, USERS.EMAIL).values(user.getId(),user.getUserName(),user.getEmail()).execute();

                
                return "Created Succesfully";

    
                
});
        
    }
    
}
