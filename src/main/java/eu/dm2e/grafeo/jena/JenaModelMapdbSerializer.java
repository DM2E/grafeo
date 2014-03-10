package eu.dm2e.grafeo.jena;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import org.mapdb.Serializer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaModelMapdbSerializer implements Serializer<Model>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID	= 1L;

	@Override
	public Model deserialize(DataInput in, int available) throws IOException {
		Model model = ModelFactory.createDefaultModel();
		if (available > 0) {
			byte[] buf = new byte[available];
			in.readFully(buf);
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			model.read(bais, "", "N-TRIPLES");
			bais.close();
		}
		return model;
	}

	@Override
	public int fixedSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void serialize(DataOutput out, Model model) throws IOException {
		OutputStream jenaOut = new ByteArrayOutputStream();
		model.write(jenaOut, "N-TRIPLES");
		byte[] buf = new byte[1024];
		ByteArrayInputStream jenaIn = new ByteArrayInputStream(buf);
		for (int read = jenaIn.read(); read > 0 ;) {
			out.write(buf);
		}
		jenaIn.close();
	}
	
}