import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;

public class Minesweeper extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;
  JFrame frame;
  JMenu gameMenu, controlsMenu;
  JMenuBar mb;
  JToggleButton[][] togglers;
  JPanel gamePanel, topBar;
  JButton smileButton;
  JLabel timerLabel, flagLabel;
  JRadioButtonMenuItem beginnerItem, intermediateItem, expertItem;
  JMenuItem controlsItem1, controlsItem2, controlsItem3, restartItem;
  ImageIcon flag, mine, block, empty, one, two, three, four, five, six, seven, eight, smile, lose, cool, scare;

  Timer timer;
  int elapsedSeconds = 0;

  boolean firstMove = true, mouseListenerIsActive = true;
  int height = 9, width = 9, mines = 10, mineCounter = 0, flags = 0;
  int scale1 = 40, scale2 = 22, scale3 = 22;

  public Minesweeper() {
    flag = new ImageIcon("assets/flagged.png");
    mine = new ImageIcon("assets/mine.png");
    block = new ImageIcon("assets/block.png");
    empty = new ImageIcon("assets/empty.png");
    one = new ImageIcon("assets/one.png");
    two = new ImageIcon("assets/two.png");
    three = new ImageIcon("assets/three.png");
    four = new ImageIcon("assets/four.png");
    five = new ImageIcon("assets/five.png");
    six = new ImageIcon("assets/six.png");
    seven = new ImageIcon("assets/seven.png");
    eight = new ImageIcon("assets/eight.png");
    smile = new ImageIcon("assets/smile.png");
    lose = new ImageIcon("assets/lose.png");
    cool = new ImageIcon("assets/cool.png");
    scare = new ImageIcon("assets/scare.png");

    setImages(scale1);

    frame = new JFrame("Mine Sweeper");

    mb = new JMenuBar();

    gameMenu = new JMenu("Game");
    beginnerItem = new JRadioButtonMenuItem("Beginner", true);
    intermediateItem = new JRadioButtonMenuItem("Intermediate");
    expertItem = new JRadioButtonMenuItem("Expert");
    controlsItem1 = new JMenuItem("Left-click an empty square to reveal it.");
    controlsItem2 = new JMenuItem("Right-click an empty square to flag it.");
    controlsItem3 = new JMenuItem("Midde-click a number to reveal its adjacent squares.");
    restartItem = new JMenuItem("Restart");
    topBar = new JPanel();

    smileButton = new JButton(smile);
    smileButton.addActionListener(this);

    timerLabel = new JLabel("000");
    timerLabel.setForeground(Color.RED);
    timerLabel.setBackground(Color.BLACK);
    timerLabel.setOpaque(true);

    flagLabel = new JLabel("000");
    flagLabel.setForeground(Color.RED);
    flagLabel.setBackground(Color.BLACK);
    flagLabel.setOpaque(true);

    try {
      File fontFile = new File("assets/DS-DIGIB.TTF");
      Font ds_digital = Font.createFont(Font.TRUETYPE_FONT, fontFile);
      ds_digital = ds_digital.deriveFont(Font.PLAIN, 48);
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      ge.registerFont(ds_digital);
      timerLabel.setFont(ds_digital);
      flagLabel.setFont(ds_digital);
    } catch (FontFormatException | IOException e) {
    }

    topBar.add(flagLabel);
    topBar.add(Box.createRigidArea(new Dimension(80, 0)));
    topBar.add(smileButton);
    topBar.add(Box.createRigidArea(new Dimension(80, 0)));
    topBar.add(timerLabel);

    frame.add(topBar, BorderLayout.NORTH);

    beginnerItem.addActionListener(this);
    intermediateItem.addActionListener(this);
    expertItem.addActionListener(this);
    restartItem.addActionListener(this);

    gameMenu.add(beginnerItem);
    gameMenu.add(intermediateItem);
    gameMenu.add(expertItem);

    controlsMenu = new JMenu("Controls");
    controlsItem1.setEnabled(false);
    controlsItem2.setEnabled(false);
    controlsItem3.setEnabled(false);
    controlsMenu.add(controlsItem1);
    controlsMenu.add(controlsItem2);
    controlsMenu.add(controlsItem3);

    mb.add(gameMenu);
    mb.add(controlsMenu);
    mb.add(restartItem);

    UIManager.put("ToggleButton.select", Color.LIGHT_GRAY);

    setGame();

    frame.setJMenuBar(mb);
    frame.setSize(430, 500);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
  }

  public void setImages(int scale) {
    flag = new ImageIcon(flag.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    mine = new ImageIcon(mine.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    block = new ImageIcon(block.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    empty = new ImageIcon(empty.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    one = new ImageIcon(one.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    two = new ImageIcon(two.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    three = new ImageIcon(three.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    four = new ImageIcon(four.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    five = new ImageIcon(five.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    six = new ImageIcon(six.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    seven = new ImageIcon(seven.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));
    eight = new ImageIcon(eight.getImage().getScaledInstance(scale, scale, Image.SCALE_SMOOTH));

    smile = new ImageIcon(smile.getImage().getScaledInstance(scale1, scale1, Image.SCALE_SMOOTH));
    lose = new ImageIcon(lose.getImage().getScaledInstance(scale1, scale1, Image.SCALE_SMOOTH));
    cool = new ImageIcon(cool.getImage().getScaledInstance(scale1, scale1, Image.SCALE_SMOOTH));
    scare = new ImageIcon(scare.getImage().getScaledInstance(scale1, scale1, Image.SCALE_SMOOTH));
  }

  public void setGame() {
    try {
      frame.remove(gamePanel);
      timer.stop();
    } catch (NullPointerException e) {
    }

    timer = new Timer(1000, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        elapsedSeconds++;
        if (elapsedSeconds < 100000) {
          timerLabel.setText(String.format("%03d", elapsedSeconds));
        } else {
          ((Timer) (e.getSource())).stop();
        }
      }
    });
    timer.setInitialDelay(0);

    firstMove = true;
    mouseListenerIsActive = true;
    flags = 0;
    elapsedSeconds = 0;
    flagLabel.setText(String.format("%03d", mines));
    timerLabel.setText("000");
    smileButton.setIcon(smile);

    if (width == 30)
      frame.setSize(750, 500);
    else if (width == 16)
      frame.setSize(430, 500);
    else if (width == 9)
      frame.setSize(430, 500);

    if (beginnerItem.isSelected())
      setImages(scale1);
    else if (intermediateItem.isSelected())
      setImages(scale2);
    else if (expertItem.isSelected())
      setImages(scale3);

    gamePanel = new JPanel();
    gamePanel.setLayout(new GridLayout(height, width));
    togglers = new JToggleButton[height][width];

    for (int r = 0; r < togglers.length; r++) {
      for (int c = 0; c < togglers[0].length; c++) {
        togglers[r][c] = new JToggleButton();
        SwingUtilities.updateComponentTreeUI(togglers[r][c]);
        togglers[r][c].setIcon(block);
        togglers[r][c].putClientProperty("row", r);
        togglers[r][c].putClientProperty("column", c);
        togglers[r][c].putClientProperty("type", 0);
        togglers[r][c].putClientProperty("visible", 0);
        togglers[r][c].putClientProperty("flag", 0);
        togglers[r][c].addMouseListener(new MouseInputListener() {
          @Override
          public void mouseMoved(MouseEvent e) {
          }

          @Override
          public void mouseDragged(MouseEvent e) {
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            if (mouseListenerIsActive) {
              smileButton.setIcon(smile);

              if (e.getButton() == MouseEvent.BUTTON3
                  || (e.getButton() == MouseEvent.BUTTON1 && e.getModifiersEx() == 128)) {
                if (Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("flag")) == 0
                    && Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("visible")) == 0) {
                  ((JToggleButton) e.getComponent()).setIcon(flag);
                  ((JToggleButton) e.getComponent()).putClientProperty("flag", 1);
                  flags++;
                  flagLabel.setText(String.format("%03d", mineCounter - flags));
                } else if (Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("flag")) == 1) {
                  ((JToggleButton) e.getComponent()).setIcon(block);
                  ((JToggleButton) e.getComponent()).putClientProperty("flag", 0);
                  flags--;
                  flagLabel.setText(String.format("%03d", mineCounter - flags));
                }
              } else if (e.getButton() == MouseEvent.BUTTON1) {
                if (firstMove) {
                  addMines(((JToggleButton) e.getComponent()));
                  timer.start();
                  firstMove = false;
                }

                if (Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("type")) == -1
                    && Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("flag")) == 0)
                  endGame(false);

                if (Integer.parseInt("" + ((JToggleButton) e.getComponent()).getClientProperty("flag")) == 0)
                  enable(((JToggleButton) e.getComponent()));

              } else if (e.getButton() == MouseEvent.BUTTON2) {

              }

              if (checkWin())
                endGame(true);
            }
          }

          @Override
          public void mousePressed(MouseEvent e) {
            if (mouseListenerIsActive)
              smileButton.setIcon(scare);
          }

          @Override
          public void mouseExited(MouseEvent e) {
          }

          @Override
          public void mouseEntered(MouseEvent e) {
          }

          @Override
          public void mouseClicked(MouseEvent e) {
          }
        });
        gamePanel.add(togglers[r][c]);
      }
    }

    frame.add(gamePanel, BorderLayout.CENTER);
    frame.revalidate();
  }

  public int checkMines(int r, int c) {
    int total = 0;

    for (int x = r - 1; x <= r + 1; x++) {
      for (int y = c - 1; y <= c + 1; y++) {
        try {
          int type = Integer.parseInt("" + togglers[x][y].getClientProperty("type"));
          if (type == -1)
            total++;
        } catch (ArrayIndexOutOfBoundsException e) {
        }
      }
    }

    return total;
  }

  public void addMines(JToggleButton toggleButton) {
    ArrayList<JToggleButton> toggleButtons = new ArrayList<>();
    int row = Integer.parseInt("" + toggleButton.getClientProperty("row"));
    int col = Integer.parseInt("" + toggleButton.getClientProperty("column"));

    for (int x = row - 1; x <= row + 1; x++) {
      for (int y = col - 1; y <= col + 1; y++) {
        try {
          toggleButtons.add(togglers[x][y]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
      }
    }

    mineCounter = 0;

    while (mineCounter < mines) {
      for (int r = 0; r < togglers.length; r++) {
        for (int c = 0; c < togglers[0].length; c++) {
          if ((int) (Math.random() * (height * width)) + 1 == 1 && !toggleButtons.contains(togglers[r][c])
              && Integer.parseInt("" + togglers[r][c].getClientProperty("type")) != -1) {
            togglers[r][c].putClientProperty("type", -1);
            mineCounter++;
          }
        }
      }
    }

    flagLabel.setText(String.format("%03d", mineCounter));

    for (int r = 0; r < togglers.length; r++) {
      for (int c = 0; c < togglers[0].length; c++) {
        if (Integer.parseInt("" + togglers[r][c].getClientProperty("type")) != -1) {
          togglers[r][c].putClientProperty("type", checkMines(r, c));
        }
      }
    }

    printBoard();
  }

  public void endGame(boolean win) {
    mouseListenerIsActive = false;
    timer.stop();

    if (win) {
      smileButton.setIcon(cool);
      for (int r = 0; r < togglers.length; r++) {
        for (int c = 0; c < togglers[0].length; c++) {
          if (Integer.parseInt("" + togglers[r][c].getClientProperty("type")) == -1) {
            togglers[r][c].setIcon(flag);
            togglers[r][c].putClientProperty("flag", 1);
            flags = mines;
            flagLabel.setText("000");
          }
        }
      }
    } else {
      smileButton.setIcon(lose);
      for (int r = 0; r < togglers.length; r++) {
        for (int c = 0; c < togglers[0].length; c++) {
          if (Integer.parseInt("" + togglers[r][c].getClientProperty("type")) == -1) {
            enable(togglers[r][c]);
          }
        }
      }
    }
  }

  public boolean checkWin() {
    int temp = 0;

    for (int r = 0; r < togglers.length; r++) {
      for (int c = 0; c < togglers[0].length; c++) {
        if (Integer.parseInt("" + togglers[r][c].getClientProperty("visible")) == 1)
          temp++;
      }
    }

    if (temp == ((height * width) - mineCounter)) {
      System.out.println(temp + " " + mineCounter);
      return true;
    }
    return false;
  }

  public void printBoard() {
    for (int r = 0; r < togglers.length; r++) {
      for (int c = 0; c < togglers[0].length; c++) {
        System.out.print(togglers[r][c].getClientProperty("type") + "\t");
      }
      System.out.println();
    }
    System.out.println();
  }

  public void enable(JToggleButton toggleButton) {
    toggleButton.putClientProperty("visible", 1);

    switch (Integer.parseInt("" + toggleButton.getClientProperty("type"))) {
      case -1:
        toggleButton.setIcon(mine);
        break;
      case 0:
        toggleButton.setIcon(empty);
        expand(toggleButton);
        break;
      case 1:
        toggleButton.setIcon(one);
        break;
      case 2:
        toggleButton.setIcon(two);
        break;
      case 3:
        toggleButton.setIcon(three);
        break;
      case 4:
        toggleButton.setIcon(four);
        break;
      case 5:
        toggleButton.setIcon(five);
        break;
      case 6:
        toggleButton.setIcon(six);
        break;
      case 7:
        toggleButton.setIcon(seven);
        break;
      case 8:
        toggleButton.setIcon(eight);
        break;
      default:
        break;
    }
  }

  public void expand(JToggleButton toggleButton) {
    int r = Integer.parseInt("" + toggleButton.getClientProperty("row"));
    int c = Integer.parseInt("" + toggleButton.getClientProperty("column"));

    for (int x = r - 1; x <= r + 1; x++) {
      for (int y = c - 1; y <= c + 1; y++) {
        try {
          if (Integer.parseInt("" + togglers[x][y].getClientProperty("type")) != -1
              && Integer.parseInt("" + togglers[x][y].getClientProperty("visible")) == 0
              && Integer.parseInt("" + togglers[x][y].getClientProperty("flag")) == 0)
            enable(togglers[x][y]);
        } catch (StackOverflowError | ArrayIndexOutOfBoundsException e) {
        }
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == beginnerItem && beginnerItem.isSelected()) {
      height = 9;
      width = 9;
      mines = 10;

      beginnerItem.setSelected(true);
      intermediateItem.setSelected(false);
      expertItem.setSelected(false);
      setGame();
    } else if (e.getSource() == intermediateItem && intermediateItem.isSelected()) {
      height = 16;
      width = 16;
      mines = 40;

      beginnerItem.setSelected(false);
      intermediateItem.setSelected(true);
      expertItem.setSelected(false);
      setGame();
    } else if (e.getSource() == expertItem && expertItem.isSelected()) {
      height = 16;
      width = 30;
      mines = 99;

      beginnerItem.setSelected(false);
      intermediateItem.setSelected(false);
      expertItem.setSelected(true);
      setGame();
    } else if (e.getSource() == beginnerItem) {
      beginnerItem.setSelected(true);
    } else if (e.getSource() == intermediateItem) {
      intermediateItem.setSelected(true);
    } else if (e.getSource() == expertItem) {
      expertItem.setSelected(true);
    } else if (e.getSource() == restartItem || e.getSource() == smileButton) {
      setGame();
    }

    repaint();
  }

  public static void main(String[] args) {
    new Minesweeper();
  }

}