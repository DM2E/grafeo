package eu.dm2e.grafeo.util;



//CHECKSTYLE.OFF: JavadocVariable

/**
 * Central list of RDF entities.
 *
 * <p>
 * Every vocabulary is represented by a static final class, which must define
 * a BASE constant (the base URI of the vocabulary) and constants for all entities,
 * (by convention prefixed with CLASS_ for classes and PROP_ for properties).
 * </p>
 *
 * @author Konstantin Baierer
 *
 */
public final class NS {


	/**
	 * RDFS.
	 */
	public static final class RDFS {
		public static final String BASE = "http://www.w3.org/2000/01/rdf-schema#";
		public static final String PROP_LABEL = BASE + "label";
		public static final String PROP_COMMENT = BASE + "comment";
	}

	/**
	 * Collections Ontology.
	 */
	public static final class CO {

		public static final String BASE = "http://purl.org/co/";

		public static final String PROP_ITEM_CONTENT = BASE + "itemContent";
		public static final String PROP_SIZE		   = BASE + "size";
		public static final String PROP_INDEX        = BASE + "index";
		public static final String PROP_ITEM  	   = BASE + "item";
		public static final String PROP_FIRST_ITEM   = BASE + "firstItem";
		public static final String PROP_NEXT_ITEM   = BASE + "nextItem";
		public static final String PROP_LAST_ITEM   = BASE + "lastItem";

		public static final String CLASS_LIST = BASE + "List";
		public static final String CLASS_ITEM = BASE + "Item";
	}

	/**
	 * RDF.
	 */
	public static final class RDF {

		public static final String BASE      = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		public static final String PROP_TYPE = BASE + "type";
	}

	/** SKOS. */
	public static final class SKOS {

		public static final String BASE       = "http://www.w3.org/2004/02/skos/core#";
		public static final String PROP_LABEL = BASE + "label";
	}

	/**
	 * Dublin Core Elements.
	 */
	public static final class DC {

		public static final String BASE       = "http://purl.org/dc/elements/1.1/";
		public static final String PROP_TITLE = BASE + "title";
		public static final String PROP_DATE  = BASE + "date";

	}


}
