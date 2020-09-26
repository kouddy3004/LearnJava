package multiThreading;

import java.util.ArrayList;
import java.util.List;

public class LearnThread {

     public static void main(String[] args) throws Exception{
                Thread t1=new Thread(
                        ()->{
                            for(int i=0;i<=5;i++){
                                System.out.println(Thread.currentThread().getName()+" ==>"+Thread.currentThread().getPriority());
                                try{Thread.sleep(500);}catch (Exception e){}
                            }
                        }
                ,"First Thread");
         Thread t2=new Thread(
                 ()->{
                     for(int i=0;i<=5;i++){
                         System.out.println(Thread.currentThread().getName()+" ==>"+Thread.currentThread().getPriority());
                         try{Thread.sleep(500);}catch (Exception e){}
                     }
                 }
                 ,"Second Thread");
         t1.setPriority(Thread.MIN_PRIORITY);
         t2.setPriority(Thread.MAX_PRIORITY);
         t1.start();
         try{Thread.sleep(500);}catch (Exception e){}
         t2.start();
    }

}
