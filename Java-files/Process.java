package package_6606598;

import java.util.ArrayList;
import java.util.List;

public class Process {
	
	private int processId;
	
	//This field represents the list of segments that this process has 
	private List<Segment> segments;
	
	//This filed will be used to track the segment number of the next new segment we want to add to the process
	private int nextSegmentNumber = 1;
	
	

	//The following 3 fields are used to keep track of the segments that have been removed
	//this will be useful for the 'PhysicalMemory' class as in the 'UpdateSegments' method we will have to deallocate
	//the removed segments from the memory allocation spaces
	private Segment removed;
	private List<Segment> previousRemovedSegment;
	private int prevRemovedSegNumber = 0;
	
	public Process(int processId, List<Segment> segmentList) {
		this.processId = processId;
		this.segments = new ArrayList<Segment>();
		this.previousRemovedSegment = new ArrayList<Segment>();
		
		//The passes List of segments in the Process constructor will automatically be added to this process using 
		//the following method
		addSegments(segmentList);
	}



	//Simple method to return the process ID
	public int getProcessId() {
		return processId;
	}

	//Simple method that returns the list of segments that this process has
	public List<Segment> getSegments() {
		return segments;
	}
	
	
	//simple getter to get the removed segment from this process
	public Segment getRemoved() {
		return this.removed;
	}
	
	
	//getter for the list of previously removed segments
	public List<Segment> getPreviousRemovedSegment() {
		return previousRemovedSegment;
	}

	
	
	
	
	/**
	 * This method will be used used to return a Segment object, from the process, specified by the segment number
	 * in the parameter.
	 * If this segment at the indicated segment number doesn't exist, an exception will be thrown
	 * @param segmentNumber
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Segment getSegment(int segmentNumber) throws IllegalArgumentException{
		Segment x = null;
		for(Segment s: this.segments) {
			if(segmentNumber == s.getSegmentNumber()) {
				x = s;
				break;
			}
		}
		if(x == null) {
			throw new IllegalArgumentException("This segment number doesn't exist in this process");
		}
		return x;
	}
	



	/**
	 * This method will be used to add a segment from the segment listed passed in the parameter.
	 * If this segment already exists in this process then the 'editsSegments' method will be called
	 * where the segment properties will be updated,
	 * otherwise the segment will be added normally to the list of segments for this process
	 * @param segmentList
	 */
	public void addSegments(List<Segment> segmentList) {
		this.removed = null;
		
		for(Segment segment: segmentList) {
			if(this.removed != null) {
				//if a segment has been previously removed then the segment
				//number of the next segment we want to allocate will be updated so that no conflicts 
				//will be caused between the other segments in the process
				segment.setSegmentNumber(prevRemovedSegNumber++);
			}
			
			if(!this.containsSegmentNumber(segment)) {
				segment.setSegmentNumber(nextSegmentNumber++);
				this.segments.add(segment);
				
			} else {
				this.editSegments(segment);
			}
		}
	}
	
	
	
	/**
	 * This private method is used to check if the segment exists in the array list or not
	 * @param segment
	 * @return
	 */
	private boolean containsSegmentNumber(Segment segment) {
		boolean output = false;
		for(Segment s: this.segments) {
			if(segment.getSegmentNumber() == s.getSegmentNumber()) {
				output = true;
				break;
			} else {
				output = false;
			}
		}
		return  output;
	}
	
	
	/**
	 * This method will compare the size of the existing segment and the updated segment, by taking 
	 * the difference, if the size is zero then this segment has to be deleted from the table and
	 * the segments in the list that come after this deleted must have their segment number updated.
	 * If the difference is not zero then we will only need to update the size of the segment
	 * @param segment
	 */
	private void editSegments(Segment segment) throws IllegalArgumentException{

		int previousSize = 0;
		int currentSegmentIndex = 0;
		
		//This loop will get the segment size and the index of the existing segment
		for(int i = 0; i < this.segments.size(); i++) {
			if(segment.getSegmentNumber() == this.segments.get(i).getSegmentNumber()) {
				previousSize = segments.get(i).getSegmentSize();
				currentSegmentIndex = i;
				break;
			}
		}
		int newSize = previousSize + segment.getSegmentSize();
		if(newSize < 0) {
			throw new IllegalArgumentException("the updated version of the segment's size cant be negative");
		}
		
		switch (newSize) {
		
		case 0: 
			//the removed segment will be added to the removed segments list and a record of their segment number
			//will be kept to update the segment number of the segments that come after it
			this.removed = this.segments.get(currentSegmentIndex);
			this.prevRemovedSegNumber = removed.getSegmentNumber();
			this.previousRemovedSegment.add(removed);
			this.segments.remove(currentSegmentIndex);
			nextSegmentNumber = segment.getSegmentNumber();
			
			//This loop will be used to update the segment number of the segments that come after the 
			//one that is deleted by calling the 'setSegmentNumber' method from the segment class
			for(Segment s: this.segments) {
				if(s.getSegmentNumber() > nextSegmentNumber && s.getSegmentNumber() != 0) {
					s.setSegmentNumber(nextSegmentNumber++);
				}
			}
			break;
		
		default:
			//Here we will get the segment and update its segment size
			this.segments.get(currentSegmentIndex).setSegmentSize(newSize);
			this.segments.get(currentSegmentIndex).setIncludedInProcesses(segment.getIncludedInProcesses());
			this.segments.get(currentSegmentIndex).setSegmentType(segment.getSegmentType());
		
			break;
		}
		
	}
	

	
	/**
	 * This method is used to calculate the size of the process by looping through
	 * through all the segments in this process and adding their sizes together
	 * (the size of the process is the summation of all the segments size's in the segments list of process)
	 */
	public int getProcessSize() {
		int totalSize = 0;
		for(Segment s: this.segments) {
			totalSize += s.getSegmentSize();
		}
		
		return totalSize;
	}
	
	
	
	
}
