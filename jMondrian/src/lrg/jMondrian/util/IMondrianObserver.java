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

public interface IMondrianObserver {
	public static enum WorkPrecision {
		EXACT, APPROXIMATE
	}
	
	/**
	 * Sets the total work required to render this picture
	 * @param totalWork indicates the total number of workunits
	 */
	public void setReaminingWork(int remainingWork, WorkPrecision precision);
	
	
	/**
	 * Sets the cancel state to the given value.
	 * 
	 * @param value <code>true</code> indicates that cancelation has
	 *     been requested (but not necessarily acknowledged);
	 *     <code>false</code> clears this flag
	 * @see #isCanceled()
	 */
	public void setCanceled(boolean value);
	
	/**
	 * Notifies that a subtask of the main task is beginning.
	 * Subtasks are optional; the main task might not have subtasks.
	 *
	 * @param name the name (or description) of the subtask
	 */
	public void subTask(String name);

	/**
	 * Notifies that a given number of work unit of the main task
	 * has been completed. Note that this amount represents an
	 * installment, as opposed to a cumulative amount of work done
	 * to date.
	 *
	 * @param work a non-negative number of work units just completed
	 */
	public void worked(int work);
	
	public void workComplete();
}
