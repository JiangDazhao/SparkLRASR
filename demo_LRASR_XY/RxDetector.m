function [result, sigma, sigmaInv] = RxDetector(M)
%RX anomaly detector
%   RxDetector performs the RX anomaly detector
%
% Usage
%   [result] = hyperRxDetector(M)
% Inputs
%   M  - 2D data matrix (p x N)
% Outputs
%   result - Detector output (1 x N)
%   sigma - Covariance matrix (p x p)
%   sigmaInv - Inverse of covariance matrix (p x p)

% Remove the data mean
% p=162 N=708 M=162*708
[p, N] = size(M);
% mMean= 162*1
mMean = mean(M, 2);
M = M - repmat(mMean, 1, N);

% Compute covariance matrix
%sigma= 162*162
sigma = hyperCov(M);
delta=1e-5;%regularizaton parameter
%simaInv=162*162
sigmaInv = inv(sigma+delta*eye(size(sigma)));

result = zeros(N, 1);
for i=1:N
    result(i) = M(:,i).'*sigmaInv*M(:,i);
end
%result = 171*1
result = abs(result);

return;