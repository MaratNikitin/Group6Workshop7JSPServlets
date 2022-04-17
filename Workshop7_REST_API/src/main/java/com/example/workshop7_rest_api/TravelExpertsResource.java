/*
Author: Marat Nikitin;
Co-Author: , contribution: ;
PROJ-207 Threaded Project, Stage 3, Group 6, Workshop #7 (JSP/Servlets),
OOSD program, SAIT, March-May 2022;
This app creates a RESTful API serving select tables of the 'travelexperts' MySQL database.
This is the class where main logic of REST API is defined
*/

package com.example.workshop7_rest_api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Agent;
import model.Package;
import model.Product;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.List;

@Path("/")
public class TravelExpertsResource {

    // constructor:
    public TravelExpertsResource()
    {
        try
        {
            // ensuring that Maria DB drivers are found:
            Class.forName("org.mariadb.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    // Get a list of all packages as JSON/GSON objects:
    @Path("/packages") // URL (GET): http://localhost:8080/api/packages
    @GET // Accessible by GET Method
    @Produces(MediaType.APPLICATION_JSON) // JSON/GSON objects are returned here
    public String getPackages() {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit from the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("packages");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Query query = em.createQuery("SELECT p FROM Package p"); //selecting all packages
        List<Package> packagesList = query.getResultList(); // creating a list for Package class objects
        Type type = new TypeToken<List<Package>>(){}.getType(); // setting a Type of JSON objects
        Gson gson = new Gson(); // introducing JSON/GSON object
        response = gson.toJson(packagesList, type); // saving JSON/GSON list to be returned
        em.close(); // mission accomplished, connection closed
        return response; // returning a JSON/GSON list of packages
    } // end of getPackages

    // Get a list of all products in a selected Package found by packageId passed in URL:
    @Path("/getproducts/{PackageId}") // URL (GET): http://localhost:8080/api/getproducts/1
    @GET // Accessible by GET Method
    @Produces(MediaType.APPLICATION_JSON) // JSON/GSON objects are returned here
    public String getProductsOfPackage(@PathParam("PackageId") int packageId) {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit from the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("products");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        // creating a list of Products belonging to the selected package is created using a good old raw SQL:
        List<Product> productList = em.createNativeQuery("SELECT products.ProductId, products.ProdName " +
                "FROM products JOIN products_suppliers ON products.ProductId = products_suppliers.ProductId " +
                "JOIN packages_products_suppliers ON products_suppliers.ProductSupplierId = packages_products_suppliers.ProductSupplierId " +
                "WHERE packages_products_suppliers.PackageId="+packageId).getResultList();
        Type type = new TypeToken<List<Product>>(){}.getType(); // setting a Type of JSON objects
        Gson gson = new Gson(); // introducing JSON/GSON object
        response = gson.toJson(productList, type); // saving JSON/GSON list to be returned
        em.close(); // mission accomplished, connection closed
        return response; // returning a JSON/GSON list of packages
    } // end of getPackages

    // Get a Package with a specified id:
    @Path("/getpackage/{PackageId}") // URL(GET): http://localhost:8080/api/getpackage/2
    @GET // Accessible by GET Method
    @Produces(MediaType.APPLICATION_JSON) // JSON objects are returned here
    public String getPackage(@PathParam("PackageId") int packageId) // int packageId is assigned from the URL
    {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit of the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("packages");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Package myPackage = em.find(Package.class, packageId); // finding a package by packageId value from the URL
        Gson gson = new Gson(); // introducing JSON/GSON object
        response = gson.toJson(myPackage); // saving agent as JSON/GSON to be returned
        em.close(); // mission accomplished - connection closed
        return response; // returning a JSON/GSON with the selected Agent
    }

    // Update a Package or insert a new Package in the database using a POST method:
    // If a unigue packageId is sent to the DB, then insert operation is done, but the packageID is changed (auto-incremented)
    // If an existing packageId is sent to the DB in request body, then that package is updated
    @Path("/updatepackage") // URL(POST): http://localhost:8080/api/updatepackage
    @POST // Accessible by POST Method
    @Produces(MediaType.APPLICATION_JSON) // simple feedback message for a user comes back in JSON format
    @Consumes({MediaType.APPLICATION_JSON}) // send a Package as JSON in your request body
    public String updatePackage(String JSONString)
    {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit of the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("packages");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Gson gson = new Gson(); // introducing JSON/GSON object
        Package myPackage = gson.fromJson(JSONString, Package.class); // creating a Package class instance from JSON request body
        em.getTransaction().begin(); // opening a transaction
        Package mergedPackage = em.merge(myPackage); // trying to update or insert a Package in the DB
        if(mergedPackage != null)
        { // updating or inserting a Package in the DB was successful
            em.getTransaction().commit(); // transaction is committed - changes are saved in the DB
            em.close(); // mission accomplished - connection closed
            response = "{'message', 'Package was updated or inserted successfully'}"; // positive feedback message is saved
        }
        else
        { // updating a Package in the DB failed
            em.getTransaction().rollback(); // transaction rollback - no need to save faulty changes
            em.close(); // connection is closed even though the insert/update failed
            response = "{'message', 'Failed to update or insert a package'}"; // preparing bad news message
        }
        return response; // message for a user about the update/insert status is returned
    }

    // Add/insert a new Package in the database using a PUT method:
    @Path("/addpackage") // URL(PUT): http://localhost:8080/api/addpackage
    @PUT // Accessible by PUT Method
    @Produces(MediaType.APPLICATION_JSON) // simple feedback message for a user comes back in JSON format
    @Consumes({MediaType.APPLICATION_JSON}) // send a Package as JSON in your request body
    public String addPackage(String JSONString)
    {
        String response = ""; // introducing and initiating the variable to be returned
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("packages");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Gson gson = new Gson(); // introducing JSON/GSON object
        Package myPackage = gson.fromJson(JSONString, Package.class); // creating an Agent class instance from JSON request body
        em.getTransaction().begin(); // opening a transaction
        em.persist(myPackage); // trying to insert a new Agent in the DB
        if(em.contains(myPackage))
        { // inserting an Agent in the DB was successful
            em.getTransaction().commit(); // transaction is committed - changes are saved in the DB
            em.close(); // mission accomplished - connection closed
            response = "{'message', 'Package was inserted successfully'}"; // positive feedback message is saved
        }
        else
        { // inserting an Package in the DB failed
            em.getTransaction().rollback(); // transaction rollback - no need to save faulty changes
            em.close(); // connection is closed even though the insert failed
            response = "{'message', 'Failed to insert a package'}"; // preparing bad news message
        }
        return response; // message for a user about the insert status is returned
    }

    // Delete a Package with an packageId specified in the URL:
    @Path("/deletepackage/{PackageId}") // URL(DELETE): http://localhost:8080/api/deletepackage/11
    @DELETE // Accessible by DELETE Method
    @Produces(MediaType.APPLICATION_JSON) // simple feedback message for a user comes back in JSON format
    public String deletePackage(@PathParam("PackageId") int packageId)
    {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit of the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("packages");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Package myPackage = em.find(Package.class, packageId); // finding the Package to be deleted in the DB
        em.getTransaction().begin(); // opening a transaction
        em.remove(myPackage); // deleting the Package
        if(!em.contains(myPackage))
        { // delete attempts was successful
            em.getTransaction().commit(); // saving changes in the DB
            em.close(); // mission accomplished - connection closed
            response = "{'message', 'Package was deleted successfully'}"; // preparing good news message
        }
        else
        {
            em.getTransaction().rollback(); // transaction rollback - no need to save faulty changes
            em.close(); // connection is closed even though the delete failed
            response = "{'message', 'Failed to delete package'}"; // preparing bad news message
        }
        return response; // message for a user about the insert status is returned
    }

    // Get a list of all Agents as JSON/GSON objects:
    @Path("agents") // URL: http://localhost:8080/api/agents
    @GET // Accessible by GET Method
    @Produces(MediaType.APPLICATION_JSON) // JSON objects are returned here
    public String getAgents()
    {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit of the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("agents");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Query query = em.createQuery("SELECT a FROM Agent a"); //select all agents
        List<Agent> agentList = query.getResultList(); // creating a list for Agent class objects
        Type type = new TypeToken<List<Agent>>(){}.getType(); // setting a Type of JSON objects
        Gson gson = new Gson(); // introducing JSON/GSON object
        response = gson.toJson(agentList, type); // saving JSON/GSON list to be returned
        em.close(); // mission accomplished - connection closed
        return response; // returning a JSON/GSON list of agents
    }

    // Get an Agent with a specified id:
    @Path("/getagent/{AgentId}") // URL: http://localhost:8080/api/getagent/5
    @GET // Accessible by GET Method
    @Produces(MediaType.APPLICATION_JSON) // JSON objects are returned here
    public String getAgent(@PathParam("AgentId") int agentId) // int agentId is assigned from the URL
    {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit of the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("agents");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Agent agent = em.find(Agent.class, agentId); // finding an Agent by agentId value from the URL
        Gson gson = new Gson(); // introducing JSON/GSON object
        response = gson.toJson(agent); // saving agent as JSON/GSON to be returned
        em.close(); // mission accomplished - connection closed
        return response; // returning a JSON/GSON with the selected Agent
    }

    // Update an Agent or insert a new Agent in the database using a POST method:
        // If a unigue agentId is sent to the DB, then insert operation is done, but the agentID is changed (auto-incremented)
    @Path("/updateagent") // URL (POST): http://localhost:8080/api/updateagent
    @POST // Accessible by POST Method
    @Produces(MediaType.APPLICATION_JSON) // simple feedback message for a user comes back in JSON format
    @Consumes({MediaType.APPLICATION_JSON}) // send an Agent as JSON in your request body
    public String updateAgent(String JSONString)
    {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit of the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("agents");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Gson gson = new Gson(); // introducing JSON/GSON object
        Agent agent = gson.fromJson(JSONString, Agent.class); // creating an Agent class instance from JSON request body
        em.getTransaction().begin(); // opening a transaction
        Agent mergedAgent = em.merge(agent); // trying to update or insert an Agent in the DB
        if(mergedAgent != null)
        { // updating or inserting an Agent in the DB was successful
            em.getTransaction().commit(); // transaction is committed - changes are saved in the DB
            em.close(); // mission accomplished - connection closed
            response = "{'message', 'Agent was updated or inserted successfully'}"; // positive feedback message is saved
        }
        else
        { // updating an Agent in the DB failed
            em.getTransaction().rollback(); // transaction rollback - no need to save faulty changes
            em.close(); // connection is closed even though the update failed
            response = "{'message', 'Failed to update or insert an agent'}"; // preparing bad news message
        }
        return response; // message for a user about the update/insert status is returned
    }

    // Add/insert a new Agent in the database using a PUT method:
    @Path("/addagent") // URL (PUT): http://localhost:8080/api/addagent
    @PUT // Accessible by PUT Method
    @Produces(MediaType.APPLICATION_JSON) // simple feedback message for a user comes back in JSON format
    @Consumes({MediaType.APPLICATION_JSON}) // send an Agent as JSON in your request body
    public String addAgent(String JSONString)
    {
        String response = ""; // introducing and initiating the variable to be returned
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("agents");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Gson gson = new Gson(); // introducing JSON/GSON object
        Agent agent = gson.fromJson(JSONString, Agent.class); // creating an Agent class instance from JSON request body
        em.getTransaction().begin(); // opening a transaction
        em.persist(agent); // trying to insert a new Agent in the DB
        if(em.contains(agent))
        { // inserting an Agent in the DB was successful
            em.getTransaction().commit(); // transaction is committed - changes are saved in the DB
            em.close(); // mission accomplished - connection closed
            response = "{'message', 'Agent was inserted successfully'}"; // positive feedback message is saved
        }
        else
        { // inserting an Agent in the DB failed
            em.getTransaction().rollback(); // transaction rollback - no need to save faulty changes
            em.close(); // connection is closed even though the insert failed
            response = "{'message', 'Failed to insert agent'}"; // preparing bad news message
        }
        return response; // message for a user about the insert status is returned
    }

    // Delete an Agent with an agentId specified in the URL:
    @Path("/deleteagent/{AgentId}") // URL (DELETE): http://localhost:8080/api/deleteagent/24
    @DELETE // Accessible by DELETE Method
    @Produces(MediaType.APPLICATION_JSON) // simple feedback message for a user comes back in JSON format
    public String deleteAgent(@PathParam("AgentId") int agentId)
    {
        String response = ""; // introducing and initiating the variable to be returned
        // referencing a persistence unit of the persistence.xml file:
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("agents");
        EntityManager em = emf.createEntityManager(); // introducing EntityManager instance - opening DB connection
        Agent agent = em.find(Agent.class, agentId); // finding the Agent to be deleted in the DB
        em.getTransaction().begin(); // opening a transaction
        em.remove(agent); // deleting the Agent
        if(!em.contains(agent))
        { // delete attempts was successful
            em.getTransaction().commit(); // saving changes in the DB
            em.close(); // mission accomplished - connection closed
            response = "{'message', 'Agent was deleted successfully'}"; // preparing good news message
        }
        else
        {
            em.getTransaction().rollback(); // transaction rollback - no need to save faulty changes
            em.close(); // connection is closed even though the delete failed
            response = "{'message', 'Failed to delete agent'}"; // preparing bad news message
        }
        return response; // message for a user about the insert status is returned
    }

} // end of TravelExpertsResource class