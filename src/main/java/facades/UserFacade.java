package facades;

import dto.HobbyDTO;
import dto.UserDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Role;
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
        User user;
        try {
            try {

                Query query = em.createQuery("SELECT u FROM User u WHERE u.phone = :phone", User.class);
                query.setParameter("phone", phone);
                user = (User) query.getSingleResult();

            } catch (Exception e) {
                user = null;
            }
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
    public long getUserCountByHobby(String hobby) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT COUNT(u) FROM User u JOIN u.hobbies h WHERE h.name = :hobby", User.class);
            query.setParameter("hobby", hobby);
            long count = (long) query.getSingleResult();
            return count;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Long> getAllZipCodes() {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("SELECT c FROM CityInfo c", CityInfo.class);
            List<CityInfo> cityInfos = query.getResultList();
            List<Long> allZips = new ArrayList();
            for (CityInfo c : cityInfos) {
                allZips.add(c.getZip());
            }
            return allZips;
        } finally {
            em.close();
        }
    }

    @Override
    public UserDTO createUSer(UserDTO userDTO) {

        EntityManager em = emf.createEntityManager();

        User user = new User(userDTO.userName, userDTO.userPass, userDTO.fName, userDTO.lName, userDTO.phone);
        Address address;
        Hobby h;
        try {

            try {
                Query q1 = em.createQuery("SELECT a FROM Address a WHERE a.street = :street", Address.class);
                q1.setParameter("street", userDTO.street);
                address = (Address) q1.getSingleResult();
            } catch (Exception e) {
                address = null;
            }
            if (address == null) {
                address = new Address(userDTO.street);
            }
            user.setAddress(address);

            CityInfo cityInfo = em.find(CityInfo.class, userDTO.zip);
            if (cityInfo == null) {
                cityInfo = new CityInfo(userDTO.zip, userDTO.city);
            }
            address.setCityInfo(cityInfo);

            for (HobbyDTO hobby : userDTO.hobbies) {
                try {

                    Query q2 = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :hobby", Hobby.class);
                    q2.setParameter("hobby", hobby.name);
                    h = (Hobby) q2.getSingleResult();

                } catch (Exception e) {
                    h = null;
                }
                if (h == null) {
                    h = new Hobby(hobby.name);
                }
                user.addHobbies(h);
            }

            Role role = em.find(Role.class, "user");
            
            user.addRole(role);
            
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return new UserDTO(user);
    }

    @Override
    public UserDTO editUser(UserDTO userDTO) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Address address;
        try {
            User user = em.find(User.class, userDTO.userName);

            if (user == null) {
                throw new PersonNotFoundException("Could not find person with given user name");
            }
            user.setfName(userDTO.fName);
            user.setlName(userDTO.lName);
            user.setPhone(userDTO.phone);

            try {

                Query query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street", Address.class);
                query.setParameter("street", userDTO.street);
                address = (Address) query.getSingleResult();

            } catch (Exception e) {
                address = null;
            }

            if (address == null) {
                address = new Address(userDTO.street);
            }
            user.setAddress(address);

            CityInfo cityInfo = em.find(CityInfo.class, userDTO.zip);
            if (cityInfo == null) {
                cityInfo = new CityInfo(userDTO.zip, userDTO.city);
            }
            address.setCityInfo(cityInfo);

            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();

            return new UserDTO(user);
        } finally {
            em.close();
        }

    }

    @Override
    public UserDTO addHobby(UserDTO userDTO) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Hobby h;
        try {
            User user = em.find(User.class, userDTO.userName);
            if (user == null) {
                throw new PersonNotFoundException("Could not find person with given user name");
            }

            HobbyDTO hobby = userDTO.hobbies.get(userDTO.hobbies.size() - 1);

            try {

                Query q2 = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :hobby", Hobby.class);
                q2.setParameter("hobby", hobby.name);
                h = (Hobby) q2.getSingleResult();

            } catch (Exception e) {
                h = null;
            }
            if (h == null) {
                h = new Hobby(hobby.name);
            }

            user.addHobbies(h);

            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            return new UserDTO(user);

        } finally {
            em.close();;
        }

    }

    @Override
    public UserDTO deleteHobby(UserDTO userDTO) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        String hobbyName = "";
        Hobby hobby1;
        try {
            User user = em.find(User.class, userDTO.userName);
            if (user == null) {
                throw new PersonNotFoundException("User not found");
            }

            List<HobbyDTO> hobbyListDTO = userDTO.hobbies;

            List<Hobby> hobbyList = user.getHobbies();

            for (Hobby hobby : hobbyList) {
                for (HobbyDTO hobbyDTO : hobbyListDTO) {
                    if (hobby.getName() == hobbyDTO.name) {
                        break;
                    }
                }
                hobbyName = hobby.getName();
            }

            try {

                Query query = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :hobby");
                query.setParameter("hobby", hobbyName);
                hobby1 = (Hobby) query.getSingleResult();

            } catch (Exception e) {
                hobby1 = null;
            }

            user.deleteHobbies(hobby1);

            em.getTransaction().begin();
            em.merge(user);

            em.getTransaction().commit();

            return new UserDTO(user);
        } finally {
            em.close();
        }

    }

    @Override
    public UserDTO deleteUser(String userName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
