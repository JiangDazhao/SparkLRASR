%%    demo for LRASR algorithm
%------------------Brief description
%
%This demo implements the hyperspectral anomaly detection using low-rank
%and saprse represention model described in [1] for the Urban data.
%
% [1] Yang Xu, Zebin Wu, Jun Li, Antonio Plaza, and Zhihui Wei, ¡°Anomaly Detection
%     in Hyperspectral Images Based on Low-Rank and Sparse Representation,¡±
%     IEEE Trans. Geosci. Remote Sens., vol. 54, no. 4, pp. 1030¨C1043, Mar. 2016.

clear;
clc

%Parameter set
lambda = 0.1;
beta = 0.1;
K = 15;%cluster center
P = 20;%number of pixels in each cluster

% load data
load Urban.mat
M = Urban; 
%clear Urban
M = M(1:80,189:288,:);
[no_lines,no_rows, no_bands]=size(M);

% load Ground Truth
load UGt.mat
GT=UGt;


%convert data
M = reshape(M, no_lines*no_rows, no_bands).';
M = M./max(M(:));% normalize to 0~1
X = M;
%clear M

% Dictionary construction
Dic = DicCon(X, K, P);

% solve representation coefficient matrix S and saprse error matrix E

[S E] = LADMAP_LRASR( X, Dic, lambda, beta);

%show result
re = reshape(sqrt(sum(E.^2)),no_lines,no_rows);
figure
imagesc(re)
axis image
title('LRASR for Urban data')
% plot ROC curve and calculate AUC
[tpr,fpr,thresholds] = roc(GT(:)',re(:)');
AUC = trapz(fpr,tpr);
figure
plot(fpr,tpr,'-')
xlabel('false alarm rate')
ylabel('probability of detection')
title('ROC curve of Urban data')