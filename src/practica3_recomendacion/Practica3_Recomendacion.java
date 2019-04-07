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
    public static void main(String[] args) throws IOException {
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
       System.out.println("FICHEROS cargados satisfactoriamente: movie-titles.csv,movie-tags.csv,users.csv");
       //Init Controller
       Controller controller=new Controller(lmovie,ltag,luser,lrating,mv,rv,tv,uv);
       //INIT APP:
       Scanner in = new Scanner(System.in);
       Integer data=-1;
       BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
       System.out.println();
       System.out.println("-----------MENÚ----------");
       System.out.println("1. MAE");
       System.out.println("2. Precision and Recall");
       System.out.println("3. nDGC");
       System.out.println("-1. Salir");
       System.out.println("Elija el método de evaluación para evaluar los alg. de predicción: ");
       data = Integer.valueOf(reader.readLine());
       while(data!=-1){
           if(null!=data)switch (data) {
               case 1:
                   //MAE
                    System.out.println("CALCULANDO MAE....");
                   for(int i=0;i<5;i++){
                       System.out.println("____________________________________________");
                       String nombre="ratings_train_"+i+".csv";
                       System.out.println("Construyendo training.... "+nombre);
                       controller.ConstruirModelo(nombre);
                       System.out.println("Training construido. "+nombre);
                       //Realizar predicciones con TEST
                       System.out.println("Realizando predicciones.... "+nombre);
                       List<Movie_title> lMoviesRecom=new ArrayList<>();
                       nombre="ratings_test_"+i+".csv";
                       lMoviesRecom=controller.RecommendMoviesMAE(nombre);
                   }  
                    System.out.println("**********");
                    System.out.println("MAE "+(controller.media/5));
                    System.out.println("**********");
                    System.out.println();
                    System.out.println("-----------MENÚ----------");
                    System.out.println("1. MAE");
                    System.out.println("2. Precision and Recall");
                    System.out.println("3. nDGC");
                    System.out.println("-1. Salir");
                    System.out.println("Elija el método de evaluación para evaluar los alg. de predicción: ");
                    data = Integer.valueOf(reader.readLine());
                   break;
               case 2:
                   System.out.println("CALCULANDO PRECISION AND RECALL....");
                   
                    for(int i=0;i<5;i++){
                       System.out.println("____________________________________________");
                       String nombre="ratings_train_"+i+".csv";
                       System.out.println("Construyendo training.... "+nombre);
                       controller.ConstruirModelo(nombre);
                       System.out.println("Training construido. "+nombre);
                       //Realizar predicciones con TEST
                       System.out.println("Realizando predicciones.... "+nombre);
                       List<Movie_title> lMoviesRecom=new ArrayList<>();
                       nombre="ratings_test_"+i+".csv";
                       lMoviesRecom=controller.RecommendMovies_PrecisionRecall(nombre);
                       
                      
                   }  
                   
                    System.out.println("**********");
                    System.out.println("Precision: "+(double)controller.precision/5);
                    System.out.println("Recall: ");
                    System.out.println("**********");
                    System.out.println();
                   
                    System.out.println();
                    System.out.println("-----------MENÚ----------");
                    System.out.println("1. MAE");
                    System.out.println("2. Precision and Recall");
                    System.out.println("3. nDGC");
                    System.out.println("-1. Salir");
                    System.out.println("Elija el método de evaluación para evaluar los alg. de predicción: ");
                    data = Integer.valueOf(reader.readLine());
                   break;
               case 3:
                   
                   
                   
                   
                    System.out.println("CALCULANDO nDCG....");
                    System.out.println();
                    System.out.println("-----------MENÚ----------");
                    System.out.println("1. MAE");
                    System.out.println("2. Precision and Recall");
                    System.out.println("3. nDGC");
                    System.out.println("-1. Salir");
                    System.out.println("Elija el método de evaluación para evaluar los alg. de predicción: ");
                    data = Integer.valueOf(reader.readLine());
                   break;
               default:
                   break;
           }
       }
    }
}
