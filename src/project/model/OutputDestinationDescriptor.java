package project.model;

/**
 * Describes an abstract output destination (e.g., file, URI, etc.).
 * For now, just a string pointer for simplicity.
 */
public abstract class OutputDestinationDescriptor {
    private final String destinationPointer;

    public OutputDestinationDescriptor(String destinationPointer) {
        this.destinationPointer = (destinationPointer == null) ? "" : destinationPointer;
    }

    public String getDestinationPointer() {
        return destinationPointer;
    }
}
