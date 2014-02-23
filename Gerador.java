import java.util.ArrayList;

/**
Logica - Projeto 1 - 2013.2 v2.0
jrmo@cin.ufpe.br
**/

public class Gerador {
	public static void main (String[] args) {
	
	  /** classe Arquivo: http://moreno.cin.ufpe.br/~if672cc/shared/Arquivo.java **/
		Arquivo arq = new Arquivo("Expressoes.in", "Expressoes.out");
		String exp;
		int casos = arq.readInt();
		int numTabela = 1;

		while (!arq.isEndOfFile()) {

			for (int i = 0; i < casos; i++) {	//para cada caso (expressao)
				exp = arq.readString();
				
				if (!exp.contains("x") && !exp.contains("y") && !exp.contains("z") && !exp.contains("t")) {		//se a expressao nao tiver 0s e/ou 1s
					arq.println("Tabela #" + numTabela);
					arq.println();							//linha em branco
				} else {
					ArrayList<No> sub = new ArrayList<No>();				//ArrayList de subexpressoes
					ArrayList<String> var = new ArrayList<String>();		//ArrayList de variaveis
					
					/** manipulando as variaveis **/
					for (int j = 0; j < exp.length(); j++) {				//verifica cara char da expressao para saber quantos literais existem
						if (exp.charAt(j) == 'x' && !var.contains("x")) {			//compara o char com x
							var.add(0, "x");
						}
						if (exp.charAt(j) == 'y' && !var.contains("y")) {			//compara o char com y
							var.add("y");
						}
						if (exp.charAt(j) == 'z' && !var.contains("z")) {			//compara o char com z
							var.add("z");
						}
						if (exp.charAt(j) == 't' && !var.contains("t")) {			//compara o char com t
							var.add("t");
						}
					}
					
					int varexp = var.size();	//guardando a quantidade de literais da expressao

					/** identificando as subexpressoes **/
					for (int j = 0; j < exp.length(); j++) {
						String s = exp.substring(j, j+1);		//verifica cada caractere da expressao

						if (sub.size() == 0) {
							No no = new No(s);
							if (s.equals("(")) { no.inc_pOpen(); }
							sub.add(no);

						} else {
							if (s.equals("(")){
								for (int k = 0; k < sub.size(); k++) {
									if (sub.get(k).pOpen > sub.get(k).pClose) {
										sub.get(k).concatExp(s);
										sub.get(k).inc_pOpen();
									}
								}
								No no = new No(s);
								no.inc_pOpen();
								sub.add(no);
							}

							if (s.equals(")")) {
								for (int k = 0; k < sub.size(); k++) {
									if (sub.get(k).pOpen > sub.get(k).pClose) {
										sub.get(k).concatExp(s);
										sub.get(k).inc_pClose();
									}
								}
							}

							if (!s.equals(")") && !s.equals("(")) {
								for (int k = 0; k < sub.size(); k++) {
									if (sub.get(k).pOpen > sub.get(k).pClose) {
										sub.get(k).concatExp(s);
									}
								}
							}
						}
					}
					
					for (int j = 0; j < sub.size(); j++) {				//retirando subexpressoes iguais do ArrayList de subexpressoes, se houver
						for (int k = j+1; k < sub.size(); k++) {
							if (sub.get(k).exp.contentEquals(sub.get(j).exp) ) {
								sub.remove(sub.get(j));
							}
						}
					}
					
					for (int j = 0; j < sub.size(); j++) {				//retirando subexpressoes que so tem 0s e 1s
						if (!sub.get(j).exp.contains("x") && !sub.get(j).exp.contains("y") 
								&& !sub.get(j).exp.contains("z") && !sub.get(j).exp.contains("t")){
							sub.remove(sub.get(j));
						}
					}
					
					/** gerando tabela verdade das variaveis **/
					int linhas = calcPotencia(2, varexp); 				//calcula o numero de linhas da tabela verdade das variaveis
					int hifensvar = (2*varexp)+1;						//quantidade de hifens que serao impressos para as variaveis
					char tabVar[][] = new char[linhas][varexp];			//instanciando a tabela para as combinacoes das variaveis
					tabVariaveis(linhas, varexp, tabVar);				//colocando os valores de 0 e 1 na tabela verdade das variaveis
					
					/** gerando tabela verdade de subexpressoes **/		//mesma quantidade de linhas da tabela de variaveis
					int subcolunas = sub.size();						//quantidade de colunas da tabela de subexpressoes
					char tabExp[][] = new char[linhas][subcolunas];		//instanciando tabela para os valores das subexpressoes

					No arraysub[] = sub.toArray(new No[0]);				//colocando as subexpressoes da lista em um array
					ordena(arraysub);									//ordenando o array de subexpressoes (algoritmo insertion sort modificado)
					
					int hifenssub = hifensSub(arraysub);				//quantidade de hifens que serao impressos para as subexpressoes

					/** colocando subexpressoes em notacao posfixa para poder fazer os calculos **/
					String arrayposfixa[] = new String[arraysub.length];	//array para subexpessoes posfixadas

					for (int k = 0; k < arrayposfixa.length; k++) {			//pegando cada subexpressao do arraysub...
						String subpos = posfixa(arraysub[k].exp);			//...transformando em posfixa...
						arrayposfixa[k] = subpos;							//...e colocando no arrayposfixa
					}

					char valor;
					String valorj[] = new String[1];
					
					for (int j = 0; j < linhas; j++) {							//atribuindo as valoracoes as expressoes posfixadas
						for (int k = 0; k < arrayposfixa.length; k++) {			//para cada subexpressao posfixada
							valorj[0] = arrayposfixa[k];
							
							if (arrayposfixa[k].contains("x")) {
								int x = var.indexOf("x");
								valor = tabVar[j][x];
								valorj[0] = valorj[0].replace('x', valor);
							}
							if (arrayposfixa[k].contains("y")) {
								int y = var.indexOf("y");
								valor = tabVar[j][y];
								valorj[0] = valorj[0].replace('y', valor);
							}
							if (arrayposfixa[k].contains("z")) {
								int z = var.indexOf("z");
								valor = tabVar[j][z];
								valorj[0] = valorj[0].replace('z', valor);
							}
							if (arrayposfixa[k].contains("t")) {
								int t = var.indexOf("t");
								valor = tabVar[j][t];
								valorj[0] = valorj[0].replace('t', valor);
							}
							
							char resultado = valorExp(valorj[0]);		//calculando cada posicao de tabela de subexpressoes
							tabExp[j][k] = resultado;
						}
					}
					
					/** imprimindo tabela completa **/
					arq.println("Tabela #" + numTabela);

					printHifens(hifensvar, hifenssub, arq);

					//1) imprimindo variaveis
					if (var.contains("x")) { arq.print("|" + var.get(var.indexOf("x"))); }
					if (var.contains("y")) { arq.print("|" + var.get(var.indexOf("y"))); }
					if (var.contains("z")) { arq.print("|" + var.get(var.indexOf("z"))); }
					if (var.contains("t")) { arq.print("|" + var.get(var.indexOf("t"))); }
					arq.print("|");

					//2) imprimindo subexpressoes
					for (int j = 0; j < arraysub.length; j++) {
						arq.print(arraysub[j].exp);
						arq.print("|");
					}
					arq.println();

					printHifens(hifensvar, hifenssub, arq);

					//3) imprimindo valores
					for(int linha = 0; linha < tabVar.length; linha++) {
						for (int coluna = 0; coluna < tabVar[linha].length; coluna++) {
							arq.print("|" + tabVar[linha][coluna]);
						}
						arq.print("|");

						for (int coluna = 0; coluna < tabExp[linha].length; coluna++) {
							int spaces = arraysub[coluna].exp.length()-1;
							for (int s = 0; s < spaces; s++) {
								arq.print(" ");
							}
							arq.print(tabExp[linha][coluna]+ "|");
						}
						arq.println();

						printHifens(hifensvar, hifenssub, arq);
					}

					/** verificando satisfativel/insatisfativel e refutavel/tautologia**/
					ArrayList<Character> expr = new ArrayList<Character>();		//arraylist que guarda os valores da ultima coluna da tabela de expressoes
					for (int j = 0; j < linhas; j++) {
						expr.add(tabExp[j][tabExp[0].length-1]);				//adicionando os valores para verificar
					}

					if (expr.contains('1')) {			//verificando para imprimir
						arq.print("satisfativel");
					}
					if (!expr.contains('1')) {
						arq.print("insatisfativel");
					}
					arq.print(" e ");
					if (expr.contains('0')) {
						arq.println("refutavel");
					}
					if (!expr.contains('0')) {
						arq.println("tautologia");
					}
					arq.println();

					sub.removeAll(sub);					
				} //fim else
				numTabela++;
			} //for casos
		}
		arq.close();
	} //fim main
	

