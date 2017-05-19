package in.abhishekmishra.learning.knowledgelib.kbase;

import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface KnowledgeBaseTransaction<T> {
	public T execute(Model knowledgeBaseModel);
}
