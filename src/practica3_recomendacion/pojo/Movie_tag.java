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
public class Movie_tag {

    
    private Integer idItem;
    private String tag;

    
    public Movie_tag(Integer _idItem,String _tag){
        this.idItem=_idItem;
        this.tag=_tag;
    }
    
    /**
     * @return the idItem
     */
    public Integer getIdItem() {
        return idItem;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }
    
    @Override
    public boolean equals(Object o) 
    { 
        Movie_tag t; 
        if(!(o instanceof Movie_tag)) 
        { 
            return false; 
        } 
          
        else
        { 
            t=(Movie_tag)o; 
            if(this.idItem.equals(t.getIdItem()) && this.tag.equals(t.getTag())) 
            { 
                return true; 
            } 
        } 
        return false; 
    } 
 
}
