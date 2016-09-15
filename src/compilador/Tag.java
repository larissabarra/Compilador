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
public class Tag {

    public final static int 
            
            //Palavras reservadas   
            
            PROGRAM = 256,
            
            // Tipos de Dados
            
            INT = 257,
            FLOAT = 258,
            
            // Controle de Fluxo
            
            IF = 259,
            ELSE = 260,
            
            // Estrutura de Repetição
            
            REPEAT = 261,
            UNTIL = 262,
            
            // Comandos
            
            SCAN = 263,
            PRINT = 264,

            // Caracteres especiais
            
            DOIS_PONTOS = ':',
            PONTO_VIRGULA = ';',
            
            // Escopo
            
            AC = '{',
            FC = '}',
            
            AP = '(',
            FP = ')',
            
            // Operadores Aritméticos
            
            ADD = '+',
            SUB = '-',
            MULT = '*',
            DIV = '/',
            
            // Operador de Atribuição
            
            ATRIB = '=',
                        
            // Operadores de Comparação

            GT = '>',
            LT = '<',
            NOT = '!',
        
            EQ = 273, // ==
            GE = 274, // >=
            LE = 275, // <=
            NE = 276, // <>
            NG = 283, // !>

            // Operadores Lógicos
            
            OR = 277,
            AND = 278,
                      
            //Outros tokens
            
            INT_NUM = 279,
            FLOAT_NUM = 280,
            LITERAL = 281,
            ID = 282,
            TRUE = 284,
            FALSE = 285;
            
}
