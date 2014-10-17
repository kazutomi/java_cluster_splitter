import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.lang.*;


public class treeMethod {


	// in reading class
	int nestNum = 0;
	int sn = 0;// start num for substring
	String head = "  ";// use as tabs
	public treeNode root, firstLeaf, currentNode;
	// private int lineNum = 0;
	private double treemapX = 0;
	private double treemapY = 0;
	private double treemapW = 0;
	private double treemapH = 0;
	private int maxDepth = -1;
	private int maxNodeNum = -1;
	private int nodeArrayNum = clusterSplitter.getClusterNum();
	private int clusterAutoNum = -1;//-1 -> uninited
	private int leafNodeNum = -1;//-1 -> uninited
	private String timepointFileName = "";
	private String clusteringFileName = "";
	private double maxMiNNodeHeightWithLeafChild = -1.0;

	public double getMaxMiNNodeHeightWithLeafChild() {
		return this.maxMiNNodeHeightWithLeafChild;
	}

	public void setMaxLeafHeight(double maxLeafHeight) {
		this.maxMiNNodeHeightWithLeafChild = maxLeafHeight;
	}





	// you can change this queue as a variabe of a function in stead of for this class.
	// (then you should change the function which you use for the breadth first search for branchArray)
	private Queue<treeNode> firstQueue = new LinkedList<treeNode>();// only for breadth-first branch search;
	private Queue<treeNode> secondQueue = new LinkedList<treeNode>();// only for breadth-first branch search;
	private LinkedList<treeNode> nodeList = new LinkedList<treeNode>();// for holding  branches as a list in order of [height];
	private LinkedList<treeNode> clusterNodeList = new LinkedList<treeNode>();// for cluster. the size() is [custerMun]
	private treeNode[] midNodeArray;// for create clusterList//@@ del
	
//	private treeNode[] midNodeArray;

	ArrayList<ArrayList<String>> dataTable = new ArrayList<ArrayList<String>>();
	private String tableHeader = "";

	// constructor
	public treeMethod(treeNode r) {
		this.root = currentNode = r;
	}

//	public void makeData(String fn) {//@@@-> change to (inFiletimepoint, inFileCluster, outFolder)
//		this.timepointFileName = fn+".csv";
//		this.clusteringFileName = fn+"_clusterP.txt";
//	}
	public void makeData(String treeFile, String valueFile){//@@@
		this.timepointFileName = valueFile;//@@@
		this.clusteringFileName = treeFile;//@@@
		
		this.readTableFile(timepointFileName);// read csv file
		this.makeTableHeader();
//		 printDataTable();
		
		this.makeDendrogramTree(clusteringFileName);// making ("text/"+fn+".csv") ("text/AR0278_1_Signal2_nrdt.txt") tree using btObj as the root;
		this.decideClusterAutoNum();
//		this.decideClusterNum();
		this.makeNodesLists(0.0, 0.0, (float)clusterSplitter.winW, (float)clusterSplitter.winW);// make treemap information, make leaf list and midNode list.

		//old ver
//		if(ControllMethod.isWriteClusterNum()){
//			writeClusterLeafInfo(ControllMethod.getOutputFolderName());//@@@ -> declarator comment
//		}
//		 printAllLeaves();
//		 printBranchList();

//		 this.root.printNode();//print all node
	}

	void makeDendrogramTree(String filePath) {
		boolean printComment = false;
		readDendrogramFile(filePath);// dendrogram file from R
		if(printComment){
			System.out.println("----------^^ read tree ^^------------\n");
		

			if (clusterSplitter.tnObj != root) {
				System.out.println("couldn't back to root savely. the structure is wrong...");
				System.exit(1);
			} else {
				System.out.println("back to root savely.\n");
				// this.root.printNode();

//			System.out.println("----------^^ sohw read tree ^^------------\n");
			}
		}

	}

	private void decideClusterAutoNum(){// make the clusterAutoNum = log10(leaf node number)
		boolean printComments = false;
		if (printComments)System.out.println("\nuser setting cluster number -> "+clusterSplitter.getUserClusterNum());
		if(clusterSplitter.getUserClusterNum() == 0){
			int c = (int)Math.round(50.0000000000000 * Math.log10((double)(this.getLeafNodeNum()))  -140.05149978319912);
//			int c = (int)Math.rint(21.714724095162598 * Math.log10((double)(this.getLeafNodeNum())  -140.05149978319912));
			c = Math.max(20,c);
			c = Math.min(c, 75);
			
//			int c = (int)Math.round(38.46 * Math.log10((double)(this.getLeafNodeNum()) + 95.38));
//			c = c + 5-(c%5);
			clusterSplitter.setUserClusterNum(c);
			clusterSplitter.setClusterNum(c);
			
			if (printComments){
				System.out.println("adjusted cluster number -> " +c+"\n");
			}
		}
	}
	

	
	void makeNodesLists(double x, double y, double w, double h) {
		boolean printComment = false;
		initeTreemap(x, y, w, h);// for whole tree map.
		this.root.setTMFigures(0.0, 0.0, 1.0, 1.0, h / w);
		this.firstLeaf = null;
		makeLeafList(this.root);// make a list only contain leafs. -> very important list!!
		makeAllMidpoint();
//		this.printAllLeaves();//@test
		
		initAllTMInfo(root);//whats this?
		if(printComment) System.out.println("set Treemap normalized information for all nodes.\n");
		
//		this.printAllNodes();//@test
		
		/**
		//@@@@
		 * makeMidnodeList for cluster split
		 * most important change in the new vertion!!;
		//@@@@
		 *
		 */
		makeInnerNodeMethodNew();
		makeClusterValue();
		
//		printAllTree();//@test
		
	}
	
