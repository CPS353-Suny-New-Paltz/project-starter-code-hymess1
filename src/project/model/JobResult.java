package project.model;

/**
 * Represents the outcome of a job.
 * Contains success flag, result text, and an optional error message.
 */
public class JobResult {
    private final boolean success;
    private final String resultText;
    private final String errorMessage;

    public JobResult(boolean success, String resultText, String errorMessage) {
        this.success = success;
        this.resultText = (resultText == null) ? "" : resultText;
        this.errorMessage = (errorMessage == null) ? "" : errorMessage;
    }

    public boolean isSuccess() { return success; }
    public String getResultText() { return resultText; }
    public String getErrorMessage() { return errorMessage; }
}
