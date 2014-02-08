
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathon
 */
class BufferedImageTest extends Component{
    
    private BufferedImage bi;
    
    public BufferedImageTest(float hue) {
        
        try {
            bi = ImageIO.read(new File(getClass().getResource("TetrisBlock.png").toURI()));
        } catch (IOException | URISyntaxException ex) {
            Logger.getLogger(BufferedImageTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setHueSaturation(bi, hue, 1);
        
    }
    
    private void setHueSaturation(BufferedImage bi, float hue, float sat){        
        for (int x=0; x<bi.getWidth(); x++){
            for (int y=0; y<bi.getHeight(); y++){
                int rgba = bi.getRGB(x, y);
                Color c= new Color(rgba, false);
                int aCompensation=rgba-c.getRGB();
                float[] hsbVals=Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                c = new Color(Color.HSBtoRGB(hue, sat, hsbVals[2]), true);
                bi.setRGB(x, y, c.getRGB()+aCompensation);
            }
        }
    }
    
    @Override
    public void paint(Graphics g){
        g.drawImage(bi, 10, 10, null);
    }
}

public class BufferedImageTesting extends JApplet{
    public BufferedImageTesting(){}
    
    public void buildUI(){
        final BufferedImageTest bi = new BufferedImageTest(0);
        add("Center", bi);
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame("Buffered Image Test");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
        BufferedImageTesting tst = new BufferedImageTesting();
        tst.buildUI();
        f.add("Center", tst);
        
        f.pack();
        f.setVisible(true);
    }
}