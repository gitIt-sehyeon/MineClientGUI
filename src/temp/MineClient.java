package temp;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

class MineClient extends JFrame {
    static int inPort = 9999;
    static String address = "192.168.35.75";
    static public PrintWriter out;
    static public BufferedReader in;
    static int width = 0;
    static int num_mine = 0;
    static Map map;
    static int find = 0;
    static public Socket socket;

    public Container cont;
    public JPanel p0, p1, p2;
    public JTextField widthText, NumMineText;
    public JButton b_map;
    public JButton[] buttons;
    public ImageIcon bomb = new ImageIcon("bomb.png");;
    public ImageIcon normal = new ImageIcon("normal.png");
    public ImageIcon findd = new ImageIcon("find.png");

    JLabel total;
    int buttonSize;

    int numTry = 0;
    int numFind = 0;
    int restMine;

    public static void main(String[] args) {
        MineClient game = new MineClient();
    }

    public MineClient() {
        this.setTitle("MineClientGUI");
        this.setSize(1000, 1000);
        this.setLocation(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        cont = getContentPane();
        cont.setLayout(new FlowLayout());
        p0 = new JPanel();
        p0.setBackground(Color.black);
        p1 = new JPanel();
        p1.setBackground(Color.black);
        p1.setPreferredSize(new Dimension(400, 700));

        total = new JLabel("Try : " + numTry + "     Find : " + numFind + "     Rest of mine : " + restMine);
        total.setForeground(new Color(50, 150, 200));
        total.setFont(new Font("Consolas", Font.PLAIN, 25));

        p2 = new JPanel();
        p2.setLayout(new FlowLayout());
        p2.setBackground(Color.black);
        p2.setPreferredSize(new Dimension(400, 100));
        p2.add(total);

        widthText = new JTextField();
        widthText.setPreferredSize(new Dimension(450, 100));
        widthText.setFont(new Font("Consolas", Font.PLAIN, 35));
        widthText.setForeground(new Color(50, 150, 200));
        widthText.setBackground(Color.black);
        widthText.setCaretColor(Color.white);
        widthText.setText("Enter width");
        Border widthTextBorder = new LineBorder(new Color(50, 150, 200), 2);
        widthText.setBorder(widthTextBorder);

        NumMineText = new JTextField();
        NumMineText.setPreferredSize(new Dimension(450, 100));
        NumMineText.setFont(new Font("Consolas", Font.PLAIN, 35));
        NumMineText.setForeground(new Color(50, 150, 200));
        NumMineText.setBackground(Color.black);
        NumMineText.setCaretColor(new Color(50, 150, 200));
        NumMineText.setText("Enter num of Mine");
        NumMineText.setBorder(widthTextBorder);

        b_map = new JButton("gimme that freaking bomb");
        b_map.setBackground(Color.black);
        Border lineBorder = new LineBorder(new Color(50, 150, 200), 2);
        b_map.setBorder(lineBorder);
        b_map.setPreferredSize(new Dimension(350, 100));
        b_map.setForeground(new Color(50, 150, 200));
        b_map.setFont(new Font("Consolas", Font.PLAIN, 20));
        b_map.addActionListener(new MyActionListener0());

        p0.add(widthText);
        p0.add(NumMineText);
        p0.add(b_map);
        cont.setLayout(new BorderLayout());
        cont.add(p0, BorderLayout.NORTH);
        cont.add(p1, BorderLayout.CENTER);
        cont.add(p2, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    class MyActionListener0 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                width = Integer.parseInt(widthText.getText());
                num_mine = Integer.parseInt(NumMineText.getText());
                if (num_mine > width * width) {
                    showMessageDialog("You have to enter the number of mines within a certain range.", "Wrong input", JOptionPane.WARNING_MESSAGE);
                    widthText.setEnabled(true);
                    NumMineText.setEnabled(true);
                    widthText.setText("Enter width");
                    NumMineText.setText("Enter num of Mine");
                    return;
                }
                restMine = num_mine;
                widthText.setEnabled(false);
                NumMineText.setEnabled(false);
                b_map.setEnabled(false);
                total.setText("Try : " + numTry + "     Find : " + numFind + "     Rest of mine : " + restMine);

                socket = new Socket(address, inPort);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(width + "," + num_mine);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            map = new Map(width, num_mine);

            p1.setLayout(new GridLayout(width, width));
            buttons = new JButton[width * width];
            buttonSize = 400 / width;
            for (int i = 0; i < width * width; i++) {
                buttons[i] = new JButton(resizeIcon(normal, buttonSize, buttonSize));
                buttons[i].setText("" + i);
                buttons[i].setBackground(Color.black);
                buttons[i].setForeground(Color.black);
                buttons[i].addActionListener(new MyActionListener1());
                p1.add(buttons[i]);

                p1.revalidate();
                p1.repaint();
                pack();
            }
        }
    }

    class MyActionListener1 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            int i = Integer.parseInt(s);
            int x = i / width;
            int y = i % width;

            try {
                out.println(x + "," + y);
                String msg = in.readLine();
                int result = Integer.parseInt(msg);
                numTry++;
                if (result >= 0) {
                    numFind++;
                    restMine--;
                    JButton b = (JButton) e.getSource();

                    b.setIcon(resizeIcon(bomb, buttonSize, buttonSize));
                    map.updateMap(x, y);
                } else {
                    JButton b = (JButton) e.getSource();
                    b.setIcon(resizeIcon(findd, buttonSize, buttonSize));
                }
                total.setText("Try : " + numTry + "     Find : " + numFind + "     Rest of mine : " + restMine);
                pack();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }

    private void showMessageDialog(String message, String title, int messageType) {
        // Create a custom JPanel for the message
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 200));

        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Consolas", Font.PLAIN, 30)); // Set font size here
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(false);
        messageArea.setEditable(false);
        messageArea.setPreferredSize(new Dimension(380, 180));
        panel.add(messageArea);

        // Show the dialog with the custom panel
        JOptionPane.showMessageDialog(null, panel, title, messageType);
    }
}