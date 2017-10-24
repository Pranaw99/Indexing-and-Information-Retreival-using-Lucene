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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
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

public class compareAlgorithms {
	public static void main(String[] args) throws IOException, ParseException {
		//PrintWriter shortWriter = new PrintWriter("TF_IDF"+"_Short.txt", "UTF-8");
		//PrintWriter longWriter = new PrintWriter("TF_IDF"+"_Long.txt", "UTF-8");
		PrintWriter shortWriter_BIM25 = new PrintWriter("BM25"+"_Short.txt", "UTF-8");
		PrintWriter longWriter_BIM25 = new PrintWriter("BM25"+"_Long.txt", "UTF-8");
		PrintWriter shortWriter_LMDS = new PrintWriter("LMDS"+"_Short.txt", "UTF-8");
		PrintWriter longWriter_LMDS = new PrintWriter("LMDS"+"_Long.txt", "UTF-8");
		PrintWriter shortWriter_LMJMS = new PrintWriter("LMJMS"+"_Short.txt", "UTF-8");
		PrintWriter longWriter_LMJMS = new PrintWriter("LMJMS"+"_Long.txt", "UTF-8");
		PrintWriter shortWriter_VMS = new PrintWriter("VMS"+"_Short.txt", "UTF-8");
		PrintWriter longWriter_VMS = new PrintWriter("VMS"+"_Long.txt", "UTF-8");
		int begin,end;
		//System.out.println("Total number of documents in the corpus: "+reader.maxDoc());
		/// Here is the logic to fetch information from the topic file
		String inpQuery = new String(Files.readAllBytes(Paths.get("C:\\Users\\bhada\\Downloads\\IU_Sem3\\Search\\Assignments\\Assignment2\\topics.51-100")), StandardCharsets.UTF_8);
		//**************** hashmap creation to store the results for long and short query **********//
		HashMap<String, Float> resShortquery = new HashMap<String, Float>();
		HashMap<String, Float> resLongquery = new HashMap<String, Float>();
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
			
				//System.out.println("Running TF_IDF for Short Query : "+shortQuery);
				
				//***** calling BM25 model for retrieval model ***********// 
				resShortquery = callAlgorithm(shortQuery,"BM25");
				fileWrite(resShortquery, number, shortWriter_BIM25, 0, "BM25");
				//System.out.println("Running TF_IDF for Long Query : "+longQuery);
				resLongquery = callAlgorithm(longQuery,"BM25");
				fileWrite(resLongquery, number, longWriter_BIM25, 1, "BM25");
				System.out.println("BM25 Done..");
				
				//***** calling LMDS model for retrieval model ***********// 
				resShortquery = callAlgorithm(shortQuery,"LMDS");
				fileWrite(resShortquery, number, shortWriter_LMDS, 0, "LMDS");
				//System.out.println("Running TF_IDF for Long Query : "+longQuery);
				resLongquery = callAlgorithm(longQuery,"LMDS");
				fileWrite(resLongquery, number, longWriter_LMDS, 1, "LMDS");
				System.out.println("LMDS Done..");

				//***** calling LMJMS model for retrieval model ***********// 
				resShortquery = callAlgorithm(shortQuery,"LMJMS");
				fileWrite(resShortquery, number, shortWriter_LMJMS, 0, "LMJMS");
				//System.out.println("Running TF_IDF for Long Query : "+longQuery);
				resLongquery = callAlgorithm(longQuery,"LMJMS");
				fileWrite(resLongquery, number, longWriter_LMJMS, 1, "LMJMS");
				System.out.println("LMJMS Done..");
				
				//***** calling VMS model for retrieval model ***********// 
				resShortquery = callAlgorithm(shortQuery,"VMS");
				fileWrite(resShortquery, number, shortWriter_VMS, 0, "VMS");
				//System.out.println("Running TF_IDF for Long Query : "+longQuery);
				resLongquery = callAlgorithm(longQuery,"VMS");
				fileWrite(resLongquery, number, longWriter_VMS, 1, "VMS");
				System.out.println("VMS Done..");
				
				// iterate to the next query
				inpQuery = inpQuery.substring(0,inpQuery.indexOf("<top>")) + inpQuery.substring(inpQuery.indexOf("</top>")+"</top>".length(),inpQuery.length());
				inpQuery = inpQuery.trim();
		}
		shortWriter_BIM25.close();longWriter_BIM25.close();
		shortWriter_LMDS.close();longWriter_LMDS.close();
		shortWriter_LMJMS.close();longWriter_LMJMS.close();
		shortWriter_VMS.close();longWriter_VMS.close();
		//shortWriter_BIM25.close();longWriter_BIM25.close();
	}
	
	public static HashMap<String, Float> callAlgorithm(String Query, String searchAlgo) throws IOException, ParseException{
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths
			.get("C:\\Users\\bhada\\Downloads\\IU_Sem3\\Search\\Assignments\\Assignment2\\index\\index")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(reader);
		if (searchAlgo.equals("BM25")) {
		searcher.setSimilarity(new BM25Similarity());	
		} else if(searchAlgo.equals("LMDS")) {
		searcher.setSimilarity(new LMDirichletSimilarity());	
		} else if(searchAlgo.equals("LMJMS")) {
			searcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));	
		}else if (searchAlgo.equals("VMS")) {
			searcher.setSimilarity(new ClassicSimilarity());	
		}
		
		HashMap<String, Float> document_matrix = new HashMap<String, Float>();		
		String queryString = Query;
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("TEXT", analyzer);
		org.apache.lucene.search.Query query = parser.parse(queryString);
		TopDocs topDocs = searcher.search(query, 1000);
		ScoreDoc[] docs = topDocs.scoreDocs;
		for (int i = 0; i < docs.length; i++) {
		Document doc = searcher.doc(docs[i].doc);
		//System.out.println(doc.get("DOCNO")+" "+docs[i].score);
		document_matrix.put(doc.get("DOCNO"), docs[i].score);
		}
		return document_matrix;
	}
	//***************** code to fetch the data between tags **************************//
	public static String fetchText(String text, String tag) {
		if (text.indexOf("<"+tag+">")!=-1 && text.indexOf("</"+tag+">")!=-1) {
			int startIndex = text.indexOf("<" + tag + ">") + ("<" + tag + ">").length();
			int endIndex = text.indexOf("</" + tag + ">");
			return(text.substring(startIndex, endIndex).trim());
		}
		else {
			return "NULL";
		}
	}
	
	//********************** code to write data into the files **************************//
	
	public static void fileWrite(HashMap <String , Float> mp, String num, PrintWriter writer, int queryLength, String algo) {
		java.util.Iterator<Entry<String, Float>> it = mp.entrySet().iterator();
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
