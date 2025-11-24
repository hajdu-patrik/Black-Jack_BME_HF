package blackjack.gui;

import blackjack.logic.RoundResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A dedicated JFrame to display the statistics history using a JList.
 */
public class StatisticsFrame extends JFrame {
    private static final int MAX_RESULTS_TO_SHOW = 10;
    
    /**
     * Constructs the statistics window.
     * @param parent The parent frame.
     * @param history The list of results to display.
     */
    public StatisticsFrame(JFrame parent, List<RoundResult> history) {
        setTitle("Last " + MAX_RESULTS_TO_SHOW + " rounds statistics");
        setSize(600, 450);
        setMinimumSize(new Dimension(550, 350));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        int historySize = history.size();
        List<RoundResult> limitedHistory = history.subList(
                Math.max(0, historySize - MAX_RESULTS_TO_SHOW), 
                historySize
        );

        DefaultListModel<RoundResult> model = new DefaultListModel<>();
        model.addAll(limitedHistory);

        JList<RoundResult> resultList = new JList<>(model);
        resultList.setCellRenderer(new RoundResultRenderer());
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        resultList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                RoundResult selected = resultList.getSelectedValue();
                if (selected != null) {
                    showDetails(selected);
                }
            }
        });

        JButton closeButton = new JButton("Back to Game");
        closeButton.addActionListener(e -> dispose());
        
        add(new JScrollPane(resultList), BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);
    }
    
    private void showDetails(RoundResult result) {
        String details = String.format(
            "<html>" +
            "<h2>Round Result Details</h2>" +
            "<b>Winner:</b> %s<br><br>" +
            "<b>Player Score:</b> %d<br>" +
            "<b>Player Hand:</b> %s<br><br>" +
            "<b>Dealer Score:</b> %d<br>" +
            "<b>Dealer Hand:</b> %s" +
            "</html>",
            result.getWinner(),
            result.getPlayerScore(),
            String.join(", ", result.getPlayerHand()),
            result.getDealerScore(),
            String.join(", ", result.getDealerHand())
        );

        JOptionPane.showMessageDialog(this, details, "Round Details", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Custom ListCellRenderer to display RoundResult objects in a user-friendly format in the JList.
     */
    private class RoundResultRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof RoundResult result) {
                String winner = result.getWinner();
                String text = String.format("Round %d: %s | Player: %d (%s) | Dealer: %d (%s)",
                        index + 1,
                        winner,
                        result.getPlayerScore(),
                        result.getPlayerHand().size() + " cards",
                        result.getDealerScore(),
                        result.getDealerHand().size() + " cards"
                );
                renderer.setText(text);

                if (!isSelected) {
                    if (winner.contains("Player")) {
                        renderer.setBackground(new Color(150, 255, 150));
                    } else if (winner.contains("Dealer")) {
                        renderer.setBackground(new Color(255, 150, 150));
                    } else {
                        renderer.setBackground(new Color(255, 255, 150));
                    }
                }
            }
            return renderer;
        }
    }
}