#include <cstdlib>
#include <iostream>
#include <fstream>
#include <string>
#include <limits>
#include <stdexcept>

using namespace std;

class Node{
public:

	int jobId;
	int time;
	Node* Next;

	Node(int id, int nodeTime){
		this->Next = NULL;
		this->jobId = id;
		this->time = nodeTime;
	}

	Node(){
		this->Next = NULL;
		this->jobId = 0;
		this->time = 0;
	}

	Node(int id){
        this->jobId = id;
        this->time = 0;
        this->Next = NULL;
    }
};

class List{

public:
	Node* dummyHead;

	List(){
		dummyHead = new Node();
	}

	Node* getHead(){
		return dummyHead;
	}

	bool isListEmpty(){
	    Node* walker = dummyHead;
	    if(walker->Next == NULL)
            return true;
        else
            return false;
	}

	void Insert(Node* newNode){
        Node* walker = dummyHead;
        while(walker->Next!=NULL){
            walker=walker->Next;
        }
        newNode->Next=walker->Next;
        walker->Next=newNode;
	}

	int remove(){
        Node* walker = dummyHead;
        int jobremove=0;
        if(walker->Next->Next!=NULL){
            jobremove = walker->Next->jobId;
            walker->Next = walker->Next->Next;
        }
        else if(walker->Next!=NULL){
            jobremove = walker->Next->jobId;
            walker->Next = NULL;
        }
        return jobremove;
	}
};

class Hash{

public:
    List** table;
    int Size;

    Hash(int size){
        Size = size;
        table = new List*[Size];
        for(int i=0;i<size;i++){
            table[i] = new List();
        }
    }

    void insert(int Parent, int Kid){
        Node* walker = table[Kid]->getHead();
        Node* newNode = new Node(Parent);

        while(walker->Next != NULL){
            walker = walker->Next;
        }
        walker->Next = newNode;
    }

    int parentCount(int job){
        Node* walker = table[job]->getHead();
        int count = 0;
        while(walker->Next != NULL){
            walker = walker->Next;
            count++;
        }
        return count;
    }

