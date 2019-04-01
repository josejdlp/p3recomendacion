/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2_recomendacion;

import practica2_recomendacion.pojo.Movie_tag;
import practica2_recomendacion.pojo.User;
import practica2_recomendacion.pojo.Recomendacion;
import practica2_recomendacion.pojo.Movie_title;
import practica2_recomendacion.pojo.Rating;
import practica2_recomendacion.views.MovieView;
import practica2_recomendacion.views.RatingView;
import practica2_recomendacion.views.UserView;
import practica2_recomendacion.views.TagView;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;



/**
 *
 * @author UJA
 */
public class Controller {
    static final int k = 10;
    static final int m=50;
    private List<Movie_title> listMovies;
    private List<Movie_tag> listTags;
    private List<Rating> listRatings;
    private MovieView movieView;
   //similitud
     Map<Integer,List<Recomendacion>> mapComparadorItems;
     Map<Integer,List<Recomendacion>> mapVecinos;
    private Map<Integer,Map<Integer,Double>> mapRatings;
    private Map<Integer,String> mapMovies;
    private List<Recomendacion> listPredicciones;
    public Controller(List<Movie_title> _lm,List<Movie_tag> _lt,List<User> _lu,List<Rating> _lr,
                        MovieView _mv,RatingView _rv,TagView _tv,UserView _uv){
        this.listMovies=_lm;
        this.listTags=_lt;
        this.listRatings=_lr;
        this.movieView=_mv;
        this.mapMovies=new HashMap<>();
         this.mapComparadorItems=new HashMap<>();
        this.mapVecinos=new HashMap<>();
        this.mapRatings=new HashMap<>();
        this.listPredicciones=new ArrayList<>();
        //cargar mapa movies
        for(Movie_title mt:listMovies){
            mapMovies.put(mt.getIdItem(), mt.getTitle());
        }
    }
    public void showMovies(){
        movieView.printMovies(listMovies);
    }
    public void ConstruirModelo(){
         
       Set<Integer> users=BuildUserUniques();
        //  1) SIMILITUD. FUNCION DE SIMILITUD COEF. CORRELACIÓN DE PEARSON [-1,1] selec >0
       init(users);
        Map<Integer,List<Recomendacion>> prodSim=CalculateProdSimilares();
       for(Integer k:prodSim.keySet()){
           List<Recomendacion> lists =new ArrayList<>();
           lists=ordenarProdSim(prodSim.get(k));
           mapComparadorItems.put(k, lists);
       }
       // 2) Seleccionar vecinos limitado a m
       for(Integer key:mapComparadorItems.keySet()){
            ArrayList<Recomendacion> rec=new ArrayList<>();
            int contador=1;
           for(int i=0;i<mapComparadorItems.get(key).size();i++){
                if(contador>m){
                    break;
                }
               rec.add(mapComparadorItems.get(key).get(i));
               contador+=1; 
           }
            mapVecinos.put(key,rec);
       }
    }
    List<Movie_title> RecommendMovies(int idUser){
       List<Movie_title> l=new ArrayList<>();
       List<Integer> prodsNews=NewProducts(idUser);
       //3) Calcular la predicción para las películas que NO HA VISTO
       CalcularPredicciones(idUser,prodsNews);
       List<Recomendacion> listFinal=ordenarProdSim(listPredicciones);
       //mostrar predicciones
       for(Recomendacion key:listFinal){
           System.out.println(mapMovies.get(key.getIdMovie())+" :"+key.getValor());
       }
        return l;
    }
     private void CalcularPredicciones(Integer idUser,List<Integer> prodsNews){
         listPredicciones.clear();
        //mappredicciones
       for(Integer prodPredecir:prodsNews){
           double prediccion=0;
           double sumasimilitud=0;
           double numerador=0;
           int contadork=0;
           for(Recomendacion prodVecino:mapVecinos.get(prodPredecir)){
               if(contadork>=k){
                   break;
               }else{
                   double nota=mapRatings.get(prodVecino.getIdMovie()).get(idUser);
                   if(nota!=0.0){
                        double similitud=prodVecino.getValor();
                        numerador=numerador+(similitud*nota);
                        sumasimilitud=sumasimilitud+similitud;
                        contadork=contadork+1;
                   }
               } 
           }
           prediccion=numerador/sumasimilitud;
           Recomendacion rec=new Recomendacion(prodPredecir, prediccion);
           listPredicciones.add(rec);
       }
    
    }
    private List<Recomendacion> ordenarProdSim(List<Recomendacion> recomendar){
         Recomendacion aux=new Recomendacion();
       Integer idAUX;
       Double valorAux;
       for(int i = 0; i < recomendar.size(); i++){
			for(int j=i+1; j < recomendar.size(); j++){
				if(recomendar.get(i).getValor() < recomendar.get(j).getValor()){
					
                                        idAUX=recomendar.get(i).getIdMovie();
                                        valorAux=recomendar.get(i).getValor();
					
                                        
                                        recomendar.get(i).setIdMovie(recomendar.get(j).getIdMovie());
                                        recomendar.get(i).setValor(recomendar.get(j).getValor());
                                        
                                        recomendar.get(j).setIdMovie(idAUX);
                                        recomendar.get(j).setValor(valorAux);
                                }
			}
		}
        
        return recomendar;
    }
    private void init(Set<Integer> users){
       List<Integer> listProdSim=new ArrayList<>();
       //Init mapa general 
        for(Movie_title m:listMovies){
            //Init rating por cada user
            Map<Integer,Double> userRating=new HashMap<>();
            for(Integer u:users){
                        userRating.put(u, 0.0);
            }
            mapRatings.put(m.getIdItem(), userRating);
        }
        //Rellenar mapRating
        for(Rating r:listRatings){
            mapRatings.get(r.getIdItem()).put(r.getIdUser(), r.getRating());
        }
    }
  
