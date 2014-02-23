import java.util.ArrayList;

/**
Logica - Projeto 2 - 2013.2
jrmo@cin.ufpe.br
**/

public class Resolucao {
	public static void main (String[] args) {
		Arquivo arq = new Arquivo("Expressoes.in", "Expressoes.outt");
		String exp;
		int casos = arq.readInt();
		int numCaso = 1;
		
		while (!arq.isEndOfFile()) {			
			for (int i = 0; i < casos; i++) {	//para cada caso (expressao)
				exp = arq.readString();
				
				/** separando as clausulas da expressao **/
				ArrayList<No> clausulas = separarClausulas(exp);
				
				/** imprimindo **/
				arq.print("caso #" + numCaso + ": ");
				
				/** verificando **/
				if (!ehFNC(clausulas)) {
					arq.print("nao esta na FNC");
				} else {
					if (!allHorn(clausulas)) {
						arq.print("nem todas as clausulas sao de horn");
					} else {						
						ArrayList<String> var = getVariaveis(exp);	/** pega vari�veis da express�o **/
						if (insatisfativel(var, clausulas)) {
							arq.print("insatisfativel");
						} else {
							arq.print("satisfativel");
						}
					}
				}
				arq.println();
				numCaso++;
			}	//fim for casos
		}	//fim arquivo
		arq.close();
	}	//fim main
	
	public static void prePosfixaClausulas(ArrayList<No> c) {	//juntando os literais da clausula com parenteses
		for (int i = 0; i < c.size(); i++) {
			ArrayList<String> lit = separarLiterais(c.get(i));
			String newi = null;
			String s = null;
			for (int j = 0; j < lit.size(); j++) {
				s = lit.get(j);
				if (newi == null) {
					newi = s;
				} else {
					newi = "(" + newi + "+" + s + ")";
				}
			}
			
			No novo = new No(newi);
			c.set(i, novo);
		}
	}
	
	public static String prePosfixaExpressao(ArrayList<No> c) {		//juntando as clausulas com parenteses
		prePosfixaClausulas(c);
		String exp = null;
		String s = null;
		for (int i = 0; i < c.size(); i++) {
			s = c.get(i).exp;
			
			if (exp == null) {
				exp = s;
			} else {
				exp = "(" + exp + "." + s + ")";
			}
		}
		return exp;
	}
	
	public static String posfixa(String exp) {
		Pilha p = new Pilha();
		String posfixa = "";

		for (int j = 0; j < exp.length(); j++) {
			if (ehVariavel(exp.substring(j, j+1))) {
				posfixa = posfixa + exp.charAt(j);
			}
			if (ehOperador(exp.substring(j, j+1))) {
				p.push(exp.substring(j,j+1));
			}
			if (exp.charAt(j) == ')') {
				posfixa = posfixa + p.getTop();
				p.pop();
			}
		}
		return posfixa;
	}
	
	public static String allPosfixa(ArrayList<No> c) {
		String pos = posfixa(prePosfixaExpressao(c));
		return pos;
	}
	
