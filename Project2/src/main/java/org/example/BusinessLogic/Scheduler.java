package org.example.BusinessLogic;


import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scheduler {

    private List<Server> servers;
    private List<Queue<Task>> queues;
    private int maxNoServers;
    private int maxTasksPerServer;
    private ConcreteStrategyQueue strategy;
    private int totalClientsServed;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.servers = new ArrayList<>();
        this.queues = new ArrayList<>();
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(maxTasksPerServer);
            servers.add(server);
            queues.add(new LinkedList<>());
            Thread t = new Thread(server);
            t.start();
        }

        this.strategy = new ConcreteStrategyQueue();
        this.totalClientsServed=0;
    }

    public List<Server> getServers() {
        return servers;
    }

    void sendTaskToFreeServer(List<Queue<Task>> queues) {
        for (Server server : this.getServers()) {
            Task currentTask = server.getCurrentTask();
            if (currentTask != null) {
                if (currentTask.getServiceTime() == 0) {
                    server.setCurrentTask(null);
                    totalClientsServed++;
                }
            } else {
                for (Queue<Task> queue : queues) {
                    if (!queue.isEmpty()) {
                        Task task = queue.remove();
                        server.setCurrentTask(task);
                        break;
                    }
                }
            }
        }
    }

    public void closeQueue() {
        List<Queue<Task>> emptyQueues = new ArrayList<>();
        for (Queue<Task> queue : queues) {
            if (queue.isEmpty() ) {
                emptyQueues.add(queue);
            }
        }
        queues.removeAll(emptyQueues);
    }

    public void someMethod(List<Server> servers, Task task) {
        ConcreteStrategyQueue queueStrategy = new ConcreteStrategyQueue();
        queueStrategy.addTask(servers, task);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < servers.size(); i++) {
            stringBuilder.append("Queue ");
            stringBuilder.append(i + 1);
            stringBuilder.append(": ");
            for (Task task : queues.get(i)) {
                stringBuilder.append(task);
                stringBuilder.append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

}