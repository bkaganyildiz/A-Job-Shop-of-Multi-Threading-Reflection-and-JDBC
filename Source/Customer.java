//***************************************************************************************************************************************************

import java.lang.reflect.InvocationTargetException;
import java.util.Random ;
import java.util.logging.Level;
import java.util.logging.Logger;

//***************************************************************************************************************************************************




//***************************************************************************************************************************************************

public class Customer extends Thread implements Person
{
  //=================================================================================================================================================

  public        String  title                   ;
  public        String  name                    ;
  public        JobShop jobShop                 ;

  //-------------------------------------------------------------------------------------------------------------------------------------------------

  private       int     numberOfProductRequests ;
  private final Random  random                  ;
  private String partNames ; 

  //=================================================================================================================================================

  public Customer ( String name , JobShop jobShop )
  {
    this.title                   = "Customer      " ;
    this.name                    = name             ;
    this.jobShop                 = jobShop          ;
    this.numberOfProductRequests = 0                ;
    this.random                  = new Random()     ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  @Override
  public void talk ( String format , Object ... args )  // This is a synchronized wrapper for printf method
  {
    synchronized ( System.out )  { System.out.printf( format + "%n" , args ) ;  System.out.flush() ; }
  }

  //=================================================================================================================================================

  @Override
  public void spendTime ( int minMilliseconds , int maxMilliseconds )  // This is a wrapper for Thread.sleep
  {
    int duration = minMilliseconds + (int) ( Math.random() * ( maxMilliseconds - minMilliseconds ) ) ;

    try { Thread.sleep( duration ) ; } catch ( InterruptedException ex ) { /* Do nothing */ }
  }

  //=================================================================================================================================================

  
  private Part generateRandomPart ()
  {
    // ..
      int ranNum = random.nextInt(partNames.length()) ; 
      String className = "Part"+partNames.charAt(ranNum); 
      
      Class tempClass = null ;
      try {
                // create Class object from a class name
                tempClass = Class.forName(className);
        }
        catch( ClassNotFoundException e )
        {
                System.out.println( "Unable to find the class" );
                System.exit( 1 );
        }
       
      Part returner ; 
      try {
          returner = (Part) tempClass.getConstructor().newInstance();
          return returner ; 
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
          //Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchMethodException | SecurityException ex) {
          //Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
      }
      //return sb.toString();
      return null;
      //return sb.toString();
  }
  //=================================================================================================================================================

  private Part [] generateRandomProductRequest ()
  {
    int     numberOfParts = 2 + random.nextInt( 4 )   ;
    Part [] parts         = new Part[ numberOfParts ] ;

    for ( int i = 0 ; i < numberOfParts ; i++ )  { parts[ i ] = generateRandomPart() ; }
    return parts ;
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
    // ...
      partNames  = jobShop.db.partNames();
      while(jobShop.isOpen) {
            //if(random.nextInt(10)==1) {
               Part [] reqWaiter = generateRandomProductRequest();
            jobShop.addProductRequest(reqWaiter);
            talk( "%s %s : Submitting a product request of %d parts" , title , name,reqWaiter.length ) ;
            this.numberOfProductRequests ++ ; 
            synchronized(jobShop.productRequests) {
                jobShop.productRequests.notify(); 

            } 
            synchronized(reqWaiter) {
               while(true) {
                       try {
                           reqWaiter.wait(); // After wait look for productRequest like nothing happened .
                       } catch (InterruptedException e) {
                           continue;
                       }
                       break ;
                    }
           }
        //}
        
       
        
         
        spendTime(500, 600);
        
      }
        
      talk( "%s %s : Submitted a total of %d product requests to the jobshop" , title , name,this.numberOfProductRequests) ;
    
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

