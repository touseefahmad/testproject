package com.example.enigma_pc.smartmailforblinds;
import java.util.Observable;

/**
 * Created by Touseef on 3/28/2016.
 */


public class DataObservor extends Observable {
    protected void triggerObservers() {
        setChanged();
        notifyObservers();
    }
}
