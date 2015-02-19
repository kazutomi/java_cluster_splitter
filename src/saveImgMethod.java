import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;


public class saveImgMethod {
	
// TODO
//	make 
//		treeNode, treeMethod, clusterNodeList, width, height, clusterNum
//	as variable for this class
//	
//
//	

/*
 * algorithms and sub routines
 * 
 * - input values for drawing treemaps
 *  	<string> image name,
 * 		<string> output dir, 
 * 		<int> switch, 
 * 		<treeMethod> treeMethod class instance, 
 * 		<list<treeNode>> clusterNodeList head node, <- num of the list is the cluster number
 * 		<int> width,
 * 		<int> height
 * 
 * - algorithm
 * [1] decide the time point to draw
 * [2] create a blank canvas
 * [3] decide (x, y, w, h) for each genes' block by read the parent (recursively) starting with root
 * [4] after decided the (x, y, w, h) of a gene, decide (H, S, B) for the gene's block by read the data table of gene expressions
 * [5] then draw the block
 * [6] after all block be done -> save the 
 * 
 */



	// values did no used
//	private static treeNode tnObj; // = ControllMethod.tnObj;
	// should not use -> use the length of cluster treeNode list
//	private static int clusterNum;
	

	private static treeMethod tmObj = clusterSplitter.tmObj;
	private static LinkedList<treeNode> frameNodeList = new LinkedList<treeNode>();// for draw frames -> mainly for clusters
	private static LinkedList<treeNode> coatNodeList = new LinkedList<treeNode>();// for dras blocks -> mainly for each gene
	
	
	// dataset name -> should be a global value of clusterSplitter @@@
	private static String fileName; // = ControllMethod.getDataName();// you should make this variable refer from controlMthod
	

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		saveImgMethod.fileName = fileName;
	}

	private static int width = clusterSplitter.winW;
	private static int height = clusterSplitter.winH;
	
	private static treeNode tn;
	
	// values for recursive usage in drawing process
	private static double TMX = tmObj.getTreemapX();
	private static double TMY = tmObj.getTreemapY();
	private static double TMW = tmObj.getTreemapW();
	private static double TMH = tmObj.getTreemapH();

	public static void saveImg(){

	}

	// did not used
//	private static void makeFolder(String f){
//		String folderName = "./img/"+f;
//		File newDir = new File(folderName);
//
//		// if the directory does not exist, create it
//		if (!newDir.exists()) {
//			System.out.println("creating directory: " + folderName);
//			boolean result = newDir.mkdir();  
//
//			if(result) {    
//				System.out.println("DIR created");  
//			}else{//Error couldnt made the folder
//				
//			}
//		}
//	}
	
