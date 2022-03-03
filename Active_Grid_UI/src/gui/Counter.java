package gui;

public class Counter {

    int count;

    Counter() {
        count= 0;
    }

    public void increment() {
        count++ ;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int i) {
        count= i;
    }

}
