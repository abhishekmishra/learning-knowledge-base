package in.abhishekmishra.learning.knowledgelib.kbase;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkeletalKnowledgeBaseImpl implements KnowledgeBase {
	private static final Logger LOG = LoggerFactory.getLogger(SkeletalKnowledgeBaseImpl.class);

	protected Model model;
	protected Resource conceptMetaResource;
	protected Property isAProperty;

	public SkeletalKnowledgeBaseImpl() {
	}

	protected void initDatabase() {
		model = ModelFactory.createDefaultModel();
	}

	public Resource addConcept(Concept concept) {
		LOG.debug("Adding concept " + concept);
		executeTransaction((Model m) -> {
			LOG.debug("model is " + m);
			Resource conceptResource = m.createResource(CONCEPT_NS + concept.getName());
			m.add(conceptResource, getIsAProperty(), getConceptMetaResource());
			return true;
		});
		return executeQuery((Model m) -> {
			Resource conceptResource = m.getResource(CONCEPT_NS + CONCEPT_NS + concept.getName());
			return conceptResource;
		});
	}

	public boolean conceptExists(String conceptName) {
		return executeQuery((Model m) -> {
			Resource conceptResource = m.getResource(CONCEPT_NS + conceptName);
			return conceptResource != null;
		});
	}

	public void writeModel() {
		// print the Model as RDF/XML
		model.write(System.out, "RDF/XML-ABBREV");
		System.out.println();
		model.write(System.out, "N-TRIPLE");
	}

	public Model getModel() {
		if (model == null) {
			initDatabase();
		}
		return model;
	}

	public <T> T executeTransaction(KnowledgeBaseTransaction<T> transaction) {
		LOG.debug("Started transaction " + transaction.toString());
		return transaction.execute(getModel());
	}

	public <T> T executeQuery(KnowledgeBaseQuery<T> query) {
		LOG.debug("Started query " + query.toString());
		return query.execute(getModel());
	}

	public Resource getConceptMetaResource() {
		if (conceptMetaResource == null) {
			executeTransaction((Model m) -> {
				conceptMetaResource = m.createResource(CONCEPT_NS + "Concept");
				return true;
			});
		}
		return conceptMetaResource;
	}

	public Property getIsAProperty() {
		if (isAProperty == null) {
			executeTransaction((Model m) -> {
				isAProperty = m.createProperty(RELATIONS_NS + "isA");
				return true;
			});
		}
		return isAProperty;
	}

}