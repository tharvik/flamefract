package ch.epfl.flamemaker.gui;

/**
 * Main class</br>
 * Used to initiate the whole program, in
 * particular the GUI.
 */
public class FlameMaker {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FlameMakerGUI().start();
			}
		});
	}

}
