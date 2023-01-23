package com.example.wesave;

public class MySingleton {
    // Static variable reference of single_instance
    // of type Singleton
    private static MySingleton single_instance = null;

    // Declaring a variable of type String
    public String s;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private MySingleton()
    {
        s = "Hello I am a string part of Singleton class";
    }

    // Static method
    // Static method to create instance of Singleton class
    public static MySingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new MySingleton();

        return single_instance;
    }

}
