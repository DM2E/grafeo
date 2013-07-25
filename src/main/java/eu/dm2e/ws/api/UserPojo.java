package eu.dm2e.ws.api;

import java.util.HashSet;
import java.util.Set;

import eu.dm2e.ws.NS;
import eu.dm2e.ws.grafeo.annotations.RDFClass;
import eu.dm2e.ws.grafeo.annotations.RDFProperty;

@RDFClass(NS.FOAF.CLASS_PERSON)
public class UserPojo extends AbstractPersistentPojo<UserPojo> {
	
//	@Override
//	public int getMaximumJsonDepth() { return 10; };
	
	public enum THEMES {
		  LIGHT("light")
		, DARK("dark")
		;
		
		private String name;
		public String getName() { return name; }
		THEMES(String val) {
			this.name = val;
		}
	}
	
	@RDFProperty(NS.FOAF.PROP_NAME)
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@RDFProperty(NS.OMNOM.PROP_PREFERRED_THEME)
	private String preferredTheme; // = THEMES.LIGHT.getName();
	public String getPreferredTheme() { return preferredTheme; }
	public void setPreferredTheme(String preferredTheme) { this.preferredTheme = preferredTheme; }
	
	@RDFProperty(value = NS.OMNOM.PROP_FILE_SERVICE, serializeAsURI = true)
	private Set<WebservicePojo> fileServices = new HashSet<>();
	public Set<WebservicePojo> getFileServices() { return fileServices; }
	public void setFileServices(Set<WebservicePojo> fileServices) { this.fileServices = fileServices; }

	@RDFProperty(value = NS.OMNOM.PROP_WEBSERVICE, serializeAsURI = true)
	private Set<WebservicePojo> webServices = new HashSet<>();
	public Set<WebservicePojo> getWebServices() { return webServices; }
	public void setWebServices(Set<WebservicePojo> webServices) { this.webServices = webServices; }
}
