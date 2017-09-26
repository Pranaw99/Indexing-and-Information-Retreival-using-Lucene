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
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
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
import java.util.List;
import java.util.Map;
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
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
//import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
//import org.jdom2.*;
//import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;



/**

 * Index all text files under a directory.

 * <p>

 * This is a command-line application demonstrating simple Lucene indexing. Run

 * it with no command-line arguments for usage information.

 */

public class generateIndex {

	private generateIndex() {
	}
	/** Index all text files under a directory. */

	public static void main(String[] args) {

		//ArrayList<HashMap<String, String>> documents = new ArrayList();

		String indexPath = "C:\\Users\\bhada\\Downloads\\IU_Sem3\\Search";
		String docPath = "C:\\Users\\bhada\\Downloads\\IU_Sem3\\Search\\Assignments\\Data\\corpus";
		final Path docDir = Paths.get(docPath);
		//System.out.println("docDir" + docDir);

		try {
			//System.out.println("Indexing to directory '" + indexPath + "'...");
			Directory dir = FSDirectory.open(Paths.get(indexPath));

			// here we have use standard analyzer
			Analyzer analyzer = new StandardAnalyzer();

			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, iwc);
			
			File folder = new File("C:\\Users\\bhada\\Downloads\\IU_Sem3\\Search\\Assignments\\Data\\corpus");
			File[] listOfFiles = folder.listFiles();
			//System.out.println(listOfFiles);
			ArrayList<HashMap<String, String>> documents = new ArrayList();

			for (int i = 0; i < listOfFiles.length; i++) {
			  File file = listOfFiles[i];
			  
			  //System.out.println(file);
			  if (file.isFile() && file.getName().endsWith(".trectext")) {

				/**if (i == 0) {
					System.out.println(content);
				}**/
			
				try {
					
					List<InputStream> streams = Arrays.asList(
							new ByteArrayInputStream("<ROOT>".getBytes()),
							new FileInputStream(file),
							new ByteArrayInputStream("</ROOT>".getBytes())
							);
				
				  InputStream is = new SequenceInputStream(Collections.enumeration(streams));
				  String theString = IOUtils.toString(is,"UTF-8");
	              String pattern = "&\\s";
	              theString = theString.replaceAll(pattern, "&amp;");
	              InputStream ist = new ByteArrayInputStream(theString.getBytes(StandardCharsets.UTF_8.name()));
				  
				  
				  
		          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		          org.w3c.dom.Document doc = dBuilder.parse(ist);


		          	NodeList nList  = doc.getElementsByTagName("DOC");

		          //System.out.println(nList.getLength());
		          		          		          		          
		          for (int temp = 0; temp < nList.getLength(); temp++) {
		             Node nNode = nList.item(temp);
		             //System.out.println("nNode" + nNode.getLength());
		             Element eElement = (Element) nNode;
		             NodeList cell = eElement.getChildNodes();
		             //System.out.println("child" + cell.getLength());
		             //System.out.println(cell.item(0));
		             String TEXTupdate = "";
		             String HEADupdate = "";
		             String DOCNOupdate = "";
		             String BYLINEupdate = "";
		             String DATELINEupdate = "";
		             
		             
		             //System.out.println("check each doc length" + cell.getLength());
		             
		             HashMap<String, String> document = new HashMap<String, String>(); 
		             for (int j = 0; j < cell.getLength(); j++) {		            	 
		            	 Node kNode = cell.item(j);
		            	 if (kNode.getNodeType() == Node.ELEMENT_NODE) {
		            		 //System.out.println("isme gush raha hai");
		            		 Element celement = (Element) kNode; 
		            		 //System.out.println(celement.getTagName());
		            	 		            	 
		            	 if (celement.getTagName().equals("DOCNO")) {
		            		 //System.out.println("isme gush raha hai");
		            		 //System.out.println(celement.getTextContent());
		            		 DOCNOupdate = DOCNOupdate + celement.getTextContent();
		            		 NodeList nList1 = celement.getElementsByTagName("DOCNO");
		            		 //System.out.println("Doc no. len" + nList1.getLength());
			            	 for (int K = 0; temp < nList1.getLength(); temp++) {
					             Node nNode1 = nList1.item(temp);

					             //System.out.println("\nCurrent Element :" + nNode1.getNodeName());
					             
					             if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
					                Element eElement1 = (Element) nNode1;
					                System.out.println("isme gush raha hai ki nahi");
					                DOCNOupdate = DOCNOupdate + eElement1.getTextContent();		               			                
					             }
					             
					          }
			            	 //System.out.println(DOCNOupdate);
			            	 document.put("DOCNO",DOCNOupdate);		            		 
		            	 }
		            	 if (celement.getAttribute("BYLINE") != null) {
		            		 BYLINEupdate = BYLINEupdate + celement.getTextContent();
		            		 NodeList nList2 = celement.getElementsByTagName("BYLINE");
			            	 for (int K = 0; temp < nList2.getLength(); temp++) {
					             Node nNode1 = nList2.item(temp);

					             //System.out.println("\nCurrent Element :" + nNode1.getNodeName());
					             
					             if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
					                Element eElement1 = (Element) nNode1;
					                BYLINEupdate = BYLINEupdate + eElement1.getTextContent();		               			                
					             }
					          }
			            	 document.put("BYLINE",BYLINEupdate);
		            		 
		            	 }
		            	 
		            	 if (celement.getAttribute("DATELINE") != null) {
		            		 DATELINEupdate = DATELINEupdate + celement.getTextContent();
		            		 NodeList nList3 = celement.getElementsByTagName("DATELINE");
			            	 for (int K = 0; temp < nList3.getLength(); temp++) {
					             Node nNode1 = nList3.item(temp);

					             //System.out.println("\nCurrent Element :" + nNode1.getNodeName());
					             
					             if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
					                Element eElement1 = (Element) nNode1;
					                DATELINEupdate = DATELINEupdate + eElement1.getTextContent();		               			                
					             }
					          }
			            	 document.put("DATELINE",DATELINEupdate);
		            	 }
		            	 
		            	 if (celement.getAttribute("HEAD") != null) {
		            		 HEADupdate = HEADupdate + celement.getTextContent();
		            		 NodeList nList4 = celement.getElementsByTagName("HEAD");
			            	 for (int K = 0; temp < nList4.getLength(); temp++) {
					             Node nNode1 = nList4.item(temp);

					             //System.out.println("\nCurrent Element :" + nNode1.getNodeName());
					             
					             if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
					                Element eElement1 = (Element) nNode1;
					                HEADupdate = HEADupdate + eElement1.getTextContent();		               			                
					             }
					          }
			            	 document.put("HEAD",HEADupdate);
		            	 }
		            	 if (celement.getAttribute("TEXT") != null) {
		            		 //System.out.println("TEXT MAIN GHUSA HAI");
		            		 
		            		 TEXTupdate = TEXTupdate + celement.getTextContent();
		            		 NodeList nList5 = celement.getElementsByTagName("TEXT");
			            	 for (int K = 0; temp < nList5.getLength(); temp++) {
					             Node nNode1 = nList5.item(temp);

					             //System.out.println("\nCurrent Element :" + nNode1.getNodeName());
					             
					             if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
					                Element eElement1 = (Element) nNode1;
					                TEXTupdate = TEXTupdate + eElement1.getTextContent();		               			                
					             }
					          }
			            	 document.put("TEXT",TEXTupdate);		            		 
		            	 }	
		            	 } 
		             }
		            	 