	public static boolean insatisfativel(ArrayList<String> var, ArrayList<No> clausulas) {
		String exp = allPosfixa(clausulas);
		
		char tabVar[][] = tabVar(var.size());
		int linhas = potencia(2, var.size());
		char valor;
		String result[][] = new String[linhas][1];
		
		for (int i = 0; i < linhas; i++) {		//colocando valoracoes
			String expc = exp;
			
			if (expc.contains("a")) {
				int a = var.indexOf("a");
				valor = tabVar[i][a];
				expc = expc.replace('a', valor);
			}
			if (expc.contains("b")) {
				int b = var.indexOf("b");
				valor = tabVar[i][b];
				expc = expc.replace('b', valor);
			}
			if (expc.contains("c")) {
				int c = var.indexOf("c");
				valor = tabVar[i][c];
				expc = expc.replace('c', valor);
			}
			if (expc.contains("d")) {
				int d = var.indexOf("d");
				valor = tabVar[i][d];
				expc = expc.replace('d', valor);
			}
			
			String resultado = calcula(expc);
			result[i][0] = resultado;
		}
		
		ArrayList<String> expr = new ArrayList<String>();
		for (int i = 0; i < linhas; i++) {
			expr.add(result[i][0]);				//adicionando os valores para verificar
		}
		if (!expr.contains("1")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String calcula(String exp) {
		Pilha resultado = new Pilha();
		for (int i = 0; i < exp.length(); i++) {
			if (operando(exp.charAt(i))) {
				resultado.push(exp.substring(i, i+1));
			} else if (exp.charAt(i) == '-') {
				String op = resultado.pop();
				resultado.push(calcula1(op));
			} else {
				String op2 = resultado.pop();
				String op1 = resultado.pop();
				resultado.push(calcula2(op1, op2, exp.substring(i, i+1)));
			}
		}
		return resultado.getTop();
		
	}
	
	public static String calcula2(String op1, String op2, String operador) {
		String resultado = null;
		if (operador.equals("+")) {
			resultado = ou(op1, op2);
		}
		if (operador.equals(".")) {
			resultado = e(op1, op2);
		}
		return resultado;
	}

	public static String calcula1(String op) {
		return nega(op);
	}

	public static boolean operando(char c) {
		if (c == '0' || c == '1') {
			return true;
		} else {
			return false;
		}
	}
	
	public static String nega(String op) {
		if (op.equals("0")) {
			return "1";
		} else {
			return "0";
		}
	}

	public static String ou(String op1, String op2) { //soma
		if (op1.equals("0") && op2.equals("0")) {
			return "0";
		} else {
			return "1";
		}
	}

	public static String e(String op1, String op2) { //multiplicacao
		if (op1.equals("1") && op2.equals("1")) {
			return "1";
		} else {
			return "0";
		}
	}
	
	public static char[][] tabVar(int colunas) {
		int linhas = potencia(2, colunas);					
		char tab[][] = new char[linhas][colunas];
		
		for (int i = 0; i < tab.length; i++) {
			for (int j = 0; j < tab[0].length; j++) {
				tab[i][j] = '0';
			}
		}
		
		for (int i = linhas/2; i < linhas; i++) { tab[i][0] = '1'; }
		
		if (colunas > 1) {
			for (int i = linhas/4; i < linhas/2; i++) { tab[i][1] = '1'; }
			for (int i = (linhas/4)*3; i < linhas; i++) { tab[i][1] = '1'; }
		}
		if (colunas == 3) {
			for (int i = 0; i < linhas; i++) {
				if (i%2 != 0) { tab[i][2] = '1'; }
			}
		}
		if (colunas == 4) {
			int ini = 14;

			for (int n = 0; n < 4; n++) {
				for (int i = linhas-ini; i < linhas-(ini-2); i++) { tab[i][2] = '1'; }
				ini = ini - 4;
			}
			for (int i = 0; i < linhas; i++) {
				if (i%2 != 0) { tab[i][3] = '1'; }
			}		
		}
		
		return tab;
	}
	
	public static ArrayList<String> getVariaveis (String exp) {
		ArrayList<String> var = new ArrayList<String>();
		
		for (int j = 0; j < exp.length(); j++) {
			if (exp.charAt(j) == 'a' && !var.contains("a")) {
				var.add(0, "a");
			}
			if (exp.charAt(j) == 'b' && !var.contains("b")) {
				var.add("b");
			}
			if (exp.charAt(j) == 'c' && !var.contains("c")) {
				var.add("c");
			}
			if (exp.charAt(j) == 'd' && !var.contains("d")) {
				var.add("d");
			}
		}
		return var;
	}

	public static int potencia(int base, int expoente) {
		if (expoente == 0) {
			return 1;
		} else {
			int resp = 1;
			for (int i = 0; i < expoente; i++) {
				resp = resp * base;
			}
			return resp;
		}
	}
	
	public static ArrayList<String> separarLiterais(No c) { 	//recebe o No (String) com a clausula para separar
		ArrayList<String> atom = new ArrayList<String>();
		
		for (int j = 0; j < c.exp.length(); j++) {	//para cada caractere da clausula
			String aux = c.exp.substring(j, j+1);
			if (aux.equals("-")) {
				atom.add(aux);
			}
			if (ehVariavel(aux)) {
				if (!atom.isEmpty() && atom.get(atom.size()-1).equals("-")) {
					atom.set(atom.size()-1, "(" + atom.get(atom.size()-1) + aux + ")");
				} else {
					atom.add(aux);
				}
			}
		}	
		return atom;
	}
	
	public static ArrayList<No> separarClausulas (String exp) {		//separa todas as clausulas da expressao
		ArrayList<No> clausulas = new ArrayList<No>();
		No primeiro = new No("(");
		primeiro.inc_pOpen();
		clausulas.add(primeiro);
		for (int i = 1; i < exp.length(); i++) {
			int index = clausulas.size()-1;
			if (clausulas.get(index).pOpen == clausulas.get(index).pClose && exp.charAt(i) == '.'){
				clausulas.add(new No(""));
				index = clausulas.size()-1;
			} else {
				if (exp.charAt(i) == '(') {
					clausulas.get(index).inc_pOpen();
				}
				if (exp.charAt(i) == ')') {
					clausulas.get(index).inc_pClose();
				}
				clausulas.get(index).exp = clausulas.get(index).exp + exp.charAt(i); 
			}
		}
		return clausulas;
	}
	
	public static boolean ehFNC(ArrayList<No> c) {
		boolean b = false;
		int countop = 0;
		int countparent = 0;
		for (int i = 0; i < c.size(); i++) {
			if (c.get(i).exp.contains(".") || c.get(i).exp.contains(">")) {
				countop = countop + 1;
			}
			if (c.get(i).pOpen > 1) {
				countparent = countparent + 1;
			}
		}
		if (countop == 0 && countparent == 0) {
			b = true;
		}
		return b;
	}
	
	public static boolean allHorn(ArrayList<No> c) {
		int count = 0;
		for (int i = 0; i < c.size(); i++){
			if (ehHorn(c.get(i).exp)) {
				count++;
			}
		}
		if (count == c.size()) {
			return true;
		} else {
			return false;
		}	
	}
	
	public static boolean ehHorn(String exp) {
		Pilha p = new Pilha();
		ArrayList<String> h = new ArrayList<String>();
		int count = 0;	//contador
		for (int i = exp.length()-1; i >= 0; i--) {		//colocando cada caractere na pilha
			p.push(exp.substring(i, i+1));
		}
		while (!p.pilha.isEmpty()) {		//separando os literais
			if (ehVariavel(p.getTop())) {
				if (!h.isEmpty() && h.get(h.size()-1).equals("-")) {
					String concat = h.get(h.size()-1) + p.getTop();
					h.set(h.size()-1, concat);
				} else {
					h.add(p.getTop());
				}
			}
			if (p.getTop().equals("-")) {
				h.add(p.getTop());
			}
			p.pop();
		}
		for (int i = 0; i < h.size(); i++) {
			if (!h.get(i).contains("-")) {
				count++;
			}
		}
		if (count <= 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean ehOperador(String c) {
		return c.equals("+") || c.equals(".") || c.equals(">") || c.equals("-");
	}
	
	public static boolean ehVariavel(String c) {
		return c.equals("a") || c.equals("b") || c.equals("c") || c.equals("d");
	}
		
	public static class No {
		private String exp;
		private int pOpen;		//numero de parentesis de abertura
		private int pClose;		//numero de parentesis de fechamento

		public No (String exp) {
			this.exp = exp;
			this.pOpen = 0;
			this.pClose = 0;
		}

		public void concatExp(String exp2) {
			this.exp = this.exp + exp2;
		}

		public void inc_pOpen() {
			this.pOpen++;
		}

		public void inc_pClose() {
			this.pClose++;
		}
	}

	public static class Pilha {
		private ArrayList<String> pilha;

		public Pilha() {
			this.pilha = new ArrayList<String>();
		}
		
		public int tamanho() {
			return this.pilha.size();
		}

		public void push(String c) {
			this.pilha.add(0, c);		//coloca no comeco da pilha
		}

		public String pop() {
			return this.pilha.remove(0);
		}

		public String getTop() {
			return this.pilha.get(0);
		}
	}
	
}
