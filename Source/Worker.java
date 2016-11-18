
import java.util.logging.Level;
import java.util.logging.Logger;



public class Worker extends Employee
{
  //=================================================================================================================================================

  private int numberOfPartsAssembled ;

  //=================================================================================================================================================

  public Worker ( String name , JobShop jobShop )
  {
    super( name , jobShop ) ;

    title                  = "Worker        " ;
    numberOfPartsAssembled = 0                ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
    // ...
      while(jobShop.isOpen){
         talk( "%s %s : Checking for a working order" , title , name ) ; 
         Order wo = jobShop.getNextWorkingOrder()           ;
         if(wo==null) { // If there is no working order send working thread to wait
             talk( "%s %s : There are no working orders, so I'm waiting" , title , name ) ;
             synchronized(jobShop.workingOrders) {   
               while(true) {
                   try {
                       jobShop.workingOrders.wait(); // After wait look for workingOrder like nothing happened .
                   } catch (InterruptedException e) {
                       continue;
                   }
                   break ;
               }
              }
         }
         else {
             talk( "%s %s : Currently working on order %s" , title , name , wo.toString() ) ;
             while(!wo.isCompleted()){
                 try {
                     if(jobShop.db.checkInventory(wo.nextRemainingPart())){
                         jobShop.addMissingPart(wo.nextRemainingPart());
                         synchronized(jobShop.missingParts) {
                             jobShop.missingParts.notify();
                        }
                        synchronized(wo.nextRemainingPart()) {
                            while(true) {
                               try {
                                   wo.nextRemainingPart().wait(); // After wait look for productRequest like nothing happened .
                                } catch (InterruptedException e) {
                                    continue;
                                }
                               break ;
                             }
                         }
                     }
                     else {
                        jobShop.db.decrementPartCount(wo.nextRemainingPart());
                        wo.completeNextRemainingPart();
                        talk( "%s %s : Assembled next part of order %s" , title , name , wo.toString() ) ; 
                        this.numberOfPartsAssembled++; 
                     }
                     
                 } catch (Exception ex) {
                     //Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 
             }
             synchronized(wo) {
                     wo.notify();
             }            
         }
         
      }
      talk( "%s %s : Assembled a total of %d parts" , title , name , this.numberOfPartsAssembled ) ; 
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

