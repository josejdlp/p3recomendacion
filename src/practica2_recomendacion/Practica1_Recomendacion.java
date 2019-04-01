/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2_recomendacion;

import practica2_recomendacion.pojo.Movie_tag;
import practica2_recomendacion.pojo.User;
import practica2_recomendacion.pojo.Movie_title;
import practica2_recomendacion.pojo.Rating;
import practica2_recomendacion.views.MovieView;
import practica2_recomendacion.views.RatingView;
import practica2_recomendacion.views.UserView;
import practica2_recomendacion.views.TagView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author UJA
 */



public class Practica1_Recomendacion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
       System.out.println("Bienvenido al Sistema de Recomendación de Películas");
       System.out.println("Cargando FICHEROS......");
       //Models
       List<Movie_title> lmovie=new ArrayList<>();
       List<Rating> lrating=new ArrayList<>();
       List<Movie_tag> ltag=new ArrayList<>();
       List<User> luser=new ArrayList<>();
       //Views
       MovieView mv=new MovieView();
       RatingView rv=new RatingView();
       TagView tv=new TagView();
       UserView uv=new UserView();
       
       //Load Files
       Recursos r=new Recursos();
       lrating=r.LoadFileRatings("ratings.csv");
       lmovie=r.LoadFileMovies("movie-titles.csv");
       ltag=r.LoadFileTags("movie-tags.csv");
       luser=r.LoadFileUsers("users.csv");
       System.out.println("FICHEROS cargados satisfactoriamente......");
       //Init Controller
       Controller controller=new Controller(lmovie,ltag,luser,lrating,mv,rv,tv,uv);
     
       //INIT APP:
       Scanner in = new Scanner(System.in);
       System.out.println("Construyendo modelo......");
       controller.ConstruirModelo();
       System.out.println("Modelo construido.");
        Integer id = -2;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(id!=-1){
            
            System.out.print("Por favor introduzca el ID del USUARIO (-1 para salir): ");
            try {
                List<Movie_title> lMoviesRecom=new ArrayList<>();
                id = Integer.valueOf(reader.readLine());
                //TF-IDF
                //Perfiles de productos
                if(id==-1){break;}
                lMoviesRecom=controller.RecommendMovies(id);
                
                
               System.out.println("_______________________________________________________________________________________");
               System.out.println();
                
                
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("You entered : " + id);
        
        }
       
      System.out.println("FINALIZADO");
       
       
     
    }
    
}
