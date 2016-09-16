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

    public Token(int t) {
        tag = t;
    }

    @Override
    public String toString() {
        return "" + tag;
    }
}
