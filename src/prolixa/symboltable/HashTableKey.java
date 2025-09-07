package prolixa.symboltable;

public class HashTableKey {
    
    private String key;
    private final int SIZE = 101;
    private final int SHIFT = 4;

    public HashTableKey (String key) {
        this.key = key;
    }

    public String getKey () {
        return key;
    }

    @Override
    public int hashCode () {
        int temp = 0;
        for (int i = 0; i < key.length(); i++) {
            temp = ((temp << SHIFT) + key.charAt(i)) % SIZE;
        }
        return temp;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashTableKey obj = (HashTableKey) o;
        return key.equals(obj.key);
    }

    @Override
    public String toString () {
        return String.format("Chave: %s\n", key);
    }
}
