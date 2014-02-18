/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import PolyominoIO.PolyominoInputStream;
import TetrisBlock.Polyomino;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jonathon
 */
public class Gui {
    private static volatile Polyomino curP;
    private static volatile Field field;
    private static volatile JFrame frame;
    private static volatile JPanel mainPanel, fieldPanel, menuPanel, statsPanel, o1Panel, o2Panel;
    private static volatile JButton startNewGameB, pauseB, exitB;
    private static volatile JSlider minPSizeS, maxPSizeS, xFieldSizeS, yFieldSizeS, speedS;
    private static volatile JTextField scoreF, linesClearedF, minPSizeT, maxPSizeT,
            xFieldSizeT, yFieldSizeT, speedT;
    private static volatile Action upAction, leftAction, rightAction, spaceAction,
            downAction, startAction, pauseAction, exitAction;
    private static volatile long waitTime, cWaitTime, t;
    private static volatile boolean running;
    private static volatile BufferedImage foreground;
    private static final Object LOCK = new Object();
    private static volatile boolean newGame;
    private static volatile int score, linesCleared, minPSize, maxPSize, fHeight, fWidth, newFHeight, newFWidth;
    
    public static final int MIN_PSIZE=1;
    
    public Gui(){}
    
    public static void main(String[] args) {
        minPSize=4;
        maxPSize=4;
        fWidth=10;
        newFWidth=fWidth;
        fHeight=20;
        newFHeight=fHeight;
        makeFrame();
        
        waitTime=441;
        running=false;
        setForeground("TetrisStartScreen.png");
        newGame=true;
        
        fieldPanel.repaint();
        run();
    }
    
