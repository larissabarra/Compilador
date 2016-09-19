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
public class NumInt extends Token{
    public final int value;

    public NumInt(int value) {
        super("INT_NUM", Tag.INT_NUM);
        this.value = value;
    }

    public String toString() {
        return "Token: <" + nome + ", " + value + ">";
    }
}
