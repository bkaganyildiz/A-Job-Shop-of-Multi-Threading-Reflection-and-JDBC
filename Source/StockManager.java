
import java.util.logging.Level;
import java.util.logging.Logger;

//***************************************************************************************************************************************************

public class StockManager extends Employee
{
  //=================================================================================================================================================

  private int numberOfPartsSupplied ;
  //=================================================================================================================================================

  public StockManager ( String name , JobShop jobShop )
  {
    super( name , jobShop ) ;

    title                 = "Stock Manager " ;
    numberOfPartsSupplied = 0                ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
    // ...
      while(jobShop.isOpen) {
          talk( "%s %s : Checking for a reported missing part" , title , name ) ;
          String mp  = jobShop.getNextMissingPart() ; 
          if(mp==null) {
              talk( "%s %s : There are no reported missing parts, so I'm waiting" , title , name ) ;
              synchronized(jobShop.missingParts) {
                  while(true) {
                   try {
                       jobShop.missingParts.wait(); // After wait look for workingOrder like nothing happened .
                   } catch (InterruptedException e) {
                       continue;
                   }
                   break ;
               }
              } 
          }
          else {
              talk( "%s %s : Ordering part %s" , title , name , mp ) ;
              try {//sleep at 
                  jobShop.db.incrementPartCount(mp);
              } catch (Exception ex) {
                  //Logger.getLogger(StockManager.class.getName()).log(Level.SEVERE, null, ex);
              }
              talk( "%s %s : Part %s supplied" , title , name , mp ) ;
              this.numberOfPartsSupplied += 1 ;
              synchronized(mp) {
                mp.notifyAll();
              }
              
          }
          
      }
      talk( "%s %s : Restocked a total of %d parts" , title , name , this.numberOfPartsSupplied ) ;
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

