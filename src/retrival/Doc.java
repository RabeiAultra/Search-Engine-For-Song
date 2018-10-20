/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrival;

/**
 *
 * @author Aultra
 */
public class Doc {
    private int id;
    private int freq;
    private double idf;
    private double weight;

    public Doc(int id, int freq, double weight) {
        this.id = id;
        this.freq = freq;
        this.weight = weight;
    }

   

    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    Doc(int id){
        this.id=id;
        freq++;
    }
    public void setId(int id){
        this.id=id;
    }    
    
    public void calculateIdf(int m,int n){
       this.idf=1+(Math.log10(m/(double)n)/Math.log10(2));
       idf=(double)Math.round(idf * 1000d) / 1000d;
    }  
    
     public double getIdf(){
       return idf;
    }  

    @Override
    public String toString() {
        return "Doc{" + "id=" + id + ", freq=" + freq + ", idf=" + idf + ", weight=" + weight + '}';
    }
     
     public void calculateWeight(double idf){
       weight=idf*freq;
       weight=(double)Math.round(weight * 1000d) / 1000d;
    } 
     
       public double getWeight(){
       return weight;
    }  
    
     public int getId(){
        return id;
    }  
     
     public void setIdf(double idf){
         this.idf=idf;
     }
     
      public void setFreq(int freq){
        this.freq=freq;
    }  
      
       public int getFreq(){
        return freq;
    } 
       
       public void inc(){
           freq++;
       }
}