    void removeParent(int Parent){
        for(int i=0;i<Size;i++){
            Node* walker = table[i]->getHead();
            Node* prevwalker = NULL;
            while(walker->Next != NULL && walker->jobId != Parent){
                prevwalker = walker;
                walker = walker->Next;
            }
            if(walker->jobId == Parent){
                if(prevwalker == NULL)
                    table[i]->getHead()->Next = walker->Next;
                else
                    prevwalker->Next = walker->Next;
            }
        }
    }
};

    int**   scheduleTable;
    int*	processJob;
    int* 	processTime;
    int*	parentCount;
    int*	jobTime;
    int*	jobDone;
    int* 	jobMarked;
    int 	totalJobTimes=0;
    int		nodeNum;
    int 	ProcNeed;
    Hash*	DependencyGraph;
    List	Open;

    void printArray(int array[], ofstream &outFile){
        for(int i=0;i<nodeNum;i++){
            outFile<<array[i]<<" ";
        }
        outFile<<endl;
    }

    void printArrayx(int array[], ofstream &outFile){
        for(int i=1;i<=nodeNum;i++){
            outFile<<array[i]<<" ";
        }
        outFile<<endl;
    }

    void printScheduleTable(ofstream &outFile, int time){
        outFile<<endl<<"Scheduling Table: Time is "<<time<<endl;
        for(int i=0;i<ProcNeed;i++){
            outFile<<endl;
            for(int j=0;j<totalJobTimes;j++){
                if(scheduleTable[i][j]>9)
                    outFile<<scheduleTable[i][j]<<"  ";
                else
                    outFile<<scheduleTable[i][j]<<"   ";
            }
        }
        outFile<<endl;
    }

    void printStatus(ofstream &outFile){
        outFile<<endl<<"Process Job: "<<endl;
        printArray(processJob, outFile);

        outFile<<endl<<"Process Time: "<<endl;
        printArray(processTime, outFile);

        outFile<<endl<<"Parent Count: "<<endl;
        printArrayx(parentCount, outFile);

        outFile<<endl<<"Job Time: "<<endl;
        printArrayx(jobTime, outFile);

        outFile<<endl<<"Job Done: "<<endl;
        printArrayx(jobDone, outFile);

        outFile<<endl<<"Job Marked: "<<endl;
        printArrayx(jobMarked, outFile);
    }

    int getFreeProcessor(){
        for(int i=0;i<ProcNeed;i++){
			if(processJob[i]<=0){
				return i;
			}
		}
		return -1;
    }

    void ComputeScheduleTable(ifstream &inFile1, ofstream &outFile){
        int time=0;int ProcUsed=0; int jobFinished=0;int newJob=0; int availProc=0; int count=0;
        bool isGraphEmpty = false; Node* newNode;

        try{
        //step 11
        while(!isGraphEmpty){

            //step 1
            for(int i=1;i<=nodeNum;i++){
                if(parentCount[i]==0&&jobMarked[i]==0){
                    jobMarked[i]=1;
                    newNode = new Node(i, jobTime[i]);
                    Open.Insert(newNode);
                }
            }

            //step 2+3
            while(!Open.isListEmpty()&&ProcUsed<=ProcNeed){
                availProc = getFreeProcessor();
                if(availProc==-1){
                    break;
                }
                ProcUsed++;
                newJob=0;

                if(ProcUsed<=ProcNeed){
                    newJob = Open.remove();
                    processJob[availProc] = newJob;
                    processTime[availProc] = jobTime[newJob];

                    for(int t=time;t<jobTime[newJob]+time;t++){
                        scheduleTable[availProc][t] = newJob;
                    }
                }
            }

            //step 4
            if(Open.isListEmpty()){
                for(int i=0;i<ProcNeed;i++){
                    if(processTime[i]<=0)
                        count++;
                }

                if(count==ProcNeed){
                    throw invalid_argument("There's a loop in the graph, exiting.");
                }
                else
                    count=0;
            }

            //step 5
            //printScheduleTable(outFile,time);
            //printStatus(outFile);

            //step 6
            time++;

            //step 7
            for(int i=0;i<=ProcNeed;i++){
                if(processTime[i]!=0)
                    processTime[i]--;
            }

            //step 8+9
            for(int i=0;i<ProcNeed;i++){
                if(processTime[i]==0){
                    int job=processJob[i];
                    processJob[i]=0;
                    DependencyGraph->removeParent(job);
                    jobDone[job]=1;
                    ProcUsed--;
                }
            }

            //step 10
            //printScheduleTable(outFile,time);
            //printStatus(outFile);

            for(int i=0;i<=nodeNum;i++){
                parentCount[i] = DependencyGraph->parentCount(i);
                if(jobDone[i]==1)
                    jobFinished+=1;
            }

            if(jobFinished==nodeNum+1)
                isGraphEmpty=true;
            else
                jobFinished=0;
        }

        //step 12
        printScheduleTable(outFile,time);   //final schedule table
        printStatus(outFile);
        }
        catch(invalid_argument& e){
            cerr<<e.what()<<endl;
            exit (EXIT_FAILURE);
        }
    }

    void Initialization(ifstream &inFile2, ofstream &outFile){
        int nodeTime; int node;
        inFile2>>nodeNum;


        processJob = new int[nodeNum+1]();
        processTime = new int[nodeNum+1]();
        parentCount = new int[nodeNum+1]();
        jobTime = new int[nodeNum+1]();
        jobDone = new int[nodeNum+1]();
        jobMarked = new int[nodeNum+1]();

        while(!inFile2.eof()){
            inFile2>>node;
            inFile2>>nodeTime;
            jobTime[node] = nodeTime;
            totalJobTimes += nodeTime;
        }

        for(int i=1;i<=nodeNum;i++){
            parentCount[i] = DependencyGraph->parentCount(i);
        }

        scheduleTable =  new int*[nodeNum+1];
        for(int i=0;i<totalJobTimes+1;i++){
            scheduleTable[i] = new int [totalJobTimes+1];
        }

        for(int i=0;i<nodeNum+1;i++){
            for(int j=0;j<totalJobTimes;j++){
                scheduleTable[i][j]=0;
            }
        }
    }

    void FillHashTable(ifstream &inFile1){
        DependencyGraph = new Hash(nodeNum+1);
        int parent;int kid;
        while(!inFile1.eof()){
            inFile1>>parent;
            inFile1>>kid;
            DependencyGraph->insert(parent,kid);
        }
    }


    void CreateTables(ifstream &inFile1, ifstream &inFile2, ofstream &outFile){
        inFile1>>nodeNum;
        //step 0
        FillHashTable(inFile1);
        Initialization(inFile2, outFile);

        if(ProcNeed > nodeNum)
            ProcNeed = nodeNum;

        outFile<<"Number of Nodes: "<<nodeNum<<endl;
        outFile<<"Total Job Time: "<<totalJobTimes<<endl;
        outFile<<"Processor Need: "<<ProcNeed<<endl;

        ComputeScheduleTable(inFile1, outFile);
    }

        int main(int argc, char* argv[]) {

        if(argc<3){
            cout<<"usage: "<< argv[0]<<" <input1 filename> <input2 filename> <input3 int> <output filename>\n";
            return 0;
        }

        ifstream inFile1(argv[1]);
        ifstream inFile2(argv[2]);
        ProcNeed = atoi(argv[3]);
        ofstream outFile(argv[4]);

        CreateTables(inFile1,inFile2,outFile);

        inFile1.close();
        outFile.close();

        return 0;
    }


