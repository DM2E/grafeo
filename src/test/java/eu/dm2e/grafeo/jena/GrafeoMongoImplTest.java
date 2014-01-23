package eu.dm2e.grafeo.jena;

import org.junit.Test;

import eu.dm2e.grafeo.Grafeo;

public class GrafeoMongoImplTest {
	
	@Test
	public void testPutToEndpoint() {
		
		final String endpoint = "localhost:27017:omnom:graphs";
		final String graph = "http://graph1";

		Grafeo g = new GrafeoMongoImpl();
		g.addTriple("http://foo", "http://bar", "http://baz");
		g.putToEndpoint(endpoint, graph);
		g.readFromEndpoint(endpoint, graph);
	}


}
