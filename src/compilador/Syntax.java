/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Larissa
 */
public class Syntax {

    private Lexer lexer;
    private Map<String, Integer> declaracoes;
    private Env tabelaSimbolos;
    private int tipoEsperado;

    int tok = 0;
    Token t;

    public Syntax(Lexer lexer, Env tabelaSimbolos) {
        this.lexer = lexer;
        t = null;
        this.tabelaSimbolos = tabelaSimbolos;
        declaracoes = new HashMap<String, Integer>();
    }

    public void scan() {
        advance();
        program();
    }

    private void program() {
        boolean ok = true;
        boolean okAtual = false;

        okAtual = eat(Tag.PROGRAM);
        ok = ok && okAtual;

        if (!ok) {
            error("Erro na linha " + Lexer.line + ": Token esperado (" + Tag.PROGRAM + ") - Token recebido (" + tok + ")");
        }

        decl_list();

        if (!lexer.isEOF()) {
            okAtual = eat(Tag.AC);
            ok = ok && okAtual;
        } else {
            error("Erro na linha " + Lexer.line + ": Erro de sintaxe. Fim de arquivo encontrado antes do esperado.");
            ok = false;
        }

        stmt_list();

        if (!lexer.isEOF()) {
            okAtual = eat(Tag.FC);
            ok = ok && okAtual;
        } else {
            error("Erro na linha " + Lexer.line + ": Erro de sintaxe. Fim de arquivo encontrado antes do esperado.");
            ok = false;
        }
    }

    private void decl_list() {
        boolean ok = true;
        boolean okAtual = false;

        do {
            ok = true;

            okAtual = decl();
            ok = ok && okAtual;

            if (!ok) {
                while (tok != Tag.PONTO_VIRGULA && tok != Tag.AC && !lexer.isEOF()) {
                    advance();
                }
            }

            if (!lexer.isEOF()) {
                okAtual = eat(Tag.PONTO_VIRGULA);
                ok = ok && okAtual;
            } else {
                error("Erro na linha " + Lexer.line + ": Erro de sintaxe. Fim de arquivo encontrado antes do esperado.");
                break;
            }

            if (!ok) {
                error("Erro na linha " + Lexer.line + ": Erro de sintaxe na declaração de variáveis.");
            }

        } while (tok != Tag.AC && !lexer.isEOF());
    }

    private boolean decl() {

        boolean ok = true;
        boolean okAtual = false;

        okAtual = ident_list();
        ok = ok && okAtual;

        okAtual = eat(Tag.DOIS_PONTOS);
        ok = ok && okAtual;

        okAtual = type();
        ok = ok && okAtual;

        return ok;
    }

