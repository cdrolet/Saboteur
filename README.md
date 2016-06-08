# Saboteur

A tool to disrupt and change the behavior of a running spring based applications. Saboteur can by example bring down connection to database, altering service response and creating artifical delay. Those cases are traditionnaly difficult to test in a distributed system and required expensive test code.

For example: 
- What is happening when a tier one service is no more available ?
- How do my application behave when the database is down ?  
- Is my application handling correctly retry mechanism ? 


To "infiltrate" an application, 
you need to add the saboteur library in the application dependency.



