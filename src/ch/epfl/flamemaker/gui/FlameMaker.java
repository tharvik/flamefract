package ch.epfl.flamemaker.gui;

/**
 * Main class</br> Used to initiate the whole program, in particular the GUI.
 */
public class FlameMaker {
	/**
	 * Launch the GUI via Swing
	 * 
	 * @param args
	 *                Not used
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FlameMakerGUI().start();
			}
		});
	}

}