    private Map<Integer,List<Recomendacion>> CalculateProdSimilares(){
        Map<Integer, List<Recomendacion>> prodsSim=new HashMap<>();
        Double puntuacion=0.0;
        //for(Integer idProdNuevo:prodsNews){
            for(Movie_title idProdNuevo:listMovies){
               boolean compara=false;
               List<Recomendacion> l=new ArrayList<>();
                for(Integer idProdCompara:mapRatings.keySet()){
                    if(idProdNuevo.getIdItem()!=idProdCompara){
                        //tenemos los 2 ids de items para comparar
                        puntuacion= calculatePearson(idProdNuevo.getIdItem(), idProdCompara);
                        Recomendacion r=new Recomendacion();
                        r.setIdMovie(idProdCompara);
                        r.setValor(puntuacion);
                        l.add(r);
                    }  
                }
                prodsSim.put(idProdNuevo.getIdItem(),l);
        }
        return prodsSim;
    }
    private Double calculatePearson(Integer idProd, Integer idProdCompara){
        Double mediaA=0.0;
        Double mediaB=0.0; 
        //Calcular MEDIA de idProd->mediaA
        int num=0;
        for(Integer i:mapRatings.get(idProd).keySet()){
            if(mapRatings.get(idProd).get(i) >0.0){
                num=num+1;
                mediaA=mediaA+mapRatings.get(idProd).get(i);
            }
        }
        mediaA=mediaA/num;
        
        //Calcular MEDIA de idProdCompara->mediaB
        int numB=0;
        for(Integer i:mapRatings.get(idProdCompara).keySet()){
            if(mapRatings.get(idProdCompara).get(i) >0.0){
                numB=numB+1;
                mediaB=mediaB+mapRatings.get(idProdCompara).get(i);
            }
        }
        mediaB=mediaB/numB;
        
        //NUMERADOR
        Double numerador=0.0;
        Double factor=0.0;
        for(Integer u:mapRatings.get(idProd).keySet()){
            //Realizar la operacion solo para los COMUNES
            if(mapRatings.get(idProd).get(u)>0.0 && mapRatings.get(idProdCompara).get(u)>0.0){
               Double p1=mapRatings.get(idProd).get(u)-mediaA;
               Double p2=mapRatings.get(idProdCompara).get(u)-mediaB;
               factor=p1*p2;
               numerador=numerador+factor;
            }
        }
       //DENOMINADOR
       Double raizA=0.0;
       Double raizB=0.0;
       Double factorD=0.0;
       
        for(Integer u:mapRatings.get(idProd).keySet()){
            //SOLO EN COMUNES
             if(mapRatings.get(idProd).get(u)>0.0 && mapRatings.get(idProdCompara).get(u)>0.0){
                 //raiz de A:
                    
                    Double x=Math.pow(mapRatings.get(idProd).get(u)-mediaA,2);
                    raizA=raizA+x;
                    //raiz B
    
                    Double b=Math.pow(mapRatings.get(idProdCompara).get(u)-mediaB,2);
                    raizB=raizB+b;
             }
        }
       raizA=Math.sqrt(raizA);
       raizB=Math.sqrt(raizB);
       Double totalDenominador=raizA*raizB;
       Double total=numerador/totalDenominador;
        return total;
    }
    Set<Integer> BuildUserUniques(){
        List<Integer> listUserUniques=new ArrayList<>();
        for(Rating r:listRatings){
            listUserUniques.add(r.getIdUser());
        }
        Set<Integer> users=new HashSet<Integer>(listUserUniques);
        return users;
    }
    
    Set<Integer> BuildProdUniques(){
         List<Integer> ListidProdUniques=new ArrayList<>();
         for (Movie_tag elem:listTags){
             ListidProdUniques.add(elem.getIdItem());
         }
            Set<Integer> uniqueProds = new HashSet<Integer>(ListidProdUniques);
           
        return uniqueProds;
    }
       Set<String> BuildEtqUniques(){  
            //Extraer del excel del excel movie-tags las etiquetas únicas
         List<String> ListEtqUniques=new ArrayList<>();
         for (Movie_tag elem:listTags){
             ListEtqUniques.add(elem.getTag());
         }
            Set<String> uniqueTags = new HashSet<String>(ListEtqUniques);
        
        return uniqueTags;
       }
       
        private List<Integer> NewProducts(Integer idU){
            //Calcular los productos que no tiene el usuario o no ha visto
            List<Integer> listAllProd=new ArrayList<>();
            List<Integer> listUserProd=new ArrayList<>();
            List<Integer> result=new ArrayList<>();

            for(Movie_title m:listMovies){
                listAllProd.add(m.getIdItem());
            }
            for(Rating r:listRatings){
                if(Objects.equals(r.getIdUser(), idU)){
                     listUserProd.add(r.getIdItem());
                }
               
            }
            listAllProd.removeAll(listUserProd);
            return listAllProd;
    }
}
