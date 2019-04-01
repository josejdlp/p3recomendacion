/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2_recomendacion.views;

import java.util.List;
import practica2_recomendacion.pojo.Movie_title;

/**
 *
 * @author UJA
 */
public class MovieView {
    public void printMovies(List<Movie_title> l){
        for (int x=0;x<l.size();x++){
             System.out.println("Movie: "+l.get(x).getIdItem()+" - "+l.get(x).getTitle());
        }
   }
}
