# A-Job-Shop-of-Multi-Threading-Reflection-and-JDBC

I have built a job shop simulation. The simulation will require a number of thread objects that represent instances of the following 4 types of people acting around a job shop in a single day from 8:00 AM in the morning until 5:00 PM in the afternoon . 

Each thread will need to print informative messages about what they are about to do or currently doing. Also, every half hour, a report about the job shop (number of each type of employees, number of standing product requests, number of working orders, number of completed orders, and the contents of the inventory) needs to be printed on the screen.

The interactions between Customer – Customer Representative, Customer Representative – Worker and Worker – Stock Manager can be thought of as a Producer – Consumer model. Once the customer submits a product request to the job shop, a representative should be notified to process the request. Once a representative places a working order, a worker should be notified to start working on the order. And once a worker reports a missing part, a stock manager should be notified to supply the part.

Design Model(UML) and Database's Tables under the Misc directory . 