	public static void printHifens(int hvar, int hsub, Arquivo arq) {		//metodo para imprimir a quantidade certa de hifens para cada caso
		for (int l = 0; l < hvar; l++) { arq.print("-"); }
		for (int l = 0; l < hsub; l++) { arq.print("-"); }
		arq.println();
	}
	

	public static void ordena(No array[]) {		//insertion sort
		int menor;
		No aux;

		for (int i = 0; i < array.length-1; i++) {
			menor = i;

			for (int j = i+1; j < array.length; j++) {
				if (array[j].exp.length() < array[menor].exp.length()) {
					menor = j;
				}
				if (array[j].exp.length() == array[menor].exp.length()) {
					if (array[j].exp.contains("x") && (array[menor].exp.contains("y") ||
							array[menor].exp.contains("z") || array[menor].exp.contains("t"))) {
						menor = j;
					}
					if (array[j].exp.contains("y") && (array[menor].exp.contains("z") || array[menor].exp.contains("t"))) {
						menor = j;
					}
					if (array[j].exp.contains("z") && array[menor].exp.contains("t")) {
						menor = j;
					}
				}
			}
			if (menor != i) {
				aux = array[menor];
				array[menor] = array[i];
				array[i] = aux;
			}
		}
	}
	

	public static void tabVariaveis(int linhas, int colunas, char tab[][]) {
		for (int i = 0; i < tab.length; i++) {
			for (int j = 0; j < tab[0].length; j++) {
				tab[i][j] = '0';
			}
		}

		for (int i = linhas/2; i < linhas; i++) { tab[i][0] = '1'; }		//sempre coloca um na metade final da primeira coluna

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
	}
	

