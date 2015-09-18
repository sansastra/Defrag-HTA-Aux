package com.launcher;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Fran on 4/16/2015.
 */
public class Launcher {

    private static Date date;

    public static void main(String[] args) throws IOException {

        date = new Date();
        SimulatorParameters.readConfigFile("nsf-config.txt");

        SimulatorParameters.startSimulation();
    }

    public static Date getDate() {
        return date;
    }

}
