import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.imageio.ImageIO;

public class SteganoImgProcess {
	
	String ext;
	int encodedMsgOffset;
	
	BufferedImage encode(BufferedImage input, BufferedImage output, int width, int height, String msg)
	{		
		int msgLength = msg.length(); 
		String message = "!encoded!" + msgLength + "!" + msg; 
		msgLength = message.length(); 
		int[] twoBitMessage = new int[4 * msgLength];
		
		char currentChar;
		for(int i =0; i < msgLength ; i++) {
			currentChar = message.charAt(i);
			twoBitMessage[4*i + 0] = (currentChar >> 6) & 0x3; 
			twoBitMessage[4*i + 1] = (currentChar >> 4) & 0x3; 
			twoBitMessage[4*i + 2] = (currentChar >> 2) & 0x3; 
			twoBitMessage[4*i + 3] = (currentChar)      & 0x3; 		}
				
		int pixel, pixOut, count = 0;;
		loop: for(int i = 0; i < width; i++) 
		{
			for(int j = 0; j < height; j++)
			{
				if(count < 4*msgLength) 
				{ 
					pixel = input.getRGB(i, j);
					pixOut = (pixel & 0xFFFFFFFC) | twoBitMessage[count++];
					output.setRGB(i, j, pixOut); 	
				} 
				else 
				{
					break loop;
				}
			}
		}
		return output;
	}
	
	
	String decode(BufferedImage input, int width, int height)
	{		
		if(!isEncoded(input, width, height)) {
			return null;
		}
		
		int msgLength = getEncodedLength(input, width, height);
				
		StringBuffer decodedMsg = new StringBuffer();
		Deque<Integer> listChar = new ArrayDeque<Integer>();
		
		int pixel, temp, charOut, ignore = 0, count = 0;
		loop: for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(ignore < 36 + 4*(String.valueOf(msgLength).length()+1)) {
					ignore++;
					continue;
				}
				
				if(count++ == 4*msgLength) {
					break loop;
				}
				pixel = input.getRGB(i, j); 
				temp = pixel & 0x03; 
				
				listChar.add(temp); 
				
				if(listChar.size() >=4) {
					charOut = (listChar.pop() << 6) | (listChar.pop() << 4) | (listChar.pop() << 2) | listChar.pop() ;
					decodedMsg.append((char)charOut);
				}
			}
			
		}
		
		String outputMsg = new String(decodedMsg);
		
		return outputMsg;
	} //end of decode()
	
	boolean isEncoded(BufferedImage input, int width, int height) { 
		
		StringBuffer decodedMsg = new StringBuffer();
		Deque<Integer> listChar = new ArrayDeque<Integer>();
		
		int pixel, temp, charOut, count = 0;
		loop: for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++, count++) {
				
				if(count == 45) { 
				break loop;
				}
				pixel = input.getRGB(i, j); 
				temp = pixel & 0x03; 
				
				listChar.add(temp); 
				
				if(listChar.size() >=4) {
					charOut = (listChar.pop() << 6) | (listChar.pop() << 4) | (listChar.pop() << 2) | listChar.pop() ;
					decodedMsg.append((char)charOut);
					count++;
				}
			}			
		}
		
		String check = new String(decodedMsg);
		System.out.println(check + " " + check.length());
		if (check.compareTo("!encoded!") == 0) {
			System.out.println("true");
			return true;
		} else {
			return false;
		}
		
	} 
	
	int getEncodedLength(BufferedImage input, int width, int height) 
	{ 
		
		StringBuffer decodedMsg = new StringBuffer();
		Deque<Integer> listChar = new ArrayDeque<Integer>();
		
		int pixel, temp, charOut, count = 0;
		loop: for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				if(count < 36) { 
					count++;
					continue;
				}
				
				pixel = input.getRGB(i, j); 
				temp = pixel & 0x03; 
				
				listChar.add(temp); 
				
				if(listChar.size() >=4) {
					
					charOut = (listChar.pop() << 6) | (listChar.pop() << 4) | (listChar.pop() << 2) | listChar.pop() ;
					if((char)charOut == '!') { 
						break loop;
					} else {
						decodedMsg.append((char)charOut); 
					}
				}
			}
			
		}
		
		String length = new String(decodedMsg);
		System.out.println("length is " + Integer.parseInt(length));
		
		return Integer.parseInt(length);
	} 
	
}