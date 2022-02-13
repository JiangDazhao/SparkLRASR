function [C] = hyperCov(M)
% HYPERCOV Computes the covariance matrix
% hyperCorr compute the sample covariance matrix of a 2D matrix.
%
% Usage
%   [C] = hyperCorr(M)
%
% Inputs
%   M - 2D matrix 
% Outputs
%   C - Sample covariance matrix

[p, N] = size(M);
% Remove mean from data
% M=162*839 u=162*1
u = mean(M.').';
for k=1:N
    M(:,k) = M(:,k) - u;
end
%C=162*162
C = (M*M.')/(N-1);
