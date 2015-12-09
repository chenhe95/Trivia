
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author He
 */
public class MainGUI extends javax.swing.JFrame {

    private static final long serialVersionUID = 8064285163713571914L;

    private static final int MAX_NUMBER_QUESTIONS = 20;

    private boolean modifiable = true;
    private int questionHandling = 0;
    private int difficulty = 0;
    private OptionsGUI opt = new OptionsGUI(this);

    /**
     * Creates new form MainGUI
     */
    public MainGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bkgPanel = bkgPanel = new BKGPanel();
        triviaLabel = new javax.swing.JLabel();
        facebookConnect = new javax.swing.JButton();
        play = new javax.swing.JButton();
        options = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        triviaLabel.setFont(new java.awt.Font("Comic Sans MS", 2, 24)); // NOI18N
        triviaLabel.setText("<html><center>T r i v i a</center></html>");

        facebookConnect.setText("Connect to Facebook");
        facebookConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facebookConnectActionPerformed(evt);
            }
        });

        play.setText("Play");
        play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playActionPerformed(evt);
            }
        });

        options.setText("Options");
        options.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bkgPanelLayout = new javax.swing.GroupLayout(bkgPanel);
        bkgPanel.setLayout(bkgPanelLayout);
        bkgPanelLayout.setHorizontalGroup(
            bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bkgPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bkgPanelLayout.createSequentialGroup()
                        .addGroup(bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(options, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(triviaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 524, Short.MAX_VALUE)
                        .addComponent(facebookConnect))
                    .addGroup(bkgPanelLayout.createSequentialGroup()
                        .addComponent(play, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        bkgPanelLayout.setVerticalGroup(
            bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bkgPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(triviaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 268, Short.MAX_VALUE)
                .addComponent(play)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(options)
                    .addComponent(facebookConnect))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bkgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bkgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void optionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsActionPerformed
        opt.setVisible(true);
    }//GEN-LAST:event_optionsActionPerformed

    private void playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playActionPerformed

        final MainGUI main = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                QuestionGUI gui = new QuestionGUI(main, Question.generateQuestionSet(main.getDifficulty(), 20));
                gui.setVisible(true);
            }
        });
    }//GEN-LAST:event_playActionPerformed

    private void facebookConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facebookConnectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_facebookConnectActionPerformed

    public boolean isModifiable() {
        return modifiable;
    }

    public void setDifficulty(int d) {
        difficulty = d;
    }

    public void setQuestionHandling(int i) {
        questionHandling = i;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getQuestionHandling() {
        return questionHandling;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainGUI gui = new MainGUI();
                gui.setVisible(true);
            }
        });
    }
    
    private static class CButton {
        
        private static final int HORIZONTAL_MARGIN = 4;
        private static final int VERTICAL_MARGIN = 4;
        
        private int x, y, w, h;
        String text, icon;
        Color color = Color.ORANGE;
        
        public CButton(int x, int y, int w, int h, String text, String icon) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.text = text;
            this.icon = icon;
        }
        
        public CButton(int x, int y, int w, int h, String text) {
            this(x, y, w, h, text, null);
        }
        
        public CButton(int x, int y, int w, int h) {
            this(x, y, w, h, "    ", null);
        }
        
        public void drawButton(Graphics2D g) {
            int textw = 0;
            int texth = 0;
            FontMetrics fontMetric = g.getFontMetrics();
            for (char c : text.toCharArray()) {
                textw += fontMetric.charWidth(c);
            }
            texth = fontMetric.getHeight();
            textw = Math.max(textw + 2 * HORIZONTAL_MARGIN, w);
            texth = Math.max(texth + 2 * VERTICAL_MARGIN, h);
            g.setColor(color);
            g.drawRect(x, y, textw, texth);
           // g.drawString(text, x + HORIZONTAL_, TOP_ALIGNMENT);
        }
    }
    
    private static class BKGPanel extends JPanel {
        
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bkgPanel;
    private javax.swing.JButton facebookConnect;
    private javax.swing.JButton options;
    private javax.swing.JButton play;
    private javax.swing.JLabel triviaLabel;
    // End of variables declaration//GEN-END:variables
}
