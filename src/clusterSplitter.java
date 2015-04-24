import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class clusterSplitter {
	/*
	 * elcipse args
	 * 
	 * @tominaga +20 1:1,3:6,8:10,12 data.csv dendrogram.txt  ../outdir
	 * 
	 * -2 1:1,1 /Users/rh/Documents/Hitachi/workSP/data/convTime/convTimes/convTimeSum.csv /Users/rh/Documents/Hitachi/workSP/data/convTime/convTimes/convTimeSumClusteringPeason.txt outdir/convtime
	 * 
	 * java clusterSplitter -0 1:1,3:5,15 testRawDataR20.csv testDendrogramR20.txt  outdir/testout
	 * java clusterSplitter -0 1:1,18:19,33 text/Figure2TM.csv text/Figure2TM_clusterP.txt outdir/testout
	 * java clusterSplitter -0 1:1,5:6,10:11,15 ./text/AR0278TM.csv text/AR0278TM_clusterP.txt outdir/testout
	 * java clusterSplitter -0 1:1,5:6,10:11,15 ./text/AR0538TM.csv text/AR0538TM_clusterP.txt outdir/testout
	 * java clusterSplitter -0 1:1,3:4,8:9,13:14,18:19,23 ./text/AR1361TM.csv ./text/AR1361TM_clusterP.txt ./outdir/testout
	 * 
	 * -0 1:1,3:6,8:10,12 ../testRawDataR20.csv ../testDendrogramR20.txt  ../outdir
	 * -0 1:1,3:5,6 /Users/rh/Documents/Hitachi/workSP/data/convTimeMonthly/0904/convTimeSum0904.csv /Users/rh/Documents/Hitachi/workSP/data/convTimeMonthly/0904/Pearson/tree.txt  /Users/rh/Documents/Hitachi/workSP/data/convTimeMonthly/0904/Pearson/treemapTest
	 * -0 1:1,3:6,8:10,12 text/testRawDataR20.csv text/testDendrogramR20.txt  outdir/outdir_R20 
	 * -0 text/AR0278TM.csv text/AR0278TM_clusterP.txt  outdir_AR0278TM 
	 * -0 text/AR0538TM.csv text/AR0538TM_clusterP.txt  outdir_AR0538TM 
	 * -0 text/AR1361TM.csv text/AR1361TM_clusterP.txt  outdir_AR1361TM 
	 * -0 text/Figure2TM.csv text/Figure2TM_clusterP.txt  outdir_Figure2TM
	 * 
	 * 
	 * */
	
	// globals
	private static boolean printClustNum = false;//XXX outputs for testing program XXX

	private static int userClusterNum = -1;// "Figure2_mfy2-2" -> 25, others ->// 100; convtime -> 7, 2, 172...
	private static int idealClusterNum = -1;// "Figure2_mfy2-2" -> 25, others ->// 100; convtime -> 7, 2, 172...
	private static int realClusterNum = -1;// "Figure2_mfy2-2" -> 25, others ->// 100; convtime -> 7, 2, 172...
	private static char userSplitMode = '|';// -> '+', '-', '/',
	private static char opeSplitMode = '|';// -> '+', '-', '/',
	
	// for label row
	private static int metaRowNum;
	// start col and end col is correspond. exp:[1,3:6,8:10,12]
	// then can get the correspond tupul by <timepointStarCol.get(i), timepointEndCol.get(i)>
	private static ArrayList<Integer> treemapTimepoints = new ArrayList<Integer>();
	private static ArrayList<float[]> clusterSplitlinePosList = new ArrayList<float[]>();  // for drawing treemap's cluster line
	
	private static File expressionFile;
	private static File dendrogramFile;
	private static File outputDir;
	
	private static int fixedArgNum = 5;
	private static String[] acceptOptions = {"-s"};// [-s] -> do not allow singleNode cluster
	private static ArrayList<Boolean> optionSwitchs = new ArrayList<Boolean>();// need init <- by acceptOption.length
	
//	private static String folderName = null;// "AR0278TM";

	public static int winW = 2000;
	public static int winH = 2000;


	private static String dataName = null;//"AR0278";// fileName;
												// //"AR0278";//"AR0538";"AR1361";"Figure2";
	private static double suppValue = 0;// "Figure2_mfy2-2" -> 5.06, others -> 0

	private static String inputValueFilePath = null;//"." + File.separator + "input"+ File.separator + folderName + File.separator + valueDataName; // "/Users/rh/Documents/Hitachi/workSP/convTime/personalConvTimeSum.txt";//@tempo(?)

	private static String inputTreeFilePath = null;// "." + File.separator + "input"+ File.separator + folderName + File.separator + treeDataName; // "/Users/rh/Documents/Hitachi/workSP/convTime/convTimeSumClusteringPeason.txt";//@tempo(?)

	static treeNode tnObj = new treeNode(null);// tree root;
	static treeMethod tmObj = new treeMethod(tnObj);// for make tree// for
													// initialize btObj as root
													// in treeMethod

	private static boolean writeClusterNum = false;// @tempo
//	private static boolean hasExtraDataRow = false;

//	public static boolean isHasExtraDataRow() {
//		return hasExtraDataRow;
//	}

//	public static void setHasExtraDataRow(boolean hasExtraDataRow) {
//		clusterSplitter.hasExtraDataRow = hasExtraDataRow;
//	}
	
	private static String errorHeader = " [ERROR] : ";

	public static void main(String[] args) {
		
		// checking args[] number
		boolean errorInFlow = true;// init -> true
		boolean printComments = false;
		
		//init
		initOptionSettings();
		checkArgs(args);

		errorInFlow = true;
		boolean errorInData = true;
		boolean errorInTreemap = true;
		int progress = 0;
		try {// main work flow area
			
			// [step 1.1]
			// make data -> read dedrogram file and make tree then decide the cluster number then make cluster information
			tmObj.makeData(inputTreeFilePath, inputValueFilePath);// read csv file
			progress = 0;
			
			// after make data, most middle node could be delete -> save more memory 
			
			// [step 1.1.1]
			// delete mid nodes
			tmObj.deleteMidnodes();
			
			// [step 1.2]
			// make output files
			tmObj.writeFilesForTominagaModule(outputDir);// -> file output -> memory
			progress = 1;
			
			// [step 1.2.1]
			// delete .csv's string table <- this table only used for making out put csv files. not necessary anymore
			tmObj.deleteDataTable();
			
			// [step 1.2.2]
			// garbage collectior
			System.gc();
			
			// for testing
			if(printComments)System.err.println("saved cluster lists");
			errorInData = false;
			
			
			//// -[step2.1]- make treeMap output folder
			String treemapOutputFolderName = getOutputDir().getPath();// set treemap output folder
			
			progress = 2;
			//// -[step2.2]- create and save treemaps
////			saveImgMethod.saveImgs(0);// or 0x11<=>0x[genes' color][genes' line] 0->[-,-], 1[-,line], 2[color,-], 3[color, line]
////			saveImgMethod.saveImgs(1);// no color, only gene's block line
			saveImgMethod.saveImgs("treemap", 2, treemapOutputFolderName);//only genes color -> most useful
			progress = 3;
////			saveImgMethod.saveImgs(3);//gene's color and gene's block line
			saveImgMethod.saveFrame("treemap", treemapOutputFolderName);//(?) no color only gene cluster's line and name -> to show the treemap structure
			progress = 4;
			
			errorInTreemap = false;
			errorInFlow = false;
		}catch(Exception err){
		      err.printStackTrace(System.err);
		      System.err.println("----");
		      err.printStackTrace();
	    }
		/*finally {
	    	if(printComments){
	    		System.err.println("user  number : "+userClusterNum);
	    		System.err.println("ideal number : "+idealClusterNum);
	    		System.err.println("real  number : "+realClusterNum);
	    	}
			if (errorInFlow) {
//				System.err.println("succesful progress number is "+progress);
				if(errorInData){
					System.err.println("error in makeData and writeData");
					System.exit(1);
				}
				if(errorInTreemap){
					System.err.println("error in makeing treemap");
					
					System.exit(2);
				}
			} else {
				if(printComments){
					System.err.println("finished");
					System.err.println("maxMidNodeHeightWithLeafChile"+tmObj.getMaxMiNNodeHeightWithLeafChild());
				}
				if(printClustNum)System.err.println((userSplitMode) +""+ userClusterNum+" : "+tmObj.getClusterNodeList().size());
				System.exit(0);
			}
		}/**/
	}
	


	private static void initOptionSettings() {
		// baby ver
//		for(int i = 0; i < acceptOptions.length; i ++){
//			optionSwitchs.add(false);
//		}
		
		// stylish ver
		getOptionSwitchs().addAll(Arrays.asList(new Boolean[acceptOptions.length]));
		Collections.fill(getOptionSwitchs(), new Boolean(false));
//		printOptionSwitchs();//TODO
	}
	
	private static void changeStatasInOptionSwitchs(int i, boolean b){
		getOptionSwitchs().remove(i);
		getOptionSwitchs().add(i, b);
		
	}

	private static void checkArgs(String[] a){
		if (a.length < fixedArgNum){
			System.err.println(errorHeader+"This program needs at least "+fixedArgNum+" args");
			System.exit(1);
		}else if(a.length > fixedArgNum + acceptOptions.length){
			System.err.println(errorHeader+"This program accept at most "+fixedArgNum+ acceptOptions.length+" args");
			System.exit(1);
		}else{//has right input args 
			//for fixed args
			checkFixedArgs(Arrays.copyOfRange(a, 0, fixedArgNum));
			//for option args (if there are any)
			if(a.length > fixedArgNum) checkOptionArgs(Arrays.copyOfRange(a, fixedArgNum, a.length));
		}
	}
	
	private static void checkOptionArgs(String[] o){
		boolean printComments = false;
		LinkedList<String> errLists = initOptionArgs(o);
		if(printComments) printOptionSwitchs();
		if(!errLists.isEmpty()){
			System.err.print("invalied options : ");
			for(String input :errLists){
				System.err.print(input+", ");
			}
			System.exit(1);
		}
	}
	
	private static void checkFixedArgs(String[] a){
		boolean errorInFlow = true;// init -> true
		boolean errorInChecking = false;// init -> false;
		boolean printComments = false;
		if(printComments){
			System.err.println("args:");
			for(String s:a){
				System.err.println(s);
			}
			System.err.println();
		}
		
		//checking are the args[] qualified
		errorInChecking = false; errorInFlow = true; printComments = false;
		try {
			//
			// 1st arg -> split mode
			//check the legality
			//splitMode and cluster number;
			setUserSplitMode(a[0].charAt(0));
			setOpeSplitMode(getUserSplitMode());
			userClusterNum = -1;
			if (getUserSplitMode() == '+' || getUserSplitMode() == '-') {
				try{
					userClusterNum = Integer.parseInt(a[0].substring(1));
				} catch (Exception e) {
					System.err.println(errorHeader+ "unqualified cluster number ->" + a[0]);
					errorInChecking = true;
					System.err.println(e);
				}
			} else {
				setUserSplitMode('|');
				userClusterNum = Integer.parseInt(a[0]);
			}
			idealClusterNum = userClusterNum;
			if (printComments) System.err.println("cluster split mood -> " + getUserSplitMode()+ ", num ->" + idealClusterNum);
			
			
			//
			// 2nd arg -> numbers for meta row and timepoint col
			String figs[] = a[1].split(":");
			// for meta row
			if(Integer.parseInt(figs[0]) >= 0){
				setMetaRowNum(Integer.parseInt(figs[0]));
			}else {
				System.err.println("the given number for meta row number: \""+figs[0]+"\", in 2nd arg: \""+a[1]+"\", is not acceptable.\n the number should be non-negative integer.");
				errorInChecking = true;
			}
			// for timepoint rows
			treemapTimepoints.clear();
			ArrayList<Integer> timepointStartColNum = new ArrayList<Integer>();
			ArrayList<Integer> timepointEndColNum = new ArrayList<Integer>(); 
			timepointStartColNum.add(-1);// for the comparison
			timepointEndColNum.add(-1);// for the comparison
			
			for(int i = 1; i < figs.length; i ++){
				String[] tuple = figs[i].split(",");
				int startColNum = Integer.parseInt(tuple[0]);
				int endColNum = Integer.parseInt(tuple[1]);
				
				// TODO XXX check the colNum's legality
				// is the colNum a non-nega int?
				// is the colNum bigger than the last one? (except the 1st one)
				// is the colNum not bigger than data table? -> TODO add check to the reading data batle!
				if ( startColNum > timepointEndColNum.get(timepointEndColNum.size() -1) && endColNum >= startColNum ){
					
					// delate the first [-1] only for the 1st time
					if(i == 1){
						timepointStartColNum.clear();
						timepointEndColNum.clear();
					}
					
					// make colNumLists
					timepointStartColNum.add(startColNum);
					timepointEndColNum.add(endColNum);
					
					// make treemapTimepoints
					for(int j = startColNum; j <= endColNum; j ++){
						treemapTimepoints.add(j);
					}
				}else {
					System.err.println("the given number for timepoint colum number: \""+figs[i]+"\", in 2nd arg: \""+a[1]+"\", is not acceptable.\n all numbers have to be non-negative, start column number should be bigger than the previous end column number, and end column number should be not smaller than the previous start column number");
					errorInChecking = true;
					break;
				}
			}

			
			//
			// 3rd and 4th args -> file path
			// dendrogramFile & expressionFile
			for(int i = 2; i <= 3; i++){
				File f = new File(a[i]);
				if(f.exists() && !f.isDirectory()) {
					switch (i) {
		            	case 2: expressionFile = f;
		            		setInputTableFilePath(f.getPath());
		            		break;	
		            	case 3: dendrogramFile = f;
		            		setInputTreeFilePath(f.getPath());
		            		break;
					}
					if(printComments)System.err.println(a[i]+" is a file");
				}else{
					System.err.println(errorHeader+ a[i]+" is not a file or does not exist");
					errorInChecking = true;
				}
			}
			
			//
			// 5th arg -> dir path
			// outputDirectry
			File f = new File(a[4]);
			if(f.exists() && f.isDirectory()){

				setOutputDir(f);// untested // outputDir = f;
				if(printComments)System.err.println(a[4]+"is a directry");
			} else{
				System.err.println(errorHeader+ a[4]+" is not a directry or does not exist");
				errorInChecking = true;
			}
			
			//
			// finish all fiexd arg check
			errorInFlow = false;
		} catch(Exception err){
			err.printStackTrace(System.err);
	    } finally {
			if (errorInFlow || errorInChecking) {
				if(errorInFlow)	System.err.println(errorHeader+ "could not finish checking args[]");
				if(errorInChecking)	System.err.println(errorHeader+ "error occurred above");
				System.exit(1);
			}
			
		}
		errorInChecking = false;

		
	}
	

	private static LinkedList<String> initOptionArgs(String[] o) {// return invalid option args
		String inputs[] = o;
		LinkedList<String> errOptionInputs = new LinkedList<String>();
		for(int i = 0; i < inputs.length; i++){// for input
			boolean matched = false;
			for(int j = 0; j < acceptOptions.length; j ++){
				if (inputs[i].equals(acceptOptions[j])){
					changeStatasInOptionSwitchs(j, true);
					matched = true;
				}
			}
			if(!matched){
				errOptionInputs.add(inputs[i]);
			}
		}
		return errOptionInputs;
	}

	private static void printOptionSwitchs(){// TODO
		System.err.print("optionSwitchs : ");
		ListIterator<Boolean> litr = getOptionSwitchs().listIterator();
		for(boolean sw : getOptionSwitchs()){
			System.err.print(sw+", ");
		}
//		while(litr.hasNext()){
//			System.err.print(litr+", ");
//		}
		System.err.println("");
	}
	
	
	
	public static String getDataName() {
		return clusterSplitter.dataName;
	}

	public static double getSuppValue() {
		return suppValue;
	}

	public static void setSuppValue(double suppValue) {
		clusterSplitter.suppValue = suppValue;
	}

	public static int getIdealClusterNum() {
		return idealClusterNum;
	}

	public static void setIdealClusterNum(int clusterNum) {
		clusterSplitter.idealClusterNum = clusterNum;
	}

	public static int getRealClusterNum() {
		return realClusterNum;
	}

	public static void setRealClusterNum(int realClusterNum) {
		clusterSplitter.realClusterNum = realClusterNum;
	}

	public static int getWinW() {
		return winW;
	}

	public static void setWinW(int winW) {
		clusterSplitter.winW = winW;
	}

	public static int getWinH() {
		return winH;
	}

	public static void setWinH(int winH) {
		clusterSplitter.winH = winH;
	}

	public static treeNode getBTObj() {
		return tnObj;
	}

	public static void setBTObj(treeNode bTObj) {
		tnObj = bTObj;
	}

	public static treeMethod getMTObj() {
		return tmObj;
	}

	public static void setMTObj(treeMethod mTObj) {
		tmObj = mTObj;
	}

	public static boolean isWriteClusterNum() {
		return writeClusterNum;
	}

	public static void setWriteClusterNum(boolean writeClusterNum) {
		clusterSplitter.writeClusterNum = writeClusterNum;
	}


	public static void setDataName(String dataName) {
		clusterSplitter.dataName = dataName;
	}

	public static String getInputTreeFilePath() {
		return inputTreeFilePath;
	}

	public static void setInputTreeFilePath(String inputTreeFilePath) {
		clusterSplitter.inputTreeFilePath = inputTreeFilePath;
	}



	public static String getInputTableFilePath() {
		return inputValueFilePath;
	}

	public static void setInputTableFilePath(String inputTableFilePath) {
		clusterSplitter.inputValueFilePath = inputTableFilePath;
	}

	public static char getUserSplitMode() {
		return userSplitMode;
	}

	public static void setUserSplitMode(char splitMode) {
		clusterSplitter.userSplitMode = splitMode;
	}

	public static char getOpeSplitMode() {
		return opeSplitMode;
	}

	public static void setOpeSplitMode(char opeSplitMode) {
		clusterSplitter.opeSplitMode = opeSplitMode;
	}

	public static int getUserClusterNum() {
		return userClusterNum;
	}

	public static void setUserClusterNum(int userClusterNum) {
		clusterSplitter.userClusterNum = userClusterNum;
	}

	public static ArrayList<Boolean> getOptionSwitchs() {
		return optionSwitchs;
	}

	public static void setOptionSwitchs(ArrayList<Boolean> optionSwitchs) {
		clusterSplitter.optionSwitchs = optionSwitchs;
	}

	public static int getMetaRowNum() {
		return metaRowNum;
	}

	public static void setMetaRowNum(int metaRowNum) {
		clusterSplitter.metaRowNum = metaRowNum;
	}

	public static ArrayList<Integer> getTreemapTimepoints() {
		return treemapTimepoints;
	}

	public static void setTreemapTimepoints(ArrayList<Integer> treemapTimepoints) {
		clusterSplitter.treemapTimepoints = treemapTimepoints;
	}

	public static File getOutputDir() {
		return outputDir;
	}

	public static void setOutputDir(File outputDir) {
		clusterSplitter.outputDir = outputDir;
	}

}