		             indexDoc(writer, document);
		          }
			       } catch (Exception e) {
			    	   e.printStackTrace();
			    	   System.out.println("Check_error");
			       }
		             
		             

	                
			             }
			          }

			writer.forceMerge(1);
			writer.commit();
			writer.close();
			
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			//Print the total number of documents in the corpus
			System.out.println("Total number of documents in the corpus: "+reader.maxDoc());
			//Print the number of documents containing the term "new" in <field>TEXT</field>.
			System.out.println("Number of documents containing the term \"new\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "new")));
			//Print the total number of occurrences of the term "new" across all documents for <field>TEXT</field>.
			System.out.println("Number of occurrences of \"new\" in the field \"TEXT\": "+reader.totalTermFreq(new Term("TEXT","new")));
			Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
			//Print the size of the vocabulary for <field>TEXT</field>, applicable when the index has only one segment.
			System.out.println("Size of the vocabulary for this field: "+vocabulary.size());
			//Print the total number of documents that have at least one term for <field>TEXT</field>
			System.out.println("Number of documents that have at least one term for this field: "+vocabulary.getDocCount());
			//Print the total number of tokens for <field>TEXT</field>
			System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
			//Print the total number of postings for <field>TEXT</field>
			System.out.println("Number of postings for this field: "+vocabulary.getSumDocFreq());
			//Print the vocabulary for <field>TEXT</field>
			TermsEnum iterator = vocabulary.iterator();
			BytesRef byteRef = null;
			System.out.println("\n*******Vocabulary-Start**********");
			while((byteRef = iterator.next()) != null) {
				String term = byteRef.utf8ToString();
				//System.out.print(term+"\t");
				}
			System.out.println("\n*******Vocabulary-End**********");
			reader.close();

			System.out.println("Done ...");

		} catch (IOException e) {

			System.out.println(" caught a " + e.getClass()

					+ "\n with message: " + e.getMessage());
		}

	}

	/** Indexes a single document 

	 * @throws IOException */

	static void indexDoc(IndexWriter writer, HashMap<String, String> document) throws IOException {
		// make a new, empty document
		Document lDoc = new Document();
		lDoc.add(new StringField("DOCNO", document.get("DOCNO"),
				Field.Store.YES));
		lDoc.add(new StringField("HEAD", document.get("HEAD"),
				Field.Store.YES));
		lDoc.add(new StringField("BYLINE", document.get("BYLINE"),
				Field.Store.YES));
		lDoc.add(new StringField("DATELINE", document.get("DATELINE"),
				Field.Store.YES));
		lDoc.add(new TextField("TEXT", document.get("TEXT"), Field.Store.NO));
		writer.addDocument(lDoc);
	}

}
