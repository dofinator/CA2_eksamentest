/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import dto.UserDTO;
import errorhandling.PersonNotFoundException;
import java.util.List;

/**
 *
 * @author chris
 */
public interface UserFacadeInterface {

    public abstract UserDTO getUserByPhone(String phone) throws PersonNotFoundException;

    public abstract List<UserDTO> getAllUsersByHobby(String hobby);

    public abstract List<UserDTO> getAllUsersByCity(String city);

    public abstract long getUserCountByHobby(String hobby);

    public abstract List<Long> getAllZipCodes();

    public abstract UserDTO createUSer(UserDTO userDTO);

    public abstract UserDTO editUser(UserDTO userDTO);

    public abstract UserDTO deleteUser(String userName);
}