    public static void run(){
        try{
            while(true){
                cWaitTime=waitTime;
                synchronized (LOCK){
                    while (!running){
                        LOCK.wait();
                    }
                    if (!field.act(curP)){
                        curP=getRanPoly();
                        if (field.lose()){
                            newGame=true;
                        }
                    }
                    if (newGame){
                        newGame=false;
                        newGame();
                    }
                    fieldPanel.repaint();
                }
                t = System.currentTimeMillis();
                Thread.sleep(waitTime/8);
                synchronized (LOCK){
                    while (System.currentTimeMillis()-t<cWaitTime)
                        LOCK.wait(waitTime/16);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void newGame(){
        running=false;
        boolean resize = false;
        if (fWidth != newFWidth || fHeight != newFHeight){
            resize = true;
            fWidth=newFWidth;
            fHeight=newFHeight;
        }
        setForeground("TetrisStartScreen.png");
        field=new Field(fWidth,fHeight);
        if (resize){
            fieldPanel.setPreferredSize(new Dimension((fWidth+2)
                    *Field.B_SIZE, (fHeight+2)*Field.B_SIZE));
            maxPSizeS.setMaximum(Math.min(fWidth, 20));
            minPSizeS.setMaximum(Math.min(fWidth, 20));

            fieldPanel.repaint();
            
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        curP=getRanPoly();
    }
    
    public static JFrame makeFrame(){
        frame = new JFrame("Tetris");
        frame.add(makeMainPanel());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
    
    public static JPanel makeMainPanel(){
        mainPanel = new JPanel(new GridBagLayout());
        addStuffToMainPanel();
        return mainPanel;
    }
        
    private static class UpAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent up){
            synchronized (LOCK){
                if (!running) return;
                field.rotate(curP);
                fieldPanel.repaint();
            }
        }
    }
    private static class LeftAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent left){
            synchronized (LOCK){
                if (!running) return;
                field.translate(curP,-1,0);
                fieldPanel.repaint();
            }
        }
    }
    private static class RightAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent right){
            synchronized (LOCK){
                if (!running) return;
                field.translate(curP,1,0);
                fieldPanel.repaint();
            }
        }
    }
    private static class DownAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent down){
            if (!running) return;
            cWaitTime/=8;
            synchronized (LOCK){
                LOCK.notifyAll();
            }
        }
    }
    private static class SpaceAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent space){
            synchronized (LOCK){
                if (!running){
                    running=true;
                    foreground=null;
                    LOCK.notifyAll();
                    return;
                }
                fieldPanel.repaint();
                while (field.translate(curP,0,-1));
                field.takePolyomino(curP);
                curP = getRanPoly();
                if (field.lose()){
                    running=false;
                    newGame=true;
                }
            }
        }
    }
    
    private static Polyomino getRanPoly(){
        int s = (int)(Math.random()*(maxPSize-minPSize+1))+minPSize;
        PolyominoInputStream i = new PolyominoInputStream(s);
        ArrayList<boolean[][]> all = i.readAll();
        
        int n = (int)(Math.random()*all.size());
        
        
        return new Polyomino(Field.fWidth/2, Field.fHeight+s/2, all.get(n), (float)n/all.size());
    }
    
    private static void setForeground(String name){
        try {
            BufferedImage b=ImageIO.read(new Gui().getClass().getResource("/gui/"+name));
            foreground = new BufferedImage(fWidth*Field.B_SIZE, fHeight*Field.B_SIZE, b.getType());
            Graphics g = foreground.createGraphics();
            if ((double)fHeight/fWidth < 2){
                int dx = (2*foreground.getWidth()-foreground.getHeight())/4;
                g.drawImage(b, dx, 0, foreground.getWidth()-dx, foreground.getHeight(),
                        0, 0, b.getWidth(), b.getHeight(), null);
            } else {
                int dy = (foreground.getHeight()-2*foreground.getWidth())/2;
                g.drawImage(b, 0, dy, foreground.getWidth(), foreground.getHeight()-dy,
                        0, 0, b.getWidth(), b.getHeight(), null);
            }
            g.dispose();
            fieldPanel.repaint();
        } catch (IOException ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        fieldPanel.repaint();
    }
    
    private static void addStuffToMainPanel(){
        mainPanel.add(makeStatsPanel(), statsGBC());
        mainPanel.add(makeFieldPanel(), fieldGBC());
        mainPanel.add(makeMenuPanel(), menuGBC());
        mainPanel.add(makeOptionsPanel1(), o1GBC());
        mainPanel.add(makeOptionsPanel2(), o2GBC());
    }
    
    private static JPanel makeFieldPanel(){
        fieldPanel=new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                synchronized (LOCK) {
                    super.paintComponent(g);
                    curP.paint(g);
                    field.paint(g);
                    if (foreground!=null) g.drawImage(foreground, Field.B_SIZE, Field.B_SIZE, this);
                    LOCK.notifyAll();
                }
            }
        };
        upAction = new UpAction();
        leftAction = new LeftAction();
        rightAction = new RightAction();
        spaceAction = new SpaceAction();
        downAction = new DownAction();
        
        startAction = new NewGameAction();
        pauseAction = new PauseAction();
        exitAction = new ExitAction();
        
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("UP"), "doUpAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("I"), "doUpAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("W"), "doUpAction");
        fieldPanel.getActionMap().put("doUpAction", upAction);
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "doLeftAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("J"), "doLeftAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("A"), "doLeftAction");
        fieldPanel.getActionMap().put("doLeftAction", leftAction);
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "doRightAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("L"), "doRightAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("D"), "doRightAction");
        fieldPanel.getActionMap().put("doRightAction", rightAction);
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "doDownAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("K"), "doDownAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("S"), "doDownAction");
        fieldPanel.getActionMap().put("doDownAction", downAction);
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "doSpaceAction");
        fieldPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT,
                InputEvent.SHIFT_DOWN_MASK), "doSpaceAction");
        fieldPanel.getActionMap().put("doSpaceAction", spaceAction);
        
        field=new Field(fWidth,fHeight);
        fieldPanel.setPreferredSize(new Dimension((Field.fWidth+2)*Field.B_SIZE,
                (Field.fHeight+2)*Field.B_SIZE));
        curP=getRanPoly();
        fieldPanel.add(curP);
        fieldPanel.add(field);

        return fieldPanel;
    }
    private static GridBagConstraints fieldGBC(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.NONE;
        c.gridheight=GridBagConstraints.REMAINDER;
        c.gridwidth=1;
        c.gridx=1;
        c.gridy=0;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JPanel makeStatsPanel(){
        statsPanel = new JPanel(new GridBagLayout());
        return statsPanel;
    }
    private static GridBagConstraints statsGBC(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=0;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JPanel makeMenuPanel(){
        menuPanel = new JPanel(new GridBagLayout());
        menuPanel.add(makeNewGameButton(), mNGB());
        menuPanel.add(makePauseButton(), mPB());
        menuPanel.add(makeExitButton(), mEB());
        
        return menuPanel;
    }
    private static GridBagConstraints menuGBC(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=2;
        c.gridy=0;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JPanel makeOptionsPanel1(){
        o1Panel = new JPanel(new GridBagLayout());
        o1Panel.add(makeMinPSizeTField(), mMPSTF());
        o1Panel.add(makeMinPSizeSlider(), mMPSS());
        o1Panel.add(makeMaxPSizeTField(), mMxPSTF());
        o1Panel.add(makeMaxPSizeSlider(), mMxPSS());
        o1Panel.add(makeXFieldTField(), mXFTF());
        o1Panel.add(makeXFieldSlider(), mXFS());
        o1Panel.add(makeYFieldTField(), mYFTF());
        o1Panel.add(makeYFieldSlider(), mYFS());
        
        return o1Panel;
    }
    private static GridBagConstraints o1GBC(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=1;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JPanel makeOptionsPanel2(){
        o2Panel = new JPanel(new GridBagLayout());
        o2Panel.add(makeSpeedTField(), mSTF());
        o2Panel.add(makeSpeedSlider(), mSS());
        
        return o2Panel;
    }
    private static GridBagConstraints o2GBC(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=2;
        c.gridy=1;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JButton makeNewGameButton(){
        startNewGameB = new JButton("New Game");
        startNewGameB.setAction(startAction);
        startNewGameB.setFocusable(false);
        startNewGameB.setText("New Game");
        
        return startNewGameB;
    }
    private static GridBagConstraints mNGB(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=0;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JButton makePauseButton(){
        pauseB = new JButton("Pause");
        pauseB.setAction(pauseAction);
        pauseB.setFocusable(false);
        pauseB.setText("Pause");
        
        return pauseB;
    }
    private static GridBagConstraints mPB(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=1;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JButton makeExitButton(){
        exitB = new JButton("Exit");
        exitB.setAction(exitAction);
        exitB.setFocusable(false);
        exitB.setText("Exit");
        
        return exitB;
    }
    private static GridBagConstraints mEB(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=2;
        c.insets=new Insets(10,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JTextField makeMinPSizeTField(){
        minPSizeT = new JTextField("Minimum Polyomino Size");
        minPSizeT.setEditable(false);
        minPSizeT.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        minPSizeT.setHorizontalAlignment(JTextField.CENTER);
        minPSizeT.setFocusable(false);
        
        return minPSizeT;
    }
    private static GridBagConstraints mMPSTF(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=0;
        c.insets=new Insets(10,10,0,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JSlider makeMinPSizeSlider(){
        minPSizeS = new JSlider(JSlider.HORIZONTAL, MIN_PSIZE, fWidth, minPSize);
        minPSizeS.setFocusable(false);
        minPSizeS.addChangeListener(new MinPSliderListener());
        minPSizeS.setMajorTickSpacing(1);
        minPSizeS.setMinorTickSpacing(1);
        minPSizeS.setPaintTicks(true);
        
        Hashtable labelTable = new Hashtable();
        
        for (int i=1; i<=25; i++){
            if (i<10 || (i%2==0))
            labelTable.put(i, new JLabel(String.valueOf(i)));
        }
        
        minPSizeS.setLabelTable(labelTable);
        minPSizeS.setPaintLabels(true);
        
        return minPSizeS;
    }
    private static GridBagConstraints mMPSS(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=1;
        c.insets=new Insets(0,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JTextField makeMaxPSizeTField(){
        maxPSizeT = new JTextField("Maximum Polyomino Size");
        maxPSizeT.setEditable(false);
        maxPSizeT.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        maxPSizeT.setHorizontalAlignment(JTextField.CENTER);
        maxPSizeT.setFocusable(false);
        
        return maxPSizeT;
    }
    private static GridBagConstraints mMxPSTF(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=2;
        c.insets=new Insets(10,10,0,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JSlider makeMaxPSizeSlider(){
        maxPSizeS = new JSlider(JSlider.HORIZONTAL, MIN_PSIZE, fWidth, maxPSize);
        maxPSizeS.setFocusable(false);
        maxPSizeS.addChangeListener(new MaxPSliderListener());
        maxPSizeS.setMajorTickSpacing(1);
        maxPSizeS.setMinorTickSpacing(1);
        maxPSizeS.setPaintTicks(true);
        
        Hashtable labelTable = new Hashtable();
        
        for (int i=1; i<=25; i++){
            if (i<10 || (i%2==0))
            labelTable.put(i, new JLabel(String.valueOf(i)));
        }
        
        maxPSizeS.setLabelTable(labelTable);
        maxPSizeS.setPaintLabels(true);
        
        return maxPSizeS;
    }
    private static GridBagConstraints mMxPSS(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=3;
        c.insets=new Insets(0,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JTextField makeXFieldTField(){
        xFieldSizeT = new JTextField("Field Width");
        xFieldSizeT.setEditable(false);
        xFieldSizeT.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        xFieldSizeT.setHorizontalAlignment(JTextField.CENTER);
        xFieldSizeT.setFocusable(false);
        
        return xFieldSizeT;
    }
    private static GridBagConstraints mXFTF(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=4;
        c.insets=new Insets(10,10,0,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JSlider makeXFieldSlider(){
        xFieldSizeS = new JSlider(JSlider.HORIZONTAL, 5, 25, 10);
        xFieldSizeS.setFocusable(false);
        xFieldSizeS.addChangeListener(new XFieldSliderListener());
        xFieldSizeS.setMajorTickSpacing(5);
        xFieldSizeS.setPaintTicks(true);
        xFieldSizeS.setPaintLabels(true);
        
        return xFieldSizeS;
    }
    private static GridBagConstraints mXFS(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=5;
        c.insets=new Insets(0,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JTextField makeYFieldTField(){
        yFieldSizeT = new JTextField("Field Height");
        yFieldSizeT.setEditable(false);
        yFieldSizeT.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        yFieldSizeT.setHorizontalAlignment(JTextField.CENTER);
        yFieldSizeT.setFocusable(false);
        
        return yFieldSizeT;
    }
    private static GridBagConstraints mYFTF(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=6;
        c.insets=new Insets(10,10,0,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JSlider makeYFieldSlider(){
        yFieldSizeS = new JSlider(JSlider.HORIZONTAL, 10, 25, 20);
        yFieldSizeS.setFocusable(false);
        yFieldSizeS.addChangeListener(new YFieldSliderListener());
        yFieldSizeS.setMajorTickSpacing(5);
        yFieldSizeS.setPaintTicks(true);
        yFieldSizeS.setPaintLabels(true);
        
        return yFieldSizeS;
    }
    private static GridBagConstraints mYFS(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=7;
        c.insets=new Insets(0,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JTextField makeSpeedTField(){
        speedT = new JTextField("Speed");
        speedT.setEditable(false);
        speedT.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        speedT.setHorizontalAlignment(JTextField.CENTER);
        speedT.setFocusable(false);
        
        return speedT;
    }
    private static GridBagConstraints mSTF(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=0;
        c.insets=new Insets(10,10,0,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static JSlider makeSpeedSlider(){
        speedS = new JSlider(JSlider.HORIZONTAL, 1, 10, 2);
        speedS.setFocusable(false);
        speedS.addChangeListener(new SpeedSliderListener());
        speedS.setMajorTickSpacing(1);
        speedS.setPaintTicks(true);
        speedS.setPaintLabels(true);
        
        return speedS;
    }
    private static GridBagConstraints mSS(){
        GridBagConstraints c = new GridBagConstraints();
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=1;
        c.insets=new Insets(0,10,10,10);
        c.ipadx=0;
        c.ipady=0;
        c.weightx=0;
        c.weighty=0;
        
        return c;
    }
    
    private static class NewGameAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent pause){
            newGame=true;
        }
    }
    private static class PauseAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent pause){
            pause();
        }
    }
    private static class ExitAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent exit){
            System.exit(0);
        }
    }
    
    private static class MinPSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e){
            pause();
            JSlider source = (JSlider)e.getSource();
            minPSize = source.getValue();
            if (minPSize>maxPSize){
                maxPSize=minPSize;
                maxPSizeS.setValue(minPSize);
            }
        }
    }
    private static class MaxPSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e){
            pause();
            JSlider source = (JSlider)e.getSource();
            maxPSize = source.getValue();
            if (maxPSize<minPSize){
                minPSize=maxPSize;
                minPSizeS.setValue(maxPSize);
            }
        }
    }
    private static class XFieldSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e){
            pause();
            JSlider source = (JSlider)e.getSource();
            newFWidth = source.getValue();
        }
    }
    private static class YFieldSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e){
            pause();
            JSlider source = (JSlider)e.getSource();
            newFHeight = source.getValue();
        }
    }
    private static class SpeedSliderListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e){
            pause();
            JSlider source = (JSlider)e.getSource();
            waitTime = (long)(40*Math.pow(1.35,source.getMaximum()-source.getValue()));
        }
    }
    
    private static void pause(){
        if (!running) return;
        running=false;
        setForeground("TetrisPauseScreen.png");
        field.repaint();
    }
}