	public static int calcPotencia (int base, int expoente) {
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
	

	public static int hifensSub(No a[]) {
		int h = 0;
		for (int i = 0; i < a.length; i++) {	//soma do tamanho de cada subexpressao
			h = h + a[i].exp.length();
		}
		h = h + a.length;						//soma com a quantidade de subexpressoes (para os "|")
		return h;
	}


	public static char nega(char op) {
		if (op == '0') {
			return '1';
		} else {
			return '0';
		}
	}


	public static char implica(char op1, char op2) {
		if (op1 == '1' && op2 == '0') {
			return '0';
		} else {
			return '1';
		}
	}


	public static char ou(char op1, char op2) {
		if (op1 == '0' && op2 == '0') {
			return '0';
		} else {
			return '1';
		}
	}


	public static char e(char op1, char op2) {
		if (op1 == '1' && op2 == '1') {
			return '1';
		} else {
			return '0';
		}
	}
	
	
	public static String posfixa(String exp) {
		Pilha p = new Pilha();
		String posfixa = "";

		for (int j = 0; j < exp.length(); j++) {
			if (exp.charAt(j) == 'x' || exp.charAt(j) == 'y' || exp.charAt(j) == 'z' 
					|| exp.charAt(j) == 't' || exp.charAt(j) == '0' || exp.charAt(j) == '1') {
				posfixa = posfixa + exp.charAt(j);
			}
			if (exp.charAt(j) == '+' || exp.charAt(j) == '.' || exp.charAt(j) == '>' || exp.charAt(j) == '-') {
				p.push(exp.charAt(j));
			}
			if (exp.charAt(j) == ')') {
				posfixa = posfixa + p.getTop();
				p.pop();
			}
		}
		return posfixa;
	}
	

	public static char valorExp(String exp) {
		Pilha resultado = new Pilha();
		for (int i = 0; i < exp.length(); i++) {
			if (operando(exp.charAt(i))) {
				resultado.push(exp.charAt(i));			//se for operando (0 ou 1), empilha
			} else if (exp.charAt(i) == '-') {			//se for operador de negacao...
				char op = resultado.pop();				//...desempilha o ultimo valor empilhado...
				resultado.push(calcula1(op));			//...calcula com o valor desempilhado e empilha o resultado
			} else {									//se for outro operador (+, . ou >)
				char op2 = resultado.pop();				//desempilha os dois ultimos valores empilhados
				char op1 = resultado.pop();
				resultado.push(calcula2(op1, op2, exp.charAt(i)));	//calcula com os valores desempilhados e empilha o resultado
			}
		}
		return resultado.getTop();		//retorna o topo, que eh onde esta o resultado de toda a operacao
	}

	public static char calcula2(char op1, char op2, char operador) {
		char resultado = 0;
		if (operador == '+') {
			resultado = ou(op1, op2);
		}
		if (operador == '.') {
			resultado = e(op1, op2);
		}
		if (operador == '>') {
			resultado = implica(op1, op2);
		}
		return resultado;
	}

	public static char calcula1(char op) {
		return nega(op);
	}

	public static boolean operando(char c) {
		if (c == '0' || c == '1') {
			return true;
		} else {
			return false;
		}
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
		private ArrayList<Character> pilha;

		public Pilha() {
			this.pilha = new ArrayList<Character>();
		}

		public void push(char c) {
			this.pilha.add(0, c);		//coloca no comeco para o ArrayList poder funcionar como pilha
		}

		public char pop() {
			return this.pilha.remove(0);	//remove do comeco
		}

		public char getTop() {
			return this.pilha.get(0);
		}
	}
}


