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
            if(args.length != 0){
                System.out.println("Arquivo não localizado no diretório Testes.");
                System.exit(1);
            }
     Env tabelaSimbolos = new Env(null);
            
            //Lexer l = new Lexer ("Testes/" + args[args.length-1]);
            Lexer l = new Lexer ("Testes/testeCorreto.txt", tabelaSimbolos);
            Syntax s = new Syntax(l, tabelaSimbolos);
            //Token t = l.scan();
            s.scan();
            //s.advance();
            //while (s.t != null) {
                //System.out.println(t.toString());
                //t = l.scan();
                //s.advance();
            //}
            
            //l.imprimeTS();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro de arquivo");
        } catch (IOException ex) {
            System.out.println("Erro de IO: " + ex.getMessage());
        }
    }
}
