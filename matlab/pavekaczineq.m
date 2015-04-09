function [x, tom] = pavekaczineq( A, b, x0, xopt, maxIts,inequalities )

% Takes arguments: A (the matrix), b (the right hand side), x0 (initial
% estimation), xopt (optimal solution -- for testing purposes), maxIts
% (maximum number of cycles to make), inequalities (binary string
% indicating which rows of A are inequalities: entry is 1 if >= inequality,
% 0 otherwise)
%
%Returns: The solution x, and tom which is a struct with tom.errtrend equal
%to the error in each iteration, and tom.cputrend equal to the CPU time
%iteration by iteration.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Make a "paving", which in your case is just a list from 1 to the number
% of rows in the matrix, in order. This will be used as the order to make
% the projections in.
pave = size(A,1);
paving = cell(pave,1);
for r = 1:pave,
    paving{r} = r;
end
 
[n, m] = size(A);  %Call the dimensions of A n by m
x = x0;  %Set the initial estimation

% Setup algorithm tomography
cpuinit = cputime;  %Will track CPU runtime
tom.cputrend = 0;
tom.errtrend = norm(x - xopt);  %Will track estimation error
ct = 0; %counter


 while ct < maxIts  && norm(x-xopt) > 1e-9  %Iterate until we've converged or exceeded our max iterations

    ct = ct + 1; %Increment the iteration counter
    
    z = 1:length(paving); %This is the list of the indices of the rows of A. Right now we just use them
                          %in order. We can change this to a random order later.
    for iter = 1:length(paving)  %Now iterate through each row

        r = z(iter);
        eqns = paving{r}   %Set eqns equal to the index of the row you want to project onto.
                            %We can change this to be multiple rows at a
                            %time if we want to try a block method.

        % Test whether this row is an inequality or not
         project = 0;
         switch inequalities(r)
             case 0     %If it's not an inequality, we will want to project
                project = 1;
             case 1     %Otherwise, we want to project only if the inequality isn't satisfied
                 if A(eqns)*x < b(eqns)
                     project = 1;
                 end
         end
         
         if project  %If we wanted to project, let's do the projection
             %This is the Kaczmarz iteration: x = x + pinv(A_i)*(b_i -A_i*x)
             %where A_i is the row of A we are currently projecting with, 
             %b_i is that entry of b, and pinv(A) denotes the pseudoinverse
             %of A. (We write it this way in case we want to use the block
             %method later).
                y = pinv(A(eqns,:)) * (b(eqns) - A(eqns,:)*x);
                x = x + y;  
         end
        %Update the error and runtime
        tom.cputrend = [tom.cputrend, cputime - cpuinit];
        tom.errtrend = [tom.errtrend, norm(x - xopt)];
    end
    
 end

 %Output the progress
display(['Error after ', num2str(ct), ' iterations is ', num2str(norm(x-xopt))]);
