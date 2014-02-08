# import libraries
library(StatDA)
library(chemometrics)
library(car)
library(MASS)
library(gvlma)
library(robustbase)

# import data
completeData = read.csv("SP2.csv",header=TRUE)
summary(completeData)

# SP2.complete = SP2.complete[-c(1,2),]
# select subset of data (remove unwanted columns)
SP2.complete <- subset( completeData, select = -c(1, 2, 3, 4, 5, 9,12,16,17,19,21,22,23))
summary(SP2.complete)
head(SP2.complete)

# center and scale data
# auto scaling
SP2.complete <- scale(SP2.complete,center = TRUE, scale = TRUE)
summary(SP2.complete)

# Variability in the data
# MAD (if MAD == 0, remove the columns)
SP2.complete.mad = apply(SP2.complete, 2, function(x) {
  mad(x, center = median(x), constant = 1.4826,na.rm = FALSE, low = FALSE, high = FALSE)
})

SP2.complete.mad

summary(SP2.complete)
summary(completeData)

exp1JCH<-completeData[,c(16,17,19)]

SP2.complete<-cbind(SP2.complete,exp1JCH)
summary(SP2.complete)
SP2.complete.shuffled <- SP2.complete[sample(nrow(SP2.complete)),]

SP2.complete.testSet<- SP2.complete.shuffled[1:88,]
SP2.complete.trainSet<- SP2.complete.shuffled[89:292,]
summary(SP2.complete.trainSet)

pca<-princomp(SP2.complete.trainSet[,-c(11,12,13)],scores=TRUE, cor=TRUE)
pca<-princomp(SP2.complete.trainSet[-c(37,83,159,162),-c(11,12,13)],scores=TRUE, cor=TRUE)
plot(pca)

summary(pca)
biplot(pca)
pca$scores[,1:6]

PCA_expValues <- cbind(pca$scores[,1:6], SP2.complete.trainSet[,c(11,12,13)])
train.frame <- data.frame(PCA_expValues)

summary(train.frame)
train_woo.frame <- train.frame
train_woo.frame <- train.frame[-c(57,127,168,175,183,144),]

#res<- pcaVarexpl(SP2.complete.trainSet[,-12],a=4)
#res
#finalrandomdata=finaldata[sample(nrow(finaldata)),]

library(DAAG)
library(boot) 

model.boot <- glm(formula = NWChem ~ Comp.1 + Comp.2 + Comp.3 + Comp.4 + Comp.5 + Comp.6, train_woo.frame, family = gaussian)

summary(model.boot)
plot(model.boot)
model.boot


# The 'cv.glm' function returns a 'delta' which shows (first) the raw cross-validation estimate
# of prediction error and (second) the adjusted cross-validation estimate. The adjustment is
# designed to compensate for the bias introduced by not using leave-one-out cross-validation.
# Leave one out cross validation (default).
val.loocv <- cv.glm(data = train_woo.frame, glmfit = model.boot,K = nrow(train_woo.frame))
val.loocv

k_cv<- vector()

for (j in 2:10){
  ka=0
  for (k in 1:100){
    ka = ka + cv.glm(data = train_woo.frame, glmfit = model.boot, K = j)$delta[1]
  }
  k_cv[j]=ka/100
}

k_cv

scaled_testSet<-scale(SP2.complete.testSet[,-c(11,12,13)],center = TRUE, scale = TRUE)
pc_new <- as.data.frame(predict(pca,newdata=scaled_testSet))



new.fit <- predict(model.boot, pc_new)

rmse <- function(error)
{
  sqrt(mean(error^2))
}

mae <- function(error)
{     
  mean(abs(error))
}

SP2.complete.testSet[,12]-new.fit
mae(SP2.complete.testSet[,12]-new.fit)
rmse(SP2.complete.testSet[,12]-new.fit)

plot(SP2.complete.testSet[,12],new.fit)
hist(model.boot$resid)
skewness(model.boot$resid)
shapiro.test(model.boot$resid)