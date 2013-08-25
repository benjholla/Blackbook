package models.traits

trait Enableable {
  def isEnabled(): Boolean
  def setEnabled(enabled: Boolean)
}
