package prolixa.symboltable;

public class Program {
    public static void main (String[] args) {
        SymbolTable table = new SymbolTable();

        // adicionando variáveis/constantes ao escopo global
        table.add("i", new SymbolInfo("int", true, true));
        table.add("j", new SymbolInfo("int", false, true));

        // adicionando novo escopo
        table.push();

        // adicionando variável ao novo escopo
        table.add("i", new SymbolInfo("char", false, false));

        // buscando variável/constante na tabela de símbolos
        table.findWithConsole("i");
        table.findWithConsole("j");

        // saindo do escopo
        table.pop();

        // buscando variável/constante na tabela de símbolos no escopo global
        table.findWithConsole("i");

    }
}
