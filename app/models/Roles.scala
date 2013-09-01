package models

object Roles { 
  val Client = 
    Permission.ViewProducts

  val Sales = 
    Permission.ViewProducts + 
    Permission.EditProducts + 
    Permission.ViewLedger

  val Accounting = 
    Permission.ViewProducts + 
    Permission.ViewLedger + 
    Permission.EditLedger

  val Admin = 
    Permission.values

}
