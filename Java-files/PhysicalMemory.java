package package_6606598;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhysicalMemory {

	//This field represents the max size of the physical memory
	private int memorySize;
		
	//This field represents the remaining size of the memory after a process has been allocated
	private int memoryLeft;
		
	//simple field that represents the base of the next segment to be allocated, the base will be updated every time
	//a segment base is assigned
	private Integer base = 0;
		
	//This list represents the processes that have been allocated to the memory
	private List<Process> processInMemory;


	//This list will be used to keep track of the segments (and their position) that have been allocated to the memory
	private LinkedList<MemoryAllocation>  memoryAllocations;
	
	//This list represents the process that are located in the 'TLB table'
	private List<ProcessSegment> tlb;
	
	//This field represents the max size of the TLB table 
	private int sizeOfTLB;
	
	
	
	public PhysicalMemory(int memorySize, int sizeOfTLB) {
		this.memorySize = memorySize;
		this.memoryLeft = memorySize;
		this.processInMemory = new ArrayList<Process>();
		this.memoryAllocations = new LinkedList<MemoryAllocation>();
		this.sizeOfTLB = sizeOfTLB;
		this.tlb = new ArrayList<ProcessSegment>();
		
		setUpMemorySegment();
	}	
	
	/**
	 * This method will be called by the constructor to initialize the memory with a free space
	 */
	private void setUpMemorySegment() {
		MemoryAllocation setUp = new MemoryAllocation(memorySize, null);
		setUp.setBase(base);
		setUp.setLimit(this.memorySize);
		setUp.setLast(true);
		setUp.setSize(memorySize);
		this.memoryAllocations.add(setUp);
	}
	
	/**
	 * This method will be used to set up a process from a 2D array.
	 * The array will be deconstructed into parts such that process Id, segments,
	 * segment type and shared processes are extracted.
	 * If this process previous exists in the memory, then it will be updated, by getting the new segments
	 * and adding them again to the process. when these segments are added again their size, segment number and shared processes
	 * list will be updated (all this is done through the method call 'addSegments()' which will be continued in the Process class.
	 * 
	 * If the process doesn't exits, then it will be added normally to the memory 
	 * @param input
	 * @return
	 */
	public Process setUpProcess(int[][] input) {
		int processId = input[0][0];
		
		List<Segment> segments = new ArrayList<Segment>();
		for(int i = 1; i < input.length; i++) {
			int segmentSize = input[i][0];
			Segment newSegment = new Segment(i,segmentSize);
			segments.add(newSegment);
			
			for(int j =1; j < input[i].length; j++) {
				if(j == 1) {
					int segmentType = input[i][1];
					newSegment.setSegmentType(segmentType);
					continue;
				}
					int procId = input[i][j];
					//Process processToShare = getProcess(procId);
					newSegment.shareWithProcess(procId);	
			}
		}
		
		if(!processExists(processId)) {			
			Process newProcess = new Process(processId, segments);
			processInMemory.add(newProcess);	
			return  newProcess;
			
		} else {
			Process p = getProcess(processId);
			p.addSegments(segments);
			return p;
		}
	}

	
	
	
	/**
	 * This method will take the new process and previous version of this new process and update the bases and limits
	 * of the new process, so that its segments know exactly which memory spaces to occupy. the memoryAllocations list will also
	 * be updated to accommodate these new segments.
	 * @param newProc
	 * @param prevProc
	 */
	private void updateSegments(Process newProc, Process prevProc) {
		int lastNewLimit = 0;
		
		int baseStart = 0;
		boolean baseStartUpdated = false;
		int lastLimit = 0;
		int index = 0;
		
		//this loop is used to get the previous bases and limits of the segments we want to remove from old process
		//so we can update the segments of the new process base on these previous bases and limits
		for(Segment s: prevProc.getSegments()) {
			this.memoryLeft += s.getSegmentSize();
			lastLimit = s.getSegmentLimit();
			
			if(s.getSegmentType() == 0 && !s.getIncludedInProcesses().isEmpty()) {
				s.setTimesUsed(s.getTimesUsed() - 1);
			}
			
			if(baseStartUpdated) {
				//here we will skip if the condition is met as we only need the start of the base since
				//the new segments of the updated process will build up on this base number
				continue;
			} 
			baseStart = s.getSegmentBase();
			baseStartUpdated = true;
		}
		
		boolean indexUpdated = false;
		
		//if the new process had a segment that was removed from the old process then we need to remove this segment from the
		//allocated memory space
		if(!newProc.getPreviousRemovedSegment().isEmpty()) {
			for(Segment s: newProc.getPreviousRemovedSegment()) {
				this.memoryAllocations.remove(this.getAllocatedMemory(s));
			}
			
			newProc.getPreviousRemovedSegment().clear();
		}
		
		//in this loop we are updating the bases and limits for the segments of the updated process
		for(Segment s: newProc.getSegments()) {
			s.setSegmentBase(baseStart);
			s.setSegmentLimit(baseStart + s.getSegmentSize());
			baseStart += s.getSegmentSize();
			lastNewLimit = s.getSegmentLimit();
			
			//if this segment already exists in the allocated memory space, then we just need to update the limits and bases
			//otherwise we will allocate a new space to the memory of the segment that we not previously in the memory
			//this new segment will be allocated exactly after the previous segment in the new Process, hence why we are keeping 
			//track on an 'index' variable just so to know where this new segment can be allocated (which is is after the previous segment)
			if(this.containsSegmentInMemory(s)) {
				MemoryAllocation ma = this.getAllocatedMemory(s);
				ma.setBase(s.getSegmentBase());
				ma.setSegmentReference(s);
				ma.setLimit(s.getSegmentLimit());
				ma.setSize(s.getSegmentSize());
				index = getAllocatedMemoryIndex(ma);
				index++;
				indexUpdated = true;
			} else {
				if(!indexUpdated) {
					index++;
				}
				MemoryAllocation newAllocation = new MemoryAllocation(s);
				this.memoryAllocations.add(index, newAllocation);
				index++;
				indexUpdated = true;
			}
			
			
		}
		
		
		//if the 'lastLimit' is bigger than the 'baseStart' then we will end up with a left over gap and this gap
		//must be added to the memory allocation space to keep the bases and the limits of the segments following consistent
		//otherwise the new segment's base and limit over lap with the segments following and therefore the bases and limits
		//of the segments following must be updated by a margin
		int difference = lastLimit - baseStart;
		
		if(difference > 0) {
			MemoryAllocation gap = new MemoryAllocation(difference, null);
			gap.setSegmentReference(null);
			gap.setBase(baseStart);
			gap.setLimit(lastLimit);
			gap.setSize(difference);
			this.memoryAllocations.add(index, gap);
			
		} else {
			int baseOfLastSeg = 0;
			if(index == this.memoryAllocations.size() - 1) {
				baseOfLastSeg = lastNewLimit;
			}
			int marginToUpdate = baseStart - lastLimit;
			for(int i = index; i < this.memoryAllocations.size(); i++) {
				if(i == (this.memoryAllocations.size() - 1)) {
					memoryAllocations.get(i).setBase(baseOfLastSeg);
					memoryAllocations.get(i).setLimit(this.memorySize);
					memoryAllocations.get(i).setSize(this.memorySize - baseOfLastSeg);
					continue;
				}
				memoryAllocations.get(i).setBase(memoryAllocations.get(i).getBase() + marginToUpdate);
				memoryAllocations.get(i).setLimit(memoryAllocations.get(i).getLimit() + marginToUpdate);
				memoryAllocations.get(i).setSize(memoryAllocations.get(i).getLimit() - memoryAllocations.get(i).getBase());
				baseOfLastSeg = memoryAllocations.get(i).getLimit();
			}
		}
		
		//if there are any gaps created they will be removed and added to the end of the memory
		if(difference != 0 || difference < 0) {
			this.shuffle();
		}
		
		if(this.getLastSegment().getSize() == 0) {
			this.memoryAllocations.remove(this.getLastSegment());
			this.memoryAllocations.get(this.memoryAllocations.size() - 1).setLast(true);;
		}
		
	}
	
	
	
	
	/**
	 * This method will be use to take an input of a 2D array and will then convert it into a process object, (this
	 * will be done when calling the 'setUpProcess' method).
	 * If the process previously exists, then we will get the segments of the Old process and create a 'Old Process Object'
	 * so that when calling the updateSegments method, the method will update the segment properties based on the old process's
	 * values.
	 * If the process doesn't exists then a new memory space will be created and will be initialized with a segment from the process.
	 *  
	 * The memory left and the base will be updated after allocating a new segment
	 * If the segment size is more than the remaining size of the memory then the segment will not be allocated and an exception
	 * will be thrown
	 * @param process
	 * @throws IllegalArgumentException
	 */
	public void allocateProcess(int[][] process) throws IllegalArgumentException{
		List<Segment> prevSegments = new ArrayList<Segment>();
		
		int procId = process[0][0];
		Process prevProc= null;
		boolean exists = false;
		
		if(this.processExists(procId)) {
			//if the process previously exists then, its segments will be added to an array 'prevSegments'
			//and a previous process object will be created so that it can be used in the 'updateSegments' method, since that
			//method will need to have knowledge of the updated process and the old process so that it can update the new segments
			//and memory allocations accordingly based on the bases and limits
			exists = true;
			 Process prev = this.getProcess(procId);
			 for(Segment s: prev.getSegments()) {
				 Segment newS = new Segment(s.getSegmentNumber(), s.getSegmentSize());
				 newS.setIncludedInProcesses(s.getIncludedInProcesses());
				 newS.setSegmentType(s.getSegmentType());
				 newS.setSegmentBase(s.getSegmentBase());
				 newS.setSegmentLimit(s.getSegmentLimit());
				 newS.setTimesUsed(s.getTimesUsed());
				 prevSegments.add(newS);
			 }
			 prevProc = new Process(procId, prevSegments);
		}
		
		//a process object will be created and initialized by the 'setUp' process method
		//if the process previously exists then, this process object will represent the updated version of the process
		Process p = this.setUpProcess(process);
		
		StringBuffer output = new StringBuffer();
		
		if(exists) {
		//call the 'updateSegments' method, it will take the new and old process to update the allocated segments of the memory (memory allocations)
			this.updateSegments(p, prevProc);
			output.append("Process " + p.getProcessId() + " has been updated\t");
		} else {
			output.append("Allocating process "+ p.getProcessId() + "\t\t");
		}
		if(p.getProcessSize() > memoryLeft) {
			throw new IllegalArgumentException("Physcal memory is full, a process cant be allocated");
		}
		
		
		for(Segment s: p.getSegments()) {
			
			//if the segment is shared with other processes, then the number of time this segment is used is updated
			//this shared segment will only be allocated once, the size and properties of this shared segment will be updated
			if(s.getSegmentType() == 0 && !s.getIncludedInProcesses().isEmpty()) {
				MemoryAllocation prevSharedSeg = this.getAllocatedSharedSegment(s);
				s.getIncludedInProcesses().add(p.getProcessId());
				if(prevSharedSeg != null && !exists) {
					this.memoryLeft -= s.getSegmentSize();
					s.setSegmentBase(prevSharedSeg.getBase());
					s.setSegmentLimit(prevSharedSeg.getLimit());
					s.setIncludedInProcesses(prevSharedSeg.getSegmentReference().getIncludedInProcesses());
					prevSharedSeg.getSegmentReference().getIncludedInProcesses().add(procId);
					prevSharedSeg.getSegmentReference().setTimesUsed(prevSharedSeg.getSegmentReference().getTimesUsed() + 1);
					this.updateSharedSegment(prevSharedSeg, s);
					continue;
				}
			}
			
			//here the base and the memory left will be updated
			this.updateBase();
			this.memoryLeft -= s.getSegmentSize();
			
			if(!exists &&this.getLastSegment().getSegmentReference() != null) {
				this.shuffle();	
			}
			
			//if the process doesn't exist, it will be allocated normally, if it does then the properties of the allocated
			//segments will have to be updated, this will be done in the 'updateSegments' method
			if(!exists) {
				MemoryAllocation thisSeg = this.normalAllocation(s);
				s.setSegmentBase(thisSeg.getBase());
				s.setSegmentLimit(thisSeg.getLimit());
			}
		}
		this.updateProcesses();
		output.append("(" +this.memoryLeft + " bytes remianing in the memory)");
		System.out.println(output);
	}
	
	
	/**
	 * This method will be used to allocate segment of a process that doesn't exist previously in the memory
	 */
	private MemoryAllocation normalAllocation(Segment segment) {
		//This list will store memory segments that we want to add to the memory and update from the
		//memory
		List<MemoryAllocation> toUpdate = new ArrayList<MemoryAllocation>();
		
		//this 'leftOver' variable will represent remaining memory left
		Integer leftOver = this.getLastSegment().getSize() - segment.getSegmentSize();
		
		
		//if the leftOver is 0, then the last segment will be filled by the new segment we are adding
		//otherwise we will have to update the existing last segment and insert the new segment we 
		//want to add
		if(leftOver == 0) {
			this.getLastSegment().setSegmentReference(segment);
			return this.getLastSegment();
		} else {
			MemoryAllocation newSeg = new MemoryAllocation(segment.getSegmentSize(), segment);
			newSeg.setBase(base);
			newSeg.setLimit(base + segment.getSegmentSize());
			newSeg.setSize(segment.getSegmentSize());
			toUpdate.add(newSeg);
			
			MemoryAllocation updatedSeg = new MemoryAllocation(leftOver, null);
			updatedSeg.setBase(newSeg.getLimit());
			updatedSeg.setLimit(updatedSeg.getBase() + leftOver);
			updatedSeg.setSize(updatedSeg.getLimit() - updatedSeg.getBase());
			updatedSeg.setLast(true);
			toUpdate.add(updatedSeg);
			
			//we are removing the last segment so we insert the new segment we want to add
			//before it and then add the updated last segment, this will be done in the following
			//loop
			this.memoryAllocations.remove(getLastSegment());

			for(MemoryAllocation ma: toUpdate) {
				this.memoryAllocations.add(ma);
			}
			
			return newSeg;
		}
	}
	
	
	
	
	
	/**
	 * This method will be used to implement the concept of compaction,
	 * All the empty holes (free spaces between the allocated segments) will be combined together and will be 
	 * added to the end. The occupied allocated segments will have their position updated too accommodate the change
	 * caused by the compaction
	 */
	private void shuffle() {
		//This List will contain all the full segments from the memory
		List<MemoryAllocation> nonEmptySegs = new ArrayList<MemoryAllocation>();
		
		//the integer will represent the size of the empty segment that we can shuffle
		int emptySegsSize = 0;
		
		int newBase = 0;
		
		//from this loop we will get the size of the empty segment and also we will get the add 
		//the non empty segments to the previous HashMap
		for(MemoryAllocation ma: this.memoryAllocations) {
			if(ma.getSegmentReference() == null) {
				emptySegsSize += ma.getSize();
			} else {
				ma.setBase(newBase);
				ma.setLimit(ma.getBase() + ma.getSize());
				newBase = ma.getBase() + ma.getSize();
				nonEmptySegs.add(ma);
			}
		}
		
		this.memoryAllocations.clear();
		
		
		for(MemoryAllocation ma: nonEmptySegs) {
			ma.setLast(false);
			this.memoryAllocations.add(ma);
		}
		
		//here we will add the new shuffled segment to the end of the HashMap so that a process can
		//fill it up
		MemoryAllocation updatedLastSeg = new MemoryAllocation(emptySegsSize, null);
		updatedLastSeg.setBase(newBase);
		updatedLastSeg.setLimit(newBase + emptySegsSize);
		updatedLastSeg.setSize(emptySegsSize);
		updatedLastSeg.setLast(true);
		this.memoryAllocations.add(updatedLastSeg);
		this.base = updatedLastSeg.getBase();
		
		this.updateProcesses();
	}
	
	
	/**
	 * This method will print the segments allocated to the memory
	 */
	public void printAllocation() {
		String output = "";
		for(MemoryAllocation ma: this.memoryAllocations) {
			if(ma.getSegmentReference() == null) {
				output += "[H" + ma.getSize() + "] ";
			} else {
				output += "[A" + ma.getSize() + "] ";
			}
		}
		System.out.println(output);
	}

	
	
	
	/**
	 * This method will be used to deallocate a process from the memory
	 * It will do so by looping through all the segments of the process and setting the memory allocation for each each
	 * segment as empty.
	 * This size of the left over memory will also be updated.
	 * 
	 * if the memory is empty and has not been allocated a process yet, then an exception will be thrown
	 * @param process
	 * @throws IllegalArgumentException
	 */
	public void deallocateProcess(int[][] process) throws IllegalArgumentException{
		if(this.processInMemory.isEmpty() || this.memoryLeft == this.memorySize){
			throw new IllegalArgumentException("Memory is empty, allocate at least one process before deallocating!");
		}
		
		int procId = process[0][0];
		Process toRemove = this.getProcess(procId);
		
		this.processInMemory.remove(toRemove);
		for(Segment s: toRemove.getSegments()) {
			if(s.getSegmentType() == 0 && !s.getIncludedInProcesses().isEmpty()) {
				if(s.getTimesUsed() - 1 != 0) {
					s.setTimesUsed(s.getTimesUsed() - 1);
					continue;
				}
				
			}
			MemoryAllocation toUpdate = this.getAllocatedMemory(s);
			toUpdate.setSegmentReference(null);
			this.memoryLeft += s.getSegmentSize();
		}
		this.shuffle();
		
		System.out.println("\nProcess " + toRemove.getProcessId() + " has been deallocated from memory (" + this.memoryLeft + " bytes remaining in memory)\n");
	
		
	}

	
	/**
	 * This method will be used to locate the position of a segment from a particular process.
	 * First the method will look for the Process and see if it exits in the TLB table, if the process
	 * doesn't exist then a 'TLB Miss' will occur and the process segment object will be added to the TLB table.
	 * IF the process already exists in the TLB table, then the position of the segment number
	 * we want will be returned.
	 * 
	 * If the process doesn't exist an exception will be thrown by the 'getProcess' method.
	 * if the the segment number doesn't exist in the process an exception will also be thrown 
	 * @param processId
	 * @param segmentNumber
	 * @throws IllegalArgumentException
	 */
	public void access(int processId, int segmentNumber) throws IllegalArgumentException{
		Process p = this.getProcess(processId);
		Segment s = p.getSegment(segmentNumber);
		String position = null;
		
		if(this.processInTLB(p, s) && this.processContainsSegment(p, segmentNumber)) {
			for(ProcessSegment ps: this.tlb) {
				if(ps.getProcess().getProcessId() == processId) {
					position = "segment " + segmentNumber + " of process " + processId +" is in the memory from " + ps.getSegment().getSegmentBase() + " to " + ps.getSegment().getSegmentLimit();
				}
			}
			
		} else {
			position = "TLB miss, try again";
			ProcessSegment newPS = new ProcessSegment(p, s);
			if(this.tlb.size() == this.sizeOfTLB) {
				this.tlb.remove(0);
			}
			this.tlb.add(newPS);
		}
		
		System.out.println(position);
	}
	
	
	//This method will be used to print the process in the memory along with the base and limit of each segment in the processes
	public void printProcesses() {
		StringBuffer output = new StringBuffer();
		for(Process p: this.processInMemory) {
			output.append("Process: "+  p.getProcessId() + "\n");
			for(Segment s: p.getSegments()) {
				
				if(s.getSegmentType() == 0 && !s.getIncludedInProcesses().isEmpty()) {
					output.append("segment "+ s.getSegmentNumber() + " located from ");
					MemoryAllocation ma = this.getAllocatedSharedSegment(s);
					if(ma == null) {
						output.append(s.getSegmentBase() + " to " + s.getSegmentLimit());
					} else {
						output.append(ma.getBase() + " to " + ma.getLimit());
					}
					
					output.append(" (shared by process(s): ");
					String sharedProcs = s.ListSharedProcs();
					output.append(sharedProcs);
					output.append(")");
				} else {
					output.append("segment "+ s.getSegmentNumber() + " located from " + s.getSegmentBase() + " to " + s.getSegmentLimit());
				}
				if(s.getSegmentBase() == 0) {
					output.append("\t\t(protection byte is " +s.getSegmentType() +")\n");
				} else {
					output.append("\t(protection byte is " +s.getSegmentType() +")\n");
				}
				
				
			}
			output.append("\n");
		}
		System.out.println(output);
	}
	
	
	//This method will be used clear the memory and rest it
	//it will be used between exercises, so that for each exercises there will be fresh new examples
	public void clearMemory() {
		this.memoryAllocations.clear();
		this.processInMemory.clear();
		this.tlb.clear();
		this.base = 0;
		this.memoryLeft = this.memorySize;
		this.setUpMemorySegment();
	}
	
	
	
	//HELPER METHODS
	
	//This method will be uses to update the size of the shared segment
	private void updateSharedSegment(MemoryAllocation prevSharedSeg, Segment s) throws IllegalArgumentException{
		
		if((prevSharedSeg.getSize() + s.getSegmentSize()) < 0) {
			throw new IllegalArgumentException("an updated version of the segments size cant be negative");
		}
		
		//if the size increases then we need to shift the allocated segments positions that come after this
		//allocated segment
		if(s.getSegmentSize() > 0) {
			int newBase = 0;
			int indexToUpdateFrom = this.getAllocatedMemoryIndex(prevSharedSeg);
			for(int i = indexToUpdateFrom; i < this.memoryAllocations.size(); i++) {
				MemoryAllocation toUpdate = this.memoryAllocations.get(i);
				if(i == indexToUpdateFrom) {
					toUpdate.setSize(toUpdate.getSize() + s.getSegmentSize());
					toUpdate.setLimit(toUpdate.getBase() + toUpdate.getSize());
					s.setSegmentBase(toUpdate.getBase());
					s.setSegmentLimit(toUpdate.getSize());
					s.setSegmentSize(toUpdate.getSize());
					newBase = toUpdate.getLimit();
					continue;
				}
				if(i == this.memoryAllocations.size() -1) {
					toUpdate.setBase(newBase);
					toUpdate.setLimit(this.memorySize);
					toUpdate.setSize(toUpdate.getLimit() - toUpdate.getBase());
					continue;
				}
				toUpdate.setBase(newBase);
				toUpdate.setLimit(newBase + toUpdate.getSize());
				newBase = toUpdate.getLimit();
			}
		}
		
		//if the size decreases then we need to create a gap/ hole between this memory allocation and the allocated 
		//segments that come after it. The shuffle method will then be called to add this free allocation space
		//to the end of the memory to avoid any fragmentation that can be cause
		if(s.getSegmentSize() < 0 && (prevSharedSeg.getSize() + s.getSegmentSize()) != 0) {
			int newSize = -1*s.getSegmentSize();
			prevSharedSeg.setSize(prevSharedSeg.getSize() + s.getSegmentSize());
			prevSharedSeg.setLimit(prevSharedSeg.getBase() + prevSharedSeg.getSize());
			
			s.setSegmentBase(prevSharedSeg.getBase());
			s.setSegmentLimit(prevSharedSeg.getSize());
			s.setSegmentSize(prevSharedSeg.getSize());
			
			int index = this.getAllocatedMemoryIndex(prevSharedSeg);
			MemoryAllocation gap = new MemoryAllocation(newSize, null);
			gap.setSegmentReference(null);
			gap.setBase(prevSharedSeg.getLimit());
			gap.setLimit(newSize+ gap.getBase());
			gap.setSize(newSize);
			this.memoryAllocations.add(index + 1, gap);
			
			this.shuffle();
		}
		//if the updated size is zero then this allocated memory segment will become free and the shuffle method
		//will be used to move this segment to the end of the memory to avoid fragmentation
		if((prevSharedSeg.getSize() + s.getSegmentSize()) == 0) {
			prevSharedSeg.setSegmentReference(null);
			this.shuffle();
		}
		
		
		
	}
	
	
	//This method will be used to return a shared segment from the me
	private MemoryAllocation getAllocatedSharedSegment(Segment segment) {
		MemoryAllocation x = null;
		for(MemoryAllocation ma: this.memoryAllocations) {
			Segment s = ma.getSegmentReference();
			if(s != null && s.getSegmentType() == 0 && s.getIncludedInProcesses().equals(segment.getIncludedInProcesses()) && segment.getSegmentNumber() == s.getSegmentNumber()) {
				x = ma;
			}
		}
		return x;
	}
	
	//This method will be used to get the last memory allocated segment (this segment can be free or occupied)
	private MemoryAllocation getLastSegment() {
		MemoryAllocation x = null;
		for(MemoryAllocation ma: this.memoryAllocations) {
			if(ma.isLast()) {
				x = ma;
			}
		}
		return x;
	}
	
	//This method will used to determine weather or not a segment number exists in a process
	//it will be used in the 'access' method to check if the parameter for the segment number should throw an
	//exception (if it doesn't exist) or not (if it exists in the process)
	private boolean processContainsSegment(Process process, int segmentNumber) {
		boolean contains = false;
		for(Segment s: process.getSegments()) {
			if(s.getSegmentNumber() == segmentNumber) {
				contains = true;
				break;
			}
		}
		
		return contains;
	}
		
		
	//This method will be used to check if the Process exists in the TLB table,
	//this method will be used to determine weather there will be a TLB miss or not
	private boolean processInTLB(Process p, Segment s) {
		boolean exists = false;
		for(ProcessSegment ps: this.tlb) {
			if(ps.getProcess().getProcessId() == p.getProcessId() && ps.getSegment().getSegmentNumber() == s.getSegmentNumber()) {
				exists = true;
				break;
			}
		}
		return exists;
	}
	
	//This method will be used to update the processes and their segments location after they have been shuffled
	//by compaction
	private void updateProcesses() {
		for(Process p: this.processInMemory) {
			for(Segment s: p.getSegments()) {
				for(MemoryAllocation ma: this.memoryAllocations) {
					if(ma.getSegmentReference() != null && ma.getSegmentReference().equals(s)) {
						s.setSegmentBase(ma.getBase());
						s.setSegmentLimit(ma.getLimit());
					}
				}
			}
		}
	}
	
	
	//This method will be used to update the base, by getting the last size of the base from the memory allocated segments
	private void updateBase() {
		int lastLimit = 0;
		for(MemoryAllocation ms: this.memoryAllocations) {
			if(!ms.isLast()) {
				lastLimit = ms.getLimit();
			}
			
		}
		this.base =lastLimit;
	}
	
	
	//This method will be used to return a MemoryAllocation object from the allocated segments, if that allocated segment
	//contains the segment we are passing as a parameter
	private MemoryAllocation getAllocatedMemory(Segment segment) {
		MemoryAllocation x = null;
		for(MemoryAllocation ma: this.memoryAllocations) {
			if(ma.getSegmentReference() != null && ma.getSegmentReference().equals(segment)) {
				x = ma;
				break;
			}
		}
		return x;
	}


	
	//This method will be used to get a process object from the memory based on the process number that is passed
	private Process getProcess(int processId) throws IllegalArgumentException {
		Process x = null;
		for(Process p: processInMemory) {
			if(p.getProcessId() == processId) {
				x = p;
				break;
			}
		}
		if(x == null) {
			throw new IllegalArgumentException(processId + " doesnt exist in the physical memory");
		}
		return x;
	}
	
	//This method will be used to check if a particular process Id exists in the memory
	private boolean processExists(int processID) {
		boolean exists = false;
		for(Process p: processInMemory) {
			if(p.getProcessId() == processID) {
				exists = true;
				break;
			}
		}
		return exists;
	}
	
	

	//This method will return the position of a memory allocated segment that exists in the memory
	//This method will be used in the 'updateSegments' method to update certain memory allocation at a certain index
	private int getAllocatedMemoryIndex(MemoryAllocation allocation) {
		int index = 0;
		for(int i = 0; i < this.memoryAllocations.size(); i++) {
			if(memoryAllocations.get(i).equals(allocation)) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	
	//This method is used to check if a segment exists in memory allocated segments list
	private boolean containsSegmentInMemory(Segment segment) {
		boolean contains = false;
		for(MemoryAllocation ma: this.memoryAllocations) {
			if(ma.getSegmentReference() != null && ma.getSegmentReference().equals(segment)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
	
	
}
