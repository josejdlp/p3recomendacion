/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3_recomendacion.pojo;

/**
 *
 * @author josejimenezdelapaz
 */
public class Recomendacion {

   
    private Integer idMovie;
    private Double valor;
    
    public Recomendacion(){}
           
    public Recomendacion(Integer _idMovie,Double _valor){
        this.idMovie=_idMovie;
        this.valor=_valor;
    }
    
    
    /**
     * @return the idMovie
     */
    public Integer getIdMovie() {
        return idMovie;
    }

    /**
     * @return the valor
     */
    public Double getValor() {
        return valor;
    }

 /**
     * @param idMovie the idMovie to set
     */
    public void setIdMovie(Integer idMovie) {
        this.idMovie = idMovie;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(Double valor) {
        this.valor = valor;
    }
}
