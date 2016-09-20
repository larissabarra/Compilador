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
public class Token {
    public final int tag; //constante que representa o token

    public final String nome;
    
    public Token(String nome, int t) {
        tag = t;
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Token: <" + nome + ">";
    }

    @Override
    public boolean equals(Object obj) {
        Token newObj;
        if(obj instanceof Token){
            newObj = (Token) obj;
        }
        else{
            return false;
        }
        return obj instanceof Token && newObj.nome.equals(nome) && newObj.tag == tag; //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
