function [S,E,J] = LADMAP_LRASR(X,A,lambda,beta)
% % This routine solves the following nuclear-norm optimization problem,
% min |S|_*+lambda*|E|_2,1+beta*|S|_1
% s.t., X = AS+E
% inputs:
%        X -- D*N data matrix, D is the data dimension, and N is the number
%             of data vectors.
%        A -- D*M matrix of a dictionary, M is the size of the dictionary
%        lambda -- parameter of the sparse term E
%        beta -- parameter of sparse term Z
% Outputs:
%        S -- representation coefficient
%        E -- sparse error
%        J -- auxiliary variable

% Author: Yang Xu  
%Email: xuyangth90@gmail.com
tol = 1e-6;
tol2=1e-2;% error tollerance 
maxIter = 1e6;% maximum iteration number 
% X=162*8000
[d n] = size(X);
% A=162*300
m = size(A,2);
rho = 1.1;
max_mu = 1e10;
mu = 1e-3;% initial mu, can be changed
%norm(A,2)=74.5925
ita1=norm(A,2)*norm(A,2);

%% Initializing optimization variables
% intialize  W=J Z=S
J = zeros(m,n);
S = zeros(m,n);
E = sparse(d,n);
% Y1 Y2 are Lagrange multipliers
Y1 = zeros(d,n);
Y2 = zeros(m,n);
%% Start main loop
iter = 0;
disp(['initial,rank=' num2str(rank(S))]);
while iter<maxIter
    %sk,jk=300*8000 ek=162*8000
    sk=S;
    jk=J;
    ek=E;
      
    iter = iter + 1;
    %update S
    %temp=300*8000
    %temp=300*8000+300*162*162*8000-300*8000
    temp=sk+(A'*(X-A*sk-ek+Y1/mu)-(sk-jk+Y2/mu))/ita1;
    
    %U=300*300 sigma=300*300 V=8000*300
    [U,sigma,V] = svd(temp,'econ');
    %diagmax sigma=300*1
    sigma = diag(sigma);
    %svp=2 sigma>1/(mu*ita1)
    svp = length(find(sigma>1/(mu*ita1)));
    if svp>=1
        sigma = sigma(1:svp)-1/(mu*ita1);
    else
        svp = 1;
        sigma = 0;
    end
    %S=300*8000 U=300*300 sigm=2*1 V=8000*300
    %300*2*2*2*2*8000
    S = U(:,1:svp)*diag(sigma)*V(:,1:svp)';  %singular value thresholding 
    
    %updata J
    temp2=S+Y2/mu;
    %temp2=300*8000 J=300*8000
    J=soft(temp2, beta/mu); % soft shrinkage
    
    %updata E
    %A=D
    xmaz = X-A*S;
    temp3 = xmaz+Y1/mu;
    %E=162*8000 temp3=162*8000
    E = solve_l1l2(temp3,lambda/mu);% l21 minimizatin operator
    
    %leq1= X-DS_(k+1)-E_(k+1) 162*8000
    leq1 = xmaz-E;
    %leq2=S_(k+1)-J_(k+1) 300*8000
    leq2 = S-J;
    %norm(X,'fro')=78.2924 norm(X,'fro')=  339.1881
    stopC=norm(leq1,'fro')/norm(X,'fro');
    stopC2=mu*max(max(sqrt(ita1)*norm(S-sk,'fro'),norm(J-jk,'fro')),norm(E-ek,'fro'))/norm(X,'fro');
        disp(['iter ' num2str(iter) ',mu=' num2str(mu,'%2.1e') ...
            ',rank=' num2str(rank(S,1e-3*norm(S,2))) ',stopALM=' num2str(stopC,'%2.3e'),'stopC2=' num2str(stopC2,'%2.3e')]);

    if stopC<tol&stopC2<tol2
        break;
    else
        %updata Lagrange multipliers
        %Y1=162*8000 Y2=300*8000
        Y1 = Y1 + mu*leq1;
        Y2 = Y2 + mu*leq2;
        %updata mu
        mu = min(max_mu,mu*rho);
    end
end

function [E] = solve_l1l2(W,lambda)
n = size(W,2);
%E=162*8000  lambda=lambda/mu
E = W;
%对每一列solve_l2
for i=1:n
    E(:,i) = solve_l2(W(:,i),lambda);
end

function [x] = solve_l2(w,lambda)
% min lambda |x|_2 + |x-w|_2^2
%w=162*1 x=162*1
nw = norm(w);
if nw>lambda
    x = (nw-lambda)*w/nw;
else
    x = zeros(length(w),1);
end