package eu.dm2e.ws.api;



import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.dm2e.ws.NS;
import eu.dm2e.ws.OmnomUnitTest;
import eu.dm2e.ws.api.json.OmnomJsonSerializer;
import eu.dm2e.ws.grafeo.Grafeo;
import eu.dm2e.ws.grafeo.jena.GrafeoImpl;
import eu.dm2e.ws.model.JobStatus;

public class JobPojoTest extends OmnomUnitTest {
	
	@Test
	public void testSerializeToJson() {
		
		JobPojo job = new JobPojo();
		job.setStatus(JobStatus.STARTED);

		JsonObject expect = new JsonObject();
		expect.addProperty(SerializablePojo.JSON_FIELD_UUID, job.getUuid());
		expect.addProperty(NS.OMNOM.PROP_JOB_STATUS, JobStatus.STARTED.name());
		expect.add(NS.OMNOM.PROP_LOG_ENTRY, new JsonArray());
		expect.add(NS.OMNOM.PROP_ASSIGNMENT, new JsonArray());
		expect.addProperty(SerializablePojo.JSON_FIELD_RDF_TYPE, job.getRDFClassUri());

		assertEquals(job, OmnomJsonSerializer.deserializeFromJSON(expect.toString(), JobPojo.class));
		assertEquals(testGson.toJson(expect), OmnomJsonSerializer.serializeToJSON(job, JobPojo.class));
		assertEquals(testGson.toJson(expect), job.toJson());
	}
	
	@Test
	public void testDeserializeFromJson() {

		JobPojo expectedJob = new JobPojo();
		
		JsonObject expectedJson = new JsonObject();
		expectedJson.addProperty(NS.OMNOM.PROP_JOB_STATUS, JobStatus.NOT_STARTED.name());
		expectedJson.addProperty(SerializablePojo.JSON_FIELD_UUID, createUUID());
//		expectedJson.add("logEntries", new JsonArray());
//		expectedJson.add("outputParameterAssignments", new JsonArray());
		expectedJson.addProperty(SerializablePojo.JSON_FIELD_RDF_TYPE, new JobPojo().getRDFClassUri());
		
		JobPojo deserializedJob = OmnomJsonSerializer.deserializeFromJSON(expectedJson.toString(), JobPojo.class);
		
//		log.info(expectedJson.toString());
		
		assertEquals(expectedJob, deserializedJob);
//		assertEquals(testGson.toJson(expectedJson), OmnomJsonSerializer.serializeToJSON(deserializedJob, LogEntryPojo.class));
//		assertEquals(testGson.toJson(expectedJson), deserializedJob.toJson());
	}
	
	@Test
	public void testExecutesPosition() {
		{
		JobPojo expJob = new JobPojo();
		expJob.setId("htp://foo/job1");
		WorkflowPositionPojo posPojo = new WorkflowPositionPojo();
		posPojo.setId("http://foo/pos1");
		expJob.setExecutesPosition(posPojo);
		log.debug(expJob.getTerseTurtle());
		}
		{
			String asTTL = "@prefix omnom: <http://onto.dm2e.eu/omnom#> . " 
				+ "<http://foo/job1>"
		        + "a                       omnom:Job ;"
				+ "omnom:status            \"NOT_STARTED\" ;"
		        + "omnom:executesPosition  <http://foo/pos1> .";
			Grafeo g = new GrafeoImpl();
			g.readHeuristically(asTTL);
			log.debug(g.getTerseTurtle());
		}
	}

}
