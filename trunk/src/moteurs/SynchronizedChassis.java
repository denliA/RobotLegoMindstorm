package moteurs;

import lejos.hardware.Sound;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;

public class SynchronizedChassis extends WheeledChassis {
	
	protected volatile boolean traveling = false;
	protected volatile boolean ended_traveling = false;
	
	public SynchronizedChassis(Wheel[] wheels, int dim) {
		super(wheels, dim);
	}
	
	@Override
	public void travel(double linear) {
		travel(linear, 1);
	}
	
	
	public synchronized void travel(final double linear, final double decalage) {
		if (traveling) {
			endTraveling(false);
		}
		else {
			traveling = true;
			new Thread() {
				@Override
				public void run() {
					travelSync(linear, decalage);
					ended_traveling = true;
				}
			}.run();
		}
	}
	
	
	
	protected void endTraveling(boolean andStop) {
		traveling = false;
		while(!ended_traveling)
			Thread.yield();
		ended_traveling=false;
		if(andStop) {
			super.stop();
			waitComplete();
		}
	}
	
	@Override
	public void stop() {
		if(traveling)
			endTraveling(true);
		else
			super.stop();
	}
	
	protected void travelSync(double linear, double decalage) {
		
		int delta = (int) (linear * 360 / (5.6 * Math.PI));
		System.out.println("[travelSync] Delta : " + delta);
		int motorSpeed =  (int) (linearSpeed * 360 / (5.6 * Math.PI));
		
		motor[0].resetTachoCount();
		motor[1].resetTachoCount();
		master.startSynchronization();
			motor[0].setSpeed((int) (motorSpeed*decalage));
			motor[1].setSpeed((int) (motorSpeed/decalage));
			motor[0].forward();
			motor[1].forward();
		master.endSynchronization();
		
		if(decalage==1) {
			while(traveling) {
				Thread.yield();
			}
		} else {
			final double KP = 40;
			double error;
			
			while(traveling && master.getTachoCount() < delta) {
				error = motor[0].getTachoCount() - motor[1].getTachoCount();
				motor[0].setSpeed((int) (motorSpeed-error*KP));
				//System.out.println("	[travelSync]  master.TachoCount is " + master.getTachoCount() + "	motor[1].TachoCount is " + motor[1].getTachoCount());
			}
			traveling = false;		
		}
		
	
	}
	
	
	
	
}




