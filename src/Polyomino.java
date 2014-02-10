
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
}
