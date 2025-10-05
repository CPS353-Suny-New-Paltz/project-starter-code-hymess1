package project.model;

/**
 * Holds delimiter preferences for output formatting.
 * Example: keyValueDelimiter = ":"  pairDelimiter = ";"
 */
public class DelimiterSpec {
    private final boolean useDefaults;
    private final String pairDelimiter;
    private final String keyValueDelimiter;

    public DelimiterSpec(boolean useDefaults, String pairDelimiter, String keyValueDelimiter) {
        this.useDefaults = useDefaults;
        this.pairDelimiter = (pairDelimiter == null) ? ";" : pairDelimiter;
        this.keyValueDelimiter = (keyValueDelimiter == null) ? ":" : keyValueDelimiter;
    }

    public boolean isUseDefaults() { 
    	return useDefaults;
    	}
    public String getPairDelimiter() { 
    	return pairDelimiter;
    	}
    public String getKeyValueDelimiter() {
    	return keyValueDelimiter;
    	}

    public static DelimiterSpec defaults() {
        return new DelimiterSpec(true, ";", ":");
    }
}
