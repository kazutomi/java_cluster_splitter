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
	 * -0 text/testRawDataR20.csv text/testDendrogramR20.txt  outdir_R20 
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
	private static char userSplitMood = '|';// -> '+', '-', '/',
	private static char opeSplitMood = '|';// -> '+', '-', '/',
	private static File expressionFile;
	private static File dendrogramFile;
	private static File outputDir;
	
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
	private static boolean hasExtraDataRow = false;

	public static boolean isHasExtraDataRow() {
		return hasExtraDataRow;
	}

	public static void setHasExtraDataRow(boolean hasExtraDataRow) {
		clusterSplitter.hasExtraDataRow = hasExtraDataRow;
	}
	
	private static String errorHeader = " [ERROR] : ";

	public static void main(String[] args) {
		
		// checking args[] number
		boolean errorInFlow = true;// init -> true
		boolean printComments = false;
		
		//init
		initOptionSettings();
		checkArgs(args);


		errorInFlow = true;
		try {
			tmObj.makeData(inputTreeFilePath, inputValueFilePath);// read csv file
			tmObj.writeFilesForTominagaModule(outputDir);
			if(printComments)System.out.println("saved cluster lists");
			errorInFlow = false;
		}catch(Exception err){
		      err.printStackTrace(System.err);
	    }finally {
	    	if(printComments){
	    		System.out.println("user  number : "+userClusterNum);
	    		System.out.println("ideal number : "+idealClusterNum);
	    		System.out.println("real  number : "+realClusterNum);
	    	}
			if (errorInFlow) {
				System.err.println("error in makeData and writeData");
				System.exit(1);
			} else {
				if(printComments){
					System.out.println("finished");
					System.out.println("maxMidNodeHeightWithLeafChile"+tmObj.getMaxMiNNodeHeightWithLeafChild());
				}
				if(printClustNum)System.out.println((userSplitMood) +""+ userClusterNum+" : "+tmObj.getClusterNodeList().size());
				System.exit(0);
			}
		}
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
		
		if (a.length < 4){
			System.err.println(errorHeader+"This program needs at least 4 args");
			System.exit(1);
		}else if(a.length > 4 + acceptOptions.length){
			System.err.println(errorHeader+"This program accept at most 5 args");
			System.exit(1);
		}else{//has right input args 
			//for fixed args
			checkFixedArgs(Arrays.copyOfRange(a, 0, 4));
			//for option args (if there are any)
			if(a.length > 4) checkOptionArgs(Arrays.copyOfRange(a, 4, a.length));
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
			System.out.println("args:");
			for(String s:a){
				System.out.println(s);
			}
			System.out.println();
		}
		
		//checking are the args[] qualified
		errorInChecking = false; errorInFlow = true; printComments = false;
		try {
			//splitMood and cluster number;
			setUserSplitMood(a[0].charAt(0));
			setOpeSplitMood(getUserSplitMood());
			userClusterNum = -1;
			if (getUserSplitMood() == '+' || getUserSplitMood() == '-') {
				try{
					userClusterNum = Integer.parseInt(a[0].substring(1));
				} catch (Exception e) {
					System.err.println(errorHeader+ "unqualified cluster number ->" + a[0]);
					errorInChecking = true;
					System.err.println(e);
				}
			} else {
				setUserSplitMood('|');
				userClusterNum = Integer.parseInt(a[0]);
			}
			idealClusterNum = userClusterNum;
			if (printComments) System.out.println("cluster split mood -> " + getUserSplitMood()+ ", num ->" + idealClusterNum);

			// dendrogramFile & expressionFile
			for(int i = 1; i <= 2; i++){
				File f = new File(a[i]);
				if(f.exists() && !f.isDirectory()) {
					switch (i) {
		            	case 1: expressionFile = f;
		            		setInputTableFilePath(f.getPath());
		            		break;	
		            	case 2: dendrogramFile = f;
		            		setInputTreeFilePath(f.getPath());
		            		break;
					}
					if(printComments)System.out.println(a[i]+" is a file");
				}else{
					System.err.println(errorHeader+ a[i]+" is not a file or does not exist");
					errorInChecking = true;
				}
			}

			// outputDirectry
			File f = new File(a[3]);
			if(f.exists() && f.isDirectory()){
				outputDir = f;
				if(printComments)System.out.println(a[3]+"is a directry");
			} else{
				System.err.println(errorHeader+ a[3]+" is not a directry or does not exist");
				errorInChecking = true;
			}
			
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
		System.out.print("optionSwitchs : ");
		ListIterator<Boolean> litr = getOptionSwitchs().listIterator();
		for(boolean sw : getOptionSwitchs()){
			System.out.print(sw+", ");
		}
//		while(litr.hasNext()){
//			System.out.print(litr+", ");
//		}
		System.out.println("");
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

	public static char getUserSplitMood() {
		return userSplitMood;
	}

	public static void setUserSplitMood(char splitMood) {
		clusterSplitter.userSplitMood = splitMood;
	}

	public static char getOpeSplitMood() {
		return opeSplitMood;
	}

	public static void setOpeSplitMood(char opeSplitMood) {
		clusterSplitter.opeSplitMood = opeSplitMood;
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



}
