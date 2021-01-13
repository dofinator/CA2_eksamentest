
package dto;

import entities.Hobby;




public class HobbyDTO {
    
    public String name;

    public HobbyDTO(Hobby hobby) {
        this.name = hobby.getName();
    }
    
    
    
}
