% This is our main program to solve linear equations


%  %Mthod 1: Projection mthod
%  disp('********************************* Projection METHOD******************')
[A, b, inequalities, xopt] = makeA();
[x, tom] = pavekaczineq( A, b, zeros(6,1), xopt,18, inequalities )


