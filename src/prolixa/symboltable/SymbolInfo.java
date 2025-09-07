package prolixa.symboltable;

import prolixa.prolixa.Type;

public class SymbolInfo {

    private final Type type;
    private Boolean isInitialized;
    private final Boolean isMutable;
    private final int[] dimensions;
 
    public SymbolInfo (Type type, Boolean isInitialized, Boolean isMutable) {
        this.type = type;
        this.isInitialized = isInitialized;
        this.isMutable = isMutable;
        this.dimensions = null;
    }
    
    public SymbolInfo (Type type, Boolean isInitialized, Boolean isMutable, int[] dimensions) {
        this.type = type;
        this.isInitialized = isInitialized;
        this.isMutable = isMutable;
        this.dimensions = dimensions;
    }

    public Type getType () {
        return type;
    }

    public Boolean getIsInitialized() {
        return isInitialized;
    }

    public Boolean getIsMutable() {
        return isMutable;
    }
    
    public int[] getDimensions() {
    	return dimensions;
    }
    
    public int getDimensionsSize() {
    	return dimensions != null? dimensions.length : 1;
    }

    public void setIsInitialized(Boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    @Override
    public String toString () {
        return String.format("Type: %s\nIs Initialized?: %s\nIs Mutable?: %s", type, isInitialized, isMutable);
    }
}
