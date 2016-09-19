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
    
    // Quando isId for identificador, isId receberá valor true
    private boolean isId;
    
    public static final Word and = new Word("&&", "AND", Tag.AND, false);
    public static final Word or = new Word("||", "OR", Tag.OR, false);
    public static final Word eq = new Word("==", "EQ", Tag.EQ, false);
    public static final Word ne = new Word("<>", "NE", Tag.NE, false);
    public static final Word le = new Word("<=", "LE", Tag.LE, false);
    public static final Word ge = new Word(">=", "GE", Tag.GE, false);
    public static final Word dif = new Word("!>", "DIF", Tag.DIF, false);


    // Construtor adicionado para adicionar símbolos e identificadores
    public Word(String s, String nome, int tag, boolean isId) {
        super(nome, tag);
        lexeme = s;
        this.isId = isId;
    }
    
    // Construtor utilizado para adicionar palavras chaves
    public Word(String s, int tag) {
        super("ID", tag);
        lexeme = s;
        isId = false;
    }
    
    @Override
    public String toString() {
        if(isId)
            return "<" + nome + ", " + lexeme + ">";
        else
            return "<" + lexeme + ">";
    }
    
    public String getLexeme(){
        return lexeme;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Word && ((Word) obj).isId == isId && ((Word) obj).lexeme == lexeme && super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
