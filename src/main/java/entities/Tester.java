/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import dto.HobbyDTO;
import dto.UserDTO;
import errorhandling.PersonNotFoundException;
import facades.UserFacade;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import utils.UserFacadeInterface;

/**
 *
 * @author chris
 */
public class Tester {
    
    public static void main(String[] args) throws PersonNotFoundException {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
        EntityManager em = emf.createEntityManager();
        
        UserFacade userFacade = UserFacade.getUserFacade(emf);
        
        User u1 = new User("test", "test", "maja", "wegner", "28939740");
        User u2 = new User("test1", "test1", "maja1", "wegner1", "25548745");
        User u3 = new User("test2", "test2", "maja2", "wegner2", "25588745");
        Hobby fodbold = new Hobby("fodbold");
        Hobby tennis = new Hobby("tennis");
        Hobby gaming = new Hobby("gaming");
        Address a1 = new Address("Slangerupgade 27a");
        Address a2 = new Address("Slangerupgade 27asda");
        Role userRole = new Role("user");
        Role adminRole = new Role("admin");
        CityInfo c1 = new CityInfo(3400, "Hillerød");
        CityInfo c2 = new CityInfo(3409, "Hillerød");

        a1.setCityInfo(c1);
        a2.setCityInfo(c1);
        a2.setCityInfo(c2);

        u1.addRole(userRole);
        u1.setAddress(a1);
        u1.addHobbies(tennis);
        u1.addHobbies(fodbold);
        u1.addHobbies(gaming);
        
        
        u2.setAddress(a2);
        u2.addHobbies(fodbold);
        u2.addRole(userRole);
        
        u3.addHobbies(tennis);
        u3.setAddress(a2);
        u3.addRole(userRole);
        
        em.getTransaction().begin();
        em.persist(userRole);
        em.persist(u1);
        em.persist(u2);
        em.persist(u3);
       
        em.getTransaction().commit();
        
        UserDTO userDTO = userFacade.getUserByPhone("28939740");
        
        System.out.println(userDTO.fName);
        
        System.out.println("******** TEST DELETE HOBBY *********\n");
        
      
        
// 
        
    }
}
