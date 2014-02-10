
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathon
 */
public class PolyominoIOTester {
    public static void main(String[] args) {
        /*boolean[][] b = {
            {true, false, true,  true, false},
            {true, true,  true,  true, false},
            {true, false, false, true, false},
            {true, false, true,  true, true},
            {true, false, true, true, false}};
        
        boolean[][] b2 = {
            {false, false, false,  false, false},
            {true, false,  false,  true, false},
            {true, false, false, false, false},
            {true, false, true,  false, true},
            {true, false, false, false, true}};
        */
        /*
        boolean[][] b = {
            {true, false, true},
            {true, false, true},
            {true, true, true}};
        
        boolean[][] b2 = {
            {false, false, false},
            {false, true, false},
            {false, false, false}};
        */
        boolean[][] b = {
            {true, false},
            {true, false}};
        
        boolean[][] b2 = {
            {true, false},
            {false, true}};
        
        for (boolean[] bArr:b){
            for (boolean bool:bArr){
                System.out.print(bool + "  ");
            }
            System.out.println();
        }
        
        System.out.println();
        
        for (boolean[] bArr:b2){
            for (boolean bool:bArr){
                System.out.print(bool + "  ");
            }
            System.out.println();
        }
        
        System.out.println("\n\n\n--------------------------------\n\n\n");
        
        try {
            PolyominoOutputStream o = new PolyominoOutputStream(b.length);
            o.writePolyomino(b);
            o.writePolyomino(b2);
            o.done();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PolyominoIOTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PolyominoIOTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            PolyominoInputStream i = new PolyominoInputStream(b.length);
            b=i.read();
            b2=i.read();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PolyominoIOTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PolyominoIOTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        for (boolean[] bArr:b){
            for (boolean bool:bArr){
                System.out.print(bool + "  ");
            }
            System.out.println();
        }
        System.out.println();
        for (boolean[] bArr:b2){
            for (boolean bool:bArr){
                System.out.print(bool + "  ");
            }
            System.out.println();
        }
    }
}
