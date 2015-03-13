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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.biopax.paxtools.impl.level3.Level3FactoryImpl;
import org.biopax.paxtools.io.jena.JenaIOHandler;
import org.biopax.paxtools.io.simpleIO.SimpleExporter;
import org.biopax.paxtools.model.BioPAXFactory;
import org.biopax.paxtools.model.BioPAXLevel;
import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.BiochemicalPathwayStep;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.Control;
import org.biopax.paxtools.model.level3.Conversion;
import org.biopax.paxtools.model.level3.Level3Factory;
import org.biopax.paxtools.model.level3.PathwayStep;
import org.biopax.paxtools.model.level3.PhysicalEntity;
import org.biopax.paxtools.model.level3.Process;
import org.biopax.paxtools.model.level3.Protein;
import org.biopax.paxtools.model.level3.Rna;
import org.biopax.paxtools.model.level3.SmallMolecule;
import org.biopax.paxtools.model.level3.SmallMoleculeReference;
import org.pathvisio.debug.Logger;
import org.pathvisio.model.LineType;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.model.GraphLink.GraphRefContainer;
import org.pathvisio.model.PathwayElement.Comment;
import org.pathvisio.model.PathwayElement.MAnchor;
import org.pathvisio.model.PathwayElement.MPoint;

/**
 * ToBiopax class define the conversion for each PathwayElement
 * to BioPAX element
 * @author adem
 */
public class ToBiopax {
	/**
	 * Field that contains all elements participating on the
	 * left (start) side of this interaction
	 */
	public static final String FIELD_LEFT = "left";
	/**
	 * Field that contains all elements participating on the
	 * right (end) side of this interaction
	 */
	public static final String FIELD_RIGHT = "right";
	/**
	 * Field that contains all elements participating as
	 * mediator of this interaction
	 */
	public static final String FIELD_MEDIATOR = "mediator";
	
