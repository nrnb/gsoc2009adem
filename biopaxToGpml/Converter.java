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

package org.pathvisio.biopax3;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.pathvisio.debug.Logger;
import org.pathvisio.gui.swing.PvDesktop;
import org.pathvisio.gui.swing.dialogs.OkCancelDialog;
import org.pathvisio.model.Pathway;
import org.pathvisio.plugin.Plugin;
import org.pathvisio.preferences.PreferenceManager;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A main class for the BioPAX converter.
 * Will call the converter for the BioPAX file
 * provided in the first command line argument and
 * convert the pathway entities to GPML pathways.
 * The resulting pathways will be saved as a GPML file in 
 * the working directory.
 * @author adem
 */
public class Converter implements Plugin {
	
	BiopaxFormat3 bpf;
	static PvDesktop desktop;
	
	public Converter(){
		PreferenceManager.init();
		Logger.log.setLogLevel(true, true, true, true, true, true);
	}

	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		desktop.registerMenuAction ("Edit", biopax_action2);
	}
	
	private final BiopaxAction biopax_action2 = new BiopaxAction();
	
	private class BiopaxAction extends AbstractAction
	{
		BiopaxAction()
		{
			putValue (NAME,"Import from BIOPAX 3 ");
		}

		public void actionPerformed(ActionEvent arg0) 
		{
			BiopaxLoading bl = new BiopaxLoading();
			bl.setVisible(true);
		}
	}
	
	private class BiopaxLoading extends OkCancelDialog{
		JTextArea searchText;
		List<org.biopax.paxtools.model.level3.Pathway> pathway_list = 
			new ArrayList<org.biopax.paxtools.model.level3.Pathway>();
		
		BiopaxLoading(){
			super(null,"Biopax Loader",null,true);
			setDialogComponent(createDialogPane());
			setSize(500, 150);
		}
		
		protected Component createDialogPane() {
			FormLayout layout = new FormLayout (
		    		"pref, 4dlu, 150dlu, 4dlu, min",
		    		"40dlu");
		    JPanel panel = new JPanel(layout);
		    CellConstraints cc = new CellConstraints();	    
		    JLabel searchSource = new JLabel("File to load ");
		    searchText = new JTextArea();
//		    searchText.setText("/home/adem/Desktop/biopax-level2/biopax-example-short-pathway.owl");
		    final JButton browseButton = new JButton(" Browse ");
		    final JFileChooser fc = new JFileChooser();
		    browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == browseButton) {
				        int returnVal = fc.showOpenDialog(BiopaxLoading.this);
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
		
		// the pathway is just open in the memory
		// there is no creation of a gpml file after conversion
		// The user can open/edit the pathway and then save it to gpml format
		protected void okPressed(){
			try {
				String owl = searchText.getText();
				File inFile = new File(owl);
				bpf = new BiopaxFormat3(inFile);
				int i=0;
				
				pathway_list = bpf.getListPathway();
//				File f = new File(owl);
				System.out.println(" Opening biopax ");
				System.out.println("size : "+pathway_list.size());
				
				if (pathway_list.size()==1){
					for(Pathway p : bpf.convert()) {	
						
						//bpf.convert();
						File f = p.getSourceFile();
						if(f == null) {
							//f = new File(inFile.getName() + "-" + ++i + ".gpml");
							f = File.createTempFile(inFile.getName() + "-" + ++i ,".gpml");
							f.deleteOnExit();
						}
						p.writeToXml(f, true);
						//GpmlFormat.writeToXml(p, System.out , true);
						desktop.getSwingEngine().openPathway(f);
					}
					i++;
				}
				else if (pathway_list.size()>1){
					System.out.println("liste "+pathway_list);
					super.okPressed();
					SelectPathwayDialog selectP = new SelectPathwayDialog(pathway_list,owl,desktop);
					selectP.setVisible(true);
				}
//				i++;
				
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			super.okPressed();
		}
	}
}
