package in.abhishekmishra.learning.knowledgelib.kbase;

import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface KnowledgeBaseQuery<T> {
	public T execute(Model m);
}