	Level3Factory factory = new Level3FactoryImpl();
	Model bpModel;
	BioPAXFactory bpFactory;
	JenaIOHandler ioh = new JenaIOHandler(factory, BioPAXLevel.L3);
	Pathway pv = BiopaxExporter.desktop.getSwingEngine().getEngine().getActivePathway();
	String elem = pv.getUniqueGraphId();
	int nb_elem = 1;
	
	
	public ToBiopax(String file) throws IOException, IllegalAccessException, InvocationTargetException{
		
		System.out.println(" Saving from GPML to Biopax");

		SimpleExporter exporter = new SimpleExporter(BioPAXLevel.L3);

		Map<PathwayElement,PhysicalEntity> list_elem =new HashMap<PathwayElement,PhysicalEntity>();
		
		bpModel = factory.createModel();
		Map<String,PathwayElement> gpml_elements = new HashMap<String,PathwayElement>(); 
		List<String> anchor_list = new ArrayList<String>();  		
		PhysicalEntity pep;
		
		org.biopax.paxtools.model.level3.Pathway pw = factory.createPathway();
		pw.setRDFId("new pathway");
		bpModel.add(pw);
		
		BiochemicalPathwayStep bps = factory.createBiochemicalPathwayStep();
		bps.setRDFId("BPS");
		bpModel.add(bps);
		
		for (PathwayElement pwElm : pv.getDataObjects()){
			
			System.out.println("element : "+elem+nb_elem);
			pep = factory.createPhysicalEntity();
//			pep.setRDFId(elem+nb_elem+elem);
//			System.out.println("type : "+pwElm.getDataNodeType());
			if (pwElm.getObjectType() == ObjectType.DATANODE){
				if (((pwElm.getDataNodeType()).equals("Metabolite"))
						|| (pwElm.getDataNodeType()).equals("GeneProduct")
						|| (pwElm.getDataNodeType()).equals("Unknown")){
					SmallMolecule sm = factory.createSmallMolecule();
					pep.setRDFId(pwElm.getGraphId());
//					pep.setStandardName(pwElm.getTextLabel());
					pep.setDisplayName(pwElm.getTextLabel());
					Set comments = new HashSet();
					for (Comment com : pwElm.getComments()){
						comments.add(com.toString());
					}
					pep.setComment(comments);
					
//					pep.addMemberPhysicalEntity(sm);
//					list_elem.put(pwElm, sm);
//					bpModel.add(pep);
					bpModel.add(pep);
					nb_elem ++;
				}
				else if ((pwElm.getDataNodeType()).equals("Protein")){
					Protein prot = factory.createProtein();
					//prot.setRDFId(elem+nb_elem);
					prot.setRDFId(pwElm.getGraphId());
					prot.setDisplayName(pwElm.getTextLabel());
					Set comments = new HashSet();
					for (Comment com : pwElm.getComments()){
						comments.add(com.toString());
					}
					prot.setComment(comments);
//					pep.addMemberPhysicalEntity(prot);
//					list_elem.put(pwElm, prot);
					bpModel.add(prot);
					nb_elem ++;
				}
				else if ((pwElm.getDataNodeType()).equals("Rna")){
					Rna rn = factory.createRna();	
//					rn.setRDFId(elem+nb_elem);
					rn.setRDFId(pwElm.getGraphId());
					rn.setDisplayName(pwElm.getTextLabel());
					Set comments = new HashSet();
					for (Comment com : pwElm.getComments()){
						comments.add(com.toString());
					}
					rn.setComment(comments);
//					pep.addMemberPhysicalEntity(rn);
//					list_elem.put(pwElm, rn);
					bpModel.add(rn);
					nb_elem ++;
				}
				
				else if ((pwElm.getDataNodeType()).equals("Complex")){
					Complex comp = factory.createComplex();	
//					comp.setRDFId(elem+nb_elem);
					comp.setRDFId(pwElm.getGraphId());
					comp.setDisplayName(pwElm.getTextLabel());
					Set comments = new HashSet();
					for (Comment com : pwElm.getComments()){
						comments.add(com.toString());
					}
					comp.setComment(comments);
					
//					pep.addMemberPhysicalEntity(comp);
//					list_elem.put(pwElm, comp);
					bpModel.add(comp);
					nb_elem ++;
				}
				list_elem.put(pwElm, pep);
				
			}
			
			
			if(isRelation(pwElm)) {
				
				Relation r = new Relation(pwElm);
				System.out.println("Relation avec lefts : "+r.getLefts()+" rights : "+r.getRights()
						+" mediators : "+r.getMediators());
				
				Set<PhysicalEntity> leftPep = new HashSet<PhysicalEntity>() ;
				Set<PhysicalEntity> rightPep = new HashSet<PhysicalEntity>() ;
				Set<PhysicalEntity> mediatorPep = new HashSet<PhysicalEntity>() ;
				
				
				for(PathwayElement elem : r.getLefts()){
					System.out.println("lefts : "+elem.getTextLabel()+" GRID : "+elem.getGraphId());
					if (elem.getObjectType()==ObjectType.DATANODE){
						PhysicalEntity pepp = factory.createPhysicalEntity();
						pepp.setRDFId(elem.getGraphId());
						pepp.setStandardName(elem.getTextLabel());
						leftPep.add(pepp);
					}
				}
				
				for(PathwayElement elem : r.getRights()){
					System.out.println("rights : "+elem.getTextLabel()+" GRID : "+elem.getGraphId());
					if (elem.getObjectType()==ObjectType.DATANODE){
						PhysicalEntity pepp = factory.createPhysicalEntity();
						pepp.setRDFId(elem.getGraphId());
						pepp.setStandardName(elem.getTextLabel());
						rightPep.add(pepp);
					}
				}
				
				for(PathwayElement elem : r.getMediators()){
					System.out.println("mediators : "+elem.getTextLabel()+" GRID : "+elem.getGraphId());
					if (elem.getObjectType()==ObjectType.DATANODE){
						PhysicalEntity pepp = factory.createPhysicalEntity();
						pepp.setRDFId(elem.getGraphId());
						pepp.setStandardName(elem.getTextLabel());
						mediatorPep.add(pepp);
					}
				}
				
				Set<PathwayStep> setPathwayStep = new HashSet<PathwayStep>();
				Set<Process> setProcess = new HashSet<Process>();
				
				if (r.getMediators().size()>0) {
					Conversion convert = factory.createConversion();
					String from = null;
					String to = null;
					for (PathwayElement fr : r.getLefts()){
						if (fr.getObjectType()==ObjectType.DATANODE)
							from = fr.getTextLabel();
						else
							from = fr.getGraphId();
					}
					for (PathwayElement fr : r.getRights()){
						if (fr.getObjectType()==ObjectType.DATANODE)
							to = fr.getTextLabel();
						else
							to = fr.getGraphId();
					}
					convert.setRDFId("ConversionOf "+elem+nb_elem);
					convert.setDisplayName("ConversionOf "+from+" To "+to);
					convert.setLeft(leftPep);
					convert.setRight(rightPep);
					bpModel.add(convert);
//					setProcess.add(convert);
					Control cont= factory.createControl();
					cont.setRDFId("Control"+nb_elem);
					cont.setController(mediatorPep);
					cont.addControlled(convert);
//					PathwayStep pt = factory.createPathwayStep();
//					pt.setStepProcess(setProcess);
//					setPathwayStep.add(pt);
					setProcess.add(cont);
					bpModel.add(cont);
					nb_elem ++;
					bps.addStepProcess(cont);
					pw.addPathwayComponent(cont);
				}
				else {
					Conversion convert = factory.createConversion();
					String from = null;
					String to = null;
					for (PathwayElement fr : r.getLefts()){
						if (fr.getObjectType()==ObjectType.DATANODE)
							from = fr.getTextLabel();
						else
							from = fr.getGraphId();
					}
					for (PathwayElement fr : r.getRights()){
						if (fr.getObjectType()==ObjectType.DATANODE)
							to = fr.getTextLabel();
						else
							to = fr.getGraphId();
					}
					convert.setRDFId("ConversionOf "+elem+nb_elem);
					convert.setDisplayName("ConversionOf "+from+" To "+to);
					convert.setLeft(leftPep);
					convert.setRight(rightPep);
					setProcess.add(convert);
					bpModel.add(convert);
					nb_elem ++;
					pw.addPathwayComponent(convert);
					pw.addPathwayOrder(bps);
					bps.addStepProcess(convert);
				}
//				pw.setPathwayComponent(setProcess);
//				pw.setPathwayOrder(setPathwayStep);
				
			}  
			
			if (pwElm.getObjectType() == ObjectType.LINE){ 
				if((pwElm.getStartGraphRef()==null)||(pwElm.getEndGraphRef()==null)){
					Logger.log.info("This pathway contains an incorrect arrow");
				}
			}
		}
		 
		exporter.convertToOWL(bpModel, 
				new BufferedOutputStream(new FileOutputStream(new File(file))));
	}
	
