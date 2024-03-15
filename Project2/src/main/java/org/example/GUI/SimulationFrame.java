package org.example.GUI;

import org.example.BusinessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationFrame extends JFrame {
    private JTextArea textArea;
    public JTextField timeLimitTextField;
    public JTextField maxServiceTimeTextField;
    public JTextField minServiceTimeTextField;
    public JTextField maxArrivalTimeTextField;
    public JTextField minArrivalTimeTextField;
    public JTextField numberOfServersTextField;
    public JTextField numberOfClientsTextField;
    private JButton startButton;

    public SimulationFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Simulation Manager");
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 10, 5, 10);

        // Time Limit
        JLabel timeLimitLabel = new JLabel("Time Limit:");
        timeLimitLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(timeLimitLabel, constraints);

        timeLimitTextField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(timeLimitTextField, constraints);

        // Min Processing Time
        JLabel minProcessingTimeLabel = new JLabel("Min Service Time:");
        minProcessingTimeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(minProcessingTimeLabel, constraints);

        minServiceTimeTextField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(minServiceTimeTextField, constraints);

        // Max Processing Time
        JLabel maxProcessingTimeLabel = new JLabel("Max Service Time:");
        maxProcessingTimeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(maxProcessingTimeLabel, constraints);

        maxServiceTimeTextField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(maxServiceTimeTextField, constraints);

        // Min Arrival Time
        JLabel minArrivalTimeLabel = new JLabel("Min Arrival Time:");
        minArrivalTimeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(minArrivalTimeLabel, constraints);

        minArrivalTimeTextField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(minArrivalTimeTextField, constraints);

        // Max Arrival Time
        JLabel maxArrivalTimeLabel = new JLabel("Max Arrival Time:");
        maxArrivalTimeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(maxArrivalTimeLabel, constraints);

        maxArrivalTimeTextField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(maxArrivalTimeTextField, constraints);


        // Number of Servers
        JLabel numberOfServersLabel = new JLabel("Number of Servers:");
        numberOfServersLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(numberOfServersLabel, constraints);

        numberOfServersTextField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(numberOfServersTextField, constraints);

        // Number of Clients
        JLabel numberOfClientsLabel = new JLabel("Number of Clients:");
        numberOfClientsLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 6;
        panel.add(numberOfClientsLabel, constraints);

        numberOfClientsTextField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(numberOfClientsTextField, constraints);

        startButton = new JButton("Start Simulation");
        startButton.setFont(new Font("Arial", Font.PLAIN, 15));
        constraints.gridx = 0;
        constraints.gridy = 7;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    SimulationManager simulationManager = new SimulationManager(SimulationFrame.this);
                    Thread simulationThread = new Thread(simulationManager);
                    simulationThread.start();
                } else {
                    JOptionPane.showMessageDialog(SimulationFrame.this, "Please enter valid input values.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(startButton, constraints);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.BOLD, 15));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);


        setVisible(true);
    }

    public boolean validateInput() {
        boolean isValid = true;

        if (timeLimitTextField.getText().isEmpty() ||
                maxServiceTimeTextField.getText().isEmpty() ||
                minServiceTimeTextField.getText().isEmpty() ||
                maxArrivalTimeTextField.getText().isEmpty() ||
                minArrivalTimeTextField.getText().isEmpty() ||
                numberOfServersTextField.getText().isEmpty() ||
                numberOfClientsTextField.getText().isEmpty()) {
            isValid = false;
        }

        if (!timeLimitTextField.getText().matches("\\d+") ||
                !maxServiceTimeTextField.getText().matches("\\d+") ||
                !minServiceTimeTextField.getText().matches("\\d+") ||
                !maxArrivalTimeTextField.getText().matches("\\d+") ||
                !minArrivalTimeTextField.getText().matches("\\d+") ||
                !numberOfServersTextField.getText().matches("\\d+") ||
                !numberOfClientsTextField.getText().matches("\\d+")) {
            isValid = false;
        }
        return isValid;
    }

    public boolean allFieldsCompleted() {
        return !timeLimitTextField.getText().isEmpty() &&
                !maxServiceTimeTextField.getText().isEmpty() &&
                !minServiceTimeTextField.getText().isEmpty() &&
                !maxArrivalTimeTextField.getText().isEmpty() &&
                !minArrivalTimeTextField.getText().isEmpty() &&
                !numberOfServersTextField.getText().isEmpty() &&
                !numberOfClientsTextField.getText().isEmpty();
    }


    public void append(String text) {
        textArea.append(text);
    }

    public String getText() {
        return textArea.getText();
    }

}