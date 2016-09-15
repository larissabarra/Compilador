/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 *
 * @author Larissa
 */
public class Lexer {
    public static int line = 1; //contador de linhas
    private char ch = ' '; //caractere lido do arquivo
    private FileReader file;
    private Hashtable words = new Hashtable();

    //Método Construtor
    public Lexer(String fileName) throws FileNotFoundException {
        try {
            file = new FileReader(fileName);
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
    
    /*Método para inserir palavras reservadas na HashTable */
    private void reserve(Word w) {
        words.put(w.getLexeme(), w); // lexema é a chave para entrada na hashTable
    }
    
    /*Lê o próximo caractere do arquivo*/
    private void readch() throws IOException {
        ch = (char) file.read();
    }
    
    /* Lê o próximo caractere do arquivo e verifica se é igual a c*/
    private boolean readch(char c) throws IOException {
        readch();
        if (ch != c) {
            return false;
        }
        ch = ' ';
        return true;
    }
    
    public Token scan() throws IOException {
    //Desconsidera delimitadores na entrada
        for (;; readch()) {
            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b') {
                continue;
            } else if (ch == '\n') {
                line++; //conta linhas
            } else {
                break;
            }
        }
        
        switch (ch) {
        //Operadores
            case '&':
                if (readch('&')) {
                    return Word.and;
                } else {
                    return new Token('&');
                }
            case '|':
                if (readch('|')) {
                    return Word.or;
                } else {
                    return new Token('|');
                }
            case '=':
                if (readch('=')) {
                    return Word.eq;
                } else {
                    return new Token('=');
                }
            case '<':
                if (readch('=')) {
                    return Word.le;
                } else {
                    return new Token('<');
                }
            case '>':
                if (readch('=')) {
                    return Word.ge;
                } else {
                    return new Token('>');
                }
            case '!':
                if (readch('>')) {
                    return Word.ng;
                } else {
                    return new Token('!');
                }
        }
        
        //Numeros
        if (Character.isDigit(ch)) {
            int value = 0;
            do {
                value = 10 * value + Character.digit(ch, 10);
                readch();
            } while (Character.isDigit(ch));
            return new NumInt(value);
        }
        
        //Identificadores
        if (Character.isLetter(ch)) {
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(ch);
                readch();
            } while (Character.isLetterOrDigit(ch));
            String s = sb.toString();
            Word w = (Word) words.get(s);
            if (w != null) {
                return w; //palavra já existe na HashTable
            }
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }
        
        //Literal
        if ((ch) == '"'){
            StringBuffer sb = new StringBuffer();
            do{
                sb.append(ch);
                readch();
            } while((ch) != '"' || (ch) != '\t');
            String s = sb.toString();
            Word w = (Word) words.get(s);
            if (w != null){
                return w;
            }
            w = new Word(s, Tag.LITERAL);
            words.put(s, w);
            return w;
        }
        
        //Caracteres não especificados
        Token t = new Token(ch);
        ch = ' ';
        return t;
    }
}
