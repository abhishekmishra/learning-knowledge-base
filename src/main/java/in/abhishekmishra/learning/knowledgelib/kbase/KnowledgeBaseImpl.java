package in.abhishekmishra.learning.knowledgelib.kbase;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeBaseImpl extends SkeletalKnowledgeBaseImpl implements KnowledgeBase {
	private static final Logger LOG = LoggerFactory.getLogger(KnowledgeBaseImpl.class);

	private String assemblerFile;

	public KnowledgeBaseImpl(String assemblerFile) {
		super();
		this.assemblerFile = assemblerFile;
	}

	@Override
	public Model getModel() {
		throw new IllegalArgumentException("Use execute transaction to use the model to insert resources etc.");
	}

	public <T> T executeTransaction(KnowledgeBaseTransaction<T> transaction) {
		LOG.debug("Started transaction " + transaction.toString());

		//Dataset ds = TDBFactory.createDataset(assemblerFile);
		Dataset ds = TDBFactory.assembleDataset(assemblerFile);
		
		ds.begin(ReadWrite.WRITE);
		T result = null;
		try {
			result = transaction.execute(ds.getDefaultModel());
			ds.commit();
		} catch (Exception e) {
			LOG.error("Exception occured executing transaction." + e.getMessage());
		} finally {
			ds.end();
		}

		LOG.debug("Completed transaction " + transaction.toString());
		return result;
	}

	public <T> T executeQuery(KnowledgeBaseQuery<T> query) {
		//Dataset ds = TDBFactory.createDataset(assemblerFile);
		Dataset ds = TDBFactory.assembleDataset(assemblerFile);
		
		ds.begin(ReadWrite.READ);
		T result = null;
		try {
			result = query.execute(ds.getDefaultModel());
		} catch (Exception e) {
			LOG.error("Exception occured executing transaction." + e.getMessage());
		} finally {
			ds.end();
		}
		return result;

	}
}
