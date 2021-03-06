package eu.dm2e.grafeo.jena;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

import eu.dm2e.grafeo.GResource;
import eu.dm2e.grafeo.Grafeo;
import eu.dm2e.grafeo.util.LogbackMarkers;

public class SparqlUpdate {
	
	private static final int WARN_TIME = 2000;

	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private String graph, endpoint, deleteClause, insertClause, whereClause;
	private boolean insertDataFlag, deleteDataFlag;
	private GrafeoImpl grafeo;
	private Map<String, String> prefixes;
	
	private boolean hasGraph() { return graph != null; };
//	private boolean hasEndpoint() { return endpoint != null; };
	private boolean hasDeleteClause() { return deleteClause != null; };
	private boolean hasInsertClause() { return insertClause != null; };
	private boolean hasWhereClause() { return whereClause != null; };

	public static class Builder {
		private String graph, endpoint, deleteClause, insertClause, whereClause;
		private GrafeoImpl grafeo;
        private Map<String,String> prefixes = new HashMap<>();
        private boolean insertDataFlag, deleteDataFlag;
		
		public Builder graph(String s)  	{ this.graph = s; return this; }
		public Builder graph(URI s)     	{ this.graph = s.toString(); return this; }
		public Builder graph(GResource s)  	{ this.graph = s.getUri(); return this; }
		
		public Builder endpoint(String s) 	{ this.endpoint = s; return this; }
		public Builder endpoint(URI s)     	{ this.endpoint = s.toString(); return this; }
		
		public Builder deleteAll() 			{ this.deleteClause = "?s ?p ?o"; return this; } 
		public Builder delete(String s) 	{ this.deleteClause = s; return this; }
		public Builder delete(Grafeo s) 	{ this.deleteClause = s.getNTriples(); deleteDataFlag = true; return this; }
		
		public Builder insert(String s) 	{ this.insertClause = s; return this; }
		public Builder insert(Grafeo s) 	{ this.insertClause = s.getNTriples(); insertDataFlag = true; return this; }
		
		public Builder where(String s) 		{ this.whereClause = s; return this; }
		
		public Builder grafeo(GrafeoImpl g) { this.grafeo = g; return this; }
		
        public Builder prefixes(Map<String,String> prefixes)	{ this.prefixes.putAll(prefixes); return this; }
        public Builder prefix(String prefix, String value) 		{ this.prefixes.put(prefix, value); return this; }
		
		public SparqlUpdate build() 		{ return new SparqlUpdate(this); }
	}

	public SparqlUpdate(Builder builder) {
		
		if (null == builder.deleteClause && null == builder.insertClause)
			throw new IllegalArgumentException("UPDATE query requires insert or delete or both.");
		
		this.graph = builder.graph;
		this.prefixes = builder.prefixes;
		this.deleteClause = builder.deleteClause;
		this.insertClause = builder.insertClause;
		this.whereClause = builder.whereClause;
		this.insertDataFlag = builder.insertDataFlag;
		this.deleteDataFlag = builder.deleteDataFlag;
		
		if (null != builder.endpoint && null != builder.grafeo)
			throw new IllegalArgumentException("Must set endpoint or grafeo, not both.");
		else if (null != builder.endpoint) 
			this.endpoint = builder.endpoint;
		else if (null != builder.grafeo) {
			this.grafeo = builder.grafeo;
			this.prefixes.putAll(this.grafeo.getNamespacesUsed());
		} else 
			throw new IllegalArgumentException("Must set exactly one of endpoint or grafeo.");
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
        if (!prefixes.keySet().isEmpty()) {
            for (String prefix : prefixes.keySet()) {
                sb.append("PREFIX ")
                  .append(prefix)
                  .append(": <")
                  .append(prefixes.get(prefix))
                  .append(">\n");
            }
            sb.append("\n");
        }
        // only insert
        if (! hasDeleteClause()) {
        	if (insertDataFlag) {
				sb.append( hasGraph()
					? String.format(" INSERT DATA { GRAPH <%s> { %s } }", graph, insertClause)
					: String.format(" INSERT DATA { %s }", insertClause));
        	} else {
				sb.append( hasGraph()
					? String.format(" INSERT { GRAPH <%s> { %s } }", graph, insertClause)
					: String.format(" INSERT { %s }", insertClause));
				sb.append( hasWhereClause()
					? hasGraph() 
						? String.format(" WHERE { GRAPH <%s> { %s } }", graph, whereClause)
						: String.format(" WHERE { %s }", whereClause)
					: "WHERE { }");	
        	}
        	
		// only delete	
        } else if (! hasInsertClause()) {
			if (deleteDataFlag) {
				sb.append( hasGraph()
					? String.format(" DELETE DATA { GRAPH <%s> { %s } }", graph, deleteClause)
					: String.format(" DELETE DATA { %s }", deleteClause));
			} else {
				sb.append( hasGraph()
					? String.format(" DELETE { GRAPH <%s> { %s } }", graph, deleteClause)
					: String.format(" DELETE { %s }", deleteClause));
				sb.append( hasWhereClause()
					?  hasGraph() 
						? String.format(" WHERE { GRAPH <%s> { %s } }", graph, whereClause)
						: String.format(" WHERE { %s }", whereClause)
					: hasGraph() 
						? String.format(" WHERE { GRAPH <%s> { %s } }", graph, deleteClause)
						: String.format(" WHERE { %s }", deleteClause));
				}
		// both insert and delete
        } else {
				sb.append( hasGraph()
					? String.format(" DELETE { GRAPH <%s> { %s } }", graph, deleteClause)
					: String.format(" DELETE { %s }", deleteClause));
				sb.append( hasGraph()
					? String.format(" INSERT { GRAPH <%s> { %s } }", graph, insertClause)
					: String.format(" INSERT { %s }", insertClause));
				sb.append( hasWhereClause()
					?  hasGraph() 
						? String.format(" WHERE { GRAPH <%s> { %s } }", graph, whereClause)
						: String.format(" WHERE { %s }", whereClause)
					: hasGraph() 
						? String.format(" WHERE { GRAPH <%s> { %s } }", graph, deleteClause)
						: String.format(" WHERE { %s }", deleteClause));
//				log.info("MODIFY+DELETE:" + sb.toString());
        }
		
		return sb.toString();
	}
	
	public void execute() {
		long startTime = System.currentTimeMillis();
        UpdateRequest update = UpdateFactory.create();
//        for (Entry<String, String> namespaceMapping : new GrafeoImpl().getNamespacesUsed().entrySet()) {
//        	update.setPrefix(namespaceMapping.getKey(), namespaceMapping.getValue());
//        }
        log.trace(LogbackMarkers.DATA_DUMP, "UPDATE query (created): {}", toString());
        update.add(toString());
        log.trace(LogbackMarkers.DATA_DUMP, "UPDATE query (Jena): {}", toString());
        UpdateProcessor exec;
		if (null != endpoint) {
			exec = UpdateExecutionFactory.createRemoteForm(update, endpoint);
		} else {
			GraphStore gs = GraphStoreFactory.create(grafeo.getModel());
			exec = UpdateExecutionFactory.create(update, gs);
		}
        exec.execute();
        long estimatedTime = System.currentTimeMillis() - startTime;
        if (estimatedTime > WARN_TIME) {
	        log.warn(LogbackMarkers.TRACE_TIME, "UPDATE took {}ms", estimatedTime);
        } else { 
	        log.trace(LogbackMarkers.TRACE_TIME, "UPDATE took {}ms", estimatedTime);
        }
	}
}
