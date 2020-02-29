package com.yy.framework.web.lang;

import org.apache.commons.lang.math.RandomUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ValidateCode {
	
	private static final String[] FONT_TYPES = { "Arial", "AvantGarde Bk BT", "Calibri" };
	
	public static BufferedImage createImageValidateCode(int w, int h, int x, int y, int fontSize, String code) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		createBackground(g, w, h);
		
		Random random = new Random();
		
		g.setColor(new Color(50 + random.nextInt(100), 50 + random.nextInt(100), 50 + random.nextInt(100)));
		int s = RandomUtils.nextInt(FONT_TYPES.length);
		g.setFont(new Font(FONT_TYPES[s], Font.BOLD, fontSize)); 
		g.drawString(code, x, y);
		g.dispose();
		
		return image;
	}
	
	private static void createBackground(Graphics g, int w, int h) {
		// 填充背景
		g.setColor(getRandColor(220,250)); 
		g.fillRect(0, 0, w, h);
		// 加入干扰线条
		for (int i = 0; i < 8; i++) {
			g.setColor(getRandColor(40, 150));
			Random random = new Random();
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			int x1 = random.nextInt(w);
			int y1 = random.nextInt(h);
			g.drawLine(x, y, x1, y1);
		}
	}
	
	private static Color getRandColor(int fc, int bc) { 
		int f = fc;
		int b = bc;
		Random random = new Random();
		if (f > 255) {
			f = 255;
		}
		if (b > 255) {
			b = 255;
		}
		return new Color(f + random.nextInt(b - f), f + random.nextInt(b - f), f + random.nextInt(b - f));
	}

}