	private void makeInnerNodeMethodNew(){
		MidnodeListMethod mm = new MidnodeListMethod();
		this.midNodeArray = mm.makeMidNodesLists(this.root, this.maxMiNNodeHeightWithLeafChild);//System.out.println("midNodeArrau done");
		clusterSplitter.setClusterNum(midNodeArray.length+1);
		this.setHeightNumInMidNodeArray();
		makeClusterNodeList(clusterSplitter.getClusterNum());//need
	}
	
	private void makeInnerNodeMethodOld(){
		makeNodeList(clusterSplitter.getClusterNum());//should be renew @@ del
//		this.printNodeList();//@test
		makeNodeArray(clusterSplitter.getClusterNum());//@@ del
//		this.printNodeArray();//@test
		makeClusterNodeList(clusterSplitter.getClusterNum());//need
//		this.printClusterNodeList();//@test
	}

	private void makeAllMidpoint(){
		treeNode tarLeaf = this.firstLeaf;
		while(tarLeaf != null){
			makeMidPoint(tarLeaf);
			tarLeaf = tarLeaf.getNextLeaf();//it needs to be a depth first order
		}
		
	}
	
	private void makeMidPoint(treeNode tn){
		treeNode tarNode = tn;
		if(tarNode.isLeaf()){
			tarNode.setMidpoint(0);
			tarNode.setMidLineNum(0);
		}else{
			double point = 0.5*(tarNode.getLeft().getMembers() + tarNode.getLeft().getMidpoint() + tarNode.getRight().getMidpoint());
			tarNode.setMidpoint(point);
			double line = (0.5*(tarNode.getLeft().getMembers() + tarNode.getLeft().getMidLineNum() + tarNode.getRight().getMidLineNum()));
			tarNode.setMidLineNum((int)(line));
			if(tarNode.getPath() == 'L'){
				tarNode.setMidLineNum((int)Math.ceil(line));
			}else if(tarNode.getPath() == 'R'){
				tarNode.setMidLineNum((int)Math.floor(line));
			}//@hold
		}
		
		if(tarNode.getPath() == 'R'){
			makeMidPoint(tarNode.getParent());
		}
		
	}

	
	
	//////////////////////////////////
	///// make lists and arrays 
	////  -nodeList
	////  -nodeArray
	////  -clusterList
	////////////////////////////////
	

	// nodeList
	private void makeNodeList(int n) {// make a node list ordered by height. node number > n, but only have right order till n node.
		int counter = n;
		treeNode tn = null;
		// queue.add(this.root);
		nodeList.add(this.root);
		putChildrenToQue(this.root);
		// System.out.println("branchLList.size ="+branchLList.size());

		while (!firstQueue.isEmpty()) {//search nodes as breadth first //@@@ we beed a new queue for holding what is the next node should be in the queue for addNodeList 
			tn = firstQueue.poll();
			int index = nodeList.size() - 1;
			if ((counter > 0) || (tn.getHeight() >= nodeList.get(index).getHeight())) {// tarNod.heigh > than the last node.height in the list
				putChildrenToQue(tn);// after n nodes in BranchLList, only add children if the height are higher than the last node. 
				addNewNodeList(tn);
				counter--;
			}
		}
//		nodeArray = nodeList.toArray(new treeNode[0]);
	}
	
	private void addNodeList(int s, int t){
		// make a t nodes list from s > node list. (t>s)
	}
	
	// setHeightNums for new makeNidNodeList
	private void setHeightNumInMidNodeArray(){
		int i = 0;
		for(treeNode tn: midNodeArray){
			tn.setHeightNum(i);
			i++;
		}
	}
	
	// nodeArray
	private void makeNodeArray(int n){// coppy the first n nodes from nodeList 
		treeNode tarNod;
		midNodeArray = nodeList.toArray(new treeNode[0]);
		for(int i =0; i < n; i++){
			tarNod = midNodeArray[i];
			tarNod.setHeightNum(i);
		}
		
	}
	
	private void addNodeArray(int s, int t){
		// copy the nodes from s+1 node to t node of nodeList to join s nodes nodeArray (s>t) -> ex addNodeArray(1,2) -> put one new node to nodeArray
	}

