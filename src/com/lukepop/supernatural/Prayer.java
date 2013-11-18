package com.lukepop.supernatural;

import com.lukepop.entity.Entity;
import com.lukepop.island.Island;

//Prayer is a special class of job, because it has no actual
//entity representation. The job object will be the main hut, the villagers
//will go there to pray.
public class Prayer 
{
	public static final int WORK_TIME = 1000;
	public static final String RESOURCE = "E";
	//The main hut will serve as the job object.
	public static final Entity JOB_OBJECT =  Island.shrine;
	public static final int RADIUS = (int) JOB_OBJECT.radius;
	public static final double X_LOC = JOB_OBJECT.x;
	public static final double Y_LOC = JOB_OBJECT.y;
}
