function y = soft(x,T)

T = T + eps;
%y=300*8000
y = max(abs(x) - T, 0);
y = y./(y+T) .* x;