	private void putChildrenToQue(treeNode c) {
		if (c.getLeft() != null) {
			firstQueue.add(c.getLeft());
		}
		if (c.getRight() != null) {
			firstQueue.add(c.getRight());
		}

	}

	private void addNewNodeList(treeNode c) {// with bubble sort method
		int index = nodeList.size() - 1;
		// System.out.println("index ="+index);
		while (true) {
			// System.out.println("target height ="+branchLList.get(index).getHeight()+", this height ="+c.getHeight());
			if (c.getHeight() < nodeList.get(index).getHeight()) {
				nodeList.add(index + 1, c);
				break;
			} else {
				index--;
			}
			// System.out.println(branchLList.get(index).getHeight());//@test
		}// System.out.println();//@test
	}
	
	// clusterNodeList
	private void makeClusterNodeList(int n){// for N clusters
		this.clearClusterNodeList();
		if(n<0){return;}
		if(n==1){clusterNodeList.add(this.root);return;}
		int currentN = n-2;
		treeNode tarNod;
		
		while(this.clusterNodeList.size() < n){// add n node to clusterNodeList;
			tarNod = this.midNodeArray[currentN];
//			System.out.println("heightNum:"+tarNod.getLeft().getHeightNum());//@test
			if(tarNod.getLeft().getHeightNum() > (n-2) || tarNod.getLeft().getHeightNum() == -1){//@@@ or you can make this by height insead of heightNum
				clusterNodeList.add(tarNod.getLeft());
				tarNod.getLeft().setClusterNode(true);// -> you need to set it false if you are going to make a new list
			}
//			System.out.println("heightNum:"+tarNod.getRight().getHeightNum());//@test
			if(tarNod.getRight().getHeightNum() > (n-2) || tarNod.getRight().getHeightNum() == -1){
				clusterNodeList.add(tarNod.getRight());
				tarNod.getRight().setClusterNode(true);// -> you need to set it false if you are going to make a new list
			}
			currentN --;
			
			if(currentN <0){//Error
				System.out.println("Error in making clusterNodeList");
				break;
			}
		}
	}
	
	private void clearClusterNodeList(){
		treeNode tarClstNode;
		for(int i = 0; i < this.clusterNodeList.size(); i ++){
			tarClstNode = this.clusterNodeList.get(i);
			tarClstNode.setClusterNode(false);
		}
		this.clusterNodeList.clear();
	}
	

	// you can change this as a breath-first function which also fill hira and
	// horz info.
	void initAllTMInfo(treeNode c) {// Recursion
		treeNode bt = c;
		bt.setTreemapInfo();
		if (bt.isLeaf() == false) {
			bt.left.setTreemapInfo();
			initAllTMInfo(bt.getLeft());

			bt.right.setTreemapInfo();
			initAllTMInfo(bt.getRight());
		}
	}

	void initTreeInfo() {
		// under construction
		// to change the initAllTMInfo to a non recursion function
	}
	
	void initAllMemberInfo(){
		treeNode tn = this.firstLeaf;
		while (tn != null) {
//do something.
			tn = tn.getNextLeaf();
		}
	}

	// you can change this function as a depth-first search function;
	// void makeLeafList() {// only for root
	// if (root.left != null) {
	// makeLeafList(root.left);
	// }
	// if (root.right != null) {
	// makeLeafList(root.right);
	// }
	// }

	void makeLeafList(treeNode q) {
		treeNode tn = q;
//		 System.out.println("into a makeLeafList(**)");//@@@test
		if (tn.isLeaf()) {
			if (firstLeaf == null) {
				firstLeaf = tn;
				currentNode = firstLeaf;
			} else {
				currentNode.setNextLeaf(tn);
				tn.setFormerLeaf(currentNode);
				currentNode = tn;
			}

			// // insert data from dataTable
			// System.out.println(this.dataTable.get(Integer.parseInt(nt.name.replaceAll("L",
			// ""))).get(0));
			//int l = Integer.parseInt(nt.getName().replaceAll("L", ""));
			int l = tn.getTableLine();
			tn.setProbeID(this.dataTable.get(l).get(0));
			int rowSize = this.dataTable.get(0).size();
			if(rowSize == this.dataTable.get(l).size() && clusterSplitter.isHasExtraDataRow()){
				tn.setGeneSymbol(this.dataTable.get(l).get(rowSize - 1));
			}

			Double d;
//			System.out.println(rowSize);
			for (int i = 1; i <= rowSize-1; i++) {
//				System.out.println("i="+i+"->"+this.dataTable.get(l).get(i));
				d = Double.parseDouble(this.dataTable.get(l).get(i)) + clusterSplitter.getSuppValue();
				tn.addValue(d);
				if ((tn.getValueMax() == -1) || (d > tn.getValueMax())) {
					tn.setValueMax(d);
				}
				if ((tn.getValueMin() == -1) || (d < tn.getValueMin())) {
					tn.setValueMin(d);
				}
			}

		} else {
			if (tn.left != null) {
				makeLeafList(tn.left);
			}
			if (tn.right != null) {
				makeLeafList(tn.right);
			}
		}
	}