    private boolean ident_list() {
        boolean ok = true;
        boolean okAtual = false;

        if (t != null && t.tag == Tag.ID) {
            declaracoes.put(tabelaSimbolos.get(t).getName(), null);
        }
        okAtual = eat(Tag.ID);
        ok = ok && okAtual;

        while (tok == Tag.VIRGULA) {
            okAtual = eat(Tag.VIRGULA);
            ok = ok && okAtual;
            if (t != null && t.tag == Tag.ID) {
                declaracoes.put(tabelaSimbolos.get(t).getName(), null);
            }

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
                for (String dec : declaracoes.keySet()) {
                    if (declaracoes.get(dec) == null) {
                        declaracoes.put(dec, Tag.INT);
                        Token key = tabelaSimbolos.getKey(dec);
                        tabelaSimbolos.get(key).setType(Tag.INT_NUM);
                    }
                }
                okAtual = eat(Tag.INT);
                ok = ok && okAtual;
                break;
            case Tag.FLOAT:
                for (String dec : declaracoes.keySet()) {
                    if (declaracoes.get(dec) == null) {
                        declaracoes.put(dec, Tag.FLOAT);
                        Token key = tabelaSimbolos.getKey(dec);
                        tabelaSimbolos.get(key).setType(Tag.FLOAT_NUM);
                    }
                }
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

        do {
            ok = true;
            switch (tok) {
                case Tag.ID:
                    if ((tabelaSimbolos.get(t)) != null) {
                        if (declaracoes.get(tabelaSimbolos.get(t).getName()) == null) {
                            error("Erro na linha " + Lexer.line + ": Erro de semântica. Variável " + t.toString() + " não declarada.");
                        }
                    }
                    okAtual = stmt();
                    ok = ok && okAtual;

                    if (!ok) {
                        if (!lexer.isEOF()) {
                            error("Erro na linha " + Lexer.line + ": Erro de sintaxe no comando de atribuição.");
                        }
                    }

                    break;
                case Tag.IF:
                    okAtual = stmt();
                    ok = ok && okAtual;

                    break;
                case Tag.REPEAT:
                    okAtual = stmt();
                    ok = ok && okAtual;

                    break;
                case Tag.SCAN:
                    tipoEsperado = Tag.ANY;
                    okAtual = stmt();
                    ok = ok && okAtual;

                    if (!ok) {
                        if (!lexer.isEOF()) {
                            error("Erro na linha " + Lexer.line + ": Erro de sintaxe no comando scan.");
                        }
                    }

                    break;
                case Tag.PRINT:
                    okAtual = stmt();
                    ok = ok && okAtual;

                    if (!ok) {
                        if (!lexer.isEOF()) {
                            error("Erro na linha " + Lexer.line + ": Erro de sintaxe no comando print.");
                        }
                    }

                    break;
                case Tag.ERROR:
                    while (tok != Tag.PONTO_VIRGULA && tok != Tag.FC && tok != Tag.UNTIL && !lexer.isEOF()) {
                        advance();
                    }
                    ok = false;
                    break;
                default:
                    ok = false;
                //error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
            }

            if (!lexer.isEOF()) {
                okAtual = eat(Tag.PONTO_VIRGULA);
                ok = ok && okAtual;
            } else {
                error("Erro na linha " + Lexer.line + ": Erro de sintaxe. Fim de arquivo encontrado antes do esperado.");
                break;
            }

        } while (tok != Tag.FC && tok != Tag.UNTIL && !lexer.isEOF());

    }

    private boolean stmt() {
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.ID:
                Word atrib = new Word(((Word) (t)).getLexeme(), t.nome, t.tag);
                if ((tabelaSimbolos.get(t)) != null) {
                    if (declaracoes.get(tabelaSimbolos.get(t).getName()) == null) {
                        error("Erro na linha " + Lexer.line + ": Erro de semântica. Variável " + t.toString() + " não declarada.");
                    }
                    tipoEsperado = tabelaSimbolos.get(t).getType();
                }
                okAtual = eat(Tag.ID);
                ok = ok && okAtual;

                okAtual = eat(Tag.ATRIB);
                ok = ok && okAtual;

                resAtual = simple_expr();
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
                if (tipoEsperado != resAtual.getType() && tipoEsperado != 600  && resAtual.getType() != 600) {
                    error("Erro na linha " + Lexer.line + ": Erro de semântica. Tipo incorreto.");
                } else {
                    Token key = tabelaSimbolos.getKey(atrib.getLexeme());
                    tabelaSimbolos.get(key).setValue(resAtual.getValue());
                }

                break;
            case Tag.IF:
                okAtual = eat(Tag.IF);
                ok = ok && okAtual;

                resAtual = condition();
                okAtual = resAtual.isValid();

                ok = ok && okAtual;

                if (!ok) {
                    if (!lexer.isEOF()) {
                        error("Erro na linha " + Lexer.line + ": Erro de sintaxe no bloco if.");
                    }
                }

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

                resAtual = condition();
                okAtual = resAtual.isValid();

                ok = ok && okAtual;

                if (!ok) {
                    if (!lexer.isEOF()) {
                        error("Erro na linha " + Lexer.line + ": Erro de sintaxe no bloco repeat.");
                    }
                }

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
                tipoEsperado = Tag.ANY;
                okAtual = eat(Tag.PRINT);
                ok = ok && okAtual;

                okAtual = eat(Tag.AP);
                ok = ok && okAtual;

                resAtual = writable();
                ok = ok && okAtual;

                okAtual = eat(Tag.FP);
                ok = ok && okAtual;

                break;
            case Tag.ERROR:
                while (tok != Tag.PONTO_VIRGULA && tok != Tag.FC && tok != Tag.UNTIL && !lexer.isEOF()) {
                    advance();
                }
                ok = false;
                break;
            default:
                error("Erro na linha " + Lexer.line + ": Comando mal-formulado.");
        }

        if (!ok) {
            while (tok != Tag.PONTO_VIRGULA && tok != Tag.FC && tok != Tag.UNTIL && !lexer.isEOF()) {
                advance();
            }
        }

        return ok;
    }

