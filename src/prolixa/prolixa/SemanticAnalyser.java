package prolixa.prolixa;

import java.util.LinkedList;

import prolixa.analysis.DepthFirstAdapter;
import prolixa.node.*;
import prolixa.symboltable.*;

public class SemanticAnalyser extends DepthFirstAdapter {

	private SymbolTable symbolTable = new SymbolTable();
	
	// ======================================================================================================
	// ----------------------------------------- DECLARAÇÕES ------------------------------------------------
	// ======================================================================================================

	@Override
	public void outAAVariavelADeclaracao(AAVariavelADeclaracao node) { 
		Type type = inferTyping(node.getTipo());
		SymbolInfo info = new SymbolInfo(type, false, true);
		symbolTable.add(node.getId().toString(), info);
	}

	@Override
	public void outAAConstanteADeclaracao(AAConstanteADeclaracao node) {
		Type type = inferTyping(node.getTipo());
		boolean isInitialized = node.getAtribuicaoConst() == null? false : true;
		SymbolInfo info = new SymbolInfo(type, isInitialized, false);
		if (isInitialized) {
			PAExp exp = ((AAAtribuicaoConstADeclaracao) node.getAtribuicaoConst()).getAExp();
			Type initializedType = inferTyping(exp);
			if (type != initializedType){
				throw new RuntimeException("ERRO: Esperado um " + type + " e encontrado um " + initializedType + " na declaração de constante");
			}
		}
		symbolTable.add(node.getId().toString(), info);
	}
	
	@Override
	public void outAAVetorADeclaracao (AAVetorADeclaracao node) {
		LinkedList<PADeclaracao> matriz = node.getMatriz();
		boolean isMatriz = matriz.size() > 0? true : false;
		int[] dimensions = new int[matriz.size() + 1];
		
		if (!(node.getAExp() instanceof AANumberValAExp)) {
			throw new RuntimeException("ERRO: Os índices " + (isMatriz? "da matriz" : "do vetor") +" devem ser valores estáticos do tipo 'NUMBER'");
		}
		
		String idxVetorString = ((AANumberValAExp) node.getAExp()).toString().strip();
		dimensions[0] = Integer.parseInt(idxVetorString);
		
		if (isMatriz) {			
			for (int i = 0; i < matriz.size(); i++) {
				AAMatrizADeclaracao nodeTermosMatriz = (AAMatrizADeclaracao) matriz.get(i);
				if (!(nodeTermosMatriz.getAExp() instanceof AANumberValAExp)) {
					throw new RuntimeException("ERRO: Os índices da matriz devem ser valores estáticos do tipo 'NUMBER'");
				}
				String idxMatrizString = ((AANumberValAExp) nodeTermosMatriz.getAExp()).toString().strip();
				dimensions[i + 1] = Integer.parseInt(idxMatrizString);
			}
		}
		
		Type type = inferTyping(node.getTipo());
		String id = node.getId().toString();
		SymbolInfo info = new SymbolInfo(type, false, true, dimensions);
		symbolTable.add(id, info);
	}
	
	// ======================================================================================================
	// ----------------------------------------- EXPRESSÕES -------------------------------------------------
	// ======================================================================================================
	
	@Override
	public void outAAExpOrAExp(AAExpOrAExp node) {		
		if (!(node.getDir() instanceof AAAnswerValAExp) || !(node.getEsq() instanceof AAAnswerValAExp)) {
			System.err.println("ERRO: Disjunção lógica deve ser feita apenas com valores do tipo 'answer'!");
		}
	}
	
	@Override
	public void outAAExpAndOrAExp(AAExpAndOrAExp node) {		
		if (!(node.getDir() instanceof AAAnswerValAExp) || !(node.getEsq() instanceof AAAnswerValAExp)) {
			System.err.println("ERRO: Conjunção lógica deve ser feita apenas com valores do tipo 'answer'!");
		}
	}
	
	@Override
	public void outAAExpXorOpAExp(AAExpXorOpAExp node) {		
		if (!(node.getDir() instanceof AAAnswerValAExp) || !(node.getEsq() instanceof AAAnswerValAExp)) {
			System.err.println("ERRO: Disjunção exclusiva deve ser feita apenas com valores do tipo 'answer'!");
		}
	}
	
