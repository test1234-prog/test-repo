package uz.momoit.makesense_dbridge.web.rest.errors;

public class BadRequestAlertException extends RuntimeException {

    private final String defaultMessage;
    private final String  entityName;
    private final String errorKey;

    public BadRequestAlertException(String defaultMessage, String entityName, String errorKey) {
        this.defaultMessage = defaultMessage;
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
