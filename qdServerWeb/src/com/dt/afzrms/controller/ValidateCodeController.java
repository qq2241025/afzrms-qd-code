package com.dt.afzrms.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dt.afzrms.common.Const;
/**
 * 
 * @Title 验证码图片生成类   
 * @Description TODO
 * @author 
 * @createDate 2012-6-19 下午02:04:59
 * @modifier 
 * @modifyDate 
 * @version 1.0
 *
 */
@Controller
@RequestMapping(value = "/validateCode")
public class ValidateCodeController extends BaseController{
	
	 private char[] charSeq="abcdefghijkmnpqrstuvwxyzABCDEFGHJLMNPQRSTUVWXYZ0123456789".toCharArray(); 

	@RequestMapping(value = "image", method = RequestMethod.GET)
	public void getCode(HttpServletRequest request, HttpServletResponse response) {
		
		// 在缓存中创建图形对象，然后输出
		int width = 60, height = 20; // 输出图片的大小
		BufferedImage buff = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB); // 指定缓冲的图片和颜色结构
		Graphics g = buff.getGraphics(); // 得到绘图对象

		// 利用graphics对象我们就可以画图了:
		// 矩形:
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		//设置字体样式
		Font font=new Font("Times New Roman", Font.PLAIN, 20);

		//设置字体
		g.setFont(font); 
		
		// 干扰线:(循环的画出细小的线条)
		Random rand = new Random();

		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160,200));
		for (int i=0;i<155;i++){
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);
			int xl = rand.nextInt(12);
			int yl = rand.nextInt(12);
			g.drawLine(x,y,x+xl,y+yl);
		}

		// 验证码:
		String coding = ""; // 保存得到的验证码字符串
		for (int i = 0; i < 4; i++) {
			String rands=String.valueOf(charSeq[rand.nextInt(charSeq.length)]); 
			coding+=rands;
			// 将认证码显示到图象中
			g.setColor(new Color(20+rand.nextInt(110),20+rand.nextInt(110),20+rand.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
			g.drawString(rands,13*i+6,16);
		}
		// 信息存入session
		request.getSession().setAttribute(Const.VALIDATE_CODE, coding);

		// 清空缓存区:(这一步非常重要,不然服务器会报错误)
		g.dispose();
		
		response.setContentType("image/jpeg");
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			ImageIO.write(buff, "jpeg", out);
			out.flush(); // 强行将缓冲区的内容输入到页面
			out.close();
			out = null;
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {}
				out = null;
			}
		}
		
	}

	/**
	 * 
	 * 随机产生指定区域内的RGB颜色
	 * @param min
	 * @param max
	 * @return
	 *
	 */
	private Color getRandColor(int min, int max) {
		Random random1 = new Random();
		if (min >= 255)
			min = 255;
		if (max >= 255)
			max = 255;
		int r = min + random1.nextInt(max - min);
		int g = min + random1.nextInt(max - min);
		int b = min + random1.nextInt(max - min);
		return new Color(r, g, b); 
	}
}