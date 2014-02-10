
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathon
 */
public class PolyominoInputStream {
    FileInputStream i;
    int size;
    
    public PolyominoInputStream(int size) throws FileNotFoundException{
        i = new FileInputStream(size + "-omino");
        this.size=size;
    }
    
    public boolean[][] read() throws IOException{
        
        boolean[][] polyomino = new boolean[size][size];
        int numBytes = (polyomino.length * polyomino[0].length + 7)/ 8;
        byte[] b = new byte[numBytes];
        
        if (-1 == i.read(b)) return null;
        
        byte currentByte=0;
        for (int x=0; x<polyomino.length; x++){
            for (int y=0; y<polyomino[0].length; y++){
                
                int count = (x*polyomino[0].length + y);
                
                if (count%8 == 0)
                    currentByte=b[count/8];
                polyomino[x][y] = ((currentByte >> (count%8)) & 1) == 1;
            }
        }
        return polyomino;
    }
}
