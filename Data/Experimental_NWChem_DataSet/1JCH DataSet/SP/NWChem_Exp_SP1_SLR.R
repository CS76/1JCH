# import libraries
library(StatDA)
library(chemometrics)
library(car)
library(MASS)
library(gvlma)


# Read the data from file in to a data frame
sp1 = read.csv("NWChem_sp1.csv",header=TRUE)
summary(sp1)

head(sp1)

sp1 = sp1[-c(11,12),]
edaplot(sp1$Exp.1JCH,H.freq=FALSE)
edaplot(sp1$NWChem.10,H.freq=FALSE)

# Simple linear model
lm.fit = lm(sp1$Exp.1JCH ~ sp1$NWChem.10,sp1)
summary(lm.fit)
plot(lm.fit) #  Residuals vs fitted plots / Normal Q-Q plot / Residuals vs Leverage plot
plot(sp1$NWChem.10,sp1$Exp.1JCH)
abline(lm.fit,col="red",lwd=2)
lm.resi<- resid(lm.fit)
lm.stdres<-rstandard(lm.fit)
plot(sp1$Exp.1JCH,lm.resi)

skewness(lm.resi)
qqnorm(lm.resi)
qqline(lm.resi)
hist(lm.resi)

shapiro.test(lm.resi)
# Detect outliers in the dataset and their removal
coeffArray<- vector()
for(i in 1:length(sp1$Mol.ID)){
  coeffArray[i] = lm(sp1$Exp.1JCH[-i]~ sp1$NWChem.10[-i])$coeff[2]
}
plot(coeffArray - (lm.fit)$coeff[2])
text(coeffArray - (lm.fit)$coeff[2], labels = sp1$Mol.ID, pos = 4,cex=0.7)


library(DAAG)

val.daag<-CVlm(df=sp1,m=5,form.lm=formula(Exp.1JCH ~ NWChem.10))

library(boot)

model.boot <- glm(Exp.1JCH ~ NWChem.10,sp1,family = gaussian)

summary(model.boot)
# The 'cv.glm' function returns a 'delta' which shows (first) the raw cross-validation estimate
# of prediction error and (second) the adjusted cross-validation estimate. The adjustment is
# designed to compensate for the bias introduced by not using leave-one-out cross-validation.

# Ten fold cross validation.

val.10.fold <- cv.glm(data = sp1, glmfit = model.boot, K = 10)
warnings()
val.10.fold

# Leave one out cross validation (default).

val.loocv <- cv.glm(data = sp1, glmfit = model.boot, K = nrow(sp1))
val.loocv


k_cv<- vector()

for (j in 2:10){
  ka=0
  for (k in 1:100){
    ka = ka + cv.glm(data = sp1, glmfit = model.boot, K = j)$delta[1]
  }
  k_cv[j]=ka/100
}

k_cv