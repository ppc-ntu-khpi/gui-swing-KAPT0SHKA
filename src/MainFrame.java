import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainFrame extends JFrame {
    private JComboBox<String> comboBox1;
    private JButton buttonLoadFile;
    private JButton buttonShow;
    private JButton buttonAbout;
    private JTextArea textArea1;
    private JPanel mainPanel;

    public MainFrame() {
        // Initialize components
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        comboBox1 = new JComboBox<>();
        textArea1 = new JTextArea();
        textArea1.setEditable(false);

        buttonLoadFile = new JButton("Load File");
        buttonShow = new JButton("Show");
        buttonAbout = new JButton("About");

        // Add components to main panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(comboBox1);
        topPanel.add(buttonLoadFile);
        topPanel.add(buttonShow);
        topPanel.add(buttonAbout);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(textArea1), BorderLayout.CENTER);

        setContentPane(mainPanel);
        setTitle("Bank Client Info");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        buttonLoadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataFromFile();
            }
        });

        buttonShow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayCustomerData();
            }
        });

        buttonAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });

        // Add event listener to comboBox1 for item selection
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // On selection change, clear textArea1
                textArea1.setText("");
            }
        });
    }

    private void loadDataFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/test.dat"))) {
            String line;
            int lineNumber = 0;

            line = br.readLine();
            lineNumber++;

            int numCustomers = Integer.parseInt(line.trim());

            // Clear the combo box before loading new data
            comboBox1.removeAllItems();
            comboBox1.addItem("Select Customer");

            for (int i = 0; i < numCustomers; i++) {
                line = br.readLine();
                lineNumber++;

                if (line == null || line.trim().isEmpty()) {
                    System.err.println("Missing customer info at line " + lineNumber);
                    continue;
                }

                String[] customerInfo = line.split("\t");
                if (customerInfo.length < 3) {
                    System.err.println("Invalid customer info at line " + lineNumber + ": " + String.join(", ", customerInfo));
                    continue;
                }

                String firstName = customerInfo[0];
                String lastName = customerInfo[1];
                int numAccounts = Integer.parseInt(customerInfo[2]);

                // Add the customer to the combo box
                for (int j = 0; j < numAccounts; j++) {
                    line = br.readLine();
                    lineNumber++;

                    if (line == null || line.trim().isEmpty()) {
                        System.err.println("Missing account info for customer: " + firstName + " " + lastName + " at line " + lineNumber);
                        break;
                    }

                    String[] accountInfo = line.split("\t");
                    if (accountInfo.length < 3) {
                        System.err.println("Invalid account info for customer: " + firstName + " " + lastName + " at line " + lineNumber + ": " + String.join(", ", accountInfo));
                        continue;
                    }

                    char accountType = accountInfo[0].charAt(0);
                    String accountTypeString = "";
                    switch (accountType) {
                        case 'S':
                            accountTypeString = "Personal Savings";
                            break;
                        case 'C':
                            accountTypeString = "Checking";
                            break;
                        default:
                            System.err.println("Unknown account type at line " + lineNumber + ": " + accountType);
                            break;
                    }

                    // Add the customer with their account type to the combo box
                    comboBox1.addItem(firstName + " " + lastName + " (" + accountTypeString + ")");
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }



    private void displayCustomerData() {
        // Отримати вибраного користувача з комбінованого списку
        String selectedCustomer = (String) comboBox1.getSelectedItem();

        if (selectedCustomer != null && !selectedCustomer.equals("Select Customer")) {
            // Отримати ім'я та прізвище користувача з вибраного рядка
            String[] customerInfo = selectedCustomer.split("\\s+");
            String firstName = customerInfo[0];
            String lastName = customerInfo[1];

            // Очистити текстове поле перед виведенням нових даних
            textArea1.setText("");

            try (BufferedReader br = new BufferedReader(new FileReader("src/test.dat"))) {
                String line;
                int lineNumber = 0;

                line = br.readLine();
                lineNumber++;

                int numCustomers = Integer.parseInt(line.trim());

                boolean foundCustomer = false;

                for (int i = 0; i < numCustomers; i++) {
                    line = br.readLine();
                    lineNumber++;

                    if (line == null || line.trim().isEmpty()) {
                        System.err.println("Missing customer info at line " + lineNumber);
                        continue;
                    }

                    String[] customerData = line.split("\t");

                    // Перевіряємо, чи поточний користувач співпадає з обраним користувачем
                    if (customerData[0].equals(firstName) && customerData[1].equals(lastName)) {
                        foundCustomer = true;

                        int numAccounts = Integer.parseInt(customerData[2]);

                        // Виводимо дані користувача у текстове поле
                        textArea1.append("Customer: " + firstName + " " + lastName + "\n");

                        for (int j = 0; j < numAccounts; j++) {
                            line = br.readLine();
                            lineNumber++;

                            if (line == null || line.trim().isEmpty()) {
                                System.err.println("Missing account info for customer: " + firstName + " " + lastName + " at line " + lineNumber);
                                break;
                            }

                            String[] accountInfo = line.split("\t");

                            // Виводимо дані рахунку у текстове поле
                            textArea1.append("Account Type: " + accountInfo[0] + ", Balance: $" + accountInfo[1]);

                            if (accountInfo[0].equals("S")) {
                                textArea1.append(", Interest Rate: " + accountInfo[2] + "\n");
                            } else if (accountInfo[0].equals("C")) {
                                textArea1.append(", Overdraft: $" + accountInfo[2] + "\n");
                            }
                        }

                        // Завершуємо цикл, якщо знайдено потрібного користувача
                        break;
                    }
                }

                if (!foundCustomer) {
                    System.err.println("Customer not found: " + selectedCustomer);
                }

            } catch (IOException | NumberFormatException e) {
                System.err.println("Error reading the file: " + e.getMessage());
            }
        } else {
            // Повідомлення про помилку, якщо користувач не вибраний
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, "Bank Client Info Application\nDeveloped by Your Name", "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
