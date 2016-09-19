/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Larissa
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Lexer l = new Lexer ("Testes/teste3.txt");
            Token t = l.scan();
            while (t != null) {
                System.out.println(t.toString());
                t = l.scan();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro de arquivo");
        } catch (IOException ex) {
            System.out.println("Erro de IO");
        }
    }
}
