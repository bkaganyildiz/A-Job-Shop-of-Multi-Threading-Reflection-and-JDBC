
import java.lang.reflect.*;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

//***************************************************************************************************************************************************

public class CustomerRepresentative extends Employee
{
  //=================================================================================================================================================

  private int numberOfProductRequests ;

  //=================================================================================================================================================

  public CustomerRepresentative ( String name , JobShop jobShop )
  {
    super( name , jobShop ) ;
    title                   = "Representative" ;
    numberOfProductRequests = 0                ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  private String identifyPartName ( Part part ) 
  {
    // ...
      Class c  = part.getClass() ; 
      try {
          Field name = c.getDeclaredField("name");
          name.setAccessible(true);
          if(name.getType().equals(String.class))  
             return (String) name.get(part) ;
      } catch (NoSuchFieldException | SecurityException ex) {
          //Logger.getLogger(CustomerRepresentative.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalArgumentException | IllegalAccessException ex) {
          //Logger.getLogger(CustomerRepresentative.class.getName()).log(Level.SEVERE, null, ex);
      }
      try { 
          Field index = c.getDeclaredField("index") ;
          index.setAccessible(true);
          return ""+(char) (((int)index.get(part))+64) ;
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
          //Logger.getLogger(CustomerRepresentative.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      try {
          //          Method[] methods = c.getDeclaredMethods() ; 
//          for(int i=0;i<methods.length;i++) {
//              if(methods[i].getReturnType()==String.class) {
//                  String namedMehod = (String) methods[i].getName();
//                  Object [] args = new Object[0] ;
//                  Method name = c.getDeclaredMethod(namedMehod);
//                  name.setAccessible(true);
//                  return (String) name.invoke(part,args) ; 
//              }
//              else if(methods[i].getReturnType()== int.class) {
//                  String namedMehod = (String) methods[i].getName();
//                  Method index = c.getDeclaredMethod(namedMehod);
//                  index.setAccessible(true);
//                  Object [] args = new Object[1] ;
//                    double a = 0 ; 
//                    args[0] = a ;
//                    return ""+ ((char)((int)index.invoke(part,args)+64)) ;
//              }
//          } // I've tried to do it in proper way but after it gives so many errors i comment it out 
          Field method = c.getDeclaredField("method") ;
          method.setAccessible(true);
          String namedMethod = (String) method.get(part) ;
          if(namedMethod.equals("getName")){
              Method name = c.getDeclaredMethod("getName");
              name.setAccessible(true);
              Object [] args = new Object[0] ;
              
              return (String) name.invoke(part,args) ; 
          }
          else if(namedMethod.equals("getIndex")) {
              Method index = c.getDeclaredMethod("getIndex", double.class);
              index.setAccessible(true);
              Object [] args = new Object[1] ;
              double a = 0 ; 
              args[0] = a ;
              return ""+ ((char)((int)index.invoke(part,args)+64)) ; 
          }
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
          Logger.getLogger(CustomerRepresentative.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NoSuchMethodException | InvocationTargetException ex) {
         // Logger.getLogger(CustomerRepresentative.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      
      return null ;
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
    // ...
      while(jobShop.isOpen) {
          Part [] req = jobShop.getNextProductRequest() ; 
          talk( "%s %s : Checking for a standing product request" , title , name ) ;
          if(req==null){ // if there is no product request wait the representative thread . 
            talk( "%s %s : There are no product requests, so I'm waiting" , title , name ) ; 
            synchronized(jobShop.productRequests) {
                 while(true) {
                    try {
                        jobShop.productRequests.wait(); // After wait look for productRequest like nothing happened .
                    } catch (InterruptedException e) {
                        continue;
                    }
                    break ;
                 }
            }
            
              
          }
          else { // If there is a productRequest for representative thread 
                String [] temp = new String[req.length] ;
                int i=0;
                for(Part part : req) {
                    temp[i++] = identifyPartName(part) ; 
                }
                this.numberOfProductRequests++;
                Order tempOrder = new Order(jobShop.generateNewOrderID(),temp);
                talk( "%s %s : I am adding a new order %s" , title , name,tempOrder.toString()) ;
                jobShop.addWorkingOrder(tempOrder) ; 
                synchronized(jobShop.workingOrders) {
                    jobShop.workingOrders.notify();
                }
                synchronized(tempOrder) {
                 while(true) {
                    try {
                        tempOrder.wait(); // After wait look for productRequest like nothing happened .
                    } catch (InterruptedException e) {
                        continue;
                    }
                    break ;
                 }
                }
                synchronized(req) {
                    req.notifyAll();
                }
            }
               
      }
      //After shop is closed 
      
      talk( "%s %s : Processed a total of %d product requests" , title , name,numberOfProductRequests) ;
      
      
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

