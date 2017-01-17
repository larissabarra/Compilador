/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Larissa
 */
public class Lexer {

    public static int line = 1; //contador de linhas
    private char ch = ' '; //caractere lido do arquivo
    private InputStream is = null; 
    private InputStreamReader isr = null;
    private BufferedReader file;
    private Env tabelaSimbolos = new Env(null);
    private boolean espera = false;
    private boolean EOF;    
    
    //Método Construtor
    public Lexer(String fileName) throws FileNotFoundException {
        EOF = false;
        try {
            is = new FileInputStream(fileName);
            isr = new InputStreamReader(is);

            // create new buffered reader
            file = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado");
            throw e;
        }

        //Insere palavras reservadas na HashTable
        reserve(new Word("program", Tag.PROGRAM));
        reserve(new Word("int", Tag.INT));
        reserve(new Word("float", Tag.FLOAT));
        reserve(new Word("if", Tag.IF));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("repeat", Tag.REPEAT));
        reserve(new Word("until", Tag.UNTIL));
        reserve(new Word("scan", Tag.SCAN));
        reserve(new Word("print", Tag.PRINT));
        reserve(new Word("true", Tag.TRUE));
        reserve(new Word("false", Tag.FALSE));
    }
    
    /* Método para imprimir TS */
    public void imprimeTS() {
        tabelaSimbolos.imprime();
    }

    /* Método para inserir palavras reservadas na HashTable */
    private void reserve(Word w) {
        tabelaSimbolos.put(w, new Id(w.getLexeme(), w.tag)); //lexema é a chave para entrada na hashTable
    }

    /* Lê o próximo caractere do arquivo */
    private void readch() throws IOException {
        ch = (char) file.read();
    }

    /* Lê o próximo caractere do arquivo e verifica se é igual a c */
    private boolean readch(char c) throws IOException {
        readch();
        if (ch != c) {
            return false;
        }
        ch = ' ';
        return true;
    }

    public Token scan() throws IOException {
        /*
        Pra todos os casos que o caracter seguinte pode afetar o token, a posição do arquivo
        foi salva usando a função file.mark() logo antes de ler o próximo. Assim, se ele não
        completar o token esperado, é possível voltar 1 posição para que ele seja lido novamente
        quando a função for chamada de novo.
        */
        if(espera) {
            file.reset();
            espera = false;
        }
        //Desconsidera delimitadores na entrada
        for (;; readch()) {
            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b') {
                continue;
            } else if (ch == '\n') {
                line++; //conta linhas
            } else if (ch == '%') { // Comentário de linha única
                while (!readch('\n')) {
                }
                line++;
            } else if (ch == '/') { // Comentário de várias linhas
                if (readch('*')) {
                    do {
                        readch();
                        if (ch == '\n') {
                            line++;
                        } else if (ch == '*' && readch('/')) {
                            break;
                        } else if (ch == Character.MAX_VALUE) {
                            EOF = true;
                            System.out.println("Erro: comentário não foi fechado");
                            readch();
                            return new Token("ERROR", Tag.ERROR);
                        }
                    } while (true);
                } else {
                    //VOLTAR UMA POSIÇÃO
                    file.mark(10);
                    espera = true;
                    Token t = new Token("DIV", Tag.DIV);
                    return t;
                }
            } else {
                break;
            }
        }

        // Fim de arquivo
        if (ch == Character.MAX_VALUE) {
            EOF = true;
            //System.out.println("Análise terminada com sucesso!");
            return null;
        }

        switch (ch) {
            //Operadores
            case '&':
                if (readch('&')) {
                    return Word.and;
                } else {
                    return new Token("AND", '&');
                }
            case '|':
                if (readch('|')) {
                    return Word.or;
                } else {
                    return new Token("OR", '|');
                }
            case '=':
                if (readch('=')) {
                    return Word.eq;
                } else {
                    //VOLTAR UMA POSIÇÃO
                    file.mark(10);
                    espera = true;
                    return new Token("ATRIB", Tag.ATRIB);
                }
            case '<':
                if (readch('=')) {
                    return Word.le;
                } else {
                    //VOLTAR UMA POSIÇÃO
                    file.mark(10);
                    espera = true;
                    return new Token("LT", Tag.LT);
                }
            case '>':
                if (readch('=')) {
                    return Word.ge;
                } else {
                    //VOLTAR UMA POSIÇÃO
                    file.mark(10);
                    espera = true;
                    return new Token("GT", Tag.GT);
                }
            case '!':
                if (readch('>')) {
                    return Word.dif;
                } else {
                    //VOLTAR UMA POSIÇÃO
                    file.mark(10);
                    espera = true;
                    return new Token("NOT", Tag.NOT);
                }
        }

        //Numeros
        if (Character.isDigit(ch)) {
            boolean isZero = false, isFloat = false;
            if (ch == '0') {
                isZero = true;
            }
            int value = 0, resto = 0;
            float val = 0;
            do {
                value = 10 * value + Character.digit(ch, 10);
                if (isFloat) {
                    resto = 10 * resto + Character.digit(ch, 10);
                }
                readch();
                if (ch == '.') {
                    if (!isFloat) { //garantia que só vou achar 1 ponto 
                        isFloat = true;
                        val = value;
                        readch();
                    } else {
                        //ERRO: achei mais de 1 ponto
                        System.out.println("Erro na linha " + line + ": float mal formatado");
                        readch();
                        return new Token("ERROR", Tag.ERROR);
                    }
                } else if(Character.isAlphabetic(ch)) { //se for uma letra vai ser tentativa de id
                    System.out.println("Erro na linha " + line + ": identificador mal formatado");
                    readch();
                    return new Token("ERROR", Tag.ERROR);
                }
            } while (Character.isDigit(ch));
            if (isFloat) {
                val += (resto / Math.pow(10, (("" + resto).length())));
                tabelaSimbolos.put(new NumFloat(val), new Id(val, Tag.FLOAT_NUM));
                return new NumFloat(val);
            } else {
                if (isZero) {
                    tabelaSimbolos.put(new NumInt(0), new Id(0, Tag.INT_NUM));
                    return new NumInt(0); //só retorno 0 se for int, porque o float pode começar com 0
                }
                tabelaSimbolos.put(new NumInt(value), new Id(value, Tag.INT_NUM));
                return new NumInt(value);
            }
        }  //Literal
        else if ((ch) == '"') {
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(ch);
                readch();
                if(ch == '\n') {
                    System.out.println("Erro na linha " + line + ": literal mal formatado");
                    readch();
                    return new Token("ERROR", Tag.ERROR);
                }
            } while ((ch) != '"');
            sb.append('"');
            ch = ' ';
            String s = sb.toString();
            Word w = new Word(s, Tag.LITERAL);
            Id id = (Id) tabelaSimbolos.get(w);
            if (id != null) {
                return w;
            }
            tabelaSimbolos.put(w, new Id(s, Tag.LITERAL));
            return w;
        } //Identificadores
        else if (Character.isLetter(ch)) {
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(ch);
                readch();
            } while (Character.isLetterOrDigit(ch));
            String s = sb.toString();

            // Verifica a existência da palavra na tabela de símbolos
            Word w = (Word) tabelaSimbolos.getKey(s);
            
            if(w != null){
                return w; //palavra já existe na HashTable
            }

            w = new Word(s, "ID", Tag.ID);
            
            tabelaSimbolos.put(w, new Id(s, Tag.ID));
            return w;
        } //outros caracteres reconhecidos
        else {// if(ch == ':' || ch == ';' || ch == ',' || ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '*' || ch == '+' || ch == '-') {
            
            Token t = null;
            
            switch(ch){
                case ':':
                    t = new Token("DOIS_PONTOS", (int) ch);
                    break;
                case ';':
                    t = new Token("PONTO_VIRGULA", (int) ch);
                    break;
                case ',':
                    t = new Token("VIRGULA", (int) ch);
                    break;
                case '(':
                    t = new Token("AP", (int) ch);
                    break;
                case ')':
                    t = new Token("FP", (int) ch);
                    break;
                case '{':
                    t = new Token("AC", (int) ch);
                    break;
                case '}':
                    t = new Token("FC", (int) ch);
                    break;
                case '*':
                    t = new Token("MULT", (int) ch);
                    break;
                case '+':
                    t = new Token("ADD", (int) ch);
                    break;
                case '-':
                    t = new Token("SUB", (int) ch);
                    break;
            }
          
            if(t != null){
                ch = ' ';
                return t;
            }
        }

        //Caracteres não especificados
        System.out.println("Erro na linha " + line + ": símbolo não identificado ("+ ch + ")");
        readch();
        return new Token("ERROR", Tag.ERROR);
    }
    
    public boolean isEOF(){
        return EOF;
    }
}