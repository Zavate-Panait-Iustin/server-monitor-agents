package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerAgent extends Agent {

    private BufferedWriter writer;
    private final String filePath = "history.txt";

    @Override
    protected void setup() {
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true)); // append mode
        } catch (IOException e) {
            e.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    logMessage(content);
                } else {
                    block();
                }
            }
        });
    }

    private void logMessage(String content) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

     
        String logLine = String.format("[%s] %s", timestamp, content);

        try {
            writer.write(logLine);
            writer.newLine();
            writer.flush();

          
            System.out.println(logLine);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        try {
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(getLocalName() + " terminated.");
    }
}
