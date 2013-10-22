package eu.dm2e.grafeo.json;

import com.google.gson.*;
import eu.dm2e.grafeo.gom.SerializablePojo;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Convenience class wrapping JSON serialization/deserialization functionality for easy reuse.
 *
 * @author Konstantin Baierer
 */
public class GrafeoJsonSerializer {
	
//	private transient static Logger log = LoggerFactory.getLogger(GrafeoJsonSerializer.class.getName());

//	private static class SpecificClassExclusionStrategy implements ExclusionStrategy {
//		private final Class<?> excludedThisClass;
//
//		public SpecificClassExclusionStrategy(Class<?> excludedThisClass) {
//			this.excludedThisClass = excludedThisClass;
//		}
//
//		@Override
//		public boolean shouldSkipClass(Class<?> clazz) {
//			return excludedThisClass.equals(clazz);
//		}
//
//		@Override
//		public boolean shouldSkipField(FieldAttributes f) {
//			return excludedThisClass.equals(f.getDeclaredClass());
//		}
//	}
	
	private static Gson gson;
    private static final GsonBuilder gsonBuilder;
	
	static {
		
//		exclude
		
		gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
//		gsonBuilder.registerTypeAdapter(SerializablePojo.class, new SerializablePojoJsonSerializer());
//		gsonBuilder.registerTypeAdapter(AbstractPersistentPojo.class, new SerializablePojoJsonSerializer());
		
		// FIXME this is the generic solution but when used like that, no type
		// parameter can be passed on to SerializablePojoJsonSerializer<T>()
//		Reflections reflections = new Reflections("eu.dm2e.ws.api");
//		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RDFClass.class);
//		for (Class<?> clazz : annotated) {
//			log.debug("Registering JSON serializer for class : " + clazz);
//			gsonBuilder.registerTypeAdapter(clazz, new SerializablePojoJsonSerializer());
//		}
		gsonBuilder.registerTypeAdapter(DateTime.class, new JodaDateTimeSerializer());
		gson = gsonBuilder.create();
	}

    public static <T> void registerType(Type T) {
        gsonBuilder.registerTypeAdapter(T, new SerializablePojoJsonSerializer<T>());
        gson = gsonBuilder.create();
    }
	
	public static String serializeToJSON(List<? extends SerializablePojo> pojoList) {
		JsonArray retArray = new JsonArray();
		for (SerializablePojo pojo : pojoList) {
			retArray.add(pojo.toJsonObject());
		}
		return gson.toJson(retArray);
	}
	
	public static <T> String serializeToJSON(List<? extends SerializablePojo<T>> pojoList, Type T) {
		JsonArray retArray = new JsonArray();
		for (SerializablePojo<T> pojo : pojoList) {
			retArray.add(serializeToJsonObject(pojo, T));
		}
		return gson.toJson(retArray);
	}
	
	public static <T> JsonObject serializeToJsonObject(SerializablePojo<T> pojo, Type T) {
		 JsonElement jsonElem = gson.toJsonTree(pojo, T);
		 if (! jsonElem.isJsonObject()) {
			 throw new RuntimeException(pojo + " was serialized to something other than a JSON object: " + jsonElem.getClass());
		 }
		 JsonObject json = jsonElem.getAsJsonObject();
		 if (pojo.hasId())
			 json.addProperty(SerializablePojo.JSON_FIELD_ID, pojo.getId());
		 if (null != pojo.getRDFClassUri())
			 json.addProperty(SerializablePojo.JSON_FIELD_RDF_TYPE, pojo.getRDFClassUri());
		 return json;
	}
				 
	
	public static <T> String serializeToJSON(SerializablePojo<T> pojo, Type T) {
		JsonObject json = serializeToJsonObject(pojo, T);
		return gson.toJson(json);
	}
	public static <T> T deserializeFromJSON(String jsonStr, Class<T> T) {
		return (T) gson.fromJson(jsonStr, T);
	}
	
}
