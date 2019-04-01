/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3_recomendacion.pojo;

/**
 *
 * @author UJA
 */
public class Rating {
    private Integer idUser;
    private Integer idItem;
    private Double rating;
    
    public Rating(Integer _idUser,Integer _idItem,Double _rating){
        this.idUser=_idUser;
        this.idItem=_idItem;
        this.rating=_rating;  
    }
    
    
     /**
     * @return the idUser
     */
    public Integer getIdUser() {
        return idUser;
    }

    /**
     * @return the idItem
     */
    public Integer getIdItem() {
        return idItem;
    }

    /**
     * @return the rating
     */
    public Double getRating() {
        return rating;
    }
}
