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
	private int nodeArrayNum = clusterSplitter.getIdealClusterNum();
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
	private treeNode[] upperNodeArray;// for create clusterList//@@ del
	
//	private treeNode[] midNodeArray;
	ArrayList<String> tableDataString = new ArrayList<String>();
//	ArrayList<ArrayList<String>> dataTable = new ArrayList<ArrayList<String>>();
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
		boolean printData = false;
		this.timepointFileName = valueFile;//@@@
		this.clusteringFileName = treeFile;//@@@
		
		this.readTableFile(timepointFileName);// read csv file
//		this.makeTableHeader();
//		if(printData)printDataTable();
		
		this.makeDendrogramTree(clusteringFileName);// making ("text/"+fn+".csv") ("text/AR0278_1_Signal2_nrdt.txt") tree using btObj as the root;
		this.decideClusterAutoNum();

		this.makeNodesLists(0.0, 0.0, (float)clusterSplitter.winW, (float)clusterSplitter.winW);// make treemap information, make leaf list and midNode list.


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
			int c = (int)Math.round(50.0 * Math.log10((double)(this.getLeafNodeNum()))  -140.05149978319912);
//			int c = (int)Math.rint(21.714724095162598 * Math.log10((double)(this.getLeafNodeNum())  -140.05149978319912));
			c = Math.max(20,c);
			c = Math.min(c, 75);
			
//			int c = (int)Math.round(38.46 * Math.log10((double)(this.getLeafNodeNum()) + 95.38));
//			c = c + 5-(c%5);
//			clusterSplitter.setUserClusterNum(c);
			clusterSplitter.setIdealClusterNum(c);
			
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
//		makeAllMidpoint();//make mid point (for dendrogram's vertical lines position)
//		this.printAllLeaves();//@test
		
//		initAllTMInfo(root);//whats this?// set treemap info
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
//		makeClusterValue();
		
