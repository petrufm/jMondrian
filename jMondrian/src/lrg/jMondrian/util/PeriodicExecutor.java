/*
 * Copyright (c) 2006-2015 Petru-Florin Mihancea, Mihai Balint, Andreea Ionete
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lrg.jMondrian.util;

public class PeriodicExecutor implements Runnable {
	private Runnable periodicTask;
	private Thread runner;
	private long frame, startTime, nextFrameTime, idleTime, usedTime, lastUsage;
	private long delay;
	private String name;
	private boolean accelerateToRecover = false;

	public PeriodicExecutor(String name, int fps, Runnable periodicTask) {
		this.periodicTask = periodicTask;
		delay = (fps > 0) ? (1000000000l / fps) : 100000000l;
		this.name=name;
		//restart();
	}
	
	
	public void run() {
		// Remember the starting time
		startTime = System.nanoTime();
		nextFrameTime = startTime;
		idleTime = 0; usedTime = 0;
		frame = 0; lastUsage = 0;
		while (Thread.currentThread()==runner) {
			// Compute the next world tick.
			long b4 = System.nanoTime();
			periodicTask.run();
			
			try { // Delay depending on how far we are behind. 
				long now = System.nanoTime();
				lastUsage = now-b4; usedTime += lastUsage;
				nextFrameTime += delay;
				long tts = nextFrameTime - now;
				if (tts>0) {
					idleTime+=tts;
					Thread.sleep(tts/1000000);
				} else if (!accelerateToRecover) {
					nextFrameTime = now;
				}
			} catch (InterruptedException e) {
				break;
			}
			
			// Advance the frame
			frame++;
		}
	}
	
	public long unsafeGetFrameNo() {
		return frame;
	}

	public String unsafeStats() {
		double totalTime = nextFrameTime-startTime;
		double fps = (1000000000.0*(double)frame) / totalTime;
		double idles = ((double)idleTime) / totalTime;
		
		fps = (double)Math.round(fps*10.0) / 10.0;
		idles = (double)Math.round(idles*1000.0) / 10.0;
		
		double lu = (double)Math.round((double)lastUsage / 100000.0) / 10.0;
		totalTime = (double)Math.round(totalTime/100000.0) / 10.0;
		
		return "FPS("+frame+"): "+fps+", Idl: "+idles+"%, Usg: "+lu+"/"+(delay/1000000)+"/"+(usedTime/1000000)+" Tot: "+totalTime;
	}
	
	public void restart() {
		(runner = new Thread(this,name)).start();
	}
	
	public void stop() {
		runner = null;
	}

}
