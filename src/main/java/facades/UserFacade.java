package facades;

import dto.HobbyDTO;
import dto.UserDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.User;
import errorhandling.PersonNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public UserFacade() {
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

        try {
            Query q1 = em.createQuery("SELECT a FROM Address a WHERE a.street = :street", Address.class);
            q1.setParameter("street", userDTO.street);
            Address address = (Address) q1.getSingleResult();
            if (address == null) {
                address = new Address(userDTO.street);
            }
            user.setAdress(address);

            CityInfo cityInfo = em.find(CityInfo.class, userDTO.zip);
            if (cityInfo == null) {
                cityInfo = new CityInfo(userDTO.zip, userDTO.city);
            }
            address.setCityInfo(cityInfo);

            for (HobbyDTO hobby : userDTO.hobbies) {
                Query q2 = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :hobby", Hobby.class);
                q2.setParameter("hobby", hobby.name);
                Hobby h = (Hobby) q2.getSingleResult();
                if (h == null) {
                    h = new Hobby(hobby.name);
                }
                user.addHobbies(h);
            }

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
        try {
            User user = em.find(User.class, userDTO.userName);
            if (user == null) {
                throw new PersonNotFoundException("Could not find person with given user name");
            }
            user.setfName(userDTO.fName);
            user.setlName(userDTO.lName);
            user.setPhone(userDTO.phone);

            Query query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street", Address.class);
            query.setParameter("street", userDTO.street);
            Address address = (Address) query.getSingleResult();
            if (address == null) {
                address = new Address(userDTO.street);
            }
            user.setAdress(address);

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

        try {
            User user = em.find(User.class, userDTO.userName);
            if (user == null) {
                throw new PersonNotFoundException("Could not find person with given user name");
            }

            HobbyDTO hobby = userDTO.hobbies.get(userDTO.hobbies.size() - 1);

            Query q2 = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :hobby", Hobby.class);
            q2.setParameter("hobby", hobby.name);
            Hobby h = (Hobby) q2.getSingleResult();
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
        try {
            User user = em.find(User.class, userDTO.userName);
            if (user == null) {
                throw new PersonNotFoundException("User not found");
            }

            List<HobbyDTO> newList = userDTO.hobbies;
            List<HobbyDTO> oldList = new ArrayList();

            for (Hobby hobby : user.getHobbies()) {
                oldList.add(new HobbyDTO(hobby));
            }

            Set<HobbyDTO> newHash = new HashSet<HobbyDTO>(newList);
            Set<HobbyDTO> oldHash = new HashSet<HobbyDTO>(oldList);
     
            if(newHash.size() < oldHash.size()){
            
                oldHash.removeAll(newHash);
                oldList = new ArrayList(oldHash);
                hobbyName = oldList.get(0).name;

                Query query = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :hobby");
                query.setParameter("hobby", hobbyName);
                Hobby hobby = (Hobby) query.getSingleResult();

                user.deleteHobbies(hobby);

                em.getTransaction().begin();
                em.merge(user);
                em.getTransaction().commit();
            }
            return new UserDTO(user);

        } finally {
            em.close();
        }

    }

//    @Override
//    public UserDTO deleteUser(String userName) {
//        throw 
//    }

    @Override
    public UserDTO deleteUser(String userName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
