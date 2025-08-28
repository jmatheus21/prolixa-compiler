package prolixa.symboltable;

public class SymbolInfo {

    private String type;
    private Boolean isInitialized;
    private Boolean isMutable;
 
    public SymbolInfo (String type, Boolean isInitialized, Boolean isMutable) {
        this.type = type;
        this.isInitialized = isInitialized;
        this.isMutable = isMutable;
    }

    public String getType () {
        return type;
    }

    public Boolean getIsInitialized() {
        return isInitialized;
    }

    public Boolean getIsMutable() {
        return isMutable;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsInitialized(Boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    public void setIsMutable(Boolean isMutable) {
        this.isMutable = isMutable;
    }

    @Override
    public String toString () {
        return String.format("Type: %s\nIs Initialized?: %s\nIs Mutable?: %s", type, isInitialized, isMutable);
    }
}
