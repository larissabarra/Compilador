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
            if(args.length == 0){
                System.out.println("Arquivo não localizado no diretório Testes.");
                System.exit(1);
            }
            
            Lexer l = new Lexer ("Testes/" + args[args.length-1]);
            //Lexer l = new Lexer ("Testes/teste4.txt");
            Syntax s = new Syntax(l);
            //Token t = l.scan();
            boolean syntaxOk = s.scan();
            //se não tiver erro de sintaxe, faz o analisador semântico
            if(syntaxOk) {
                Semantic se = new Semantic(l);
            }
            
            //l.imprimeTS();
        } catch (FileNotFoundException ex) {
            System.out.println("Erro de arquivo");
        } catch (IOException ex) {
            System.out.println("Erro de IO: " + ex.getMessage());
        }
    }
}
