package in.abhishekmishra.learning.knowledgelib.kbase.ingest.plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import in.abhishekmishra.learning.knowledgelib.kbase.ConceptImpl;
import in.abhishekmishra.learning.knowledgelib.kbase.KnowledgeBase;
import in.abhishekmishra.learning.knowledgelib.kbase.KnowledgeBaseImpl;

/**
 * Extract countries and capitals from
 * https://en.wikipedia.org/wiki/List_of_national_capitals_in_alphabetical_order
 * 
 * @author Abhishek Mishra
 *
 */
public class CountryExtractor implements DataLoaderPlugin {

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
		countryResource = kb.addConcept(new ConceptImpl("Country"));
		capitalResource = kb.addConcept(new ConceptImpl("Capital"));
		capitalOfProperty = kb.getModel().createProperty(KnowledgeBase.RELATIONS_NS + "capitalOf");
		capitalProperty = kb.getModel().createProperty(KnowledgeBase.RELATIONS_NS + "capital");
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
		for (String country : countryAndCapitals.keySet()) {
			Resource countryInstanceResource = kb.getModel().createResource(COUNTRY_NS + country);
			kb.getModel().add(countryInstanceResource, kb.getIsAProperty(), countryResource);

			Resource capitalInstanceResource = kb.getModel()
					.createResource(CAPITAL_NS + countryAndCapitals.get(country));
			kb.getModel().add(capitalInstanceResource, kb.getIsAProperty(), capitalResource);

			System.out.println(country + " \t\t| " + countryAndCapitals.get(country));
			
			kb.getModel().add(countryInstanceResource, capitalProperty, capitalInstanceResource);
			kb.getModel().add(capitalInstanceResource, capitalOfProperty, countryInstanceResource);
		}

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
		KnowledgeBaseImpl kb = new KnowledgeBaseImpl("test");

		CountryExtractor countryExtractor = new CountryExtractor(kb);
		countryExtractor.load();
	}

}
