package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private Task currentTask;

    public Server(int maxTasksPerServer) {
        tasks = new LinkedBlockingQueue<>(maxTasksPerServer);
        waitingPeriod = new AtomicInteger(0);
    }

    public synchronized void addTask(Task newTask) {
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    void startTask() {
        if (currentTask != null) {
            System.out.println("Server starting task: " + currentTask);
        }
    }

    public synchronized Task getCurrentTask() {
        return currentTask;
    }

    public synchronized void setCurrentTask(Task task) {
        currentTask = task;
    }

    public int getQueueSize() {
        return tasks.size();
    }

    public void run() {
        while (true) {
            try {
                currentTask = tasks.take();
                startTask();
                Thread.sleep(currentTask.getServiceTime());
                synchronized(this) {
                    waitingPeriod.addAndGet(-currentTask.getServiceTime());
                }
                currentTask = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
