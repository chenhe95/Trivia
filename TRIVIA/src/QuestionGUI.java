
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
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
public class QuestionGUI extends javax.swing.JFrame {

    private List<Question> questions;
    private MainGUI main;

    // given the index, what was the answer given?
    private HashMap<Integer, Integer> answers = new HashMap<Integer, Integer>();
    private int questionIndex = 0;

    /**
     * Creates new form QuestionGUI
     */
    public QuestionGUI(MainGUI main, List<Question> questions) {
        this.main = main;
        this.questions = questions;
        for (int i = 0; i < questions.size(); i++) {
            answers.put(i, Integer.MAX_VALUE);
        }
        initComponents();
        questionPrevious.setEnabled(false);
        questionIndexDisplay.setText("(0/" + questions.size() + ")");
        // assumes questions is not empty
        loadQuestion(questions.get(questionIndex));
    }
    
    private int questionCountLockedIn() {
        int counter = 0;
        for (int v : answers.values()) {
            if (v != Integer.MAX_VALUE) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bkgPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        questionSelect = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        questionText = new javax.swing.JTextArea();
        questionNext = new javax.swing.JButton();
        questionPrevious = new javax.swing.JButton();
        submit = new javax.swing.JButton();
        exit = new javax.swing.JButton();
        questionIndexDisplay = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setViewportView(questionSelect);

        questionText.setEditable(false);
        questionText.setColumns(20);
        questionText.setRows(5);
        jScrollPane2.setViewportView(questionText);

        questionNext.setText("Lock in Answer; Next Question");
        questionNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                questionNextActionPerformed(evt);
            }
        });

        questionPrevious.setText("Previous Question");
        questionPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                questionPreviousActionPerformed(evt);
            }
        });

        submit.setText("Submit");
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });

        exit.setText("Exit");
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });

        questionIndexDisplay.setText("        ");

        javax.swing.GroupLayout bkgPanelLayout = new javax.swing.GroupLayout(bkgPanel);
        bkgPanel.setLayout(bkgPanelLayout);
        bkgPanelLayout.setHorizontalGroup(
            bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bkgPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(bkgPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(questionNext, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(questionPrevious, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                            .addComponent(submit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(bkgPanelLayout.createSequentialGroup()
                        .addComponent(questionIndexDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        bkgPanelLayout.setVerticalGroup(
            bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bkgPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(bkgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bkgPanelLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(questionNext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(questionPrevious)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submit)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(bkgPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(questionIndexDisplay)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bkgPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bkgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitActionPerformed
        // if they click submit, they cannot go and change their questions later
        submit.setEnabled(false);
        questionNext.setEnabled(false);
        questionPrevious.setEnabled(false);
        int correct = 0;
        int total = 0;
        for (Entry<Integer, Integer> entry : answers.entrySet()) {
            if (questions.get(entry.getKey()).getAnswer() == entry.getValue()) {
                ++correct;
            }
            ++total;
        }
        String result = "You have answered " + ((100 * correct) / total) + " percent of questions correctly (" + correct + " / " + total + ")";

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ResultsGUI rGUI = new ResultsGUI(result);
                rGUI.setVisible(true);
            }
        });
    }//GEN-LAST:event_submitActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        // completely aborts the questions 
        dispose();
    }//GEN-LAST:event_exitActionPerformed

    private void questionNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_questionNextActionPerformed
        int selectedIndex = questionSelect.getSelectedIndex();
        if (questionIndex <= questions.size() - 1) {
            Question current = questions.get(questionIndex);
            if (current.getAnswer() == selectedIndex) {
                if (main.getQuestionHandling() == 1) {
                    displayMessage("YES!", "You have answered this question correctly.");
                }
            } else {
                if (main.getQuestionHandling() == 1) {
                    displayMessage("NO!", "You have answered this question incorrectly. The correct answer is " + current.getChoices().get(current.getAnswer()));
                }
            }
            answers.put(questionIndex, selectedIndex);
            questionIndexDisplay.setText("(" + questionCountLockedIn() + "/" + questions.size() + ")");
            if (questionIndex < questions.size() - 1) {
                loadQuestion(questions.get(++questionIndex));
                if (questionIndex > 0 && !questionPrevious.isEnabled()) {
                    questionPrevious.setEnabled(true);
                }
            } else {
                displayMessage("Information", "No more questions left! Go over previous questions if available or click submit to see statistics!");
            }
        }
    }//GEN-LAST:event_questionNextActionPerformed

    private void questionPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_questionPreviousActionPerformed
        if (questionIndex > 0 && main.getQuestionHandling() == 0) {
            loadQuestion(questions.get(--questionIndex));
        } else {
            displayMessage("Invalid Operation", "You cannot change the answer to a question after the answer to that question has already been revealed!");
        }
        if (questionIndex == 0 && questionPrevious.isEnabled()) {
            questionPrevious.setEnabled(false);
        }
        questionIndexDisplay.setText("(" + questionCountLockedIn() + "/" + questions.size() + ")");
    }//GEN-LAST:event_questionPreviousActionPerformed

    private void displayMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void loadQuestion(Question q) {
        DefaultListModel dlm = new DefaultListModel();
        for (String choices : q.getChoices()) {
            dlm.addElement(choices);
        }
        questionSelect.setModel(dlm);
        questionText.setText(q.getQuestion());
        questionSelect.setSelectedIndex(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bkgPanel;
    private javax.swing.JButton exit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel questionIndexDisplay;
    private javax.swing.JButton questionNext;
    private javax.swing.JButton questionPrevious;
    private javax.swing.JList questionSelect;
    private javax.swing.JTextArea questionText;
    private javax.swing.JButton submit;
    // End of variables declaration//GEN-END:variables
}