	@Override
	public void outAAExpMaiorAExp(AAExpMaiorAExp node) {		
		if (!(node.getDir() instanceof AANumberValAExp) || !(node.getEsq() instanceof AANumberValAExp)) {
			System.err.println("ERRO: Operação de comparação '>' deve ser feita apenas com valores do tipo 'number'!");
		}
	}
	
	@Override
	public void outAAExpMenorAExp(AAExpMenorAExp node) {		
		if (!(node.getDir() instanceof AANumberValAExp) || !(node.getEsq() instanceof AANumberValAExp)) {
			System.err.println("ERRO: Operação de comparação '<' deve ser feita apenas com valores do tipo 'number'!");
		}
	}
	
	@Override
	public void outAAExpMaiIgualAExp(AAExpMaiIgualAExp node) {		
		if (!(node.getDir() instanceof AANumberValAExp) || !(node.getEsq() instanceof AANumberValAExp)) {
			System.err.println("ERRO: Operação de comparação '>=' deve ser feita apenas com valores do tipo 'number'!");
		}
	}
	
	@Override
	public void outAAExpMenIgualAExp(AAExpMenIgualAExp node) {		
		if (!(node.getDir() instanceof AANumberValAExp) || !(node.getEsq() instanceof AANumberValAExp)) {
			System.err.println("ERRO: Operação de comparação '<=' deve ser feita apenas com valores do tipo 'number'!");
		}
	}

	@Override
	public void outAAExpSomaAExp(AAExpSomaAExp node) {		
		if (inferTyping(node) != Type.NUMBER) {
			throw new RuntimeException("ERRO: Soma deve ser feita apenas com valores do tipo 'NUMBER'!");
		}
	}
	
	@Override
	public void outAAExpMenosAExp(AAExpMenosAExp node) {		
		if (inferTyping(node) != Type.NUMBER) {
			throw new RuntimeException("ERRO: Subtração deve ser feita apenas com valores do tipo 'NUMBER'!");
		}
	}
	
	@Override
	public void outAAExpMultAExp(AAExpMultAExp node) {		
		if (inferTyping(node) != Type.NUMBER) {
			throw new RuntimeException("ERRO: Multiplicação deve ser feita apenas com valores do tipo 'NUMBER'!");
		}
	}
	
	@Override
	public void outAAExpDivAExp(AAExpDivAExp node) {		
		if (inferTyping(node) != Type.NUMBER) {
			throw new RuntimeException("ERRO: Divisão deve ser feita apenas com valores do tipo 'NUMBER'!");
		}

		// ==================== VERIFICANDO DIVISÃO POR ZERO, CASO NÃO SEJA DINÂMICO =============================

		if (node.getEsq() instanceof AANumberValAExp) {
			String nodeLeft = node.getEsq().toString().strip();
			int divisor = Integer.parseInt(nodeLeft);
			if (divisor == 0) {
				throw new RuntimeException("ERRO: Não é possível dividir por zero!");
			}			
		}
	}
	
	@Override
	public void outAAExpDivIntAExp(AAExpDivIntAExp node) {
		if (inferTyping(node) != Type.NUMBER) {
			throw new RuntimeException("ERRO: Divisão Inteira deve ser feita apenas com valores do tipo 'NUMBER'!");
		}

		// ==================== VERIFICANDO DIVISÃO POR ZERO, CASO NÃO SEJA DINÂMICO =============================
		
		if (node.getEsq() instanceof AANumberValAExp) {
			String nodeLeft = node.getEsq().toString().strip();
			int divisor = Integer.parseInt(nodeLeft);
			if (divisor == 0) {
				throw new RuntimeException("ERRO: Não é possível dividir por zero!");
			}			
		}
	}
	
	@Override
	public void outAAExpNegativoAExp(AAExpNegativoAExp node) {
		if (inferTyping(node) != Type.NUMBER) {
			throw new RuntimeException("ERRO: Operador '-' deve anteceder apenas valores do tipo 'NUMBER'!");
		}
	}
	
	@Override
	public void outAAExpNotAExp(AAExpNotAExp node) {
		if (inferTyping(node) != Type.ANSWER) {
			throw new RuntimeException("ERRO: Operação de negação deve anteceder apenas valores do tipo 'answer'!");
		}
	}
	
