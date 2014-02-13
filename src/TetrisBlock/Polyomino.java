package TetrisBlock;


import Temp.Field;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathon
 */
public class Polyomino extends Component{
    private Point loc;
    private ArrayList<Block> poly;
    private int size;
    
    public static final int DIR_DOWN=0;
    public static final int DIR_UP=1;
    public static final int DIR_LEFT=2;
    public static final int DIR_RIGHT=3;
    
    /**
     * boolean[][] shape must be square
     */
    public Polyomino(int xLoc, int yLoc, boolean[][] shape, float hue){
        this.loc=new Point(xLoc, yLoc);
        poly=new ArrayList<>();
        
        shape = format(shape);
        
        size = shape.length;
        
        int dx = (shape[0].length-1)/2;
        int dy = (shape.length-1)/2;
        
        for (int x=0; x<shape[0].length; x++){
            for (int y=0; y<shape.length; y++){
                if (shape[y][x])
                    poly.add(new Block(x+this.loc.x-dx, y+this.loc.y-dy, hue));
            }
        }
    }
    
    @Override
    public void paint(Graphics g){
        for (Block b:poly){
            if (b!=null){
                BufferedImage bi = b.getImage();
                int xPos = bi.getWidth()*b.getLoc().x;
                int yPos = b.getImage().getWidth()*(Field.HEIGHT-b.getLoc().y);
                g.drawImage(bi, xPos, yPos, null);
            }
        }
    }
    
    public void shift(int direction){
        if (direction == DIR_DOWN) loc.translate(0, -1);
        else if (direction == DIR_UP) loc.translate(0,1);
        else if (direction == DIR_LEFT) loc.translate(-1,0);
        else if (direction == DIR_RIGHT) loc.translate(1,0);
        
        for (Block b:poly){
            if (b!=null)
                b.shift(direction);
        }
    }
    
    public void rotate(){
        if (size%2==1){
            int cx = loc.x-(size-1)/2;
            int cy = loc.y-(size-1)/2;
            for (Block bl:poly){
                int x = bl.getLoc().x-cx;
                int y = bl.getLoc().y-cy;
                int dx = y-x;
                int dy = 2-x-y;
                bl.translate(dx,dy);
            }
        } else {
            int cx = loc.x-(size-1)/2;
            int cy = loc.y-(size-1)/2;
            for (Block bl:poly){
                int x=bl.getLoc().x-cx;
                int y=bl.getLoc().y-cy;
                int dx=y-x;
                int dy=size-1-x-y;
                bl.translate(dx,dy);
            }
        }
    }

    private boolean[][] format(boolean[][] shape) {
        shape=truncate(shape);
        shape=rotateDown(shape);
        shape=makeSquare(shape);
        return shape;
    }
    
    private boolean[][] truncate(boolean[][] shape) {
        int firstH=Integer.MAX_VALUE;
        int lastH=Integer.MIN_VALUE;
        int firstV=Integer.MAX_VALUE;
        int lastV=Integer.MIN_VALUE;
        for (int i=0; i<shape.length; i++){
            boolean notEmptyColumn=false;
            for (boolean j:shape[i]){
                notEmptyColumn |= j;
            }
            if (notEmptyColumn){
                firstH=i;
                break;
            }
        }
        for (int i=shape.length-1; i>=0; i--){
            boolean notEmptyColumn=false;
            for (boolean j:shape[i]){
                notEmptyColumn |= j;
            }
            if (notEmptyColumn){
                lastH=i;
                break;
            }
        }
        for (int i=0; i<shape.length; i++){
            boolean notEmptyRow=false;
            for (int j=0; j<shape[i].length; j++){
                notEmptyRow |= shape[j][i];
            }
            if (notEmptyRow){
                firstV=i;
                break;
            }
        }
        for (int i=shape.length-1; i>=0; i--){
            boolean notEmptyRow=false;
            for (int j=0; j<shape[i].length; j++){
                notEmptyRow |= shape[j][i];
            }
            if (notEmptyRow){
                lastV=i;
                break;
            }
        }
        boolean[][] newS = new boolean[lastH-firstH+1][lastV-firstV+1];
        
        for (int i=0; i<newS.length; i++){
            for (int j=0; j<newS[i].length; j++){
                newS[i][j]=shape[firstH+i][firstV+j];
            }
        }
        
        return newS;
    }
    
    private boolean[][] rotateDown(boolean[][] shape){
        int lWeight=0;
        int rWeight=0;
        int tWeight=0;
        int bWeight=0;
        
        if (shape.length>=shape[0].length){
            for (int i=0; i<shape.length; i++){
                if (shape[i][0]) lWeight++;
                if (shape[i][shape[i].length-1]) rWeight++;
            }
        }
        
        if (shape[0].length>=shape.length){
            for (int i=0; i<shape[0].length; i++){
                if (shape[0][i]) tWeight++;
                if (shape[shape.length-1][i]) bWeight++;
            }
        }
        
        int maxWeight = Math.max(lWeight, Math.max(rWeight, Math.max(tWeight, bWeight)));
        
        if (bWeight==maxWeight) return shape;
        
        if (tWeight==maxWeight){
            boolean[][] newS = new boolean[shape.length][shape[0].length];
            
            for (int i=0; i<newS.length; i++){
                for (int j=0; j<newS[0].length; j++){
                    newS[i][j]=shape[newS.length-1-i][newS[i].length-1-j];
                }
            }
            
            return newS;
        }
        
        if (lWeight==maxWeight){
            boolean[][] newS = new boolean[shape[0].length][shape.length];
            
            for (int i=0; i<newS.length; i++){
                for (int j=0; j<newS[0].length; j++){
                    newS[i][j]=shape[j][shape[0].length-1-i];
                }
            }
            
            return newS;
        }
        
        boolean[][] newS = new boolean[shape[0].length][shape.length];
            
        for (int i=0; i<newS.length; i++){
            for (int j=0; j<newS[0].length; j++){
                newS[i][j]=shape[shape.length-1-j][i];
            }
        }

        return newS;
    }
    
    private boolean[][] makeSquare(boolean[][] shape){
        int newSize=(shape.length>shape[0].length)?shape.length:shape[0].length;
        
        boolean newS[][]=new boolean[newSize][newSize];
        
        for (int i=0; i<shape.length; i++){
            for (int j=0; j<shape[0].length; j++){
                newS[i+(newSize-shape.length)/2][j+(newSize-shape[0].length)/2]=shape[i][j];
            }
        }
        return newS;
    }
}
