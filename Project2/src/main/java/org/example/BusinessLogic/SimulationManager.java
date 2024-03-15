package org.example.BusinessLogic;
import org.example.Model.Server;
import org.example.Model.Task;
import org.example.GUI.SimulationFrame;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationManager extends JFrame implements Runnable {
    private SimulationFrame frame;
    public int timeLimit;
    public int maxProcessingTime ;
    public int minProcessingTime ;
    public int maxArrivalTime ;
    public int minArrivalTime;
    public int numberOfServers ;
    public int numberOfClients ;
    private Scheduler scheduler;
    private List<Task> generatedTasks;

    public SimulationManager(SimulationFrame frame) {
        this.frame = frame;

         timeLimit = Integer.parseInt(frame.timeLimitTextField.getText());
         maxProcessingTime = Integer.parseInt(frame.maxServiceTimeTextField.getText());
         minProcessingTime = Integer.parseInt(frame.minServiceTimeTextField.getText());
         maxArrivalTime = Integer.parseInt(frame.maxArrivalTimeTextField.getText());
         minArrivalTime = Integer.parseInt(frame.minArrivalTimeTextField.getText());
         numberOfServers = Integer.parseInt(frame.numberOfServersTextField.getText());
         numberOfClients = Integer.parseInt(frame.numberOfClientsTextField.getText());

        scheduler = new Scheduler(numberOfServers, maxProcessingTime);
        for (int i = 0; i < numberOfServers; i++) {
            Server server = new Server(i + 1);
            Thread thread = new Thread(server);
            thread.start();
            scheduler.getServers().add(server);
        }

        generatedTasks = generateNRandomTasks(numberOfClients);
        Collections.sort(generatedTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                if (t1.getArrivalTime() == t2.getArrivalTime()) {
                    return Integer.compare(t1.getServiceTime(), t2.getServiceTime());
                } else {
                    return Integer.compare(t1.getArrivalTime(), t2.getArrivalTime());
                }
            }
        });
        setVisible(true);
    }


    private List<Task> generateNRandomTasks(int numTasks) {
        Random rand = new Random();
        List<Task> tasks = new ArrayList<Task>();
        for (int i = 1; i <= numTasks; i++) {
            int processingTime = rand.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            int arrivalTime = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            Task task = new Task(i, arrivalTime, processingTime);
            tasks.add(task);
        }
        return tasks;
    }

    private void initializeQueuesAndThreads(List<Queue<Task>> queues, List<Thread> queueThreads) {
        AtomicBoolean tasksRemaining = new AtomicBoolean(true);
        for (int i = 0; i < numberOfServers; i++) {
            ArrayDeque<Task> queue = new ArrayDeque<>();
            queues.add(queue);
            Server server = scheduler.getServers().get(i);
            Thread thread = new Thread(() -> {
                while (true) {
                    Task task = null;
                    synchronized (server) {
                        if (server.getCurrentTask() == null && queue.peek() != null)
                        {  task = queue.poll(); }
                    }
                    if (task != null) {
                        while (server.getCurrentTask() != null) {
                            try { Thread.sleep(100); }
                            catch (InterruptedException e)
                            { e.printStackTrace(); }
                        }
                        server.setCurrentTask(task);
                        tasksRemaining.set(true);
                    } else if (!tasksRemaining.get())
                        break;
                }
            });
            thread.start();
            queueThreads.add(thread);
        }
    }

    private void displayWaitingClients() {
        frame.append("Waiting clients");
        for (Task task2 : generatedTasks) {
            frame.append("(" + task2.getID() + "," + task2.getArrivalTime() + "," + task2.getServiceTime() + "); ");
        }
        frame.append("\n");
    }

    @Override
    public void run() {
        int totalWaitingTime = 0, totalServiceTime = 0, peakHour = 0, maxClients = 0, ok = 0;
        int[] hourlyClients = new int[maxArrivalTime + 1];
        int currentTime = 0, totalClientsServed = 0;
        List<Queue<Task>> queues = new ArrayList<>();
        List<Thread> queueThreads = new ArrayList<>();

        initializeQueuesAndThreads(queues,queueThreads);
        displayWaitingClients();

        boolean tasksLeft = true;
        while (currentTime < timeLimit-1  && (tasksLeft && (!generatedTasks.isEmpty() || queues.stream().anyMatch(queue -> !queue.isEmpty())))) {
            tasksLeft = true;
            while (tasksLeft) {
                if (currentTime > timeLimit-1) {
                    break;
                }
                boolean taskAdded = false;
                for (int i = 0; i < generatedTasks.size(); i++) {
                    Task task = generatedTasks.get(i);
                    if (task.getArrivalTime() <= currentTime) {
                        for (int j = 0; j < numberOfServers; j++) {
                            Queue<Task> queue = queues.get(j);
                            Server server = scheduler.getServers().get(j);
                            synchronized (queue) {
                                if (queue.isEmpty() && server.getCurrentTask() == null) {
                                    queue.add(task);
                                    taskAdded = true;
                                    break;
                                }
                            }
                        }
                        if (taskAdded) {
                            generatedTasks.remove(i);  i--;
                            totalWaitingTime += currentTime - task.getArrivalTime() + task.getServiceTime() ;
                            totalServiceTime += task.getServiceTime();
                            int hour = task.getArrivalTime();
                            if(hour<=timeLimit-1) {
                                for (int j = 0; j < generatedTasks.size(); j++)
                                {
                                    int hour2 = generatedTasks.get(j).getArrivalTime();
                                    if(hour2==currentTime)
                                        hourlyClients[hour2]++;

                                }
                                hourlyClients[hour]++;
                            }
                        }
                    }
                }

                frame.append("Time " + currentTime + "\n");
                displayWaitingClients();

                for (int i = 0; i < numberOfServers; i++) {
                    Queue<Task> queue = queues.get(i);
                    Server server = scheduler.getServers().get(i);
                    synchronized (queue) {
                        Task currentTask = server.getCurrentTask();
                        if (currentTask != null && currentTask.getServiceTime() > 0) {
                            frame.append("Queue " + (i + 1) + ": " + currentTask + "\n");
                        } else
                            frame.append("Queue " + (i + 1) + ": " + "closed" + "\n");
                    }
                }
                frame.append("\n");

                boolean tasksProcessing = false;
                for (Server server : scheduler.getServers()) {
                    Task currentTask = server.getCurrentTask();
                    if (currentTask != null) {
                        tasksProcessing = true;
                        if (currentTask.getServiceTime() == 1) {
                            server.setCurrentTask(null);
                            totalClientsServed++;
                            scheduler.sendTaskToFreeServer(queues);
                        } else if (currentTask.getServiceTime() > 1) {
                            currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                        }
                    }
                }

                currentTime++;
                if (taskAdded || tasksProcessing || !generatedTasks.isEmpty() || queues.stream().anyMatch(queue -> !queue.isEmpty()))
                {   tasksLeft = true; }
                else { tasksLeft = false; }
                if (currentTime < timeLimit-1 && generatedTasks.isEmpty() && queues.stream().allMatch(Queue::isEmpty))
                { scheduler.closeQueue(); }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        double avgWaitingTime=0, avgServiceTime=0;
        if(totalClientsServed!=0)
        {   avgWaitingTime = (double) totalWaitingTime / totalClientsServed;
            avgServiceTime = (double) totalServiceTime / totalClientsServed; }
        for (int i = 0; i < hourlyClients.length; i++) {
            if (hourlyClients[i] > maxClients) {
                maxClients = hourlyClients[i];  peakHour = i; ok = 1;
            }
        }
        frame.append("Average waiting time: " + String.format("%.4f\n", avgWaitingTime));
        frame.append("Average service time: " + String.format("%.4f\n", avgServiceTime));
        if(ok==1)  { frame.append("Peak hour: " + peakHour); }
        else { frame.append("Nu a fost procesat niciun client"); }

        try {
            FileWriter fileWriter = new FileWriter("output.txt");
            fileWriter.write(frame.getText());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SimulationFrame frame = new SimulationFrame();
        if (frame.allFieldsCompleted()) {
            SimulationManager simManager = new SimulationManager(frame);
            Thread t = new Thread(simManager);
            t.start();
        }

    }
}

