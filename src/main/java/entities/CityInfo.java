/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author chris
 */
@Entity
public class CityInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String zip;
    private String city;
    
    @OneToMany(mappedBy = "cityInfo")
    private List<Address> addresses;

    public CityInfo() {
    }

    public CityInfo(String zip, String city) {
        this.zip = zip;
        this.city = city;
        this.addresses = new ArrayList();
    }
    
    
    
 

 
}
