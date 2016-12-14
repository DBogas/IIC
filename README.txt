This is the README file for this project.

I aim to study the physical components of the STCP Bus and Tram Network.
Both Day and Night networks were studied together.

The AllStops.txt is a .txt fila that contains all the stops, this avoids my IP getiing blocked and takes less than connecting to a site 
everytime i want to test.

The AllLines.txt file does the same for the lines, for the same reasons.

There are readers for these files.

There are also .csv files for gephi usage, these are alse generated with java.

At this point, i can give you a graph with all stops and edges between them, with no "grouping". It's a single connected component, so , it's gr8!

I have also methods that allow me to group the stops by street and by code (TSL2 and TSL1 have TSL as prefix, so they are grouped together).

When grouping by streets, you can currently count with a method that allows you to construct the edges between streets.
E.g:

200 leaves AL1 in direction to TR1.
Aliados and Trindade are the "dots".
Aliados-Trindade is the edge.

These edges also have weight, which represents the amount of lines that use them.

When grouping by code, you cant (yet) count with a method that builds the edges, but it must similar to the streets one, it wont take long.

Finally, there are still missing .csv generators, more on that later.


