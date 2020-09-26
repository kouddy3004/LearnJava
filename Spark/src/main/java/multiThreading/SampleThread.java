package multiThreading;

import java.util.ArrayList;
import java.util.List;

public class SampleThread {
    static String global;
    public static void main(String args[])throws Exception {
        System.out.println("Started at "+java.time.LocalTime.now());
        SampleThread obj=new SampleThread();
        obj.runThread();
        System.out.println("Finished at "+java.time.LocalTime.now());
    }

    public void runThread() throws Exception{
        global="";
        String[] myName= {"Koushik","Subra","Manian"};
        List<String> name=new ArrayList<>();
        ThreadTest[] objs=new ThreadTest[myName.length];
        for (int i=0;i<myName.length;i++){
            objs[i]=new ThreadTest(myName[i]);
            objs[i].start();
        }
        for(ThreadTest obj:objs){
            obj.join();
        }

        System.out.println(global);
        System.out.println("Completed at Method "+java.time.LocalTime.now());
    }
}
class ThreadTest extends Thread {

    public ThreadTest(String str) {
        super(str);
    }

    public synchronized void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("Loop " + i + ": " + getName());
                SampleThread.global=SampleThread.global+"_"+getName()+"_"+i;
            try {
                sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
            }
        }
        System.out.println("Test Finished for: " + getName()+" and Finished at "+java.time.LocalTime.now());
    }


}