    private Result simple_expr() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        resAtual = term();
        okAtual = resAtual.isValid();

        ok = ok && okAtual;

        resAtual = simple_exprprime(resAtual);
        okAtual = resAtual.isValid();
        ok = ok && okAtual;

        res = resAtual;
        return res;
    }

    private Result term() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        resAtual = factor_a();
        okAtual = resAtual.isValid();

        ok = ok && okAtual;

        resAtual = termprime(resAtual);
        okAtual = resAtual.isValid();
        ok = ok && okAtual;

        res = resAtual;
        return res;
    }

    private Result termprime(Result resAntes) {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.MULT:
            case Tag.DIV:
                boolean divisao = tok == Tag.DIV;
                okAtual = mulop();
                ok = ok && okAtual;

                resAtual = factor_a();
                okAtual = resAtual.isValid();

                ok = ok && okAtual;

                if (resAntes.getType() == resAtual.getType()) {
                    if (divisao) {
                        resAtual.setType(Tag.FLOAT_NUM);
                    }
                    okAtual = resAtual.isValid();
                } else {
                    error("Erro na linha " + Lexer.line + ": Erro de semântica. Tipo incorreto.");
                }

                ok = ok && okAtual;

                resAtual = termprime(resAtual);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
                break;
            case Tag.AND:
                okAtual = mulop();
                ok = ok && okAtual;

                resAtual = factor_a();
                okAtual = resAtual.isValid() && resAtual.getType() == Tag.BOOLEAN;

                ok = ok && okAtual;

                if (resAntes.getType() == resAtual.getType()) {
                    okAtual = resAtual.isValid();
                } else {
                    error("Erro na linha " + Lexer.line + ": Erro de semântica. Tipo incorreto.");
                }

                ok = ok && okAtual;

                resAtual = termprime(resAtual);
                okAtual = resAtual.isValid();

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
                resAtual = resAntes;
                break;
            default:
                resAtual = resAntes;
                ok = false;
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        res = resAtual;
        return res;

    }

    private Result factor_a() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.NOT:
                okAtual = eat(Tag.NOT);
                ok = ok && okAtual;

                resAtual = factor();
                if (resAtual.getType() == Tag.BOOLEAN) {
                    okAtual = resAtual.isValid();
                } else {
                    error("Erro na linha " + Lexer.line + ": Erro de semântica. Tipo incorreto.");
                }

                ok = ok && okAtual;

                //resAtual.setValue(!((boolean) resAtual.getValue()));
                break;
            case Tag.SUB:
                okAtual = eat(Tag.SUB);
                ok = ok && okAtual;

                resAtual = factor();

                if (resAtual.getType() == Tag.INT_NUM || resAtual.getType() == Tag.FLOAT_NUM) {
                    //resAtual.setValue(-((float) resAtual.getValue()));
                } else {
                    error("Erro na linha " + Lexer.line + ": Erro de semântica. Tipo incorreto.");
                }

                okAtual = resAtual.isValid();

                ok = ok && okAtual;
                break;
            default:
                resAtual = factor();
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
        }

        res = resAtual;
        return res;
    }

    private Result factor() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.ID:
                if ((tabelaSimbolos.get(t)) != null) {
                    if (declaracoes.get(tabelaSimbolos.get(t).getName()) == null) {
                        error("Erro na linha " + Lexer.line + ": Erro de semântica. Variável " + t.toString() + " não declarada.");
                    }
                    Id idAtual = tabelaSimbolos.get(t);
                    resAtual = new Result(idAtual.getValue(), idAtual.getType(), true);
                } else {
                    resAtual = new Result(null, Tag.ERROR, false);
                }
                okAtual = eat(Tag.ID);
                ok = ok && okAtual;
                break;
            case Tag.AP:
                okAtual = eat(Tag.AP);
                ok = ok && okAtual;

                resAtual = expression();
                okAtual = resAtual.isValid();

                ok = ok && okAtual;

                if (!ok) {
                    while (tok != Tag.FP && !lexer.isEOF()) {
                        advance();
                    }
                }

                okAtual = eat(Tag.FP);
                ok = ok && okAtual;
                break;
            default:
                resAtual = constant();
                okAtual = resAtual.isValid();
                ok = ok && okAtual;
        }

        res = resAtual;
        return res;
    }

    private Result constant() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.INT_NUM:
                NumInt ci = ((NumInt) (t));
                resAtual = new Result(ci.value, ci.tag, true);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;

                okAtual = eat(Tag.INT_NUM);

                ok = ok && okAtual;

                break;
            case Tag.FLOAT_NUM:
                NumFloat cf = ((NumFloat) (t));
                resAtual = new Result(cf.value, cf.tag, true);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;

                okAtual = eat(Tag.FLOAT_NUM);
                ok = ok && okAtual;
                break;
            case Tag.LITERAL:
                Word cw = ((Word) (t));
                resAtual = new Result(cw.nome, cw.tag, true);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;

                okAtual = eat(Tag.LITERAL);
                ok = ok && okAtual;
                break;
            default:
                resAtual = new Result(null, Tag.ERROR, false);

                ok = false;
            //error("Erro na linha " + Lexer.line + ": Constante mal-formulada.");
        }

        res = resAtual;
        return res;
    }

    private Result simple_exprprime(Result resAntes) {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.ADD:
                okAtual = addop();
                ok = ok && okAtual;

                resAtual = term();
                okAtual = resAtual.isValid();
                ok = ok && okAtual;

                if (resAntes.getType() == resAtual.getType()) {
                    if (resAtual.getType() == Tag.INT_NUM || resAtual.getType() == Tag.INT) {
                        if (!(resAntes.getValue() instanceof String) && !(resAtual.getValue() instanceof String)) {
                            resAtual = new Result((int) resAntes.getValue() + (int) resAtual.getValue(), resAtual.getType(), true);
                        } else {
                            resAtual = new Result((int) 0, resAtual.getType(), true);
                        }
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM || resAtual.getType() == Tag.FLOAT) {
                        if (!(resAntes.getValue() instanceof String) && !(resAtual.getValue() instanceof String)) {
                            resAtual = new Result((float) resAntes.getValue() + (float) resAtual.getValue(), resAtual.getType(), true);
                        } else {
                            resAtual = new Result((float) 0, resAtual.getType(), true);
                        }
                    }
                }

                resAtual = simple_exprprime(resAtual);
                okAtual = resAtual.isValid();
                ok = ok && okAtual;
                break;
            case Tag.SUB:
                okAtual = addop();
                ok = ok && okAtual;

                resAtual = term();
                okAtual = resAtual.isValid();
                ok = ok && okAtual;

                if (resAntes.getType() == resAtual.getType()) {
                    if (resAtual.getType() == Tag.INT_NUM) {
                        resAtual = new Result((int) resAntes.getValue() - (int) resAtual.getValue(), resAtual.getType(), true);
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM) {
                        resAtual = new Result((float) resAntes.getValue() - (float) resAtual.getValue(), resAtual.getType(), true);
                    }
                }

                resAtual = simple_exprprime(resAtual);
                okAtual = resAtual.isValid();
                ok = ok && okAtual;
                break;
            case Tag.OR:
                okAtual = addop();
                ok = ok && okAtual;

                resAtual = term();
                okAtual = resAtual.isValid();
                ok = ok && okAtual;

                if (resAntes.getType() == resAtual.getType()) {
                    resAtual = new Result((boolean) resAntes.getValue() || (boolean) resAtual.getValue(), resAtual.getType(), true);
                }

                resAtual = simple_exprprime(resAtual);
                okAtual = resAtual.isValid();
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
                resAtual = resAntes;
                break;
            default:
                ok = false;
                resAtual = new Result(null, Tag.ANY, false);

            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        res = resAtual;
        return res;
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

    private Result writable() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        resAtual = simple_expr();
        okAtual = resAtual.isValid();

        ok = ok && okAtual;

        res = resAtual;
        return res;
    }

    //coloquei esse método inútil só pra ficar mais legível mesmo
    private Result condition() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        resAtual = expression();
        okAtual = resAtual.isValid();
        ok = ok && okAtual;

        res = resAtual;
        return res;
    }

    private Result expression() {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        resAtual = simple_expr();
        okAtual = resAtual.isValid();

        ok = ok && okAtual;

        resAtual = expressionprime(resAtual);
        okAtual = resAtual.isValid();

        ok = ok && okAtual;

        res = resAtual;

        return res;
    }

    private Result expressionprime(Result resAntes) {
        Result res = null;
        Result resAtual = null;

        boolean ok = true;
        boolean okAtual = false;

        switch (tok) {
            case Tag.EQ:
                okAtual = relop();
                ok = ok && okAtual;

                resAtual = simple_expr();
                okAtual = resAtual.isValid();

                if (resAtual.getType() == resAtual.getType()) {
                    boolean isTrue = false;
                    if (resAtual.getType() == Tag.INT_NUM) {
                        isTrue = (int) resAntes.getValue() == (int) resAtual.getValue();
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM) {
                        isTrue = (float) resAntes.getValue() == (float) resAtual.getValue();
                    }
                    resAtual = new Result(isTrue, isTrue ? Tag.TRUE : Tag.FALSE, true);
                    okAtual = resAtual.isValid();
                } else {
                    //erro de tipo
                }

                ok = ok && okAtual;

                resAtual = expressionprime(resAtual);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
            case Tag.GT:
                okAtual = relop();
                ok = ok && okAtual;

                resAtual = simple_expr();
                okAtual = resAtual.isValid();

                if (resAtual.getType() == resAtual.getType()) {
                    boolean isTrue = false;
                    if (resAtual.getType() == Tag.INT_NUM) {
                        isTrue = (int) resAntes.getValue() > (int) resAtual.getValue();
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM) {
                        isTrue = (float) resAntes.getValue() > (float) resAtual.getValue();
                    }
                    resAtual = new Result(isTrue, isTrue ? Tag.TRUE : Tag.FALSE, true);
                    okAtual = resAtual.isValid();
                } else {
                    //erro de tipo
                }

                ok = ok && okAtual;

                resAtual = expressionprime(resAtual);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
            case Tag.GE:
                okAtual = relop();
                ok = ok && okAtual;

                resAtual = simple_expr();
                okAtual = resAtual.isValid();

                if (resAtual.getType() == resAtual.getType()) {
                    boolean isTrue = false;
                    if (resAtual.getType() == Tag.INT_NUM) {
                        isTrue = (int) resAntes.getValue() >= (int) resAtual.getValue();
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM) {
                        isTrue = (float) resAntes.getValue() >= (float) resAtual.getValue();
                    }
                    resAtual = new Result(isTrue, isTrue ? Tag.TRUE : Tag.FALSE, true);
                    okAtual = resAtual.isValid();
                } else {
                    //erro de tipo
                }

                ok = ok && okAtual;

                resAtual = expressionprime(resAtual);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
            case Tag.LT:
                okAtual = relop();
                ok = ok && okAtual;

                resAtual = simple_expr();
                okAtual = resAtual.isValid();

                if (resAtual.getType() == resAtual.getType()) {
                    boolean isTrue = false;
                    if (resAtual.getType() == Tag.INT_NUM) {
                        isTrue = (int) resAntes.getValue() < (int) resAtual.getValue();
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM) {
                        isTrue = (float) resAntes.getValue() < (float) resAtual.getValue();
                    }
                    resAtual = new Result(isTrue, isTrue ? Tag.TRUE : Tag.FALSE, true);
                    okAtual = resAtual.isValid();
                } else {
                    //erro de tipo
                }

                ok = ok && okAtual;

                resAtual = expressionprime(resAtual);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
            case Tag.LE:
                okAtual = relop();
                ok = ok && okAtual;

                resAtual = simple_expr();
                okAtual = resAtual.isValid();

                if (resAtual.getType() == resAtual.getType()) {
                    boolean isTrue = false;
                    if (resAtual.getType() == Tag.INT_NUM) {
                        isTrue = (int) resAntes.getValue() <= (int) resAtual.getValue();
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM) {
                        isTrue = (float) resAntes.getValue() <= (float) resAtual.getValue();
                    }
                    resAtual = new Result(isTrue, isTrue ? Tag.TRUE : Tag.FALSE, true);
                    okAtual = resAtual.isValid();
                } else {
                    //erro de tipo
                }

                ok = ok && okAtual;

                resAtual = expressionprime(resAtual);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
            case Tag.DIF:
                okAtual = relop();
                ok = ok && okAtual;

                resAtual = simple_expr();
                okAtual = resAtual.isValid();

                if (resAtual.getType() == resAtual.getType()) {
                    boolean isTrue = false;
                    if (resAtual.getType() == Tag.INT_NUM) {
                        isTrue = (int) resAntes.getValue() != (int) resAtual.getValue();
                    }
                    if (resAtual.getType() == Tag.FLOAT_NUM) {
                        isTrue = (float) resAntes.getValue() != (float) resAtual.getValue();
                    }
                    resAtual = new Result(isTrue, isTrue ? Tag.TRUE : Tag.FALSE, true);
                    okAtual = resAtual.isValid();
                } else {
                    //erro de tipo
                }

                ok = ok && okAtual;

                resAtual = expressionprime(resAtual);
                okAtual = resAtual.isValid();

                ok = ok && okAtual;
                break;
            // Lambda
            case Tag.AC:
            case Tag.PONTO_VIRGULA:
            case Tag.FP:
                // Não faz nada porque é lambda
                resAtual = resAntes;
                break;
            default:
                ok = false;
                resAtual = new Result(null, Tag.ANY, false);
            //error("Erro na linha " + Lexer.line + ": Expressão mal-formulada.");
        }

        res = resAtual;
        return res;
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
            tok = t != null && t.tag != 500 ? t.tag : 500;//1;//getToken(); //lê próximo token
            if (tok == 500 && !lexer.isEOF()) {
                //error("Erro na linha " + Lexer.line + ": Token não reconhecido.");//t = lexer.scan();
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
            //error("Erro na linha " + Lexer.line + ": Token esperado (" + t + ") - Token recebido (" + tok + ")");
            return false;
        }
    }

    private void error(String erro) {
        System.out.println(erro);
    }

}
