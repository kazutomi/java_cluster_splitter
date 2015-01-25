import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class ControllMethod {
	//globals
	private static String folderName = "AR0278TM";
	private static String valueDataName = "AR0278TM.csv";
	private static String treeDataName = "AR0278TM_clusterP.txt";
	
	public static int winW = 2000;
	public static int winH = 2000;
	

	
	
	
	
	
	
	
//	private static String fileName ="AR0278_1_Signal2";//"ConvTime"; //"AR0278_1_Signal2";//"AR0538_AR1004_Signals2";"AR1024_Signal_mfy4";"Figure2_mfy2-2";"Figure2_mfy2-2_test20L";
	private static String dataName ="AR0278";// fileName; //"AR0278";//"AR0538";"AR1361";"Figure2";
	private static double suppValue = 0;//"Figure2_mfy2-2" -> 5.06, others -> 0
	private static int clusterNum = 100;//"Figure2_mfy2-2" -> 25, others -> 100; convtime -> 7, 2, 172...

	//"./output/"+fileName+"Clst"+clusterNum)+"/"+fileName+"Clst"+clusterNum+"treeInfo.text"

	private static String inputValueFilePath ="."+File.separator+"input"+File.separator+folderName+File.separator+valueDataName;   //"/Users/rh/Documents/Hitachi/workSP/convTime/personalConvTimeSum.txt";//@tempo(?)
	private static File inputValueFile = new File(inputValueFilePath);//@necessary?
	private static String inputTreeFilePath ="."+File.separator+"input"+File.separator+folderName+File.separator+treeDataName;   //"/Users/rh/Documents/Hitachi/workSP/convTime/convTimeSumClusteringPeason.txt";//@tempo(?)
	private static File inputTreeFile = new File(inputTreeFilePath);//@necessary?
	private static File outputFolder = null; //created after mada data. // FileMethod.makeNewDirectory("./output/"+folderName+"/","Treemap"); //("/Users/rh/Documents/Hitachi/workSP/convTime/", "Treemap");//@necessary?
	private static String outputFolderName ="";//same as the outputFolder   // outputFolder.getPath();  //.getAbsolutePath();//@tempo(?)
	
	static treeNode tnObj = new treeNode(null);// tree root;
	static treeMethod tmObj = new treeMethod(tnObj);//for make tree// for initialize btObj as root in treeMethod
	
	private static boolean writeClusterNum = false;//@tempo
	private static boolean hasExtraDataRow = false;

	public static boolean isHasExtraDataRow() {
		return hasExtraDataRow;
	}
	public static void setHasExtraDataRow(boolean hasExtraDataRow) {
		ControllMethod.hasExtraDataRow = hasExtraDataRow;
	}
	public static void main(String[] args) {
		
		// -[step1.1]- make data
		writeClusterNum = false;//@switch
		tmObj.makeData(inputTreeFilePath, inputValueFilePath);// read csv file
		
		// -[step1.2]make outputfolder
		setOutputFolder(FileMethod.makeNewDirectory("."+File.separator+"output"+File.separator, folderName+"_Clst"+clusterNum));
		setOutputFolderName(getOutputFolder().getPath());

//				windowMethod.makeWindow();//@@@ no need for saving only program
		//System.out.println("widowMethod compleat");//@@@test
		
		//// -[step2.1]- make treeMap output folder
		String treemapOutputFolderName = FileMethod.makeNewDirectory(getOutputFolderName(), "Treemap").getPath(); 
		
		//// -[step2.2]- create and save treemaps
////		saveImgMethod.saveImgs(0);// or 0x11<=>0x[color][line] G0->[-,-], G1[-,line], G2[color,-], G3[color, line]
////		saveImgMethod.saveImgs(1);// no color, only gene's block line
		saveImgMethod.saveImgs(folderName, 2, treemapOutputFolderName);//only genes color -> most useful
////		saveImgMethod.saveImgs(3);//gene's color and gene's block line
		saveImgMethod.saveFrame(folderName, treemapOutputFolderName);//(?) no color only gene cluster's line and name -> to show the treemap structure
		
		
		//// -[step3]- save text files
		//save tree cluster lists
		tmObj.writeClusterLeafInfo(outputFolderName);////@@@ not inspected
		System.out.println("saved cluster lists");
		
		//save tree structure file
		//FileSaveMethod.saveFile(new File(FolderUtility.chooseFolder(getOutputFolderName())+"/"+folderName+"Clst"+clusterNum+"treeInfo.text"), TreeUtility.makeAllTreePathway(tmObj, "UP") + TreeUtility.makeAllTreePathway(tmObj, "CENTER")) ;
		
		System.out.println("finished"); System.exit(0);
	}

	private static class windowMethod extends JPanel {
		static void makeWindow() {
			JFrame frameObj = new JFrame("Super draws");

			frameObj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			drawing p = new drawing();
			frameObj.add(p);
			frameObj.setSize(ControllMethod.winW, ControllMethod.winW);
			frameObj.setVisible(true);
		}
	}

//	public static String getFileName(){
//		return ControllMethod.fileName;
//	}
	public static String getDataName(){
		return ControllMethod.dataName;
	}
	public static double getSuppValue() {
		return suppValue;
	}
	public static void setSuppValue(double suppValue) {
		ControllMethod.suppValue = suppValue;
	}
	public static int getClusterNum() {
		return clusterNum;
	}
	public static void setClusterNum(int clusterNum) {
		ControllMethod.clusterNum = clusterNum;
	}
	public static int getWinW() {
		return winW;
	}
	public static void setWinW(int winW) {
		ControllMethod.winW = winW;
	}
	public static int getWinH() {
		return winH;
	}
	public static void setWinH(int winH) {
		ControllMethod.winH = winH;
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
		ControllMethod.writeClusterNum = writeClusterNum;
	}
//	public static void setFileName(String fileName) {
//		ControllMethod.fileName = fileName;
//	}
	public static void setDataName(String dataName) {
		ControllMethod.dataName = dataName;
	}
	public static String getInputTreeFilePath() {
		return inputTreeFilePath;
	}
	public static void setInputTreeFilePath(String inputTreeFilePath) {
		ControllMethod.inputTreeFilePath = inputTreeFilePath;
	}
	public static File getInputTreeFile() {
		return inputTreeFile;
	}
	public static void setInputTreeFile(File inputTreeFile) {
		ControllMethod.inputTreeFile = inputTreeFile;
	}
	public static String getInputTableFilePath() {
		return inputValueFilePath;
	}
	public static void setInputTableFilePath(String inputTableFilePath) {
		ControllMethod.inputValueFilePath = inputTableFilePath;
	}
	public static File getInputTableFile() {
		return inputValueFile;
	}
	public static void setInputTableFile(File inputTableFile) {
		ControllMethod.inputValueFile = inputTableFile;
	}
	public static String getOutputFolderName() {
		return outputFolderName;
	}
	public static void setOutputFolderName(String outputFolderName) {
		ControllMethod.outputFolderName = outputFolderName;
	}
	public static File getOutputFolder() {
		return outputFolder;
	}
	public static void setOutputFolder(File outputFolder) {
		ControllMethod.outputFolder = outputFolder;
	}

}
