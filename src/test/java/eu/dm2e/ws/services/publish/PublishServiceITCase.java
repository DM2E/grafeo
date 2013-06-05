package eu.dm2e.ws.services.publish;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

import eu.dm2e.ws.DM2E_MediaType;
import eu.dm2e.ws.OmnomTestCase;
import eu.dm2e.ws.OmnomTestResources;
import eu.dm2e.ws.api.JobPojo;
import eu.dm2e.ws.api.WebserviceConfigPojo;
import eu.dm2e.ws.api.WebservicePojo;
import eu.dm2e.ws.grafeo.Grafeo;
import eu.dm2e.ws.grafeo.jena.GrafeoImpl;
import eu.dm2e.ws.model.JobStatusConstants;

/**
 * This file was created within the DM2E project.
 * http://dm2e.eu
 * http://github.com/dm2e
 * <p/>
 * Author: Kai Eckert, Konstantin Baierer
 */
public class PublishServiceITCase extends OmnomTestCase {

    Logger log = Logger.getLogger(getClass().getName());

    String SERVICE_URI;

    @Before
    public void setUp() throws Exception {
        SERVICE_URI = URI_BASE + "publish";
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDescription() {

        log.info(SERVICE_URI);
        Grafeo g = new GrafeoImpl(client.getJerseyClient()
                .resource(SERVICE_URI)
                .accept("text/turtle")
                .get(InputStream.class));
        log.info(g.getTurtle());
        assertTrue(g.containsStatementPattern(SERVICE_URI, "rdf:type", "omnom:Webservice"));
        assertTrue(g.containsStatementPattern(SERVICE_URI, "omnom:inputParam", SERVICE_URI + "/param/to-publish"));
        assertTrue(g.containsStatementPattern(SERVICE_URI + "/param/to-publish", "rdf:type", "omnom:Parameter"));
        assertTrue(g.containsStatementPattern(SERVICE_URI, "omnom:inputParam", SERVICE_URI + "/param/dataset-id"));
        assertTrue(g.containsStatementPattern(SERVICE_URI + "/param/dataset-id", "rdf:type", "omnom:Parameter"));
        assertTrue(g.containsStatementPattern(SERVICE_URI, "omnom:inputParam", SERVICE_URI + "/param/endpoint-select"));
        assertTrue(g.containsStatementPattern(SERVICE_URI + "/param/endpoint-select", "rdf:type", "omnom:Parameter"));
        assertTrue(g.containsStatementPattern(SERVICE_URI, "omnom:inputParam", SERVICE_URI + "/param/endpoint-update"));
        assertTrue(g.containsStatementPattern(SERVICE_URI + "/param/endpoint-update", "rdf:type", "omnom:Parameter"));
        assertTrue(g.containsStatementPattern(SERVICE_URI, "omnom:inputParam", SERVICE_URI + "/param/label"));
        assertTrue(g.containsStatementPattern(SERVICE_URI + "/param/label", "rdf:type", "omnom:Parameter"));
        assertTrue(g.containsStatementPattern(SERVICE_URI, "omnom:inputParam", SERVICE_URI + "/param/comment"));
        assertTrue(g.containsStatementPattern(SERVICE_URI + "/param/comment", "rdf:type", "omnom:Parameter"));
    }


    @Test
    public void testPublish() {

        WebservicePojo ws = new WebservicePojo();
        ws.loadFromURI(SERVICE_URI);
        WebserviceConfigPojo config = new WebserviceConfigPojo();
        OmnomTestResources xmlRes = OmnomTestResources.PUBLISH_RDF;
        try {
            String xmlUri = client.publishFile(configFile.get(xmlRes));


            config.setWebservice(ws);
            config.publishToService();
            config.addParameterAssignment("to-publish", xmlUri);
            config.addParameterAssignment("dataset-id", "test-dataset");
            config.addParameterAssignment("label", "Test-Dataset (from Integration Test)");
            config.addParameterAssignment("comment", "This can safely be deleted.");
            // config.addParameterAssignment("endpoint-update", "http://lelystad.informatik.uni-mannheim.de:8080/openrdf-sesame/repositories/dm2etest/statements");
            // config.addParameterAssignment("endpoint-select", "http://lelystad.informatik.uni-mannheim.de:8080/openrdf-sesame/repositories/dm2etest");
            config.publishToService();

            log.info("Configuration created for Test: " + config.getTurtle());

            ClientResponse response = client
                    .resource(SERVICE_URI)
                    .type(DM2E_MediaType.TEXT_PLAIN)
                    .put(ClientResponse.class, config.getId());
            log.info("JOB STARTED WITH RESPONSE: " + response.getStatus() + " / Location: " + response.getLocation() + " / Content: " + response.getEntity(String.class));
            URI joburi = response.getLocation();
           /**
             * WAIT FOR JOB TO BE FINISHED
             */

            String status = JobStatusConstants.NOT_STARTED.name();
            JobPojo job = null;
            while (status.equals(JobStatusConstants.NOT_STARTED.name()) || status.equals(JobStatusConstants.STARTED.name())) {
                Grafeo g = new GrafeoImpl(joburi.toString());
                job = g.getObjectMapper().getObject(JobPojo.class, joburi.toString());
                status = job.getStatus();
                log.info("Check for status: " + status);
                log.info("JOB SO FAR: " + job.getTurtle());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("An exception occurred: " + e, e);
                }
            }

            /**
             * CHECK IF JOB IS FINISHED
             */
            log.info("Status: " + status);
            assert (status.equals(JobStatusConstants.FINISHED.name()));

        } catch (Exception e) {
            throw new RuntimeException("An exception occurred: " + e, e);
        }
    }
    @Test
    public void testPublish2() {

        WebservicePojo ws = new WebservicePojo();
        ws.loadFromURI(SERVICE_URI);
        WebserviceConfigPojo config = new WebserviceConfigPojo();
        OmnomTestResources xmlRes = OmnomTestResources.PUBLISH_RDF;
        try {
            String xmlUri = client.publishFile(configFile.get(xmlRes));


            config.setWebservice(ws);
            config.publishToService();
            config.addParameterAssignment("to-publish", xmlUri);
            config.addParameterAssignment("dataset-id", "test-dataset");
            config.addParameterAssignment("label", "Test-Dataset (from Integration Test)");
            config.addParameterAssignment("comment", "This can safely be deleted.");
            // config.addParameterAssignment("endpoint-update", "http://lelystad.informatik.uni-mannheim.de:8080/openrdf-sesame/repositories/dm2etest/statements");
            // config.addParameterAssignment("endpoint-select", "http://lelystad.informatik.uni-mannheim.de:8080/openrdf-sesame/repositories/dm2etest");
            config.publishToService();

            log.info("Configuration created for Test: " + config.getTurtle());

            ClientResponse response = client
                    .resource(SERVICE_URI)
                    .type(DM2E_MediaType.TEXT_PLAIN)
                    .put(ClientResponse.class, config.getId());
            log.info("JOB STARTED WITH RESPONSE: " + response.getStatus() + " / Location: " + response.getLocation() + " / Content: " + response.getEntity(String.class));
            URI joburi = response.getLocation();
            /**
             * WAIT FOR JOB TO BE FINISHED
             */

            String status = JobStatusConstants.NOT_STARTED.name();
            JobPojo job = null;
            while (status.equals(JobStatusConstants.NOT_STARTED.name()) || status.equals(JobStatusConstants.STARTED.name())) {
                Grafeo g = new GrafeoImpl(joburi.toString());
                job = g.getObjectMapper().getObject(JobPojo.class, joburi.toString());
                status = job.getStatus();
                log.info("Check for status: " + status);
                log.info("JOB SO FAR: " + job.getTurtle());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("An exception occurred: " + e, e);
                }
            }

            /**
             * CHECK IF JOB IS FINISHED
             */
            log.info("Status: " + status);
            assert (status.equals(JobStatusConstants.FINISHED.name()));

        } catch (Exception e) {
            throw new RuntimeException("An exception occurred: " + e, e);
        }
    }

}