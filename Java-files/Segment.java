package package_6606598;

import java.util.ArrayList;
import java.util.List;

public class Segment {

	private int segmentNumber;
	private int segmentSize;
	private int segmentType = 1; //by default the type is protected (can't be shared)
	private int segmentBase;	//starting position of the segment in the memory
	private int segmentLimit;	//ending position of the segment in the memory
	
	private int timesUsed;	//this field will keep track of how many times a shared segment is used in order to 
	//know weather it should be removed from the memory after deallocating a processes that uses it or not
	
	//This list contains the list of processes that this segment is shared with
	private List<Integer> includedInProcesses;
	
	public Segment(int segmentNumber, int segmentSize) {
		this.segmentNumber = segmentNumber;
		this.segmentSize = segmentSize;
		this.includedInProcesses = new ArrayList<Integer>();
	}
	
	
	
	
	//getter and setter for segment number
	public int getSegmentNumber() {
		return this.segmentNumber;
	}
	
	public void setSegmentNumber(int segmentNumber) {
		this.segmentNumber = segmentNumber;
	}
	//------------------------------------------

	
	//getter and setter for segment size
	public int getSegmentSize() {
		return this.segmentSize;
	}

	public void setSegmentSize(int segmentSize) {
		this.segmentSize = segmentSize;
	}
	//------------------------------------------

	
	//getter and setter for segment type
	public int getSegmentType() {
		return this.segmentType;
	}
	
	public void setSegmentType(int segmentType) {
		this.segmentType = segmentType;
	}
	//------------------------------------------
	

	
	//getter and setter for segment base
	public int getSegmentBase() {
		return segmentBase;
	}

	public void setSegmentBase(int segmentBase) {
		this.segmentBase = segmentBase;
	}
	//------------------------------------------

	
	//getter and setter for segment limit
	public int getSegmentLimit() {
		return segmentLimit;
	}

	public void setSegmentLimit(int segmentLimit) {
		this.segmentLimit = segmentLimit;
	}
	//------------------------------------------
	
	
	//getter and setter for the number of times this (if it was a shared segment) segment was used
	public int getTimesUsed() {
		return timesUsed;
	}
	
	public void setTimesUsed(int timesUsed) {
		this.timesUsed = timesUsed;
	}
	//---------------------------------------------------------------------------
	
	
	//simple getter and setter for the processes that share this segment only if it was of type 1
	public List<Integer> getIncludedInProcesses() {
		return this.includedInProcesses;
	}
	
	public void setIncludedInProcesses(List<Integer> includedInProcesses) {
		this.includedInProcesses = includedInProcesses;
	}
	//---------------------------------------------------------------------

	
	/**
	 * This method will be used to add a process to the list that this segment is shared with
	 * (if and only if the segment type is 0)
	 * @param process
	 * @throws IllegalArgumentException
	 */
	public void shareWithProcess(int processId) throws IllegalArgumentException{
		if(this.segmentType == 1) {
			throw new IllegalArgumentException("Segment type is private and cant be shared");
		}
		this.includedInProcesses.add(processId);	
	}
	
	
	/**
	 * This method will return the list of processes that a segment is shared with
	 * This is done by looping through the sharedProcesses List and getting the process ID's 
	 * that share this segment
	 */
	public String ListSharedProcs() {
		String procs = "";
		int lastProcIndex = this.includedInProcesses.size() -1;
		int listSize = this.includedInProcesses.size();
		for(int i = 0; i < this.includedInProcesses.size(); i++) {
			switch (listSize) {
			
			case 1:
				procs += includedInProcesses.get(i);
				break;
				
			default:
				if(i == lastProcIndex) {
					procs += "and " + includedInProcesses.get(i);
					break;
				}
				procs += includedInProcesses.get(i);
				procs += ", ";
				break;
			}	
		}
		return procs;
	}

	
}
