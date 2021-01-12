package facades;

import dto.UserDTO;
import entities.Hobby;
import entities.User;
import errorhandling.PersonNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import security.errorhandling.AuthenticationException;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade implements utils.UserFacadeInterface {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    @Override
    public UserDTO getUserByPhone(String phone) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT u FROM User u WHERE u.phone = :phone", User.class);
            query.setParameter("phone", phone);
            User user = (User) query.getSingleResult();
            if (user.getfName() == null) {
                throw new PersonNotFoundException("No person with given phone number exist");
            }
            return new UserDTO(user);
        } finally {
            em.close();
        }

    }

    @Override
    public List<UserDTO> getAllUsersByHobby(String hobby) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT u FROM User u JOIN u.hobbies h WHERE h.name = :hobby", User.class);
            query.setParameter("hobby", hobby);
            List<User> userList = query.getResultList();
            List<UserDTO> userDTOlist = new ArrayList();
            for (User user : userList) {
                userDTOlist.add(new UserDTO(user));
            }
            return userDTOlist;
        } finally {
            em.close();

        }
    }

    @Override
    public List<UserDTO> getAllUsersByCity(String city) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT u FROM User u JOIN u.address.cityInfo c WHERE c.city = :city", UserDTO.class);
            query.setParameter("city", city);
            List<User> userList = query.getResultList();
            List<UserDTO> userDTOlist = new ArrayList();
            for (User user : userList) {
                userDTOlist.add(new UserDTO(user));
            }
            return userDTOlist;
        } finally {
            em.close();
        }
    }

    @Override
    public int getUserCountByHobby(String hobby) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Long> getAllZipCodes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UserDTO createUSer(UserDTO userDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UserDTO editUser(UserDTO userDTO) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UserDTO deleteUser(String userName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
