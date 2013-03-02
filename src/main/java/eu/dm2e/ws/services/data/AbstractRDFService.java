package eu.dm2e.ws.services.data;


import com.hp.hpl.jena.rdf.model.Model;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public abstract class AbstractRDFService {

    public static final String PLAIN = MediaType.TEXT_PLAIN;
    public static final String XML = "application/rdf+xml";
    public static final String TTL_A = "application/x-turtle";
    public static final String TTL_T = "text/turtle";
    public static final String N3 = "text/rdf+n3";


    List<Variant> supportedVariants;
    Map<MediaType, String> mediaType2Language = new HashMap<MediaType, String>();
    @Context
    Request request;


    protected AbstractRDFService() {
        this.supportedVariants = Variant.mediaTypes(
                MediaType.valueOf(PLAIN),
                MediaType.valueOf(XML),
                MediaType.valueOf(TTL_A),
                MediaType.valueOf(TTL_T),
                MediaType.valueOf(N3)
        ).add().build();
        mediaType2Language.put(MediaType.valueOf(PLAIN), "N-TRIPLE");
        mediaType2Language.put(MediaType.valueOf(XML), "RDF/XML");
        mediaType2Language.put(MediaType.valueOf(TTL_A), "TURTLE");
        mediaType2Language.put(MediaType.valueOf(TTL_T), "TURTLE");
        mediaType2Language.put(MediaType.valueOf(N3), "N3");
    }

    protected Response handleRDF(Model model) {
        Variant selectedVariant = request.selectVariant(supportedVariants);
        assert selectedVariant != null;

        return Response.ok(new RDFOutput(model, selectedVariant.getMediaType()), selectedVariant.getMediaType()).build();

    }

    protected class RDFOutput implements StreamingOutput {
        Logger log = Logger.getLogger(getClass().getName());
        Model model;
        MediaType mediaType;

        public RDFOutput(Model model, MediaType mediaType) {
            this.model = model;
            this.mediaType = mediaType;
        }

        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
            log.info("Mediatype: " + this.mediaType);
            model.setNsPrefix("dct", "http://purl.org/dc/terms/");
            model.write(output, mediaType2Language.get(this.mediaType));
        }
    }

}