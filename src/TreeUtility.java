import java.util.LinkedList;


public class TreeUtility {

	public static String makeAllTreePathway(treeMethod tr, String c){

		treeMethod tarTree = tr;
		int mode;
		String wholePathway = "";
		if(c == "UP"){
			mode = 1;
		}else if(c == "CENTER"){
			mode = 2;
		}else{//Error
			return "Error with make AllTreePathway with mode "+c;
		}
		treeNode tarNode = tarTree.firstLeaf;
		String prePathway = "";
		String newPathway = "";
		LinkedList<treeNode> pathwayNodeList = new LinkedList<treeNode>();
		LinkedList<Integer> pathwayCountList = new LinkedList<Integer>();
//		String wholePathwayU ="";//return
//		String wholePathwayC ="";//return
		

		//get pathwayNodeList
		//get rootPathway in a node
		//make diffPathway
		//
		while(tarNode != null){
			pathwayNodeList.clear();
			treeNode pathNode = tarNode;
			while(pathNode!=null){//get the nodePathway
				pathwayNodeList.add(0, pathNode);
//				pathwayCountList.add(0, new Integer(0));
				pathNode = pathNode.getParent();
			}
			
			newPathway = tarNode.getPathway();
			String diffPathway = "";
			for(int i=0; i< Math.min(prePathway.length(), newPathway.length()); i++){//compare pre with new to make diff
				if(prePathway.charAt(i)==newPathway.charAt(i)){
					diffPathway = diffPathway+Character.toLowerCase(newPathway.charAt(i));
				}
				else{diffPathway = diffPathway+Character.toUpperCase(newPathway.charAt(i));}
			}
			if(diffPathway.length() < newPathway.length()){
				diffPathway = diffPathway + newPathway.substring(diffPathway.length());
			}
			
			if(mode == 1){
			for(int i =0; i < pathwayNodeList.size(); i ++){
				wholePathway += makeTreeNodePathwayUP(pathwayNodeList.get(i), diffPathway.charAt(i));// for UP
			}
			}else if(mode == 2){
			String pathwayC ="";
			for(int i =0; i < pathwayNodeList.size(); i ++){
				// for CENTER
				if(pathwayCountList.size()<=i){
					pathwayCountList.add(new Integer(0));
				}
				if(Character.isUpperCase(diffPathway.charAt(i))){
					pathwayCountList.set(i, Integer.valueOf(0));
				}else{
					pathwayCountList.set(i, Integer.valueOf(pathwayCountList.get(i)+1));
				}
				wholePathway += makeTreeNodePathwayCENTER(pathwayNodeList.get(i), pathwayCountList.get(i));
//				pathwayC += makeTreeNodePathwayCENTER(pathwayNodeList.get(i), pathwayCountList.get(i));;//@test
			}


//			System.err.println(pathwayCountList);//@test
//			System.err.println(pathwayC);//@test
		}
			wholePathway += "\n";
//			wholePathwayU += "\n";
//			wholePathwayC += "\n";//@hold
			prePathway = newPathway;
			tarNode = tarNode.getNextLeaf();
		}
//		System.err.println(treePathway);//@del

//		System.err.println(wholePathwayC);//@del
		return wholePathway;
	}
	
	public static String makeTreeNodePathwayUP(treeNode n, char c){
		String treeNodePathway;
		treeNode pathNode = n;
		char dPath = c;
		switch(dPath){
		case 'O':treeNodePathway = "ROOT";break;
		case 'o':treeNodePathway = "    ";break;
		case 'L':treeNodePathway = "┬"+makeTreeNodePosInfo(pathNode)+"";break;
		case 'l':treeNodePathway = "|     ";break;
		case 'R':treeNodePathway = "└"+makeTreeNodePosInfo(pathNode)+"";break;
		case 'r':treeNodePathway = "      ";break;
		default :treeNodePathway = "XXXXXX";break;
		}
		return treeNodePathway;
	}
	
	public static String makeTreeNodePosInfo(treeNode n){
		String nodePosInfo;
		treeNode pathNode = n;
		nodePosInfo = pathNode.getNodePosInfo();
//		if(pathNode.isLeaf() && !pathNode.isClstNode()){
//			nodePosInfo = "("+pathNode.getHira()+"-"+pathNode.getHorz()+":"+pathNode.getTableLine()+")";
//		}else if(pathNode.isLeaf() && pathNode.isClstNode()){
//			nodePosInfo = "(("+pathNode.getHira()+"-"+pathNode.getHorz()+":"+pathNode.getTableLine()+"))";
//		}else if(!pathNode.isLeaf() && pathNode.isClstNode()){
//			nodePosInfo = "║"+pathNode.getHira()+"-"+pathNode.getHorz()+"║";
//		}else{
//			nodePosInfo = "["+pathNode.getHira()+"-"+pathNode.getHorz()+"]";
//		}//@hold
		return nodePosInfo;
	}
	
	public static String makeTreeNodePathwayCENTER(treeNode n, int i){
		String treeNodePathway ="-";
		treeNode pathNode = n;
		int pathNum = i;
		if(!pathNode.isLeaf()){//inner node
			if(pathNum == pathNode.getMidLineNum()){
				treeNodePathway = makeTreeNodePosInfo(pathNode);
				if(pathNode.getMembers() > 2 && pathNode.getLeft().getMembers()>1 && pathNode.getRight().getMembers()>1){
					treeNodePathway += "┤";
				}else if(pathNode.getPath() == 'L'){
					treeNodePathway += "┴";
				}else if(pathNode.getPath() == 'R'){
					treeNodePathway += "┬";
				}else if(pathNode.isRoot()){
					treeNodePathway = makeTreeNodePosInfo(pathNode);
				}
			}else if(pathNum == pathNode.getLeft().getMidLineNum()){
				treeNodePathway = "     ┌";
			}else if(pathNum == pathNode.getLeft().getMembers() + pathNode.getRight().getMidLineNum()){
				treeNodePathway = "     └";
			}else if(pathNum > pathNode.getLeft().getMidLineNum() &&  pathNum < pathNode.getLeft().getMembers() +pathNode.getRight().getMidLineNum()){
				treeNodePathway = "     |";
			}else if(pathNum < pathNode.getLeft().getMidLineNum() || pathNum > (pathNode.getLeft().getMembers()+pathNode.getRight().getMidLineNum())){
				treeNodePathway = "      ";
			}else{treeNodePathway = "XXXXXX";}
		}else{
			treeNodePathway = makeTreeNodePosInfo(pathNode);
		}
		return treeNodePathway;
	}
	
	
	public static void printTreeDiffPathway(String dpw){//@no more use
		for(int i = 0; i < dpw.length(); i ++){
			switch(dpw.charAt(i)){
			case 'O':System.err.print("R");break;
			case 'o':System.err.print(" ");break;
			case 'L':System.err.print("┬[ ]");break;
			case 'l':System.err.print("|   ");break;
			case 'R':System.err.print("└[ ]");break;
			case 'r':System.err.print("    ");break;
			default :System.err.print("XXXX");break;
			}
		}
		System.err.println();
	}
	
	
	public static void printSubTree(treeNode startNode){
		
	}

}
