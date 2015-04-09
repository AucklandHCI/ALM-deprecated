function [x, sol] = Kaczmarzmatlab_least(A,b)
 
 [m, d] = size(A);
 x0 = zeros(d,1);
 sol = A\b;
 disp(sol)
 fprintf('Solution of system is : %1.3f , %1.3f\n',sol(1), sol(2));
 
x = x0;

lambda=1.9;
cooling=0.9;
close;
hold on;

F = cell(m,1);
for i=1:m
    F{i,1} = @(x)b(i)/A(i,2) - A(i,1)/A(i,2)*x;
    fplot(F{i,1},[-1,2],'k');
end
 
plot(sol(1), sol(2), 'g*');
 
maxIter = 210;
Iter = 0;
while(norm(A*x - b) > 1e-3 && Iter < maxIter)
    lambda=lambda*cooling;
 for i = 1 : m
 xprev = x;
 x = x + lambda*(b(i) - A(i,:)*x)*A(i,:).'/norm(A(i,:).')^2;
  disp(x)
 plot([xprev(1),x(1)],[xprev(2),x(2)],'b--.');
 Iter = Iter + 1;
 pause(1);
 end
end
plot(x(1),x(2),'r*')
 
hold off;

