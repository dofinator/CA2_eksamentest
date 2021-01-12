package facades;

import dto.UserDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import utils.EMF_Creator;
import entities.Role;
import entities.User;
import errorhandling.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class FacadeExampleTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;

    private static User user;
    private static User admin;
    private static User both;

    private static Address a1;
    private static Address a2;
    private static Address a3;

    private static CityInfo c1;
    private static CityInfo c2;

    private static Hobby h1;
    private static Hobby h2;

    public FacadeExampleTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory();
        facade = UserFacade.getUserFacade(emf);
        EntityManager em = emf.createEntityManager();

        a1 = new Address("Ostegade 2");
        a2 = new Address("Kælkegade 4");
        a3 = new Address("Kosvinget 54");

        c1 = new CityInfo(3400, "Hillerød");
        c2 = new CityInfo(3480, "Fredensborg");

        h1 = new Hobby("Fodbold");
        h2 = new Hobby("Tennis");

        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
//            em.createQuery("delete from User").executeUpdate();
//            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");

            a1.setCityInfo(c1);
            a2.setCityInfo(c1);
            a3.setCityInfo(c2);

            user = new User("test1", "test1", "fName1", "lName1", "45142241");
            user.addRole(userRole);
            user.addHobbies(h1);
            user.setAdress(a1);

            admin = new User("test2", "test2", "fName2", "lName2", "45874412");
            admin.addRole(adminRole);
            admin.addHobbies(h2);
            admin.setAdress(a2);

            both = new User("test3", "test3", "fName3", "lname3", "65887410");
            both.addRole(userRole);
            both.addRole(adminRole);
            both.addHobbies(h1);
            both.setAdress(a3);

            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {

    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void verifyUser() throws AuthenticationException {
        User user = facade.getVeryfiedUser("test2", "test2");
        assertEquals("test2", admin.getUserName());
    }

    @Test
    public void userByPhone() throws PersonNotFoundException {
        UserDTO u = facade.getUserByPhone("45142241");
        String expectedfName = "fName1";
        assertEquals(expectedfName, u.fName);
    }
    
    @Test
    public void getAllUsersByHobby(){
        List<UserDTO> allUsers = facade.getAllUsersByHobby("Fodbold");
        int expectedSize = 2;
        assertEquals(expectedSize, allUsers.size());
    }
    
    @Test
    public void getAllUsersByCity(){
        List<UserDTO> allUsers = facade.getAllUsersByCity("Hillerød");
        String expectedCity = "Hillerød";
        assertEquals(expectedCity, allUsers.get(0).city);
    }
}
