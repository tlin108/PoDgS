
public class Hash {
	List[] table;
	
	Hash(int size){
		table = new List[size];
		for(int i=1;i<size;i++){
			table[i] = new List();
		}
	}
	
	public void insert(int Parent, int Kid){
		Node walker = table[Kid].getHead();
		Node newNode = new Node(Parent);
		
		while(walker.next != null){
			walker = walker.next;
		}
		walker.next = newNode;
	}
	
	public int parentCount(int job){
		Node walker = table[job].getHead();
		int count = 0;                                                                                                                                                                                                                                                                                                
		while(walker.next != null){
			walker = walker.next;
			count++;
		}
		return count;
	}
	
	public void removeParent(int Parent){
		for(int i=1;i<table.length; i++){
			Node walker = table[i].getHead();
			Node prevwalker = null;
			while(walker.next != null && walker.jobId != Parent){
				prevwalker = walker;
				walker = walker.next;
			}
			if(walker.jobId == Parent){
				if(prevwalker == null)
					table[i].getHead().next = walker.next;
				else
					prevwalker.next = walker.next;
			}
		}
	}
}
