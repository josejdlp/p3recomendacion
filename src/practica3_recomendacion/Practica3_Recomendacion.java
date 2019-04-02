/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3_recomendacion;

import practica3_recomendacion.pojo.Movie_tag;
import practica3_recomendacion.pojo.User;
import practica3_recomendacion.pojo.Movie_title;
import practica3_recomendacion.pojo.Rating;
import practica3_recomendacion.views.MovieView;
import practica3_recomendacion.views.RatingView;
import practica3_recomendacion.views.UserView;
import practica3_recomendacion.views.TagView;
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



public class Practica3_Recomendacion {

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
       //lrating=r.LoadFileRatings("ratings.csv");
       lmovie=r.LoadFileMovies("movie-titles.csv");
       ltag=r.LoadFileTags("movie-tags.csv");
       luser=r.LoadFileUsers("users.csv");
       System.out.println("FICHEROS cargados satisfactoriamente......");
       //Init Controller
       Controller controller=new Controller(lmovie,ltag,luser,lrating,mv,rv,tv,uv);
     
       //INIT APP:
       Scanner in = new Scanner(System.in);
      
       
       //Rellenar lista de TRAINING iterar por todos los ficheros
      
      // List<Integer> indices=new ArrayList<>();
       for(int i=0;i<5;i++){
            System.out.println("____________________________________________");
            System.out.println("PRUEBA:"+i);
            String nombre="ratings_train_"+i+".csv";
            System.out.println("Construyendo training.... "+nombre);
            controller.ConstruirModelo(nombre);

            System.out.println("Training construido. "+nombre);
           
           
           
           //Realizar predicciones con TEST
            System.out.println("Realizando predicciones.... "+nombre);
           List<Movie_title> lMoviesRecom=new ArrayList<>();
           nombre="ratings_test_0.csv";
           lMoviesRecom=controller.RecommendMovies(nombre);
           
           
           
           
           
       }
       System.out.println("**********");
       System.out.println("Suma de los MAEs / 5: "+(controller.media/5));
        System.out.println("**********");
       
       
       
      /* controller.ConstruirModelo(listFicherosTraining);
       System.out.println("Modelo construido.");
        Integer id = -2;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(id!=-1){
            
            System.out.print("Por favor introduzca el ID del USUARIO (-1 para salir): ");
            try {
                List<Movie_title> lMoviesRecom=new ArrayList<>();
                id = Integer.valueOf(reader.readLine());
                
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
       
       */
     
    }
    
}
