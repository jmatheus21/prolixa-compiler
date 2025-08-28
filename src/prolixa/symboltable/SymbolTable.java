package prolixa.symboltable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SymbolTable {

    private static final Logger logger = Logger.getLogger(SymbolInfo.class.getName());
    private LinkedList<HashMap<HashTableKey, SymbolInfo>> list = new LinkedList<>();

    /**
     * Adds a new hash table to the top of the stack
     */
    public void push () {
        list.addFirst(new HashMap<HashTableKey, SymbolInfo>());
        logger.info("New scope created!");
    }

    /**
     * Remove the hash table from the top of the stack
     * @return removed element
     */
    public HashMap<HashTableKey, SymbolInfo> pop () {
        logger.info("Removing scope...");
        return list.removeFirst();
    }

    /**
     * Fetch the hash table at the top of the stack
     * @return top hash table
     */
    public HashMap<HashTableKey, SymbolInfo> getTop () {
        logger.info("Getting current scope...");
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
        logger.info("Element " + key + " added to current scope");
    }

    /**
     * Searches, starting from the top of the stack, for the first element with the corresponding identifier
     * @param key element identifier
     * @return element related to the identifier
     */
    public SymbolInfo find (String key) {
        for (int i = 0; i < this.list.size(); i++) {
            var table = list.get(i);
            var element = table.get(new HashTableKey(key));
            if (element != null) {
                logger.info("Element found successfully!");
                return element;
            }
        }
        logger.log(Level.WARNING, "No element with key " + key + " found!");
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

}