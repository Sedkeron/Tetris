package PolyominoGeneration;

import PolyominoIO.PolyominoInputStream;
import PolyominoIO.PolyominoOutputStream;
import java.io.File;
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
public class PolyominoGenerator {
    
    private static ArrayList<Integer> polyID=new ArrayList<>();
    
    public static void generatePolyomino(int size){
        assert size>0;
        
        PolyominoOutputStream o = null;
        try {
            o = new PolyominoOutputStream(size);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PolyominoGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (size==1){
            try {
                boolean[][] p1 = {{true}};
                o.writePolyomino(p1);
                o.done();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PolyominoGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PolyominoGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        if (!(new File(size + "-omino")).exists()){
            generatePolyomino(size-1);
        }
        
        
        PolyominoInputStream i = null;
        try {
            i = new PolyominoInputStream(size-1);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PolyominoGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        polyID.clear();
        
        while(true){
            boolean[][] nMinusOneOmino = null;
            try {
                nMinusOneOmino = i.read();
            } catch (IOException ex) {
                Logger.getLogger(PolyominoGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (nMinusOneOmino==null){
                break;
            }
            
            
            
            boolean[][] adjacentTiles = genAdjacentTiles(nMinusOneOmino);
            
            for (int x=0; x<adjacentTiles.length; x++){
                for (int y=0; y<adjacentTiles[0].length; y++){
                    
                    boolean[][] polyomino = new boolean[nMinusOneOmino[0].length+2][nMinusOneOmino.length+2];
                    for (int i1=0; i1<nMinusOneOmino.length; i1++){
                        System.arraycopy(nMinusOneOmino[i1], 0, polyomino[i1+1], 1, nMinusOneOmino[0].length);
                    }
                    
                    if (adjacentTiles[x][y]){
                        polyomino[x][y]=true;
                        polyomino = truncate(polyomino);
                        if (testPolyomino(polyomino)){
                            try {
                                o.writePolyomino(polyomino);
                            } catch (IOException ex) {
                                Logger.getLogger(PolyominoGenerator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            }
        }
        try {
            o.done();
        } catch (IOException ex) {
            Logger.getLogger(PolyominoGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static boolean[][] genAdjacentTiles(boolean[][] nMinusOneOmino){
        boolean[][] adjacentTiles = new boolean[nMinusOneOmino[0].length+2][nMinusOneOmino.length+2];
        for (int x=0; x<adjacentTiles.length; x++){
            for (int y=0; y<adjacentTiles[0].length; y++){
                if (checkAdjacentTiles(nMinusOneOmino, x, y))
                    adjacentTiles[x][y]=true;
            }            
        }
        return adjacentTiles;
    }
    
    private static boolean checkAdjacentTiles(boolean[][] nMinusOneOmino, int x, int y){
        x--;
        y--;
        return (x<0 || x>=nMinusOneOmino.length || y<0 || y>=nMinusOneOmino[0].length || !nMinusOneOmino[x][y]) && (
            (x-1>=0 && x-1<nMinusOneOmino.length && y>=0 && y<nMinusOneOmino[0].length && nMinusOneOmino[x-1][y]) ||
            (x+1>=0 && x+1<nMinusOneOmino.length && y>=0 && y<nMinusOneOmino[0].length && nMinusOneOmino[x+1][y]) ||
            (x>=0 && x<nMinusOneOmino.length && y-1>=0 && y-1<nMinusOneOmino[0].length && nMinusOneOmino[x][y-1]) ||
            (x>=0 && x<nMinusOneOmino.length && y+1>=0 && y+1<nMinusOneOmino[0].length && nMinusOneOmino[x][y+1]));
    }
    
    private static boolean testPolyomino(boolean[][] polyomino){
        boolean[][] negative = new boolean[polyomino[0].length][polyomino.length];
        for (int x=0; x<polyomino.length; x++){
            fillWithConstraints(negative, polyomino, x, 0);
            fillWithConstraints(negative, polyomino, x, polyomino[0].length-1);
        }
        for (int y=1; y<polyomino.length-1; y++){
            fillWithConstraints(negative, polyomino, 0, y);
            fillWithConstraints(negative, polyomino, polyomino.length-1, y);
        }
        for (int x=0; x<polyomino.length; x++){
            for (int y=0; y<polyomino[0].length; y++){
                if (! (polyomino[x][y] || negative[x][y])){
                    return false;
                }
            }
        }
        int[] seqA = getSequence(polyomino);
        for (int seq:seqA){
            if (polyID.contains(seq)) return false;
        }
        polyID.add(seqA[0]);
        
        return true;
    }
    
    private static void fillWithConstraints(boolean[][] toFill, boolean[][] constraint, int x, int y){
        if ((x<0 || x>=toFill.length || y<0 || y>=toFill.length) ||
                constraint[x][y] || toFill[x][y])return;
        toFill[x][y]=true;
        fillWithConstraints(toFill, constraint, x+1, y);
        fillWithConstraints(toFill, constraint, x-1, y);
        fillWithConstraints(toFill, constraint, x, y+1);
        fillWithConstraints(toFill, constraint, x, y-1);
    }
    
    private static int[] getSequence(boolean[][] polyomino){
        boolean[][] p = new boolean[polyomino[0].length+2][polyomino.length+2];
        int[] seq = new int[4];
        for (int x=0; x<polyomino.length; x++){
            System.arraycopy(polyomino[x], 0, p[x+1], 1, polyomino.length);
        }
        for (int i=0; i<4; i++){
            for (int x=0; x<p.length; x++){
                for (int y=0; y<p[0].length; y++){
                    if (p[x][y]){
                        boolean[][] pT = new boolean[p[0].length][p.length];
                        for (int m=0; m<p.length; m++){
                            System.arraycopy(p[m], 0, pT[m], 0, p.length);
                        }
                        seq[i]=getSeq(pT, x, y, 0);
                        x=p.length;
                        y=p[0].length;
                        if (i==3) return seq;
                    }
                }
            }
            boolean[][] pTemp = new boolean[p[0].length][p.length];
            for(int j=0; j<p[0].length; j++){
                for(int k=0; k<p.length; k++){
                    pTemp[p.length-k-1][j] = p[j][k];
                }
            }
            p=pTemp;
        }
        return null;
    }
    
    private static int getSeq(boolean[][] p, int x, int y, int seq){
        p[x][y]=false;
        seq*=2;
        if (p[x][y-1]){
            seq+=1;
            seq=getSeq(p, x, y-1, seq);
        }
        seq*=2;
        if (p[x-1][y]){
            seq+=1;
            seq=getSeq(p, x-1, y, seq);
        }
        seq*=2;
        if (p[x][y+1]){
            seq+=1;
            seq=getSeq(p, x, y+1, seq);
        }
        seq*=2;
        if (p[x+1][y]){
            seq+=1;
            seq=getSeq(p, x+1, y, seq);
        }
        return seq;
    }
    
    private static boolean[][] truncate(boolean[][] polyomino){
        boolean[][] newP = new boolean[polyomino.length-1][polyomino.length-1];
        boolean left=false;
        for (int i=0; i<polyomino.length; i++){
            left = left || polyomino[0][i];
        }
        boolean top=false;
        for (int i=0; i<polyomino.length; i++){
            top = top || polyomino[i][0];
        }
        int x=left?0:1;
        int y=top?0:1;
        for (int i=0; i<newP.length; i++){
            System.arraycopy(polyomino[i+x], y, newP[i], 0, newP.length);
        }
        return newP;
    }
}