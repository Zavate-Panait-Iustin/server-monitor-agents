package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import util.MessageUtils;
import javax.swing.*;
import java.awt.*;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

public class ServerAgent extends Agent {

    private String serverName;
    private boolean paused = false;

    private CircularProgressBar cpuGauge;
    private CircularProgressBar ramGauge;
    private JPanel pingLED;

    private CentralProcessor cpu;
    private GlobalMemory memory;
    private long[] previousTicks;

    @Override
    protected void setup() {
        serverName = (String) getArguments()[0];

        SystemInfo si = new SystemInfo();
        cpu = si.getHardware().getProcessor();
        memory = si.getHardware().getMemory();
        previousTicks = cpu.getSystemCpuLoadTicks();

        createGUI();

        addBehaviour(new TickerBehaviour(this, 2000) {
            protected void onTick() {
                if (!paused) collectAndSend();
            }
        });
    }

    private void createGUI() {
        JFrame frame = new JFrame(serverName + " - Server Monitor");
        frame.setSize(550, 380);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(25,25,30));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

     
        JLabel title = new JLabel(serverName);
        title.setForeground(new Color(240,180,50)); // galben-aprins
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(15));

    
        
        cpuGauge = new CircularProgressBar(new Color(100,200,250)); // albastru deschis
        ramGauge = new CircularProgressBar(new Color(180,100,250)); // mov deschis

        
        JPanel gaugePanel = new JPanel();
        gaugePanel.setBackground(new Color(25,25,30));
        gaugePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10));

        JPanel cpuPanel = new JPanel(new BorderLayout());
        cpuPanel.setBackground(new Color(25,25,30));
        JLabel cpuLabel = new JLabel("CPU Usage");
        cpuLabel.setForeground(new Color(240,180,50));
        cpuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cpuPanel.add(cpuLabel, BorderLayout.NORTH);
        cpuPanel.add(cpuGauge, BorderLayout.CENTER);

        JPanel ramPanel = new JPanel(new BorderLayout());
        ramPanel.setBackground(new Color(25,25,30));
        JLabel ramLabel = new JLabel("RAM Usage");
        ramLabel.setForeground(new Color(240,180,50));
        ramLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ramPanel.add(ramLabel, BorderLayout.NORTH);
        ramPanel.add(ramGauge, BorderLayout.CENTER);

        gaugePanel.add(cpuPanel);
        gaugePanel.add(ramPanel);
        root.add(gaugePanel);
        root.add(Box.createVerticalStrut(15));

        
        
        JLabel pingLabel = new JLabel("Ping Status");
        pingLabel.setForeground(new Color(240,180,50));
        pingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        pingLED = new JPanel();
        pingLED.setPreferredSize(new Dimension(30,30));
        pingLED.setBackground(Color.GRAY);
        pingLED.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));
        pingLED.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(pingLabel);
        root.add(Box.createVerticalStrut(5));
        root.add(pingLED);
        root.add(Box.createVerticalStrut(15));

       
        
        
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(25,25,30));

        JButton pauseBtn = new JButton("Pause");
        pauseBtn.setBackground(new Color(200,50,50));
        pauseBtn.setForeground(Color.WHITE);
        pauseBtn.setFocusPainted(false);
        pauseBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pauseBtn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));
        pauseBtn.addActionListener(e -> paused = true);

        JButton resumeBtn = new JButton("Resume");
        resumeBtn.setBackground(new Color(50,200,50));
        resumeBtn.setForeground(Color.WHITE);
        resumeBtn.setFocusPainted(false);
        resumeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resumeBtn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));
        resumeBtn.addActionListener(e -> paused = false);

        btnPanel.add(pauseBtn);
        btnPanel.add(resumeBtn);
        root.add(btnPanel);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private void collectAndSend() {
        double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(previousTicks) * 100;
        previousTicks = cpu.getSystemCpuLoadTicks();

        double ramLoad = (1.0 - (double) memory.getAvailable() / memory.getTotal()) * 100;

        int ping = 20 + (int)(Math.random() * 40);

        updateGUI(cpuLoad, ramLoad, ping);

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(getAID("Monitor"));
        msg.setContent(MessageUtils.buildMetricsMessage(serverName, cpuLoad, ramLoad, ping));
        send(msg);
    }

    private void updateGUI(double cpu, double ram, int ping) {
        cpuGauge.setValue((int) cpu);
        ramGauge.setValue((int) ram);

        if (ping < 50) pingLED.setBackground(new Color(50,220,50));
        else if (ping < 80) pingLED.setBackground(new Color(250,200,50));
        else pingLED.setBackground(new Color(220,50,50));
    }

   
    
    
    private class CircularProgressBar extends JPanel {
        private int value = 0;
        private final int MAX_HISTORY = 50;
        private int[] history = new int[MAX_HISTORY];
        private int index = 0;
        private Color fillColor;

        public CircularProgressBar(Color fillColor) {
            this.fillColor = fillColor;
            setPreferredSize(new Dimension(120,120));
        }

        public void setValue(int value) {
            this.value = Math.min(100, Math.max(0, value));
            history[index] = this.value;
            index = (index + 1) % MAX_HISTORY;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            
            g2.setColor(new Color(50,50,60));
            g2.fillOval(x, y, size, size);

       
            g2.setColor(fillColor);
            g2.fillArc(x, y, size, size, 90, - (int)(3.6*value));

          
            g2.setColor(new Color(25,25,30));
            g2.fillOval(x+15, y+15, size-30, size-30);

          
            g2.setColor(new Color(255, 150, 50, 180)); 
            int hx = x + 15;
            int hy = y + size - 15;
            int w = size - 30;
            int h = size - 30;
            int step = w / MAX_HISTORY;
            for (int i=0;i<MAX_HISTORY-1;i++){
                int idx1 = (index + i) % MAX_HISTORY;
                int idx2 = (index + i +1) % MAX_HISTORY;
                int y1 = hy - (history[idx1]*h/100);
                int y2 = hy - (history[idx2]*h/100);
                g2.drawLine(hx + i*step, y1, hx + (i+1)*step, y2);
            }

          
            g2.setColor(new Color(255,220,150));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String text = value + "%";
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(text)) /2;
            int ty = getHeight()/2 + fm.getHeight()/4;
            g2.drawString(text, tx, ty);
        }
    }
}
