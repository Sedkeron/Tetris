package PolyominoIO;


import java.io.FileInputStream;
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
public class PolyominoInputStream {
    FileInputStream i;
    int size;
    
    public PolyominoInputStream(int size){
        try {
            i = new FileInputStream(size + "-omino");
        } catch (FileNotFoundException ex) {
            PolyominoGeneration.PolyominoGenerator.generatePolyomino(size);
        }
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
    
    public ArrayList<boolean[][]> readAll(){
        ArrayList<boolean[][]> all = new ArrayList<>();
        
        boolean notDone=true;
        while (notDone){
            try {
                boolean[][] b = read();
                if (b==null){
                    notDone=false;
                } else {
                    all.add(b);
                }
            } catch (IOException ex) {
                Logger.getLogger(PolyominoInputStream.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return all;
    }
}
