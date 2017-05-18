package in.abhishekmishra.learning.knowledgelib.kbase.ingest.plugin;

import java.io.IOException;

public interface DataLoaderPlugin {

	public void load() throws IOException;
}
