/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Larissa
 */
public class Syntax {

    private Lexer lexer;

    int tok = 0;
    Token t;

    public Syntax(Lexer lexer) {
        this.lexer = lexer;
        t = null;
    }

    public void scan() {
        advance();
        program();
    }

    private void program() {
        eat(Tag.PROGRAM);
        decl_list();
        eat(Tag.AC);
        stmt_list();
        eat(Tag.FC);
    }

    private void decl_list() {
        boolean ok = true;
        boolean okAtual = false;

        ok = decl();
        okAtual = eat(Tag.PONTO_VIRGULA);

        while (tok != Tag.AC && !lexer.isEOF()) {
            okAtual = decl();
            ok = ok && okAtual;

            okAtual = eat(Tag.PONTO_VIRGULA);
            ok = ok && okAtual;

            if (!ok) {
                while (tok != Tag.PONTO_VIRGULA && !lexer.isEOF()) {
                    advance();
                }
            }
        }
    }

    private boolean decl() {
        boolean ok = true;
        boolean okAtual = false;

        okAtual = ident_list();
        ok = ok && okAtual;

        okAtual = type();
        ok = ok && okAtual;

        if (!ok) {
            error("Erro na linha " + Lexer.line + ": Declaração mal-formulada.");
        }

        return ok;
    }

    private boolean ident_list() {
        boolean ok = true;
        boolean okAtual = false;

        okAtual = eat(Tag.ID);
        ok = ok && okAtual;

        while (tok == Tag.VIRGULA) {
            okAtual = eat(Tag.VIRGULA);
            ok = ok && okAtual;

            okAtual = eat(Tag.ID);
            ok = ok && okAtual;

            if (!ok) {
                break;
            }
        }

        return ok;

    }

    private boolean type() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.INT:
                okAtual = eat(Tag.INT);
                ok = ok && okAtual;

