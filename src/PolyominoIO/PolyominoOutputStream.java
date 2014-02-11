package PolyominoIO;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathon
 */
public class PolyominoOutputStream {
    FileOutputStream o;
    int size;
    
    public PolyominoOutputStream(int size) throws FileNotFoundException{
        o = new FileOutputStream(size + "temp");
        this.size=size;
    }
    
    public void writePolyomino(boolean[][] polyomino) throws IOException{
        assert polyomino.length == this.size;
        assert polyomino[0].length == this.size;
        int numBytes = (polyomino.length * polyomino[0].length + 7)/ 8;
        byte[] bytesToAdd = new byte[numBytes];
        byte currentByte=0;
        for (int x=0; x<polyomino.length; x++){
            for (int y=0; y<polyomino[0].length; y++){
                int count = (x*polyomino[0].length + y);
                if (count%8==0) 
                    currentByte=0;
                if (polyomino[x][y])
                    currentByte |= 1 << (count%8);
                if (count%8 == 7)
                    bytesToAdd[count/8]=currentByte;
            }
        }
        bytesToAdd[bytesToAdd.length-1]=currentByte;
        o.write(bytesToAdd);
    }
    
    public void done() throws IOException{
        o.close();
        File f = new File(size + "temp");
        File f2 = new File(size + "-omino");
        f2.delete();
        f.renameTo(f2);
    }
}
