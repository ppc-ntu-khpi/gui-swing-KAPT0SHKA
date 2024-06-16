import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Main class for the SWINGDemo application.
 *
 * Author: Alexander 'Taurus' Babich
 */
public class SWINGDemo {

    private final JEditorPane log;
    private final JButton show;
    private final JComboBox<String> clients;

    public SWINGDemo() {
        log = new JEditorPane("text/html", "");
        log.setPreferredSize(new Dimension(250, 150));
        show = new JButton("Show");
        clients = new JComboBox<>();
        loadCustomersFromFile("src/test.dat");

        for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
            clients.addItem(Bank.getCustomer(i).getLastName() + ", " + Bank.getCustomer(i).getFirstName());
        }
    }

    private void launchFrame() {
        JFrame frame = new JFrame("MyBank clients");
        frame.setLayout(new BorderLayout());
        JPanel cpane = new JPanel();
        cpane.setLayout(new GridLayout(1, 2));

        cpane.add(clients);
        cpane.add(show);
        frame.add(cpane, BorderLayout.NORTH);
        frame.add(log, BorderLayout.CENTER);

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer current = Bank.getCustomer(clients.getSelectedIndex());
                String accType = current.getAccount(0) instanceof CheckingAccount ? "Checking" : "Savings";
                String custInfo = "<br>&nbsp;<b><span style=\"font-size:2em;\">" + current.getLastName() + ", " +
                        current.getFirstName() + "</span><br><hr>" +
                        "&nbsp;<b>Acc Type: </b>" + accType +
                        "<br>&nbsp;<b>Balance: <span style=\"color:red;\">$" + current.getAccount(0).getBalance() + "</span></b>";
                log.setText(custInfo);
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SWINGDemo demo = new SWINGDemo();
        demo.launchFrame();
    }

    private void loadCustomersFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            line = br.readLine();
            lineNumber++;

            int numCustomers = Integer.parseInt(line.trim());

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

                Bank.addCustomer(firstName, lastName);
                Customer customer = Bank.getCustomer(Bank.getNumberOfCustomers() - 1);

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
                    double balance = Double.parseDouble(accountInfo[1]);

                    switch (accountType) {
                        case 'S':
                            double interestRate = Double.parseDouble(accountInfo[2]);
                            customer.addAccount(new SavingsAccount(balance, interestRate));
                            break;
                        case 'C':
                            double overdraft = Double.parseDouble(accountInfo[2]);
                            customer.addAccount(new CheckingAccount(balance, overdraft));
                            break;
                        default:
                            System.err.println("Unknown account type at line " + lineNumber + ": " + accountType);
                            break;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}