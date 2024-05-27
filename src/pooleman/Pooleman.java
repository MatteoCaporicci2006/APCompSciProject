package pooleman;

import javax.swing.JFrame;

public class Pooleman extends JFrame{

	public Pooleman() {
		add(new Game());
	}
	
	public static void main(String[] args) {
		Pooleman pac = new Pooleman();
		pac.setVisible(true);
		pac.setSize(450,450);
		pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pac.setLocationRelativeTo(null);
		
	}

}