/* 	public static void saveImgs(
 * 		<string> image name,
 * 		<string> output dir, 
 * 		<int> switch, 
 * 		<treeMethod> treeMethod class instance, 
 * 		<list<treeNode>> clusterNodeList head node, <- num of the list is the cluster number
 * 		<int> width,
 * 		<int> height
 * 	)
*/	
	
	
	public static void saveImgs(String name, int G, String path) {// or 0x11<=>0x[color][line] G0->[-,-], G1[-,line], G2[color,-], G3[color, line]
		saveImgMethod.setFileName(name);
		//		int width = controllMethod.winW;
		//		int height = controllMethod.winH;
		//		treeNote tn;
		//		double TMX = mtObj.getTreemapX();
		//		double TMY = mtObj.getTreemapY();
		//		double TMW = mtObj.getTreemapW();
		//		double TMH = mtObj.getTreemapH();


		////////////////////////////////////////////
		//     make directory for the files      //
		//////////////////////////////////////////
//		System.out.println("cluster num -> "+controllMethod.getClusterNum()+"controll -> "+controllMethod.getClusterNum());//@test
		String folderName = path+"/"+saveImgMethod.fileName+"_G"+G+"C"+tmObj.getClusterList().size()+"_pix"+(int)saveImgMethod.TMW;
		File theDir = new File(folderName);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + folderName);
			boolean result = theDir.mkdir();  

			if(result) {    
				System.out.println("DIR created");  
			}
		}

		/////////////////////////////////////////////
		//            for all TIME POINT          //
		///////////////////////////////////////////
		//		System.out.println("enter saveImg");
		saveImgMethod.tn = tmObj.root;//@del @@ you should do not use all target node as tn after this.
		for(int t = 0; t < tmObj.root.getValue().size()/**/; t ++){
//			System.out.println("tn.getValue().size() -> " +tn.getValue().size());
			try {

				//				System.out.println("enter try");

				// TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
				// into integer pixels
				BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

				Graphics2D ig2 = bi.createGraphics();
				RenderingHints aa = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				ig2.setRenderingHints(aa);


				////////////////////////////////////////////
				// [draw / fill] for each CLUSTER color  //
				//////////////////////////////////////////
				//			System.out.println("before cluster");


				///////////////////////////////
				// FILL each cluster block  //
				/////////////////////////////
				if(G < 2){// no color
					treeNode locCluster;
					for (int i = 0; i < tmObj.getClusterList().size(); i++) {
//						tn = mtObj.getClusterList().get(i);//@del get cluster note
						locCluster = tmObj.getClusterList().get(i);// get cluster note
						//						
						Color colYtoB, colB, colY;
//						float [] colfYtoB = new float[3];
						float R = (float)((locCluster.getValue(t)-locCluster.getValueMin())/(locCluster.getValueMax()-locCluster.getValueMin()));
//						float H = (1.0f/6.0f) + (1.0f/6.0f) *R;
						colY = new Color((int)Color.YELLOW.getRed(),(int)Color.YELLOW.getGreen(), (int)Color.YELLOW.getBlue(), (int)((1.0-R) *Color.YELLOW.getAlpha()));          //
						colB = new Color((int)Color.BLUE.getRed(),(int)Color.BLUE.getGreen(), (int)Color.BLUE.getBlue(), (int)(Color.BLUE.getAlpha()*R) );
						colYtoB = colorUtilities.blend2AlphaColor(colY, colB);
						//					colYtoB.RGBtoHSB(colYtoB.getRed(), colYtoB.getGreen(),colYtoB.getBlue(), colfYtoB);
						//					g.setColor(Color.getHSBColor(colfYtoB[0], colfYtoB[1], colfYtoB[2]));
						ig2.setColor(colYtoB);
						ig2.fill(new Rectangle2D.Double(locCluster.getTMX() * TMW + TMX, locCluster.getTMY() * TMH + TMY, locCluster.getTMW() * TMW, locCluster.getTMH() * TMH));


						///////////////////////////////
						// DRAW each cluster block  // ------- all false
						/////////////////////////////
						if(false){
							ig2.setColor(Color.BLACK);
							ig2.draw(new Rectangle2D.Double(locCluster.getTMX() * TMW + TMX, locCluster.getTMY() * TMH + TMY, locCluster.getTMW() * TMW, locCluster.getTMH() * TMH));
						}
					}
				}
				//			System.out.println("out cluster");


				/////////////////////////////////////////
				//  [draw / fill] for each GENE block //
				///////////////////////////////////////
				//			System.out.println("before GENE");
				treeNode locLeaf = tmObj.getFirstLeaf();
//				tn = mtObj.getFirstLeaf();//@del
//				locLeaf.printNodeAllInfo();//@test
				while (locLeaf != null) {

					// g.setColor(Color.getHSBColor(1.0f,(float) (j*1.0/50), 1.0f)); 
					//				g.setColor(Color.WHITE);
					// g2.fill(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));


					///////////////////////////
					// FILL each GENE block //
					/////////////////////////
					if(G > 1){
						Color colYtoB;
						float R = (float)((locLeaf.getValue(t)-locLeaf.getValueMin())/(locLeaf.getValueMax()-locLeaf.getValueMin()));
						//					System.out.println(R);
						//					colYtoB = blend2Colors(Color.YELLOW, Color.BLUE, ((tn.getValue(0)-tn.getValueMin())/(tn.getValueMax()-tn.getValueMin())));

						Color colY = new Color((int)Color.YELLOW.getRed(),(int)Color.YELLOW.getGreen(), (int)Color.YELLOW.getBlue(), (int)((1.0-R) *Color.YELLOW.getAlpha()));   //@       //
						Color colB = new Color((int)Color.BLUE.getRed(),(int)Color.BLUE.getGreen(), (int)Color.BLUE.getBlue(), (int)(Color.BLUE.getAlpha()*R) );
						//						colYtoB = colorUtilities.blend2AlphaColor(colY, colB);
						colYtoB = colorUtilities.blend2Colors(colY, colB, R);
						ig2.setColor(colYtoB);
						//				g.setColor(Color.YELLOW);
						//			g2.fill(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));

						//				g.setColor(colB);
						ig2.fill(new Rectangle2D.Double(locLeaf.getTMX() * TMW + TMX, locLeaf.getTMY() * TMH + TMY, locLeaf.getTMW() * TMW, locLeaf.getTMH() * TMH));
					}

					/////////////////////////////
					//  DRAW each GENE block  //
					///////////////////////////
					if(G == 2){// same color with fill
						ig2.draw(new Rectangle2D.Double(locLeaf.getTMX() * TMW + TMX, locLeaf.getTMY() * TMH + TMY, locLeaf.getTMW() * TMW, locLeaf.getTMH() * TMH));
					}
					if(G == 1 || G == 3){// draw gene block line
						ig2.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
						ig2.draw(new Rectangle2D.Double(locLeaf.getTMX() * TMW + TMX, locLeaf.getTMY() * TMH + TMY, locLeaf.getTMW() * TMW, locLeaf.getTMH() * TMH));
					}
					locLeaf = locLeaf.getNextLeaf();
					// j++;
				}
				//				System.out.println("out GENE");

				//////////////////////////////
				//  draw for cluster LINE  //
				////////////////////////////
				//			System.out.println("before line");
				if(true){
					for (int i = tmObj.getClusterList().size() - 2; i >= 0; i--) {// 100 cluster -> i = 98(100-2), 25
						// cluster -> i = 23 (25-2)  because... if you draw 1 line it's for 2 cluster, if you set the i 10 then 10->0, it draws 11 times
						treeNode locCluster = tmObj.getBranchArray()[i];
//						tn = mtObj.branchArray[i];//@del

						// float hue = (float) (i*1.0/mtObj.branchArray.length * 0.7 + 0.25);
						// g.setColor(Color.getHSBColor(hue,1.0f,1.0f));
						ig2.setColor(Color.BLACK);//
						//ig2.setColor(new Color(102, 237, 188));
						ig2.setStroke(new BasicStroke(2));
						ig2.draw(new Line2D.Double(locCluster.getSX() * TMW + TMX, locCluster.getSY() * TMH + TMY, locCluster.getEX() * TMW + TMX, locCluster.getEY() * TMH + TMY));

					}
				}


				///////////////////////////////
				// decide file name to save //
				/////////////////////////////
				//			System.out.println("before save img");
				//				String timePoint = ""+(t+1);
				String timePoint = String.format("%02d", (t+1));
				String outputName = saveImgMethod.fileName+"_G"+G+"C"+tmObj.getClusterList().size()+"T"+timePoint+".PNG";
				ImageIO.write(bi, "PNG", new File(folderName +"/"+ outputName));
				System.out.println("saved img "+outputName);
				//			ImageIO.write(bi, "JPEG", new File("c:\\yourImageName.JPG"));
				//			ImageIO.write(bi, "gif", new File("c:\\yourImageName.GIF"));
				//			ImageIO.write(bi, "BMP", new File("c:\\yourImageName.BMP"));


				//end of saving images
			} catch (IOException ie) {
				ie.printStackTrace();
				System.out.println("error in save img");
			}
//			System.out.println("save done -> t ="+ t +", tn.getValue().size() -> *");//@test
//			System.out.println("save done -> t ="+ t +", tn.getValue().size() ->"+tn.getValue().size());//@test
		}
	}

	public static void saveFrame(String name, String path) {
		boolean printComment = false;
		saveImgMethod.setFileName(name);
		try {
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			Graphics2D ig2 = bi.createGraphics();
			RenderingHints aa = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			ig2.setRenderingHints(aa);


			////////////////////////////////////////////
			// [draw / fill] for each CLUSTER color  //
			//////////////////////////////////////////
			//			System.out.println("before cluster");
			for (int i = 0; i < tmObj.getClusterList().size(); i++) {//XXX
				
				//get target node
				tn = tmObj.getClusterList().get(i);// get cluster note
				
				// for checking cluster nodes
				if(printComment){System.err.println("for draw frame: node number = "+i+", node pos = "+tn.getNodePos());}
				
				
				double cluX = tn.getTMX() * TMW + TMX;
				double cluY = tn.getTMY() * TMH + TMY;
				double cluW = tn.getTMW() * TMW;
				double cluH = tn.getTMH() * TMH;

				///////////////////////////////
				// FILL each cluster block  //
				/////////////////////////////
				ig2.setColor(Color.WHITE);
				ig2.fill(new Rectangle2D.Double(cluX, cluY, cluW, cluH));

				///////////////////////////////
				// DRAW each cluster block  // 
				/////////////////////////////
				ig2.setColor(Color.BLACK);
				ig2.draw(new Rectangle2D.Double(cluX, cluY, cluW, cluH));
			}

			for (int i = 0; i < tmObj.getClusterList().size(); i++) {
				tn = tmObj.getClusterList().get(i);// get cluster note
				double cluX = tn.getTMX() * TMW + TMX;
				double cluY = tn.getTMY() * TMH + TMY;
				double cluW = tn.getTMW() * TMW;
				double cluH = tn.getTMH() * TMH;
				
				Font font = new Font("TimesRoman", Font.PLAIN, 18);
				ig2.setFont(font);
				String message = "["+tn.getHira()+"-"+tn.getHorz()+"]";
				FontMetrics fontMetrics = ig2.getFontMetrics();
				int stringWidth = fontMetrics.stringWidth(message);
				int stringHeight = fontMetrics.getAscent();
				ig2.setPaint(Color.black);
				ig2.drawString(message, (int)(cluX + (cluW-stringWidth) / 2), (int)(cluY + cluH / 2 + stringHeight / 4));


			}


			///////////////////////////////
			// decide file name to save //
			/////////////////////////////
			//			System.out.println("before save img");

			String outputName = saveImgMethod.fileName+"FrameC"+tmObj.getClusterList().size()+".PNG";
			ImageIO.write(bi, "PNG", new File(path+"/"+outputName));
			System.err.println("saved img "+outputName);
			//			ImageIO.write(bi, "JPEG", new File("c:\\yourImageName.JPG"));
			//			ImageIO.write(bi, "gif", new File("c:\\yourImageName.GIF"));
			//			ImageIO.write(bi, "BMP", new File("c:\\yourImageName.BMP"));


			//end of saving images
		} catch (IOException ie) {
			ie.printStackTrace();
			System.out.println("error in save img");
		}




	}
}

//			Font font = new Font("TimesRoman", Font.BOLD, 20);
//			ig2.setFont(font);
//			String message = "www.java2s.com!";
//			FontMetrics fontMetrics = ig2.getFontMetrics();
//			int stringWidth = fontMetrics.stringWidth(message);
//			int stringHeight = fontMetrics.getAscent();
//			ig2.setPaint(Color.black);
//			ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);

