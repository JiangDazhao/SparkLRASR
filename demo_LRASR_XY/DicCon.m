function Dictionary = DicCon(X, K, P)
%
%   Dictionary construction for LRASR 
%
% Inputs:
%   X  - 2D data matrix (p x N) 
%   K - number of clusters
%   P - number of selected pixels
% Outputs:
%   Dictionary - Detector output (1 x N)
%
%Author: Yang Xu

% K-means K1=8000*1 K2=15*162
% X=162(bands)*8000(pixels)
[K1 K2]=kmeans(X',K,'Start','cluster');
Dictionary = [];
%K=15
for i=1:K

    st1=find(K1==i);
    
    if length(st1)<P
        continue;
    end
    % X=162(bands)*8000(pixels)
    %temp每类选出来重新标序号=162*357
    temp=X(:,st1);
    % kr=708*1
    kr=RxDetector(X(:,st1));
    %d1=357*1 double d2=357*1 int
    [d1 d2]=sort(kr,'ascend');
    %从该类temp中挑出P个，
    Dictionary=[Dictionary ,temp(:,d2(1:P))];
end
    