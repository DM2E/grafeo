package eu.dm2e.ws.api;

import eu.dm2e.ws.grafeo.annotations.Namespaces;
import eu.dm2e.ws.grafeo.annotations.RDFClass;
import eu.dm2e.ws.grafeo.annotations.RDFId;
import eu.dm2e.ws.grafeo.annotations.RDFProperty;


@Namespaces({"omnom", "http://onto.dm2e.eu/omnom/"})
@RDFClass("omnom:ParameterSlot")
public class ParameterSlotPojo {

	@RDFId
	private String id;
	
	@RDFProperty("omnom:inputForPosition")
	private WorkflowPositionPojo inputForPosition;
	
	@RDFProperty("omnom:outputForPosition")
	private WorkflowPositionPojo outputForPosition;
	
	@RDFProperty("omnom:forParam")
	private ParameterPojo forParam;
	
	@RDFProperty("omnom:connectedSlot")
	private ParameterSlotPojo connectedSlot;
	
	/******************
	 * GETTERS/SETTERS
	 *****************/

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public WorkflowPositionPojo getInputForPosition() { return inputForPosition; }
	public void setInputForPosition(WorkflowPositionPojo inputForPosition) { this.inputForPosition = inputForPosition; }

	public WorkflowPositionPojo getOutputForPosition() { return outputForPosition; }
	public void setOutputForPosition(WorkflowPositionPojo outputForPosition) { this.outputForPosition = outputForPosition; }

	public ParameterPojo getForParam() { return forParam; }
	public void setForParam(ParameterPojo forParam) { this.forParam = forParam; }

	public ParameterSlotPojo getConnectedSlot() { return connectedSlot; }
	public void setConnectedSlot(ParameterSlotPojo connectedSlot) { this.connectedSlot = connectedSlot; }
}