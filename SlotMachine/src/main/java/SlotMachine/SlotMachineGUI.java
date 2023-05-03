package SlotMachine;

import javax.swing.*;
import User.User;
import SlotMachine.SlotMachine;
import java.awt.*;
import java.util.Collection;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.InputStreamReader;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SlotMachineGUI {

    public static JLabel userMoneyLabel;
    public static JLabel userBetLabel;
    public static JLabel userTotalBetLabel;

    public static JSONArray readSymbolsJSON() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(SlotMachineGUI.class.getResourceAsStream("/symbols.json")))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(content.toString());
        return jsonObject.getJSONArray("symbols");
    }

    public static ImageIcon[] loadImages(int width, int height, JSONArray symbols) {
        ImageIcon[] images = new ImageIcon[symbols.length()];

        for (int i = 0; i < symbols.length(); i++) {
            String imageUrl = symbols.getJSONObject(i).getString("image_url");
            ImageIcon imageIcon = new ImageIcon(SlotMachineGUI.class.getResource(imageUrl));
            Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            images[i] = new ImageIcon(image);
        }

        return images;
    }

    public static void updateUserMoneyDisplay(User user) {
        if (userMoneyLabel != null) {
            userMoneyLabel.setText("Money: " + user.getMoney());
        }
    }
    public static void addClickEffect(JLabel label) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                label.setLocation(label.getX(), label.getY() + 4);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                label.setLocation(label.getX(), label.getY() - 4);
            }
        });
    }
    public static void createAndShowGUI(User mainUser, Collection<Column> columns) {
        // Load symbols from the symbols.json file
        JSONArray symbols = readSymbolsJSON();
        ImageIcon[] images = loadImages(90, 90, symbols);

        // Creating the main window
        JFrame frame = new JFrame("Slot Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);


        // Adding the main panel with a background image
        JPanel mainPanel = new JPanel() {
            ImageIcon imageIcon = new ImageIcon(SlotMachineGUI.class.getResource("/images/slotMachine.png"));
            Image image = imageIcon.getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        frame.add(mainPanel);

        createLabelToDisplayUserMoney(mainUser, mainPanel);


        // Create a 2D array of JLabel to store the images
        JLabel[][] imageLabels = new JLabel[5][3];
        GridBagConstraints constraints = new GridBagConstraints();

        createAllSymbol(mainPanel, constraints, imageLabels, images, columns);

        createAllButtonWithImages(mainUser, mainPanel, constraints);


        // Displaying the window
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static void createAllSymbol(JPanel mainPanel , GridBagConstraints constraints, JLabel[][] imageLabels , ImageIcon[] images, Collection<Column> columns) {
        Random random = new Random();

        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 3; row++) {
                ImageIcon imageIcon = images[random.nextInt(images.length)];
                imageLabels[col][row] = new JLabel(imageIcon);

                constraints.gridx = col;
                constraints.gridy = row;

                if (row == 0) {
                    constraints.anchor = GridBagConstraints.CENTER;
                    constraints.insets = new Insets(70, 30, 20, 30); // top, left, bottom, right
                } else if (row == 1) {
                    constraints.anchor = GridBagConstraints.CENTER;
                    constraints.insets = new Insets(20, 30, 20, 30);
                } else {
                    constraints.anchor = GridBagConstraints.CENTER;
                    constraints.insets = new Insets(20, 30, 20, 30);
                }
                constraints.gridy += 1;

                if (col == 4) {
                    constraints.insets.right = 35;
                } else {
                    constraints.insets.right = 35;
                }

                if (col == 0) {
                    constraints.insets.left = 30;
                } else if (col == 1) {
                    constraints.insets.left = 30;
                } else if (col == 2) {
                    constraints.insets.left = 30;
                } else if (col == 3) {
                    constraints.insets.left = 30;
                } else {
                    constraints.insets.left = 30;
                }

                mainPanel.add(imageLabels[col][row], constraints);
            }
        }
    }
    public static void createAllButtonWithImages(User mainUser, JPanel mainPanel, GridBagConstraints constraints) {
        // Create buttons with images
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridwidth = 2;
        buttonPanel.setOpaque(false);

        // Spin button
        ImageIcon spinIcon = new ImageIcon(SlotMachineGUI.class.getResource("/images/spin.png"));
        JLabel spinLabel = new JLabel(spinIcon);
        constraints.gridx = 2;
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.insets = new Insets(0, 13, 20, 0);
        mainPanel.add(spinLabel, constraints);
        addClickEffect(spinLabel);

        spinLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainUser.totalBetMonney(mainUser.getMoneyBet());
                userTotalBetLabel.setText("" + mainUser.getTotalBet());
            }
        });

        // Less bet button
        ImageIcon lessIcon = new ImageIcon(SlotMachineGUI.class.getResource("/images/less.png"));
        JLabel autoSpinLabel = new JLabel(lessIcon);
        constraints.gridx = 3;
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.insets = new Insets(0, 55, 51, 0);
        mainPanel.add(autoSpinLabel, constraints);
        addClickEffect(autoSpinLabel);

        autoSpinLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainUser.betLessMoney();
                System.out.println(mainUser.getMoneyBet());
                userBetLabel.setText("" + mainUser.getMoneyBet());
            }
        });

        // More bet button
        ImageIcon moreIcon = new ImageIcon(SlotMachineGUI.class.getResource("/images/more.png"));
        JLabel maxBetLabel = new JLabel(moreIcon);
        constraints.gridx = 4;
        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.insets = new Insets(0, 25, 51, 0);
        mainPanel.add(maxBetLabel, constraints);
        addClickEffect(maxBetLabel);

        maxBetLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainUser.betMoreMoney();
                userBetLabel.setText("" + mainUser.getMoneyBet());
            }
        });

        // Adding the buttons panel to the main panel
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(buttonPanel, constraints);
    }

    // Adding the action to the Spin button (now Spin label)
    public static void createLabelToDisplayUserMoney(User mainUser, JPanel mainPanel) {
        // Add the JLabel to display the user's money
        JPanel userMoneyPanel = new JPanel();
        userMoneyPanel.setOpaque(false);

        userMoneyLabel = new JLabel();
        userMoneyLabel.setFont(new Font("Arial", Font.BOLD, 22));
        userMoneyLabel.setForeground(Color.WHITE);
        userMoneyLabel.setText("" + mainUser.getMoney());

        userMoneyPanel.add(userMoneyLabel);
        GridBagConstraints userMoneyPanelConstraints = new GridBagConstraints();
        userMoneyPanelConstraints.gridx = 0;
        userMoneyPanelConstraints.gridy = 5;
        userMoneyPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        userMoneyPanelConstraints.insets = new Insets(110, 25, 0, 0);
        mainPanel.add(userMoneyPanel, userMoneyPanelConstraints);

        // Add the JLabel to display the user's bet money
        JPanel userBetPanel = new JPanel();
        userBetPanel.setOpaque(false);

        userBetLabel = new JLabel();
        userBetLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userBetLabel.setForeground(Color.WHITE);
        userBetLabel.setText("" + mainUser.getMoneyBet());

        userBetPanel.add(userBetLabel);
        GridBagConstraints userBetPanelConstraints = new GridBagConstraints();
        userBetPanelConstraints.gridx = 0;
        userBetPanelConstraints.gridy = 5;
        userBetPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        userBetPanelConstraints.insets = new Insets(48, 43, 0, 0);
        mainPanel.add(userBetPanel, userBetPanelConstraints);

        JPanel userTotalBetPanel = new JPanel();
        userTotalBetPanel.setOpaque(false);

        //Add the JLabel to display the user's money total bet
        userTotalBetLabel = new JLabel();
        userTotalBetLabel.setFont(new Font("Arial", Font.BOLD, 18));
        userTotalBetLabel.setForeground(Color.WHITE);
        userTotalBetLabel.setText("" + mainUser.getTotalBet());

        userTotalBetPanel.add(userTotalBetLabel);
        GridBagConstraints userTotalBetPanelConstraints = new GridBagConstraints();
        userTotalBetPanelConstraints.gridx = 1;
        userTotalBetPanelConstraints.gridy = 5;
        userTotalBetPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        userTotalBetPanelConstraints.insets = new Insets(48, 51, 0, 0);
        mainPanel.add(userTotalBetPanel, userTotalBetPanelConstraints);
    }
}

