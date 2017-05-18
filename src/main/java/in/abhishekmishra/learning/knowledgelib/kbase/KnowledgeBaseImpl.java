package in.abhishekmishra.learning.knowledgelib.kbase;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;

public class KnowledgeBaseImpl implements KnowledgeBase {

	private String dbFolder;
	private Dataset ds;
	private Model model;

	private Resource conceptMetaResource;
	private Property isAProperty;

	public KnowledgeBaseImpl(String dbFolder) {
		this.dbFolder = dbFolder;

		// initDatabase();
		initDefaultInMemoryModel();

		conceptMetaResource = model.createResource(CONCEPT_NS + "Concept");
		isAProperty = model.createProperty(RELATIONS_NS + "isA");
	}

	private void initDatabase() {
		ds = TDBFactory.createDataset(dbFolder);
		model = ds.getDefaultModel();
	}

	private void initDefaultInMemoryModel() {
		model = ModelFactory.createDefaultModel();
	}

	public Resource addConcept(Concept concept) {
		Resource conceptResource = model.createResource(CONCEPT_NS + concept.getName());

		model.add(conceptResource, isAProperty, conceptMetaResource);
		return conceptResource;
	}

	public boolean conceptExists(String conceptName) {
		Resource conceptResource = model.getResource(CONCEPT_NS + conceptName);
		return conceptResource != null;
	}

	public void writeModel() {
		// print the Model as RDF/XML
		model.write(System.out, "RDF/XML-ABBREV");
		System.out.println();
		model.write(System.out, "N-TRIPLE");
	}

	public Model getModel() {
		return model;
	}

	public static void main(String args[]) {
		KnowledgeBaseImpl kb = new KnowledgeBaseImpl("test");
		kb.addConcept(new ConceptImpl("Country"));
		System.out.println(kb.conceptExists("County"));

		kb.writeModel();
	}

	public Resource getConceptMetaResource() {
		return conceptMetaResource;
	}

	public Property getIsAProperty() {
		return isAProperty;
	}
}
