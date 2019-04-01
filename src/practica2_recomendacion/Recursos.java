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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author UJA
 */

public class Recursos {
    
    public List<Rating> LoadFileRatings(String nameFile){
        String filePath = new File("").getAbsolutePath();
        String line = "";
        String cvsSplitBy = ",";
        List<Rating> l = new ArrayList<>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/data/"+nameFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] part = line.split(cvsSplitBy);
                l.add(new Rating(Integer.parseInt(part[0]),
                                    Integer.parseInt(part[1]),
                                    Double.parseDouble(part[2])));   
           }
          
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }
    
     public List<Movie_title> LoadFileMovies(String nameFile){
        String filePath = new File("").getAbsolutePath();
        String line = "";
        String cvsSplitBy = ",";
        List<Movie_title> l = new ArrayList<>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/data/"+nameFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] part = line.split(cvsSplitBy);
                l.add(new Movie_title(Integer.parseInt(part[0]),part[1]));
            }
          
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }
     
     
      public List<Movie_tag> LoadFileTags(String nameFile){
        String filePath = new File("").getAbsolutePath();
        String line = "";
        String cvsSplitBy = ",";
        List<Movie_tag> l = new ArrayList<>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/data/"+nameFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] part = line.split(cvsSplitBy);
                l.add(new Movie_tag(Integer.parseInt(part[0]),part[1]));
                
            }
         
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }
      
      
       public List<User> LoadFileUsers(String nameFile){
        String filePath = new File("").getAbsolutePath();
        String line = "";
        String cvsSplitBy = ",";
        List<User> l = new ArrayList<>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/data/"+nameFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] part = line.split(cvsSplitBy);
                l.add(new User(Integer.parseInt(part[0]),part[1]));  
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }
    
    
}
