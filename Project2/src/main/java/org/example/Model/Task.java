package org.example.Model;

public class Task implements Comparable<Task>{
    private int ID;
    private int arrivalTime;
    private int serviceTime;

    public Task(int ID, int arrivalTime, int serviceTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getID() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }


    public String toString() {
        return "Task:" +
                "ID=" + ID +
                ", arrivalTime=" + arrivalTime +
                ", serviceTime=" + serviceTime ;
    }

    @Override
    public int compareTo(Task other) {
        if (this.arrivalTime < other.arrivalTime) {
            return -1;
        } else if (this.arrivalTime > other.arrivalTime) {
            return 1;
        } else {
            return 0;
        }
    }
}