	@Override
	public void outAAExpParentesesAExp(AAExpParentesesAExp node) {
		if (inferTyping(node) != Type.NUMBER) {
			throw new RuntimeException("ERRO: Operação de agrupamento deve conter expressão com resultado do tipo 'NUMBER'");
		}
	}

	@Override 
	public void outAAExpVarAExp(AAExpVarAExp node) {
		SymbolInfo info = symbolTable.find(node.toString());
		if (info == null) {
			// ====================== VARIÁVEL NÃO DECLARADA ===========================
			throw new RuntimeException("ERRO: A variável ou constante '" + node.toString() + "' não foi declarado!");
		}
		if (!info.getIsInitialized()) {
			// ====================== VARIÁVEL NÃO INICIALIZADA ===========================
			String mutable = info.getIsMutable()? "variável" : "constante";
			throw new RuntimeException("ERRO: A " + mutable + " " + node.toString() + " não foi inicializada!");
		}
	}
	
	// ======================================================================================================
	// ------------------------------------------- COMANDOS -------------------------------------------------
	// ======================================================================================================

	@Override
	public void outAAAtribConstAComando(AAAtribConstAComando node){
		String identificador = node.getId().toString();
		Type expType = inferTyping(node.getAExp());
		SymbolInfo info = this.symbolTable.find(identificador);
		
		if (info == null) {
			throw new RuntimeException("ERRO: A constante '" + identificador + "' não foi declarada!");
		}
		if (info.getIsMutable()) {
			throw new RuntimeException("ERRO: A variável '" + identificador + "' não pode receber atribuição dessa forma, use ':='!");
		}
		if (info.getIsInitialized() && !info.getIsMutable()) {
			throw new RuntimeException("ERRO: A constante '" + identificador + "' já foi inicializada!");
		}
		if (info.getType() != expType) {
			throw new RuntimeException("ERRO: A constante '" + identificador + "' é do tipo " + info.getType() + " e está recebendo um " + expType);
		}
		
		info.setIsInitialized(true);
	}
	
	@Override
	public void outAAAtrVarAComando(AAAtrVarAComando node){
		PAVar variavel = node.getAVar();
		SymbolInfo info = null;
		String key = null;
		Type expType = inferTyping(node.getAExp());
		
		if (variavel instanceof AAIdentificadorAVar) {
			
			key = ((AAIdentificadorAVar) variavel).getId().toString();
			info = this.symbolTable.find(key);
			if (info == null) {
				throw new RuntimeException("ERRO: A variável '" + key + "' não foi declarada!");
			}
			
		} else if (variavel instanceof AAAcessoVectorAVar) {
			
			AAAcessoVectorAVar acessoVetor = ((AAAcessoVectorAVar) variavel);
			key = acessoVetor.getId().toString();
			info = this.symbolTable.find(key);
			checkAcessoVector(acessoVetor, key);
			
		}
		
		if (!info.getIsMutable()) {
			throw new RuntimeException("ERRO: A constante '" + key + "' não pode receber atribuição dessa forma, use '='!");
		}
		if (info.getType() != expType) {
			throw new RuntimeException("ERRO: A variável '" + key + "' é do tipo " + info.getType() + " e está recebendo um " + expType);
		}
		
		info.setIsInitialized(true);
	}
	
	@Override
	public void outAACaptureAComando(AACaptureAComando node) {
		PAVar variavel = node.getAVar();
		String key = null;
		SymbolInfo info = null;
		
		if (variavel instanceof AAIdentificadorAVar) {
			key = ((AAIdentificadorAVar) variavel).getId().toString();
			info = this.symbolTable.find(key);
			if (info == null) {
				throw new RuntimeException("ERRO: A variável '" + key + "' não foi declarada!");
			}
		} else if (variavel instanceof AAAcessoVectorAVar) {
			key = ((AAAcessoVectorAVar) variavel).getId().toString();
			info = this.symbolTable.find(key);
			checkAcessoVector(((AAAcessoVectorAVar) variavel), key);
		}
		
		if (!info.getIsMutable()) {
			throw new RuntimeException("ERRO: A função 'capture' não pode receber uma constante como argumento!");
		}
	}
	
	@Override
	public void inAAJustInCaseThatAComando(AAJustInCaseThatAComando node) {
        Type expType = inferTyping(node.getAExp());
        
        if (expType != Type.ANSWER) {
        	throw new RuntimeException("ERRO: O resultado da expressão condicional no 'just in case' precisa ser do tipo 'ANSWER', mas ela é do tipo '" + expType + "'!");
        }
    }
	
