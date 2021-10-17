package package_6606598;

public class Allocation_Test {
	
	public static void main(String[] args) {
		
		System.out.println("Simulation of memory allocation\n");
		PhysicalMemory memory = new PhysicalMemory(900, 5);
		//Physical Memory is assigned 900 KB in this example
		//The 5 represents how many value the TLB list can hold, this will be demonstrated in exercise A.3.1, 'access' method
		//will be used to demonstrate the TLB exercise.
		
		/**
		 * Please note:
		 * 
		 * For printing the process in the memory --> 'printPorcesses' method will be used
		 * 
		 * For printing the segments in the following format [A100][A30][H40] --> 'printAllocation' method will be used
		 * (the printAllocation will be used in the Compaction part of this CW)
		 * 
		 * process examples are named according to their exercise number
		 * 
		 * The code is designed to use the compaction concept to avoid any fragmentation. When a processes is deallocated
		 * or a segment is updated, the free spaces created by such operations will be combined and add to the end
		 * (the last free memory allocation space). This is done to to allow processes to be allocated
		 * without any problems and avoid any issues of fragmentation.
		 * 
		 * detailed comments have been provided through out the code
		 */
		
		
		//examples for A.1
		System.out.println("---------------------------------------------------\nStart of example A.1.1\nCode Location: PhysicalMemory Class, Allocate / deallocate, setUpPrcoess and updateSegments methods (lines 248, 460, 69, and 112)\n");
		//for this example, processes will normally be allocated to/ deallocated from the memory
		//the size of the memory will be updated after allocating
		//note: if you try to allocate a process that is bigger than than the size of the remaining memory
		//left an exception will be thrown
		
		int[][] process1_a1 = {{1}, {100}, {200}, {10}};
		
		int[][] process2_a1 = {{2}, {50}, {15}, {55}};
		
		int[][] process3_a1 = {{3}, {150}, {25}};
		
		int[][] process4_a1 = {{4}, {160}, {5}};
		
		int[][] process5_a1 = {{5}, {5}, {10}, {15}};
		
		// here we are allocating the processes defined above to the memory
		memory.allocateProcess(process1_a1);
		memory.allocateProcess(process2_a1);
		memory.allocateProcess(process3_a1);
		memory.allocateProcess(process4_a1);
		memory.allocateProcess(process5_a1);
		
		//this method call we be used to display the process in the memory and the location (base and limit)
		//of each segment of each process in the memory
		System.out.println("\nShowing processes in memory:\n");
		memory.printProcesses();
		
		//now i am deallocating process 3 from the memory
		//after deallocating a process the location of the segments for the existing processes will be shifted
		//this is because empty space caused by deallocating process 3 will be shifted and added to the end of the 
		//memory
		System.out.println("\nDeallocating");
		memory.deallocateProcess(process3_a1);
		
		System.out.println("\nShowing processes in memory (after deallocating):\n");
		memory.printProcesses();
		
		System.out.println("\nEnd of example A.1.1\n---------------------------------------------------\n\n");
		
		//here i am emptying the memory by calling this method so i could start with a fresh new examples for the next
		//part of this exercise
		//this method is used for convince purposes so i don't have to create a new PhysicalMemory object for each exercise
		memory.clearMemory();
		
		
		
		System.out.println("---------------------------------------------------\nStart of example A.1.2\nCode Location: PhysicalMemory Class, Allocate / deallocate, setUpPrcoess and updateSegments methods (lines 248, 460, 69, and 112)\n");
		//for this example i will be allocating processes and then editing the size of their segments.
		//Once the size is updated the segments will shift in the memory to their new positions to
		//accommodate these size changes
		
		int[][] process1_a1_2 = {{1}, {100}, {200}, {10}};
		
		int[][] process2_a1_2 = {{2}, {50}, {15}, {55}};
		
		int[][] process3_a1_2 = {{3}, {150}, {25}};
		
		int[][] process4_a1_2 = {{4}, {160}, {5}};
		
		int[][] process5_a1_2 = {{5}, {5}, {10}, {15}};
		
		memory.allocateProcess(process1_a1_2);
		memory.allocateProcess(process2_a1_2);
		memory.allocateProcess(process3_a1_2);
		memory.allocateProcess(process4_a1_2);
		memory.allocateProcess(process5_a1_2);
		

		System.out.println("\nShowing processes in memory:\n");
		memory.printProcesses();
		
		//for process 1, segment 2 will be completely removed, and segment 3 will become segment 2 of this processes
		int[][] process1_a1_2_edit = {{1}, {-10}, {-200}, {10}};
		
		int[][] process2_a1_2_edit = {{2}, {30}, {-15}};
		
		memory.allocateProcess(process1_a1_2_edit);
		memory.allocateProcess(process2_a1_2_edit);
		
		System.out.println("\nShowing processes in memory (after updating some of them):\n");
		memory.printProcesses();
		
		
		System.out.println("\nEnd of example A.1.2\n---------------------------------------------------\n\n");
		
		//here i am emptying the memory by calling this method so i could start with a fresh new examples for the next exercise
		memory.clearMemory();
		
		
		
		//examples of A.2
		//here in these examples, I will be demonstrating the use of sharing of segments
		System.out.println("---------------------------------------------------\nStart of example A.2.1 (Read-write protection)\nCode Location: PhysicalMemory Class, Allocate / deallocate, setUpPrcoess and updateSegments methods (lines 248, 460, 69, and 112)\n");
		//for this exercise i will printing the processes with their segments, and the protection byte (1 or 0) for
		//each segment
		int[][] process1_a2 = {{1}, {100}, {200}, {10}};
		
		int[][] process2_a2 = {{2}, {50, 0, 1}, {15}, {55}};
		
		//if you try to change the segment type from 0 to 1, an exception would be thrown as segments of type 1
		//can't be shared with other segments
		int[][] process3_a2 = {{3}, {150, 0, 1}, {25}};
		
		int[][] process4_a2 = {{4}, {15, 0}, {25}, {5, 0, 1, 2, 3}};
		
		int[][] process5_a2 = {{5}, {10, 0, 1, 2}, {40}};
		
		
		memory.allocateProcess(process1_a2);
		memory.allocateProcess(process2_a2);
		memory.allocateProcess(process3_a2);
		memory.allocateProcess(process4_a2);
		memory.allocateProcess(process5_a2);
		
		System.out.println("\nShowing processes in memory:\n");
		memory.printProcesses();
		
		
		System.out.println("\nEnd of example A.2.1\n---------------------------------------------------\n\n");
		
		memory.clearMemory();
		
		
		System.out.println("---------------------------------------------------\nStart of example A.2.2 (Sharing of Segments)\nCode Location: PhysicalMemory Class, Allocate / deallocate, setUpPrcoess and updateSegments methods (lines 248, 460, 69, and 112)\n");

		//for this exercise, i will be allocating process that have shared segments, then i will update the size
		//of these shared segments as well as updating the processes that they are shared with. they will be updated 
		//in the 'edited' versions of the process defined below.
		
		int[][] process1_a2_2 = {{1}, {100}, {200}, {10}};
		
		int[][] process2_a2_2 = {{2}, {50, 0, 1}, {15}, {55}};
		
		int[][] process3_a2_2 = {{3}, {150, 0}, {25}};
		
		int[][] process4_a2_2 = {{4}, {20}, {15, 0, 1, 2, 3}};
		
		int[][] process5_a2_2 = {{5}, {5}, {5, 0}};
		
		memory.allocateProcess(process1_a2_2);
		memory.allocateProcess(process2_a2_2);
		memory.allocateProcess(process3_a2_2);
		memory.allocateProcess(process4_a2_2);
		memory.allocateProcess(process5_a2_2);
		
		System.out.println("\nShowing processes in memory:\n");
		memory.printProcesses();
		
		//now here, process 4 and 5 will have the properties of their shared segments updated
		
		//in this example you will notice that segment 2 from this process (p4) was previously shared
		//with process 1, 2 and 3, but now the size of this segment will be updated and so will the process
		//that it is shared with. (was shared with process 1, 2, 3 and 4, now it should only be shared with process 1 and 4)
		int[][] process4_a2_2_edit = {{4}, {20, 0}, {15, 0, 1}};
		
		//Segment 2 from process 5, was previously not shared with other processes, it only has the protection
		//byte 0, but not it will be shared with process 1 and 5
		int[][] process5_a2_2_edit = {{5}, {5}, {5, 0, 1}};
		
		memory.allocateProcess(process4_a2_2_edit);
		memory.allocateProcess(process5_a2_2_edit);
		
		System.out.println("\nShowing processes in memory (after being updated):\n");
		memory.printProcesses();
		
		
		System.out.println("\nEnd of example A.2.2\n---------------------------------------------------\n\n");
		
		

		//here i am emptying the memory by calling this method so i could start with a fresh new examples for the next exercise
		memory.clearMemory();
		
		
		
		System.out.println("---------------------------------------------------\nStart of example A.2.3 (Read-Write protection and sharing)\nCode Location: PhysicalMemory Class, Allocate / deallocate, setUpPrcoess and updateSegments methods (lines 248, 460, 69, and 112)\n");
		//for this example, when ever we are trying to allocate shared segments that have previously been allocated
		//by another processes, these shared segments will not be allocated again as they were previously allocated by previous
		//processes. this can be observed by the segment location when printing the processes (and their segments) that
		//have been allocated to the memory 
		//SHared segments between processes, will have the same location in the memory (since they will not be allocated twice)
		
		int[][] process1_a2_3 = {{1}, {100}, {200}, {10}};
		
		int[][] process2_a2_3 = {{2}, {50, 0, 1}, {15}, {55}};
		
		//here the first (shared) segment of process 3 will not be allocated again because it has been previously
		//in process 2, therefore when printing the location of segments from process 3, this shared first segment 
		//will have the same location as the first (shared) segment from process 2 (the size of the shared segment
		//will also be updated)
		int[][] process3_a2_3 = {{3}, {150, 0, 1, 2}, {25}};
		
		//here the first (shared) segment of process 4 will not be allocated again because it has been previously
		//in process 3, therefore when printing the location of segments from process 4, this shared first segment 
		//will have the same location as the first (shared) segment from process 3 (the size of the shared segment
		//will also be updated)
		int[][] process4_a2_3 = {{4}, {150, 0, 1, 2, 3}, {15, 0, 1, 2, 3}};
		

		int[][] process5_a2_3 = {{5}, {5}, {50, 0, 1, 2}};
		
		memory.allocateProcess(process1_a2_3);
		memory.allocateProcess(process2_a2_3);
		memory.allocateProcess(process3_a2_3);
		memory.allocateProcess(process4_a2_3);
		memory.allocateProcess(process5_a2_3);
		//you will notice that the first segment of process 2, 3 and 4 are shared together therefore they
		//will have the same location and their size will be updated
		
		System.out.println("\nShowing processes in memory:\n");
		memory.printProcesses();
		
		
		System.out.println("\nEnd of example A.2.3\n---------------------------------------------------\n\n");
		
		
		//here i am emptying the memory by calling this method so i could start with a fresh new examples for the next exercise
		memory.clearMemory();
		
		
		
		//example for A.3.1
		System.out.println("---------------------------------------------------\nStart of example A.3.1 (TLB)\nCode Location: PhysicalMemory Class, access method (line 502)\n");
		//in these example the TLB table will be used to get the location of a segment in a certain process
		//the 'access' method will be used to get the location of a segment.
		
		//the TLB has a size of '5', which is defined at the very top of the file, every time the TLB is full
		//the oldest item in the TLB will be removed to make space for the new item to be added, this will be done automatically
		//(first in - first out algorithm)
		
		int[][] process1_a3 = {{1}, {100}};
		
		int[][] process1_a3_edit = {{1}, {-10}};
		
		int[][] process2_a3 = {{2}, {50}, {30}};
		
		int[][] process2_a3_edit = {{2}, {50}, {-10}};
		
		int[][] process3_a3 = {{3}, {50}, {100}, {25}};
		
		int[][] process3_a3_edit = {{3}, {50}, {-100}, {25}};
		
		int[][] process4_a3 = {{4}, {50}, {5}};
		
		int[][] process5_a3 = {{5}, {475}};
		
		memory.allocateProcess(process1_a3);
		memory.allocateProcess(process2_a3);
		memory.allocateProcess(process2_a3_edit);
		memory.allocateProcess(process3_a3);
		memory.allocateProcess(process3_a3_edit);
		memory.allocateProcess(process4_a3);
		memory.allocateProcess(process5_a3);
		
		
		//the access method let you input a process number and a segment that you like to find the location of
		//you can change any process or segment that you would like to access
		
		//If you try to access a segment number that doesn't exist in a process or even a process that is not in the memory
		//an exception will be thrown
		System.out.println("\naccessing the location of segment from processes in the memory:\n");
		memory.access(1, 1);
		memory.access(1, 1);
		
		System.out.println();
		memory.access(3, 2);
		memory.access(3, 2);
		
		System.out.println();
		memory.access(2, 2);
		memory.access(2, 2);
		
		System.out.println();
		memory.access(4, 1);
		memory.access(4, 1);
		
		//after updating process 1, the location of all the segments will be updated to accommodate
		//this change for process 1.
		//even after updating the segments and their location, the TLB will be updated
		System.out.println();
		memory.allocateProcess(process1_a3_edit);
		
		//after updating process 1, you will still be able to get the updated location of any segment in the memory
		//you want to look for
		System.out.println();
		memory.access(1, 1);
		
		System.out.println();
		memory.access(3, 2);
		
		System.out.println();
		memory.access(2, 2);
		
		
		System.out.println();
		memory.access(4, 1);
		
		System.out.println("\n\nEnd of example A.3.1\n---------------------------------------------------\n\n");
		
		
		//here i am emptying the memory by calling this method so i could start with a fresh new examples for the next example
		memory.clearMemory();
		
		
		//example for A.3.2
		System.out.println("---------------------------------------------------\nStart of example A.3.2 (compaction)\nCode Location: PhysicalMemory Class, Allocate / deallocate, setUpPrcoess shuffle and updateSegments methods (lines 248, 460, 69, 390 and 112)\n");
				
		
		int[][] p1_a3_2 = {{1}, {100}};
		int[][] p1_a3_2_edit = {{1}, {-10}};
		
		int[][] p2_a3_2 = {{2}, {50}};
		int[][] p2_a3_2_edit = {{2}, {50}};
		
		int[][] p3_a3_2 = {{3}, {50}, {100}, {25}};
		int[][] p3_a3_2_edit = {{3}, {50}, {-100}, {25}};
		
		int[][] p4_a3_2 = {{4}, {50}};
		
		int[][] p5_a3_2 = {{5}, {475}};
		int[][] p5_a3_2_edit = {{5}, {35}, {105}, {45}};
		
		int[][] p6_a3_2 = {{6}, {10}, {5}, {5}};
		
		//The 'printAllocation' method will be used to how the segments are structured in the physical memory
		//you will notice that when deallocating or updating the size of segments of process, the location
		//of all the allocated segments will be updated to accommodate this change. if a segment or process is 
		//removed then the empty spaces that will be created will be shifted and added to the end to allow processes
		//to be allocated normally with out any issues of fragmentation (this is the concept of compaction)
		
		//i am calling the 'printAllocation' after each time i allocated/ deallocate/ update a process,
		//so that you can see how the size of the segments are changing
		memory.printAllocation();
		
		memory.allocateProcess(p1_a3_2);
		memory.printAllocation();
		
		memory.allocateProcess(p1_a3_2_edit);
		memory.printAllocation();
		
		memory.allocateProcess(p2_a3_2);
		memory.printAllocation();
		
		memory.allocateProcess(p2_a3_2_edit);
		memory.printAllocation();
		
		memory.allocateProcess(p3_a3_2);
		memory.printAllocation();
		
		memory.allocateProcess(p3_a3_2_edit);
		memory.printAllocation();
		
		memory.deallocateProcess(p3_a3_2);
		memory.printAllocation();
		
		memory.allocateProcess(p4_a3_2);
		memory.printAllocation();
		
		memory.allocateProcess(p5_a3_2);
		memory.printAllocation();
		
		memory.allocateProcess(p5_a3_2_edit);
		memory.printAllocation();
		
		memory.deallocateProcess(p4_a3_2);
		memory.printAllocation();
		
		memory.allocateProcess(p6_a3_2);
		memory.printAllocation();
		
		

		System.out.println("\nEnd of example A.3.2\n---------------------------------------------------\n\n");
		
		
		System.out.println("end of coursework");
	}

}
