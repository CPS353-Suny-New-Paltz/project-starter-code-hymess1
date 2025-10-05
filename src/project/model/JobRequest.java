package project.model;

/**
 * Represents a job submission request from the network layer.
 * Bundles together input, output, and formatting info.
 */
public class JobRequest {
    private final String inputSourcePointer;
    private final String outputDestinationPointer;
    private final DelimiterSpec delimiterSpec;

    public JobRequest(String inputSourcePointer, String outputDestinationPointer, DelimiterSpec delimiterSpec) {
        this.inputSourcePointer = inputSourcePointer;
        this.outputDestinationPointer = outputDestinationPointer;
        this.delimiterSpec = delimiterSpec;
    }

    public String getInputSourcePointer() { 
    	
    	return inputSourcePointer; 
    	
    	}
    
    public String getOutputDestinationPointer() { 
    	
    	return outputDestinationPointer;
    	
    	}
    
    public DelimiterSpec getDelimiterSpec() { 
    	
    	return delimiterSpec;
    	
    	}
}
