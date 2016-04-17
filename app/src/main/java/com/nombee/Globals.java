package com.nombee;

import android.app.Application;

/**
 * Created by erikotsuda on 4/12/16.
 */
public class Globals extends Application {

    /**
     * User Name (not User ID)
     */
    String userName;

    /**
     * User Birth Date
     */
    int birthYear;
    int birthMonth;
    int birthDay;

    /**
     * User Self Introduction
     */
    String selfIntro;

    /**
     * Number of Liqueurs the user drunk
     */
    int numOfDrunkLiqueurs;
}
