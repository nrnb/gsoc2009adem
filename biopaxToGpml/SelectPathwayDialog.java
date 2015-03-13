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

package org.pathvisio.biopax3;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jdom.JDOMException;
import org.pathvisio.gui.swing.PvDesktop;
import org.pathvisio.gui.swing.dialogs.OkCancelDialog;
import org.pathvisio.model.ConverterException;

/**
 * this class is needed if there is more than 1 pathway in a BioPAX file
 * PathVisio is able to open just 1 pathway at the time so the user
 * has to choose the pathway to open
 * @author adem
 */
public class SelectPathwayDialog extends OkCancelDialog {
	JComboBox pathway_choose;
	String this_owl ;
	List<org.biopax.paxtools.model.level3.Pathway> this_pat;
	static PvDesktop this_desktop;
	
	public SelectPathwayDialog(List<org.biopax.paxtools.model.level3.Pathway> pathway_list, String owl,PvDesktop desktop){
		super(null," Open Pathway ",null,false);
		this.this_owl = owl ;
		this.this_desktop = desktop;
		this.this_pat = pathway_list;
		setDialogComponent(createDialogPane(pathway_list));
		setSize(500, 120);
	}
	
	protected Component createDialogPane(List<org.biopax.paxtools.model.level3.Pathway> pathway_list) {
		String [] pathways = new String [pathway_list.size()]; 
		JPanel panel = new JPanel();	    
		int i=0;
	    for (org.biopax.paxtools.model.level3.Pathway pat: pathway_list){
	    	pathways[i] = pat.getDisplayName();
	    	i++;
	    }
	    pathway_choose = new JComboBox(pathways);
	    panel.add(pathway_choose);
	    return panel;
	}	
	
	protected void okPressed(){
		try{
			File inFile = new File(this_owl);
			BiopaxFormat3 bf = new BiopaxFormat3(inFile);
			bf.simple_convert(this_pat.get(pathway_choose.getSelectedIndex()));
			File f = File.createTempFile(pathway_choose.getSelectedItem() + "-" ,".gpml");
			bf.simple_convert(this_pat.get(pathway_choose.getSelectedIndex())).writeToXml(f, true);
			this_desktop.getSwingEngine().openPathway(f);
		}
		catch (IOException e){
			
		}
		catch (JDOMException jd){}
		catch (ConverterException ce){}
		super.okPressed();
	}
}
