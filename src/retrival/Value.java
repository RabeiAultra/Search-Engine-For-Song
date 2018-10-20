
package retrival;

import java.util.LinkedList;


class Value {

 
    LinkedList<Doc> docs;
    double idf;

        public void setIdf(double idf) {
        this.idf = idf;
    }
    
    
    public void calculateIdf(int m,int n){
       this.idf=1+(Math.log10(m/(double)n)/Math.log10(2));
       idf=(double)Math.round(idf * 1000d) / 1000d;
    }
    
       public double getIdf() {
        return idf;
    }
}
