package org.example.BusinessLogic;
import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;

public class ConcreteStrategyQueue  {

    public void addTask(List<Server> servers, Task t) {
        Server shortestQueueServer = servers.get(0);
        for (Server server : servers) {
            if (server.getQueueSize() < shortestQueueServer.getQueueSize()) {
                shortestQueueServer = server;
            }
        }
        shortestQueueServer.addTask(t);
    }
}