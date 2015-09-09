

public class Node {
	int jobId;
	int time;
	Node next;
	
	
	public Node(int id, int nodeTime){
		jobId = id;
		time = nodeTime;
		next = null;
	}
	
	public Node(){
		jobId = 0;
		time = 0;
		next = null;
	}
	
	public Node(int id){
		jobId = id;
		time = 0;
		next = null;
	}
}