//		printAllTree();//@test
		
	}
	
	private void makeInnerNodeMethodNew(){
		MidnodeListMethod mm = new MidnodeListMethod();
		this.upperNodeArray = mm.makeMidNodesLists(this.root, this.maxMiNNodeHeightWithLeafChild);//System.out.println("midNodeArrau done");
		clusterSplitter.setRealClusterNum(upperNodeArray.length+1);
//		this.setHeightNumInMidNodeArray();
		makeClusterNodeList(clusterSplitter.getRealClusterNum());//need //
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
		for(treeNode tn: upperNodeArray){
			tn.setHeightNum(i);
			i++;
		}
	}
	
	// nodeArray
	private void makeNodeArray(int n){// coppy the first n nodes from nodeList 
		treeNode tarNod;
		upperNodeArray = nodeList.toArray(new treeNode[0]);
		for(int i =0; i < n; i++){
			tarNod = upperNodeArray[i];
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
		if(n==1){
			getClusterNodeList().add(this.root);
			return;
		}
		
		for(treeNode tarNode : this.upperNodeArray){
			this.getClusterNodeList().remove(tarNode);
			this.getClusterNodeList().addLast(tarNode.getLeft());
			this.getClusterNodeList().addLast(tarNode.getRight());
//			tarNode.getLeft().setClusterNode(true);//this is for treemap
//			tarNode.getRight().setClusterNode(true);//this is for treemap
		}
		
	}
	
	private void clearClusterNodeList(){
		treeNode tarClstNode;
		for(int i = 0; i < this.getClusterNodeList().size(); i ++){
			tarClstNode = this.getClusterNodeList().get(i);
			tarClstNode.setClusterNode(false);
		}
		this.getClusterNodeList().clear();
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
			
			/*
			int l = tn.getTableLine();
			tn.setProbeID(this.dataTable.get(l).get(0));
			int rowSize = this.dataTable.get(0).size();
			if(rowSize == this.dataTable.get(l).size() && clusterSplitter.isHasExtraDataRow()){
				tn.setGeneSymbol(this.dataTable.get(l).get(rowSize - 1));
				
			}
			*/
			
			////
			//// convert text to double for treeNode value
			////
//			Double d;
////			System.out.println(rowSize);
//			for (int i = 1; i <= rowSize-2; i++) {//rowSize-2 because the last row is GO symbols
////				System.out.println("i="+i+"->"+this.dataTable.get(l).get(i));
//				d = Double.parseDouble(this.dataTable.get(l).get(i)) + clusterSplitter.getSuppValue();
//				tn.addValue(d);
//				if ((tn.getValueMax() == -1) || (d > tn.getValueMax())) {
//					tn.setValueMax(d);
//				}
//				if ((tn.getValueMin() == -1) || (d < tn.getValueMin())) {
//					tn.setValueMin(d);
//				}
//			}

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
			ListIterator litr = this.getClusterNodeList().listIterator();
//			while(litr.hasNext()){
			for(int i = 0; i < this.getClusterNodeList().size(); i ++){
//				System.out.println("writing cluster "+(i+1));
				int j = 0;
//				clstNod = (treeNode) litr.next();
				clstNod = this.getClusterNodeList().get(i);
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
		for(int i = 0; i < clusterSplitter.getRealClusterNum(); i++){
			clstNode = this.getClusterNodeList().get(i);
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
				bw.write(tableDataString.get(0)+"\n");
//				bw.write(dataTableLine(0)+"\n");//header @@@error
				Queue<treeNode> tmpQ = new LinkedList<treeNode>();
				treeNode tarNode;//target
					tarNode = clstNode;
					tmpQ.add(tarNode);
					while (!tmpQ.isEmpty()) {
						tarNode = tmpQ.poll();
						if(tarNode.isLeaf()){
							bw.write(tableDataString.get(tarNode.getTableLine())+"\n");
//							bw.write(dataTableLine(tarNode.getTableLine())+"\n");
//							bw.write(tarNode.getProbeID()+","+tarNode.getValueOneLine()+"\n");
							
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
	
/*
	String dataTableLine(int n){
		String s = "";
		Iterator itr = this.dataTable.get(n).iterator();
		while(itr.hasNext()){
			s+=itr.next()+",";
		}
		s = s.substring(0, s.length()-1);
		return s;
	}
	*/
	
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
				fillTableDataString(SLine);
//				fillDataTable(SLine.trim());
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
	
//	void makeTableHeader(){
//		boolean printComment = false;
////		for(String s: this.dataTable.get(0)){
////			this.tableHeader += s+",-";
////		}
//		
//		for(int i = 0; i < this.dataTable.get(0).size(); i ++){
//			this.tableHeader += this.dataTable.get(0).get(i)+",";
//		}
//		tableHeader = tableHeader.substring(0, tableHeader.length()-1);
//		if (printComment){System.out.println("[HEADER] : "+tableHeader);}
//	}

	private void fillTableDataString(String sLine) {
		tableDataString.add(sLine);
	}

	/*
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
	*/

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
				System.err.println("Error -> this clustering file ["+this.clusteringFileName+"] is empty");
				System.exit(1);
			}
			else{// worng format
				System.err.println("Error -> this clustering file ["+this.clusteringFileName+"] is in a wrong format. it should started with \"(\"");
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
					System.err.println("Error -> no \')\' found after the last \',\' in dendrogram file");//@@@
					tarL ="";
					System.exit(1);
				}
			} else {// Error only "," it's not a right form // comma == 0 -> [,*?] because it will be [(,] or [,,] from last substring operation 
				caseNum = -1;//Error
				System.err.println("Error -> substring starts with \',\' in reading dendrogram");//@@@//@@@
				tarL ="";
				System.exit(1);
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
		while (i < upperNodeArray.length) {
			bt = upperNodeArray[i];
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
		while (i < getClusterNodeList().size()) {
			bt = getClusterNodeList().get(i);
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
	
	/*
	void printDataTable() {
		// System.out.println(dataTable);
		for (int i = 0; i < dataTable.size(); i++) {
			for (int j = 0; j < dataTable.get(i).size(); j++) {
				System.out.print(dataTable.get(i).get(j));
			}
			System.out.println();
		}
	}
	*/
	
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
		return getClusterNodeList();
	}

	public void setClusterList(LinkedList<treeNode> clusterList) {
		this.setClusterNodeList(clusterList);
	}

	public treeNode[] getBranchArray() {
		return upperNodeArray;
	}

	public void setBranchArray(treeNode[] branchArray) {
		this.upperNodeArray = branchArray;
	}

//	public ArrayList<ArrayList<String>> getDataTable() {
//		return dataTable;
//	}

//	public void setDataTable(ArrayList<ArrayList<String>> dataTable) {
//		this.dataTable = dataTable;
//	}

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
	
	
	
	

	public LinkedList<treeNode> getClusterNodeList() {
		return clusterNodeList;
	}

	public void setClusterNodeList(LinkedList<treeNode> clusterNodeList) {
		this.clusterNodeList = clusterNodeList;
	}





	private static class MidnodeListMethod{
		private ArrayList<treeNode> tempList= new ArrayList<treeNode>();// temporary for sorting
		private LinkedList<treeNode> resultList = new LinkedList<treeNode>();// the midpoint list from root to the midnode that have higher midpoint the the maxHeight;
		private int predifIndex = -1;// previous different height index
		private boolean resultListCompleat = false;
		private double maxHeight = -1;
		private boolean printComment = false;//XXX comment XXX
		
		private treeNode[] makeMidNodesLists(treeNode rt, double mh){
			treeNode[] resultListArray = null;
			maxHeight = mh;
			tempList.add(rt);// init tempList
			
			//make resultList
			while(!resultListCompleat){
				if(printComment) {
					this.printtempList();
					this.printresultList();
					System.out.println("");
				}
				renewLists();// sort midnodes in tempList
			}
			if(printComment) {
				System.out.println("finalized lists");
				this.printtempList();
				this.printresultList();
				System.out.println("");
				System.out.println("preIndex = "+predifIndex+"\n");
			}
			
			// to decide how long the resultList should be
				resultListArray = resultList.toArray(new treeNode[resultList.size()]);

				
			if(printComment){
				System.out.println("returning whole resultListArray:");
				printTreeNodeArray(resultListArray);			
			}
			
			return resultListArray;
		}	
	
		private treeNode[] makeMidNodesLists_old(treeNode rt, double mh){
			
			treeNode[] resultListArray = null;
			
//			initLists(rt);
			maxHeight = mh;
			tempList.add(rt);// init tempList
			
			//make resultList
			while(!resultListCompleat){
				if(printComment) {
					this.printtempList();
					this.printresultList();
					System.out.println("");
				}
				renewLists();// sort midnodes in tempList
			}
			if(printComment) {
				System.out.println("finalized lists");
				this.printtempList();
				this.printresultList();
				System.out.println("");
				System.out.println("preIndex = "+predifIndex+"\n");
			}
			
			// to decide how long the resultList should be
			if(clusterSplitter.getUserSplitMood() == '-'
					|| (clusterSplitter.getUserSplitMood() == '|'
					&& (clusterSplitter.getUserClusterNum() -2) - predifIndex < resultList.size() - (clusterSplitter.getUserClusterNum()-2))){
//				return resultList.subList(0, previouseDifferentHeightIndex);
//				resultListArray = (treeNode[]) resultList.subList(0, previouseDifferentHeightIndex).toArray();
				resultListArray = resultList.subList(0, predifIndex+1).toArray(new treeNode[predifIndex+1]);
				
				if(printComment){
					System.out.println("returning shorted resultListArray:");
					printTreeNodeArray(resultListArray);
				}
			} else{
				resultListArray = resultList.toArray(new treeNode[resultList.size()]);
				
				if(printComment){
					System.out.println("returning whole resultListArray:");
					printTreeNodeArray(resultListArray);
				}
			}
			
			
			
			
			return resultListArray;
		}	
		
		private void printTreeNodeArray(treeNode[] a){
			System.out.print("resultListArray :");
			for(treeNode tarNode: a){
				System.out.print(tarNode.getHira()+"-"+tarNode.getHorz()+"("+tarNode.getHeight()+"), ");
			}
			System.out.println("");
		}
		
//		private void initLists(treeNode rt){
//			tempList.add(rt);
//		}


		private void renewLists(){
			treeNode tarNode = getFirstInTmpList();//get first node and add its children into tmpList
			if(tarNode == null){
				resultListCompleat = true;
				return;
			}//no more qualified(higher height midnode) node to sort-> no trim to the resultList!!
			
			renewTempList(tarNode);
			
			
			/*
			 * relation of clusterNum, resultList.size() and index
			 * clustNum  1   2   3   4   5   6   7   8   9
			 * size      0   1   2   3   4   5   6   7   8
			 *           +---+---+---+---+---+---+---+---+
			 * index     | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
 			 *           +---+---+---+---+---+---+---+---+
 			 * value     |9.0|7.3|6.2|3.6|3.4|3.2|2.5|1.2|
 			 *           +---+---+---+---+---+---+---+---+
			 *
			 * if resultList.size+1 >= ideaClustNum // conditions that will return resultList
			 * 	if tarNode.height != getLst.height
			 * 		return rsultList
			 * 	else // conditions that will return resultLis.sublist even (tarNode.height == getLst.height)
			 * 		if preIndex > 0 -> [0].height != tarNode.height
			 * 			if splitMood = '-' || (splitMood = '|' && (result.siz()-(idealClstNum-1)) >((idealClstNum -1) - (preIndex +1)) //until the gap between + and clusterNum is bigger than - and clusterNum
			 * 				return resultList.sublist(0:preIndex+1)
			 * else// continue to add tarNode to resultList
			 * 	resultList.add(tarNode);
			 */


			if(resultList.size() >0 && tarNode.getHeight() != resultList.getLast().getHeight()){
				predifIndex = resultList.size()-1;
			}
			 if(resultList.size() >= clusterSplitter.getIdealClusterNum() -1){// if resultList is long enough
				 // conditions that will return resultList
				 if (resultList.size() == 0/*userClusterNum == 1*/ || tarNode.getHeight() != resultList.getLast().getHeight()){
					 resultListCompleat = true;
					 return;
				 }
				 //XXX algorithm XXX
				 if(tarNode.getHeight() == resultList.getLast().getHeight()){ // conditions that will return resultLis.sublist even (tarNode.height == getLst.height)
					 if(printComment)System.out.println("!!!sameHeight , preIndex = "+ predifIndex);
					 
					  	if (predifIndex >= 0){
					  			if(clusterSplitter.getUserSplitMood() == '-'){
					  		
					  			// [0].height != tarNode.height -> not all height value are same -> if so resultList should grow till the tempList is empty or has a different height value
					  			resultListCompleat = true;
					  			List<treeNode> subList = resultList.subList(predifIndex+1, resultList.size());
					  			subList.removeAll(subList);
//					  			resultList.remove
//					  			resultList = (LinkedList<treeNode>) resultList.subList(0, predifIndex+1);
					  			return;
					  		}
					  		if ((clusterSplitter.getUserSplitMood() == '|' )
					  				&& (resultList.size()-(clusterSplitter.getIdealClusterNum() -1)) > ((clusterSplitter.getIdealClusterNum()-1) - (predifIndex+1))){ //until the gap between + and clusterNum is bigger than - and clusterNum
					  			resultListCompleat = true;
					  			List<treeNode> subList = resultList.subList(predifIndex+1, resultList.size());
					  			subList.removeAll(subList);
//					  			resultList = (LinkedList<treeNode>) resultList.subList(0, predifIndex+1);
					  			return;
					  		}
					  	}else{
					  		if(clusterSplitter.getUserSplitMood() == '-'){
					  			resultListCompleat = true;
					  			resultList.clear();
					  			return;
				  			}
					  	}
				}
				 
			}
			resultList.add(tarNode);
		}


		private treeNode getFirstInTmpList() {
			if(!tempList.isEmpty()){
				treeNode tarNode = tempList.get(0);//take the first node in the tempList as the tarNode;
				tempList.remove(0);// eliminate tarNode from tempList
				return tarNode;
			}
			return null;
		}
		
		private void renewTempList(treeNode tarNode) {
			// add the children of the tarNode
			treeNode[] tarChildren = {tarNode.getLeft(), tarNode.getRight()};// get tarNode's children as an array
			for(treeNode c: tarChildren){// for all the children
				if(c.getHeight() > maxHeight){// if the height is higher than the maxLeafHeight
					tempList.add(binarySearchFortempList(c.getHeight()), c);// insert the child to the tempList sorted by its height
//					if(printComments)System.out.println("add a new Node to the tempList");
				}
			}
		}


		private void renewLists_old(){
			if(tempList.isEmpty()){predifIndex = resultList.size()-1;resultListCompleat = true; return;}//no more qualified(higher height midnode) node to sort-> no trim to the resultList!!
			
			treeNode tarNode = tempList.get(0);//take the first node in the tempList as the tarNode;
			tempList.remove(0);// eliminate tarNode from tempList
			if(resultList.size()>0){
				if (tarNode.getHeight() != resultList.get(resultList.size()-1).getHeight()){//if tarNode doesn't have the same height as the last node in resultList
					if (resultList.size() == clusterSplitter.getUserClusterNum()-1){
						resultListCompleat = true;
						return;// (and copy the resultList as the nodeList for split clusters)
					}
					predifIndex = resultList.size()-1;//previousDifferentHeightNode place is the .size()-2 (the node before the last node in the resultList)
				}// if the tarNode has the same height as the last node in resultList, then preHeightIndex do not change.
			}
			
			resultList.add(tarNode);
			
			if(predifIndex > -1 && resultList.size() > clusterSplitter.getUserClusterNum()-1 && clusterSplitter.getUserSplitMood() == '-'){// is the mood is '-' then do not need any longer resultList.
				resultListCompleat = true; return;
			}
			

			treeNode[] tarChildren = {tarNode.getLeft(), tarNode.getRight()};// get tarNode's children as an array
			for(treeNode c: tarChildren){// for all the children
				if(c.getHeight() > maxHeight){// if the height is higher than the maxLeafHeight
					tempList.add(binarySearchFortempList(c.getHeight()), c);// insert the child to the tempList sorted by its height
//					System.out.println("add a new Node to the tempList");
				}
			}
			
			

		}
		
		private void printtempList(){
			System.out.print("tempList: ");
			if(this.tempList.size()>0){
				ListIterator litr = this.tempList.listIterator();
				while(litr.hasNext()){
					treeNode tarNode = (treeNode) litr.next();
					System.out.print(tarNode.getHira()+"-"+tarNode.getHorz()+"("+tarNode.getHeight()+"), ");
				}
			}else{System.out.print("null");}
			System.out.print("\n");
		}
		
		private void printresultList(){
			System.out.print("resultList: ");
			if(this.resultList.size()>0){
				ListIterator litr = this.resultList.listIterator();
				while(litr.hasNext()){
					treeNode tarNode = (treeNode) litr.next();
					System.out.print(tarNode.getHira()+"-"+tarNode.getHorz()+"("+tarNode.getHeight()+"), ");
				}
			}else{System.out.print("null");}
			System.out.print("\n");
		}

		/**
		 * @author rh
		 *	get a new height, then find a index to place the node, sorted by height
		 *	return the insert place for the newNode; 
		 */
		private int binarySearchFortempList(double h){	//System.out.println("get into binarySearchFoertempList");
			double midHeight, newHeight = h;
			int firstIndex, lastIndex, midIndex;
			int indexGap = -1;// -1 -> undefined;
			
			/**
			 * compare the newHeight with the first node's height and the last node's height
			 * - if newHeight higher than the first node's height, then return place as 0;
			 * - if newHeight lower than the last node's height, then return place size of tempList;
			 * if the newHeight is in the range -> bigIndex is the first one's, smallIndex is the last one's;
			 * - keep search the place until bigIndex and smallIndex is beside, then the place is the middle of the two.
			 */
			
			if (tempList.size() == 0){return 0;}
			
			if(newHeight >= tempList.get(0).getHeight() || newHeight <= tempList.get(tempList.size()-1).getHeight()){//compare the newHeight with the first node's height and the last node's height
				if(newHeight >= tempList.get(0).getHeight()){//* - if newHeight higher than the first node's height, 
					return 0;//then return place as 0;
				} else{//* - if newHeight lower than the last node's height, 
					return tempList.size();//then return place size of tempList;
				}
			} else{
				firstIndex = 0;//* if the newHeight is in the range -> bigIndex is the first one's, smallIndex is the last one's;
				lastIndex = tempList.size()-1;
				indexGap = lastIndex - firstIndex;
				midIndex = firstIndex + indexGap/2;
				midHeight = tempList.get(midIndex).getHeight();
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
					midHeight = tempList.get(midIndex).getHeight();
				}
				return lastIndex;
			}
			
		}
		
		
			
	}
}

