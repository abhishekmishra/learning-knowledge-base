package in.abhishekmishra.learning.knowledgelib.kbase.ingest.plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import in.abhishekmishra.learning.knowledgelib.kbase.ConceptImpl;
import in.abhishekmishra.learning.knowledgelib.kbase.KnowledgeBase;
import in.abhishekmishra.learning.knowledgelib.kbase.SkeletalKnowledgeBaseImpl;

/**
 * Extract countries and capitals from
 * https://en.wikipedia.org/wiki/List_of_national_capitals_in_alphabetical_order
 * 
 * @author Abhishek Mishra
 *
 */
public class CountryExtractor implements DataLoaderPlugin {
	private static final Logger LOG = LoggerFactory.getLogger(CountryExtractor.class);

	public static final String COUNTRY_NS = KnowledgeBase.BASE_URI + "country/#";
	public static final String CAPITAL_NS = KnowledgeBase.BASE_URI + "capital/#";

	Map<String, String> countryAndCapitals;
	private KnowledgeBase kb;
	private Resource countryResource;
	private Resource capitalResource;
	private Property capitalOfProperty;
	private Property capitalProperty;

	public CountryExtractor(KnowledgeBase kb) throws IOException {
		this.kb = kb;
		countryAndCapitals = new HashMap<String, String>();
		LOG.debug("Started country extractor");
		countryResource = kb.addConcept(new ConceptImpl("Country"));
		capitalResource = kb.addConcept(new ConceptImpl("Capital"));
		capitalOfProperty = kb.executeTransaction((Model m) -> {
			return m.createProperty(KnowledgeBase.RELATIONS_NS + "capitalOf");
		});
		capitalProperty = kb.executeTransaction((Model m) -> {
			return m.createProperty(KnowledgeBase.RELATIONS_NS + "capital");
		});
	}

	private void initData() throws IOException {
		Document doc = Jsoup.connect("http://en.wikipedia.org/wiki/List_of_national_capitals_in_alphabetical_order")
				.get();
		Elements countryAndCapitalRows = doc.select(".wikitable tr");
		for (Element countryAndCapitalRow : countryAndCapitalRows) {
			Elements countries = countryAndCapitalRow.select("td:nth-child(2)");
			if (countries.size() > 0) {
				Elements capitals = countryAndCapitalRow.select("td:nth-child(1)");
				if (capitals.size() > 0) {
					// System.out.println("Country - " + countries.get(0).text()
					// + " capital - " + capitals.get(0).text());
					countryAndCapitals.put(countries.get(0).text(), capitals.get(0).text());
				}
			}
			// System.out.println(countryAndCapitalRow.text());
		}

		System.out.println("loaded " + countryAndCapitals.size() + " countries");
	}

	public void load() throws IOException {
		initData();
		kb.executeTransaction((Model m) -> {
			for (String country : countryAndCapitals.keySet()) {
				Resource countryInstanceResource = m.createResource(COUNTRY_NS + country);
				m.add(countryInstanceResource, kb.getIsAProperty(), countryResource);

				Resource capitalInstanceResource = m.createResource(CAPITAL_NS + countryAndCapitals.get(country));
				m.add(capitalInstanceResource, kb.getIsAProperty(), capitalResource);

				System.out.println(country + " \t\t| " + countryAndCapitals.get(country));

				m.add(countryInstanceResource, capitalProperty, capitalInstanceResource);
				m.add(capitalInstanceResource, capitalOfProperty, countryInstanceResource);
			}
			return true;
		});
	}

	public Resource getCountryResource() {
		return countryResource;
	}

	public Resource getCapitalResource() {
		return capitalResource;
	}

	public Property getCapitalOfProperty() {
		return capitalOfProperty;
	}

	public Property getCapitalProperty() {
		return capitalProperty;
	}

	public static void main(String args[]) throws IOException {
		//KnowledgeBase kb = new KnowledgeBaseImpl("test");
		KnowledgeBase kb = new SkeletalKnowledgeBaseImpl();

		CountryExtractor countryExtractor = new CountryExtractor(kb);
		countryExtractor.load();
	}

}
