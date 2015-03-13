// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//

package gpmltobiopax;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.pathvisio.debug.Logger;
import org.pathvisio.gui.swing.PvDesktop;
import org.pathvisio.gui.swing.dialogs.OkCancelDialog;
import org.pathvisio.plugin.Plugin;
import org.pathvisio.preferences.PreferenceManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * BiopaxExporter is the plugin for exporting a pathway from GPML
 * to BioPAX file
 * @author adem 
 */

public class BiopaxExporter implements Plugin {
	
	static PvDesktop desktop;
	
	public BiopaxExporter(){
	//public static void main(String[] args) {
		PreferenceManager.init();
		Logger.log.setLogLevel(true, true, true, true, true, true);
	}


	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		desktop.registerMenuAction ("Edit", biopaxExportAction);
	}
	
	private final BiopaxExportAction biopaxExportAction = new BiopaxExportAction();
	
	private class BiopaxExportAction extends AbstractAction
	{
		BiopaxExportAction()
		{
			putValue (NAME,"Export to BIOPAX ");
		}

		public void actionPerformed(ActionEvent arg0) 
		{
			try{
				BiopaxSaving bs = new BiopaxSaving();
				bs.setVisible(true);
			}
			catch (Exception e){
				Logger.log.error("Error in exporting to Biopax : "+e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * BiopaxSaving create the menu for choosing the BioPAX file to save
	 */
	private class BiopaxSaving extends OkCancelDialog{
		JTextArea searchText;
		
		BiopaxSaving(){
			super(null,"Save to BioPAX ",null,true);
			setDialogComponent(createDialogPane());
			setSize(500, 150);
		}
		
		protected Component createDialogPane() {
			FormLayout layout = new FormLayout (
		    		"pref, 4dlu, 150dlu, 4dlu, min",
		    		"40dlu");
		    JPanel panel = new JPanel(layout);
		    CellConstraints cc = new CellConstraints();	    
		    JLabel searchSource = new JLabel(" Save in ");
		    searchText = new JTextArea();
//		    searchText.setText("/home/adem/Desktop/biopax-level2/biopax-example-short-pathway.owl");
		    final JButton browseButton = new JButton(" Browse ");
		    final JFileChooser fc = new JFileChooser();
		    browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == browseButton) {
				        int returnVal = fc.showOpenDialog(BiopaxSaving.this);
				        if (returnVal == JFileChooser.APPROVE_OPTION) {
				        	searchText.removeAll();
				            File file = fc.getSelectedFile();       
				            searchText.append(file.toString());
				        } else {
							searchText.append("" );
				        }
				   }
				}
			});
		    panel.add(searchSource, cc.xy(1, 1));
		    panel.add(searchText, cc.xy(3, 1));
		    panel.add(browseButton, cc.xy(5, 1));     
		    return panel;
		}	
		
		protected void okPressed(){
			try{
				ToBiopax tb = new ToBiopax(searchText.getText());
			}
			catch (Exception e){
				e.printStackTrace();
			}
			super.okPressed();
		}
	}
	
}

