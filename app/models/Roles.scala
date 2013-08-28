package models

object Roles { 
  val Client = 
    Permission.ViewProducts

  val Sales = 
    Permission.ViewProducts + 
    Permission.EditProducts + 
    Permission.ViewLedger

  val Accounting = 
    Permission.ViewLedger + 
    Permission.EditLedger

  val Admin = 
    Permission.ViewUsers + 
    Permission.EditUsers

  val Owner = 
    Permission.values

}
