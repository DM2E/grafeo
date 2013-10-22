package eu.dm2e.grafeo.json;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

/**
 * Serialize and Deserialize Joda DateTime to/from JSON
 */
public class JodaDateTimeSerializer implements JsonSerializer<DateTime>, JsonDeserializer<DateTime>{
	
	  @Override
	  public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
		  DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		  return new JsonPrimitive(fmt.print(src));
	  }
	  
	  @Override
	  public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
	    return new DateTime(json.getAsString());
	  }

}