	boolean isRelation(PathwayElement pe) {
		if(pe.getObjectType() == ObjectType.LINE) {
			System.out.println(" LINE ");
			MPoint s = pe.getMStart();
			MPoint e = pe.getMEnd();
			if(s.isLinked() && e.isLinked()) {
				//Objects behind graphrefs should be PathwayElement
				//so not MAnchor
				if(pv.getElementById(s.getGraphRef()) != null &&
						pv.getElementById(e.getGraphRef()) != null)
				{
					return true;
				}
			}
		}
		return false;
	}


	static class Relation {
		private Set<PathwayElement> lefts = new HashSet<PathwayElement>();
		private Set<PathwayElement> rights = new HashSet<PathwayElement>();
		private Set<PathwayElement> mediators = new HashSet<PathwayElement>();
		
		public Relation(PathwayElement relationLine) {
			if(relationLine.getObjectType() != ObjectType.LINE) {
				throw new IllegalArgumentException("Object type should be line!");
			}
			Pathway pathway = relationLine.getParent();
			if(pathway == null) {
				throw new IllegalArgumentException("Object has no parent pathway");
			}
			//Add obvious left and right
			addLeft(pathway.getElementById(
					relationLine.getMStart().getGraphRef()
			));
			addRight(pathway.getElementById(
					relationLine.getMEnd().getGraphRef()
			));
			//Find all connecting lines (via anchors)
			for(MAnchor ma : relationLine.getMAnchors()) {
				for(GraphRefContainer grc : ma.getReferences()) {
					if(grc instanceof MPoint) {
						MPoint mp = (MPoint)grc;
						PathwayElement line = mp.getParent();
						if(line.getMStart() == mp) {
							//Start linked to anchor, make it a 'right'
							if(line.getMEnd().isLinked()) {
								addRight(pathway.getElementById(line.getMEnd().getGraphRef()));
							}
						} else {
							//End linked to anchor
							if(line.getEndLineType() == LineType.LINE) {
								//Add as 'left'
								addLeft(pathway.getElementById(line.getMStart().getGraphRef()));
							} else {
								//Add as 'mediator'
								addMediator(pathway.getElementById(line.getMStart().getGraphRef()));
							}
						}
					} else {
						Logger.log.warn("unsupported GraphRefContainer: " + grc);
					}
				}
			}
		}
		
		void addLeft(PathwayElement pwe) {
			addElement(pwe, lefts);
		}
		
		void addRight(PathwayElement pwe) {
			addElement(pwe, rights);
		}
		
		void addMediator(PathwayElement pwe) {
			addElement(pwe, mediators);
		}
		
		void addElement(PathwayElement pwe, Set<PathwayElement> set) {
			if(pwe != null) {
				//If it's a group, add all subelements
				if(pwe.getObjectType() == ObjectType.GROUP) {
					for(PathwayElement ge : pwe.getParent().getGroupElements(pwe.getGroupId())) {
						addElement(ge, set);
					}
				}
				set.add(pwe);
			}
		}
		
		Set<PathwayElement> getLefts() { return lefts; }
		Set<PathwayElement> getRights() { return rights; }
		Set<PathwayElement> getMediators() { return mediators; }
	}
	
}