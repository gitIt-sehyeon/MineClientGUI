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
    static String address = "192.168.35.225";
    static public PrintWriter out;
    static public BufferedReader in;
    static int width = 0;
    static int num_mine = 0;
    static Map map;
    static int find = 0;
    static public Socket socket;

    public Container cont;
    public JPanel p0, p1;
    public JTextField widthText, NumMineText;
    public JButton b_map;
    public JButton[] buttons;
    public ImageIcon bomb=new ImageIcon("bomb.png");;
    public ImageIcon normal = new ImageIcon("normal.png");
    public ImageIcon findd = new ImageIcon("find.png");
    int buttonSize; // assuming p1 is a square panel of 400x400

    public static void main(String[] args) {
        temp.MineClient game = new MineClient();
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
        Border lineBorder = new LineBorder(new Color(50,150,200), 2);
        b_map.setBorder(lineBorder);
        b_map.setPreferredSize(new Dimension(350,100));
        b_map.setForeground(new Color(50,150,200));
        b_map.setFont(new Font("Consolas", Font.PLAIN, 20));
        b_map.addActionListener(new MyActionListener0());

        p0.add(widthText);
        p0.add(NumMineText);
        p0.add(b_map);
        cont.setLayout(new BorderLayout());
        cont.add(p0, BorderLayout.NORTH);
        cont.add(p1, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    class MyActionListener0 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                width = Integer.parseInt(widthText.getText());
                num_mine = Integer.parseInt(NumMineText.getText());
                widthText.setEnabled(false);
                NumMineText.setEnabled(false);
                b_map.setEnabled(false);

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
                pack(); // 컴포넌트의 크기에 맞춰 프레임 크기 조정
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
                if (result >= 0) {
                    // System.out.println(" Find mine at ("+x+", "+y+")");
                    JButton b = (JButton) e.getSource();


                    b.setIcon(resizeIcon(bomb, buttonSize, buttonSize));
                    map.updateMap(x, y);
                } else {
                    // System.out.println(" No mine at ("+x+", "+y+")");
                    JButton b = (JButton) e.getSource();
                    b.setIcon(resizeIcon(findd, buttonSize,buttonSize));
                }

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

}