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

import org.biopax.paxtools.model.level3.Entity;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.pathvisio.model.PathwayElement;

public class DefaultXrefMapper implements XrefMapper {
	public void mapXref(Entity e, PathwayElement pwElm) {
		for(org.biopax.paxtools.model.level3.Xref xref : e.getXref()) {
			Xref gpmlXref = getDataNodeXref(xref);
			if(gpmlXref != null) {
				pwElm.setGeneID(gpmlXref.getId());
//				pwElm.setDataSource(gpmlXref.getDataSource());
				break; //Stop after first valid xref
			}
		}
	}
	
	Xref getDataNodeXref(org.biopax.paxtools.model.level3.Xref x) {
		String db = x.getDb();
		DataSource ds = DataSource.getByFullName(db);
		String id = x.getId();
		return new Xref(id, ds);
	}

}
