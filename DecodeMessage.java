import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;

public class DecodeMessage extends JFrame implements ActionListener
{
	JButton open = new JButton("Open");
	JButton decode = new JButton("Decode");
    JButton reset = new JButton("Reset");
	JTextArea message = new JTextArea(10,3);
	BufferedImage image = null;
	JScrollPane imagePane = new JScrollPane();	
	
	public DecodeMessage() 
	{
		JPanel p = new JPanel(new FlowLayout());
		p.add(open);
		p.add(decode);
		p.add(reset);
		this.getContentPane().add(p, BorderLayout.NORTH);
		open.addActionListener(this);
		decode.addActionListener(this);
		reset.addActionListener(this);
    
		p = new JPanel(new GridLayout(1,1));
		p.add(new JScrollPane(message));
		message.setFont(new Font("Arial",Font.BOLD,20));
		p.setBorder(BorderFactory.createTitledBorder("Decoded message"));
		message.setEditable(false);
		this.getContentPane().add(p, BorderLayout.SOUTH);
		
		imagePane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
		this.getContentPane().add(imagePane, BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);   
		//this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
    }
	
	public void actionPerformed(ActionEvent ae)
	{
		Object o = ae.getSource();
		if(o == open)
			openImage();
		else if(o == decode)
			decodeMessage();
		else if(o == reset) 
			resetInterface();
	}
	
	private java.io.File showFileDialog(boolean open)
	{
		JFileChooser fc = new JFileChooser("Open an image");
		javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter()
		{
			public boolean accept(java.io.File f) 
			{
				String name = f.getName().toLowerCase();
				return f.isDirectory() ||   name.endsWith(".png") || name.endsWith(".bmp");
			}
			public String getDescription() 
			{
				return "Image (*.png, *.bmp)";
			}
       };
			fc.addChoosableFileFilter(ff);
 
			java.io.File f = null;
			if(open && fc.showOpenDialog(this) == fc.APPROVE_OPTION)
				f = fc.getSelectedFile();
			else if(!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION)
				f = fc.getSelectedFile();
			return f;
	}
	
	private void openImage() 
	{
		java.io.File f = showFileDialog(true);
		try 
		{   
			image = ImageIO.read(f);
			JLabel l = new JLabel(new ImageIcon(image));
			imagePane.getViewport().add(l);
			this.validate();
       } 
	   catch(Exception ex) {}
    }
	
	private void resetInterface() 
	{
		message.setText("");
		imagePane.getViewport().removeAll();
		image = null;
		this.validate();
    }
	
	
	private void decodeMessage() 
	{
		SteganoImgProcess sip = new SteganoImgProcess();
		String vk = sip.decode(image, image.getWidth(),image.getHeight());
		    message.setText(vk);

	}
	
	public static void main(String arg[]) {
    new DecodeMessage();
    }
 }