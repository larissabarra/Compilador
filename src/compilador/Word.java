/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/**
 *
 * @author Larissa
 */
public class Word extends Token{
    private String lexeme = "";
    
    public static final Word and = new Word("&&", "AND", Tag.AND);
    public static final Word or = new Word("||", "OR", Tag.OR);
    public static final Word eq = new Word("==", "EQ", Tag.EQ);
    public static final Word ne = new Word("<>", "NE", Tag.NE);
    public static final Word le = new Word("<=", "LE", Tag.LE);
    public static final Word ge = new Word(">=", "GE", Tag.GE);
    public static final Word dif = new Word("!>", "DIF", Tag.DIF);

    // Construtor adicionado para adicionar símbolos e identificadores
    public Word(String s, String nome, int tag) {
        super(nome, tag);
        lexeme = s;
    }
    
    // Construtor utilizado para adicionar palavras chaves
    // O nome do token das palavras chave será a própria palavra
    public Word(String s, int tag) {
        super(s, tag);
        lexeme = s;
    }
    
    @Override
    public String toString() {
        if(nome.equals("ID"))
            return "<" + nome + ", " + lexeme + ">";
        else
            return "<" + lexeme + ">";
    }
    
    public String getLexeme(){
        return lexeme;
    }
    
}
