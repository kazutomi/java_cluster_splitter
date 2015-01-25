import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class drawing extends JPanel {
	// static treeMethod tmObj = new treeMethod(null);// a treeNote object;
	static treeNode tnObj = ControllMethod.tnObj;
	static treeMethod mtObj = ControllMethod.tmObj;

	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		this.setBackground(Color.RED);

		Graphics2D g2 = (Graphics2D) g;
		RenderingHints aa = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(aa);

		// g.setColor(Color.RED);
		// g.drawLine(10, 10, 100, 30);
		//
		// g.setColor(Color.BLUE);
		// g.drawRect(10, 50, 100, 30);
		//
		// g.setColor(Color.ORANGE);
		// g.fill3DRect(10, 90, 100, 30, true);
		//
		// g2.draw(new Line2D.Double(20, 20, 110, 40));
		// g2.draw(new Rectangle2D.Double(120, 120, 50, 50));
		// g2.draw(new RoundRectangle2D.Double(50, 50, 30, 30, 10, 10));

		
		treeNode tn;
		double TMX = mtObj.getTreemapX();
		double TMY = mtObj.getTreemapY();
		double TMW = mtObj.getTreemapW();
		double TMH = mtObj.getTreemapH();

		//draw for each cluster color
		for (int i = 0; i < ControllMethod.getClusterNum(); i++) {
			// get cluster note
			tn = mtObj.getClusterList().get(i);

			/////////////////////////////
			// fill each cluster block //
			/////////////////////////////
			if(false){
				Color colYtoB, colB, colY;
				float [] colfYtoB = new float[3];
				float R = (float)((tn.getValue(14)-tn.getValueMin())/(tn.getValueMax()-tn.getValueMin()));
				float H = (1.0f/6.0f) + (1.0f/6.0f) *R;
				
				colY = new Color((int)Color.YELLOW.getRed(),(int)Color.YELLOW.getGreen(), (int)Color.YELLOW.getBlue(), (int)((1.0-R) *Color.YELLOW.getAlpha()));          //
				colB = new Color((int)Color.BLUE.getRed(),(int)Color.BLUE.getGreen(), (int)Color.BLUE.getBlue(), (int)(Color.BLUE.getAlpha()*R) );
				colYtoB = blend2AlphaColor(colY, colB);
//				colYtoB.RGBtoHSB(colYtoB.getRed(), colYtoB.getGreen(),colYtoB.getBlue(), colfYtoB);
				
//				g.setColor(Color.getHSBColor(colfYtoB[0], colfYtoB[1], colfYtoB[2]));
				g.setColor(colYtoB);

				g2.fill(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));
			}

			//draw each cluster block
			if(false){
				g.setColor(Color.BLACK);
				g2.draw(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));
			}
		}

		// draw for each gene block
		// int j = 0;
		tn = mtObj.firstLeaf;
		while (tn != null) {

			// g.setColor(Color.getHSBColor(1.0f,(float) (j*1.0/50), 1.0f)); 
//			g.setColor(Color.WHITE);
			// g2.fill(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));

			
			//////////////////////////////
			// fill for each gene block //
			//////////////////////////////
			if(true){
				Color colYtoB;
				float R = (float)((tn.getValue(0)-tn.getValueMin())/(tn.getValueMax()-tn.getValueMin()));
				float H = (float) ((1.0f/6.0f) + (1.0f/6.0f) * R);
				//			System.out.println(R);
//				colYtoB = blend2Colors(Color.YELLOW, Color.BLUE, ((tn.getValue(0)-tn.getValueMin())/(tn.getValueMax()-tn.getValueMin())));

				Color colY = new Color((int)Color.YELLOW.getRed(),(int)Color.YELLOW.getGreen(), (int)Color.YELLOW.getBlue(), (int)((1.0-R) *Color.YELLOW.getAlpha()));          //
				Color colB = new Color((int)Color.BLUE.getRed(),(int)Color.BLUE.getGreen(), (int)Color.BLUE.getBlue(), (int)(Color.BLUE.getAlpha()*R) );
				colYtoB = blend2AlphaColor(colY, colB);
				g.setColor(colYtoB);
				//				g.setColor(Color.YELLOW);
				//			g2.fill(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));

				//				g.setColor(colB);
				g2.fill(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));
			}

			//draw each gene block
			g.setColor(Color.LIGHT_GRAY);
			g.setColor(Color.WHITE);
//			g2.draw(new Rectangle2D.Double(tn.getTMX() * TMW + TMX, tn.getTMY() * TMH + TMY, tn.getTMW() * TMW, tn.getTMH() * TMH));
			tn = tn.getNextLeaf();
			// j++;
		}

		///////////////////////////
		// draw for cluster line //
		///////////////////////////
		for (int i = ControllMethod.getClusterNum() - 2; i >= 0; i--) {// 100 cluster -> i = 98(100-2), 25
			// cluster -> i = 23 (25-2)
			// because...
			// if you draw 1 line it's for 2 cluster,
			// if you set the i 10 then 10->0, it draws 11 times
			//
			tn = mtObj.getBranchArray()[i];

			// float hue = (float) (i*1.0/mtObj.branchArray.length * 0.7 +
			// 0.25);
			// g.setColor(Color.getHSBColor(hue,1.0f,1.0f));
			g.setColor(Color.BLACK);
			g2.draw(new Line2D.Double(tn.getSX() * TMW + TMX, tn.getSY() * TMH + TMY, tn.getEX() * TMW + TMX, tn.getEY() * TMH + TMY));

		}
	}

	private Color blend2Colors(Color c0, Color c1, double d) {// d = 0.0~1.0, no alpha

		double weight0 = 1.0-d;
		double weight1 = d;

		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();

		return new Color((int) r, (int) g, (int) b);
	}

	public Color blend2AlphaColor(Color c0, Color c1) {
		double totalAlpha = c0.getAlpha() + c1.getAlpha();
		double weight0 = c0.getAlpha() / totalAlpha;
		double weight1 = c1.getAlpha() / totalAlpha;

		double r = weight0 * c0.getRed() + weight1 * c1.getRed();
		double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
		double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
		double a = Math.max(c0.getAlpha(), c1.getAlpha());

		return new Color((int) r, (int) g, (int) b, (int) a);
	}



}



