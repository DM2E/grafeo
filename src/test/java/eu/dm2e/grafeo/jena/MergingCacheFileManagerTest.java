package eu.dm2e.grafeo.jena;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MergingCacheFileManagerTest {
	
	private static final Logger log = LoggerFactory.getLogger(MergingCacheFileManagerTest.class);
	
	@Test
	public void test() throws Exception {
		
		MergingCacheFileManager fm = new MergingCacheFileManager();
		for (int i = 0;i < 100; i++) {
			String dummyURI = "http://foo/" + i;
			fm.readModel(null, dummyURI);
			fm.addCacheModel(dummyURI, null);
		}
		log.debug("{} : ", fm.getFromCache("htp://foo/" + 1));
		
	}

}
