package eu.dm2e.grafeo.jena;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import eu.dm2e.grafeo.GResource;

public class GrafeoMongoImpl extends GrafeoImpl {
	private MongoClient mongoClient;
	private transient Logger log = LoggerFactory.getLogger(getClass().getName());
	private Map<String,DBCollection> dbMap = new HashMap<>();
	
	private DBCollection getMongoColl(String connStr) throws NumberFormatException, UnknownHostException {
		if (! dbMap.containsKey(connStr)) {
			String[] segs = connStr.split(":");
			assert(segs.length == 4);
			MongoClient client = new MongoClient(segs[0], Integer.valueOf(segs[1]));
			final DB db = client.getDB(segs[2]);
			dbMap.put(connStr, db.getCollection(segs[3]));
		}
		return dbMap.get(connStr);
	}
	
	private String mongoifyURI(String uri) { return uri.replaceAll(":", "__COLON__"); }
	private String unMongoifyURI(String uri) { return uri.replaceAll("__COLON__", ":"); }
	
	public GrafeoMongoImpl() { super(); }
	
	@Override
	public void emptyGraph(String endpoint, String graph) {
		DBCollection coll;
		try {
			coll = getMongoColl(endpoint);
			BasicDBObject needle = new BasicDBObject();
			needle.put("graph", mongoifyURI(graph));
			coll.remove(needle);
		} catch (NumberFormatException | UnknownHostException e) {
			final String msg = "Couldn't connect to MongoDB: " +  endpoint;
			log.error(msg, e);
			e.printStackTrace();
			throw new RuntimeException(msg, e);
		}
	}
	@Override public void emptyGraph(String endpoint, URI graph) { this.emptyGraph(endpoint, graph.toString()); }
	
	// TODO Think about how to handle posting (i.e. patching an existing model) Have to retrieve it first etc. pp.
	@Override public void postToEndpoint(String endpoint, String graph) { this.putToEndpoint(endpoint, graph); }
	@Override public void postToEndpoint(String endpoint, URI graphURI) { this.postToEndpoint(endpoint, graphURI.toString()); } 
	@Override public void postToEndpoint(URI endpointUpdate, String graph) { this.postToEndpoint(endpointUpdate.toString(), graph); }
	
	@Override
	public void putToEndpoint(String endpoint, String graph) {
		DBCollection coll;
		try {
			coll = getMongoColl(endpoint);
			BasicDBObject needle = new BasicDBObject();
			needle.put("graph", mongoifyURI(graph));
			BasicDBObject insert = new BasicDBObject();
			insert.put("graph", mongoifyURI(graph));
			insert.put("data", this.getNTriples());
			coll.update(needle, insert, true, false);
		} catch (NumberFormatException | UnknownHostException e) {
			final String msg = "Couldn't connect to MongoDB: " +  endpoint;
			log.error(msg, e);
			e.printStackTrace();
			throw new RuntimeException(msg, e);
		}
	}
	@Override public void putToEndpoint(String endpoint, URI graph) { this.putToEndpoint(endpoint, graph.toString()); }
	@Override public void putToEndpoint(URI endpointUpdate, String graph) { this.putToEndpoint(endpointUpdate.toString(), graph.toString()); }

	@Override
	public void readFromEndpoint(String endpoint, String graph, int expansionSteps, int retryCount) {
		DBCollection coll;
		try {
			coll = getMongoColl(endpoint);
			BasicDBObject needle = new BasicDBObject();
			final String mongoifiedGraph = mongoifyURI(graph);
			needle.put("graph", mongoifiedGraph);
			// upsert
			DBObject doc = coll.findOne(needle);
			if (null == doc) {
				log.warn("No such graph <{}> found in MongoDB collection '{}' ", graph, endpoint);
				return;
			}
			String serialized = (String) doc.get("data");
			this.readHeuristically(serialized);
		} catch (NumberFormatException | UnknownHostException e) {
			final String msg = "Couldn't connect to MongoDB: " +  endpoint;
			log.error(msg, e);
			e.printStackTrace();
			throw new RuntimeException(msg, e);
		}
        Set<GResource> resourceCache = new HashSet<GResource>();
        for ( ; expansionSteps > 0 ; expansionSteps--) {
        	log.debug("Expansion No. " + expansionSteps);
        	log.debug("Size Before expansion: "+ this.size());
        	for (GResource gres : this.listURIResources()) {
        		if (resourceCache.contains(gres)){
        			continue;
        		}
        		try {
        			log.debug("Reading graph " + graph + " from endpoint " + endpoint + ".");
        			this.readFromEndpoint(endpoint, graph, 0);
        		} catch (Throwable t) {
        			log.debug("Graph not found in endpoint: " + graph);
        			try {
						this.load(gres.getUri(), 0);
	        		} catch (Throwable t2) {
	        			log.warn("URI un-dereferenceable: " + graph);
	        			log.warn("Continuing because this concerns only nested resources.");
//	        			throw(t2);
	        		}
        		}
				resourceCache.add(gres);
    		}
        	log.debug("Size After expansion: "+ this.size());
        }
        if(log.isTraceEnabled())
	        log.trace("Summary: \n{}", summarizeClasses());
        log.debug("Reading from endpoint finished.");
	}
	@Override public void readFromEndpoint(String endpoint, String graph, int expansionSteps) { this.readFromEndpoint(endpoint, graph, expansionSteps, RETRY_COUNT); }
	@Override public void readFromEndpoint(String endpoint, URI graphURI) { this.readFromEndpoint(endpoint, graphURI.toString()); }
	@Override public void readFromEndpoint(URI endpoint, String graph) { this.readFromEndpoint(endpoint.toString(), graph); }
	@Override public void readFromEndpoint(URI endpoint, URI graph) { this.readFromEndpoint(endpoint.toString(), graph.toString()); }
	@Override public void readFromEndpoint(String endpoint, String graph) { this.readFromEndpoint(endpoint, graph, 0); }
}
