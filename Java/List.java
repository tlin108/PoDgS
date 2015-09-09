
public class List {
	Node dummyHead;
	
	List(){
		dummyHead = new Node();
	}
	
	Node getHead(){
		return dummyHead;
	}
	
	public boolean isListEmpty(){
		Node walker = dummyHead;
		if(walker.next == null)
			return true;
		else
			return false;
	}
	public void Insert(Node newNode){
		Node walker = dummyHead;
		while(walker.next!=null){
			walker=walker.next;
		}
		newNode.next=walker.next;
		walker.next=newNode;
	}
	
	public int remove(){
		Node walker = dummyHead;
		int jobremove=0;
		if(walker.next.next!=null){
			jobremove = walker.next.jobId;
			walker.next = walker.next.next;
		}
		else if(walker.next!=null){
			jobremove = walker.next.jobId;
			walker.next = null;
		}
		return jobremove;
				
	}
}
