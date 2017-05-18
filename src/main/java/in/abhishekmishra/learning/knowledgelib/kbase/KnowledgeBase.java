package in.abhishekmishra.learning.knowledgelib.kbase;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public interface KnowledgeBase {
	public static final String BASE_URI = "http://abhishekmishra.in/learning/knowledge/";
	public static final String CONCEPT_NS = "http://abhishekmishra.in/learning/knowledge/concept#";
	public static final String RELATIONS_NS = "http://abhishekmishra.in/learning/knowledge/relations#";

	public Resource addConcept(Concept concept);

	public boolean conceptExists(String conceptName);
	
	public Resource getConceptMetaResource();

	public Property getIsAProperty(); 
	
	public Model getModel();

}
