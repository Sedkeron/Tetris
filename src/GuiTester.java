
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
public class GuiTester extends JApplet{
    private Polyominoe p;
    private Polyominoe p2;
    
    public GuiTester(){
        boolean[][] shape = {{true},{true},{true},{true}};
        p=new Polyominoe(5, 5, shape, 0);
        boolean[][] shape2 = {{true, false},{true, false},{true, true}};
        p2=new Polyominoe(10,10,shape2, .5f);
    }
    
    public void buildUI(){
        add(p);
        add(p2);
        //only does the last; comment out add(p2); to see the first
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame("Buffered Image Test");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
        GuiTester tst = new GuiTester();
        tst.buildUI();
        f.add(tst);
        
        f.setPreferredSize(new Dimension(500, 500));
        
        f.pack();
        f.setVisible(true);
        
        while(true){
            long t=System.currentTimeMillis();
            while (System.currentTimeMillis()-t<250){}
            tst.p2.shift(Polyominoe.DIR_LEFT);
            tst.p2.repaint();
            while (System.currentTimeMillis()-t<500){}
            tst.p2.shift(Polyominoe.DIR_UP);
            tst.p2.repaint();
            while (System.currentTimeMillis()-t<750){}
            tst.p2.shift(Polyominoe.DIR_RIGHT);
            tst.p2.repaint();
            while (System.currentTimeMillis()-t<1000){}
            tst.p.rotateCW();
            tst.p.repaint();
            tst.p2.shift(Polyominoe.DIR_DOWN);
            tst.p2.repaint();
        }
    }
}
