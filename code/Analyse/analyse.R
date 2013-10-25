# Set working directory
setwd("D:\\Git-data\\dtp\\code\\LockingVergelijker\\src\\testRuns");

# Build data structure for 20 banks data
index_20 = c(1:10);
data_20 <- data.frame(num=rep(NA, length(index_20)), 	min_1=rep(NA, length(index_20)), 
									avg_1=rep(NA, length(index_20)), 
									med_1=rep(NA, length(index_20)), 
									max_1=rep(NA, length(index_20)),
									tot_1=rep(NA, length(index_20)),
									min_2=rep(NA, length(index_20)), 
									avg_2=rep(NA, length(index_20)), 
									med_2=rep(NA, length(index_20)), 
									max_2=rep(NA, length(index_20)), 
									stringsAsFactors=FALSE)
          
# Build data structure for 25 banks data
index_25 = c(11:20);
data_25 <- data.frame(num=rep(NA, length(index_20)), 	min_1=rep(NA, length(index_20)), 
									avg_1=rep(NA, length(index_20)), 
									med_1=rep(NA, length(index_20)), 
									max_1=rep(NA, length(index_20)),
									tot_1=rep(NA, length(index_20)),
									min_2=rep(NA, length(index_20)), 
									avg_2=rep(NA, length(index_20)), 
									med_2=rep(NA, length(index_20)), 
									max_2=rep(NA, length(index_20)), 
									stringsAsFactors=FALSE)

# Fill 20 banks data structure
index = 1;
for (i in index_20) {
	if(i<10){
		filename <- paste(0,i,sep="");
	}else{
		filename <- i;
	}
	temp = read.csv(paste(filename,".txt",sep=""), header=FALSE);
	data_20[index, ] <- c(i, min(temp[,1]), mean(temp[,1]), median(temp[,1]), max(temp[,1]), sum(temp[,1]),
				       min(temp[,2]), mean(temp[,2]), median(temp[,2]), max(temp[,2]))
	index = index + 1;
}

# Fill 25 banks data structure
index = 1;
for (i in index_25) {
	if(i<10){
		filename <- paste(0,i,sep="");
	}else{
		filename <- i;
	}
	temp = read.csv(paste(filename,".txt",sep=""), header=FALSE);
	data_25[index, ] <- c(i, min(temp[,1]), mean(temp[,1]), median(temp[,1]), max(temp[,1]), sum(temp[,1]),
				       min(temp[,2]), mean(temp[,2]), median(temp[,2]), max(temp[,2]))
	index = index + 1;
}

# Statistical comparisons
# Significance test in num of collisions


# Significance test in max thread time