	@Override
	public void inAAAsLongAComando(AAAsLongAComando node) {
		Type expType = inferTyping(node.getAExp());
        
        if (expType != Type.ANSWER) {
        	throw new RuntimeException("ERRO: O resultado da expressão condicional no 'as long as' precisa ser do tipo 'ANSWER', mas ela é do tipo '" + expType + "'!");
        }
	}
	
	@Override
	public void inAAShowAComando(AAShowAComando node) {
		Type expType = inferTyping(node.getAExp());
		
		if (expType == Type.UNKNOWN) {
			throw new RuntimeException("ERRO: A função 'show' recebe apenas valores de tipos primitivos");
		}
	}
	
	@Override
	public void inAAConsideringAComando(AAConsideringAComando node) {
		if (inferTyping(node.getAVar()) != Type.NUMBER) {
			throw new RuntimeException("ERRO: A variável da função 'considering' precisa ser do tipo 'NUMBER'");
		}
		if ((inferTyping(node.getExp1()) != Type.NUMBER) && (inferTyping(node.getExp2()) != Type.NUMBER) && (inferTyping(node.getExp3()) != Type.NUMBER)) {
			throw new RuntimeException("ERRO: A expressão da função 'considering' precisa ser do tipo 'NUMBER'");
		}
		
		PAVar variavel = node.getAVar();
		String key = null;
		SymbolInfo info = null;
		
		if (variavel instanceof AAIdentificadorAVar) {
			key = ((AAIdentificadorAVar) variavel).getId().toString();
			info = this.symbolTable.find(key);
			if (info == null) {
				throw new RuntimeException("ERRO: A variável '" + key + "' não foi declarada!");
			}
		} else if (variavel instanceof AAAcessoVectorAVar) {
			key = ((AAAcessoVectorAVar) variavel).getId().toString();
			info = this.symbolTable.find(key);
			checkAcessoVector(((AAAcessoVectorAVar) variavel), key);
		}
		
		if (!info.getIsMutable()) {
			throw new RuntimeException("ERRO: A função 'considering' não pode receber uma constante como argumento!");
		}
	}
	
	@Override
	public void inAABlocoAComando(AABlocoAComando node) {
        symbolTable.push();
    }

	@Override
    public void outAABlocoAComando(AABlocoAComando node) {
        symbolTable.pop();
    }
	
	// ======================================================================================================
	// --------------------------------------- MÉTODOS AUXILIARES -------------------------------------------
	// ======================================================================================================
	