	public void initeTreemap(double x, double y, double w, double h) {
		this.treemapX = x;
		this.treemapY = y;
		this.treemapW = w;
		this.treemapH = h;
	}






	public void makeClusterValue() {// search tree in breadth first order;
		ArrayList<treeNode> nodeArrayBF = new ArrayList<treeNode>(); // node in
		// BreadthFirst
		// order
		Queue<treeNode> tmpQ = new LinkedList<treeNode>();
		treeNode tn;

		tmpQ.add(this.root);

		while (!tmpQ.isEmpty()) {
			tn = tmpQ.poll();
			if (!tn.getLeft().isLeaf()) {
				tmpQ.add(tn.left);
			}
			if (!tn.getRight().isLeaf()) {
				tmpQ.add(tn.right);
			}
			nodeArrayBF.add(tn);
		}

		//
		for (int i = nodeArrayBF.size() - 1; i >= 0; i--) {
			tn = nodeArrayBF.get(i);
			//			System.out.println("[" + i + "]=>" + tn.getHira() + "-"+ tn.getHorz());
			tn.makeClusterValues();
			//System.out.println("[" + i + "]=>[" + tn.getHira() + "-"+ tn.getHorz()+"] => Value[" + tn.getValue().size()+"]");
		}

	}

	////////////////////////////////////////////
	//	write clusterRow and separetedClusters//
	////////////////////////////////////////////
	public void writeFilesForTominagaModule(File outD){
		writeClusterRow(outD);
		writeSeparatedClusters(outD);
	}
	
