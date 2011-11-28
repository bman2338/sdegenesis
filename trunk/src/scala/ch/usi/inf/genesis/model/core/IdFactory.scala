package ch.usi.inf.genesis.model.core

object IdFactory {
	private var currentId : Int = -1
	def nextId() : Int =  { currentId += 1; return currentId }
	def reset() : Unit = currentId = -1
	def getCurrentId() : Int = currentId
}