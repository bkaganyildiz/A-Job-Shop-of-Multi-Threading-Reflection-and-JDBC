
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
//***************************************************************************************************************************************************


//***************************************************************************************************************************************************




//***************************************************************************************************************************************************

public class DBServices
{
  //=================================================================================================================================================

  public  String     driver     ;
  public  String     url        ;
  public  String     database   ;
  public  String     username   ;
  public  String     password   ;

  //-------------------------------------------------------------------------------------------------------------------------------------------------

  private Connection connection ;
  private Statement  statement  ;
  private PreparedStatement ps ;
  //=================================================================================================================================================

  public DBServices () throws Exception
  {
    driver   = "com.mysql.jdbc.Driver"        ;
    url      = "jdbc:mysql://localhost:3306/" ;
    database = "CENG443"                      ;
    username = "root"                         ;
    password = ""                         ;

    Class.forName( driver ) ;

    connection = DriverManager.getConnection( url + database , username , password ) ;
    statement  = (Statement) connection.createStatement()                                        ;
  }

  //=================================================================================================================================================

  /* Suggested */public synchronized List< Pair< String , Integer > > getInventory () throws Exception
  {
    // ...
      List< Pair< String , Integer > > returner = new ArrayList<>(); 
      try {
        ResultSet rs = statement.executeQuery("select * from inventory");
        while(rs.next())
        {
            Pair< String , Integer > temp = new Pair<>(rs.getString("Part"),rs.getInt("Count")); 
            returner.add(temp) ; 
        }
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return returner ; 
      
  }

  public synchronized String  partNames() {
      String returner = "" ;
      try {
          ResultSet rs = statement.executeQuery("select Part from inventory");
          while(rs.next()) {
             returner += rs.getString("Part")  ; 
          }
      } catch (SQLException ex) {
          //Logger.getLogger(DBServices.class.getName()).log(Level.SEVERE, null, ex);
      }
      return returner ; 
  }
  //=================================================================================================================================================

  /* Suggested */ public synchronized void  setPartCount ( String partName , int partCount ) throws Exception
  {
    // ...
      ResultSet rs = statement.executeQuery("SELECT * from inventory WHERE Part ='"+partName+"'") ; 
      String update ; 
      if(rs.next())
        update = "UPDATE inventory "+ "SET Count = Count+1 WHERE Part='" + partName +"'";
      else
        update = "INSERT INTO inventory (Part,Count) VALUES ('"+partName+"','"+partCount+"')" ; 
      
      try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
      
  }

  //=================================================================================================================================================

  /* Suggested */ public synchronized void incrementPartCount ( String partName ) throws Exception
  {
    // ...
      ResultSet rs = statement.executeQuery("SELECT * from inventory WHERE Part ='"+partName+"'") ; 
      String update ; 
      if(rs.next())
        update = "UPDATE inventory "+ "SET Count = Count+1 WHERE Part='" + partName +"'";
      else
        update = "INSERT INTO inventory (Part,Count) VALUES ('"+partName+"','"+1+"')" ; 
      
      try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
      
  }

  //=================================================================================================================================================

  /* Suggested */public synchronized void decrementPartCount ( String partName ) throws Exception
  {
    // ...
       String update = "UPDATE inventory "
              + "SET Count = Count-1 WHERE Part='" + partName +"'";
      try {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
  }
  public synchronized boolean checkInventory(String partName) {
      String getter = "SELECT Count FROM inventory WHERE Part='" + partName +"'"  ;
      ResultSet rs = null ; 
      try { 
          rs = statement.executeQuery(getter) ;
          rs.next();
          return (0>=rs.getInt("Count")) ;
      } catch (SQLException ex) {
          //Logger.getLogger(DBServices.class.getName()).log(Level.SEVERE, null, ex);
      }
      return true ; 
  }

  //=================================================================================================================================================

  public void close () throws Exception
  {
    if ( statement  != null )  { statement .close() ; }
    if ( connection != null )  { connection.close() ; }
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

