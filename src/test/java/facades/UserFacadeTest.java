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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class UserFacadeTest {

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

    private static Hobby fodbold;
    private static Hobby tennis;
    private static Hobby gaming;
    private static Hobby bordtennis;

    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
        EntityManager em = emf.createEntityManager();

        a1 = new Address("Ostegade 2");
        a2 = new Address("Kælkegade 4");
        a3 = new Address("Kosvinget 54");

        c1 = new CityInfo(3400, "Hillerød");
        c2 = new CityInfo(3480, "Fredensborg");

        fodbold = new Hobby("Fodbold");
        tennis = new Hobby("Tennis");
        gaming = new Hobby("Gaming");
        bordtennis = new Hobby("bordtennis");

        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
//            em.createQuery("delete from User").executeUpdate();
//            em.createQuery("delete from Role").executeUpdate();

            em.createNativeQuery("DELETE FROM user_roles").executeUpdate();
            em.createNativeQuery("DELETE FROM roles").executeUpdate();
            em.createNativeQuery("DELETE FROM HOBBY_users").executeUpdate();
            em.createNativeQuery("DELETE FROM HOBBY").executeUpdate();
            em.createNativeQuery("DELETE FROM users").executeUpdate();
            em.createNativeQuery("DELETE FROM ADDRESS").executeUpdate();
            em.createNativeQuery("DELETE FROM CITYINFO").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");

            a1.setCityInfo(c1);
            a2.setCityInfo(c1);
            a3.setCityInfo(c2);

            user = new User("user", "test1", "fName1", "lName1", "45142241");
            user.addRole(userRole);
            user.addHobbies(fodbold);
            user.setAddress(a1);

            admin = new User("admin", "test2", "fName2", "lName2", "45874412");
            admin.addRole(adminRole);
            admin.addHobbies(tennis);
            admin.setAddress(a2);

            both = new User("both", "test3", "fName3", "lname3", "65887410");
            both.addRole(userRole);
            both.addRole(adminRole);
            both.addHobbies(fodbold);
            both.addHobbies(gaming);
            both.addHobbies(bordtennis);
            both.setAddress(a3);

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
        User user = facade.getVeryfiedUser("admin", "test2");
        assertEquals("admin", admin.getUserName());
    }

    @Test
    public void userByPhone() throws PersonNotFoundException {
        UserDTO u = facade.getUserByPhone("45142241");
        String expectedfName = "testEdit";
        assertEquals(expectedfName, u.fName);
    }

    @Test
    public void getAllUsersByHobby() {
        List<UserDTO> allUsers = facade.getAllUsersByHobby("Fodbold");
        int expectedSize = 2;
        assertEquals(expectedSize, allUsers.size());
    }

    @Test
    public void getAllUsersByCity() {
        List<UserDTO> allUsers = facade.getAllUsersByCity("Hillerød");
        String expectedCity = "Hillerød";
        assertEquals(expectedCity, allUsers.get(0).city);
    }

    @Test
    public void getUserCountByHobby() {
        long count = facade.getUserCountByHobby("Fodbold");
        long expCount = 1;
        assertEquals(expCount, 1);
    }

    @Test
    public void getAllZips() {
        List<Long> allZips = facade.getAllZipCodes();
        int expSize = 2;
        assertEquals(expSize, allZips.size());
    }

    @Test
    public void createUser() throws PersonNotFoundException {

        User user = new User("create", "create", "createLname", "createFname", "65887451");
        Address a = new Address("Ostegade 2");
        CityInfo c = new CityInfo(3400, "Hillerød");
        Hobby h = new Hobby("Fodbold");

        a.setCityInfo(c);
        user.setAddress(a);
        user.addHobbies(h);

        UserDTO u = facade.createUSer(new UserDTO(user));

        UserDTO userDTO = facade.getUserByPhone("65887451");

        String expected = "65887451";

        assertEquals(expected, userDTO.phone);

    }

    @Test
    public void editUser1() throws PersonNotFoundException {

        user.setfName("testEdit");

        UserDTO userDTO = facade.editUser(new UserDTO(user));

        UserDTO user = facade.getUserByPhone("45142241");

        String expectedFname = "testEdit";

        assertEquals(expectedFname, userDTO.fName);

    }

    @Test
    public void editUser2() throws PersonNotFoundException {
        Address a = new Address("Ostegade 2");
        CityInfo c = new CityInfo(3400, "Hillerød");

        a.setCityInfo(c);

        user.setAddress(a);

        UserDTO userDTO = facade.editUser(new UserDTO(user));

        assertEquals("Ostegade 2", userDTO.street);

    }

    @Test
    public void addHobby() throws PersonNotFoundException {

        user.addHobbies(tennis);

        UserDTO userDTO = facade.addHobby(new UserDTO(user));

        assertEquals(2, userDTO.hobbies.size());

    }

    @Test
    public void deleteHobby() throws PersonNotFoundException {

        both.deleteHobbies(gaming);

        UserDTO userDTO = new UserDTO(both);

        UserDTO u = facade.deleteHobby(userDTO);

        assertEquals(2, u.hobbies.size());

    }
}