	/**
	 * Infer the type of a declaration node
	 * @param node declaration node
	 * @return type of the declaration
	 */
	public Type inferTyping (Node node) {
        if (node instanceof AANumberValAExp || node instanceof AANumberADeclaracao) return Type.NUMBER;
        if (node instanceof AAAnswerValAExp || node instanceof AAAnswerADeclaracao) return Type.ANSWER;
        if (node instanceof AASymbolValAExp || node instanceof AASymbolADeclaracao) return Type.SYMBOL;
        if (node instanceof AAStringAExp) return Type.STRING;
		if (node instanceof AAExpVarAExp) {
			AAExpVarAExp var = (AAExpVarAExp) node;
			SymbolInfo info = symbolTable.find(var.toString());
			if (info != null) return info.getType();
		}
        
        // ================================== OPERAÇÕES BOOLEANAS ==================================
        
        if (node instanceof AAExpOrAExp) {
            AAExpOrAExp current = (AAExpOrAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.ANSWER);
        }
        
        if (node instanceof AAExpAndOrAExp) {
            AAExpAndOrAExp current = (AAExpAndOrAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.ANSWER);
        }
        
        if (node instanceof AAExpXorOpAExp) {
            AAExpXorOpAExp current = (AAExpXorOpAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.ANSWER);
        }
        
        // ================================== OPERAÇÕES NUMÉRICAS ==================================

        if (node instanceof AAExpSomaAExp) {
            AAExpSomaAExp current = (AAExpSomaAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }
        
        if (node instanceof AAExpMenosAExp) {
            AAExpMenosAExp current = (AAExpMenosAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }
        
        if (node instanceof AAExpMultAExp) {
            AAExpMultAExp current = (AAExpMultAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }
        
        if (node instanceof AAExpDivAExp) {
            AAExpDivAExp current = (AAExpDivAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }
        
        if (node instanceof AAExpDivIntAExp) {
            AAExpDivIntAExp current = (AAExpDivIntAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }
        
        // ================================== OPERAÇÕES DE COMPARAÇÃO ==================================
        
        if (node instanceof AAExpMaiorAExp) {
            AAExpMaiorAExp current = (AAExpMaiorAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }
        
        if (node instanceof AAExpMenorAExp) {
            AAExpMenorAExp current = (AAExpMenorAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }

		if (node instanceof AAExpMenIgualAExp) {
            AAExpMenIgualAExp current = (AAExpMenIgualAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }

		if (node instanceof AAExpMaiIgualAExp) {
            AAExpMaiIgualAExp current = (AAExpMaiIgualAExp) node;
            return inferTypingExpBin(current.getDir(), current.getEsq(), Type.NUMBER);
        }

		if (node instanceof AAExpIgualAExp) {
			AAExpIgualAExp current = (AAExpIgualAExp) node;
			Type typeCurrentRight = inferTyping(current.getDir());
        	Type typeCurrentLeft = inferTyping(current.getEsq());
			if (typeCurrentRight != Type.NUMBER && typeCurrentRight != Type.ANSWER) {
				throw new RuntimeException("ERRO: Esperado valor do tipo 'NUMBER' ou 'ANSWER' e encontrado " + typeCurrentRight + " para " + current.getDir().toString());
			}
			if (typeCurrentLeft != Type.NUMBER && typeCurrentLeft != Type.ANSWER) {
				throw new RuntimeException("ERRO: Esperado valor do tipo 'NUMBER' ou 'ANSWER' e encontrado " + typeCurrentLeft + " para " + current.getEsq().toString());
			}
			if (typeCurrentLeft != typeCurrentRight) {
				throw new RuntimeException("ERRO: Esperado tipos semelhantes e encontrado " + typeCurrentRight + " e " + typeCurrentLeft);
			}
			return typeCurrentRight;
		}

		if (node instanceof AAExpDifAExp) {
			AAExpDifAExp current = (AAExpDifAExp) node;
			Type typeCurrentRight = inferTyping(current.getDir());
        	Type typeCurrentLeft = inferTyping(current.getEsq());
			if (typeCurrentRight != Type.NUMBER && typeCurrentRight != Type.ANSWER) {
				throw new RuntimeException("ERRO: Esperado valor do tipo 'NUMBER' ou 'ANSWER' e encontrado " + typeCurrentRight + " para " + current.getDir().toString());
			}
			if (typeCurrentLeft != Type.NUMBER && typeCurrentLeft != Type.ANSWER) {
				throw new RuntimeException("ERRO: Esperado valor do tipo 'NUMBER' ou 'ANSWER' e encontrado " + typeCurrentLeft + " para " + current.getEsq().toString());
			}
			if (typeCurrentLeft != typeCurrentRight) {
				throw new RuntimeException("ERRO: Esperado tipos semelhantes e encontrado " + typeCurrentRight + " e " + typeCurrentLeft);
			}
			return typeCurrentRight;
		}

		// ================================== OPERAÇÕES UNÁRIAS ==================================

		if (node instanceof AAExpNegativoAExp){
			AAExpNegativoAExp current = (AAExpNegativoAExp) node;
			return inferTypingExpUna(current.getAExp(), Type.NUMBER);
		}

		if (node instanceof AAExpNotAExp){
			AAExpNotAExp current = (AAExpNotAExp) node;
			return inferTypingExpUna(current.getAExp(), Type.ANSWER);
		}

		// ================================== OPERAÇÕES DE AGRUPAMENTO ============================

		if (node instanceof AAExpParentesesAExp) {
        	AAExpParentesesAExp current = (AAExpParentesesAExp) node;
        	return inferTyping(current.getAExp());
        }

        return Type.UNKNOWN;
    }
	
	/**
	 * Infer the type of a binary expression
	 * @param right right expression
	 * @param left left expression
	 * @param type expected type
	 * @return inferred type
	 */
	public Type inferTypingExpBin (PAExp right, PAExp left, Type type) {
		Type typeCurrentRight = inferTyping(right);
        Type typeCurrentLeft = inferTyping(left);
        if (typeCurrentRight != type) {
            throw new RuntimeException("ERRO: Esperado valor do tipo " + type.toString() + " e encontrado " + typeCurrentRight + " para " + right.toString());
        }
        if (typeCurrentLeft != type) {
        	throw new RuntimeException("ERRO: Esperado valor do tipo " + type.toString() + " e encontrado " + typeCurrentLeft + " para " + left.toString());
        }
        return type;
	}

	/**
	 * Infer the type of a unary expression
	 * @param exp expression
	 * @param type expected type
	 * @return inferred type
	 */
	public Type inferTypingExpUna (PAExp exp, Type type) {
		Type typeCurrentExp = inferTyping(exp);
        if (typeCurrentExp != type) {
            throw new RuntimeException("ERRO: Esperado valor do tipo " + type.toString() + " e encontrado " + typeCurrentExp + " para " + exp.toString());
        }
        return type;
	}
	
	/**
	 * Verify if the accessed position of a vector/matrix is valid
	 * @param key vector/matrix identifier
	 * @param position accessed position
	 * @param idx dimension index (0 for vector, 1+ for matrix)
	 */
	public void verifyAccessVector (String key, int position, int idx) {
		if (position < 0) throw new RuntimeException("ERRO: O índice do vetor está fora do intervalo de valores declarados");
		
		SymbolInfo info = symbolTable.find(key);
		
		if (info == null) throw new RuntimeException("ERRO: O índice do vetor está fora do intervalo de valores declarados");
		
		int[] dimensions = info.getDimensions();
		
		if (position >= dimensions[idx]) throw new RuntimeException("ERRO: O índice do vetor está fora do intervalo de valores declarados");
	}
	
	/**
	 * Check the access to a vector/matrix
	 * @param node access node
	 * @param key vector/matrix identifier
	 */
	public void checkAcessoVector (AAAcessoVectorAVar node, String key) {
		SymbolInfo info = this.symbolTable.find(key);
		LinkedList<PAVar> matriz = node.getAVar();
		boolean isMatriz = matriz.size() > 0? true : false;
		
		// VERIFICANDO SE VETOR/MATRIZ FOI DECLARADO
		if (info == null) {
			String message = String.format("ERRO: $s não foi %s!", (isMatriz? "A matriz" : "O vetor"), key, (isMatriz? "a" : "o"));
			throw new RuntimeException(message);
		}
		
		PAExp expVetor = node.getAExp();
		Type expVetorType = inferTyping(expVetor);
		
		// VERIFICANDO SE O ÍNDICE PASSADO É NUMÉRICO
		if (expVetorType != Type.NUMBER) {
			throw new RuntimeException("ERRO: Os índices " + (isMatriz? "da matriz" : "do vetor") +" devem ser valores do tipo 'NUMBER'");
		}
		
		String expVetorString = expVetor.toString().strip();
		int expVetorInt = Integer.parseInt(expVetorString);
		
		// VERIFICANDO SE POSIÇÃO É VÁLIDA
		verifyAccessVector(key, expVetorInt, 0);
		
		// VERIFICANDO SE FOI PASSADO O MESMO NÚMERO DE DIMENSÕES DA MATRIZ
		if (matriz.size() != info.getDimensionsSize()) {
			String message = String.format("ERRO: A matriz %s usada tem %d índices, enquanto a matriz $s declarada tem %d índices", key, matriz.size(), key, info.getDimensionsSize());
			throw new RuntimeException(message);
		}
		
		// VERIFICA OS OUTROS ÍNDICES, CASO SEJA UMA MATRIZ
		if (matriz.size() > 0) {
			for (int i = 0; i < matriz.size(); i++) {
				PAExp expMatriz = ((AAVirgulaExpAVar) matriz.get(i)).getAExp();
				Type expMatrizType = inferTyping(expMatriz);
				
				if (expMatrizType != Type.NUMBER) {
					throw new RuntimeException("ERRO: Os índices da matriz devem ser valores do tipo 'NUMBER'");
				}
				
				String expMatrizString = expMatriz.toString().strip();
				int expMatrizInt = Integer.parseInt(expMatrizString);
				
				verifyAccessVector(key, expMatrizInt, i + 1);
			}
		}
	}
}
