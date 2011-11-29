package ch.usi.inf.genesis.model.extractors

abstract class Analysis {
	//def write(db: DB) : Unit;
	def toJSON() : String;
}