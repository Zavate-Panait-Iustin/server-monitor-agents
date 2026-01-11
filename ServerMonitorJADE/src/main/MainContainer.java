package main;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

public class MainContainer {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");

        ContainerController container = rt.createMainContainer(profile);

        try {
            container.createNewAgent("Server1",
                    "agents.ServerAgent",
                    new Object[]{"Server1"}).start();

            container.createNewAgent("Server2",
                    "agents.ServerAgent",
                    new Object[]{"Server2"}).start();

            container.createNewAgent("Monitor",
                    "agents.MonitorAgent",
                    null).start();

            container.createNewAgent("Logger",
                    "agents.LoggerAgent",
                    null).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
