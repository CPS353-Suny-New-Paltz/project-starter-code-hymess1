package project.api.process;

import project.annotations.ProcessAPI;

/**
 * Process boundary API: data storage system ↔ compute engine.
 * - Abstracts IO details behind simple, stable contracts.
 */
@ProcessAPI
public interface DataIOService {

    /**
     * Returns an abstract pointer describing the input source (e.g., path/URI).
     * Never returns null; return "" if not configured.
     */
    String readInputSourcePointer();

    /**
     * Writes result text to the destination pointer.
     * Returns true on success, false on failure. Never throws across boundary.
     */
    boolean writeToDestination(String destinationPointer, String resultText);
}

