package util
import anorm._
import java.io.BufferedReader
import java.io.InputStreamReader

object Db { 
  
  /**
   * Returns true if name normalizes to a valid string
   */
  def isValidName(name:String):Boolean = {
    normalizeName(name).length() > 0
  }
  
  /**
   * Makes name lower case, replaces invalid characters with an underscore
   */
  def normalizeName(name:String):String = {
    name.trim().replaceAll("[^A-Za-z0-9_.]+", "_").toLowerCase()
  }
  
  def scopeIdentity()(implicit c: java.sql.Connection): Long = {
    val query = SQL("SELECT LastVal()")
    query().map( row => row[Long]("LastVal") ).head
  }
  
  def uploadsPath():String = {
    "./uploads/products/"
  }
  
  def defaultProductIconPath():String = {
    "./public/images/default-product-icon.png"
  }
  
  def debug(input:String) :String = {
		var proc = Runtime.getRuntime().exec(input);
		var stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		var stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        
		var output = "";
                                 
        var exitVal = proc.waitFor();
        
        var stdIn = stdInput.readLine();
        while(stdIn != null){
            output += stdIn + "\n";
            stdIn = stdInput.readLine();
        }
        
        var stdErr = stdError.readLine();
        while(stdErr != null){
            output += stdErr + "\n";
            stdIn = stdError.readLine();
        }

        output += "\n\nExitValue: " + exitVal; 

		return output.trim();
	}
}
