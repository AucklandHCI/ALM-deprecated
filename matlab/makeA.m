function [A, b, inequalities, xopt] = makeA()

% Returns the matrix A, right hand side b, a binary list identifying which
% rows of A are inequalities, and the optimal solution xopt

A = [0	0	-1	0	2	0
0	0	0      -1	0	2			
0	0	1	0	0	0			
0	0	0	1	0	0		
1	0	0	0	0	0		
0	1	0	0	0	0		
-1	0	0	0	1	0	
0	-1	0	0	0	1	
0	0	1	0      -1	0				
0	-1	0	0	0	1		
-1	0	1	0	0	0	
0	0	0	1	0      -1
-1	0	0	0	1	0				
0	-1	0	0	0	1				
0	0	1	0	-1	0				
0	-1	0	0	0	1				
-1	0	1	0	0	0					
0	0	0	1	0      -1];

b = [0	0	276	228	0	0	77	26	77	26	77       26 0	0	0	0	0	0]';

inequalities=zeros(18,1);
inequalities(13:18)=1;


xopt = [0.0
0.0
276
228
138.000000000000
114.000000000000];
