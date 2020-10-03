
This is a set of tools to help manipulate the examination timetabling format 
as used in the 2nd International Timetabling Competition  ITC2
Foramt is referred to as "TTC07"

TTC07exam.exe 

Will verify solutions and also convert the data and solution to OPL/CPLEX format.

To generate the OPL data files from the .exam and .sln file

./TTC07exam.exe test.exam test.sln 1

NOTE: CPLEX on the given OPL encoding can only solve relatively small instances. 
However, by enforncing the solution as constraint it can verify solutions to largish instances.

Usual Caveats:

Research code not for commercial use. 
Might well give problems - please feel free to contact me 
http://www.cs.nott.ac.uk/~ajp/
All is copyright by Andrew Parkes and University of Nottingham
