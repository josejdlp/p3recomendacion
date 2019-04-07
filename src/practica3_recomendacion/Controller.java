/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica3_recomendacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import practica3_recomendacion.pojo.Movie_tag;
import practica3_recomendacion.pojo.User;
import practica3_recomendacion.pojo.Recomendacion;
import practica3_recomendacion.pojo.Movie_title;
import practica3_recomendacion.pojo.Rating;
import practica3_recomendacion.views.MovieView;
import practica3_recomendacion.views.RatingView;
import practica3_recomendacion.views.UserView;
import practica3_recomendacion.views.TagView;
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
    public double media;
    public double precision;
    public double recall;
    
    public int tp=0;
    public int fp=0;
    
   //similitud
     Map<Integer,List<Recomendacion>> mapComparadorItems;
     Map<Integer,List<Recomendacion>> mapVecinos;
    private Map<Integer,Map<Integer,Double>> mapRatings;
    private Map<Integer,Map<Integer,Double>> mapTestRating;
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
        this.mapTestRating=new HashMap<>();
        this.listPredicciones=new ArrayList<>();
       
        media=0.0;
        precision=0.0;
        recall=0.0;
        
        tp=0;
        fp=0;
    }
    public void showMovies(){
        movieView.printMovies(listMovies);
    }
    public void ConstruirModelo(String ficheroTraining){
       //CARGAR FICHEROS TRAINING -> RELLENADO DE LISTRATING
        Recursos r=new Recursos();
        listRatings.clear();
        mapVecinos.clear();
        mapMovies.clear();
        mapComparadorItems.clear();
        mapVecinos.clear();
        listPredicciones.clear();
        mapTestRating.clear();
        
         //cargar mapa movies
        for(Movie_title mt:listMovies){
            mapMovies.put(mt.getIdItem(), mt.getTitle());
        }
        
        
       LoadFileRatings(ficheroTraining);
       Set<Integer> users=BuildUserUniques();
       init(users);
        //  1) SIMILITUD. FUNCION DE SIMILITUD COEF. CORRELACIÓN DE PEARSON [-1,1] selec >0
       
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
    
    
     List<Movie_title> RecommendMovies_PrecisionRecall(String ficheroTest){ 
       //Integer tp=0;
       //Integer fp=0;
       tp=0;
       fp=0;
       List<Integer> list_usu_predicho=new ArrayList<>();
       List<Movie_title> l=new ArrayList<>();
       //CargaTestPredicciones
       mapTestRating.clear();
       LoadFileTestRating(ficheroTest);
       //por cada usuario del fichero test, pedimos la predicción
       for(Integer idUser:mapTestRating.keySet()){
           if(!list_usu_predicho.contains(idUser)){
                //predecir solo 1 vez el usuario
                list_usu_predicho.add(idUser);
                //List<Integer> prodsNews=NewProducts(idUser);
                List<Integer> prodsNews=new ArrayList<>();
                for(Integer item:mapTestRating.get(idUser).keySet()){
                    prodsNews.add(item);
                }
                
                //3) Calcular la predicción para las películas que NO HA VISTO
                CalcularPredicciones(idUser,prodsNews);
                List<Recomendacion> listFinal=ordenarProdSim(listPredicciones);
                List<Recomendacion> listReducida=new ArrayList<>();
               
                
                // limitar a 10 listPredicciones
                for(int i=0;i<listFinal.size();i++){
                    if(listReducida.size()<10){
                        listReducida.add(listFinal.get(i));
                    }
                }
                /*System.out.println("-+-+-+-+-+-+");
                for(Recomendacion key:listReducida){
                    System.out.println(key.getIdMovie()+". "+mapMovies.get(key.getIdMovie())+" :"+key.getValor());
                }*/
              //Calculamos tp: aparece en predicciones y en test con >=3.5
              for(Recomendacion rec:listReducida){
                  if(mapTestRating.containsKey(idUser)){
                      if(mapTestRating.get(idUser).containsKey(rec.getIdMovie())){
                          //Esta en recomendaciones y esta en test real
                          if(mapTestRating.get(idUser).get(rec.getIdMovie())>=3.5){
                              //es considerada buena
                              tp=tp+1;
                          }
                      }
                  }
              }
              //Calculamos fp: aparece en predicciones y en Test <3.5
              for(Recomendacion rec:listReducida){
                  if(mapTestRating.containsKey(idUser)){
                      if(mapTestRating.get(idUser).containsKey(rec.getIdMovie())){
                          //Esta en recomendaciones y esta en test real
                          if(mapTestRating.get(idUser).get(rec.getIdMovie())<3.5){
                              //es considerada buena
                              fp=fp+1;
                          }
                      }
                  }
              }
           }
           int r=3;
       }
       int a=2;
       //PRECISION
       Double prec=(double)tp/(tp+fp);
       precision=precision+prec;
       //RECALL
       
       /*
       List<Integer> prodsNews=NewProducts(idUser);
       //3) Calcular la predicción para las películas que NO HA VISTO
       CalcularPredicciones(idUser,prodsNews);
       List<Recomendacion> listFinal=ordenarProdSim(listPredicciones);
       //mostrar predicciones
       for(Recomendacion key:listFinal){
           System.out.println(mapMovies.get(key.getIdMovie())+" :"+key.getValor());
       }*/
       
        return l;
    }
    
    List<Movie_title> RecommendMoviesMAE(String ficheroTest){
       List<Movie_title> l=new ArrayList<>();
       int n=0;
       Double total=0.0;
        tp=0;
       fp=0;
       List<Integer> list_usu_predicho=new ArrayList<>();
      
       //CargaTestPredicciones
       mapTestRating.clear();
       LoadFileTestRating(ficheroTest);
       //por cada usuario del fichero test, pedimos la predicción
       for(Integer idUser:mapTestRating.keySet()){
           if(!list_usu_predicho.contains(idUser)){
                //predecir solo 1 vez el usuario
                list_usu_predicho.add(idUser);
                //List<Integer> prodsNews=NewProducts(idUser);
                List<Integer> prodsNews=new ArrayList<>();
                for(Integer item:mapTestRating.get(idUser).keySet()){
                    prodsNews.add(item);
                }
                
                //3) Calcular la predicción para las películas que NO HA VISTO
                CalcularPredicciones(idUser,prodsNews);
                List<Recomendacion> listFinal=ordenarProdSim(listPredicciones);
                List<Recomendacion> listReducida=new ArrayList<>();
               
                
                // limitar a 10 listPredicciones
                for(int i=0;i<listFinal.size();i++){
                    if(listReducida.size()<10){
                        listReducida.add(listFinal.get(i));
                    }
                }
                
                for(Recomendacion r:listReducida){
                    Double prediccion=r.getValor();
                    if(prediccion>0.0){
                         Double real=mapTestRating.get(idUser).get(r.getIdMovie());
                         Double diferencia=Math.abs(prediccion-real);
                         total=total+diferencia;
                         n=n+1;
                    }
              }
           }
       }
       Double resultado=total/n;
       media=media+resultado;
        return l;
    }
      
      /*
    List<Movie_title> RecommendMoviesMAE(String ficheroTest){
       List<Movie_title> l=new ArrayList<>();
       //Cargar fichero test, sacar el idUsuario y productoAPredecir
       //CargaTestPredicciones
       LoadFileTestRating(ficheroTest);
       int n=0;
       Double total=0.0;
        for(Integer user:mapTestRating.keySet()){
            List<Integer> prodsNews=new ArrayList<>();
            //Recorremos todos los usuarios para predecir los items de todos
            for(Integer item:mapTestRating.get(user).keySet()){
                prodsNews.add(item);
            }
            //PREDICCION
             CalcularPredicciones(user,prodsNews);
              for(Recomendacion r:listPredicciones){
                Double prediccion=r.getValor();
                if(prediccion>0.0){
                     Double real=mapTestRating.get(user).get(r.getIdMovie());
                     Double diferencia=Math.abs(prediccion-real);
                     total=total+diferencia;
                     n=n+1;
                }
              }
        }
     
       Double resultado=total/n;
       System.out.println("MAE:"+resultado);
       media=media+resultado;
        return l;
    }
    */
    
    
     private void CalcularPredicciones(Integer idUser,List<Integer> prodsNews){
         listPredicciones.clear();
        //mappredicciones
       for(Integer prodPredecir:prodsNews){
           double prediccion=0;
           double sumasimilitud=0;
           double numerador=0;
           int contadork=0;
           for(Recomendacion prodVecino:mapVecinos.get(prodPredecir)){
               try{
                        if(contadork>=k){
                            break;
                        }else{
                           // System.out.println(prodVecino.getIdMovie());
                            //System.out.println(idUser);
                            double nota=0.0;
                                if(mapRatings.get(prodVecino.getIdMovie()).containsKey(idUser)){
                                   nota=mapRatings.get(prodVecino.getIdMovie()).get(idUser);
                                    if(nota!=0.0){
                                        double similitud=prodVecino.getValor();
                                        numerador=numerador+(similitud*nota);
                                        sumasimilitud=sumasimilitud+similitud;
                                        contadork=contadork+1;
                                    }
                                }
                        }
                 }catch(Exception e){
                       System.out.println("Execpcion"+e.toString());
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
           /* for(Integer u:users){
                        userRating.put(u, 0.0);
            }*/
           for(Rating ra:listRatings){
               if(!userRating.containsKey(ra.getIdUser())){
                   userRating.put(ra.getIdUser(), 0.0);
               }
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
        
        
        
        public void LoadFileRatings(String nameFile){
            String filePath = new File("").getAbsolutePath();
            String line = "";
            String cvsSplitBy = ",";
           // List<Rating> l = new ArrayList<>(); 
            try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/data/"+nameFile))) {
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] part = line.split(cvsSplitBy);
                    listRatings.add(new Rating(Integer.parseInt(part[0]),
                                        Integer.parseInt(part[1]),
                                        Double.parseDouble(part[2])));   
               }

            } catch (IOException e) {
                e.printStackTrace();
            }
         }
        
        public void LoadFileTestRating(String nameFile){
            String filePath = new File("").getAbsolutePath();
            String line = "";
            String cvsSplitBy = ",";
           // List<Rating> l = new ArrayList<>(); 
            try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/data/"+nameFile))) {
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] part = line.split(cvsSplitBy);
                    Integer idUser=Integer.parseInt(part[0]);
                    Integer idItem= Integer.parseInt(part[1]);
                    Double nota=Double.parseDouble(part[2]);
                    
                    Map<Integer,Double> items=new HashMap<>();
                    if(!mapTestRating.containsKey(idUser)){
                        items.put(idItem, nota);
                        mapTestRating.put(idUser, items);
                    }else{
                        mapTestRating.get(idUser).put(idItem, nota);
                    }
                    
                    
                    
                    
               }

            } catch (IOException e) {
                e.printStackTrace();
            }
         }
}
