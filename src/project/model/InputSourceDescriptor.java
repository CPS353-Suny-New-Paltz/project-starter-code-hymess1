package project.model;

/**
 * Describes an abstract input source (e.g., file, URI, etc.).
 * For now, just a string pointer for simplicity.
 */
public abstract class InputSourceDescriptor {
    private final String sourcePointer;

    public InputSourceDescriptor(String sourcePointer) {
        this.sourcePointer = (sourcePointer == null) ? "" : sourcePointer;
    }

    public String getSourcePointer() {
        return sourcePointer;
    }
}
