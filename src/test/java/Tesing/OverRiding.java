package Tesing;

class Dog{
    public void bark(){
        System.out.println("woof ");
    }
}

class Hound extends Dog{
    public void sniff(){
        System.out.println("sniff ");
    }

    public void bark(){
        System.out.println("bowl");
        super.bark();
    }
}

public class OverRiding{
    public static void main(String [] args){
        Dog dog = new Dog();
        System.out.println(dog);
        Dog dog1 = new Dog();
        System.out.println(dog1);
    }
}