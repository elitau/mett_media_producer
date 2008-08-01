package multimonster.common.edit;

import java.io.Serializable;

/**
 * This is the detailed description of a filter.
 * It is used to inform clients in a standardised way
 * about filters and their functionality.
 * 
 * @author Holger Velke (sihovelk)
 */
public class FilterDetail implements Serializable {
    
	/**
	 * the name of the filter
	 */
	private String filterName;
	
	/**
	 * The plugin that has to be used when using this
	 * filter.
	 */
	private FilterPlugInIdentifier id;
	
    /**
     * Freetext infromation about the filter.
     * Descripton that could be diplayed to the user
     */
    private String detailedInformation;   
    
    /**
     * The filter-specific <code>FilterAction</code>.
     */
    private FilterAction action;    
       
	/**
	 * @param id The plugin that has to be used when using this
	 * filter.
	 * @param detailedInformation Freetext infromation about the 
	 * filter. Descripton that could be diplayed to the user
	 * @param action The filter-specific <code>FilterAction</code>.
	 */
	public FilterDetail(
		FilterPlugInIdentifier id,
		String detailedInformation,
		FilterAction action) {
		super();
		this.id = id;
		this.detailedInformation = detailedInformation;
		this.action = action;
	}
		
	/**
	 * @return Returns the action.
	 */
	public FilterAction getAction() {
		return action;
	}

	/**
	 * @return Returns the detailedInformation.
	 */
	public String getDetailedInformation() {
		return detailedInformation;
	}

	/**
	 * @return Returns the id.
	 */
	public FilterPlugInIdentifier getId() {
		return id;
	}

	/**
	 * @return Returns the filterName.
	 */
	public String getFilterName() {
		return filterName;
	}

}
