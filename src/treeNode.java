import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class treeNode {
	private static char leafPThL = '(';
	private static char leafPThR = ')';
	private static char leafClstPThL = '〖';
	private static char leafClstPThR = '〗';
	private static char innerPThL = '[';
	private static char innerPThR = ']';
	private static char innerClstPThL = '║';
	private static char innerClstPThR = '║';
	
	public treeNode parent = null;
	public treeNode left = null;
	public treeNode right = null;
//	static int midNodeNum = 0;

	private treeNode formerLeaf = null;
	private treeNode nextLeaf = null;
	private int leafNum = 0;
	private boolean root = false;
	private boolean leaf = false;
	private boolean clusterNode = false;
	private char path = '-';// root->O, left->L, right->R
	
//	private boolean cluster = false;//@del

	public String type = "treeNode";
	public String head = "\t";
	private int hira = -1;// -1:undefined root = 0, child +1
	private int horz = -1;// -1:undefined most left n = 1, right n +1 (no
							// n is horz = 0)
	private static ArrayList<Integer> numHiraHorz = new ArrayList<Integer>();// max
																				// hira
																				// =
																				// numHiraHorz.size();
																				// max
																				// horz
																				// =
																				// numHiraHorz.get(hira);

	// information
	private String name = null;
	private String label = null;
	private String info = null;
	private int tableLine = -1;
	private int members = -1;
	private double height = -1;
	private int heightNum = -1;
	private double midpoint = -1;
//	private int midS = -1;//start for tree node
//	private int midE = -1;//@del
	private int midLineNum = -1;// only for the text line tree structure -> (left.Members+left.midLine+right.midLine)/2

	// from dataTable
	private String probeID = null;
//	private String geneSymbol = null;
	private ArrayList<Double> value = new ArrayList<Double>();// do not use for clusterSplitter
	private double valueMax = -1;
	private double valueMin = -1;

	// for treemap figures !! all figures are normalized !!
	private int leftMem = -1;// members number in left child
	private int rightMem = -1;// members number in right child
	private double tmW = -1;//
	private double tmH = -1;//
	private double tmX = -1;//
	private double tmY = -1;//
	private double tmR = -1;// rate H/W
	private double tmLineSX = -1;// devision line start x position
	private double tmLineSY = -1;// devision line start y position
	private double tmLineEX = -1;// devision line end x position
	private double tmLineEY = -1;// devision line end y position

	// public treeN() {
	//
	// }

	public treeNode(treeNode p) {// make a child
		this.parent = p;
		// System.err.println("created a new nStructure");
		if (p == null) {// for root
			this.root = true;
			this.setHiraHorz(0);
		} else if (p.hira >= 0) {
			this.setHiraHorz(p.hira + 1);//@@@check
		}
	}

	public void setParent(treeNode p) {
		this.parent = p;
		this.setHiraHorz(this.parent.getHira() + 1);//@@@check
	}

	public void setLeft(treeNode l) {
		this.left = l;
		this.left.setPath('L');
	}

	public void setRight(treeNode r) {
		this.right = r;
		this.right.setPath('R');
	}

	// get methods
	public treeNode getParent() {
		treeNode bt = this.parent;
		return bt;
	}

	public treeNode getLeft() {
		treeNode bt = this.left;
		return bt;
	}

	public treeNode getRight() {
		treeNode bt = this.right;
		return bt;
	}

	public treeNode getNextLeaf() {
		treeNode bt = null;
		if (this.leaf) {
			bt = this.nextLeaf;
		}
		return bt;
	}

	public treeNode getFormerLeaf() {
		treeNode bt = null;
		if (this.leaf) {
			bt = this.formerLeaf;
		}
		return bt;
	}

	// getNset hira horis
	public void setHiraHorz(int h) {
		this.setHira(h);
		if ((h + 1) > treeNode.getNumHiraHorz().size()) {
			treeNode.getNumHiraHorz().add(1);
		} else {
			treeNode.getNumHiraHorz().set(h,
					(treeNode.getNumHiraHorz().get(h) + 1));
		}
		// this.horz = numHiraHorz.get(h);
		this.setHorz(treeNode.getNumHiraHorz().get(h));
	}

	public void setHira(int i) {
		this.hira = i;
	}

	public void setHorz(int i) {
		this.horz = i;
	}

	public int getHira() {
		int i = this.hira;
		return (i+1);
	}

	public int getHorz() {
		int i = this.horz;
		return i;
	}

	//////////////////////////
	// make tree node
	/////////////////////////
	// make inner node
	public treeNode newInnerNode() {// return a nList pointer -> make a
										// child n and return it
		treeNode td = null;// CurrentNode for return
		if (this.left == null && this.right == null) {// making a child in left
			setLeft(new treeNode(this));
			td = this.left;// for return this.left as current n;
		} else if (this.left != null && this.right == null) {// making a child in right
			setRight(new treeNode(this));
			td = this.right;// for return this.right as current n;
		} else if (this.left != null && this.right != null) {//Error->both occupyed
			System.err.println("error in treeNode newInnerNode() -> both children are occupied");
			System.exit(1);
		} else {//Error -> something else...(maybe this else is not necessary)
			System.err.println("error in treeNode newInnerNode() -> left is occupied while right is null");
			System.exit(1);
		}
		td.leaf = false;
		return td;
	}
	// make a leaf node
	public treeNode newLeafNode(int n) {// make a leaf node as a child but do not go into the leaf
		//init the child
		treeNode td = new treeNode(this);// target new leaf node
		td.leaf = true;
		td.tableLine = n;
		td.members = 1;
		td.height = 0;
		td.midpoint = 0;
		td.members = 1;
		//set the child in either left or right
		if (this.left == null && this.right == null) {// making a child in left
			this.setLeft(td);
		} else if (this.left != null &&this.right == null) {// making a child in right
			this.setRight(td);
		} else if (this.left != null && this.right != null) {// both occupied
			 System.err.println("error in incerting nStructure in treeNode newLeafNode -> both of the children are occupied");
			 System.exit(1);
		} else {
			 System.err.println("error in incerting Structure in treeNode newLeafNode -> the right child is occupied but left is null");
			 System.exit(1);
		}
		return td;
	}

	public void printNode() {
		this.printNode("");
	}

	public void printNode(String h) {// with tabs
		String hd = h + head;
		System.err.print(h + "[" + type + "](");

		if (this.left != null || this.right != null) {
			System.err.println();
			if (this.left != null) {
				this.left.printNode(hd);
			}
			if (this.right != null) {
				this.right.printNode(hd);
			}
			System.err.print(h);
		}
		/*
		System.err.println("" + "hira = [" + this.hira + "], " + "horz = ["
				+ this.horz + "], " + "{" + info + "}, [leaf = " + leaf
				+ "], [label = " + label + "], [label = " + label
				+ "], [midpoint = " + midpoint + "], [height = " + height
				+ "], [members = " + members + "], [name = " + name + "] )");
		*/
		// treeMethod.root = this;//only for test
		this.printNodeAllInfo();
	}

	public void fillInformation(String l) {// important//only for R
		this.info = l;

		// rex
		String p;
		Pattern pattern;
		Matcher matcher;

		// int members
		p = "members = (.*?),";
		pattern = Pattern.compile(p);
		matcher = pattern.matcher(l);

		if (matcher.find()) {
			this.members = Integer.parseInt(l.substring(matcher.start() + 10,
					matcher.end() - 2));
		}

		if (l.matches("(.*)leaf = TRUE(.*)")) {
			this.leaf = true;
			this.name = l.substring(0, l.indexOf(","));
			this.label = l.substring(l.indexOf("=") + 2,
					l.indexOf(",", l.indexOf("=")));
			this.height = 0;
			// this.height = Double.parseDouble(l.substring(
			// l.indexOf("height = ") + 9, l.indexOf(", leaf")));
		} else {
			this.midpoint = Double.parseDouble(l.substring(
					l.indexOf("midpoint = ") + 11, l.indexOf(", height =")));
			String str = l.substring(l.indexOf("height = ") + 9);
			if (str.matches("(.*), class = (.*)")) {
				str = str.substring(0, str.indexOf(", class = "));
			}
			if (str.matches("(.*), value = (.*)")) {
				str = str.substring(0, str.indexOf(", value = "));
			}
			this.height = Double.parseDouble(str);
			
			//make maxLeafHeight
			if (this.height > clusterSplitter.tmObj.getMaxMiNNodeHeightWithLeafChild()){
				clusterSplitter.tmObj.setMaxLeafHeight(this.height);
			}

		}
		if (this.parent == null) {
			root = true;
		}
		// example of l data
		// 15L, label = 15L, members = 1L, height = 0, leaf = TRUE
		// members = 2L, midpoint = 0.5, height = 0.275492403347977
	}

	public void setTreemapInfo() {// !!you have to set this.tmW.tmH.tmX.tmY.tmR
									// before enter this
									// function!! -> use setTMFigures(x,y,w,h)
									// for root.(tmW and root.tmH)
		// make this.leftMem,rightMem,tmLine(SX,SY,EX,EY)
		// make (left,right)(tmW,tmH,tmX,tmY,tmR)

		// if(this.horz == -1){
		// if(this.numHiraHorz[this.hira] > 0){
		// this.numHiraHorz[this.hira]++;
		// }else{
		// this.numHiraHorz[this.hira] = 1;
		// }
		// this.horz = this.numHiraHorz[this.hira];
		// }

		if (this.leaf) {
			this.leftMem = this.rightMem = 0;
			this.tmLineSX = this.tmLineSY = this.tmLineEX = this.tmLineEY = 0;
		} else {
			leftMem = this.left.members;
			rightMem = this.right.members;

			// culcs for children
			if (this.tmR > 1) {// (split in horizontal direction [-]

				// decide left.tm(X,Y,W,H,R)
				this.left.tmX = this.tmX;
				this.left.tmY = this.tmY;
				this.left.tmW = this.tmW;
				this.left.tmH = this.tmH * (1.0 * this.leftMem / this.members);

				// decide right.tm(X,Y,W,H,R)
				this.right.tmX = this.left.tmX;
				this.right.tmY = this.left.tmY + this.left.tmH;
				this.right.tmW = this.tmW;
				this.right.tmH = this.tmH - this.left.tmH;

				// decide tmLine(SX,SY,EX,EY)
				this.tmLineSX = this.right.tmX;
				this.tmLineSY = this.right.tmY;
				this.tmLineEX = this.right.tmX + this.right.tmW;
				this.tmLineEY = this.right.tmY;

			} else {// split in vertical direction [|]

				// decide left.tm(X,Y,W,H,R)
				this.left.tmX = this.tmX;
				this.left.tmY = this.tmY;
				this.left.tmW = this.tmW * (1.0 * this.leftMem / this.members);
				this.left.tmH = this.tmH;

				// decide right.tm(X,Y,W,H,R)
				this.right.tmX = this.left.tmX + this.left.tmW;
				this.right.tmY = this.tmY;
				this.right.tmW = this.tmW - this.left.tmW;
				this.right.tmH = this.tmH;

				// decide tmLine(SX,SY,EX,EY)
				this.tmLineSX = this.right.tmX;
				this.tmLineSY = this.right.tmY;
				this.tmLineEX = this.right.tmX;
				this.tmLineEY = this.right.tmY + this.right.tmH;
			}
			// for childlen's tmR
			this.left.tmR = 1.0 * this.left.tmH / this.left.tmW;
			this.right.tmR = 1.0 * this.right.tmH / this.right.tmW;

			// this.left.setTreemapInfo();
			// this.right.setTreemapInfo();

		}

	}
	

	public void makeClusterValues() {
//		System.err.println("left = " + this.left.getValue().size());
//		System.err.println("right = " + this.right.getValue().size());

		ArrayList<Double> leftValue = this.left.getValue();
		ArrayList<Double> rightValue = this.right.getValue();

		for (int i = 0; i < leftValue.size(); i++) {
			Double d = (leftValue.get(i) * this.leftMem + rightValue.get(i) * this.rightMem) / (this.leftMem + this.rightMem);
			this.value.add(d);
			if ((this.valueMax == -1) || (d > this.valueMax)) {
				this.valueMax = d;
			}
			if ((this.valueMin == -1) || (d < this.valueMin)) {
				this.valueMin = d;
			}
		}
	}

	
	/////////////////////////////
	///// print functions  /////
	///////////////////////////

	public void printNodeAllInfo() {
		char lp ='[';//left parenthesis
		char rp =']';//left parenthesis 
		if(this.leaf){
			lp ='(';
			rp =')';
		}
		System.err.println("" 
				+ "number = "+lp+ this.tableLine +rp+ " "
//				+ "leaf = ["+ this.leaf + "] "
				+ "pos = "+lp+ this.hira + "-" + this.horz +rp+ " "
				+ "heightNum = [" + this.heightNum + "], "
				+ "height = [" + this.height + "], "
				+ "midLineNum = ["+ this.midLineNum + "], " 
				+ "midpoint = ["+ this.midpoint + "], " 
				
				+ "members = [" + this.members + "], "
				+ "leftMem = [" + this.leftMem + "], " 
				+ "rightMem = ["+ this.rightMem + "], " 
				+ "name = ["+ this.name + "], " 
				+ "label = [" + this.label + "], " 
				
				+ "tmW = [" + this.tmW + "], "
				+ "tmH = [" + this.tmH + "], " + "tmX = [" + this.tmX + "], "
				+ "tmY = [" + this.tmY + "], " + "tmR = [" + this.tmR + "], "
				+ "tmLineSX = [" + this.tmLineSX + "], " + "tmLineSY = ["
				+ this.tmLineSY + "], " + "tmLineEX = [" + this.tmLineEX
				+ "], " + "tmLineEY = [" + this.tmLineEY + "], "
				
				
				+ "ValueMax = " + this.valueMax + ", "
				+ "ValueMin = " + this.valueMin + ", " 
				
				+ "ProbeID = [" + this.probeID + "], "
//				+ "GeneSymbol = [" + this.geneSymbol + "], " 
				
				+ "Value = "+ this.value + ", " 
				
				+"");

	}
	
	public String getHiraHorz() {
		return ( this.getHira()+ "-"+ this.getHorz());
	}

	public void setTMFigures(double x, double y, double w, double h, double r) {
		this.tmX = x;
		this.tmY = y;
		this.tmW = w;
		this.tmH = h;
		this.tmR = r;
		
//		System.err.print("root set TMFfigures -> ");//@test
//		this.printNodeInfo();//@test
	}

	
	/////////////////////////////
	/////getter and setters/////
	///////////////////////////
	
	public int getMembers() {
		return this.members;
	}

	public double getTMX() {
		return this.tmX;
	}

	public double getTMY() {
		return this.tmY;
	}

	public double getTMW() {
		return this.tmW;
	}

	public double getTMH() {
		return this.tmH;
	}

	public double getTMR() {
		return this.tmR;
	}

	public double getHeight() {
		return this.height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	public double getSX() {
		return this.tmLineSX;
	}

	public double getSY() {
		return this.tmLineSY;
	}

	public double getEX() {
		return this.tmLineEX;
	}

	public double getEY() {
		return this.tmLineEY;
	}

	public String getProbeID() {
		return this.probeID;
	}

	public void setProbeID(String s) {
		this.probeID = s;
	}


	public String getValueOneLine(){
		String line = "";
		Double v = this.getValue(0);
		line += v;
		for(int i = 1; i < this.value.size(); i ++){
			v = this.getValue(i);
			line += ","+v;
		}
		return line;
	}
	
	public ArrayList<Double> getValue() {
		return this.value;
	}

	public Double getValue(int i) {
		Double d;
		if (this.value.size() > i) {
			d = this.value.get(i);
		} else {
//			d = -1d;
			d = null;
		}
		return d;
	}

	public void addValue(Double d) {
		this.value.add(d);
	}

	public void setValue(int i, Double d) {
		this.value.add(i, d);
	}

	public Double getValueMax() {
		return this.valueMax;
	}

	public Double getValueMin() {
		return this.valueMin;
	}

	public void setValueMax(Double d) {
		this.valueMax = d;
	}

	public void setValueMin(Double d) {
		this.valueMin = d;
	}
	
	public double getMidpoint() {
		return midpoint;
	}

	public void setMidpoint(double midpoint) {
		this.midpoint = midpoint;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTableLine() {
		return tableLine;
	}

//	public void setCluster(boolean t) {
//		this.cluster = t;
//	}
//
//	public boolean getCluster() {
//		return this.cluster;
//	}//@del

	// public Double getV

	public static ArrayList<Integer> getNumHiraHorz() {
		return numHiraHorz;
	}

	public static void setNumHiraHorz(ArrayList<Integer> numHiraHorz) {
		treeNode.numHiraHorz = numHiraHorz;
	}

	public void setMembers(int members) {
		this.members = members;
	}

//	public static int getMidNodeNum() {
//		return midNodeNum;
//	}
//
//	public static void setMidNodeNum(int midNodeNum) {
//		treeNode.midNodeNum = midNodeNum;
//	}

	public static int getLeafNum() {
		return getLeafNum();
	}

	public void setLeafNum(int leafNum) {
		this.leafNum = leafNum;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getLeftMem() {
		return leftMem;
	}

	public void setLeftMem(int leftMem) {
		this.leftMem = leftMem;
	}

	public int getRightMem() {
		return rightMem;
	}

	public void setRightMem(int rightMem) {
		this.rightMem = rightMem;
	}

	public double getTmW() {
		return tmW;
	}

	public void setTmW(double tmW) {
		this.tmW = tmW;
	}

	public double getTmH() {
		return tmH;
	}

	public void setTmH(double tmH) {
		this.tmH = tmH;
	}

	public double getTmX() {
		return tmX;
	}

	public void setTmX(double tmX) {
		this.tmX = tmX;
	}

	public double getTmY() {
		return tmY;
	}

	public void setTmY(double tmY) {
		this.tmY = tmY;
	}

	public double getTmR() {
		return tmR;
	}

	public void setTmR(double tmR) {
		this.tmR = tmR;
	}

	public double getTmLineSX() {
		return tmLineSX;
	}

	public void setTmLineSX(double tmLineSX) {
		this.tmLineSX = tmLineSX;
	}

	public double getTmLineSY() {
		return tmLineSY;
	}

	public void setTmLineSY(double tmLineSY) {
		this.tmLineSY = tmLineSY;
	}

	public double getTmLineEX() {
		return tmLineEX;
	}

	public void setTmLineEX(double tmLineEX) {
		this.tmLineEX = tmLineEX;
	}

	public double getTmLineEY() {
		return tmLineEY;
	}

	public void setTmLineEY(double tmLineEY) {
		this.tmLineEY = tmLineEY;
	}

	public void setFormerLeaf(treeNode formerLeaf) {
		this.formerLeaf = formerLeaf;
	}

	public void setNextLeaf(treeNode nextLeaf) {
		this.nextLeaf = nextLeaf;
	}

	public void setValue(ArrayList<Double> value) {
		this.value = value;
	}

	public void setValueMax(double valueMax) {
		this.valueMax = valueMax;
	}

	public void setValueMin(double valueMin) {
		this.valueMin = valueMin;
	}

	public int getHeightNum() {
		return heightNum;
	}

	public void setHeightNum(int heightNum) {
		this.heightNum = heightNum;
	}

	public char getPath() {
		return path;
	}

	public void setPath(char path) {
		this.path = path;
	}
	


//	public boolean isClstNode() {
//		return clusterNode;
//	}
//
//	public void setClustrNode(boolean clst) {
//		this.clusterNode = clst;
//	}

	public int getMidLineNum() {
		return midLineNum;
	}

	public void setMidLineNum(int midNodeNum) {
		this.midLineNum = midNodeNum;
	}

	public void setTableLine(int tableLine) {
		this.tableLine = tableLine;
	}
	public static char getLeafPThL() {
		return leafPThL;
	}

	public static char getLeafPThR() {
		return leafPThR;
	}

	public static void setLeafPThR(char leafPThR) {
		treeNode.leafPThR = leafPThR;
	}

	public static char getLeafClstPThL() {
		return leafClstPThL;
	}

	public static void setLeafClstPThL(char leafClstPThL) {
		treeNode.leafClstPThL = leafClstPThL;
	}

	public static char getLeafClstPThR() {
		return leafClstPThR;
	}

	public static void setLeafClstPThR(char leafClstPThR) {
		treeNode.leafClstPThR = leafClstPThR;
	}

	public static char getInnerPThL() {
		return innerPThL;
	}

	public static void setInnerPThL(char innerPThL) {
		treeNode.innerPThL = innerPThL;
	}

	public static char getInnerPThR() {
		return innerPThR;
	}

	public static void setInnerPThR(char innerPThR) {
		treeNode.innerPThR = innerPThR;
	}

	public static char getInnerClstPThL() {
		return innerClstPThL;
	}

	public static void setInnerClstPThL(char innerClstPThL) {
		treeNode.innerClstPThL = innerClstPThL;
	}

	public static char getInnerClstPThR() {
		return innerClstPThR;
	}

	public static void setInnerClstPThR(char innerClstPThR) {
		treeNode.innerClstPThR = innerClstPThR;
	}

	public static void setLeafPThL(char leafPThL) {
		treeNode.leafPThL = leafPThL;
	}

	public boolean isClusterNode() {
		return clusterNode;
	}

	public void setClusterNode(boolean clusterNode) {
		this.clusterNode = clusterNode;
	}

	/////////////////////////////
	///// unique getters   /////
	///////////////////////////
	//
	public String getPathway(){// return this node's pathway of from root to this node
		String pathway = "";
		treeNode tarNod = this;
		while(tarNod.getHira() > 0){
			pathway = tarNod.getPath()+pathway;
			tarNod = tarNod.getParent();
		}
		pathway = 'O'+pathway;
		
		return pathway;
	}
	
	public String getNodePos(){
		String nodePos = "";
		nodePos = this.getHira()+"-"+this.getHorz();
		if(this.isLeaf()){nodePos += ":"+this.getTableLine();}
		
		return nodePos;
	}
	public String getNodePosInfo(){
		String nodeName = "";
		if(this.isLeaf() && !this.isClusterNode()){
			nodeName = leafPThL+this.getNodePos()+leafPThR;
		}else if(this.isLeaf() && this.isClusterNode()){
			nodeName = leafClstPThL+this.getNodePos()+leafClstPThR;
		}else if(!this.isLeaf() && !this.isClusterNode()) {
			nodeName = innerPThL+this.getNodePos()+innerPThR;
		}else{
			nodeName = innerClstPThL+this.getNodePos()+innerClstPThR;
		}
		return nodeName;
		
	}

//	public String get
}
