package in.abhishekmishra.learning.knowledgelib.kbase.query;

import java.io.IOException;

import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import in.abhishekmishra.learning.knowledgelib.kbase.KnowledgeBaseImpl;
import in.abhishekmishra.learning.knowledgelib.kbase.ingest.plugin.CountryExtractor;

public class CountryCapitalQuery {

	public static void main(String args[]) throws IOException {

		KnowledgeBaseImpl kb = new KnowledgeBaseImpl("test");
		CountryExtractor countryExtractor = new CountryExtractor(kb);
		countryExtractor.load();
		
	    // list countries
	    ResIterator iter = kb.getModel().listSubjectsWithProperty(countryExtractor.getCapitalOfProperty());
	    while (iter.hasNext()) {
	        Resource r = iter.nextResource();
	        System.out.println("Capital - " + r.toString());
	    }
		
		StmtIterator stmtIter = kb.getModel().listStatements();
		while(stmtIter.hasNext()) {
			Statement s = stmtIter.nextStatement();
			System.out.println(s);
		}
		
		//kb.getModel().write(System.out, "N-TRIPLE");
		//kb.getModel().write(System.out, "RDF/XML-ABBREV");
	}
}