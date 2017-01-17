/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.IOException;

/**
 *
 * @author Larissa
 */
public class Semantic {
    private Lexer lexer;

    Token tok;

    public Semantic(Lexer lexer) {
        this.lexer = lexer;
        tok = null;
    }
    
    /*
    VERIFICAR:
    - tipos
    - declaração antes de usar
    */
    
    
    boolean advance() {
        try {
            tok = lexer.scan();
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(Syntax.class.getName()).log(Level.SEVERE, null, ex);
            error("Erro na linha " + Lexer.line + ": Token não reconhecido.");
            return false;
        }
    }

    boolean eat(int t) {
        if (tok.tag == t) {
            return advance();
        } else {
            //error("Erro na linha " + Lexer.line + ": Token esperado (" + t + ") - Token recebido (" + tok + ")");
            return false;
        }
    }

    private void error(String erro) {
        System.out.println(erro);
    }
}
