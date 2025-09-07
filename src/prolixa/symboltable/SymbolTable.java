package prolixa.symboltable;

import java.util.HashMap;
import java.util.LinkedList;

public class SymbolTable {

    private LinkedList<HashMap<HashTableKey, SymbolInfo>> list = new LinkedList<>();

    /**
     * Adds a new hash table to the top of the stack
     */
    public void push () {
        list.addFirst(new HashMap<HashTableKey, SymbolInfo>());
    }

    /**
     * Remove the hash table from the top of the stack
     * @return removed element
     */
    public HashMap<HashTableKey, SymbolInfo> pop () {
        return list.removeFirst();
    }

    /**
     * Fetch the hash table at the top of the stack
     * @return top hash table
     */
    public HashMap<HashTableKey, SymbolInfo> getTop () {
        return list.getFirst();
    }

    /**
     * Adds elements to the hash table from the top of the stack
     * @param key element identifier
     * @param info information related to the element
     */
    public void add (String key, SymbolInfo info) {
        if (list.size() < 1) {
            push();
        }
        var table = list.getFirst();
        table.put(new HashTableKey(key), info);
    }

    /**
     * Searches, starting from the top of the stack, for the first element with the corresponding identifier
     * @param key element identifier
     * @return element related to the identifier
     */
    public SymbolInfo find (String key) {
        for (var table : this.list) {
            var element = table.get(new HashTableKey(key));
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    /**
     * Uses the 'find' method, but returns a message to the console
     * @param key element identifier
     */
    public void findWithConsole (String key) {
        var element = find(key);
        if (element == null) {
            System.out.println("Could not find " + key);
        } else {
            System.out.println("Identifier: " + key + "\n" + element.toString());
        }
    }

    public void printAll () {
        System.out.println("Imprimindo todos os elementos:");
        for (var table : this.list) {
            for (var element : table.entrySet()){
            	System.out.println("===================================");
            	System.out.println(element.getKey().toString() + element.getValue().toString());
            }
        }
    }

}