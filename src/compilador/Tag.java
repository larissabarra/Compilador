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
            PRG = 256,
            BEG = 257,
            END = 258,
            TYPE = 259,
            INT = 260,
            CHAR = 261,
            BOOL = 262,
            
            //Operadores e pontuação
            EQ = 288,
            GE = 289,
            LE = 290,
            NE = 291,
            
            //Outros tokens
            NUM = 278,
            ID = 279;
}
