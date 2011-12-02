package ch.usi.inf.genesis.model.extractors
import ch.usi.inf.genesis.model.extractors._
import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.famix.MethodEntity
import ch.usi.inf.genesis.model.navigation.NavigatorOption._


object InvocationExtractorFactory {

	def getSimpleInvocationExtractor() : Extractor = {

			//select only method entities
			val selection = (obj: ModelObject) => {
						obj match {
						case MethodEntity() => true;
						case _ => false;
						} };
			
						
			
			val addNodeAux = (m: ModelObject, h: HierarchyAnalysis) => {
			  var name = m.getName();
			  name match {
			    case "" =>
			    case _ => {
			      val prop = m.getProperties(INVOKINGMETHODS_PROP);
			       prop match {
			         case Some(p) =>  h.nodes.put(m.getId(), m);
			         case None => {
			        	 val invocation = m.getProperties(SEND_INVOCATIONS_PROP);
			        	 invocation match {
			        	 	case Some(invocation) => { 
			        	 		invocation.foreach(inv => {
			        	 		  val invokedList = inv.getProperties(CANDIDATES);
			        	 		  invokedList match {	
			        	 		    case None =>
			        	 		    case Some(invokedList) =>
			        	 		      invokedList.foreach(invoked => {
			        	 		        if(invoked.getName() != "") {
			        	 		        	m.addProperty(INVOKINGMETHODS_PROP, invoked);
			        	 		        }
			        	 		      });
			        	 		  }
			        	 		});
			          
			        	 		h.nodes.put(m.getId(), m);
			        	 	}
			        	 	case None =>
			        	 }
			        }}}}
			  SKIP_SUBTREE
			};
			
			
			
			var analysisFactory = () => { new HierarchyAnalysis(INVOKINGMETHODS_PROP, Some(addNodeAux)); };	
			
			val extractor =  new HierarchyExtractor(INVOKINGMETHODS_PROP, selection, Some(analysisFactory));

			return extractor;
	}

}