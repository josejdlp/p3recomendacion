/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2_recomendacion.pojo;

/**
 *
 * @author UJA
 */
public class Movie_title {

    private Integer idItem;
    private String title;
    
    public Movie_title(Integer _idItem,String _title){
        this.idItem=_idItem;
        this.title=_title;
    }
    
    
    /**
     * @return the idItem
     */
    public Integer getIdItem() {
        return idItem;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

}
