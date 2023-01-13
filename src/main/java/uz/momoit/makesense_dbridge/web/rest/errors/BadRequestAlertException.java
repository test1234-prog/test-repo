package uz.momoit.makesense_dbridge.web.rest.errors;

public class BadRequestAlertException extends RuntimeException {

    private String defaultMessage;
    private String entityName;
    private String errorKey;

    public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
        this.defaultMessage = defaultMessage;
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }
}
