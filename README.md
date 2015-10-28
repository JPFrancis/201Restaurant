##Restaurant Project Repository

###Student Information
  + Name: John Francis
  + USC Email: jpfranci@usc.edu
  + USC ID: 8136928609
  + Section: T/Th 11:00
  + Lab: Wednesday at 12:00

###Resources
  + [Restaurant v1](http://www-scf.usc.edu/~csci201/readings/restaurant-v1.html)
  + [Agent Roadmap](http://www-scf.usc.edu/~csci201/readings/agent-roadmap.html)

###Issues

-I do not have a fourth test for the interleaving stuff. I also did all the extra credit. For the condition where the cashier doesn’t have enough money for the market shipment, I decided to cancel the shipment before it was packaged.

-I also, unfortunately, have a bug with my Pay method for the customers that was working for last week but now shows up occasionally at the end of the sequence, it’s a null pointer that prevents the customer from sending the pay message to the cashier.

SET THE NAMES OF THE CUSTOMERS TO SOMETHING LIKE “PIZZA” AND “SALAD” TO SEE IT FUNCTION CORRECTLY. BASICALLY AS LONG AS THE CUSTOMERS ARE NOT ORDERING THE SAME THING  

####Testing

-For testing the cashier not having enough money, splitting the bill, and all that stuff, you can both see it in the tests and run the program, to witness the split at least. Otherwise, you can wait for the funds to deplete.

-Also, please note that my design from v2.1 allowed all neededFoods to be immediately split individually and ordered from the markets that carry them, which is what you will see.

