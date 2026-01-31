package proiect.facultate.oosd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ScientificCalculator extends JFrame {
    
    // Componente pentru calculatorul numeric
    private JTextField display;
    private double firstValue = 0;
    private String operator = "";
    private boolean startNewNumber = true;
    private double memory = 0;

    // Componente pentru calculatorul de date
    private JSpinner dateFromSpinner;
    private JSpinner dateToSpinner;
    private JTextField diffDetailedField;
    private JTextField diffDaysField;
    private JTextField workingDaysField;
    private JComboBox<String> dateCalcCombo;
    
    // Panouri pentru diferite moduri de calcul date
    private JPanel diffBetweenDatesPanel;
    private JPanel addSubtractDaysPanel;
    
    // Componente pentru adunare/scădere zile
    private JSpinner baseDateSpinner;
    private JSpinner daysToAddSpinner;
    private JComboBox<String> addSubtractCombo;
    private JTextField resultDateField;
    private JCheckBox excludeWeekendsCheckbox;

    public ScientificCalculator() {
        // Configurare Fereastră Principală
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // 1. Meniul (View, Edit, Help)
        setJMenuBar(createMenuBar());

        // 2. Zona de Afișaj (Nord)
        display = new JTextField("0");
        display.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(display, BorderLayout.NORTH);

        // 3. Panoul Central (Conține Calculatorul și Date Calculator)
        JPanel centralPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centralPanel.add(createNumericPanel());
        centralPanel.add(createDatePanel());
        add(centralPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.add(new JMenu("View"));
        mb.add(new JMenu("Edit"));
        mb.add(new JMenu("Help"));
        return mb;
    }

    private JPanel createNumericPanel() {
        JPanel mainNumeric = new JPanel(new BorderLayout(5, 5));
        mainNumeric.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Butoane Memorie
        JPanel memPanel = new JPanel(new GridLayout(1, 5, 2, 2));
        String[] mems = {"MC", "MR", "MS", "M+", "M-"};
        ActionListener memAction = new MemoryActionListener();
        for (String m : mems) {
            JButton btn = new JButton(m);
            btn.addActionListener(memAction);
            memPanel.add(btn);
        }
        
        // Tastatura principală - layout corect conform imaginii
        JPanel buttonsPanel = new JPanel(new GridLayout(6, 5, 2, 2));
        String[] labels = {
            "←", "CE", "C", "±", "√",
            "7", "8", "9", "/", "%",
            "4", "5", "6", "*", "1/x",
            "1", "2", "3", "-", "",
            "0", "", ".", "+", "="
        };

        ActionListener numAction = new NumericActionListener();
        for (String l : labels) {
            if (l.isEmpty()) {
                buttonsPanel.add(new JLabel("")); // Spațiu gol
            } else {
                JButton btn = new JButton(l);
                btn.addActionListener(numAction);
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                buttonsPanel.add(btn);
            }
        }

        mainNumeric.add(memPanel, BorderLayout.NORTH);
        mainNumeric.add(buttonsPanel, BorderLayout.CENTER);
        return mainNumeric;
    }

    private JPanel createDatePanel() {
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        datePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        datePanel.add(new JLabel("Select the date calculation you want"));
        datePanel.add(Box.createVerticalStrut(5));
        
        // ComboBox pentru selectare mod calcul
        dateCalcCombo = new JComboBox<>(new String[]{
            "Calculate the difference between two dates",
            "Add or subtract days from a date"
        });
        dateCalcCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        dateCalcCombo.addActionListener(e -> switchDateCalculationMode());
        datePanel.add(dateCalcCombo);
        
        datePanel.add(Box.createVerticalStrut(20));

        // Container pentru panouri switching
        JPanel cardPanel = new JPanel(new CardLayout());
        
        // Panou 1: Diferență între date
        diffBetweenDatesPanel = createDiffBetweenDatesPanel();
        cardPanel.add(diffBetweenDatesPanel, "diff");
        
        // Panou 2: Adunare/Scădere zile
        addSubtractDaysPanel = createAddSubtractDaysPanel();
        cardPanel.add(addSubtractDaysPanel, "addsub");
        
        datePanel.add(cardPanel);

        return datePanel;
    }

    private JPanel createDiffBetweenDatesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Zona de selecție date
        JPanel gridDates = new JPanel(new GridLayout(2, 2, 5, 5));
        dateFromSpinner = new JSpinner(new SpinnerDateModel());
        dateToSpinner = new JSpinner(new SpinnerDateModel());
        dateFromSpinner.setEditor(new JSpinner.DateEditor(dateFromSpinner, "dd.MM.yyyy"));
        dateToSpinner.setEditor(new JSpinner.DateEditor(dateToSpinner, "dd.MM.yyyy"));

        gridDates.add(new JLabel("From")); 
        gridDates.add(new JLabel("To"));
        gridDates.add(dateFromSpinner); 
        gridDates.add(dateToSpinner);
        gridDates.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.add(gridDates);

        panel.add(Box.createVerticalStrut(20));

        JLabel lblDetailed = new JLabel("Difference (years, months, weeks, days)");
        panel.add(lblDetailed);
        panel.add(Box.createVerticalStrut(5));
        
        diffDetailedField = new JTextField();
        diffDetailedField.setEditable(false);
        diffDetailedField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(diffDetailedField);

        panel.add(Box.createVerticalStrut(15));

        JLabel lblDays = new JLabel("Difference (days)");
        panel.add(lblDays);
        panel.add(Box.createVerticalStrut(5));
        
        diffDaysField = new JTextField();
        diffDaysField.setEditable(false);
        diffDaysField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(diffDaysField);

        panel.add(Box.createVerticalStrut(15));

        JLabel lblWorkingDays = new JLabel("Working days (excluding weekends)");
        panel.add(lblWorkingDays);
        panel.add(Box.createVerticalStrut(5));
        
        workingDaysField = new JTextField();
        workingDaysField.setEditable(false);
        workingDaysField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(workingDaysField);

        panel.add(Box.createVerticalStrut(20));
        
        JButton calcBtn = new JButton("Calculate");
        calcBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcBtn.addActionListener(e -> calculateDateDiff());
        panel.add(calcBtn);
        
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createAddSubtractDaysPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Selectare dată de bază
        JLabel lblBaseDate = new JLabel("Base date");
        panel.add(lblBaseDate);
        panel.add(Box.createVerticalStrut(5));
        
        baseDateSpinner = new JSpinner(new SpinnerDateModel());
        baseDateSpinner.setEditor(new JSpinner.DateEditor(baseDateSpinner, "dd.MM.yyyy"));
        baseDateSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(baseDateSpinner);

        panel.add(Box.createVerticalStrut(20));

        // Selectare operație (Add/Subtract)
        JPanel opPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addSubtractCombo = new JComboBox<>(new String[]{"Add", "Subtract"});
        opPanel.add(addSubtractCombo);
        opPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(opPanel);

        panel.add(Box.createVerticalStrut(10));

        // Număr de zile
        JLabel lblDays = new JLabel("Number of days");
        panel.add(lblDays);
        panel.add(Box.createVerticalStrut(5));
        
        daysToAddSpinner = new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));
        daysToAddSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(daysToAddSpinner);

        panel.add(Box.createVerticalStrut(15));

        // Checkbox pentru excludere weekend
        excludeWeekendsCheckbox = new JCheckBox("Exclude weekends (working days only)");
        excludeWeekendsCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(excludeWeekendsCheckbox);

        panel.add(Box.createVerticalStrut(20));

        // Rezultat
        JLabel lblResult = new JLabel("Result date");
        panel.add(lblResult);
        panel.add(Box.createVerticalStrut(5));
        
        resultDateField = new JTextField();
        resultDateField.setEditable(false);
        resultDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(resultDateField);

        panel.add(Box.createVerticalStrut(20));
        
        JButton calcBtn = new JButton("Calculate");
        calcBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        calcBtn.addActionListener(e -> calculateAddSubtractDays());
        panel.add(calcBtn);
        
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void switchDateCalculationMode() {
        int selectedIndex = dateCalcCombo.getSelectedIndex();
        CardLayout cl = (CardLayout) diffBetweenDatesPanel.getParent().getLayout();
        
        if (selectedIndex == 0) {
            cl.show(diffBetweenDatesPanel.getParent(), "diff");
        } else {
            cl.show(diffBetweenDatesPanel.getParent(), "addsub");
        }
    }

    // Logica funcțională pentru memorie
    private class MemoryActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            try {
                double currentValue = Double.parseDouble(display.getText().replace(",", "."));
                
                switch (cmd) {
                    case "MC":
                        memory = 0;
                        break;
                    case "MR":
                        display.setText(String.valueOf(memory).replace(".", ","));
                        startNewNumber = true;
                        break;
                    case "MS":
                        memory = currentValue;
                        break;
                    case "M+":
                        memory += currentValue;
                        break;
                    case "M-":
                        memory -= currentValue;
                        break;
                }
            } catch (NumberFormatException ex) {
                // Ignoră erori la parsare
            }
        }
    }

    // Logica funcțională pentru cifre și operații
    private class NumericActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            
            try {
                // Introducere cifre și virgulă
                if ("0123456789".contains(cmd)) {
                    if (startNewNumber) {
                        display.setText(cmd);
                        startNewNumber = false;
                    } else {
                        display.setText(display.getText() + cmd);
                    }
                } 
                else if (".".equals(cmd)) {
                    if (startNewNumber) {
                        display.setText("0,");
                        startNewNumber = false;
                    } else if (!display.getText().contains(",")) {
                        display.setText(display.getText() + ",");
                    }
                }
                // Operatori binari
                else if ("+-*/".contains(cmd)) {
                    firstValue = Double.parseDouble(display.getText().replace(",", "."));
                    operator = cmd;
                    startNewNumber = true;
                } 
                // Egal
                else if ("=".equals(cmd)) {
                    if (!operator.isEmpty()) {
                        double secondValue = Double.parseDouble(display.getText().replace(",", "."));
                        double res = 0;
                        
                        switch (operator) {
                            case "+":
                                res = firstValue + secondValue;
                                break;
                            case "-":
                                res = firstValue - secondValue;
                                break;
                            case "*":
                                res = firstValue * secondValue;
                                break;
                            case "/":
                                if (secondValue != 0) {
                                    res = firstValue / secondValue;
                                } else {
                                    display.setText("Error");
                                    startNewNumber = true;
                                    operator = "";
                                    return;
                                }
                                break;
                        }
                        
                        display.setText(formatNumber(res));
                        startNewNumber = true;
                        operator = "";
                    }
                } 
                // Clear
                else if ("C".equals(cmd)) {
                    display.setText("0");
                    startNewNumber = true;
                    operator = "";
                    firstValue = 0;
                }
                // Clear Entry
                else if ("CE".equals(cmd)) {
                    display.setText("0");
                    startNewNumber = true;
                }
                // Backspace
                else if ("←".equals(cmd)) {
                    String current = display.getText();
                    if (current.length() > 1) {
                        display.setText(current.substring(0, current.length() - 1));
                    } else {
                        display.setText("0");
                        startNewNumber = true;
                    }
                }
                // Schimbă semnul
                else if ("±".equals(cmd)) {
                    double val = Double.parseDouble(display.getText().replace(",", "."));
                    display.setText(formatNumber(-val));
                }
                // Rădăcină pătrată
                else if ("√".equals(cmd)) {
                    double val = Double.parseDouble(display.getText().replace(",", "."));
                    if (val >= 0) {
                        display.setText(formatNumber(Math.sqrt(val)));
                    } else {
                        display.setText("Error");
                    }
                    startNewNumber = true;
                }
                // Procent
                else if ("%".equals(cmd)) {
                    double val = Double.parseDouble(display.getText().replace(",", "."));
                    display.setText(formatNumber(val / 100));
                    startNewNumber = true;
                }
                // Reciproc (1/x)
                else if ("1/x".equals(cmd)) {
                    double val = Double.parseDouble(display.getText().replace(",", "."));
                    if (val != 0) {
                        display.setText(formatNumber(1 / val));
                    } else {
                        display.setText("Error");
                    }
                    startNewNumber = true;
                }
                
            } catch (NumberFormatException ex) {
                display.setText("Error");
                startNewNumber = true;
            }
        }
    }

    // Funcție auxiliară pentru formatarea numerelor
    private String formatNumber(double num) {
        // Elimină zerourile inutileee
        if (num == (long) num) {
            return String.valueOf((long) num);
        } else {
            return String.valueOf(num).replace(".", ",");
        }
    }

    private void calculateDateDiff() {
        try {
            Date d1 = (Date) dateFromSpinner.getValue();
            Date d2 = (Date) dateToSpinner.getValue();
            LocalDate start = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Period period = Period.between(start, end);
            long totalDays = ChronoUnit.DAYS.between(start, end);
            
            // Calculează zilele lucrătoare
            long workingDays = calculateWorkingDays(start, end);
            
            // Calculează săptămânile corect
            int weeks = period.getDays() / 7;
            int remainingDays = period.getDays() % 7;
            
            String detailedDiff = "";
            if (period.getYears() != 0) {
                detailedDiff += Math.abs(period.getYears()) + " years, ";
            }
            if (period.getMonths() != 0) {
                detailedDiff += Math.abs(period.getMonths()) + " months, ";
            }
            if (weeks != 0) {
                detailedDiff += Math.abs(weeks) + " weeks";
            }
            if (remainingDays != 0) {
                if (!detailedDiff.isEmpty() && !detailedDiff.endsWith(", ")) {
                    detailedDiff += ", ";
                }
                detailedDiff += Math.abs(remainingDays) + " days";
            }
            
            // Elimină virgula finală dacă există
            if (detailedDiff.endsWith(", ")) {
                detailedDiff = detailedDiff.substring(0, detailedDiff.length() - 2);
            }
            
            if (detailedDiff.isEmpty()) {
                detailedDiff = "0 days";
            }
            
            diffDetailedField.setText(detailedDiff);
            diffDaysField.setText(Math.abs(totalDays) + " days");
            workingDaysField.setText(Math.abs(workingDays) + " working days");
            
        } catch (Exception ex) {
            diffDetailedField.setText("Error");
            diffDaysField.setText("Error");
            workingDaysField.setText("Error");
        }
    }

    private void calculateAddSubtractDays() {
        try {
            Date baseDate = (Date) baseDateSpinner.getValue();
            LocalDate base = baseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            int daysToAdd = (Integer) daysToAddSpinner.getValue();
            String operation = (String) addSubtractCombo.getSelectedItem();
            boolean excludeWeekends = excludeWeekendsCheckbox.isSelected();
            
            // Inversează semnul dacă operația este "Subtract"
            if ("Subtract".equals(operation)) {
                daysToAdd = -daysToAdd;
            }
            
            LocalDate result;
            
            if (excludeWeekends) {
                // Adaugă/scade doar zile lucrătoare
                result = addWorkingDays(base, daysToAdd);
            } else {
                // Adaugă/scade zile normale
                result = base.plusDays(daysToAdd);
            }
            
            // Formatare rezultat
            java.time.format.DateTimeFormatter formatter = 
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
            resultDateField.setText(result.format(formatter));
            
        } catch (Exception ex) {
            resultDateField.setText("Error");
        }
    }

    // Calculează numărul de zile lucrătoare între două date
    private long calculateWorkingDays(LocalDate start, LocalDate end) {
        // Asigură-te că start este înainte de end
        LocalDate from = start.isBefore(end) ? start : end;
        LocalDate to = start.isBefore(end) ? end : start;
        
        long workingDays = 0;
        LocalDate current = from;
        //s
        while (!current.isAfter(to)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            current = current.plusDays(1);
        }
        
        return workingDays;
    }

    // Adaugă un număr specific de zile lucrătoare la o dată
    private LocalDate addWorkingDays(LocalDate start, int workingDaysToAdd) {
        LocalDate current = start;
        int daysAdded = 0;
        int direction = workingDaysToAdd > 0 ? 1 : -1;
        int targetDays = Math.abs(workingDaysToAdd);
        
        while (daysAdded < targetDays) {
            current = current.plusDays(direction);
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            
            // Numără doar zilele lucrătoare
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                daysAdded++;
            }
        }
        
        return current;
    }

    public static void main(String[] args) {
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch(Exception e) {}
        SwingUtilities.invokeLater(() -> new ScientificCalculator());
    }
}