# import librarieslibrary(StatDA)
library(StatDA)
library(chemometrics)
library(car)
library(MASS)
library(gvlma)

sp2_sd = read.csv("sd.csv",header=TRUE)
summary(sp2_sd)

sd(sp2_sd$Error) 
sd(sp2_sd$Error.1) 


# Read the data from file in to a data frame
sp2 = read.csv("SP2_SLR1.csv",header=TRUE)
summary(sp2)

head(sp2)
hist(sp2$Experimental_1JCH)

edaplot(sp2$Experimental_1JCH,H.freq=FALSE)
edaplot(sp2$Avg_NWChemPrediction,H.freq=FALSE)

# Simple linear model
lm.fit = lm(sp2$Experimental_1JCH ~ sp2$Avg_NWChemPrediction,sp2)
summary(lm.fit)
plot(lm.fit) #  Residuals vs fitted plots / Normal Q-Q plot / Residuals vs Leverage plot
plot(sp2$Avg_NWChemPrediction,sp2$Experimental_1JCH)
abline(lm.fit,col="red",lwd=2)
lm.resi<- resid(lm.fit)
lm.stdres<-rstandard(lm.fit)
plot(sp2$Experimental_1JCH,lm.resi)

skewness(lm.resi)
qqnorm(lm.resi)
qqline(lm.resi)
hist(lm.resi)

shapiro.test(lm.resi)
# Detect outliers in the dataset and their removal
coeffArray<- vector()
for(i in 1:length(sp2$Mol_ID)){
  coeffArray[i] = lm(sp2$Experimental_1JCH[-i]~ sp2$Avg_NWChemPrediction[-i])$coeff[2]
}

plot(coeffArray - (lm.fit)$coeff[2])
text(coeffArray - (lm.fit)$coeff[2], labels = (1:255), pos = 4,cex=0.7)

sp2_new<-sp2[-c(249,255),]
lm1.fit = lm(sp2_new$Experimental_1JCH~ sp2_new$Avg_NWChemPrediction)
summary(lm1.fit)
plot(lm1.fit) #  Residuals vs fitted plots / Normal Q-Q plot / Residuals vs Leverage plot
plot(sp2_new$Avg_NWChemPrediction,sp2_new$Experimental_1JCH)
abline(lm1.fit,col="red",lwd=2)
lm1.resi<- resid(lm1.fit)
lm1.stdres<-rstandard(lm1.fit)
plot(sp2_new$Experimental_1JCH,lm1.stdres)


skewness(lm1.resi)
qqnorm(lm1.resi)
qqline(lm1.resi)
hist(lm1.resi)
shapiro.test(lm1.resi)


b=boxcox(object=lm1.fit, data = sp2, lambda = seq(-2, 2, length = 10))

lambda <- b$x[which.max(b$y)]
lambda

# # boxcox transformation of the data
# lm.boxcox<-lm(((((Exp.1JCH)^(lambda))-1)/lambda) ~ NWChem.1JCH,sp2)
# summary(lm.boxcox)
# lm.boxcox.resi<- resid(lm.boxcox)
# lm.boxcox.stdres<-rstandard(lm.boxcox)
# plot(Exp.1JCH,lm.boxcox.stdres)
# plot(lm.boxcox) 
# skewness(lm.boxcox.resi)
# qqnorm(lm.boxcox.resi)
# qqline(lm.boxcox.resi)
# hist(lm.boxcox.resi)
# # still not normally distributed ???
# shapiro.test(lm.boxcox.resi)


# cross validation to find the best model

library(DAAG)

val.daag<-CVlm(df=sp2_new,m=5,form.lm=formula(Experimental_1JCH ~ Avg_NWChemPrediction))

library(boot)

model.boot <- glm(Experimental_1JCH  ~ Avg_NWChemPrediction,sp2_new,family = gaussian)

summary(model.boot)
# The 'cv.glm' function returns a 'delta' which shows (first) the raw cross-validation estimate
# of prediction error and (second) the adjusted cross-validation estimate. The adjustment is
# designed to compensate for the bias introduced by not using leave-one-out cross-validation.

# Ten fold cross validation.

val.10.fold <- cv.glm(data = sp2_new, glmfit = model.boot, K = 10)
warnings()
val.10.fold

# Leave one out cross validation (default).

val.loocv <- cv.glm(data = sp2_new, glmfit = model.boot, K = nrow(sp2_new))
val.loocv


k_cv<- vector()

for (j in 2:10){
  ka=0
  for (k in 1:100){
    ka = ka + cv.glm(data = sp2_new, glmfit = model.boot, K = j)$delta[1]
  }
  k_cv[j]=ka/100
}

k_cv
