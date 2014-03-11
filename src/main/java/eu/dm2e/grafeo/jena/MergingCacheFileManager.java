package eu.dm2e.grafeo.jena;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.atlas.AtlasException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class MergingCacheFileManager extends FileManager {
	
	private final Set<String> uriSet = new HashSet<>();
	private final Model mergedModel = ModelFactory.createDefaultModel();
	
	@Override
	public void addCacheModel(String uri, Model m) {
		uriSet.add(uri);
	}
	
	@Override
	public Model readModel(Model __ignoreModel, String filenameOrURI) {
		try {
			super.readModel(mergedModel, filenameOrURI);
		} catch (AtlasException e) {
			e.printStackTrace();
		}
		return mergedModel;
	}
	
	@Override
	public Model readModel(Model __model, String filenameOrURI, String baseURI, String syntax) {
		try{
			super.readModel(mergedModel, filenameOrURI, baseURI, syntax);
		} catch (AtlasException e) {
			e.printStackTrace();
		}
		return mergedModel;
	}
	
	@Override
	public Model readModel(Model __model, String filenameOrURI, String rdfSyntax) {
		try {
			super.readModel(mergedModel, filenameOrURI, rdfSyntax);
		} catch (AtlasException e) {
			e.printStackTrace();
		}
		return mergedModel;
	}
	
	@Override
	public Model getFromCache(String filenameOrURI) {
		if (uriSet.contains(filenameOrURI))
			return mergedModel;
		return null;
	}
	
	@Override
	public void removeCacheModel(String uri) {
		uriSet.remove(uri);
	}

}
