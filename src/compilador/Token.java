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
        return "<" + nome + ">";
    }

}
