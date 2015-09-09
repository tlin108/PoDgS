import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.*;


public class Main {
		static int[][]	scheduleTable;
		static int[]	processJob;
		static int[] 	processTime;
		static int[]	parentCount;
		static int[]	jobTime;
		static int[]	jobDone;
		static int[] 	jobMarked;
		static int 		totalJobTimes=0;
		static int		nodeNum;
		static int 		ProcNeed;
		static Hash		DependencyGraph;
		static List		Open;
		
	public static void CreateTables(Scanner inFile1, Scanner inFile2, PrintWriter pWriter){
		nodeNum = inFile1.nextInt();
		//step 0
		FillHashTable(inFile1);
		Initialization(inFile2, pWriter);
		

		if(ProcNeed > nodeNum)
			ProcNeed = nodeNum;
		
		pWriter.print("Number of Nodes: ");
		pWriter.print(nodeNum);
		pWriter.println("");
		pWriter.print("Total Job Time: ");
		pWriter.print(totalJobTimes);
		pWriter.println("");
		pWriter.print("Processor Need: ");
		pWriter.print(ProcNeed);
		pWriter.println("");
		
		ComputeScheduleTable(inFile1, pWriter);
	}
	
	public static void Initialization(Scanner inFile2, PrintWriter pWriter){
		int nodeTime,node;
		nodeNum = inFile2.nextInt();
		
		processJob = new int[nodeNum+1];
		processTime = new int[nodeNum+1];
		parentCount = new int[nodeNum+1];
		jobTime = new int[nodeNum+1];
		jobDone = new int[nodeNum+1];
		jobMarked = new int[nodeNum+1];
		
		while(inFile2.hasNext()){
			node = inFile2.nextInt();
			nodeTime = inFile2.nextInt();
			jobTime[node] = nodeTime;
			totalJobTimes += nodeTime;
		}
		
		for(int i=1;i<=nodeNum;i++){
			parentCount[i] = DependencyGraph.parentCount(i);
		}
		
		scheduleTable = new int[nodeNum+1][totalJobTimes+1];
	}
	
	public static void FillHashTable(Scanner inFile1){
		DependencyGraph = new Hash(nodeNum+1);
		int parent,kid;
		while(inFile1.hasNext()){
			parent = inFile1.nextInt();
			kid = inFile1.nextInt();
			DependencyGraph.insert(parent, kid);
		}
	}
	
	public static void ComputeScheduleTable(Scanner inFile, PrintWriter pWriter){
		int time=0,ProcUsed=0,jobFinished=0,newJob = 0, availProc = 0,count=0;
		boolean isGraphEmpty = false;
		Open = new List();
		
		try{
		//step 11	
		while(!isGraphEmpty){
			
			
			//step 1
			for(int i=1;i<=nodeNum;i++){
				if(parentCount[i]==0&&jobMarked[i]==0){
					jobMarked[i]=1;
					Node newNode = new Node(i,jobTime[i]);
					Open.Insert(newNode);
				}
			}
			
			//step 2+3
			
			while(!Open.isListEmpty()&&ProcUsed<=ProcNeed){
				
				availProc=getFreeProcessor();
				
				if(availProc==-1){
					break;
				}
				ProcUsed++;
				newJob=0;
				if(ProcUsed<=ProcNeed){
					newJob=Open.remove();
					processJob[availProc] = newJob;
					processTime[availProc] = jobTime[newJob];
					
					for(int t=time;t < jobTime[newJob]+time;t++){
						scheduleTable[availProc][t] = newJob;
					}
				}
			}
			
			//step 4
			
			if(Open.isListEmpty()){
				for(int i=1;i<=ProcNeed;i++){
					if(processTime[i]<=0)
						count++;
				}
				
				if(count==ProcNeed){
					throw new Exception();
				}
				else
					count=0;
			}
		
			//step 5
			//printScheduleTable(pWriter,time);
			//printStatus(pWriter);
			
			//step 6
			time++;
			
			//step 7
			for(int i=1;i<=nodeNum;i++){
				if(processTime[i]!=0)
					processTime[i]--;
			}
			
			//step 8+9
			for(int i=1;i<=ProcNeed;i++){
				if(processTime[i]==0){
					int job=processJob[i];
					processJob[i]=0;
					DependencyGraph.removeParent(job);
					jobDone[job]=1;
					ProcUsed--;
				}
			}
			
			//step 10
			//printScheduleTable(pWriter,time);
			//printStatus(pWriter);
			
			for(int i=1;i<=nodeNum;i++){
				parentCount[i] = DependencyGraph.parentCount(i);
				if(jobDone[i]==1)
					jobFinished+=1;
			}
			
			
			if(jobFinished==nodeNum)
				isGraphEmpty=true;
			else
				jobFinished=0;
							
		}
		//step 12
		printScheduleTable(pWriter,time);	//final schedule Table 
		printStatus(pWriter);
		}
		catch(Exception e){
			System.out.println("There is a cycle in the graph, exiting");
			System.exit(0);
		}
		
	}
	
	public static int getFreeProcessor(){
		for(int i=1;i<=ProcNeed;i++){
			if(processJob[i]<=0){
				return i;
			}
		}
		return -1;
	}
	public static void printStatus(PrintWriter pWriter){
		pWriter.println("");
		pWriter.println("Process Job:");
		printArray(processJob,pWriter);
		
		pWriter.println("");
		pWriter.println("Process Time:");
		printArray(processTime,pWriter);
		
		pWriter.println("");
		pWriter.println("Parent Count:");
		printArray(parentCount,pWriter);
		
		pWriter.println("");
		pWriter.println("Job Time:");
		printArray(jobTime,pWriter);
		
		pWriter.println("");
		pWriter.println("Job Done:");
		printArray(jobDone,pWriter);
		
		pWriter.println("");
		pWriter.println("Job Marked:");
		printArray(jobMarked,pWriter);
	}
	
	public static void printScheduleTable(PrintWriter pWriter, int time){
		pWriter.println("");
		pWriter.println("Scheduling Table: Time is "+time);
		pWriter.println("");
		for(int i=1;i<=ProcNeed;i++){
			pWriter.println("");
			for(int j=0;j<totalJobTimes;j++){
				if(scheduleTable[i][j]>9)
					pWriter.print(scheduleTable[i][j]+"  ");
				else	
					pWriter.print(scheduleTable[i][j]+"   ");
			}
		}
		pWriter.println("");		
	}
	
	public static void printArray(int array[], PrintWriter pWriter){
		for(int i=1;i<=nodeNum;i++){
			pWriter.print(array[i]+" ");
		}
		pWriter.println("");
	}
	
	public static void main(String[] args){
		
		try{
			Scanner inFile1 = new Scanner(new FileReader(args[0]));
			Scanner inFile2 = new Scanner(new FileReader(args[1]));
			ProcNeed = Integer.parseInt(args[2]);
			File outFile = new File(args[3]);
			FileWriter fWriter = new FileWriter (outFile);
			PrintWriter pWriter = new PrintWriter (fWriter);
			
			CreateTables(inFile1,inFile2,pWriter);
			
			inFile1.close();
			fWriter.close();
			pWriter.close();
			
		} catch(FileNotFoundException e){
			System.out.println("File not found");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
