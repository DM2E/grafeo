package eu.dm2e.ws.api;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;

import eu.dm2e.ws.Config;
import eu.dm2e.ws.grafeo.GResource;
import eu.dm2e.ws.grafeo.Grafeo;
import eu.dm2e.ws.grafeo.annotations.RDFClass;
import eu.dm2e.ws.grafeo.annotations.RDFInstancePrefix;
import eu.dm2e.ws.grafeo.jena.GrafeoImpl;

public abstract class AbstractPersistentPojo<T> {
	
    Logger log = Logger.getLogger(getClass().getName());
//	public static getClass {
//		return T;
//	}
	abstract String getId();
	abstract void setId(String id);
	
	public URI getIdAsURI() {
		URI uri = null;
		try {
			uri = new URI(getId());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}
	
	// TODO this should be a static method but it's impossible to determine the runtime static class
	public T constructFromRdfString(String rdfString, String id) {
		Grafeo g = new GrafeoImpl();
		g.readHeuristically(rdfString);
		T theThing = g.getObjectMapper().getObject(this.getClass(), id);
		return theThing;
	}
	public T constructFromRdfString(String rdfString) {
		Grafeo g = new GrafeoImpl();
		g.readHeuristically(rdfString);
		String rdfType = this.getClass().getAnnotation(RDFClass.class).value();
		String prefix = "http://FOOBAR/";
		try { 
			prefix = this.getClass().getAnnotation(RDFInstancePrefix.class).value();
		} catch (NullPointerException e) {
			// TODO
			throw(e);
		}
		GResource topBlank = g.findTopBlank(rdfType);
		T theThing;
		if (null != topBlank) {
			String newURI = prefix + UUID.randomUUID().toString();;
			topBlank.rename(newURI);
			theThing = g.getObjectMapper().getObject(this.getClass(), newURI);
		}
		else {
			throw new RuntimeException("No top blank node.");
		}
		return theThing;
	}
	public void readFromEndPointById(String id) {
		this.readFromEndPointById(id, false);
	}
	
	public void readFromEndPointById(String id, boolean expand) {
		this.setId(id);
        String endPoint = Config.getString("dm2e.ws.sparql_endpoint");
        Grafeo g = new GrafeoImpl();
        g.readFromEndpoint(endPoint, this.getId());
        // Add statements from graphs that are terminal nodes in this graph
        if (expand) {
        	log.info("Before expansion: "+ g.size());
        	for (GResource gres : g.listResourceObjects()) {
//        		g.readFromEndpoint(endPoint, gres.getUri());
        		try {
					g.readHeuristically(new URL(gres.getUri()).openStream());
				} catch (IOException e) {
					log.warning("Couldn't derefrence <"+gres.getUri()+"> Trying to read from endpoint");
					g.readFromEndpoint(endPoint, gres.getUri());
				}
    		}
        	log.info("After expansion: "+ g.size());
        }
		T theNewPojo = g.getObjectMapper().getObject(this.getClass(), this.getId());
        try {
            BeanUtils.copyProperties(this, theNewPojo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("An exception occurred: " + e, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("An exception occurred: " + e, e);
        }
    }


	public void publish(String endPoint, String graph) {
        log.info("Writing to endpoint: " + endPoint + " / Graph: " + graph);
		Grafeo g = new GrafeoImpl();
		g.getObjectMapper().addObject(this);
		g.emptyGraph(endPoint, graph);
		g.writeToEndpoint(endPoint, graph);
	}
	public void publish(String endPoint) {
		if (null == this.getId()) {
			String prefix;
			try {
				prefix = this.getClass().getAnnotation(RDFInstancePrefix.class).value();
			} catch (NullPointerException e) {
				prefix = "http://data.dm2e.eu/THIS_CLASS_SHOULD_HAVE_A_RDFINSTANCEPREFIX/";
			}
			String newURI = prefix+UUID.randomUUID().toString();
			this.setId(newURI);
		}
		this.publish(endPoint, this.getId());
	}
	public void publish() {
		String endPoint = Config.getString("dm2e.ws.sparql_endpoint_statements");
		this.publish(endPoint);
	}
	
	public Grafeo getGrafeo() {
		GrafeoImpl g = new GrafeoImpl();
		g.getObjectMapper().addObject(this);
		return g;
	}
	
	public String getTurtle() {
		return getGrafeo().getTurtle();
	}
	public String getNTriples() {
		return getGrafeo().getNTriples();
	}
	public String getCanonicalNTriples() {
		return getGrafeo().getCanonicalNTriples();
	}
	
}
