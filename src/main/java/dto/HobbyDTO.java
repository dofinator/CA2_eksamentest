
package dto;

import entities.Hobby;




public class HobbyDTO {
    
    private String name;

    public HobbyDTO(Hobby hobby) {
        this.name = hobby.getName();
    }
    
    
    
}
