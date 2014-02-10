
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
public class PolyominoGeneratorTester {
    public static void main(String[] args) {
        PolyominoGenerator.generatePolyomino(3);
        ArrayList<boolean[][]> polys = new ArrayList<>();
        boolean[][] poly = {{false,false,false},{false,false,false},{false,false,false}};
        try {
            PolyominoInputStream i = new PolyominoInputStream(3);
            while(poly!=null){
                poly = i.read();
                if (poly != null) polys.add(poly);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PolyominoGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PolyominoGeneratorTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (boolean[][] p:polys){
            System.out.println(arr(p));
        }
    }
    
    private static String arr(boolean[][] array){
        String str="";
        for (boolean[] arra:array){
            for (boolean boo:arra){
                str += boo + " ";
            }
            str+="\n";
        }
        return str;
    }
}
