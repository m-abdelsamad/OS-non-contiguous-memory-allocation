package package_6606598;

public class ProcessSegment {
	/**
	 * This is a helper class used for the TLB
	 * The TLB will contain instances of this class, which would have a process along with a segment that we want to look for
	 * or we have already looked for.
	 * 
	 * The segment object in this class contains the location (base and limit) of it in the physical memory which
	 * we will want to retrieve by calling the 'access' method in the 'PhysicalMemory' class
	 */
	
	private Process process;
	private Segment segment;
	
	public ProcessSegment(Process process, Segment segment) {
		this.process = process;
		this.segment = segment;
	}

	//simple getter and setter for the Process object
	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}
	//---------------------------------------------

	
	//Simple getter and setter for the Segment object 
	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}
	//-------------------------------------------------
	
	
	
	

}
