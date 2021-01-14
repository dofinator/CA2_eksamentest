package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.UserDTO;
import entities.User;
import errorhandling.PersonNotFoundException;
import facades.FacadeExample;
import facades.UserFacade;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.ejb.PostActivate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.SetupTestUsers;

/**
 * @author lam@cphbusiness.dk
 */
@Path("users")
public class DemoResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final ExecutorService ES = Executors.newCachedThreadPool();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static String cachedResponse;
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("info")
    public String getInfo() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("phone/{phone}")
    public String getUserByPhone(@PathParam("phone") String phone) throws PersonNotFoundException {
        UserDTO user = FACADE.getUserByPhone(phone);
        return GSON.toJson(user);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("hobby/{hobby}")
    public String getUserByHobby(@PathParam("hobby") String hobby) throws PersonNotFoundException {
        List <UserDTO> users = FACADE.getAllUsersByHobby(hobby);
        return GSON.toJson(users);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("city/{city}")
    public String getUserByCity(@PathParam("city") String city) throws PersonNotFoundException {
        List <UserDTO> users = FACADE.getAllUsersByCity(city);
        return GSON.toJson(users);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("count/{hobby}")
    public String getCountByHobby(@PathParam("hobby") String hobby){
        long count = FACADE.getUserCountByHobby(hobby);
        return  GSON.toJson(count);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("allzips")
    public String getAllZip(@PathParam("zip") String zip){
        List <Long> zips = FACADE.getAllZipCodes();
        return GSON.toJson(zips);
    }

    @Path("add")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPerson(String user) {
        UserDTO u = GSON.fromJson(user, UserDTO.class);
        u = FACADE.createUSer(u);
        return Response.ok(u).build();
    }
    
    
    
    
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

}