	public void writeClusterRow(File outD){
		boolean printComment = false;
		try{File file = new File(outD, "clusters.csv");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			if(printComment)System.out.println(file.getPath());
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			//
			//write process
			Queue<treeNode> tmpQ = new LinkedList<treeNode>();
			treeNode tarNod, clstNod;//target
//			for(int i = 0; i < ControllMethod.getClusterNum(); i++){
			ListIterator litr = this.clusterNodeList.listIterator();
//			while(litr.hasNext()){
			for(int i = 0; i < this.clusterNodeList.size(); i ++){
//				System.out.println("writing cluster "+(i+1));
				int j = 0;
//				clstNod = (treeNode) litr.next();
				clstNod = this.clusterNodeList.get(i);
				tarNod = clstNod;
				tmpQ.add(tarNod);
				while (!tmpQ.isEmpty()) {
					tarNod = tmpQ.poll();
					if(tarNod.isLeaf()){
						j++;
						bw.write((tarNod.getTableLine()+0)+"\n");
//						bw.write(tarNod.getProbeID()+", "+(tarNod.getTableLine()+0)+"\n");
//						bw.write(tarNod.getProbeID()+", "+tarNod.getValueOneLine()+", "+tarNod.getGeneSymbol()+", "+(i+1)+"-["+ clstNod.getHira()+"-"+clstNod.getHorz()+"]"+"\n");
					}else{
						tmpQ.add(tarNod.getLeft());
						tmpQ.add(tarNod.getRight());
					}
				}
				bw.write("\n");
			}
			//end write process
			//
			bw.close();
			if(printComment)System.out.println("saved ClusterRow");//@system
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeSeparatedClusters(File outD){
		boolean printComment = false;
		treeNode clstNode;
		for(int i = 0; i < clusterSplitter.getClusterNum(); i++){
			clstNode = this.clusterNodeList.get(i);
			try {
				File file = new File(outD, (i+1)+".csv");
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				//
				//write process
				bw.write(tableHeader+"\n");//header @@@error
				Queue<treeNode> tmpQ = new LinkedList<treeNode>();
				treeNode tarNode;//target
					tarNode = clstNode;
					tmpQ.add(tarNode);
					while (!tmpQ.isEmpty()) {
						tarNode = tmpQ.poll();
						if(tarNode.isLeaf()){
							bw.write(tarNode.getProbeID()+", "+tarNode.getValueOneLine()+"\n");
						}else{
							tmpQ.add(tarNode.getLeft());
							tmpQ.add(tarNode.getRight());
						}
					}
				//end write process
				//
				bw.close();
				if(printComment) System.out.println("saved SeparatedClusters"+(i+1));//@system
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	


	
	//////////////////////////
	// open file operation
	/////////////////////////
	void readTableFile(String filePath) {
		boolean printComment = false;
		BufferedReader br = null;
		// Open the file from the createWriter() example
		try {
			String SLine;
			br = new BufferedReader(new FileReader(filePath));// /readTreeTest10/text/AR0538_AR1004_Signals2_L20.csv

			while ((SLine = br.readLine()) != null) {
				fillDataTable(SLine.trim());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		


		if(printComment ) System.out.println("finish read tree");
		if(printComment) System.out.println();
	}
	
	void makeTableHeader(){
		boolean printComment = false;
//		for(String s: this.dataTable.get(0)){
//			this.tableHeader += s+",-";
//		}
		
		for(int i = 0; i < this.dataTable.get(0).size(); i ++){
			this.tableHeader += this.dataTable.get(0).get(i)+",";
		}
		tableHeader = tableHeader.substring(0, tableHeader.length()-1);
		if (printComment){System.out.println("[HEADER] : "+tableHeader);}
	}

	void fillDataTable(String l) {
		this.dataTable.add(new ArrayList<String>());
		String[] elements = l.split(",");
		for (int i = 0; i < elements.length; i++) {
			dataTable.get(dataTable.size() - 1).add(elements[i]);
		}
		//!!!!!!!! dont use this!!!!!!!!!//
//		while (this.dataTable.get(this.dataTable.size() - 1).size() < 17) {
//			dataTable.get(dataTable.size() - 1).add("NULL");
//		}
	}

	void readDendrogramFile(String filePath) {// make a new class and functuion -> readfunc(this)
		boolean printComment = false;
		BufferedReader br = null;
		// Open the file from the createWriter() example
		try {
			String SLine;
			br = new BufferedReader(new FileReader(filePath));
			while ((SLine = br.readLine()) != null) {
				this.findNestForTree(SLine.trim());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		if(printComment) System.out.println("finish read tree");
		if(printComment) System.out.println();
	}


	//////////////////////////
	// read string and make tree
	/////////////////////////
	
	void findNestForTree(String L){
		String tarL = L;
		// input is 1 line (and it's very very long!!) and the file contains only 1 line!!
//		System.out.println("ROOT -> "+tarL);
		tarL = this.findRootT(tarL);// make root node
		initeLeafNodeNum();
		
		while(tarL.length() > 0){
			//System.out.println("NODE -> "+tarL);
			tarL = this.findNestT(tarL);// make other node until the string's end
			
		}
	}
	
	String findRootT(String L) {
		String tarL = L;
		String returnStr = "";
		if (tarL.startsWith("(")) {
			currentNode = root;
			tarL = tarL.substring(1).trim();
			returnStr = tarL;
		}
		else{//Error if tarL is not started with "(", it's an Error format or empty.
			if (tarL.length() == 0){// empty
				System.out.println("Error -> this clustering file ["+this.clusteringFileName+"] is empty");
				System.exit(1);
			}
			else{// worng format
				System.out.println("Error -> this clustering file ["+this.clusteringFileName+"] is in a wrong format. it should started with \"(\"");
				System.exit(1);
			}
//		returnStr = "";//@hold
		}
		return returnStr; 
	}
	//@@@
	String findNestT(String tarL){
		boolean printComment = false;
		String currentC = "";// current Chunk
		String currentS = "";// current String
		int caseNum = 0;// -1->Error, 0->null 1->[(], 2->[number,], 3->[number),] midNode 4-> [number)] end
		
		////define caseNum
		if (tarL.startsWith("(")) {
			caseNum = 1;
			currentC = "(";
			currentNode = currentNode.newInnerNode();
		} else {
			////////currentS = tarL.substring(0, tarL.indexOf(",") +1);
			int comma = tarL.indexOf(",");
			if (comma > 0) {//next chunk -> [number,] or [number),]
				currentC = tarL.substring(0, comma+1);
				currentS = tarL.substring(0, comma);
				//if(currentS.charAt(currentS.length()-1) == ')'){//currentS -> [number)] -----------------> inner node
				if(tarL.charAt(comma-1) == ')'){//currentS -> [number)] -----------------> inner node
					
					caseNum = 3;
					currentS = tarL.substring(0, tarL.indexOf(")")); //delete the last ')'
					//insert val to current node. -> val -> double(currentS)
					currentNode.setHeight(Double.parseDouble(currentS));
					currentNode.setLeftMem(currentNode.getLeft().getMembers());//set leftMem
					currentNode.setRightMem(currentNode.getRight().getMembers());// set rightMem
					currentNode.setMembers(currentNode.getLeftMem() + currentNode.getRightMem());// set members
					if((currentNode.getLeftMem() == 1 || currentNode.getRightMem() == 1) && currentNode.getHeight() > this.maxMiNNodeHeightWithLeafChild){
						this.maxMiNNodeHeightWithLeafChild = currentNode.getHeight();
					}
					currentNode = currentNode.getParent();
					
					
				}else{//currentS -> [number] ----------------------------------------------------------> leaf node
					//make leaf -> leaf name -> currentS
					
					caseNum = 2;
					treeNode child;
					child = currentNode.newLeafNode(Integer.parseInt(currentS));
					this.setLeafNodeNum(this.getLeafNodeNum() +1);
					
				}
			} else if (comma == -1) {// no "," -> supposed to be the last chunk
				if(tarL.charAt(tarL.length()-1) == ')'){//-> current node should back to root.　----------> back to root
					
					caseNum = 4;
					if (currentNode == this.root){
						if(printComment) System.out.println("back to root safely!");
						currentC = tarL;
						currentS = tarL.substring(0, tarL.length()-1);
//						System.out.println("currentS -> "+currentS);//@test
						currentNode.setHeight(Double.parseDouble(currentS));
						currentNode.setLeftMem(currentNode.getLeft().getMembers());//set leftMem
						currentNode.setRightMem(currentNode.getRight().getMembers());// set rightMem
						currentNode.setMembers(currentNode.getLeftMem() + currentNode.getRightMem());// set members
					}else{//Error didnt back to the root
						if(printComment) System.out.println("did not back to the root -> the clustering file structure might wrong");//System Error
					}
//

				}else{//Error this file does't end with ')'
					caseNum = -1; //Error
					System.out.println("Error -> no \')\' found after the last \',\'");//@@@
					tarL ="";

				}
			} else {// Error only "," it's not a right form // comma == 0 -> [,*?] because it will be [(,] or [,,] from last substring operation 
				caseNum = -1;//Error
				System.out.println("Error -> substring starts with \',\'");//@@@//@@@
				tarL ="";
			}
		}
		
		switch(caseNum){
		case -1: ; break;
		case 0: ; break;
		case 1: ; break;
		case 2: ; break;
		case 3: ; break;
		case 4: ; break;
		}
		
		if(tarL.length() == 0){
			System.exit(1);
		}else{
//			System.out.println(currentC+" -> "+caseNum);
			tarL = tarL.substring(currentC.length()).trim();
		}
		return tarL;
	}

	/////////////////////////////
	///// print functions  /////
	///////////////////////////

	private void printAllLeavesRC(treeNode c) {	//@del// with ReCursion
		treeNode tn = c;
		tn.printNodeAllInfo();
		if (tn.getNextLeaf() != null) {
			printAllLeavesRC(tn.getNextLeaf());
		}
	}

	private void printAllLeaves() {
		System.out.println("vvv all leaves ");//@system
		treeNode tn = this.firstLeaf;
		while (tn != null) {
			tn.printNodeAllInfo();
			tn = tn.getNextLeaf();
		}
		System.out.println("^^^ all leaves");//@system
	}
	
	private void printNodeArray() {
		System.out.println("vvv nodeArray[]");//system
		treeNode bt;
		int i = 0;
		while (i < midNodeArray.length) {
			bt = midNodeArray[i];
			bt.printNodeAllInfo();
			i++;
		}
		System.out.println("^^^ nodeArray[]");//system
	}
	private void printNodeList() {
		System.out.println("vvv NodeList<>");//system
		treeNode bt;
		int i = 0;
		while (i < nodeList.size()) {
			bt = nodeList.get(i);
			bt.printNodeAllInfo();
			i++;
		}
		System.out.println("^^^ NodeList<>");//system
	}
	private void printClusterNodeList() {
		System.out.println("vvv clusterNodeList<>");//system
		treeNode bt;
		int i = 0;
		while (i < clusterNodeList.size()) {
			bt = clusterNodeList.get(i);
			bt.printNodeAllInfo();
			i++;
		}
		System.out.println("^^^ clusterNodeList<>");//@system
	}
	
	private void printAllNodes(){//breadth first
		System.out.println("vvv all Nodes");//system
		Queue<treeNode> tempoQueue = new LinkedList<treeNode>();
//		int counter = n;//@del
		treeNode tn = null;
		tempoQueue.add(this.root);

		while (!tempoQueue.isEmpty()) {//search nodes as breadth first //@@@ we beed a new queue for holding what is the next node should be in the queue for addNodeList 
			tn = tempoQueue.poll();
			tn.printNodeAllInfo();
		// put the children to the tempoQueue
			if (tn.getLeft() != null) {
				tempoQueue.add(tn.getLeft());
			}
			if (tn.getRight() != null) {
				tempoQueue.add(tn.getRight());
			}
		}
		System.out.println("^^^ all Nodes");//system
	}

	private void printAllTree(){
		System.out.println("vvv tree");//@system
		System.out.println(TreeUtility.makeAllTreePathway(this, "UP"));//@system
		System.out.println("^^^ tree");//@system
	}
	
	void printDataTable() {
		// System.out.println(dataTable);
		for (int i = 0; i < dataTable.size(); i++) {
			for (int j = 0; j < dataTable.get(i).size(); j++) {
				System.out.print(dataTable.get(i).get(j));
			}
			System.out.println();
		}
	}
	
	//@@@
	private void initeLeafNodeNum(){
		this.setLeafNodeNum(0);		
	}
	
	//////////////////////////////////
	///// getter and setter     /////
	////////////////////////////////
	public int getClusterAutoNum() {
		return clusterAutoNum;
	}

	public void setClusterAutoNum(int clusterAutoNum) {
		this.clusterAutoNum = clusterAutoNum;
	}

	public int getLeafNodeNum() {
		return leafNodeNum;
	}

	public void setLeafNodeNum(int leafNodeNum) {
		this.leafNodeNum = leafNodeNum;
	}

//	public int getClusterNum() {
//		return this.clusterNum;
//	}
//
//	public void setClusterNum(int clusterNum) {
//		this.clusterNum = clusterNum;
//	}

	public int getNestNum() {
		return nestNum;
	}

	public void setNestNum(int nestNum) {
		this.nestNum = nestNum;
	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public treeNode getRoot() {
		return root;
	}

	public void setRoot(treeNode root) {
		this.root = root;
	}

	public treeNode getFirstLeaf() {
		return firstLeaf;
	}

	public void setFirstLeaf(treeNode firstLeaf) {
		this.firstLeaf = firstLeaf;
	}

	public treeNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(treeNode currentNode) {
		this.currentNode = currentNode;
	}

	public String getTimepointFileName() {
		return timepointFileName;
	}

	public void setTimepointFileName(String timepointFileName) {
		this.timepointFileName = timepointFileName;
	}

	public String getClusteringFileName() {
		return clusteringFileName;
	}

	public void setClusteringFileName(String clusteringFileName) {
		this.clusteringFileName = clusteringFileName;
	}

	public Queue<treeNode> getQueue() {
		return firstQueue;
	}

	public void setQueue(Queue<treeNode> queue) {
		this.firstQueue = queue;
	}

	public LinkedList<treeNode> getBranchLList() {
		return nodeList;
	}

	public void setBranchLList(LinkedList<treeNode> branchLList) {
		this.nodeList = branchLList;
	}

	public LinkedList<treeNode> getClusterList() {
		return clusterNodeList;
	}

	public void setClusterList(LinkedList<treeNode> clusterList) {
		this.clusterNodeList = clusterList;
	}

	public treeNode[] getBranchArray() {
		return midNodeArray;
	}

	public void setBranchArray(treeNode[] branchArray) {
		this.midNodeArray = branchArray;
	}

	public ArrayList<ArrayList<String>> getDataTable() {
		return dataTable;
	}

	public void setDataTable(ArrayList<ArrayList<String>> dataTable) {
		this.dataTable = dataTable;
	}

	public void setTreemapX(double treemapX) {
		this.treemapX = treemapX;
	}

	public void setTreemapY(double treemapY) {
		this.treemapY = treemapY;
	}

	public void setTreemapW(double treemapW) {
		this.treemapW = treemapW;
	}

	public void setTreemapH(double treemapH) {
		this.treemapH = treemapH;
	}
	

	public double getTreemapX() {
		return this.treemapX;
	}

	public double getTreemapY() {
		return this.treemapY;
	}

	public double getTreemapW() {
		return this.treemapW;
	}

	public double getTreemapH() {
		return this.treemapH;
	}
	
	
	
	

	private static class MidnodeListMethod{
		private ArrayList<treeNode> shortList = new ArrayList<treeNode>();// temporary for sorting
		private LinkedList<treeNode> longList = new LinkedList<treeNode>();// the midpoint list from root to the midnode that have higher midpoint the the maxHeight;
		private int previouseDifferentHeightIndex = -1;
		private boolean longListCompleat = false;
		private double maxHeight = -1;
		private boolean printComment = false;
		

		private treeNode[] makeMidNodesLists(treeNode rt, double mh){
			
			treeNode[] longListArray = null;
			
//			initLists(rt);
			maxHeight = mh;
			shortList.add(rt);// init shortList
			
			//make longList
			while(!longListCompleat){
				if(printComment) {
					this.printShortList();
					this.printLongList();
					System.out.println("");
				}
				renewLists();// sort midnodes in shortList
			}
			if(printComment) {
				System.out.println("finalized lists");
				this.printShortList();
				this.printLongList();
				System.out.println("");
				System.out.println("preIndex = "+previouseDifferentHeightIndex+"\n");
			}
			
			// to decide how long the longList should be
			if(clusterSplitter.getSplitMood() == '-'
					|| (clusterSplitter.getSplitMood() == '|'
					&& (clusterSplitter.getUserClusterNum() -2) - previouseDifferentHeightIndex < longList.size() - (clusterSplitter.getUserClusterNum()-2))){
//				return longList.subList(0, previouseDifferentHeightIndex);
//				longListArray = (treeNode[]) longList.subList(0, previouseDifferentHeightIndex).toArray();
				longListArray = longList.subList(0, previouseDifferentHeightIndex+1).toArray(new treeNode[previouseDifferentHeightIndex+1]);
				
				if(printComment){
					System.out.println("returning shorted longListArray:");
					printTreeNodeArray(longListArray);
				}
			} else{
				longListArray = longList.toArray(new treeNode[longList.size()]);
				
				if(printComment){
					System.out.println("returning whole longListArray:");
					printTreeNodeArray(longListArray);
				}
			}
			
			
			
			
			return longListArray;
		}	
		
		private void printTreeNodeArray(treeNode[] a){
			System.out.print("longListArray :");
			for(treeNode tarNode: a){
				System.out.print(tarNode.getHira()+"-"+tarNode.getHorz()+"("+tarNode.getHeight()+"), ");
			}
			System.out.println("");
		}
		
//		private void initLists(treeNode rt){
//			shortList.add(rt);
//		}

		private void renewLists(){
			if(shortList.isEmpty()){previouseDifferentHeightIndex = longList.size()-1;longListCompleat = true; return;}//no more qualified(higher height midnode) node to sort-> no trim to the longList!!
			
			treeNode tarNode = shortList.get(0);//take the first node in the shortList as the tarNode;
			shortList.remove(0);// eliminate tarNode from shortList
			if(longList.size()>0){
				if (tarNode.getHeight() != longList.get(longList.size()-1).getHeight()){//if tarNode doesn't have the same height as the last node in longList
					if (longList.size() == clusterSplitter.getUserClusterNum()-1){
						longListCompleat = true;
						return;// (and copy the longList as the nodeList for split clusters)
					}
					previouseDifferentHeightIndex = longList.size()-1;//previousDifferentHeightNode place is the .size()-2 (the node before the last node in the longList)
				}// if the tarNode has the same height as the last node in Longlist, then preHeightIndex do not change.
			}
			
			longList.add(tarNode);
			
			if(previouseDifferentHeightIndex > -1 && longList.size() > clusterSplitter.getUserClusterNum()-1 && clusterSplitter.getSplitMood() == '-'){// is the mood is '-' then do not need any longer longList.
				longListCompleat = true; return;
			}
			

			treeNode[] tarChildren = {tarNode.getLeft(), tarNode.getRight()};// get tarNode's children as an array
			for(treeNode c: tarChildren){// for all the children
				if(c.getHeight() > maxHeight){// if the height is higher than the maxLeafHeight
					shortList.add(binarySearchForShortList(c.getHeight()), c);// insert the child to the shortList sorted by its height
//					System.out.println("add a new Node to the shortList");
				}
			}
			
			

		}
		
		private void printShortList(){
			System.out.print("shortlist: ");
			ListIterator litr = this.shortList.listIterator();
			while(litr.hasNext()){
				treeNode tarNode = (treeNode) litr.next();
				System.out.print(tarNode.getHira()+"-"+tarNode.getHorz()+"("+tarNode.getHeight()+"), ");
			}
			System.out.print("\n");
		}
		
		private void printLongList(){
			System.out.print("Longlist: ");
			ListIterator litr = this.longList.listIterator();
			while(litr.hasNext()){
				treeNode tarNode = (treeNode) litr.next();
				System.out.print(tarNode.getHira()+"-"+tarNode.getHorz()+"("+tarNode.getHeight()+"), ");
			}
			System.out.print("\n");
		}

		/**
		 * @author rh
		 *	get a new height, then find a index to place the node, sorted by height
		 *	return the insert place for the newNode; 
		 */
		private int binarySearchForShortList(double h){	//System.out.println("get into binarySearchFoerShortList");
			double midHeight, newHeight = h;
			int firstIndex, lastIndex, midIndex;
			int indexGap = -1;// -1 -> undefined;
			
			/**
			 * compare the newHeight with the first node's height and the last node's height
			 * - if newHeight higher than the first node's height, then return place as 0;
			 * - if newHeight lower than the last node's height, then return place size of shortList;
			 * if the newHeight is in the range -> bigIndex is the first one's, smallIndex is the last one's;
			 * - keep search the place until bigIndex and smallIndex is beside, then the place is the middle of the two.
			 */
			
			if (shortList.size() == 0){return 0;}
			
			if(newHeight >= shortList.get(0).getHeight() || newHeight <= shortList.get(shortList.size()-1).getHeight()){//compare the newHeight with the first node's height and the last node's height
				if(newHeight >= shortList.get(0).getHeight()){//* - if newHeight higher than the first node's height, 
					return 0;//then return place as 0;
				} else{//* - if newHeight lower than the last node's height, 
					return shortList.size();//then return place size of shortList;
				}
			} else{
				firstIndex = 0;//* if the newHeight is in the range -> bigIndex is the first one's, smallIndex is the last one's;
				lastIndex = shortList.size()-1;
				indexGap = lastIndex - firstIndex;
				midIndex = firstIndex + indexGap/2;
				midHeight = shortList.get(midIndex).getHeight();
				while(indexGap > 1){//* - keep search the place until bigIndex and smallIndex is beside, then the place is the middle of the two.
//					System.out.println("in the while loop");
					if(newHeight == midHeight){
						return midIndex;
					} else if(newHeight > midHeight){
						lastIndex = midIndex;
					} else{
						firstIndex = midIndex;
					}
					indexGap = lastIndex - firstIndex;
					midIndex = firstIndex + indexGap/2;
					midHeight = shortList.get(midIndex).getHeight();
				}
				return lastIndex;
			}
			
		}
		
		
			
	}
}

