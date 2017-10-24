import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
//import java.util.stream;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
//import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.management.Query;
import javax.swing.text.html.HTMLDocument.Iterator;
import javax.xml.parsers.DocumentBuilder;

import org.apache.lucene.queryparser.classic.ParseException;

public class searchTrectopics {
	public static void main(String[] args) throws IOException, ParseException {
		PrintWriter shortWriter = new PrintWriter("TF_IDF"+"_Short.txt", "UTF-8");
		PrintWriter longWriter = new PrintWriter("TF_IDF"+"_Long.txt", "UTF-8");
		int begin,end;
		//System.out.println("Total number of documents in the corpus: "+reader.maxDoc());
		/// Here is the logic to fetch information from the topic file
		String inpQuery = new String(Files.readAllBytes(Paths.get("C:\\Users\\bhada\\Downloads\\IU_Sem3\\Search\\Assignments\\Assignment2\\topics.51-100")), StandardCharsets.UTF_8);
		//**************** hashmap creation to store the results for long and short query **********//
		HashMap<String, Double> resShortquery = new HashMap<String, Double>();
		HashMap<String, Double> resLongquery = new HashMap<String, Double>();
		String number = "",shortQuery = "",longQuery = "";
		
		// loop to iterate through entire short 
		while (inpQuery.indexOf("top")!=-1 && fetchText(inpQuery, "top").equals("NULL") == false) {
			
			String information = fetchText(inpQuery, "top");
			// fetching the content of the number for each query 
			if(information.indexOf("<num>") != -1){
				begin = information.indexOf("<num>") + ("<num>").length();
				String remainingQuery = information.substring(begin);
				end = remainingQuery.indexOf("<");
				end += begin;
				number = information.substring(begin, end).trim();
				number = number.replace("\n", "").replace("\r", "");
				number = number.replace("Number:","").trim();	
				System.out.println("num : "+number);
				}
			if(information.indexOf("<title>") != -1){
				begin = information.indexOf("<title>") + ("<title>").length();
				String remainingQuery = information.substring(begin);
				end = remainingQuery.indexOf("<");
				end += begin;
				shortQuery = information.substring(begin, end).trim();				
				shortQuery = shortQuery.replace("\n", "").replace("\r", "");
				shortQuery = shortQuery.replace("Topic:","").trim();
				shortQuery = shortQuery.replace("/", " ");
				}
			if(information.indexOf("<desc>") != -1){
				begin = information.indexOf("<desc>") + ("<desc>").length();
				String remainingQuery = information.substring(begin);
				end = remainingQuery.indexOf("<");
				end += begin;
				longQuery = information.substring(begin, end).trim();
				longQuery = longQuery.replace("Description:","").trim();
				longQuery = longQuery.replace("\n", "").replace("\r", "");
				longQuery = longQuery.replace("/", " ");
				}
			
				System.out.println("Running TF_IDF for Short Query : "+shortQuery);
				resShortquery = tfIDF(shortQuery);
				fileWrite(resShortquery, number, shortWriter, 0, "TF_IDF");
				System.out.println("Running TF_IDF for Long Query : "+longQuery);
				resLongquery = tfIDF(longQuery);
				fileWrite(resLongquery, number, longWriter, 1, "TF_IDF");
				System.out.println("TF_IDF Done..");
				inpQuery = inpQuery.substring(0,inpQuery.indexOf("<top>")) + inpQuery.substring(inpQuery.indexOf("</top>")+"</top>".length(),inpQuery.length());
				inpQuery = inpQuery.trim();
		}
		shortWriter.close();
		longWriter.close();
	}
		
