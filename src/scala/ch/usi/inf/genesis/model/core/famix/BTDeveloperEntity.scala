package ch.usi.inf.genesis.model.core.famix

case class BTDeveloperEntity() extends Entity{

}


object BTDeveloperEntityProperty extends Enumeration{
  type BTDeveloperEntityProperty = String

  val NAME = "name"
  val DISPLAY_NAME = "displayName"
  val E_MAIL = "email"
}