package package_6606598;

public class MemoryAllocation {
	/**
	 * This is a helper class that is used to make the representation of a physical memory segment easier.
	 * It is used to represent all the segments in the memory (memory allocation spaces)
	 * a memory allocation can contain a segment from some process, or it can be empty (a free space for
	 * other segments that can be allocated to it)
	 */
	
	private int base; //starting position of this memory allocation space
	private int limit;	//ending position of this memory allocation space
	private Segment segmentReference; //the segment that is contained in this memory allocation space (the reference can be empty, meaning that its a free space)
	private int size;  //size of the memory allocation
	
	private boolean isLast;	//this boolean flag will be used to know and keep track of the last segment in the memory
	//the last segment in the memory should always be free (representing the left over space in the physical memory) and if it is not free it means that the memory is full
	
	
	//There is more than one constructor used to initialize a memoryallocation object to allow for flexibility
	public MemoryAllocation(Segment segmentReference) {
		this.base = segmentReference.getSegmentBase();
		this.segmentReference = segmentReference;
		this.limit = segmentReference.getSegmentLimit();
		this.size = segmentReference.getSegmentSize();
	}

	
	public MemoryAllocation(int size, Segment segment) {
		this.size = size;
		if(segment != null) {
			this.segmentReference = segment;
			this.base = segment.getSegmentBase();
			this.limit = segment.getSegmentLimit();
		}
		
	}
	

	//getter and setter for the base
	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}
	//--------------------------------------
	

	//getter and setter for the segment reference
	public Segment getSegmentReference() {
		return segmentReference;
	}

	public void setSegmentReference(Segment segmentReference) {
		this.segmentReference = segmentReference;
	}
	//---------------------------------------------------------


	//getter and setter for the limit
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	//--------------------------------------


	//getter and setter for the boolean flag 'isLast'
	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
	//--------------------------------------------------


	//getter and setter for the size of the memory allocation
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}	
	//------------------------------------------
	
	
	
	
}
