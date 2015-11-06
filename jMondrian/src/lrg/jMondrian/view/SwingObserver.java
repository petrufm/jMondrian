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
package lrg.jMondrian.view;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import lrg.jMondrian.util.IMondrianObserver;

public class SwingObserver implements IMondrianObserver {
	private static final int MAX_WORK = 1000;
	private int work = 0, totalWork = 0;
	private WorkPrecision totalPrecision=WorkPrecision.APPROXIMATE;

	private JFrame frame;
	private JProgressBar progress;
	
	public SwingObserver() {
		frame = new JFrame();
		frame.setTitle("jMondrian: building picture.");
        frame.setSize(400, 50);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { frame.dispose(); }
        });
        progress = new JProgressBar();
        progress.setMaximum(MAX_WORK);
        progress.setValue(0);
        
        frame.add(progress);
	}
	
	@Override
	public void setCanceled(boolean value) {
	}

	@Override
	public void setReaminingWork(int remainingWork, WorkPrecision precision) {
		frame.setVisible(true);
		if (totalPrecision==WorkPrecision.EXACT) return;
		
		totalPrecision = precision;
		totalWork+=remainingWork;
	}

	@Override
	public void subTask(String name) {
		frame.setTitle("jMondrian: "+name);
	}

	@Override
	public void workComplete() {
		frame.dispose();
	}

	@Override
	public void worked(int work) {
		this.work += work;
		progress.setValue(this.work*MAX_WORK / totalWork);
	}

}
