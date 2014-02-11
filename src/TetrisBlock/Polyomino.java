package TetrisBlock;


import Temp.Field;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

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
    private Block[][] poly;
    
    public static final int DIR_DOWN=0;
    public static final int DIR_UP=1;
    public static final int DIR_LEFT=2;
    public static final int DIR_RIGHT=3;
    
    public Polyomino(int xLoc, int yLoc, boolean[][] shape, float hue){
        this.loc=new Point(xLoc, yLoc);
        
        shape = format(shape);
        
        int dx = (shape.length-1)/2;
        int dy = (shape[0].length-1)/2;
        
        poly = new Block[shape.length][shape[0].length];
        
        for (int x=0; x<shape.length; x++){
            for (int y=0; y<shape[0].length; y++){
                if (shape[x][y])
                    poly[x][y]=new Block(x+this.loc.x-dx, y+this.loc.y-dy, hue);
            }
        }
    }
    
    @Override
    public void paint(Graphics g){
        for (Block[] bArr:poly){
            for (Block b:bArr){
                if (b!=null){
                    BufferedImage bi = b.getImage();
                    int xPos = bi.getWidth()*b.getLoc().x;
                    int yPos = b.getImage().getWidth()*(Field.HEIGHT-b.getLoc().y);
                    g.drawImage(bi, xPos, yPos, null);
                }
            }
        }
    }
    
    public void shift(int direction){
        if (direction == DIR_DOWN) loc.translate(0, -1);
        else if (direction == DIR_UP) loc.translate(0,1);
        else if (direction == DIR_LEFT) loc.translate(-1,0);
        else if (direction == DIR_RIGHT) loc.translate(1,0);
        
        for (Block[] bArr:poly){
            for (Block b:bArr){
                if (b!=null)
                    b.shift(direction);
            }
        }
    }
    
    public void rotateCW(){
        for (Block[] bArr:poly){
            for (Block b:bArr){
                if (b!=null)
                    b.rotateCW(this.loc);
            }
        }
    }
    
    public void rotateCCW(){
        for (Block[] bArr:poly){
            for (Block b:bArr){
                if (b!=null)
                    b.rotateCCW(this.loc);
            }
        }
    }

    private boolean[][] format(boolean[][] shape) {
        shape=truncate(shape);
        shape=rotateDown(shape);
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
        int tWeight=0;
        int bWeight=0;
        int lWeight=0;
        int rWeight=0;
        
        for (int i=0; i<shape.length; i++){
            if (shape[i][0]) tWeight++;
            if (shape[i][shape[i].length-1]) bWeight++;
            if (shape[0][i]) lWeight++;
            if (shape[shape.length-1][i]) rWeight++;
        }
        
        int maxWeight = Math.max(tWeight, Math.max(bWeight, Math.max(lWeight, rWeight)));
        
        if (bWeight==maxWeight) return shape;
        
        if (tWeight==maxWeight){
            boolean[][] newS = new boolean[shape.length][shape.length];
            
            for (int i=0; i<)
        }
    }
}
