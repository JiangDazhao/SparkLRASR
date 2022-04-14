fileID=fopen('img2D.bin','w');
fwrite(fileID,readoutfloatImg,'float');
fclose(fileID);
fileid=fopen('img2D.bin');
readoutfloatImg=fread(fileid,[162,8000],'float');

fileID=fopen('GT.bin','w');
fwrite(fileID,GT,'float');
fclose(fileID);
fileid=fopen('GT.bin');
readoutGT=fread(fileid,'float');