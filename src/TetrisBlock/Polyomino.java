package TetrisBlock;


import gui.Field;
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
        poly=new ArrayList<>();
        
        shape = format(shape);
        
        size = shape.length;
        this.loc=new Point(xLoc-size/2, yLoc-size/2);
        
        for (int x=0; x<shape[0].length; x++){
            for (int y=0; y<shape.length; y++){
                if (shape[y][x])
                    poly.add(new Block(x+this.loc.x, y+this.loc.y, hue));
            }
        }
    }
    
    public Polyomino(int xLoc, int yLoc, ArrayList<Block> poly, int size){
        this.poly=poly;
        this.size=size;
        this.loc=new Point(xLoc,yLoc);
    }
    
    public ArrayList<Block> getContents(){
        return poly;
    }
    
    public Point getLoc(){
        return new Point(loc);
    }
    
    public int getLength(){
        return this.size;
    }
    
    @Override
    public void paint(Graphics g){
        for (Block b:poly){
            if (b!= null) b.paint(g);
        }
    }
    
    public ArrayList<Point> getTranslateLocs(int dx, int dy){
        ArrayList<Point> locs = new ArrayList<>();
        
        for (Block b:poly){
            if (b!=null)
                locs.add(b.getTranslateLoc(dx, dy));
        }
        return locs;
    }
    
    public void translate(int dx, int dy){
        loc.translate(dx, dy);
        
        for (Block b:poly){
            if (b!=null)
                b.translate(dx, dy);
        }
    }
    
    public ArrayList<Point> getRotateAndTranslateLocs(int dx, int dy){
        ArrayList<Point> locs = getTranslateLocs(dx,dy);
        for (Point p:locs){
            p.translate(p.y+loc.x+dx-p.x-loc.y-dy,
                    size-1-p.x-p.y+loc.x+dx+loc.y+dy);
        }
        return locs;
    }
    
    public void rotate(){
        for (Block bl:poly){
            bl.translate(bl.getLoc().y+loc.x-bl.getLoc().x-loc.y,
                    size-1-bl.getLoc().x-bl.getLoc().y+loc.x+loc.y);
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
                if (shape[i][0]) rWeight++;
                if (shape[i][shape[i].length-1]) lWeight++;
            }
        }
        
        if (shape[0].length>=shape.length){
            for (int i=0; i<shape[0].length; i++){
                if (shape[0][i]) bWeight++;
                if (shape[shape.length-1][i]) tWeight++;
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
                newS[i+(newSize-shape.length+1)/2][j+(newSize-shape[0].length+1)/2]=shape[i][j];
            }
        }
        return newS;
    }
}