	public static HashMap<String, Double> tfIDF(String Query) throws IOException, ParseException{
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths
			.get("C:\\Users\\bhada\\Downloads\\IU_Sem3\\Search\\Assignments\\Assignment2\\index\\index")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int N = reader.maxDoc();

			IndexSearcher searcher = new IndexSearcher(reader);
			// Get the preprocessed query terms
			//Term queryString = new Term("TEXT","POLICE ROCKS");
			String queryString = Query;
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("TEXT", analyzer);
			org.apache.lucene.search.Query query = parser.parse(queryString);
			//TermQuery query = new TermQuery(queryString);
			//System.out.println(query);a
			Set<Term> queryTerms = new LinkedHashSet<Term>();
			searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
		/**
		* Get document frequency
		*/
		//int df=reader.docFreq(new Term("TEXT", "police"));
		//System.out.println("Number of documents containing the term police for field TEXT : " + df);
		/**
		* Get document length and term frequency
		*/
		// Use DefaultSimilarity.decodeNormValue(…) to decode normalized
		// document length
		HashMap <Integer,Float> document_len = new HashMap<Integer,Float>();
		ClassicSimilarity dSimi = new ClassicSimilarity();
		// Get the segments of the index
		List<LeafReaderContext> leafContexts = reader.getContext().reader()
		.leaves();
		// Processing each segment
		for (int i = 0; i < leafContexts.size(); i++) {
		// Get document length
		LeafReaderContext leafContext = leafContexts.get(i);
		int startDocNo = leafContext.docBase;
		int numberOfDoc = leafContext.reader().maxDoc();
			for (int docId = 0; docId < numberOfDoc; docId++) {
				// Get normalized length (1/sqrt(numOfTokens)) of the document
				float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
				// Get length of the document
				float docLeng = 1 / (normDocLeng * normDocLeng);
				// inserting  the data into the Hashmap
				document_len.put(docId + startDocNo, docLeng);
				
//				System.out.println("Length of doc(" + (docId + startDocNo)
//					+ ", " + searcher.doc(docId +
//							startDocNo).get("DOCNO")
//					+ ") is " + docLeng);
				}
				//System.out.println();
		}
		HashMap <String,Double> document_matrix = new HashMap<String,Double>();
		// Calculate the tf-idf score of each of the term
		for (Term t : queryTerms) {
			//System.out.println("check1");
			String term = t.text().toString();
			System.out.println(term);
			// This code is used to calculate the k(t) of the F score formula
				int kt = reader.docFreq(new Term("TEXT", term));
				//System.out.println("K(t)"+ kt);
				for (int i = 0; i < leafContexts.size(); i++) {
					LeafReaderContext leafContext = leafContexts.get(i);
					PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
					"TEXT", new BytesRef(term));
					//System.out.println(de.);
					int startDocNo = leafContext.docBase;
					int doc;
					if (de != null) {
						while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
							//System.out.println("check");
//							System.out.println("\"police\" occurs " + de.freq()
//							+ " time(s) in doc(" + (de.docID() +
//									startDocNo)+ ")");
							// get the document length from the created document matrix
							int Doc_ID = de.docID() + startDocNo;
							float len_doc = document_len.get(Doc_ID);
							
							// Now apply tf-idf formula to get the score
							double score = (de.freq()/len_doc) * Math.log(1+(N/kt));
							//double val = document_matrix.get(Doc_ID);
							if (document_matrix.containsKey(Doc_ID)) {
								score  = score + document_matrix.get(Doc_ID);
								
								document_matrix.put("" + Doc_ID + searcher.doc(Doc_ID).get("DOCNO"), score);
							}
							else {
								document_matrix.put("" + Doc_ID + searcher.doc(Doc_ID).get("DOCNO"), score);
							}
							
							}
						}
					}
		}
		//System.out.println("check");															
		//System.out.println(document_len.size());
		//document_matrix.forEach((Doc_ID, score) -> System.out.println(Doc_ID + " : " + score));
			HashMap <String,Double> sorted_document_matrix = new HashMap<String,Double>();
		
			sorted_document_matrix = sortHashMapByValues(document_matrix);
			//sorted_document_matrix.forEach((Doc_ID, score) -> System.out.println(Doc_ID + " : " + score));

			// Get frequency of the term "police" from its postings
			return sorted_document_matrix;
		
			}
	
	
	public static LinkedHashMap<String, Double> sortHashMapByValues(HashMap<String, Double> passedMap){
	    List<String> mapKeys = new ArrayList<>(passedMap.keySet());
	    List<Double> mapValues = new ArrayList<>(passedMap.values());
	    Collections.sort(mapValues,Collections.reverseOrder());
	    Collections.sort(mapKeys,Collections.reverseOrder());

	    LinkedHashMap<String, Double> sortedMap =
	        new LinkedHashMap<>();

	    java.util.Iterator<Double> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Double val = valueIt.next();
	        java.util.Iterator<String> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	        	String key = keyIt.next();
	            Double comp1 = passedMap.get(key);
	            Double comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}
	public static String fetchText(String text, String tag) {
		if (text.indexOf("<"+tag+">")!=-1 && text.indexOf("</"+tag+">")!=-1) {
			int sIndex = text.indexOf("<" + tag + ">") + ("<" + tag + ">").length();
			int eIndex = text.indexOf("</" + tag + ">");
			return(text.substring(sIndex, eIndex).trim());
		}
		else {
			return "NULL";
		}
	}
	
	public static void fileWrite(HashMap <String , Double> mp, String num, PrintWriter writer, int queryLength, String algo) {
		java.util.Iterator<Entry<String, Double>> it = mp.entrySet().iterator();
		int count = 1;
		while (it.hasNext()&&count<=1000) {
			Map.Entry pair = (Map.Entry)it.next();
			//System.out.println(pair.getKey() + " = " + pair.getValue() + "count"+count);
			if (queryLength==0) {
				writer.format("%3d %3d %14s %3d %.18f %15s %3s",Integer.parseInt(num),0,pair.getKey(),count,pair.getValue(),algo+"_Short","\n");
			}
			if (queryLength==1) {
				writer.format("%3d %3d %14s %3d %.18f %15s %3s",Integer.parseInt(num),0,pair.getKey(),count,pair.getValue(),algo+"_Long","\n");
			}
			it.remove(); 
			count++;
		}
	}
	
}
