
package retrival;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom2.*;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.Scanner;
import java.util.TreeMap;

public class Retrival {
    private static String documents="Lyrics.txt";
    private static String cleanDocument="output.txt";
    private static String stopWordFile="StopWord.txt";
    static InvertedIndex inv ;
    static String queryIn;
    static Map <Float,Integer>  retrievedItems;
public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
         inv = new InvertedIndex(cleanDocument);
         inv.readInvertedIndexFile();
      //   inv.printInvertedInex();
         requestQuery();
         evaluation();
  //  Element key= root.getChild("key");
    //System.out.println(key.getAttribute("idf"));
      //  createStmmerFile();
       // createInvertedIndexFile();
        
    }

public static void evaluation() throws SAXException, IOException, ParserConfigurationException, ParserConfigurationException{
    Map<String,LinkedList<Integer>> relevantItems = new TreeMap();
     File inputFile = new File("files\\documents\\Name.xml"); 
 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document docFile = dBuilder.parse(inputFile);
         docFile.getDocumentElement().normalize();
        NodeList nList = docFile.getElementsByTagName("key");
         for(int i=0;i<nList.getLength();i++){
           Node nNode = nList.item(i);   
           Element key = (Element) nNode;
           NodeList documentList=key.getElementsByTagName("document");
           
           LinkedList<Integer> documents=new LinkedList();
           String name=key.getAttribute("mane");
            for(int j=0;j<documentList.getLength();j++){  
                 Node documentNode = documentList.item(j); 
                 Element document = (Element) documentNode;
                 int id = Integer.parseInt(document.getAttribute("id"));
                 if(relevantItems.containsKey(name)){relevantItems.get(name).add(id);}
                 else {documents.add(id);relevantItems.put(name, documents);}
            }       
       }
         
         //queryIn
         Map <Integer, String> retrived= new TreeMap(); 
         System.out.println("The documents retrived: ");
         for (Map.Entry<Float, Integer> pair : retrievedItems.entrySet()) {
          System.out.println("Document"+pair.getValue());
          retrived.put(pair.getValue(),"retrived");
         }
          System.out.println("==================================");
          System.out.println("The documents relevant: ");
          int relevantRetrieved =0; 
          LinkedList relevant=new LinkedList() ;
          if(relevantItems.containsKey(queryIn))
                  { relevant = relevantItems.get(queryIn);}
          for(int i=0;i<relevant.size();i++){
                 System.out.println("Document"+relevant.get(i));
              if(retrived.containsKey(relevant.get(i))){
                  relevantRetrieved++; 
              }
          }
          
         
         System.out.println("Total Number of Relevant Documents: "+relevant.size());
         System.out.println("Total Number of Retrived Documents: "+retrived.size());
         System.out.println("Number of Relevant Documents retrived: "+relevantRetrieved);
         float precection=relevantRetrieved/(float)Math.max(1,relevant.size());
         float recall=relevantRetrieved/(float)Math.max(1, retrived.size());
         System.out.println("==================================");
         System.out.println("Precection:  "+precection);
         System.out.println("Recall:  "+recall);
         
}


public static void requestQuery() throws IOException{
  Scanner in = new Scanner(System.in);
  String query=in.nextLine();
  queryIn=query;
  //removing query and steaming stopwords
    query=Lower(query);
    query=RemoveSpichlChar(query);
    query=RemoveStop(query);
    query=RemoveNumbers(query);
    query=WordRoot(query);
    query=query.replaceAll("(\\s\\w\\s)"," ");
    query = query.replaceAll("\\s{2,}"," ");  //Delete the trailing spaces
     String[] word=query.split(" ");
     
     //put query words as key and index as value
      Map<String,Doc> words = new HashMap();
    for(int i=0;i<word.length;i++){
        if(words.get(word[i])==null){
             words.put(word[i],new Doc(i));  
        }
        else{
            words.get(word[i]).inc();
        }  
    }

    //calculate weghts for the query words
   Map<String,Value> invertedindex=inv.invertedindex;
for (Map.Entry<String, Doc> pair : words.entrySet()) {
       double idf=0;
    if(invertedindex.get(pair.getKey())==null);
    else {idf=invertedindex.get(pair.getKey()).getIdf();}
    Doc doc=pair.getValue();
   doc.setIdf(idf);
   doc.calculateWeight(idf);
   doc.getWeight();
   
}

int co=0;
Map<Integer,LinkedList<Float>> documents = new HashMap();

//get All documents
for (Map.Entry<String, Doc> pair : words.entrySet()) {
     String key=pair.getKey();
     Doc value=pair.getValue();
     LinkedList<Doc> docs;
     if(invertedindex.get(key)==null){continue;}
     else  docs=invertedindex.get(key).docs;
    for(int i=0;i<docs.size();i++){
        int id=invertedindex.get(key).docs.get(i).getId();
       if(!documents.containsKey(id))documents.put(id, new LinkedList());
    }
}


for (Map.Entry<Integer, LinkedList<Float>> docTerms : documents.entrySet()) {
  int docId=docTerms.getKey();  
  
  for (Map.Entry<String, Doc> pair : words.entrySet()) {
      String term=pair.getKey();if(invertedindex.get(term)==null){continue;}
     LinkedList<Doc> docs= invertedindex.get(term).docs;
     boolean contain = false;
     int index=-1;
     for(int i=0;i<docs.size();i++){
         if(docs.get(i).getId()==docId){
             contain=true;
             index=i;
         }
        
     }
     
if(contain){docTerms.getValue().add((float)docs.get(index).getWeight());}
else docTerms.getValue().add(0f);
     
  }
  
}


//Find simlarity and store in array
Map <Float,Integer> siml = new  TreeMap();
for (Map.Entry<Integer, LinkedList<Float>> docTerms : documents.entrySet()) {
     float valu=cosSim(words,docTerms.getValue());
    siml.put(valu,docTerms.getKey());  
}
Map<Float, Integer> simlA = new TreeMap(Collections.reverseOrder());
simlA.putAll(siml);


retrievedItems=simlA;


//System.out.println(words);
//System.out.println(documents);
    System.out.println("===============================");
System.out.println("The top 5 documents ranking based on similarity");
int k=0;
for (Map.Entry<Float, Integer> arr : simlA.entrySet()) {
    if(k++>5)break;
    System.out.println("Document"+arr.getValue());
}
    System.out.println("===============================");
}

    
public static float cosSim(Map<String,Doc> q, LinkedList<Float>d){
    
    return dotProduct(q,d)/(float)Math.sqrt(calcLengthD(d)*calcLengthQ(q));
}

 public static float dotProduct(Map<String,Doc> q, LinkedList<Float>d){
    float s=0;
    int c=0;
    for (Map.Entry<String, Doc> pair : q.entrySet()) {
        if(c>=d.size())break;
        float wq=(float)pair.getValue().getWeight();
        float wd=d.get(c++);
        s+=(wq*wd);
    }
  return s;   
 }

