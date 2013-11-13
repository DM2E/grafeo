package eu.dm2e.grafeo;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.dm2e.grafeo.jena.GrafeoImpl;

/**
 * This file was created within the DM2E project.
 * http://dm2e.eu
 * http://github.com/dm2e
 * <p/>
 * Author: Kai Eckert, Konstantin Baierer
 */

public class GrafeoTest {
	
	@SuppressWarnings("unused")
	private String
		res1 = "http://res1",
		res2 = "http://res2",
		res3 = "http://res3";

    @Test
    public void testEscaping() {
        GrafeoImpl g = new GrafeoImpl();
        String test = "http://foo";
        String uri1 = "<http://foo>";
        String lit1 = "\"http://foo\"";
        String lit = g.literal(test).toEscapedString();
        String uri = g.resource(test).toEscapedString();
        assert(lit.equals(lit1));
        assert(uri.equals(uri1));

    }

	/**
	 *
	 * @see eu.dm2e.grafeo.Grafeo#containsTriple(String,String, eu.dm2e.grafeo.GLiteral)
	 */
	@Test
	public void containsStatementPatternGLiteral() {
		GrafeoImpl grafeo = new GrafeoImpl();
		String s = "dc:foo",
			   p = "dc:bar";
		GLiteral o = grafeo.literal(42);
		grafeo.addTriple(s,p,o);
		assertTrue(grafeo.containsTriple(s,p,o));
	}

	/**
	 *
	 * @see eu.dm2e.grafeo.Grafeo#containsTriple(String,String,String)
	 */
	@Test
	public void containsStatementPatternString() {
		GrafeoImpl grafeo = new GrafeoImpl();
		String s = "dc:foo",
			   p = "dc:bar",
			   o = "dc:quux";
		grafeo.addTriple(s,p,o);
		assertTrue(grafeo.containsTriple(s,p,o));
	}
	
	@Test
	public void testIsa() {
		GrafeoImpl g = new GrafeoImpl();
		g.addTriple(res1, "rdf:type", res2);
		assertTrue(g.get(res1).isa(res2));
	}
	
	@Test
	public void testJsonLd() {
		GrafeoImpl g = new GrafeoImpl();
		GResource bn = g.blank();
		g.addTriple(res1, "rdf:type", res2);
		g.addTriple(res1, "dc:foo", res2);
		g.addTriple(res1, "dc:bar", g.literal(23));
		g.addTriple(res1, "dc:quux", bn);
		g.addTriple(bn, "dc:fnork", "foaf:Blott");
		System.out.println(g.getTerseTurtle());
		System.out.println(g.getJsonLd());
	}

}
