package dto;

import entities.Hobby;
import entities.User;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    public String userName;
    public String userPass;
    public String fName;
    public String lName;
    public String phone;
    public String street;
    public long zip;
    public String city;
    public List<HobbyDTO> hobbies;

    public UserDTO(User user) {
        this.userName = user.getUserName();
        this.userPass = user.getUserPass();
        this.fName = user.getfName();
        this.lName = user.getlName();
        this.phone = user.getPhone();
        this.street = user.getAddress().getStreet();
        this.zip = user.getAddress().getCityInfo().getZip();
        this.city = user.getAddress().getCityInfo().getCity();
        this.hobbies = new ArrayList();
        for (Hobby hobby : user.getHobbies()) {
            this.hobbies.add(new HobbyDTO(hobby));
        }
    }

    

    

}