public static float calcLengthQ(Map<String,Doc> q){
    float s=0;
   for (Map.Entry<String, Doc> p : q.entrySet()) {
       float v=(float)p.getValue().getWeight();
       s+=(Math.pow(v, 2));
   }
   return s;
}

public static float calcLengthD(LinkedList<Float>d){
     float s=0;
   for (int i=0;i<d.size();i++) {
       float v=d.get(i);
       s+=(Math.pow(v, 2));
   }
   return s;
    
}

public static void createStmmerFile() throws IOException{
    PrintWriter writer = new PrintWriter(new File("files\\output\\"+cleanDocument));
        writer.print("");
        writer.close();
        
        BufferedReader reader = new BufferedReader(new FileReader("files\\documents\\"+documents));
        String line = null;
        while ((line = reader.readLine()) != null) {
            line=Lower(line);
            line=RemoveSpichlChar(line);
            line=RemoveStop(line);
            line=RemoveNumbers(line);
            line=WordRoot(line);
            line=line.replaceAll("(\\s\\w\\s)"," ");
            line = line.replaceAll("\\s{2,}"," ");  //Delete the trailing spaces
            WirteFile(line);   
        }
    
}
public static void createInvertedIndexFile() throws IOException{
   
        inv.fillInvertedIndex();
        inv.calcIdf();
        inv.createInvertedInexFile();
}
    
   
    public static String ReadFile(String source) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(source));
        String str = "";
        String line = null;
        while ((line = reader.readLine()) != null) {
            str += line;
            str += "\n";
        }
        return str;
    }

    public static String Lower(String str) {
        return str.toLowerCase();
    }

    public static String RemoveSpichlChar(String str) {
        String s = str;
        s=s.replaceAll("([^A-z0-9\\s]|[_\\[\\]\\\\\\^\\`])"," ");  
        return s;
    }
    
    public static String RemoveNumbers(String str){
        String s=str;
        s=s.replaceAll("(\\d)\\w*", " ");
        return s;
    }

    public static String RemoveStop(String str) throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(str, "\n");
        LinkedList<String> Stops = GetStopWords();
       for(int i=0;i<Stops.size();i++){
             
                str = str.replaceAll(" "+Stops.get(i)+" ", " ");  

        }
       str = str.replaceAll("(\\')\\w*"," ");
        return str;
    }

    public static String WordRoot(String str) {
        StringTokenizer tokenizer=new StringTokenizer(str, " \n");
        Stemmer s=new Stemmer();
        while(tokenizer.hasMoreTokens()){
            String token=tokenizer.nextToken();
            for(int i=0;i<token.length();i++)
                s.add(token.charAt(i));
            s.stem();
            String root=s.toString();
            str=str.replace(token, root);
        }
        return str;
    }
    public static void WirteFile(String str) throws IOException{
        FileWriter fw = new FileWriter(new File("files\\output\\"+cleanDocument),true);
        StringTokenizer tokenizer=new StringTokenizer(str,"\n");
        while(tokenizer.hasMoreTokens()){
            fw.append(tokenizer.nextToken()+"\n");
            fw.write(System.getProperty( "line.separator" ));
        }
        fw.write(System.getProperty( "line.separator" ));
        fw.close();
    }
    
    
    public static LinkedList<String> GetStopWords() throws IOException {
        LinkedList<String> Words = new LinkedList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("files\\"+stopWordFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                Words.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Retrival.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Words;
    }

    
}


class Term{
    float weight;
    int id;
}