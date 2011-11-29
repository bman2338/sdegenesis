package ch.usi.inf.genesis.model.extractors
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.ModelVisitor

abstract class Extractor extends ModelVisitor {
	def extract(model: ModelObject): Analysis;
}