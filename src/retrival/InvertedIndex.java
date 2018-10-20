
package retrival;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class InvertedIndex {
  Map<String,Value> invertedindex = new TreeMap();  
 int noDoc;
 private String fileName;

InvertedIndex(String name){
    fileName=name;   
}

public void printInvertedInex(){

 for(Map.Entry<String,Value> entry : invertedindex.entrySet()) {     
     System.out.print(entry.getKey()+":\tIdf: "+entry.getValue().idf+"\t");
     LinkedList<Doc> doc=entry.getValue().docs;
     double idf=entry.getValue().idf;
     for(int i=0;i<doc.size();i++){
         
         System.out.print("Document"+doc.get(i).getId()+" :Freq: "+doc.get(i).getFreq()+" \\Weghit:"+doc.get(i).getWeight()+"\t->\t");
         
     }

     System.out.println("</key>");

}   
       
}

public void createInvertedInexFile() throws FileNotFoundException{
      PrintWriter writer = new PrintWriter(new File("files\\output\\"+"InvertedIndex.xml"));
        writer.println("<file>");
 for(Map.Entry<String,Value> entry : invertedindex.entrySet()) {
     System.out.print("<key idf='"+entry.getValue().idf+"' name='"+entry.getKey()+"' >\n");
      writer.print("<key idf='"+entry.getValue().idf+"' name='"+entry.getKey()+"' >\n");
     //System.out.print(entry.getKey()+":\tIdf: "+entry.getValue().idf+"\t");
     LinkedList<Doc> doc=entry.getValue().docs;
     double idf=entry.getValue().idf;
     for(int i=0;i<doc.size();i++){
         
        // System.out.print("Document"+doc.get(i).getId()+" :Freq: "+doc.get(i).getFreq()+" \\Weghit:"+doc.get(i).getWeight()+"\t->\t");
         System.out.print("\t<document id='"+doc.get(i).getId()+"' freq='"+doc.get(i).getFreq()+"' weghit='"+doc.get(i).getWeight()+"'></document>\n");
         writer.print("\t<document id='"+doc.get(i).getId()+"' freq='"+doc.get(i).getFreq()+"' weghit='"+doc.get(i).getWeight()+"'></document>\n");
     }
       writer.println("</key>");
     System.out.println("</key>");

}   
        writer.println("</file>");
        writer.close();
}



public void calcIdf(){
 for(Map.Entry<String,Value> entry : invertedindex.entrySet()) {
     LinkedList<Doc> doc=entry.getValue().docs;
     Value val=entry.getValue();
     val.calculateIdf(noDoc, doc.size());
    //  doc.get(i).calculateIdf(noDoc,doc.size());
     for(int i=0;i<doc.size();i++){
       doc.get(i).calculateWeight(val.idf);
     }

}   
}

public void readInvertedIndexFile() throws ParserConfigurationException, SAXException, IOException{
     File inputFile = new File("files\\output\\InvertedIndex.xml"); 
 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document docFile = dBuilder.parse(inputFile);
         docFile.getDocumentElement().normalize();
         System.out.println("Root element :" + docFile.getDocumentElement().getNodeName().toUpperCase());
        NodeList nList = docFile.getElementsByTagName("key");
         System.out.println("----------------------------");
   
         for(int i=0;i<nList.getLength();i++){
          Node nNode = nList.item(i);   
           Element key = (Element) nNode;
          // System.out.println("Word: "+key.getAttribute("name")+" idf: "+key.getAttribute("idf"));
           NodeList documentList=key.getElementsByTagName("document");
           
           LinkedList<Doc> doc=new LinkedList();
              Value val= new Value();
              float idf=Float.parseFloat(key.getAttribute("idf"));
              String name=key.getAttribute("name");
              val.setIdf(idf);
           for(int j=0;j<documentList.getLength();j++){
               
                Node documentNode = documentList.item(j); 
                Element document = (Element) documentNode;
           //System.out.println("document"+document.getAttribute("id") +" freq:"+document.getAttribute("freq")+" weghit:"+document.getAttribute("weghit")); 
           int docId=Integer.parseInt(document.getAttribute("id"));
           int freq=Integer.parseInt(document.getAttribute("freq"));
           float weghit=Float.parseFloat(document.getAttribute("weghit"));
           doc.add(new Doc(docId,freq,weghit));
           val.docs=doc;
           invertedindex.put(name, val);
           }
         //  System.out.println("\n");
         }
         
}
public void fillInvertedIndex() throws FileNotFoundException, IOException{
  BufferedReader reader = new BufferedReader(new FileReader("files\\output\\"+fileName));
        String line="";
        int docId=0; //
        int itr=0;
        while ((line = reader.readLine()) != null) {//Docuement
            if(line.isEmpty())continue;
            String[] word=line.split(" ");
             LinkedList<Doc> doc=null;
              Value val=null;
            for(int i=0;i<word.length;i++){//Words 
                
                /** if word not exist create new key **/
                if(!invertedindex.containsKey(word[i])){
                    val=new Value();
                    doc = new LinkedList();
                    doc.add(new Doc(docId)); //Add first in doc in word with freq:1
                    val.docs=doc;
                    
                    invertedindex.put(word[i],val);
                    }
              /* if word exist in document increase frequency */
              else{
                  Doc lastDocument=invertedindex.get(word[i]).docs.getLast();
                  if(lastDocument.getId()==docId){
                  lastDocument.inc(); 
                  }
                  else{
                      invertedindex.get(word[i]).docs.add(new Doc(docId));
                  }     
              }

            }
              docId++;
        }
        this.noDoc=docId;
        }   


    
}