                break;
            case Tag.FLOAT:
                okAtual = eat(Tag.FLOAT);
                ok = ok && okAtual;
                break;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Tipo de dado não reconhecido.");
        }

        return ok;
    }

    private void stmt_list() {
        boolean ok = true;
        boolean okAtual = false;

        okAtual = stmt();
        ok = ok && okAtual;
        okAtual = eat(Tag.PONTO_VIRGULA);
        ok = ok && okAtual;

        while (tok != Tag.FC && tok != Tag.UNTIL && !lexer.isEOF()) {
            switch (tok) {
                case Tag.ID:
                case Tag.IF:
                case Tag.REPEAT:
                case Tag.SCAN:
                case Tag.PRINT:
                    okAtual = stmt();
                    ok = ok && okAtual;

                    okAtual = eat(Tag.PONTO_VIRGULA);
                    ok = ok && okAtual;
                    
                    // TODO: Tratamento de Erro
                    /*if (!ok) {
                        error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
                    }*/
                    break;
                default:
                //error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
            }
        }

    }

    private boolean stmt() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.ID:
                okAtual = eat(Tag.ID);
                ok = ok && okAtual;

                okAtual = eat(Tag.ATRIB);
                ok = ok && okAtual;

                okAtual = simple_expr();
                ok = ok && okAtual;

                break;
            case Tag.IF:
                okAtual = eat(Tag.IF);
                ok = ok && okAtual;

                okAtual = condition();
                ok = ok && okAtual;

                okAtual = eat(Tag.AC);
                ok = ok && okAtual;

                stmt_list();
                okAtual = eat(Tag.FC);
                ok = ok && okAtual;

                okAtual = if_stmt();
                ok = ok && okAtual;

                break;
            case Tag.REPEAT:
                okAtual = eat(Tag.REPEAT);
                ok = ok && okAtual;

                stmt_list();
                okAtual = eat(Tag.UNTIL);
                ok = ok && okAtual;

                okAtual = condition();
                ok = ok && okAtual;

                break;
            case Tag.SCAN:
                okAtual = eat(Tag.SCAN);
                ok = ok && okAtual;

                okAtual = eat(Tag.AP);
                ok = ok && okAtual;

                okAtual = eat(Tag.ID);
                ok = ok && okAtual;

                okAtual = eat(Tag.FP);
                ok = ok && okAtual;

                break;
            case Tag.PRINT:
                okAtual = eat(Tag.PRINT);
                ok = ok && okAtual;

                okAtual = eat(Tag.AP);
                ok = ok && okAtual;

                okAtual = writable();
                ok = ok && okAtual;

                okAtual = eat(Tag.FP);
                ok = ok && okAtual;

                break;
            default:
                error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
        }

        if (!ok) {
            while (tok != Tag.PONTO_VIRGULA && !lexer.isEOF()) {
                advance();
            }
        }

        return ok;
    }

    private boolean simple_expr() {
        boolean ok = true;
        boolean okAtual = false;

        //TODO: tratar se for lambda
        // simple-expr não gera lambda
        okAtual = term();
        ok = ok && okAtual;

        okAtual = simple_exprprime();
        ok = ok && okAtual;

        return ok;
    }

    private boolean term() {
        boolean ok = true;
        boolean okAtual = false;

        okAtual = factor_a();
        ok = ok && okAtual;

        okAtual = termprime();
        ok = ok && okAtual;

        return ok;
    }

    private boolean termprime() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.MULT:
            case Tag.DIV:
            case Tag.AND:
                okAtual = mulop();
                ok = ok && okAtual;

                okAtual = factor_a();
                ok = ok && okAtual;

                okAtual = termprime();
                ok = ok && okAtual;

                break;
            //Lambda
            case Tag.ADD:
            case Tag.SUB:
            case Tag.OR:
            case Tag.FP:
            case Tag.EQ:
            case Tag.GT:
            case Tag.GE:
            case Tag.LT:
            case Tag.LE:
            case Tag.DIF:
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
                // Não faz nada porque é lambda
                return true;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        return ok;

    }

    private boolean factor_a() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.NOT:
                okAtual = eat(Tag.NOT);
                ok = ok && okAtual;

                okAtual = factor();
                ok = ok && okAtual;

                break;
            case Tag.SUB:
                okAtual = eat(Tag.SUB);
                ok = ok && okAtual;

                okAtual = factor();
                ok = ok && okAtual;
                break;
            default:
                okAtual = factor();
                ok = ok && okAtual;
        }

        return ok;
    }

    private boolean factor() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.ID:
                okAtual = eat(Tag.ID);
                ok = ok && okAtual;
                break;
            case Tag.AP:
                okAtual = eat(Tag.AP);
                ok = ok && okAtual;

                okAtual = expression();
                ok = ok && okAtual;

                okAtual = eat(Tag.FP);
                ok = ok && okAtual;
                break;
            default:
                okAtual = constant();
                ok = ok && okAtual;
        }

        return ok;
    }

    private boolean constant() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.INT_NUM:
                okAtual = eat(Tag.INT_NUM);
                ok = ok && okAtual;
                break;
            case Tag.FLOAT_NUM:
                okAtual = eat(Tag.FLOAT_NUM);
                ok = ok && okAtual;
                break;
            case Tag.LITERAL:
                okAtual = eat(Tag.LITERAL);
                ok = ok && okAtual;
                break;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Constante mal-formulada.");
        }

        return ok;
    }

    private boolean simple_exprprime() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.ADD:
            case Tag.SUB:
            case Tag.OR:
                okAtual = addop();
                ok = ok && okAtual;

                okAtual = term();
                ok = ok && okAtual;

                okAtual = simple_exprprime();
                ok = ok && okAtual;
                break;
            // Lambda
            case Tag.FP:
            case Tag.EQ:
            case Tag.GT:
            case Tag.GE:
            case Tag.LT:
            case Tag.LE:
            case Tag.DIF:
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
                // Não faz nada porque é lambda
                return true;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        return ok;
    }

    private boolean addop() {
        boolean ok = true;
        boolean okAtual;

        switch (tok) {
            case Tag.ADD:
                okAtual = eat(Tag.ADD);
                okAtual = ok && okAtual;
                break;
            case Tag.SUB:
                okAtual = eat(Tag.SUB);
                ok = ok && okAtual;
                break;
            case Tag.OR:
                okAtual = eat(Tag.OR);
                ok = ok && okAtual;
                break;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        return ok;
    }

    private boolean mulop() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.MULT:
                okAtual = eat(Tag.MULT);
                ok = ok && okAtual;
                break;
            case Tag.DIV:
                okAtual = eat(Tag.DIV);
                ok = ok && okAtual;
                break;
            case Tag.AND:
                okAtual = eat(Tag.AND);
                ok = ok && okAtual;
                break;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        return ok;
    }

    private boolean relop() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.EQ:
                okAtual = eat(Tag.EQ);
                ok = ok && okAtual;
                break;
            case Tag.GE:
                okAtual = eat(Tag.GE);
                ok = ok && okAtual;
                break;
            case Tag.GT:
                okAtual = eat(Tag.GT);
                ok = ok && okAtual;
                break;
            case Tag.LE:
                okAtual = eat(Tag.LE);
                ok = ok && okAtual;
                break;
            case Tag.LT:
                okAtual = eat(Tag.LT);
                ok = ok && okAtual;
                break;
            case Tag.DIF:
                okAtual = eat(Tag.DIF);
                ok = ok && okAtual;
                break;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        return ok;
    }

    private boolean writable() {
        boolean ok = true;
        boolean okAtual = false;

        okAtual = simple_expr();
        ok = ok && okAtual;

        return ok;
    }

    //coloquei esse método inútil só pra ficar mais legível mesmo
    private boolean condition() {
        boolean ok = true;
        boolean okAtual = false;

        okAtual = expression();
        ok = ok && okAtual;

        return ok;
    }

    private boolean expression() {
        boolean ok = true;
        boolean okAtual = false;

        ok = simple_expr();
        ok = ok && okAtual;

        okAtual = expressionprime();
        ok = ok && okAtual;

        return ok;
    }

    private boolean expressionprime() {
        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.EQ:
            case Tag.GT:
            case Tag.GE:
            case Tag.LT:
            case Tag.LE:
            case Tag.DIF:
                okAtual = relop();
                ok = ok && okAtual;

                okAtual = simple_expr();
                ok = ok && okAtual;

                okAtual = expressionprime();
                ok = ok && okAtual;
                break;
            // Lambda
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
            case Tag.FP:
                // Não faz nada porque é lambda
                return true;
            default:
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        return ok;
    }

    private boolean if_stmt() {
        boolean ok = true;
        boolean okAtual = false;

        if (tok == Tag.ELSE) {
            okAtual = eat(Tag.ELSE);
            ok = ok && okAtual;

            okAtual = eat(Tag.AC);
            ok = ok && okAtual;

            stmt_list();

            okAtual = eat(Tag.FC);
            ok = ok && okAtual;
        }

        return ok;
    }

    boolean advance() {
        try {
            t = lexer.scan();
            tok = t != null ? t.tag : 0;//1;//getToken(); //lê próximo token
            if (tok == 0 && !lexer.isEOF()) {
                error("[LEXICO] Erro na linha " + Lexer.line + ": Token não reconhecido.");
                //t = lexer.scan();
                return false;
            }
            return true;
        } catch (IOException ex) {
            //Logger.getLogger(Syntax.class.getName()).log(Level.SEVERE, null, ex);
            error("Erro na linha " + Lexer.line + ": Token não reconhecido.");
            return false;
        }
    }

    boolean eat(int t) {
        if (tok == t) {
            return advance();
        } else {
            error("Erro na linha " + Lexer.line + ": Token esperado (" + t + ") - Token recebido (" + tok + ")");
            return false;
        }
    }

    private void error(String erro) {
        System.out.println(erro);
    }

    private boolean proximoComando() {
        boolean existeProximo = false;

        while (tok != Tag.PONTO_VIRGULA && !lexer.isEOF()) {
            advance();
        }

        existeProximo = (tok == Tag.PONTO_VIRGULA);

        return existeProximo;
    }
}
