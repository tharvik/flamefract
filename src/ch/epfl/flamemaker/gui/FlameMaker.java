package ch.epfl.flamemaker.gui;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Main class</br> Used to initiate the whole program, in particular the GUI.
 * 
 * @author Subri Guy-Laurent - 228357
 * @author Rousset Valérian - 229049
 */
public class FlameMaker {
	/**
	 * Launch the GUI via Swing
	 * 
	 * @param args
	 *                Not used
	 */
	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if (info.getName().equals("Nimbus")) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (final Exception e) {

				}

				new FlameMakerGUI().start();
			}
		});
	}

}
