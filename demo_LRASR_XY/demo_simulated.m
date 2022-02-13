%%    demo for LRASR algorithm
%------------------Brief description
%
%This demo implements the hyperspectral anomaly detection using low-rank
%and saprse represention model described in [1] for the simulated data.
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

%generate data, here is the simulated data
load Sandiego.mat
M = Sandiego;
clear Sandiego;
% elimate bad bands
M(:,:,[1:6 33:35 97 104:110 153:166 221:224 ]) = [];
M(:,:,[94 95 96]) = [];

%anomaly spectrum
sig1 = squeeze(M(144,244,:));

% use part of the whole image for test
M = M(71:170,41:140,:);
%186 100 100
[no_lines,no_rows, no_bands] = size(M);

% implanting anomaly target
[a b] = meshgrid(10:25:100);
tau = [0.05 0.1 0.2  0.4];%abundance fraction
for i=1:4
    temp = zeros(1,4,no_bands);
    for j=1:4
        temp(:,j,:) = sig1;
    end
    M(a(i,i),b(:,i),:) = (1-tau(i))*M(a(i,i),b(:,i),:)+tau(i)*temp;
end


% Grount truth map
GT = zeros(no_lines,no_rows);
for i=1:4
    for j=1:4
        GT(a(i,j),b(i,j),:)=1;
    end
end


%convert data
M = reshape(M, no_lines*no_rows, no_bands).';
M = M./max(M(:));% normalize to 0~1
%X=186(bands)*10000(pixels)
X = M;
clear M

% Dictionary construction
Dic = DicCon(X, K, P);

% solve representation coefficient matrix S and saprse error matrix E

[S E] = LADMAP_LRASR( X, Dic, lambda, beta);

%show result
re=reshape(sqrt(sum(E.^2)),no_lines,no_rows);
figure
imagesc(re)
axis image
title('LRASR for simulated data')
% plot ROC curve and calculate AUC
[tpr,fpr,thresholds] = roc(GT(:)',re(:)');
AUC=trapz(fpr,tpr);
figure
plot(fpr,tpr,'-')
xlabel('false alarm rate')
ylabel('probability of detection')
title('ROC curve of simulated data')